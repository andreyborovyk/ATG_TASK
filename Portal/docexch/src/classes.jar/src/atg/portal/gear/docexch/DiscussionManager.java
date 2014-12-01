/*<ATGCOPYRIGHT>
* Copyright (C) 2001-2011 Art Technology Group, Inc.
* All Rights Reserved.  No use, copying or distribution of this
* work may be made except in accordance with a valid license
* agreement from Art Technology Group.  This notice must be
* included on all copies, modifications and derivatives of this
* work.
*
* Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
* ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
* LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
* MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
*
* "Dynamo" is a trademark of Art Technology Group, Inc.
</ATGCOPYRIGHT>*/

package atg.portal.gear.docexch;

import atg.nucleus.GenericService;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.dtm.*;
import atg.portal.nucleus.NucleusComponents;

import atg.core.util.NumberTable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;

import javax.naming.*;

/**
 * This class provides a backend interface into the Discussion gear.  It 
 * does not rely on any of the discussion classes, but pokes directly into
 * the DiscussionRepository.
 *
 * There is only one method implemented, to delete a forum given the ID.
 *
 * @author Will Sargent
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DiscussionManager.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DiscussionManager extends GenericService
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DiscussionManager.java#2 $$Change: 651448 $";

    /** JNDI name of the transaction manager */
    public static String
    TRANSACTION_MANAGER = "dynamo:/atg/dynamo/transaction/TransactionManager";
    
    /** JNDI name of the transaction manager */
    public static String
    DISCUSSION_REPOSITORY = "dynamo:/atg/portal/gear/discussion/DiscussionRepository";
    
    /** Repository view name of the user gear boards. */
    public static String USER_GEAR_BOARDS = "userGearBoards";
    
    /** Repository view name of the message board. */
    public static String MESSAGE_BOARD = "messageBoard";
    
    /** Repository view name of the gear boards. */
    public static String GEAR_BOARDS = "gearBoards";  
    
    /** The delete flag.  Set on the forum repository item. */
    public static String DELETE_FLAG = "deleteFlag";

    protected static RqlStatement sMessageBoardStatement;
    static {
      try {
        sMessageBoardStatement = RqlStatement.parseRqlStatement("messageBoard = ?0");
      } catch (RepositoryException e) {}
    }
    
    public RqlStatement getMessageBoardStatement()
    {
        return sMessageBoardStatement;
    }
    
    /**
     * property: discussionRepository
     */
    protected MutableRepository mDiscussionRepository;
    
    protected MutableRepository getDiscussionRepository() throws NamingException
    {
        // perform JNDI lookup if null 
        if (mDiscussionRepository == null)
        {        
            mDiscussionRepository = (MutableRepository) NucleusComponents.lookup(DISCUSSION_REPOSITORY);           
        }
        return mDiscussionRepository;
    }
    
    public void setDiscussionRepository(MutableRepository pDiscussionRepository)
    {
        mDiscussionRepository = pDiscussionRepository;
    }          
        
    public TransactionManager lookupTransactionManager() throws NamingException
    {        
        return (TransactionManager) NucleusComponents.lookup(TRANSACTION_MANAGER);  
    }   
    
    //---------------------------------------
    /**
     * Delete a forum.
     * @param pForumId the parameter of the forum.
     */    
    public void deleteForum(String pForumId) 
    {        
        TransactionManager tm = null;
        TransactionDemarcation td = null;
        try
        {              
            tm = lookupTransactionManager();
            td = new TransactionDemarcation();                
            td.begin(tm);  
            
            setDeleteFlag(pForumId);
            deleteGearBoards(pForumId);            
            deleteUserGearBoards(pForumId);  
        } catch (TransactionDemarcationException e) 
        {
            rollback(tm);
            String msg = "DeleteForum - transaction error...";
            log(msg, e);
        } catch (NamingException ne)
        {
            rollback(tm);
            String msg = "DeleteForum - naming exception...";
            log(msg, ne);   
        } catch (RepositoryException re)
        {            
            rollback(tm);
            String msg = "DeleteForum - repository exception...";
            log(msg, re);   
        } finally
        {
            try
            {
                if (td != null) td.end();
            } catch (TransactionDemarcationException tde)
            {
            }
        }
    }     
    
    protected void setDeleteFlag(String pForumId)
        throws NamingException, RepositoryException
    {         
        MutableRepository mr = getDiscussionRepository();
        String forumId = pForumId;
        MutableRepositoryItem forum = mr.getItemForUpdate(forumId, MESSAGE_BOARD);
        Integer one = NumberTable.getInteger(1);
        forum.setPropertyValue(DELETE_FLAG, one);
        mr.updateItem(forum);           
    }
    
    protected void deleteGearBoards(String pForumId) 
        throws NamingException, RepositoryException
    {               
        MutableRepository r = getDiscussionRepository();
        RqlStatement statement = getMessageBoardStatement();
        String forumId = pForumId;        
        RepositoryView gbView = r.getView(GEAR_BOARDS);
        Object [] params = { forumId };
        RepositoryItem[] gearList = statement.executeQuery(gbView, params);
        
        // retrieve messageBoard items and copy to array
        if (gearList != null) {
            for (int i = 0; i < gearList.length; i++) {
                String id = gearList[i].getRepositoryId();
                r.removeItem(id, GEAR_BOARDS);
            }
        }
    }
    
    protected void deleteUserGearBoards(String pForumId) 
        throws NamingException, RepositoryException
    {        
        MutableRepository r = getDiscussionRepository();
        RqlStatement statement  = getMessageBoardStatement();
        String forumId = pForumId;        
        RepositoryView ubView = r.getView(USER_GEAR_BOARDS);
        Object[] params = { forumId };
        RepositoryItem[] gearList = statement.executeQuery(ubView, params);
        
        if (gearList != null) {
            for (int i = 0; i < gearList.length; i++) {
                String id = gearList[i].getRepositoryId();
                r.removeItem(id, USER_GEAR_BOARDS);
            }
        }  
    }
    
    void log(String pMessage, Throwable pThrowable)
    {
        //ServletContext sc = pageContext.getServletContext();
        //sc.log(pMessage, pThrowable);
        if (isLoggingError())
        {
            logError(pMessage, pThrowable);
        }
    }
 
    //-------------------------------------
    /**
     * Mark the current transaction to be rolled
     * back, logging any checked exceptions
     **/
    void rollback(TransactionManager pManager)
    {
        try
        {
            if (pManager != null)
                pManager.setRollbackOnly();
        } catch (SystemException se)
        {
            log("SystemException ", se);
        }
    }
   
}

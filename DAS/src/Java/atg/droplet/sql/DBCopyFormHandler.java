/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.droplet.sql;

// atg classes
import atg.adapter.gsa.*;
import atg.droplet.*;
import atg.servlet.*;

// java classes
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * This form handler uses a <code>DBCopier</code> to copy data from
 * one database to another.
 *
 * @see DBCopier
 *
 * @beaninfo
 *   description: Copy data using a DBCOpier.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Sam Perman
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/DBCopyFormHandler.java#3 $$Change: 658351 $
 * @updated $DateTime: 2011/07/19 16:08:54 $$Author: tterhune $
 */
public class DBCopyFormHandler extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/sql/DBCopyFormHandler.java#3 $$Change: 658351 $";

  //-------------------------------------
  // Properties
  //-------------------------------------

  //---------------------------------------------------------------------------
  // property:DBCopier
  //---------------------------------------------------------------------------

  private DBCopier mDBCopier;
  public void setDBCopier(DBCopier pDBCopier) {
    mDBCopier = pDBCopier;
  }

  /**
   * This is the class that actually performs the copy.  This property
   * should be set to a subclass of <code>DBCopier</code> that matches
   * the database you are using.  If you are using Oracle, then use OracleDBCopier.
   * If you are using DB2, then use DB2DBCopier.
   * @beaninfo
   *          description: The Object that performs the database copy
   **/
  public DBCopier getDBCopier() {
    return mDBCopier;
  }


  //---------------------------------------------------------------------------
  // property:SourceServer
  //---------------------------------------------------------------------------

  private String mSourceServer;
  public void setSourceServer(String pSourceServer) {
    mSourceServer = pSourceServer;
  }

  /**
   * The name of the server to copy <i>from</i>.
   * @beaninfo
   *          description: The name of the server to copy from
   **/
  public String getSourceServer() {
    return mSourceServer;
  }


  //---------------------------------------------------------------------------
  // property:SourceUser
  //---------------------------------------------------------------------------

  private String mSourceUser;
  public void setSourceUser(String pSourceUser) {
    mSourceUser = pSourceUser;
  }

  /**
   * The username to use when copying from <code>sourceServer</code>
   * @beaninfo
   *          description: The username to use when copying from the source server
   **/
  public String getSourceUser() {
    return mSourceUser;
  }


  //---------------------------------------------------------------------------
  // property:SourcePassword
  //---------------------------------------------------------------------------

  private String mSourcePassword;
  public void setSourcePassword(String pSourcePassword) {
    mSourcePassword = pSourcePassword;
  }

  /**
   * The password to use when copying from <code>sourceServer</code>
   * @beaninfo
   *          description: The password to use when copying from the source server
   **/
  public String getSourcePassword() {
    return mSourcePassword;
  }


  //---------------------------------------------------------------------------
  // property:DestinationServer
  //---------------------------------------------------------------------------

  private String mDestinationServer;
  public void setDestinationServer(String pDestinationServer) {
    mDestinationServer = pDestinationServer;
  }

  /**
   * The name of the server to copy <i>to</i>
   * @beaninfo
   *          description: The name of the server to copy to
   **/
  public String getDestinationServer() {
    return mDestinationServer;
  }

  //---------------------------------------------------------------------------
  // property:DestinationUser
  //---------------------------------------------------------------------------

  private String mDestinationUser;
  public void setDestinationUser(String pDestinationUser) {
    mDestinationUser = pDestinationUser;
  }

  /**
   * The username to use when copying to <code>destinationServer</code>
   * @beaninfo
   *          description: The username to use when copying from the destination server
   **/
  public String getDestinationUser() {
    return mDestinationUser;
  }


  //---------------------------------------------------------------------------
  // property:DestinationPassword
  //---------------------------------------------------------------------------

  private String mDestinationPassword;
  public void setDestinationPassword(String pDestinationPassword) {
    mDestinationPassword = pDestinationPassword;
  }

  /**
   * The password to use when copying to <code>destinationServer</code>
   * @beaninfo
   *          description: The password to use when copying from the destination server
   **/
  public String getDestinationPassword() {
    return mDestinationPassword;
  }

  //---------------------------------------------------------------------------
  // property:Status
  //---------------------------------------------------------------------------

  private int mStatus;

  /**
   * The result of <code>DBCopier.copy</code>
   * @beaninfo
   *          description: The result of DBCopier.copy
   **/
  public int getStatus() {
    return mStatus;
  }

  //---------------------------------------------------------------------------
  // property:SuccessURL
  //---------------------------------------------------------------------------

  private String mSuccessURL;
  public void setSuccessURL(String pSuccessURL) {
    mSuccessURL = pSuccessURL;
  }

  /**
   * Where to go on success
   * @beaninfo
   *          description: The URL to go to if the copy succeeds
   **/
  public String getSuccessURL() {
    return mSuccessURL;
  }


  //---------------------------------------------------------------------------
  // property:ErrorURL
  //---------------------------------------------------------------------------

  private String mErrorURL;
  public void setErrorURL(String pErrorURL) {
    mErrorURL = pErrorURL;
  }

  /**
   * where to go on error
   * @beaninfo
   *          description: The URL to go to if the copy fails
   **/
  public String getErrorURL() {
    return mErrorURL;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * Uses the <code>DBCopier</code> to copy the source database to the
   * destination.
   *
   * @param pRequest The request object.
   * @param pResponse The response object.
   * @return true if succesful, false otherwise
   * @exception ServletException
   * @exception IOException
   **/
  public boolean handleCopy(DynamoHttpServletRequest pRequest,
                        DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (! checkFormRedirect (null, getErrorURL(), pRequest, pResponse)) {
      if(isLoggingDebug())
        logDebug("checkFormRedirect returned false.");

      return false;
    }

    try {
      if(isLoggingDebug())
        logDebug("Copying the data");
      DBCopier copier = getDBCopier();
      DBConnectionInfo source = new DBConnectionInfo();
      DBConnectionInfo destination = new DBConnectionInfo();

      String sourceServer = getSourceServer();
      String sourceUser = getSourceUser();
      String sourcePass = getSourcePassword();

      String destinationServer = getDestinationServer();
      String destinationUser = getDestinationUser();
      String destinationPass = getDestinationPassword();

      source.setServer(sourceServer);
      source.setUser(sourceUser);
      source.setPassword(sourcePass);

      destination.setServer(destinationServer);
      destination.setUser(destinationUser);
      destination.setPassword(destinationPass);

      copier.setSource(source);
      copier.setDestination(destination);

//        atg.core.util.CommandProcessor cp = new atg.core.util.CommandProcessor("badcommand");
//        int status = cp.exec();
//        String output = cp.getOutput();

//        System.out.println("status: " + status);
//        System.out.println("output: " + output);
      mStatus = copier.copy();
      if (mStatus != 0) {
        addFormException(new DropletException(copier.getCommandOutput()));
      }

    }
    catch(Exception e) {
      if(isLoggingError())
        logError(e);

      addFormException(new DropletException("Can't copy db.", e));
      return checkFormRedirect(getSuccessURL(),getErrorURL(),pRequest,pResponse);
    }

    return checkFormRedirect(getSuccessURL(),getErrorURL(),pRequest,pResponse);
  }

} // end of class



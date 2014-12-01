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

package atg.projects.b2bstore.order;

// Java classes
import java.util.*;
// DAS classes
import atg.service.pipeline.*;
import atg.repository.*;
import atg.adapter.gsa.*;
import atg.nucleus.*;
import atg.servlet.*;
import atg.core.util.*;
  
// DPS classes
import atg.userprofiling.*;
// DSS classes

// DCS classes
import atg.commerce.order.*;
import atg.commerce.order.processor.*;

/**
 * This processor is called in the final stages of processOrder chain. Whenever user
 * purchases any items, those items and profileId of the user are stored in 
 * userCatalogRefs item descriptor.
 * @author Manoj Potturu
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/ProcUpdateCatalogRefOfProfile.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */


public class ProcUpdateCatalogRefOfProfile extends GenericService implements PipelineProcessor
{
  //-------------------------------------
  // class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/ProcUpdateCatalogRefOfProfile.java#2 $$Change: 651448 $";
  
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final ResourceBundle sResourceBundle = ResourceBundle.getBundle(ProcessorConstants.MY_RESOURCE_NAME);
  
  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  /** Order Repository */
  Repository mRepository;

  //-------------------------------------
  /**
   * Sets Order Repository
   **/
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }

  //-------------------------------------
  /**
   * Returns Order Repository
   **/
  public Repository getRepository() {
    return mRepository;
  }
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  public ProcUpdateCatalogRefOfProfile() {
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * This method is executed by the pipeline manager to execute this
   * process. This processor takes Order & Profile Parameters from the
   * passed parameters and tries to add the catalogRef of all the commerce
   * Items to the userCatalogRefs item-descriptor.
   *
   **/
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {

    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    RepositoryItem profile = (RepositoryItem) map.get(PipelineConstants.PROFILE);

    // Check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource(ProcessorConstants.INVALID_ORDER_PARAMETER,
                                                                       ProcessorConstants.MY_RESOURCE_NAME,
                                                                       sResourceBundle));
    if ( profile == null) 
      throw new InvalidParameterException(ResourceUtils.getMsgResource(ProcessorConstants.INVALID_PROFILE_PARAMETER,
                                                                       ProcessorConstants.MY_RESOURCE_NAME,
                                                                       sResourceBundle));
    checkAndUpdate(order, profile);
    return ProcessorConstants.SUCCESS;
  }
  
    /**
     * This function checks the profile for the catalogRefId, and updates the
     * profile with catalogRefId if it doesn't contain the catalog
     *
     * @param pOrder the order
     * @param pProfile the profile to check and update for.
     * @throws RepositoryException
     *
     **/
  protected void checkAndUpdate(Order pOrder, RepositoryItem pProfile)
    throws RepositoryException{
      
    MutableRepositoryItem mutItem = null;
    CommerceItem ci = null;
    boolean catalogRefExists;

    // Get the items from the order
    List commerceItemsList = pOrder.getCommerceItems();
    Iterator CIIterator = commerceItemsList.iterator();

    // Get the profile to check & update for.
    String profileId = pProfile.getRepositoryId();
    MutableRepository mutRep = (MutableRepository) getRepository();
    
    // Iterate through each commerce items
    while ( CIIterator.hasNext()) {

      ci = (CommerceItem) CIIterator.next();

      // check whether the catalogRef of given CI already exits with this profile
       catalogRefExists = checkProfile(ci, pProfile);
        
      // if catalogRef does'nt exist then update with this catalogref & profileId
      if ( !catalogRefExists) {
        String catalogRefId = ci.getCatalogRefId();
    
        try {
          mutItem = mutRep.createItem(ProcessorConstants.DESC_NAME);
          mutItem.setPropertyValue(ProcessorConstants.PROFILE_ID, profileId);
          mutItem.setPropertyValue(ProcessorConstants.CATALOG_REF_ID, catalogRefId);
          mutRep.addItem(mutItem);
        }
        catch(RepositoryException re)
        {
          if ( isLoggingError()) {
            logError(re);
          } // end of if ()
        }
      } // end of if ()
    } // end of while ()
  }

   /**
    * This function checks whether the given profile contains the catalogRefId
    * of the commerce item. If it contains then it returns false, else returns 
    * true.
    *
    * @param pCItem the CommerceItem which contains the catalogRefId.
    * @param pProfile the profile of the user to check for.
    * @throws RepositoryException
    * @return true if profile contains catalogRefId, false else.
    **/
  protected boolean checkProfile(CommerceItem pCItem, RepositoryItem pProfile)
    throws RepositoryException{
       

    // Get the catalog and profile ids.
    String catalogRefId = pCItem.getCatalogRefId();
    String profileId = pProfile.getRepositoryId();
    
    try {
      RepositoryView rv = getRepository().getView(ProcessorConstants.DESC_NAME);
      QueryBuilder qb = rv.getQueryBuilder();

      // Create a query for profile_id
      QueryExpression profileProperty = qb.createPropertyQueryExpression(ProcessorConstants.PROFILE_ID);
      QueryExpression profileValue = qb.createConstantQueryExpression(profileId);
      Query queryProfile = qb.createComparisonQuery(profileProperty, profileValue, QueryBuilder.EQUALS);

      // Create a query for catalog_id
      QueryExpression catProperty = qb.createPropertyQueryExpression(ProcessorConstants.CATALOG_REF_ID);
      QueryExpression catValue = qb.createConstantQueryExpression(catalogRefId);
      Query queryCat = qb.createComparisonQuery(catProperty, catValue, QueryBuilder.EQUALS);

      // combine the two queries
      Query[] pieces = { queryProfile, queryCat };
      Query finalQuery = qb.createAndQuery(pieces);

      RepositoryItem[] items = rv.executeQuery(finalQuery);

      // if items is null then no items exist.
      if ( items != null ){
        return true;
      } 
    }
    catch(RepositoryException re)
    {
      if (isLoggingError()) {
        logError(re);
      } // end of if ()
        
    }
    return false;
  }

  /**
   * The return codes that this processor can return.
   * The list of return codes are:
   * <UL>
   *   <LI>1 - The processor completed
   * </UL>
   *
   * @return an <code>int[]</code> of the valid return codes
   */
  public int[] getRetCodes() {
    int[] retCodes = {ProcessorConstants.SUCCESS};
    return retCodes;
  }

}   // end of class

  
  

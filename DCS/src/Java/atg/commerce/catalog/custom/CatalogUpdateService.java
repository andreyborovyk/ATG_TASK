 /*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.commerce.catalog.custom;

import atg.repository.*;
import atg.commerce.catalog.CMSConstants;
import atg.commerce.catalog.custom.TreeWalkUpdateService.ShadowMapKey;
import atg.core.util.ResourceUtils;
import atg.epub.project.CreateProject;
import atg.epub.project.ProjectException;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.versionmanager.WorkingContext;
import atg.versionmanager.Workspace;
import java.util.*;
/**
 * <p>
 * This service batch-computes the properties that are usually maintained
 * by <code>CatalogCompletionService</code>. This can be used either as 
 * a migration tool, or if you want to disable <code>CatalogCompletionService</code> 
 * for performance reasons, and then batch-compute the properties with 
 * this service. Properties computed by this service: 
 * catalog.directAncestorCatalogsAndSelf
 * catalog.allRootCategories 
 * catalog.indirectAncestorCatalogs
 * catalog.ancestorCategories 
 * category.parentCategory 
 * category.parentCatalog
 * 
 * @see CatalogCompletionService
 * 
 * @author Jon Turgeon
 * @beaninfo 
 *   description: A service used to batch-compute the properties regularly
 *                maintained by CatalogCompletionService 
 *   displayname: CatalogUpdateService
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/custom/CatalogUpdateService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CatalogUpdateService 
  extends TreeWalkUpdateService 
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/custom/CatalogUpdateService.java#2 $$Change: 651448 $";


  public String getNameToLog() {
    return "CatalogUpdateService";
  }  
  
  public String getServiceName()
  {
    return CMSConstants.TYPE_UPDATE;
  }
    
  private String mProjectActivityID;
  
  public String getProjectActivityID() {
    return mProjectActivityID;
  }

  public void setProjectActivityID(String pProjectActivityID) {
    mProjectActivityID = pProjectActivityID;
  }

/**
   * The main method does some basic setup and logging, calls processTree and
   * then does some cleanup.
   *    
   * When running in versioned environment, in addition to the above functionality, it creates a
   * project using configured workflow and make the associated workspace current development line
   * before making any changes to the catalog.
   *
   * @beaninfo descripton: Batch-updates all incrementally-computed properties of the catalog and
   *    category repository items
   */
  protected void updateCatalog() {

    long startTime = System.currentTimeMillis();
    
    if(isLoggingInfo()) 
    {
      String msgArgs[] = { ResourceUtils.getMsgResource("PROCESS_UPDATE_CATALOG", MY_RESOURCE_NAME, sResourceBundle), new Date(startTime).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_STARTED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));
    }

    Workspace ws=null; 

    // Save previous thread user so that it can be restored later
    User previousUser = ThreadSecurityManager.currentUser();

    try{
      if(getProjectWorkflow()!=null){
        // ProjectWorkflow is configured, hence assumed that CUS is running in Versioned environment
        if(isLoggingDebug())
          logDebug("Project Workflow: " + getProjectWorkflow());

        String processName = getProcessName();
        if(isLoggingDebug())
          logDebug("Process Name: " + processName);

        // Get the workspace Id
        String workspaceId = null;
        
        if (getProjectActivityID() != null)
          workspaceId = CreateProject.createProcess(processName, getProjectWorkflow(), getProjectActivityID(), PUBLISHING_USER);
        else
          workspaceId = CreateProject.createProcess(processName, getProjectWorkflow(), PUBLISHING_USER);
 
        if(isLoggingDebug())
          logDebug("Worskspace ID: " + workspaceId);

        // Use workspace id to get the workspace
        ws = getWorkspace(workspaceId);
        if(isLoggingDebug())
          logDebug("Workspace: " + ws.getID());

        // Make the workspace current development line.
        WorkingContext.pushDevelopmentLine(ws);
      }
      if(getRepository() == null)
        setRepository(getCatalogTools().getCatalog());
      
      if(getRepository() == null)
      {
        if(isLoggingError())
          logError("CATALOG REPOSITORY IS NULL");
        return;
      }
      
      if(isLoggingDebug())
        logDebug("CATALOG REPOSITORY IS " + getRepository());
      if(isLoggingInfo())
        logInfo(ResourceUtils.getMsgResource("START_UPDATE", MY_RESOURCE_NAME, sResourceBundle));
      
      //Do the real work
      processTree();

      if(isLoggingInfo())
      {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        
        String msgArgs[] = { ResourceUtils.getMsgResource("PROCESS_UPDATE_CATALOG", MY_RESOURCE_NAME, sResourceBundle), new Date(endTime).toString() };
        
        logInfo(ResourceUtils.getMsgResource("PROCESS_ENDED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));

        String msgArgs1[] = { ResourceUtils.getMsgResource("PROCESS_UPDATE_CATALOG", MY_RESOURCE_NAME, sResourceBundle), Long.valueOf(seconds).toString() };
        logInfo(ResourceUtils.getMsgResource("PROCESS_TIME_INSECS", MY_RESOURCE_NAME, sResourceBundle, msgArgs1));
      }
    } catch(RepositoryException re) {
      if(isLoggingError()){
        logError(re);
      }
    } catch(ProjectException pe) {
      if(isLoggingError()){
        logError(pe);
      }
    } catch(Exception e) {
      if(isLoggingError()){
        logError(e);
      }
    } 
    finally{
      //pops the current development line when on versioned environments
      if(getProjectWorkflow() != null && ws != null){
        WorkingContext.popDevelopmentLine();
        unassumeUserIdentity(previousUser);
      }
    }
    return;
  }
  
  /**
   * This method sets all the properties calculated by this service
   * that are pulled down from the top of the tree. Most of the attributes that
   * are updated are pulled down from the top. Since this method can be called 
   * passing catalogs, categories or folders we need to check what the parent
   * item and child items are. Since all 3 item types (catalog, category and folder)
   * all update site we do not need to check the type to do the update.
   * 
   * Catalog attributes being updated are:
   *  DirectAncestorCatalogsAndSelf, IndirectAncestorCatalogs, AncestorCategories,
   *  Sites
   *  
   * Category attributes being updated are:
   *  ComputedCatalogs, ParentCatalog, AncestorCategories, Sites
   *  
   * Folder attributes being updated are:
   *  Sites  
   * 
   * @param pParentItem the parent item is the item that the data will be pulled from
   * @param pCurrentItem the current item is the item that will be updated with 
   *  data from the pParentItem.
   * 
   * @throws RepositoryException the repository exception
   * 
   */
  protected void goingDownUpdateItem(RepositoryItem pParentItem, RepositoryItem pCurrentItem, ShadowBean pShadowBean)
    throws RepositoryException {
    
    CatalogProperties props = getCatalogProperties(); 
   
    if (isLoggingDebug()) {
      logDebug("Inside goingDownUpdateItem with pParentItem:" + pParentItem);
      logDebug("Inside goingDownUpdateItem with pCurrentItem:" + pCurrentItem);
      logDebug("Inside goingDownUpdateItem with pShadowBean:" + pShadowBean);
    }
    
    //Since the repository items are not updated till the very end we need to get prior values from the tracking object!
    ShadowMapKey parentId = new ShadowMapKey(pParentItem.getRepositoryId(), pParentItem.getItemDescriptor().getItemDescriptorName());
    ShadowBean parentShadowBean = 
      (ShadowBean) getCompletedItems().get(parentId);

    //In the case we are at the top of the tree the parent and current items are the same.
    if (parentShadowBean == null)
      parentShadowBean = pShadowBean;
    
    if (pCurrentItem != null) {
      
      if (getCatalogTools().isCatalogFolder(pParentItem)) {
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          // So the parent is a catalog and the current item is a catalog, set the direct
          // catalog attribute
          List<String> directAncestorCatalogsAndSelf = new ArrayList<String>();
          directAncestorCatalogsAndSelf.add(pCurrentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getDirectAncestorCatalogsAndSelfPropertyName(),
              directAncestorCatalogsAndSelf, pShadowBean);
        }
      } else if (getCatalogTools().isCatalog(pParentItem)) {
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          //So the parent is a catalog and the current item is a catalog, set the direct 
          //catalog attribute
          List <String> directAncestorCatalogsAndSelf;
          if (parentShadowBean.getDirectAncestorCatalogsAndSelf() != null)
            directAncestorCatalogsAndSelf = new ArrayList <String> (parentShadowBean.getDirectAncestorCatalogsAndSelf());
          else
            directAncestorCatalogsAndSelf = new ArrayList <String>();
          
          directAncestorCatalogsAndSelf.add(pCurrentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getDirectAncestorCatalogsAndSelfPropertyName(), directAncestorCatalogsAndSelf, pShadowBean);
          
          List<String> indirectAncestorCatalogs = null;
          if (parentShadowBean.getIndirectAncestorCatalogs() != null)
            indirectAncestorCatalogs = new ArrayList<String>(parentShadowBean.getIndirectAncestorCatalogs());
          else
            indirectAncestorCatalogs = new ArrayList<String>();

          setPropertyValueAddAll(pCurrentItem, props.getIndirectAncestorCatalogsPropertyName(),
              indirectAncestorCatalogs, pShadowBean);
        } else if (getCatalogTools().isCategory(pCurrentItem)) {
          //So the parent is a catalog and the current item is a category
          List <String> directAncestorCatalogsAndSelf = null;
          if (parentShadowBean.getDirectAncestorCatalogsAndSelf() == null)
            directAncestorCatalogsAndSelf = new ArrayList <String>();
          else
            directAncestorCatalogsAndSelf = new ArrayList <String> (parentShadowBean.getDirectAncestorCatalogsAndSelf());

          List <String> indirectAncestorCatalogs = null;
          if (parentShadowBean.getIndirectAncestorCatalogs() == null)
            indirectAncestorCatalogs = new ArrayList <String>();
          else
            indirectAncestorCatalogs = new ArrayList <String>(parentShadowBean.getIndirectAncestorCatalogs());
          
          Set<String> tempSet = new HashSet<String>();
          tempSet.addAll(directAncestorCatalogsAndSelf);
          tempSet.addAll(indirectAncestorCatalogs);
          if (!tempSet.contains(pParentItem) && pParentItem != pCurrentItem)
            tempSet.add(pParentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getComputedCatalogsPropertyName(), tempSet, pShadowBean);
        }
        
        //Parent is a Catalog, so just clone the parents categories
        List<String> ancestorCategories = null;
        if (parentShadowBean.getAncestorCategories() == null)
          ancestorCategories = new ArrayList<String>();
        else
          ancestorCategories = new ArrayList<String>(parentShadowBean.getAncestorCategories());
        setPropertyValueAddAll(pCurrentItem, props.getAncestorCategoriesPropertyName(), ancestorCategories, pShadowBean);

      } else if (getCatalogTools().isCategory(pParentItem)) {
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          //So the parent is a category and the current item is a catalog, set the direct 
          //catalog attribute, since the parent was a category then start out as an empty set
          List <String> directAncestorCatalogsAndSelf = new ArrayList <String>();
          directAncestorCatalogsAndSelf.add(pCurrentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getDirectAncestorCatalogsAndSelfPropertyName(), directAncestorCatalogsAndSelf, pShadowBean);

          //So the parent is a category and the current item is a catalog, set the direct catalog attribute
          Set<String> indirectAncestorCatalogs = parentShadowBean.getComputedCatalogs();
          setPropertyValueAddAll(pCurrentItem, props.getIndirectAncestorCatalogsPropertyName(), new ArrayList<String>(indirectAncestorCatalogs), pShadowBean);
        
        } else if (getCatalogTools().isCategory(pCurrentItem)) {
          //So the parent is a category and the current item is a category
          Set<String> catalogs = null;
          if (parentShadowBean.getComputedCatalogs() == null)
            catalogs = new HashSet<String>();
          else
            catalogs = parentShadowBean.getComputedCatalogs();
          setPropertyValueAddAll(pCurrentItem, props.getComputedCatalogsPropertyName(), catalogs, pShadowBean);
        }

        //Parent is a Category, so just clone the parents categories and add the parent category
        List<String> parentAncestorCategories = null;
        if (parentShadowBean.getAncestorCategories() == null)
          parentAncestorCategories = new ArrayList<String>();
        else
          parentAncestorCategories = new ArrayList<String>(parentShadowBean.getAncestorCategories());
        parentAncestorCategories.add(pParentItem.getRepositoryId());
        setPropertyValueAddAll(pCurrentItem, props.getAncestorCategoriesPropertyName(), parentAncestorCategories, pShadowBean);
      } 
      
      //These are generic attributes that are on both catalogs and categories so it is a bit simpler
      //Copy down the sites from the parent.
      Set<String> sitesForPreviousItem = null;
      if (parentShadowBean.getSites() == null)
        sitesForPreviousItem = new HashSet<String>();
      else
        sitesForPreviousItem = parentShadowBean.getSites();
      setPropertyValueAddAll(pCurrentItem, props.getSitesPropertyName(), sitesForPreviousItem, pShadowBean); 
      
      //This completed items is used for the entire catalog repository to enhance the performance by stopping the 
      //recursion if this item (catalog or category) has been processed.
      ShadowMapKey id = new ShadowMapKey(pCurrentItem.getRepositoryId(), pCurrentItem.getItemDescriptor().getItemDescriptorName());
      getCompletedItems().put(id, pShadowBean);
    }
  }
  
  /**
   * This method sets all the properties calculated by this service
   * the are pulled up from the bottom of the tree. Currently the only
   * value pulled up is the AllRootCategories. This is the first category 
   * while walking down each branch.
   * 
   * @param pParentItem the parent item that will be updated.
   * @param pCurrentItem the current item is used to pull the data
   * from for the pParentItem update.
   * 
   * @throws RepositoryException the repository exception
   */
  protected void goingUpUpdateItem(RepositoryItem pParentItem, RepositoryItem pCurrentItem, 
      ShadowBean pShadowBean)
    throws RepositoryException {
    
    CatalogProperties props = getCatalogProperties(); 
   
    if (isLoggingDebug()) {
      logDebug("Inside goingUpUpdateItem with pParentItem:" + pParentItem);
      logDebug("Inside goingUpUpdateItem with pCurrentItem:" + pCurrentItem);
      logDebug("Inside goingDownUpdateItem with pShadowBean:" + pShadowBean);
    }
    
    if (pCurrentItem != null) {
      //If the parent Item is a catalog then update then add the catalog to the set of parent catalogs
      if (getCatalogTools().isCatalog(pParentItem)) {
        //Since we are updatng the parent item we need to get the parent item from the map of items, if the
        //parent has not been processed then it is not in the map and will need to create it!
        ShadowBean parentShadowBean = null;
        
        ShadowMapKey parentId = new ShadowMapKey(pParentItem.getRepositoryId(), pParentItem.getItemDescriptor().getItemDescriptorName());
        if (getCompletedItems().get(parentId) == null) {
          parentShadowBean = new ShadowBean();
          parentShadowBean.setId((String) pParentItem.getPropertyValue(props.getIdPropertyName()));
          
          parentShadowBean.setTypeOfItem(pParentItem.getItemDescriptor().getItemDescriptorName());
          
          //Always grab the sites from the repository and add them to the tracker object
          Set<String> sites = (Set<String>) pParentItem.getPropertyValue(props.getSitesPropertyName());
          parentShadowBean.setSites(sites);
        } else
          parentShadowBean = (ShadowBean) getCompletedItems().get(parentId);
        
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          //So the parent is a catalog and the current item is a catalog, set the root categories
          //Since the current catalog can have both root categories and descended root categrories we need to add them both to the parent
          Set<String> allRootCategories = new HashSet<String>();
          
          //This is always from the repository as it is not a calced value
          for (RepositoryItem item:(Set<RepositoryItem>) pParentItem.getPropertyValue(props.getRootCategoriesPropertyName())) {
            allRootCategories.add(item.getRepositoryId());
          }
          
          //This is the first time this has been calced so grab the info from the repository
          if (pShadowBean.getAllRootCategories() == null) {
            for (RepositoryItem item:(Set<RepositoryItem>) pCurrentItem.getPropertyValue(props.getAllRootCategoriesPropertyName())) {
              allRootCategories.add(item.getRepositoryId());
            }
          } else {
            allRootCategories.addAll(pShadowBean.getAllRootCategories());
          }
          
          setPropertyValueAddAll(pParentItem, props.getAllRootCategoriesPropertyName(), allRootCategories, parentShadowBean);           
        } else if (getCatalogTools().isCategory(pCurrentItem)) {
          //So the parent is a catalog and the current item is a category, copy over the rootCategories to the allRootCategories
          Set<String> allRootCategories = new HashSet<String>();
          
          //This is always from the repository as it is not a calced value
          for (RepositoryItem item:(Set<RepositoryItem>) pParentItem.getPropertyValue(props.getRootCategoriesPropertyName())) {
            allRootCategories.add(item.getRepositoryId());
          }
          
          setPropertyValueAddAll(pParentItem, props.getAllRootCategoriesPropertyName(), allRootCategories, parentShadowBean); 
        }
        
        //This completed items is used for the entire catalog repository to enhance the performance by stopping the 
        //recursion if this item (catalog or category) has been processed.
        getCompletedItems().put(parentId, parentShadowBean);
      } 
    }
  }
}

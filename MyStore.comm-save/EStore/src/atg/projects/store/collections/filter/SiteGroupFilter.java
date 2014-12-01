/*<ATGCOPYRIGHT>
 * Copyright (C) 2009-2010 Art Technology Group, Inc.
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

package atg.projects.store.collections.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import atg.multisite.SiteGroupManager;
import atg.multisite.SiteManager;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;

/**
 * <p>
 * Filters a collection of (repository) items on their sites/siteGroup
 * properties.
 * </p>
 * 
 * <p>
 * A Set of the (repository) items site ids and siteGroups site ids is 
 * constructed and compared to the site ids of the current sites 
 * mShareableTypeId siteGroup site ids. If any sites from the items Set match
 * then the item will be returned in the filtered collection.
 * </p>
 * 
 * @author ckearney
 */
public class SiteGroupFilter extends CachedCollectionFilter{

  /** Class version */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/collections/filter/SiteGroupFilter.java#3 $$Change: 635816 $";

  //-----------------------------------
  // PROPERTIES
  //-----------------------------------
  
  //-------------------------------------
  // property: shareableTypeId
  protected String mShareableTypeId;

  /**
   * This property contains a shareable ID to be used when determining sharing sites.
   * @return current shareable type ID
   */
  public String getShareableTypeId() {
    return mShareableTypeId;
  }

  /**
   * Sets the sharableTypeId
   * @param pShareableTypeId
   */
  public void setShareableTypeId(String pShareableTypeId) {
    mShareableTypeId = pShareableTypeId;
  }
  
  //-------------------------------------
  // property: sitesPropertyName
  protected String mSitesPropertyName;

  /**
   * This property contains the name of the property that holds references to an item's sites.
   * @return 'mSitesPropertyName' property name
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }

  public void setSitesPropertyName(String pSitesPropertyName) {
    mSitesPropertyName = pSitesPropertyName;
  }
  
  //-------------------------------------
  // property: siteGroupPropertyName
  protected String mSiteGroupPropertyName;

  /**
   * This property contains the name of the property that holds references to an item's siteGroups.
   * @return 'mSiteGroupPropertyName' property name
   */
  public String getSiteGroupPropertyName() {
    return mSiteGroupPropertyName;
  }

  public void setSiteGroupPropertyName(String pSiteGroupPropertyName) {
    mSiteGroupPropertyName = pSiteGroupPropertyName;
  }
  
  //-------------------------------------
  // property: idPropertyName
  protected String mIdPropertyName;

  /**
   * This property contains the name of the property that holds references to an item's id.
   * @return 'mIdPropertyName' property name
   */
  public String getIdPropertyName() {
    return mIdPropertyName;
  }

  public void setIdPropertyName(String pIdPropertyName) {
    mIdPropertyName = pIdPropertyName;
  }
  
  
  //-------------------------------------
  // property: siteGroupManager
  protected SiteGroupManager mSiteGroupManager;

  /**
   * This property contains a reference to {@link SiteGroupManager} to be used when determining sharing sites.
   * @return SiteGroupManager instance
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }

  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }

  //-------------------------------------
  // property: siteManager
  protected SiteManager mSiteManager;

  /**
   * This property contains a reference to {@link SiteManager} to be used when determining active sites.
   * @return SiteManager instance
   */
  public SiteManager getSiteManager() {
    return mSiteManager;
  }

  public void setSiteManager(SiteManager pSiteManager) {
    mSiteManager = pSiteManager;
  }
  
  //-------------------------------------
  // property: includeDisabledSites
  protected boolean mIncludeDisabledSites;

  /**
   * Returns the includeDisabledSites property.
   * 
   * @return the includeDisabledSites property.
   */
  public boolean isIncludeDisabledSites() {
    return mIncludeDisabledSites;
  }

  /**
   * Sets the includeDisabledSites property.
   * 
   * @param pIncludeDisabledSites whether or not to filter out items that exist only
   * on disabled sites
   */
  public void setIncludeDisabledSites(boolean pIncludeDisabledSites) {
    mIncludeDisabledSites = pIncludeDisabledSites;
  }
  
  //-------------------------------------
  // property: includeInactiveSites
  /**
   * mIncludeInactiveSites; a value of <code>true</code> indicates that items should be
   * retained in the collection even if all their sites are inactive
   */
  protected boolean mIncludeInactiveSites;

  /**
   * Returns the includeInactiveSites property.
   * 
   * @return the includeInactiveSites property.
   */
  public boolean isIncludeInactiveSites() {
    return mIncludeInactiveSites;
  }

  /**
   * Sets the includeInactiveSites property.
   * 
   * @param pIncludeInactiveSites whether or not to filter out items that exist only
   * on inactive sites 
   */
  public void setIncludeInactiveSites(boolean pIncludeInactiveSites) {
    mIncludeInactiveSites = pIncludeInactiveSites;
  }
  
  //-----------------------------------
  // property: mAllowAllSite
  private boolean mAllowAllSite;
  
  /**
   * @return allowAllSite boolean
   */
  public boolean isAllowAllSite() {
    return mAllowAllSite;
  }

  /**
   * If set to true and there are no sites/siteGroups configured
   * for a particular item in the collection to be filtered, the
   * item is treated as all-site and will be returned in the filtered
   * collection.
   * 
   * @param pAllowAllSite Indicates whether or not to return an item
   * if it has no sites/siteGroups configured
   */
  public void setAllowAllSite(boolean pAllowAllSite) {
    mAllowAllSite = pAllowAllSite;
  }

  //-----------------------------------
  // METHODS
  //-----------------------------------
  
  /**
   * This method filters the passed in collection (of repository items) based
   * on whether or not any of its items sites/siteGroups.sites exist in the
   * current mShareableTypeId siteGroup
   * 
   * @param pUnfilteredCollection the collection to be filtered
   * @param pCollectionIdentifierKey unused
   * pProfile pCollectionIdentifierKey unused
   * 
   * @return a collection items that have the current site or siteGroup
   * 
   * @throws FilterException if the item does not have a sites property
   */
  @Override
  public Collection generateFilteredCollection(Collection pUnfilteredCollection, String pCollectionIdentifierKey, RepositoryItem pProfile)
      throws FilterException {
    
    //If the sites property name is not entered then just return the unfiltered collection
    if (getSitesPropertyName() == null)
      return pUnfilteredCollection;

    //The result
    Collection<RepositoryItem> resultCollection = new HashSet<RepositoryItem>();

    // Current sharing sites, there should remain items from these sites only
    Collection<String> sharingSiteIds = getSiteGroupManager().getSharingSiteIds(getShareableTypeId());

    // sharingSiteIds includes the current site. It's null if there is no current site or if
    // the configured shareable type isn't registered (e.g., the customer has unregistered the
    // ShoppingCart shareable type to say that all sites can share the cart). In either case,
    // we return all items in the unfiltered collection.
    if (sharingSiteIds == null) {
      return pUnfilteredCollection;
    }

    // If we're supposed to filter out items that exist only on disabled sites or only on inactive sites,
    // then remove the sites from consideration before we start looking at the items.
    if (!isIncludeDisabledSites() || !isIncludeInactiveSites()) {
      String[] siteArray = sharingSiteIds.toArray(new String[0]);
      if (!isIncludeDisabledSites()) {
        siteArray = getSiteManager().filterDisabledSites(siteArray);
      }
      if (!isIncludeInactiveSites()) {
        siteArray = getSiteManager().filterInactiveSites(siteArray);
      }
      // If all the sites in the group are gone, filter out all the items.
      if (siteArray.length == 0) {
        return new HashSet();
      }
      sharingSiteIds = Arrays.asList(siteArray);
    }
    
    // Perform the filtering
    for (Iterator<RepositoryItem> iterator = pUnfilteredCollection.iterator(); iterator.hasNext();) {
      RepositoryItem item = iterator.next();
      
      // Merged siteIds set
      Set combinedSiteIds = new HashSet();
      
      // The items siteIds
      Set itemSiteIds = getSiteIds(item);
      if(itemSiteIds != null && itemSiteIds.size() > 0){
        combinedSiteIds.addAll(itemSiteIds);
      }
      
      // The items siteGroup siteIds
      Set itemSiteGroupSiteIds = getSiteGroupSiteIds(item);
      if(itemSiteGroupSiteIds != null && itemSiteGroupSiteIds.size() > 0){
        combinedSiteIds.addAll(itemSiteGroupSiteIds);
      }

      // All-site
      if(isAllowAllSite() && combinedSiteIds.size() == 0){
        resultCollection.add(item);
        continue;
      }
           
      //Now see if this items sites exists in the sharingSiteIds
      if (!Collections.disjoint(combinedSiteIds, sharingSiteIds)) {
        resultCollection.add(item);
      }
    }
    return resultCollection;
  }
  
  /**
   * Gets the siteIds property from the passed in RepositoryItem
   * and returns them in a Set.
   * 
   * @param pItem A RepositoryItem
   * @return A Set of pItems siteIds
   */
  protected Set<String> getSiteIds(RepositoryItem pItem){
    // A collection of Site RepositoryItems
    Collection sites = null;
    
    try{
      sites = (Collection)pItem.getPropertyValue(getSitesPropertyName());
    }
    catch(Exception e){
      if(isLoggingError()){
        logError("Could not get property " + getSitesPropertyName()
            + " from repository item " + pItem, e);
      }
    }
    
    if(pItem == null){
      return null;
    }
    
    // Create our result set from the collection of sites
    Set<String> siteIds = new HashSet<String>(sites.size());
    for(Object item : sites){
      if(item instanceof RepositoryItem){
        String siteId = (String) ((RepositoryItem)item).getPropertyValue(getIdPropertyName());
        
        if(siteId == null){
          continue;
        }
        siteIds.add(siteId);
      }
    }
    return siteIds;
  }
  
  /**
   * Gets the siteIds from all pItems siteGroups and returns them in 
   * a Set.
   * 
   * @param pItem A RepositoryItem
   * @return A Set of siteIds constructed from the siteGroups of pItem
   */
  protected Set<String> getSiteGroupSiteIds(RepositoryItem pItem){
    // A collection of SiteGroup repository Items
    Collection siteGroups = null;
    
    try{
      siteGroups = (Collection)pItem.getPropertyValue(getSiteGroupPropertyName());
    }
    catch(Exception e){
      if(isLoggingError()){
        logError("Could not get property " + getSiteGroupPropertyName()
            + " from repository item " + pItem, e);
      }
    }
    
    if(siteGroups == null){
      return null;
    }
    
    // Create our result set from the collection of siteGroups
    Set<String> siteIds = new HashSet();    
    for(Object site : siteGroups){
      if(site instanceof RepositoryItem){
        Set<String> currentSiteId = getSiteIds((RepositoryItem)site);
        if(currentSiteId != null){
          siteIds.addAll(currentSiteId);
        }
      }
    }
    return siteIds;
  }
}

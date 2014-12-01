/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.commerce.collections.filter;


import java.util.*;
import atg.beans.*;
import atg.service.collections.filter.*;
import atg.repository.*;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.inventory.InventoryException;
import atg.commerce.catalog.CatalogServices;
import atg.commerce.catalog.CatalogTools;
/**
 * This filter will filter products in the collection based on their inventory
 * availability.
 * <p>
 * The availability state for each sku is checked against the InventoryManager. If any of a product's
 * skus are considered available, the product is considered available and is added to the
 * filtered results.
 * <p>
 * The inventory states that mean a sku is available are configurable using the
 * <tt>inclludeInventoryStates</tt> property. See the InventoryManager for
 * valid states.
 * <p>
 * The InventoryFilter cache should be disabled if your Inventory Manager is already caching
 * inventory status information.
 *
 * @see atg.commerce.inventory.InventoryManager
 * @author Jim Fecteau
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/collections/filter/InventoryFilter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * 
 */

public class InventoryFilter extends CachedCollectionFilter

{

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/collections/filter/InventoryFilter.java#2 $$Change: 651448 $";

  /**
  * Sets the array of possible inventory states that mean a sku
  * is considered available.
  */
  protected Integer [] mIncludeInventoryStates;
  public void setIncludeInventoryStates(Integer [] pIncludeInventoryStates)
  {
    mIncludeInventoryStates = pIncludeInventoryStates;
  }
  /**
  * Returns the array of possible inventory states that mean a sku
  * is considered available.
  * <p>
  * See the InventoryManager javadoc for all valid inventory states.
  * <p>
  * @see atg.commerce.inventory.InventoryManager
  * @beaninfo description: The array of possible inventory states that mean a sku
  * is considered available.
  */
  public Integer [] getIncludeInventoryStates()
  {
    return mIncludeInventoryStates;
  }

  protected InventoryManager mInventoryManager;

  /**
  * Sets the InventoryManager
  */
  public void setInventoryManager(InventoryManager pInventoryManager)
  {
    mInventoryManager = pInventoryManager;
  }
  /**
  * Returns the InventoryMananager
  * @beaninfo description: The InventoryManager
  */
  public InventoryManager getInventoryManager()
  {
    return mInventoryManager;
  }

  protected CatalogTools mCatalogTools;

  /**
  * Sets the CatalogTools
  */
  public void setCatalogTools(CatalogTools pCatalogTools)
  {
    mCatalogTools = pCatalogTools;
  }
  /**
  * Returns the CatalogTools
  * @beaninfo description: The CatalogTools
  */
  public CatalogTools getCatalogTools()
  {
    return mCatalogTools;
  }

  protected CatalogServices mCatalogServices;

  /**
  * Sets the CatalogServices
  */
  public void setCatalogServices(CatalogServices pCatalogServices)
  {
    mCatalogServices = pCatalogServices;
  }
  /**
  * Returns the CatalogServices
  * @beaninfo description: The CatalogServices
  */
  public CatalogServices getCatalogServices()
  {
    return mCatalogServices;
  }

  /**
  * @see atg.service.collections.filter.CachedCollectionFilter#shouldApplyFilter
  * @return true by default
  */
  public boolean shouldApplyFilter(Collection pUnfilteredCollection, String pKey,
                                    RepositoryItem pProfile)
  {
    return true;
  }

  /**
  * This filter does not produce a context key.
  * @return null. This implementation of the InventoryFilter does not use a context
  * key. 
  */
  public Object generateContextKey(Collection pUnfilteredCollection, String pCollectionIdentifierKey,
                                      RepositoryItem pProfile)
  {
    return null;
  }

  /**
  * Generates a filtered collection based on Inventory availabilty. 
  * <p>
  * A product is included in the filtered collection if it does not contain any
  * skus, or any of its skus is considered available.
  * <p>
  * The availabilty state of the sku must be one of
  * the preconfigured include states.
  * <p>
  * If the new filtered collection is the same size as the unfiltered collection,
  * the filtered collection is disgarded and the unfiltered collection is returned.
  * 
  * @param pUnfilteredCollection the unfiltered collection
  * @param pCollectionIdentifierKey the key provided by the caller.
  * @return the filtered collection
  */
  protected Collection generateFilteredCollection(Collection pUnfilteredCollection,
                                                String pCollectionIdentifierKey,
                                                RepositoryItem pProfile)
    throws FilterException
  {


    String skuPropertyName = getCatalogServices().getProductSKUPropertyName();
    InventoryManager im = getInventoryManager();

    Collection returnCollection = generateNewCollectionObject(pUnfilteredCollection);

    try
    {
      CatalogTools catalogTools = getCatalogTools();

      //get the inventory states that mean a sku is available and create
      //a list for easy lookup.
      Integer [] includeInventoryStates = getIncludeInventoryStates();
      List availableStates;
      if(includeInventoryStates == null || includeInventoryStates.length == 0)
        availableStates = new ArrayList();
      else
        availableStates = Arrays.asList(includeInventoryStates);

      if(isLoggingDebug())
        logDebug("IncludeInventoryStates are: " + availableStates);

      Iterator unfilterator = pUnfilteredCollection.iterator();
      while(unfilterator.hasNext())
      {
        Object nextObject = unfilterator.next();

        if(!catalogTools.isProduct(nextObject))
        {
          //item in not a product
          //add it to the return collection and continue.
          returnCollection.add(nextObject);
          continue;
        }

        RepositoryItem nextItem = (RepositoryItem) nextObject;

        if(isLoggingDebug())
         logDebug("generateFilteredCollection: nextItem is " + nextItem);

        //for each product, check avaialabilty of all its skus. if any sku is
        //considered available, the product is returned in the filtered collection
        Collection skus = (Collection) nextItem.getPropertyValue(skuPropertyName);

        //a product with no skus passes!
        if(skus == null || skus.size() == 0)
        {
          //add it to the return collection and continue.
          returnCollection.add(nextObject);
        }
        else
        {
          Iterator skuerator = skus.iterator();
          while(skuerator.hasNext())
          {
            RepositoryItem sku = (RepositoryItem) skuerator.next();
            if(isLoggingDebug())
              logDebug("generateFilteredCollection: checking sku availability " + sku);

            try
            {
              int availabiltyStatus =  im.queryAvailabilityStatus(sku.getRepositoryId());

              if(isLoggingDebug())
                logDebug("generateFilteredCollection: checking sku availability " + sku + " status is " + availabiltyStatus);

              //if the sku is available, add the product to the filtered collection
              if(availableStates.contains(Integer.valueOf(availabiltyStatus)))
              {
                returnCollection.add(nextItem);
                if(isLoggingDebug())
                  logDebug("generateFilteredCollection: add item to return collection " + nextItem);
                break;
              }
            }
            catch(atg.commerce.inventory.MissingInventoryItemException exc)
            {
              //skus that aren't in the inventory will NOT be filtered out
              if(isLoggingWarning())
                logWarning(exc);
              returnCollection.add(nextItem);
              if(isLoggingDebug())
                logDebug("generateFilteredCollection: add item to return collection " + nextItem);
              break;
            }
          }//end while skus
        }//end has skus
      }//end while products

      //if we didn't filter anything out, just return
      //the orig. collection. this will optimize the cleanup of the new collection
      //object.
      if(returnCollection.size() == pUnfilteredCollection.size())
        return pUnfilteredCollection;
      else
        return returnCollection;
    }
    catch(RepositoryException exc)
    {
      throw new FilterException(exc);
    }
    catch(InventoryException exc)
    {
      throw new FilterException(exc);
    }
    catch(ClassCastException exc)
    {
      throw new FilterException(exc);
    }
  }


}

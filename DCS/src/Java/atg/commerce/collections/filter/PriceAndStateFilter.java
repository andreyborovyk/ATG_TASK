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
import atg.commerce.catalog.CatalogServices;
import atg.commerce.catalog.CatalogTools;
import atg.beans.*;
/**
 * This filter demonstrates how someone might implement a filter based on
 * profile attributes.
 * <p>
 * The objective of this filter is to create a filtered collection of products
 * for users with a registered home state of X, that includes only the products
 * with a price less than or equal to Y.  X and Y are configurable values of the filter.
 * <p>
 * This filter assumes that a standard catalog configuration is being used which has
 * the list price maintained as a property of the sku item. A product qualifies
 * for the filtered collection if any of its skus pass the price test, or if the
 * product does not have a sku.
 *
 * @author Jim Fecteau
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/collections/filter/PriceAndStateFilter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class PriceAndStateFilter extends CachedCollectionFilter

{

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/collections/filter/PriceAndStateFilter.java#2 $$Change: 651448 $";

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

  protected String mListPricePropertyName;

  /**
  * Sets the property name used to store the list price on the sku
  */
  public void setListPricePropertyName(String pListPricePropertyName)
  {
    mListPricePropertyName = pListPricePropertyName;
  }
  /**
  * Returns the property name used to store the list price on the sku
  * @beaninfo description: The property name used to store the list price on the sku
  */
  public String getListPricePropertyName()
  {
    return mListPricePropertyName;
  }


  protected String mStatePropertyName;
  /**
  * Sets the property name used to get the user's home state
  */
  public void setStatePropertyName(String pStatePropertyName)
  {
    mStatePropertyName = pStatePropertyName;
  }
  /**
  * Returns the property name used to get the user's home state
  * @beaninfo description: The property name used to get the user's home state
  */
  public String getStatePropertyName()
  {
    return mStatePropertyName;
  }


  protected String mState;
  /**
  * Sets the home state that applies to this filter
  */
  public void setState(String pState)
  {
    mState = pState;
  }
  /**
  * Returns the home state that applies to this filter
  * @beaninfo description: The home state that applies to this filter
  */
  public String getState()
  {
    return mState;
  }

  protected Double mPrice;
  /**
  * Sets the threshold price
  */
  public void setPrice(Double pPrice)
  {
    mPrice = pPrice;
  }
  /**
  * Returns the threshold price
  * @beaninfo description: The threshold price
  */
  public Double getPrice()
  {
    return mPrice;
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

  /**
  * Generates a context key for this filter.
  * The configured home state is the appropriate
  * context key because this filter creates filtered results specific to a user that is
  * registered in the configured home state. Therefore, having the state as the context
  * key will enable all subsequent executions of the filter, for users in the same home state,
  * to correctly identify results in the cache.
  * <p>
  * @param pUnfilteredCollection the collection to be filtered
  * @param pCollectionIdentifierKey the key that uniquely identifies the unfiltered collection. 
  * @return the filter's configured home state.
  */
  public Object generateContextKey(Collection pUnfilteredCollection, String pCollectionIdentifierKey,
                                      RepositoryItem pProfile)
  {
    return getState();
  }


  /**
  * If the filter is executed for a user that is not registered in the same
  * home state as the filter's configured home state, then this method
  * will return false. In other words, the filter does not apply
  * in that context. It only applies when the current user, represented by
  * the pProfile parameter, is registered in the same home state as the
  * filter's configured home state.
  *
  * @see atg.service.collections.filter.CachedCollectionFilter#shouldApplyFilter
  */
  public boolean shouldApplyFilter(Collection pUnfilteredCollection, String pKey,
                                    RepositoryItem pProfile)
  {

    //check user's registered home state
    String state = null;
    try
    {
      state = (String) DynamicBeans.getSubPropertyValue(pProfile, getStatePropertyName());
      if(state == null || !state.equals(getState()))
        return false;
    }
    catch(PropertyNotFoundException exc)
    {
      if(isLoggingError())
        logError( exc);
      return false;
    }
    return true;
  }

  /**
  * Generates a filtered collection based on the registered home state of the user and
  * the price of each product in the collection.
  * <p>
  * If the new filtered collection is the same size as the unfiltered collection,
  * the filtered collection is disgarded and the unfiltered collection is returned.
  * 
  * @param pUnfilteredCollection the unfiltered collection
  * @param pCollectionIdentifierKey the key the uniquely identifies the unfiltered collection. 
  * @return the filtered collection
  */
  protected Collection generateFilteredCollection(Collection pUnfilteredCollection,
                                                String pCollectionIdentifierKey,
                                                RepositoryItem pProfile)
    throws FilterException
  {
    Collection returnCollection = generateNewCollectionObject(pUnfilteredCollection);

    Iterator unfilterator = pUnfilteredCollection.iterator();

    Double thresholdPrice = getPrice();
    try
    {
      CatalogTools catalogTools = getCatalogTools();

      //loop through the products and check the price on each sku
      while(unfilterator.hasNext())
      {

        boolean includeProduct = true;

        Object nextObject = unfilterator.next();

        if(!catalogTools.isProduct(nextObject))
        {
          //item is not a product
          //add it the returned collection and continue.
          returnCollection.add(nextObject);
          continue;
        }

        RepositoryItem nextItem = (RepositoryItem) nextObject;

        if(isLoggingDebug())
         logDebug("generateFilteredCollection: next product is " + nextItem);
        //check the price of each sku. if any of them are GT the configured price,
        //exclude the product from the filtered results.
        Collection skus = (Collection) nextItem.getPropertyValue(getCatalogServices().getProductSKUPropertyName());
        //a product with no skus passes!
        if(skus == null || skus.size() == 0)
        {
          includeProduct = true;
        }
        else
        {
          Iterator skuerator = skus.iterator();
          while(skuerator.hasNext())
          {
            RepositoryItem sku = (RepositoryItem) skuerator.next();

            if(isLoggingDebug())
              logDebug("generateFilteredCollection: checking sku price " + sku);

            Double skuPrice = (Double) sku.getPropertyValue(getListPricePropertyName());

            if(isLoggingDebug())
              logDebug("generateFilteredCollection: sku price is " + skuPrice);

            if(skuPrice.compareTo(thresholdPrice) > 0 )
            {
              if(isLoggingDebug())
                logDebug("generateFilteredCollection: excluding product " + nextItem);
              includeProduct = false;
              break;
            }
          }//end while skus
        }//end if skus
        if(includeProduct)
          returnCollection.add(nextItem);
      }//end while products

      //if we didn't filter anything out, just return
      //the orig. collection. this will optimize the cleanup of the new collection
      //object.
      if(returnCollection.size() == pUnfilteredCollection.size())
        return pUnfilteredCollection;
      else
        return returnCollection;

    }//end try
    catch(RepositoryException exc)
    {
      throw new FilterException(exc);
    }
  }
}

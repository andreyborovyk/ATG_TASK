/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.projects.store.catalog;

import atg.commerce.catalog.custom.CustomCatalogTools;

import atg.core.util.StringUtils;

import atg.nucleus.ServiceException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import java.util.*;


/**
 * The extensions to ootb CatalogTools.
 * At the time of its writing, all it contained were the
 * ids of the Store catalog and the corresponding
 * repository items.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/StoreCatalogTools.java#2 $
 */
public class StoreCatalogTools extends CustomCatalogTools {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/StoreCatalogTools.java#2 $$Change: 651448 $";

  /**
   * Root navigation category id.
   */
  private String mRootNavigationCategoryId = "rootCategory";

  /**
   * Size sort order.
   */
  String[] mSizeSortOrder;

  // ------------------------------------------------------------------------------
  /**
   * This field contains pairs of objects, where a key is the name of the property,
   * and the value is a key for the localized string. E.g.:
   * <pre>
   *  color=browse_productComparisons.colors,\
   *  size=browse_productComparisons.sizes,\
   * </pre>
   * Note, that the field is declared as {@link LinkedHashMap} in order to keep pairs
   * in the same order as they are specified in <code>.properties</code> file.
   */
  private LinkedHashMap propertyToLabelMap;

  /**
   * Setter-method for {@link #propertyToLabelMap} field.
   * @param pPropertyToLabelMap Map containing Property-to-Label pairs
   */
  public void setPropertyToLabelMap(LinkedHashMap pPropertyToLabelMap) {
    propertyToLabelMap = pPropertyToLabelMap;
  }

  /**
   * Getter-method for {@link #propertyToLabelMap} field.
   * @return pPropertyToLabelMap Map containing Property-to-Label pairs
   */
  public LinkedHashMap getPropertyToLabelMap(){
    return propertyToLabelMap;
  }
  // ------------------------------------------------------------------------------

  /**
   * This field contains pairs of objects, where a key is the name of the property,
   * and the value is a name of filter bean. E.g.:
   * <pre>
   * color=/atg/store/collections/filter/ColorSorter,\
   * size=/atg/store/collections/filter/SizeSorter,\
   * </pre>
   */
  private Map propertyToFilterMap;

  /**
   * Setter-method for {@link #propertyToFilterMap} field.
   * @param pPropertyToLabelMap Map containing Property-to-Filter pairs
   */
  public void setPropertyToFilterMap(Map pPropertyToFilterMap) {
    propertyToFilterMap = pPropertyToFilterMap;
  }

  /**
   * Getter-method for {@link #propertyToFilterMap} field.
   * @param pPropertyToLabelMap Map containing Property-to-Filter pairs
   */
  public Map getPropertyToFilterMap(){
    return propertyToFilterMap;
  }
  // ------------------------------------------------------------------------------
  /**
   * @return RootNavigationCategoryId.
   */
  public String getRootNavigationCategoryId() {
    return mRootNavigationCategoryId;
  }

  /**
   * @param pRootNavigationCategoryId - new RootNavigationCategoryId.
   */
  public void setRootNavigationCategoryId(String pRootNavigationCategoryId) {
    mRootNavigationCategoryId = pRootNavigationCategoryId;
  }

  /**
   * @return Returns the SizeSortOrder used for displaying sizes.
   */
  public String[] getSizeSortOrder() {
    return mSizeSortOrder;
  }

  /**
   * @param pSizeSortOrder The SizeSortOrder used for displaying sizes.
   */
  public void setSizeSortOrder(String[] pSizeSortOrder) {
    mSizeSortOrder = pSizeSortOrder;
  }

  /**
   * Get the catalog with the given id.
   * @return the catalog with the given id
   * @param pCatalogId - catalog id
   * @throws atg.repository.RepositoryException if error occurs
   */
  public RepositoryItem getCatalog(String pCatalogId) throws RepositoryException {
    Repository catalog = getCatalog();
    RepositoryItem catalogItem = catalog.getItem(pCatalogId, getBaseCatalogItemType());

    if (null == catalogItem) {
      if (isLoggingError()) {
        logError("The Store catalog id " + pCatalogId + " is not valid.");
      }
    }

    return catalogItem;
  }

  /**
   * Returns a list of possible colors for a collection of skus.
   * @param pSkus collection of skus
   * @return a list of possible colors
   */
  public List getPossibleColors(Collection pSkus) {
    StoreCatalogProperties catalogProps = (StoreCatalogProperties) getCatalogProperties();

    return getPossibleValuesForSkus(pSkus, catalogProps.getColorPropertyName());
  }

  /**
   * Creates a map of color names to swatch items.
   *
   * @param pSkus - sku collection
   * @return Map of color names to swatch media items
   */
  public Map getPossibleColorSwatches(Collection pSkus) {
    StoreCatalogProperties catalogProps = (StoreCatalogProperties) getCatalogProperties();
    Map values = new HashMap();

    if (pSkus != null && pSkus.size() > 0) {
      Iterator childSkuerator = pSkus.iterator();
      RepositoryItem childSku;
      RepositoryItem swatch;

      while (childSkuerator.hasNext()) {
        childSku = (RepositoryItem) childSkuerator.next();

        String color = (String) childSku.getPropertyValue(catalogProps.getColorPropertyName());

        if (StringUtils.isBlank(color)) {
          if (isLoggingDebug()) {
            logDebug("sku" + childSku.getRepositoryId() + " has a missing color specification");
          }
        } else {
          if (!values.containsKey(color)) {
            //get the swatch
            swatch = (RepositoryItem) childSku.getPropertyValue(catalogProps.getColorSwatchName());

            if (swatch != null) {
              values.put(color, swatch);
            }
          }
        }
      }
    }

    return values;
  }

  /**
   * Creates a map of possible color swatches mapped by SKU's woodFinish
   * Valid for furniture-sku only!
   * @param pSkus
   * @return
   */
  public Map<String, RepositoryItem> getPossibleWoodColorSwatches(Collection<RepositoryItem> pSkus)
  {
    Map<String, RepositoryItem> result = new HashMap<String, RepositoryItem>();
    if (pSkus == null || pSkus.size() == 0)
    {
      return result;
    }
    StoreCatalogProperties catalogProps = (StoreCatalogProperties) getCatalogProperties();
    for (RepositoryItem sku: pSkus)
    {
      String woodFinish = (String) sku.getPropertyValue(catalogProps.getWoodFinishPropertyName());
      if (!StringUtils.isBlank(woodFinish) && !result.containsKey(woodFinish))
      {
        RepositoryItem swatch = (RepositoryItem) sku.getPropertyValue(catalogProps.getColorSwatchName());
        if (swatch != null)
        {
          result.put(woodFinish, swatch);
        }
      }
    }
    return result;
  }

  /**
   * Returns the list of possible sizes for a given collection of skus.
   *
   * @param pSkus collection of skus
   * @return list of colors
   */
  public List getPossibleSizes(Collection pSkus) {
    StoreCatalogProperties catalogProps = (StoreCatalogProperties) getCatalogProperties();

    return getPossibleValuesForSkus(pSkus, catalogProps.getSizePropertyName());
  }

  /**
   * Obtains a list of sizes used by SKUs specified. This list will be sorted in correspondence to the {@link #getSizeSortOrder()} property.
   * @see #getSizeSortOrder()
   * @param pSkus collection of SKUs sizes should be taken from.
   * @return a List<String> of sizes.
   */
  @SuppressWarnings("unchecked")
  public List<String> getSortedSizes(Collection<RepositoryItem> pSkus)
  {
    return sortSizes(getPossibleSizes(pSkus));
  }

  /**
   * Returns the list of possible values for a given collection of skus.
   *
   * @param pSkus collection of skus
   * @param pPropertyName the property name of the sku to use.
   * @return Collection of values
   */
  public List getPossibleValuesForSkus(Collection pSkus, String pPropertyName) {
    List values = new ArrayList();

    if (pSkus != null && pSkus.size() > 0) {
      Iterator skuerator = pSkus.iterator();
      RepositoryItem sku;

      while (skuerator.hasNext()) {
        sku = (RepositoryItem) skuerator.next();

        Object value = sku.getPropertyValue(pPropertyName);

        if ((value == null) || (value instanceof String && StringUtils.isBlank((String) value))) {
          if (isLoggingDebug()) {
            logDebug("sku" + sku.getRepositoryId() + " has a missing " + pPropertyName + " specification");
          }
        } else if (!values.contains(value)) {
          values.add(value);
        }
      }
    }

    return values;
  }

  /**
   * Finds the sku with the matching color and size.
   * @param pSkus collection of skus
   * @param pColor - color name
   * @param pSize - size name
   * @return the matching child sku
   */
  public RepositoryItem findSku(Collection pSkus, String pColor, String pSize) {
    if ((pColor == null) || (pSize == null)) {
      return null;
    }

    StoreCatalogProperties catalogProps = (StoreCatalogProperties) getCatalogProperties();
    String sizePropertyName = catalogProps.getSizePropertyName();
    String colorPropertyName = catalogProps.getColorPropertyName();

    if (pSkus != null && pSkus.size() > 0) {
      Iterator childSkuerator = pSkus.iterator();
      RepositoryItem childSku;
      String size;
      String color;

      while (childSkuerator.hasNext()) {
        childSku = (RepositoryItem) childSkuerator.next();
        size = (String) childSku.getPropertyValue(sizePropertyName);
        color = (String) childSku.getPropertyValue(colorPropertyName);

        if ((size != null) && (color != null) && size.equals(pSize) && color.equals(pColor)) {
          return childSku;
        }
      }
    }

    return null;
  }

  /**
   * Finds the sku with the matching wood finish.
   * @param pSkus collection of skus
   * @param pWoodFinish - wood finish name
   * @return the matching child sku
   */
  public RepositoryItem findSku(Collection pSkus, String pWoodFinish) {
    if ((pWoodFinish == null)) {
      return null;
    }

    StoreCatalogProperties catalogProps = (StoreCatalogProperties) getCatalogProperties();
    String woodFinishPropertyName = catalogProps.getWoodFinishPropertyName();

    if (pSkus != null && pSkus.size() > 0) {
      Iterator childSkuerator = pSkus.iterator();
      RepositoryItem childSku;
      String woodFinish;

      while (childSkuerator.hasNext()) {
        childSku = (RepositoryItem) childSkuerator.next();
        woodFinish = (String) childSku.getPropertyValue(woodFinishPropertyName);

        if (pWoodFinish.equals(woodFinish)) {
          return childSku;
        }
      }
    }

    return null;
  }

  /**
   * Returns a product's child skus.
   * @param pProduct - product
   * @return Collection of skus
   */
  public Collection getProductChildSkus(RepositoryItem pProduct) {
    return (Collection) pProduct.getPropertyValue(getCatalogProperties().getChildSkusPropertyName());
  }

  /**
   * Sorts a list of colors.
   * @param pColors - list of pColors
   * @return the sorted list of colors.
   */
  public List sortColors(List pColors) {
    ArrayList sorted = new ArrayList(pColors.size());
    sorted.addAll(pColors);
    Collections.sort(sorted);

    return sorted;
  }

  /**
   * Sorts a list of wood finishes.
   * @param pWoodFinishes - list of wood finishes
   * @return the sorted list of wood finishes.
   */
  public List sortWoodFinishes(List pWoodFinishes) {
    ArrayList sorted = new ArrayList(pWoodFinishes.size());
    sorted.addAll(pWoodFinishes);
    Collections.sort(sorted);

    return sorted;
  }
  /**
   * Sorts sizes accoring to the configured template.
   * @see #getSizeSortOrder()
   * @param pSizes - list of sizes
   * @return List of sorted sizes
   */
  public List sortSizes(List pSizes) {
    String[] sortOrder = getSizeSortOrder();

    if ((sortOrder != null) && (sortOrder.length > 0)) {
      return sortStrings(pSizes, getSizeSortOrder());
    } else {
      return pSizes;
    }
  }

  /**
   * Sorts the collection of strings relative to their ordinal position in the sort order array.
   * <p>
   * @param pStrings a collection of strings to sort
   * @param pSortOrder the string array of possible values and their relative order.
   * @return List of sorted Strings
   */
  public List sortStrings(List pStrings, String[] pSortOrder) {
    int size = pStrings.size();
    List sortedStrings = new ArrayList(size);
    int sortStringIndex = 0;

    for (int i = 0; (i < pSortOrder.length) && (sortStringIndex < size); i++)
      if (pStrings.contains(pSortOrder[i])) {
        sortedStrings.add(sortStringIndex++, pSortOrder[i]);
        pStrings.remove(pSortOrder[i]);
      }

    // Add any sizes that weren't in the sort order at the end
    sortedStrings.addAll(pStrings);

    return sortedStrings;
  }

}

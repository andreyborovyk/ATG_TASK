/*<ATGCOPYRIGHT>
 * Copyright (C) 2010-2011 Art Technology Group, Inc.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;

/**
 * StoreCatalogTools implementation with Internationalization support. 
 * @see StoreCatalogTools
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class InternationalizedStoreCatalogTools extends StoreCatalogTools
{
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 651448 $";

  private SizesComparator mSizesComparator;
  
  @Override
  public List<String> getSortedSizes(Collection<RepositoryItem> pSkus)
  {
    if (pSkus == null)
    {
      return Collections.emptyList();
    }
    
    InternationalizedStoreCatalogProperties catalogProperties = (InternationalizedStoreCatalogProperties) getCatalogProperties();
    String sizePropertyName = catalogProperties.getSizePropertyName();
    String defaultSizePropertyName = catalogProperties.getDefaultSizePropertyName();

    Map<String, String> sizesByDefaultSize = new HashMap<String, String>(); //Contains 'Default Size -> Size' mapping
    for (RepositoryItem sku: pSkus)
    {
      String size = (String) sku.getPropertyValue(sizePropertyName);
      String defaultSize = (String) sku.getPropertyValue(defaultSizePropertyName);
      if (!StringUtils.isEmpty(defaultSize) && !sizesByDefaultSize.containsKey(defaultSize))
      {
        sizesByDefaultSize.put(defaultSize, size);
      }
    }
    
    List<String> sortedDefaultSizes = new ArrayList<String>(sizesByDefaultSize.keySet()); 
    Collections.sort(sortedDefaultSizes, mSizesComparator);
    
    List<String> result = new LinkedList<String>();
    for (String defaultSize: sortedDefaultSizes)
    {
      result.add(sizesByDefaultSize.get(defaultSize));
    }
    
    return result;
  }
  
  @Override
  public void doStartService() throws ServiceException
  {
    super.doStartService();
    mSizesComparator = new SizesComparator();
  }

  /**
   * A {@link Comparator} implementation for sorting clothing-sku sizes.
   * @author ATG
   * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 651448 $ 
   * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
   */
  private class SizesComparator implements Comparator<String>
  {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 651448 $";

    private Map<String, Integer> mSortOrder;
    
    /**
     * Creates a new instance of SizesComparator.
     * This instance reads the {@link StoreCatalogTools#getSizeSortOrder()} property and compares all passed objects
     * accordingly to this defined sort order.
     * Note that this property lists all possible default size names in ascending order.
     */
    private SizesComparator()
    {
      mSortOrder = new HashMap<String, Integer>();
      String[] sizeSortOrder = getSizeSortOrder();
      if (sizeSortOrder != null)
      {
        for (int i = 0; i < sizeSortOrder.length; i++)
        {
          mSortOrder.put(sizeSortOrder[i], i);
        }
      }
    }
    
    /**
     * This implementation of {@link Comparator#compare(Object, Object)} method is relied on the size sort order specified.
     * If any of the parameters specified is not listed in the sort order, there will be thrown an {@link IllegalArgumentException}.
     * @param o1 - first size to be compared.
     * @param o2 - second size to be compared.
     * @see SizesComparator#SizesComparator(String[])
     * @throws IllegalArgumentException if some parameter is not listed in the sizes sort order array.
     */
    public int compare(String o1, String o2)
    {
      Integer order1 = mSortOrder.get(o1);
      Integer order2 = mSortOrder.get(o2);
      if (order1 == null || order2 == null)
      {
        throw new IllegalArgumentException("Only default size values should be compared. Unknown size: " + (order1 == null ? order1 : order2));
      }
      return order1 - order2;
    }
  }
}

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

package atg.commerce.inventory;

// atg classes
import atg.repository.*;

// java classes
import java.util.Comparator;

/**
 * Used to compare sku links.  Returns a comparison of the Ids.
 * Used by RepositoryInventoryManager
 *
 * @see RepositoryInventoryManager
 *
 * Created: Tue Mar 14 10:41:59 2000
 *
 * @author Sam Perman
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/SkuLinkComparator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SkuLinkComparator implements Comparator {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/SkuLinkComparator.java#2 $$Change: 651448 $";

  
  public int compare(Object pSkuLink1, Object pSkuLink2)
  {
    RepositoryItem s1 = (RepositoryItem) pSkuLink1;
    RepositoryItem s2 = (RepositoryItem) pSkuLink2;
    
    RepositoryItem id1 = (RepositoryItem)s1.getPropertyValue("item");
    RepositoryItem id2 = (RepositoryItem)s2.getPropertyValue("item");

    if((id1 == null) || (id2 == null))
      throw new ClassCastException("Could not find item property in bundle link.");

    return id1.getRepositoryId().compareTo(id2.getRepositoryId());
  }
  
} // SkuLinkComparator

/*<ATGCOPYRIGHT>
 * Copyright (C) 2010 Art Technology Group, Inc.
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

/**
 * StoreCatalogProperties implementation with Internationalization support.
 * @see StoreCatalogProperties
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogProperties.java#3 $$Change: 635816 $ 
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class InternationalizedStoreCatalogProperties extends StoreCatalogProperties
{
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogProperties.java#3 $$Change: 635816 $";

  private String mDefaultSizePropertyName = "sizeDefault";
  
  public String getDefaultSizePropertyName()
  {
    return mDefaultSizePropertyName;
  }
  
  public void setDefaultSizePropertyName(String pDefaultSizePropertyName)
  {
    mDefaultSizePropertyName = pDefaultSizePropertyName;
  }
}

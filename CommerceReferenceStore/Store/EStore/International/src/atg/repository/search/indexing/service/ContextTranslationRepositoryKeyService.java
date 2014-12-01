/*<ATGCOPYRIGHT>
* Copyright (C) 2005-2011 Art Technology Group, Inc.
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


package atg.repository.search.indexing.service;

//import atg.repository.TranslationDerivation;


import atg.repository.dp.RepositoryKeyService;
import atg.repository.search.indexing.Context;

/**
 * this class is use to get the correct locale from the context object
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/repository/search/indexing/service/ContextTranslationRepositoryKeyService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ContextTranslationRepositoryKeyService implements RepositoryKeyService {

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/repository/search/indexing/service/ContextTranslationRepositoryKeyService.java#2 $$Change: 651448 $";
  
  Context mContext = null;

  /**
   * Returns the current locale from the context object
   * @return Object
   */

  public Object getRepositoryKey() {
    if (mContext == null) return null;
    Object locale = mContext.getCurrentDocumentLocale();
    return locale;
  }

  /**
   * @param pContext Context object to set the current locale
   */
  public void setContext(Context pContext) {
    mContext = pContext;
  }
}

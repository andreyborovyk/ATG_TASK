/*<ATGCOPYRIGHT>
* Copyright (C) 2005-2010 Art Technology Group, Inc.
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
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/repository/search/indexing/service/ContextTranslationRepositoryKeyService.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */

public class ContextTranslationRepositoryKeyService implements RepositoryKeyService {

  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/repository/search/indexing/service/ContextTranslationRepositoryKeyService.java#3 $$Change: 635816 $";
  
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

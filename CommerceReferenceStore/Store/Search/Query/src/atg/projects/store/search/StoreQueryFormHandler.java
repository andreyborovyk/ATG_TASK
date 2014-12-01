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

package atg.projects.store.search;

import java.io.IOException;

import atg.search.formhandlers.QueryFormHandler;
import atg.servlet.DynamoHttpServletRequest;

/**
 * CRS extention of search form handler.
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Search/Query/src/atg/projects/store/search/StoreQueryFormHandler.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreQueryFormHandler extends QueryFormHandler {
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Search/Query/src/atg/projects/store/search/StoreQueryFormHandler.java#2 $$Change: 651448 $";

  @Override
  protected boolean doRedirect(DynamoHttpServletRequest pRequest, String pUrl) throws IOException {
    // This method is doing a redirect to URL got from some redirect processor;
    // do not redirect at the end of 'search' method, because it will override current redirect
    setSuccessURL(null);
    return super.doRedirect(pRequest, pUrl);
  }
}

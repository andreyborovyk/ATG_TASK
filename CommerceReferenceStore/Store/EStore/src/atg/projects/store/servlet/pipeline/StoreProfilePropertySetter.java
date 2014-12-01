/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.servlet.pipeline;


import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfilePropertySetter;
import java.io.IOException;
import javax.servlet.ServletException;


/**
 * <p>Update the store profile properties based on values for the current site.
 *
 * @author dwilson
 *
 */
public class StoreProfilePropertySetter extends ProfilePropertySetter {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/servlet/pipeline/StoreProfilePropertySetter.java#2 $$Change: 651448 $";


  /**
   * Update the store profile properties based on values for the current site.
   *
   */
  public boolean setProperties(Profile pProfile,
                               DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
      throws IOException, ServletException, RepositoryException {
    // Resolve StoreRequestLocale created by DynamoHandler. userPrefLang cookie is written to browser.
    pRequest.getRequestLocale();

    return true;
  }
}


/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.projects.store.profile;

import atg.repository.*;
import atg.userprofiling.RepositoryProfileItemFinder;

/**
 * Finds profile repository items by querying the profile repository.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/profile/StoreRepositoryProfileItemFinder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class StoreRepositoryProfileItemFinder extends RepositoryProfileItemFinder {

  //-------------------------------------
  // Constants.
  //-------------------------------------

  /** Class version string. **/
  public static String CLASS_VERSION = 
  "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/profile/StoreRepositoryProfileItemFinder.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables.
  //-------------------------------------



  //-------------------------------------
  // Methods.
  //------------------------------------- 

  /**
   * Returns the Query which will be executed to find the profiles
   * with the given email address.  The query returned will be of the
   * form <code>(email EQUALS IGNORECASE pEmail)</code>.
   * {@inheritDoc}
   * @exception RepositoryException if there is a problem constructing
   * the query
   **/
  public Query generateEmailQuery(String pEmail, QueryBuilder pQueryBuilder)
    throws RepositoryException
  {
    String emailProperty = getEmailPropertyName();
    QueryExpression qe1 = pQueryBuilder.createPropertyQueryExpression(emailProperty);
    QueryExpression qe2 = pQueryBuilder.createConstantQueryExpression(pEmail);
    return pQueryBuilder.createPatternMatchQuery(qe1, qe2, QueryBuilder.EQUALS, true);
  }

}

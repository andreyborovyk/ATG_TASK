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

package atg.servlet.pipeline;

import java.util.*;

/**
 *
 * <p>This implementation of Authenticator takes a single property,
 * passwords.  This property is a plaintext mapping from id to
 * password.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/BasicAuthenticator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: An authenticator that maps plaintext IDs to passwords
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Pipeline
 *   attribute: icon /atg/ui/common/images/pipelinecomp.gif
 **/

public
class BasicAuthenticator
implements Authenticator
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/BasicAuthenticator.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties

  /** The mapping from user id to password */
  Properties mPasswords;

  /** An optional string to use to identify this authenticator */
  String mRealm;

  //-------------------------------------
  /**
   *
   * Constructs a new BasicAuthenticator
   **/
  public BasicAuthenticator ()
  {
  }

  //-------------------------------------
  /**
   *
   * Returns true if the specified userid and password are an
   * authentic combination, false if not.
   **/
  public boolean authenticate (String pUserId,
                               String pPassword)
  {
    if (mPasswords == null) return false;

    String usersPassword = mPasswords.getProperty(pUserId);
    return usersPassword != null && usersPassword.equals(pPassword);
  }

  //-------------------------------------
  // Properties
  //-------------------------------------
  /**
   * Returns the mapping from user id to password
   * @beaninfo
   *   description: Mapping of user IDs to passwords
   **/
  public Properties getPasswords ()
  {
    return mPasswords;
  }

  //-------------------------------------
  /**
   * Sets the mapping from user id to password
   **/
  public void setPasswords (Properties pPasswords)
  {
    mPasswords = pPasswords;
  }

  /**
   * Returns the name of the realm to be displayed to the user for this
   * authenticator.
   * @beaninfo
   *   description: The realm of this authenticator
   **/
  public String getRealm ()
  {
    return mRealm;
  }

  //-------------------------------------
  /**
   * Sets the name of the realm to be displayed to the user for this
   * authenticator.
   **/
  public void setRealm (String pRealm)
  {
    mRealm = pRealm;
  }
}

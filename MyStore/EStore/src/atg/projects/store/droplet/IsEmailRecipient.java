/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.profile.StoreProfileTools;

import atg.repository.MutableRepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userprofiling.ProfileTools;

import java.io.IOException;

import javax.servlet.ServletException;


/**
* <p>
* Given an emailAddress, this droplet will call into
* StoreProfileTools.retrieveEmailRecipient(). If a valid item is
* returned, the droplet renders a "true" oparam, otherwise, "false".
* <p>
* This droplet takes the following input parameters:
* <ul>
* <li>email - The email address of the current Profile
* </ul>
* <p>
* This droplet renders the following oparams:
* <ul>
* <li>true - if current Profile has already subscribed to receive emails
* <li>false - if current Profile has not subscribed to receive emails
* </ul>
* <p>
* Example:
*
* <PRE>
*
* &lt;dsp:droplet bean="/atg/store/droplet/IsEmailRecipient"&gt; &lt;dsp:param
* name="email" bean="RegistrationFormHandler.value.email"&gt; &lt;dsp:oparam name="true"&gt;
* * &lt;/dsp:oparam&gt;  &lt;dsp:oparam name="false"&gt; &lt;/dsp:oparam&gt;&lt;/dsp:droplet&gt;
*
* </PRE>
*
* @author ATG
* @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/IsEmailRecipient.java#3 $$Change: 635816 $
*
*/
public class IsEmailRecipient extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/IsEmailRecipient.java#3 $$Change: 635816 $";



  /** The input parameter name for the email address to check. */
  public static final ParameterName PARAM_EMAIL = ParameterName.getParameterName("email");

  /** The oparam name rendered once if current Profile has already subscribed to receive emails.*/
  public static final ParameterName OPARAM_OUTPUT_TRUE = ParameterName.getParameterName("true");

  /** The oparam name rendered once if current Profile has not subscribed to receive emails.*/
  public static final ParameterName OPARAM_OUTPUT_FALSE = ParameterName.getParameterName("false");

  /**
   * Profile tools.
   */
  protected ProfileTools mProfileTools;

  /**
   * @return the profileTools.
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * @param pProfileTools - The profileTools to set.
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * Given an email address, this will call into
   * StoreProfileTools.retrieveEmailRecipient() to determine
   * if the address is already subscribed to receive emails or not.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object email = pRequest.getObjectParameter(PARAM_EMAIL);

    if ((email == null) || !(email instanceof String)) {
      if (isLoggingDebug()) {
        logDebug("INVALID PARAM: invalid or no email address supplied");
      }

      return;
    }

    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    MutableRepositoryItem emailRecipient = (MutableRepositoryItem) profileTools.retrieveEmailRecipient((String) (email));

    if (emailRecipient != null) {
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_TRUE, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_FALSE, pRequest, pResponse);
    }
  } 
}

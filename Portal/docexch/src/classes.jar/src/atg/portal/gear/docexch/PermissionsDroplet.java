/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.portal.gear.docexch;

import atg.portal.framework.*;
import atg.servlet.*;
import atg.droplet.DropletException;
import java.io.IOException;
import javax.servlet.ServletException;
import atg.nucleus.naming.ParameterName;


/**
 * This servlet uses the gear environment to determine what type of permissions 
 * the user has within this gear instance by comparing the users roles to the 
 * gears roles.  The user may have roles of guest, member, or administrator and
 * the gear instance will have discussion permission, and write
 * permission associated with one of those roles.  This droplet implies a hierarchy
 * of roles in the order anyone, guest, member, administrator such that if the user 
 * one role he also "inherits" the roles that come earlier in the list.  In addition
 * to personalized permissions, this droplet also considers the values of gear-wide
 * parameters, such as whether the gear is readOnly or has discussions enabled.  
 * This droplet renders the following oparams:
 * <UL>
 * <LI>mayWrite if the user may create,edit,delete documents
 * <LI>mayDiscuss if the user may contribute to discussion
 * <LI>mayUpdateStatus if the user may change the status of an item
 * </UL>
 * 
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/PermissionsDroplet.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class PermissionsDroplet extends DynamoServlet
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/PermissionsDroplet.java#2 $$Change: 651448 $";

  public static final ParameterName MAY_UPDATE_STATUS = ParameterName.getParameterName("mayUpdateStatus");
  public static final ParameterName MAY_DISCUSS = ParameterName.getParameterName("mayDiscuss");
  public static final ParameterName MAY_WRITE = ParameterName.getParameterName("mayWrite");
  public static final ParameterName MAY_NOT_UPDATE_STATUS = ParameterName.getParameterName("mayNotUpdateStatus");
  public static final ParameterName MAY_NOT_DISCUSS = ParameterName.getParameterName("mayNotDiscuss");
  public static final ParameterName MAY_NOT_WRITE = ParameterName.getParameterName("mayNotWrite");
  public static final ParameterName GEAR_ENV = ParameterName.getParameterName("pafEnv");
  

  public static final String UPDATE_STATUS_PERMISSION_ROLE_PARAM = "updateStatusPermissionRole";
  public static final String DISCUSS_PERMISSION_ROLE_PARAM = "discussPermissionRole";
  public static final String WRITE_PERMISSION_ROLE_PARAM = "writePermissionRole";
  public static final String ENABLE_DISCUSSION_PARAM = "enableDiscussion";
  public static final String READ_ONLY_PARAM = "readOnly";

  public void service(DynamoHttpServletRequest pReq,
                      DynamoHttpServletResponse pRes)
    throws ServletException, IOException 
  {
    Object gearEnvObj = pReq.getObjectParameter(GEAR_ENV);
    GearEnvironment gearEnv = (GearEnvironment)gearEnvObj;

    String discussRole = gearEnv.getGearInstanceParameter(DISCUSS_PERMISSION_ROLE_PARAM);
    String writeRole = gearEnv.getGearInstanceParameter(WRITE_PERMISSION_ROLE_PARAM);
    String updateStatusRole = gearEnv.getGearInstanceParameter(UPDATE_STATUS_PERMISSION_ROLE_PARAM);
    
    boolean enableDiscussion = Boolean.valueOf(gearEnv.getGearInstanceParameter(ENABLE_DISCUSSION_PARAM)).booleanValue();
    boolean readOnly = Boolean.getBoolean(gearEnv.getGearInstanceParameter(READ_ONLY_PARAM));
    
    if (isLoggingDebug()) {
      String msg = "updateStatusRole = [" + updateStatusRole
                   + "] discussRole = [" + discussRole
                   + "] writeRole = [" + writeRole + "]";
      logDebug(msg);           
    }
    
    if (userHas(updateStatusRole, gearEnv))
      pReq.serviceLocalParameter(MAY_UPDATE_STATUS, pReq, pRes);
    else 
      pReq.serviceLocalParameter(MAY_NOT_UPDATE_STATUS, pReq, pRes);
      
    if (enableDiscussion && userHas(discussRole,gearEnv))    
      pReq.serviceLocalParameter(MAY_DISCUSS, pReq, pRes);
    else 
      pReq.serviceLocalParameter(MAY_NOT_DISCUSS, pReq, pRes);

    if ((!readOnly) && userHas(writeRole,gearEnv))    
      pReq.serviceLocalParameter(MAY_WRITE, pReq, pRes);
    else 
      pReq.serviceLocalParameter(MAY_NOT_WRITE, pReq, pRes);
  }


  /**
   * returns true if the user has the role requested. 
   */
  boolean userHas(String pRole, GearEnvironment pGearEnv) {
    if (pRole == null || pRole.equals("anyone"))
      return true;

    if (!pGearEnv.isRegisteredUser()) // anonymous user doesn't have further access
      return false;
    
    // let in the leader
    boolean isAdmin = pGearEnv.isLeader();
    if (isAdmin) {
      if (isLoggingDebug()) 
        logDebug("user is admin");
      return true;
    }

    // if required role is any registered user, let everyone who has made it to this point in
    if (pRole.equals("anyRegUser")) 
        return true;

    boolean isMember = pGearEnv.isMember();
    boolean member = pRole.equals("member") && (isAdmin || isMember);
    if (member) {
      if (isLoggingDebug()) {
        logDebug("userHas: member=" + member);     
      } 
      return true;
    }
    boolean isGuest = pGearEnv.isGuest();
    boolean guest = pRole.equals("guest") && (isAdmin || isMember || isGuest);
    if (guest) {
      if (isLoggingDebug()) {
        logDebug("userHas: guest=" + guest);
      }
      return true;
    }
    return false;
  } 

}

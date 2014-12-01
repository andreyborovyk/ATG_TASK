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

package atg.portal.gear.alert.taglib;

import java.security.Principal;

import java.util.*;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;

import atg.core.util.SortedVector;

import atg.nucleus.Nucleus;
import atg.portal.nucleus.NucleusComponents;

import atg.portal.alert.Alert;
import atg.portal.alert.AlertRepositoryManager;
import atg.portal.alert.AlertConstants;
import atg.portal.alert.SourceHandler;
import atg.portal.alert.TargetHandler;

import atg.portal.framework.Community;
import atg.portal.framework.GearEnvironment;
import atg.portal.framework.CommunityTool;
import atg.portal.framework.UserUtilities;

import atg.servlet.ServletUtil;

import atg.repository.*;
import atg.repository.rql.RqlStatement;

import atg.security.AccountManager;
import atg.security.RepositoryAccountManager;

import atg.userdirectory.Role;
import atg.userdirectory.RoleFolder;
import atg.userdirectory.Organization;
import atg.userdirectory.User;
import atg.userdirectory.UserDirectory;
import atg.userdirectory.UserDirectoryLoginUserAuthority;
import atg.userdirectory.account.AccountUserDirectory;
import atg.userdirectory.repository.RepositoryUserDirectoryImpl;

/**
 * Tag for obtaining a list of members for a specific community.
 *
 * @see AnotherClass
 * @author Andy Jacobs
 * @version $Id: //app/portal/version/10.0.3/alert/alertTaglib.jar/src/atg/portal/gear/alert/taglib/GetAlertsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 **/
public class GetAlertsTag extends TagSupport implements AlertConstants
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/alert/alertTaglib.jar/src/atg/portal/gear/alert/taglib/GetAlertsTag.java#2 $$Change: 651448 $";



  private static final String[] PRECACHED_PROPERTIES = {"login", "firstName", "lastName"};


  private static final String COMMUNITY_ENABLED = "communityEnabled";
  private static final String ROLE_ENABLED = "roleEnabled";
  private static final String ORGANIZATION_ENABLED = "organizationEnabled";
  private static final String USER_ENABLED = "userEnabled";


  //-------------------------------------
  // MEMBER VARIABLES
  //-------------------------------------

  private static UserDirectory mUserDirectory;
  private static Repository mAlertsRepository;
  private static RepositoryView mUserRepositoryView;
  private static AlertRepositoryManager mAlertRepositoryManager;

  //-------------------------------------
  // PROPERTIES
  //-------------------------------------

  //-------------------------------------
  // property: GearEnvironment
  private GearEnvironment mGearEnvironment;

  /**
   * Sets the gear for which the list of alerts will be pulled.
   */
  public void setGearEnvironment(GearEnvironment pGearEnvironment)
  {
    mGearEnvironment = pGearEnvironment;
  }

  /**
   * Returns the gear used for used for obtaining a list of alerts.
   */
  public GearEnvironment getGearEnvironment()
  {
    return mGearEnvironment;
  }

  //-------------------------------------
  // property: Count
  private int mCount;

  /**
   *
   */
  public void setCount(String pCount)
  {
    // Make sure everything is cool
    // Count should be greater than 0
    try
    {
      mCount = Integer.parseInt(pCount);
      if(mCount <= 0)
        mCount = -1;
    }
    catch(NumberFormatException e)
    {
      mCount = -1;
    }
  }

  /**
   *
   */
  public String getCount()
  {
    return Integer.toString(mCount);
  }

  /**
   *
   */
  public int getCountAsInt()
  {
    return mCount;
  }


  //-------------------------------------
  // property: Index
  private int mIndex;

  /**
   *
   */
  public void setIndex(String pIndex)
  {
    try
    {
      mIndex = Integer.parseInt(pIndex);
      if(mIndex < 0)
        mIndex = 0;
    }
    catch(NumberFormatException e)
    {
      mIndex = 0;
    }
  }

  /**
   *
   */
  public String getIndex()
  {
    return Integer.toString(mIndex);
  }

  /**
   *
   */
  public int getIndexAsInt()
  {
    return mIndex;
  }

  //-------------------------------------
  // property: AlertType
  private String mAlertType;

  /**
   *
   */
  public void setAlertType(String pAlertType)
  {
    mAlertType = pAlertType;
  }

  /**
   *
   */
  public String getAlertType()
  {
    return mAlertType;
  }

  //-------------------------------------
  // property: alerts
  private Collection mAlerts;

  /**
   *
   */
  public void setAlerts(Collection pAlerts)
  {
    mAlerts = pAlerts;
  }

  /**
   *
   */
  public Collection getAlerts()
  {
    return mAlerts;
  }

  //-------------------------------------
  // property: user
  private User mUser;

  /**
   *
   */
  public void setUser(User pUser)
  {
    mUser = pUser;
  }

  /**
   *
   */
  public User getUser()
  {
    return mUser;
  }

  //-------------------------------------
  // METHODS
  //-------------------------------------

  /**
   * @return a Collection of targets that are configured for this gear.
   */
  private Collection getTargetTypes(String pAlertType)
  {
    String communityEnabled = mGearEnvironment.getGearInstanceParameter(COMMUNITY_ENABLED);
    String roleEnabled = mGearEnvironment.getGearInstanceParameter(ROLE_ENABLED);
    String organizationEnabled = mGearEnvironment.getGearInstanceParameter(ORGANIZATION_ENABLED);
    String userEnabled = mGearEnvironment.getGearInstanceParameter(USER_ENABLED);

    ArrayList targets = new ArrayList();

    if (pAlertType != null && pAlertType.equals(ALERT_USER_VIEW))
    {
        if(userEnabled != null && userEnabled.equals("true"))
          targets.add(ALERT_USER_TARGET);
    }
    else
    {
        // It's the alert GROUP view, so it's one or all of these depending on alert gear config
        if(communityEnabled != null && communityEnabled.equals("true"))
          targets.add(ALERT_COMMUNITY_TARGET);

        if(roleEnabled != null && roleEnabled.equals("true"))
          targets.add(ALERT_ROLE_TARGET);

        if(organizationEnabled != null && organizationEnabled.equals("true"))
          targets.add(ALERT_ORGANIZATION_TARGET);
    }

    return targets;
  }

  /**
   *
   */
  private String getView(String pAlertType)
  {
    if (pAlertType != null && pAlertType.equals(ALERT_USER_VIEW))
      return ALERT_USER_VIEW;
    else
      return ALERT_GROUP_VIEW;
  }


  /**
   *
   **/
  public int doStartTag()
  {
    // What views are we going to search
    Collection targets = getTargetTypes(getAlertType());
    Locale loc = ((HttpServletRequest)pageContext.getRequest()).getLocale();
    User user = UserUtilities.getCurrentUser();

    if(user == null)
    {
      pageContext.setAttribute(getId(), this);
      return SKIP_BODY;
    }

    Collection alerts = mAlertRepositoryManager.getAlerts(user,
                                                          targets,
                                                          getView(getAlertType()),
                                                          getIndexAsInt(),
                                                          getCountAsInt(),
                                                          loc);

    setAlerts(alerts);

    pageContext.setAttribute(getId(), this);
    return EVAL_BODY_INCLUDE;
  }

  /**
   *
   */
  public int doEndTag()
    throws JspException
  {
    return EVAL_PAGE;
  }

  /**
   *
   */
  public void release()
  {
    super.release();
    setGearEnvironment(null);
    setIndex("0");
    setCount("-1");
    setAlertType(null);
    setAlerts(null);
  }

  //-------------------------------------
  // CONSTRUCTORS
  //-------------------------------------
  static
  {
    try
    {
      // Get the AlertRepository
      mAlertsRepository = (Repository) NucleusComponents.lookup("dynamo:/atg/portal/alert/AlertRepository");


      // NOTE: This lookup value should be pulled from the configuration file.
      UserDirectoryLoginUserAuthority userDirectoryLoginUserAuthority;

      userDirectoryLoginUserAuthority = (UserDirectoryLoginUserAuthority) NucleusComponents.lookup("dynamo:/atg/userprofiling/ProfileUserAuthority");

      mUserDirectory = userDirectoryLoginUserAuthority.getUserDirectory();

      // Get the RepositoryView from the user directory
      if(mUserDirectory instanceof RepositoryUserDirectoryImpl)
      {
        Repository repository = ((RepositoryUserDirectoryImpl)mUserDirectory).getRepository();

        mUserRepositoryView = repository.getView("user");
      }

      // Get the Alert Manager.
      mAlertRepositoryManager = (AlertRepositoryManager) NucleusComponents.lookup("dynamo:/atg/portal/alert/AlertRepositoryManager");

    }
    catch(javax.naming.NamingException e)
    {
      System.out.println("Error: GetUserListTag: Unable to get AccountUserDirectory");
    }
    catch(RepositoryException e)
    {
    }
  }




  //-------------------------------------
  // java.lang.Object method overrides
  //-------------------------------------
  /**
   * @return a String representation of this object
   */
  public String toString ()
  {
    StringBuffer buf = new StringBuffer ();
    buf.append (getClass ().getName ());
    buf.append ('[');
    buf.append ("this object's value, as a String");
    buf.append (']');
    return buf.toString ();
  }
}





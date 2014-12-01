/*<ATGCOPYRIGHT>
 * Copyright (C) 2002-2011 Art Technology Group, Inc.
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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.calendar.taglib;
 
import atg.portal.gear.calendar.CalendarConstants;
import atg.portal.framework.GearEnvironment;
import java.io.IOException;
import java.util.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * JSP Tag to determine whether a user has access to create calendar events. The body of the
 * tag will be rendered or skipped, based on whether the user has access.
 *
 * This is determined as follows:
 * if user is a portal administrator, grant access
 * 
 * if mIsPublic (i.e. public event) is true, check for either:
 *  gear instance parameter "openAccessPublic"=true
 *  OR
 *  user has gear role "calAdmin"
 *
 * if mIsPublic is false, check for either:
 *  gear instance parameter "openAccessPrivate"=true
 *  OR
 *  user has gear role "calWriter" OR "calAdmin"
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/HasAccessTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class HasAccessTag extends TagSupport {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/HasAccessTag.java#2 $$Change: 651448 $";



  protected GearEnvironment mGearEnv;
  public GearEnvironment getGearEnv()
  {
    return mGearEnv;
  }
  public void setGearEnv(GearEnvironment pGearEnv)
  {
    mGearEnv = pGearEnv;
  }

  protected boolean mIsPublic;
  public boolean getIsPublic()
  {
    return mIsPublic;
  }
  public void setIsPublic(boolean pIsPublic)
  {
    mIsPublic = pIsPublic;
  }

  public int    doStartTag() throws JspException
  {

    if ( mGearEnv.isPortalAdministrator() ) {
       return EVAL_BODY_INCLUDE;
    }

    Boolean privateAccessParam=Boolean.valueOf( mGearEnv.getGearInstanceParameter("openAccessPrivate"));
    Boolean publicAccessParam=Boolean.valueOf( mGearEnv.getGearInstanceParameter("openAccessPublic"));
    if (mIsPublic) {
      if (publicAccessParam.booleanValue()) {
        return EVAL_BODY_INCLUDE;
      } else if (mGearEnv.getGear().hasRole(CalendarConstants.ADMIN_ROLENAME) ) {
        return EVAL_BODY_INCLUDE;
      }
    } else {
       if (privateAccessParam.booleanValue() || publicAccessParam.booleanValue()) {
                return EVAL_BODY_INCLUDE;
       } else if (mGearEnv.getGear().hasRole(CalendarConstants.WRITER_ROLENAME) || mGearEnv.getGear().hasRole(CalendarConstants.ADMIN_ROLENAME) ) {
             return EVAL_BODY_INCLUDE;
       }
    }

    return SKIP_BODY;
  }

//----------------------------------------

public int doEndTag ()
  throws JspException
  {
    return EVAL_PAGE;
  }


//----------------------------------------
/**
 * release this tag
 */
public void release()
{
  super.release();

  setIsPublic(false);
  setGearEnv(null);

}


}

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

package atg.portal.gear.calendar.taglib;

import javax.servlet.jsp.tagext.*;

/**
 * The following information should be included for JavaBeans and
 * for Nucleus components.  See the BeanInfo Generator spec for details.
 *
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/DateTEI.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *    description: (description of this JavaBean if applicable)
 *    attribute: componentCategory (category of this Nucleus component)
 *    attribute: icon (DDW icon for this Nucleus component)
 **/

public class DateTEI extends TagExtraInfo 
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/DateTEI.java#2 $$Change: 651448 $";

  /**
   *
   **/
  public VariableInfo[] getVariableInfo(TagData pData) {
    return new VariableInfo[] {
        new VariableInfo(pData.getId(),
                         "atg.portal.gear.calendar.taglib.DateTag",
                         true,
                         VariableInfo.NESTED)
      };
  }
}

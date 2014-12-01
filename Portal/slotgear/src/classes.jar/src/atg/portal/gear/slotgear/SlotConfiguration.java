// Configuration.java
//
//<ATGCOPYRIGHT>
// Copyright (C) 2001-2011 Art Technology Group, Inc.
// All Rights Reserved.  No use, copying or distribution of this
// work may be made except in accordance with a valid license
// agreement from Art Technology Group.  This notice must be
// included on all copies, modifications and derivatives of this
// work.
//
// Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES 
// ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
// LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
// MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
//
// "Dynamo" is a trademark of Art Technology Group, Inc.
//</ATGCOPYRIGHT>*/

package atg.portal.gear.slotgear;

import java.util.*;
import javax.naming.*;
import atg.nucleus.*;
import atg.security.*;

/**
 * Object that contains configuration settings for the Portal, set by
 * Nucleus.
 *
 * @version $Id: //app/portal/version/10.0.3/slotgear/classes.jar/src/atg/portal/gear/slotgear/SlotConfiguration.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class SlotConfiguration extends GenericService
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/slotgear/classes.jar/src/atg/portal/gear/slotgear/SlotConfiguration.java#2 $$Change: 651448 $";

  /////////////
  // Members //
  /////////////

  private Properties mSlotPages = new Properties();

  ////////////////////////
  // Property accessors //
  ////////////////////////

  /**
   * Returns the set of "slot" pages, indexed by itemDescriptorName
   */
  public Properties getSlotPages()
  {
    return mSlotPages;
  }

  /**
   * Changes the set of "login" pages, indexed by output device.
   */
  public void setSlotPages(Properties pPages)
  {
    mSlotPages = pPages;
  }

  /**
   * Returns the slot page for the given itemDescriptorName.
   */
  public String getSlotPage(String pItemDescriptor)
  {
    return mSlotPages.getProperty(pItemDescriptor);
  }

}

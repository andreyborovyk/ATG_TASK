/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.droplet;

import java.awt.event.*;

/**
 * <P>Interface implemented by an AWT Component that will customize
 * a DropletInvocation to call some droplet.
 *
 * @author Joe Berkovitz
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletCustomizer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public interface DropletCustomizer
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletCustomizer.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * Sets the DropletInvocation to be customized.
   */
  public void setInvocation(DropletInvocation invocation);

  //-------------------------------------
  /**
   * Returns true if the customization completed successfully.
   */
  public boolean isComplete();

  //-------------------------------------
  /**
   * Adds an action listener to receive an event when this
   * customizer is either completed or cancelled.
   */
  public void addActionListener(ActionListener l);

  //-------------------------------------
  /**
   * Removes an action listener.
   */
  public void removeActionListener(ActionListener l);
}

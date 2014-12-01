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

package atg.droplet;

import javax.servlet.*;

import java.io.*;
import java.util.*;

/**
 * An EventReceiver is a tag that receives events from requests (either Anchor
 * events or Form events).
 * <p>
 * This interface is implemented by the AnchorTag and the FormEventReceiver
 * tags: InputTag, SelectTag, and TextAreaTag.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/EventReceiver.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
interface EventReceiver {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/EventReceiver.java#2 $$Change: 651448 $";

  //---------------------------
  /** 
   * @return the default value to use in a set event if no value was
   * specified 
   */
  public String getDefault();

  /**
   * @return the value to use when the submit occurs (overriding the 
   * value in the form).  If this returns null, we use the value in the
   * form.
   */
  public String getSubmitValue();

  /**
   * Get the name of the parameter that identifies this event as occuring
   */
  public String getName();

  /**
   * Return the complete path name of the property
   */
  public String getPropertyPath();

  /**
   * Return the array of path names that reference the properties
   */
  public String [] getPathNames();

  /**
   * Sets the list of path names
   */
  public void setPathNames(String [] pPathNames);

  /**
   * Return the dimensions for each entry in the path.  If this returns
   * null, there are no dimensions for any of the path dimensions.
   */
  public int [][] getPathDims();

  /**
   * Sets the dimensions for each entry of the path (could be null if
   * no path entries have dimensions).
   */
  public void setPathDims(int [][] pPathNames);

  /**
   * Return the path name of the component 
   */
  public String getComponentPath();

  /**
   * Is this handler expecting an image expecting .x and .y args?
   */
  public boolean isImage();

  //-------------------------------------
  /**
   * The converter to apply to any set accesses to this value
   */
  public TagConverter getConverter();

  //-------------------------------------
  /**
   * The arguments for the converter
   */
  public Properties getConverterArgs();

  //-------------------------------------
}

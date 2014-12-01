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

import java.beans.*;

/**
 * <P>Interface representing an invocation of some droplet, to be customized by
 * a DropletCustomizer.
 *
 * @author Joe Berkovitz
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletInvocation.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public interface DropletInvocation
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletInvocation.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * Sets the value of a parameter.
   * @param name the parameter name
   * @param value the string value of the parameter
   * @param isOutput true if the parameter is an OPARAM
   */
  public void putParameter(String name, DropletInvocation.Parameter value);

  //-------------------------------------
  /**
   * Removes a parameter from the invocation.
   */
  public void removeParameter(String name);

  //-------------------------------------
  /**
   * Gets the string value of a parameter.  Returns null
   * if parameter is not set.
   */
  public DropletInvocation.Parameter getParameter(String name);

  //-------------------------------------
  /**
   * Returns the HTML representation of this invocation.
   */
  public String getInvocationText();

  //-------------------------------------
  /**
   * Get the set of ParamDescriptors associated with this invocation
   */
  public ParamDescriptor[] getParamDescriptors();

  //-------------------------------------
  /**
   * Listen for parameter changes
   */
  public void addPropertyChangeListener(PropertyChangeListener listener);

  //-------------------------------------
  /**
   * Stop listening for parameter changes
   */
  public void removePropertyChangeListener(PropertyChangeListener listener);

  //-------------------------------------
  /**
   * Support class representing a droplet parameter.
   */
  public static class Parameter {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletInvocation.java#2 $$Change: 651448 $";

    private String mValue;
    private boolean mIsOutput;

    public Parameter(String pValue, boolean pIsOutput) {
      mValue = pValue;
      mIsOutput = pIsOutput;
    }

    public Parameter(String pValue) {
      this (pValue, false);
    }

    public String getValue() {
      return mValue;
    }

    public boolean isOutput() {
      return mIsOutput;
    }
  }
}

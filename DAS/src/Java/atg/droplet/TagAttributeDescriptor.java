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

/**
 * This class describes an attribute which is used as an argument to a
 * TagConverter.   This contains a description of the attribute as well
 * as flags indicating whether or not the attribute is required or optional
 * for the use of that converter.  An attribute can also be "automatic" which
 * means that the converter is implied by the use of that attribute in one
 * of the tags which accepts TagConverters.  If a converter does not have any
 * automatic attributes, you must always specify the name of the converter
 * using the converter attribute.
 *
 * @see atg.droplet.TagConverter
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagAttributeDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class TagAttributeDescriptor extends java.beans.FeatureDescriptor
{
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagAttributeDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  /** True if this parameter is optional */
  boolean mOptional = false;

  /** True if the presence of this attribute implies the use of this converter */
  boolean mAutomatic = false;

  //-------------------------------------
  // CONSTRUCTORS
  //-------------------------------------

  public TagAttributeDescriptor() {
  }

  public TagAttributeDescriptor(String pName,
                         String pDescription,
                         boolean pOptional,
                         boolean pAutomatic)
  {
    setName(pName);
    setShortDescription(pDescription);
    setOptional(pOptional);
    setAutomatic(pAutomatic);
  }

  //-------------------------------------
  // METHODS
  //-------------------------------------


  /**
   * Sets property Optional
   **/
  public void setOptional(boolean pOptional) {
    mOptional = pOptional;
  }

  /**
   * Returns property Optional
   **/
  public boolean isOptional() {
    return mOptional;
  }

  /**
   * Sets property Automatic.  This indicates whether or not the use of
   * this attribute in a tag should imply the use of a converter.  For
   * example, the DateTagConverter is implied by the use of the date 
   * attribute.  If no automatic attribute exists for a particular converter,
   * it must always be specified by name using the converter attribute.
   **/
  public void setAutomatic(boolean pAutomatic) {
    mAutomatic = pAutomatic;
  }

  /**
   * Returns property Automatic
   **/
  public boolean isAutomatic() {
    return mAutomatic;
  }

  //----------------------------------------
  /**
   * String representation
   */
  public String toString() {
    return getName();
  }
}

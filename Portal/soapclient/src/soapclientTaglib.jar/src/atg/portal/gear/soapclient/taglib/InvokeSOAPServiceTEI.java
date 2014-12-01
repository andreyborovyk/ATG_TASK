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

package atg.portal.gear.soapclient.taglib;

// Java classes
import javax.servlet.jsp.tagext.*;

// DAS classes

// DPS classes

// DSS classes

// DCS classes

/**
 * The TagExtraInfo for the 
 * {@link InvokeSOAPServiceTag<code>InvokeSOAPServiceTag</code>}
 * tag.
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/taglib/InvokeSOAPServiceTEI.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class InvokeSOAPServiceTEI
  extends TagExtraInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/taglib/InvokeSOAPServiceTEI.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * Return a <code>VariableInfo</code> object that indicats the nature
   * of the scripting variable for the InvokeSOAPServiceTag.  The
   * InvokeSOAPServiceTag makes itself available as scripting variable
   * that is accessible for a scope of <code>VariableInfo.NESTED</code>.
   *
   *
   * @param pData a <code>TagData</code> value
   * @return a <code>VariableInfo[]</code> value that will contain a 
   * single VariableInfo object.
   */
  public VariableInfo[] getVariableInfo(TagData pData) 
  {
    return new VariableInfo[] 
      {
        new VariableInfo(pData.getId(),
                         "atg.portal.gear.soapclient.SOAPServiceTagResult",
                         true,
                         VariableInfo.AT_END)
          };
  }

}   // end of class

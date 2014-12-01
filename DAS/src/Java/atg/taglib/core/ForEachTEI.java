/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.taglib.core;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * This class provides the tag extra info for the ForEach tag
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ForEachTEI.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class ForEachTEI
    extends TagExtraInfo
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ForEachTEI.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof ForEachTEI
     */
    public ForEachTEI()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * get the variable info for the ForEachTag object
     * @param pTagData the TagData for the ForEachTag object
     * @return a VariableInfo array containing the VariableInfo for a
     * ForEachTag object
     */
    public VariableInfo[] getVariableInfo(TagData pTagData)
    {
	if(pTagData.getAttributeString("castClass") != null &&
	   pTagData.getAttributeString("elementId") != null &&
	   pTagData.getAttributeString("keyId") != null &&
	   pTagData.getAttributeString("keyCastClass") != null) {
	    return new VariableInfo[] {
		new VariableInfo(pTagData.getId(),
				 "atg.taglib.core.ForEachTag",
				 true,
				 VariableInfo.NESTED),
		new VariableInfo(pTagData.getAttributeString("elementId"),
				 pTagData.getAttributeString("castClass"),
				 true,
				 VariableInfo.NESTED),
		new VariableInfo(pTagData.getAttributeString("keyId"),
				 pTagData.getAttributeString("keyCastClass"),
				 true,
				 VariableInfo.NESTED)
		    };
	}
	else if(pTagData.getAttributeString("castClass") != null &&
		pTagData.getAttributeString("elementId") != null) {
	    return new VariableInfo[] {
		new VariableInfo(pTagData.getId(),
				 "atg.taglib.core.ForEachTag",
				 true,
				 VariableInfo.NESTED),
		new VariableInfo(pTagData.getAttributeString("elementId"),
				 pTagData.getAttributeString("castClass"),
				 true,
				 VariableInfo.NESTED)
		    };
	}
	else if(pTagData.getAttributeString("keyCastClass") != null &&
		pTagData.getAttributeString("keyId") != null) {
	    return new VariableInfo[] {
		new VariableInfo(pTagData.getId(),
				 "atg.taglib.core.ForEachTag",
				 true,
				 VariableInfo.NESTED),
		new VariableInfo(pTagData.getAttributeString("keyId"),
				 pTagData.getAttributeString("keyCastClass"),
				 true,
				 VariableInfo.NESTED)
		    };
	}
	else {
	    return new VariableInfo[] {
		new VariableInfo(pTagData.getId(),
				 "atg.taglib.core.ForEachTag",
				 true,
				 VariableInfo.NESTED),
	    };
	}
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

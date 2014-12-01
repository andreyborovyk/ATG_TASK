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

/****************************************
 * an ordering object to be used when creating 
 * a SortTagComparator
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/Ordering.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class Ordering
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/Ordering.java#2 $$Change: 651448 $";

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
    // Property
    private String mProperty;
    /**
     * set Property
     * @param pProperty the Property
     */
    public void setProperty(String pProperty) { mProperty = pProperty; }
    /**
     * get Property
     * @return the Property
     */
    public String getProperty() { return mProperty; }

    //----------------------------------------
    // Reverse
    private boolean mReverse;
    /**
     * set Reverse
     * @param pReverse the Reverse
     */
    public void setReverse(boolean pReverse) { mReverse = pReverse; }
    /**
     * get Reverse
     * @return the Reverse
     */
    public boolean isReverse() { return mReverse; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof Ordering
     */
    public Ordering(String pProperty, boolean pReverse)
    {
	mProperty = pProperty;
	mReverse = pReverse;
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

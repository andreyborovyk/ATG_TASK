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

package atg.sqljmsadmin.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * encode a compound key from two longs into a single string
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/EncodeCompoundKeyTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class EncodeCompoundKeyTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/EncodeCompoundKeyTag.java#2 $$Change: 651448 $";

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
    // Long1
    private Long mLong1;
    /**
     * set Long1
     * @param pLong1 the Long1
     */
    public void setLong1(Long pLong1) { mLong1 = pLong1; }
    /**
     * get Long1
     * @return the Long1
     */
    public Long getLong1() { return mLong1; }

    //----------------------------------------
    // Long2
    private Long mLong2;
    /**
     * set Long2
     * @param pLong2 the Long2
     */
    public void setLong2(Long pLong2) { mLong2 = pLong2; }
    /**
     * get Long2
     * @return the Long2
     */
    public Long getLong2() { return mLong2; }

    //----------------------------------------
    // EncodedKey
    private String mEncodedKey;
    /**
     * set EncodedKey
     * @param pEncodedKey the EncodedKey
     */
    public void setEncodedKey(String pEncodedKey) { mEncodedKey = pEncodedKey; }
    /**
     * get EncodedKey
     * @return the EncodedKey
     */
    public String getEncodedKey() { return mEncodedKey; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof EncodeCompoundKeyTag
     */
    public EncodeCompoundKeyTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start this tag
     */
    public int doStartTag()
	throws JspException
    {
	if(mLong1 == null || mLong2 == null)
	    return SKIP_BODY;

	long value1 = mLong1.longValue();
	long value2 = mLong2.longValue();

	byte[] valuesCombined = new byte[32];
	byte tempByte;

	// encode the first long
	for(int i=0, j=56; i < 16; i+=2, j-=8) {
	    tempByte = (byte) (value1 >>> j);
	    valuesCombined[i] = (byte) (tempByte >> 4);
	    valuesCombined[i] &= (byte) 0x0f;
	    valuesCombined[i+1] = (byte) (tempByte & 0x0f);
	    valuesCombined[i] += 65;
	    valuesCombined[i+1] += 65;
	}

	// encode the second long
	for(int i=16, j=56; i < 32; i+=2, j-=8) {
	    tempByte = (byte) (value2 >>> j);
	    tempByte = (byte) (tempByte & 0xff);
	    valuesCombined[i] = (byte) (tempByte >> 4);
	    valuesCombined[i] &= (byte) 0x0f;
	    valuesCombined[i+1] = (byte) (tempByte & 0x0f);
	    valuesCombined[i] += 65;
	    valuesCombined[i+1] += 65;
	}

	mEncodedKey = new String(valuesCombined);

	pageContext.setAttribute(getId(), this);

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mLong1 = null;
	mLong2 = null;
	mEncodedKey = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

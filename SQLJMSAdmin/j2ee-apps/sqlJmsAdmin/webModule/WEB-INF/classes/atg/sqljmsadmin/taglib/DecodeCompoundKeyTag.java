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
 * decode a compound key from a string into two longs
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DecodeCompoundKeyTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DecodeCompoundKeyTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DecodeCompoundKeyTag.java#2 $$Change: 651448 $";

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
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DecodeCompoundKeyTag
     */
    public DecodeCompoundKeyTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * do start tag
     */
    public int doStartTag()
	throws JspException
    {
	if(mEncodedKey == null)
	    return SKIP_BODY;

	byte[] valuesCombined = mEncodedKey.getBytes();

	byte halfByte1 = 0;
	byte halfByte2 = 0;
	byte fullByte = 0;
	long tempValue = 0;
	long value1New = 0;
	long value2New = 0;

	for(int i=0, j=56; i < 16; i+=2, j-=8) {
	    fullByte = 0;
	    halfByte1 = valuesCombined[i];
	    halfByte2 = valuesCombined[i+1];
	    halfByte1 -= 65;
	    halfByte2 -= 65;

	    fullByte = (byte) (halfByte1 << 4);
	    fullByte &= (byte) 0xf0;
	    halfByte2 &= (byte) 0x0f;
	    fullByte |= halfByte2;

	    tempValue = (long) (fullByte & 0xff);
	    tempValue <<= j;
	    value1New ^= tempValue;
	}

	for(int i=16, j=56; i < 32; i+=2, j-=8) {
	    fullByte = 0;
	    halfByte1 = valuesCombined[i];
	    halfByte2 = valuesCombined[i+1];
	    halfByte1 -= 65;
	    halfByte2 -= 65;

	    fullByte = (byte) (halfByte1 << 4);
	    fullByte &= (byte) 0xf0;
	    halfByte2 &= (byte) 0x0f;
	    fullByte |= halfByte2;

	    tempValue = (long) (fullByte & 0xff);
	    tempValue <<= j;
	    value2New ^= tempValue;
	}

	mLong1 = new Long(value1New);
	mLong2 = new Long(value2New);

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
	mEncodedKey = null;
	mLong1 = null;
	mLong2 = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

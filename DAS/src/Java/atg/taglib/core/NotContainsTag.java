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
import java.util.*;

/****************************************
 * The atg:NotContains tag tests to see if an Object or primitive is equal
 * to any of the Objects or primitives within the values attribute of the
 * atg:Contains tag. If
 * so, the body is not rendered, if not, then the body is rendered. This tag can take an
 * optional java.util.Comparator instance attribute. If one
 * is supplied, then the 
 * values attribute is considered to contain the Object or primitive attribute if the
 * Object or primitive attribute is equal to any of the values of the
 * Objects or primitives within
 * the values attribute, as defined by the Comparator instance. 
 * The casting and comparison rules for the atg:Contains tag are the same
 * as those used in the <a href="#ifNotEqual">atg:IfNotEqual</a> tag.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;atg:NotContains values="&lt;%= specialCustomerIds %&gt;"
 *                  object="&lt;%= profile.getId() %&gt;"&gt;
 *   
 *   Hey &lt;%= profile.getFirstName() %&gt;, you aren't one of our special
 *   customers. Sorry.
 *   
 * &lt;/atg:NotContains&gt;
 * </pre>
 * </code>
 * 
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/NotContainsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class NotContainsTag
    extends ContainsTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/NotContainsTag.java#2 $$Change: 651448 $";

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
     * Constructs an instanceof NotContainsTag
     */
    public NotContainsTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // GenericTag methods
    //----------------------------------------

    //----------------------------------------
    /**
     * render this tag if the values attribute does not contain
     * the specified value
     */
    public int doStartTag()
	throws JspException
    {
	if(getValues() == null)
	    return SKIP_BODY;

	if(isChildOfExclusiveIf() && getExclusiveIfTag().isTesting() == false)
	    return SKIP_BODY;

	// first, determine the type of the values attribute
	if(getValues() instanceof Collection) {
	    Collection values = (Collection) getValues();

	    if(checkForInclusion(values.iterator()) == NOT_CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Iterator
	else if(getValues() instanceof Iterator) {
	    Iterator values = (Iterator) getValues();
	    
	    if(checkForInclusion(values) == NOT_CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Object Array
	else if(getValues() instanceof Object[]) {
	    Object[] values = (Object[]) getValues();

	    if(checkForInclusion(values) == NOT_CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Enumeration
	else if(getValues() instanceof Enumeration) {
	    Enumeration values = (Enumeration) getValues();
	    if(checkForInclusion(values) == NOT_CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Dictionary
	else if(getValues() instanceof Dictionary) {
	    Dictionary values = (Dictionary) getValues();
	    
	    if((isCompareElement() ? 
		checkForInclusion(values.elements()) : 
		checkForInclusion(values.keys())) == NOT_CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Map
	else if(getValues() instanceof Map) {
	    Map values = (Map) getValues();

	    if((isCompareElement() ?
		checkForInclusion(values.values().iterator()) :
		checkForInclusion(values.keySet().iterator())) == NOT_CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// boolean array
	else if(getValues() instanceof boolean[]) {
	    boolean[] values = (boolean[]) getValues();
	    
	    if(isBooleanSet()) {
		for(int i=0; i < values.length; i++) {
		    if(isBoolean() == values[i])
			return SKIP_BODY;
		}
	    }
	    
	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	// byte array
	else if(getValues() instanceof byte[]) {
	    byte[] values = (byte[]) getValues();

	    if(isByteSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getByte() == values[i])
			return SKIP_BODY;
		}
	    }

	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	// char array
	else if(getValues() instanceof char[]) {
	    char[] values = (char[]) getValues();

	    if(isCharSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getChar() == values[i])
			return SKIP_BODY;
		}
	    }

	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	// short array
	else if(getValues() instanceof short[]) {
	    short[] values = (short[]) getValues();

	    if(isShortSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getShort() == values[i])
			return SKIP_BODY;
		}
	    }

	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	// int array
	else if(getValues() instanceof int[]) {
	    int[] values = (int[]) getValues();

	    if(isIntSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getInt() == values[i])
			return SKIP_BODY;
		}
	    }

	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	// long array
	else if(getValues() instanceof long[]) {
	    long[] values = (long[]) getValues();

	    if(isLongSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getLong() == values[i])
			return SKIP_BODY;
		}
	    }

	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	// float array
	else if(getValues() instanceof float[]) {
	    float[] values = (float[]) getValues();

	    if(isFloatSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getFloat() == values[i])
			return SKIP_BODY;
		}
	    }

	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	// double array
	else if(getValues() instanceof double[]) {
	    double[] values = (double[]) getValues();

	    if(isDoubleSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getDouble() == values[i])
			return SKIP_BODY;
		}
	    }

	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}

	return SKIP_BODY;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

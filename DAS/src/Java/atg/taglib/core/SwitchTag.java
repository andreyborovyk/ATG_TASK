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

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * The core:Switch tag is used exactly like a switch statement in Java. The
 * body of the switch tag contains any number of core:Case tags and one
 * optional core:DefaultCase tag. When the core:Switch tag renders its
 * body, it will test its value attribute against the value attributes of
 * its immediate child core:Case tags until it finds a core:Case tag whose
 * value tests true for equality. It will then render the body of the
 * core:Case tag with an equal value, and skip all the other core:Case and
 * core:DefaultCase tags. If there are no equal core:Case tags and an
 * core:DefaultCase tag is encountered, the body of the core:DefaultCase
 * tag is rendered, and all subsequent core:Case and core:DefaultCase tags
 * are not rendered.
 * <p>
 * An optional Comparator attribute may also be supplied which will be
 * used when comparing the core:Switch tag's value attribute with the
 * value attributes of all the core:Case children tags.
 * <p>
 * The core:Switch tag should only
 * be used when doing equality comparisons between Objects. If one
 * would like to do more complex comparisons using other boolean
 * comparison operators, one should probably consider using the core:ExclusiveIf tag.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;core:Switch value="&lt;%= profile.getType() %&gt;"&gt;
 * 
 *   &lt;core:Case value="&lt;%= UserTypes.BIG_SPENDER %&gt;"&gt;
 *     So good to see you again &lt;%= profile.getFirstName() %&gt;! Take
 *     your time and let us know if there's anything with which we can help you. 
 *   &lt;/core:Case&gt;
 * 
 *   &lt;core:Case value="&lt;%= UserTypes.STINGY_SPENDER %&gt;"&gt;
 *     You're a drain on our business. Please don't come to our site anymore.
 *   &lt;/core:Case&gt;
 * 
 *   &lt;core:DefaultCase&gt;
 *     Whatever, thanks for coming to our site. Be a sport and buy something.
 *   &lt;/core:DefaultCase&gt;
 * 
 * &lt;/core:Switch&gt;
 * </pre>
 * </code>
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/SwitchTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class SwitchTag
    extends ExclusiveIfTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/SwitchTag.java#2 $$Change: 651448 $";

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
    // Value
    private Object mValue;

    /**
     * set Value
     * @param pValue the Value
     */
    public void setValue(Object pValue) { mValue = pValue; }
    /**
     * get Value
     * @return the Value
     */
    public Object getValue() { return mValue; }

    //----------------------------------------
    // Comparator
    private Comparator mComparator;
    /**
     * set Comparator
     * @param pComparator the Comparator
     */
    public void setComparator(Comparator pComparator) { mComparator = pComparator; }
    /**
     * get Comparator
     * @return the Comparator
     */
    public Comparator getComparator() { return mComparator; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof SwitchTag
     */
    public SwitchTag()
    {
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
      super.release();
      setValue(null);
      setComparator(null);
      processEndTagIfNecessary();
    }
   
    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

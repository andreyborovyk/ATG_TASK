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
 * The atg:ExclusiveIf tag behaves in a similar way to 
 * an if/else block in Java. This tag may contain any number 
 * of conditional subtags such as atg:IfEqual, atg:IfNotEqual, 
 * atg:IfGreaterThan etc. The atg:ExclusiveIf tag will render 
 * the first and only the first immediate child conditional 
 * tag which evaluates to true. The bodies of all other immediate 
 * child conditional tags within the atg:ExclusiveIf body will 
 * not be rendered. If there is an atg:DefaultCase tag present 
 * within the body of this tag, and none of the boolean conditional 
 * tags evaluate to true, then the atg:DefaultCase tag's body 
 * will be rendered, and no other immediate boolean logic or 
 * atg:DefaultCase tags will be rendered.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ExclusiveIfTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class ExclusiveIfTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ExclusiveIfTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------
    private boolean mStarted = false;
    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Testing
    private boolean mTesting;
    /**
     * set Testing
     * @param pTesting the Testing
     */
    public void setTesting(boolean pTesting) { mTesting = pTesting; }
    /**
     * get Testing
     * @return the Testing
     */
    public boolean isTesting() { return mTesting; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof ExclusiveIfTag
     */
    public ExclusiveIfTag()
    {
	setTesting(true);
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // GenericTag methods
    //----------------------------------------

    //----------------------------------------
    /**
     * override the doStartTag method
     * always render the body of this tag
     */
    public int doStartTag()
    throws JspException
    {
      mStarted = true;
      return EVAL_BODY_INCLUDE;
    }
    
    //  ----------------------------------------
    /**
     * finish executing this tag
     * @return EVAL_PAGE so that the rest of the page gets evaluated
     * @exception JspException if there was an error
     */
    public int doEndTag()
    throws JspException
    {
      processEndTagIfNecessary();
      return EVAL_PAGE;
    }
    

    //----------------------------------------
    /**
     * override the release method
     */
    public void release()
    {
      super.release();
      processEndTagIfNecessary();
    }
    
    /**
     * This method gets called either from the doEndTag, or from the release
     * method, whichever gets called first for a given doStartTag invocation.
     * Remember that in JSP, you can see the sequence 
     *   doStartTag
     *   doEndTag
     *   doStartTag
     *   release
     * if the implementation is "reusing tag instances".  We only want to do
     * this logic once per page tag instance on the page.
     */
    void processEndTagIfNecessary() {
      try {
        if (!mStarted) return;
      } finally {
        performPerTagCleanup();
      }
    }
    //  -------------------------------------
    /**
     *
     * Cleans up any state left over from execution of a tag
     **/
    public void performPerTagCleanup ()
    {
      mStarted = false;
      mTesting = true;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

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


import javax.ejb.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 *IfEqualEJBTag implementation
 *
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/IfEqualEJBTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class IfEqualEJBTag
  extends EqualEJB
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/IfEqualEJBTag.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Member variables
  //-------------------------------------


  //-------------------------------------
  // Properties
  //-------------------------------------
 

  /**
   *
   * Constructor
   **/
  public IfEqualEJBTag()
  {
  }

  //-------------------------------------
  // Tag interface methods
  //-------------------------------------

  //-------------------------------------
  /**
   *
   * This performs the actual work of the tag.  
   **/

  public int doStartTag ()
    throws JspException
  {
      if(getEjb1()==null||
         getEjb2()==null) {
          return SKIP_BODY;
      }
      if(isEquals()) {
	  doneTesting();
	  return EVAL_BODY_INCLUDE;
      }
      return SKIP_BODY;
  }

}











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
 *
 * <p>base class for IfEqualEJBTag and IfNotEqualEJBTag
 *
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/EqualEJB.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class EqualEJB
  extends BooleanConditionalTag
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/EqualEJB.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Member variables
  //-------------------------------------


  //-------------------------------------
  // Properties
  //-------------------------------------
 
 
  //-------------------------------------
  // property ejb1

  private EJBObject mEjb1;

  public EJBObject getEjb1()
  { return mEjb1; }

  public void setEjb1(EJBObject pEjb1)
  { 
     mEjb1 = pEjb1; 
  }

  //-------------------------------------
  // property ejb

  private EJBObject mEjb2;

  public EJBObject getEjb2 ()
  { return mEjb2; }

  public void setEjb2 (EJBObject pEjb2)
  {
      mEjb2 = pEjb2; 
   }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public EqualEJB()
  {
  }

  //-------------------------------------
  // Tag interface methods
  //-------------------------------------

  //-------------------------------------

    public void release()
    {
	setEjb1(null);
	setEjb2(null);
    }


    protected boolean isEquals(){
	if (getEjb1()!=null&&
            getEjb2()!=null){
	    try{
		if (getEjb1().isIdentical(getEjb2()))
                  return true;	                
	    } catch (Exception e){}   
	}
        return false;
    }
    

}











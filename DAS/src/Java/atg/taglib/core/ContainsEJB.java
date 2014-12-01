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
import javax.rmi.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.util.*;

/**
 *
 * <p>base class for ContainsEJBTag and NotContainEJBTag
 *
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ContainsEJB.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ContainsEJB
  extends BooleanConditionalTag
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ContainsEJB.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Member variables
  //-------------------------------------


  //-------------------------------------
  // Properties
  //-------------------------------------
 
 
  //-------------------------------------
  // property mValues

  private Object mValues;

  public Object getValues ()
  { return mValues; }

  public void setValues (Object pValues)
  { mValues = pValues; }

  //-------------------------------------
  // property ejb

  private EJBObject mEjb;

  public EJBObject getEjb ()
  { return mEjb; }

  public void setEjb (EJBObject pEjb)
  {
     mEjb = pEjb; 
  }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public ContainsEJB()
  {
  }

  //-------------------------------------
  // Tag interface methods
  //-------------------------------------

  //-------------------------------------

    public void release()
    {
	super.release();

	setValues(null);
	setEjb(null);
    }

    protected boolean isContains(Iterator pIterator){
	if (getEjb()!=null){
	   while (pIterator.hasNext()){
	      try{
              EJBObject object=(EJBObject)PortableRemoteObject.narrow( pIterator.next(),EJBObject.class);
              if (getEjb().isIdentical(object))
                  return true;
	      } catch (Exception e){}
              
	   }      
	}
        return false;
    }
    protected boolean isContains(Object[] pObjects){
         if (getEjb()!=null){
	   for (int i=0;i<pObjects.length;i++){
	      try{
              EJBObject object=(EJBObject)PortableRemoteObject.narrow( pObjects[i],EJBObject.class);
              if (getEjb().isIdentical(object))
                  return true;
	      } catch (Exception e){}
              
	   }      
	}
        return false;
    }
    protected boolean isContains(Enumeration pEnumeration){
        if (getEjb()!=null){
	   while (pEnumeration.hasMoreElements()){
	      try{
              EJBObject object=(EJBObject)PortableRemoteObject.narrow( pEnumeration.nextElement(),EJBObject.class);
              if (getEjb().isIdentical(object))
                  return true;
	      } catch (Exception e){}
              
	   }      
	}
        return false;
    }
   

}











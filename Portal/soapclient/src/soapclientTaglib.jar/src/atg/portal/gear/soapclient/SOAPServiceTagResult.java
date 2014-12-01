/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.portal.gear.soapclient;

// Java classes
import java.util.*;
// DAS classes

// DPS classes

// DSS classes

// DCS classes

/**
 * This class represents the object that is returned by the
 * {@link atg.portal.gear.soapclient.taglib.InvokeSOAPServiceTag<code>
 * InvokeSOAPServiceTag's</code>} invokeService method.
 *
 * <P>It supplies two pieces of information, a possible returned value from
 * the invoked service.  This value is accessible via the
 * <code>getResult()</code> method.  Additionally, this result object will
 * return a set of error codes if any error occurred during the processing
 * of the tag.  The page programmer can check to see if any errors occurred
 * via the <code>hasError()</code> method.  If the result object indicates
 * that there are errors, then the page developer should ignore trying to
 * get the results and look to the <code>ErrorCodes</code> property.  This
 * property will contain a String array of possible error codes that could
 * be returned from the tag.  The page developer should then iterate over these
 * error codes and use them to look up a particular string message in a given
 * resource bundle.
 *
 * <P>For more information on what error codes could be returned by a tag
 * check the <code>InvokeSOAPServiceTag</code>
 *
 * @see atg.portal.gear.soapclient.taglib.InvokeSOAPServiceTag
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/SOAPServiceTagResult.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPServiceTagResult
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/SOAPServiceTagResult.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  
  
  //---------------------------------------------------------------------
  // property: result
  Object mResult;

  /**
   * The result object that represents a returned value from a SOAP
   * service invocation
   * @return method return value
   */
  public Object getResult() {
    return mResult;
  }

  /**
   * Set the result property.
   * @param pResult
   */
  public void setResult(Object pResult) {
    mResult = pResult;
  }

  List mErrorObjects;
  //-------------------------------------
  /**
   * Sets the collection of error Objects in the result
   **/
  public void setErrorObjects(List pErrorObjects) {
    mErrorObjects = pErrorObjects;
  }

  //-------------------------------------
  /**
   * Returns the collection of error Objects in the result
   **/
  public List getErrorObjects() {
    return mErrorObjects;
  }

  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  public SOAPServiceTagResult(){
  }
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * Return true if this tag result object contains error codes.
   *
   * @return true if there are errors
   */
  public boolean hasError() {
    if (getErrorObjects() != null &&
        getErrorObjects().size() > 0) {
      return true;
    }
    else {
      return false;
    }
  }

  public void addErrorObject(String pErrorCode, String pErrorMessage, Object[] pErrorParams)
  {
    
    ErrorObject errObj = new ErrorObject();
    errObj.setErrorCode(pErrorCode);
    errObj.setMessage(pErrorMessage);
    errObj.setParameterValues(pErrorParams);
    if (getErrorObjects() == null ) {
      setErrorObjects(new ArrayList(11));
    } // end of if ()
    
    getErrorObjects().add(errObj);
  }
}   // end of class

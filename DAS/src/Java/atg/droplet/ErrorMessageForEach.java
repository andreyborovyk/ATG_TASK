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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.droplet;

import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;
import atg.core.util.StringUtils;

import atg.naming.NameContext;

/**
 * The servlet takes a list of exceptions and renders the text of 
 * an error message for each exception, by translating the message and 
 * propertyName properties of the exception.
 * <p>
 * The list of exceptions can be specified as an input parameter in one of two 
 * ways.  If you do not define a source for exceptions, the list of all 
 * exceptions that have occurred in this request is used.  
 * <p>
 * Otherwise, you can specify a Vector of 
 * ServletException s as the exceptions parameter.
 * For example, you'd add this parameter to the invocation
 * of this servlet in a jhtml file:
 * <pre>
 *     &lt;param name="exceptions" value="bean:MyFormHandler.formExceptions"&gt;
 * </pre>
 * <p>
 * The exceptions are translated into error messages using a pair of mapping 
 * tables.  One keys off
 * of the <code>errorCode</code> property of the exception, the other keys off
 * of its <code>propertyName</code>.  The tables supply specific errors 
 * to display for each exception that are likely to be encountered.  
 * If no entry is provided for a errorCode or a propertyName, the original
 * message property in the exception is printed.
 * <p>The mapping tables are created by taking the union of two different
 * dictionaries.  The default dictionary is defined with the propertyNameTable 
 * and messageTable bean properties of this servlet.  The other dictionary is 
 * taken from a the propertyNameTable and messageTable request parameters
 * passed to the servlet. 
 * <p>
 * Each exception can either have an entry in the message table, the property
 * name table or both.  If both entries are present, the "message" table entry
 * is displayed but it can use the sequence "param:propertyName" to 
 * substitute the contents of the "propertyName" table entry.
 * <p>
 * This servlet takes the same parameters as the servlet atg.droplet.ForEach
 * to control how the exceptions are displayed.  Each time that the
 * output parameter is rendered, this servlet sets "message" and
 * "propertyName" parameters which contain the translated values.
 * <p>
 * <p>Note that propertyName is only associated with DropletFormExceptions -
 * those exceptions that are generated in a failure to set the value of
 * a bean property.  For ServletExceptions, the propertyName entry is not
 * set.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ErrorMessageForEach.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ErrorMessageForEach extends ForEach {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ErrorMessageForEach.java#2 $$Change: 651448 $";

  final static String PROPERTY_NAME = "propertyName";
  final static String MESSAGE = "message";
  final static String PROPERTY_NAME_TABLE = "propertyNameTable";
  final static String MESSAGE_TABLE = "messageTable";

  public ErrorMessageForEach () {
  }

  //--------- Property: ResourceName -----------
  String mResourceName;
  /**
   * Sets the property ResourceName.
   */
  public void setResourceName(String pResourceName) {
    mResourceName = pResourceName;
  }
  /**
   * @return The value of the property ResourceName.
   */
  public String getResourceName() {
    return mResourceName;
  }

  //--------- Property: MessageTable -----------
  Properties mMessageTable;
  /**
   * Sets the property MessageTable.  This Properties object contains a 
   * translation from the message field of the exception to a text message to 
   * be displayed.
   */
  public void setMessageTable(Properties pMessageTable) {
    mMessageTable = pMessageTable;
  }
  /**
   * @return The value of the property MessageTable.
   */
  public Properties getMessageTable() {
    return mMessageTable;
  }

  //--------- Property: PropertyNameTable -----------
  Properties mPropertyNameTable;
  /**
   * Sets the property PropertyNameTable.  This Properties object defines
   * a translation from the propertyName in a DropletFormException to 
   * the text description of that property as it is to be displayed to
   * the user.
   */
  public void setPropertyNameTable(Properties pPropertyNameTable) {
    mPropertyNameTable = pPropertyNameTable;
  }
  /**
   * @return The value of the property PropertyNameTable.
   */
  public Properties getPropertyNameTable() {
    return mPropertyNameTable;
  }

  static final Object [] sEmptyArray = new Object[0];

  /**
   * Returns the Vector of exceptions to display.
   **/
  public Object getArray(DynamoHttpServletRequest pReq) {
    Object obj = pReq.getObjectParameter("exceptions");
    Vector exceptions; 
    if (obj == null) {
      NameContext ctx = pReq.getRequestScope();
      if (ctx == null) return sEmptyArray;
      exceptions = (Vector) ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
      /* Must return an empty array to avoid an error */
      if (exceptions == null) return sEmptyArray;
    }
    else if (obj instanceof Vector) {
      exceptions = (Vector) obj;
    }
    else if (obj instanceof Collection) {
      Collection collItems = (Collection)obj;
      Iterator iterItems = collItems.iterator();
      Vector vecCopy = new Vector(collItems.size());
      while (iterItems.hasNext()) {
        vecCopy.add(iterItems.next());
      }
      return vecCopy;
    }
    else if (obj instanceof ServletException) {
      exceptions = new Vector();
      exceptions.addElement(obj);
    }
    else {
      if (isLoggingError())
        logError("formHandler parameter = " + obj + " is not of type GenericFormHandler");
      exceptions = null;
    }
    return exceptions;
  }

  /**
   * In this method, we extract the message and propertyName values that
   * we want to render in the request first mapping them through the
   * tables provided.
   */
  protected void setElementParameter(DynamoHttpServletRequest pReq, 
  				     String pElementName, Object pValue) {
    String errorCode, message, propertyName = null;
    ResourceBundle errors = null;
    
    if (mResourceName != null) {
      Locale locale;
      if (pReq.getRequestLocale() == null) locale = Locale.getDefault();
      else locale = pReq.getRequestLocale().getLocale();
      errors = ResourceBundle.getBundle(mResourceName, locale);
    }

    if (pValue instanceof DropletFormException) {
      DropletFormException df = (DropletFormException) pValue;
      propertyName = df.getPropertyName();
      Dictionary propertyNameTable = null;
      try {
        propertyNameTable = ServletUtil.convertStringToDictionary(pReq,
      					pReq.getParameter(PROPERTY_NAME_TABLE));
      }
      catch (ServletException exc) {
        if (isLoggingError())
	  logError("can't get value of " + PROPERTY_NAME_TABLE + " parameter: " + exc);
      }
      propertyName = getUnionOf(propertyName, propertyName, 
      				errors, propertyNameTable, mPropertyNameTable);
      pReq.setParameter(PROPERTY_NAME, propertyName);
    }
    else 
      pReq.setParameter(PROPERTY_NAME, null);

    if (pValue instanceof ServletException) {
      if (pValue instanceof DropletException) {
	errorCode = ((DropletException) pValue).getErrorCode();
	message = ((DropletException) pValue).getMessage();
      }
      else {
	errorCode = "ServletException";
	message = ((ServletException) pValue).getMessage();
      }
      Dictionary messageTable = null; 
      try {
	messageTable = ServletUtil.convertStringToDictionary(pReq,	
					    pReq.getParameter(MESSAGE_TABLE));
      }
      catch (ServletException exc) {
	if (isLoggingError())
	  logError("can't get value of " + MESSAGE_TABLE + " parameter: " + exc);
      }
      message = getUnionOf(message, errorCode, errors, messageTable, mMessageTable);
      if (propertyName != null)
	message = StringUtils.replace(message, "param:propertyName", propertyName);
      pReq.setParameter(MESSAGE, message);
    }
  }

  /**
   * Returns the value after it has been mapped through both of the
   * dictionaries.  Either of the dictionaries may be null and the
   * neither may contain an entry for the value.  In this case, the 
   * value itself is returned.
   */
  String getUnionOf(String pDefaultValue,
  		    String pValue, ResourceBundle pRB,
		    Dictionary pDict1, Dictionary pDict2) {
    String tmp = null; 

    /* No key, use the default value */
    if (pValue == null) return pDefaultValue;

    if (pDict1 != null) tmp = (String) pDict1.get(pValue);
    if (tmp == null && pDict2 != null) tmp = (String) pDict2.get(pValue);
    try {
      if (tmp == null && (pRB != null)) 
        tmp = pRB.getString(pValue);
    }
    /* Ignore these since the value itself is a reasonable alternative */
    catch (MissingResourceException exc) {}
    if (tmp != null) return tmp;
    else return pDefaultValue;
  }
}

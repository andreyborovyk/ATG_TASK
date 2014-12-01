/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.commerce.catalog;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.IOException;
import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.core.util.ResourceUtils;


/**
 * This droplet is capable of returning a string which is the concatentation of all
 * the displayable properties for a sku.  By default the properties for a particular
 * Sku are defined on a product level, since this allows saving storage space.  The droplet
 * obtains all the necessary information via parameters and then renders the output parameter
 * after setting the <code>displayElement</code> parameter.  This droplet can take the following
 * parameters.
 *
 * <P>
 *
 * <UL>
 *    <LI> sku - the sku object.  This is used to obtain the various properties from
 *    <LI> product - the product object. contains information on a skus displayable property
 *    <LI> delimiter - in the string returned, which is the conatentation of all a skus displayable
 *    properties, each property will be separated by the delimiter.  This will default to a space.
 *    <LI> displayElementName - the name of the parameter that should be set on output.
 * </UL>
 *
 * <P>
 *
 * The following output parameters can be set:
 *
 * <UL>
 *    <LI> output - rendered with the displayElement parameter set
 * </UL>
 *
 *
 * <P>
 *
 * Alternatively, the list of properties to extract from a sku can be obtained by setting
 * the propertyList parameter to a comma separated list.  The list should be in the form of
 * size,color etc.  If this parameter is set, then there is no need to set the product property.
 *
 * <P>
 *
 * If an error is encountered while obtaining either a property from the sku object, or a necessary
 * parameter is not supplied (sku object etc.) then the output parameter will be set to null and the
 * output parameter will be renedered
 *
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/DisplaySkuProperties.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class DisplaySkuProperties
    extends DynamoServlet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/DisplaySkuProperties.java#2 $$Change: 651448 $";


    //--------------------------------------------------
    // Constants

    static final ParameterName DISPLAY_ELEMENT_NAME = ParameterName.getParameterName("displayElementName");
    static final ParameterName SKU = ParameterName.getParameterName("sku");
    static final ParameterName PRODUCT = ParameterName.getParameterName("product");
    static final ParameterName DELIMITER = ParameterName.getParameterName("delimiter");
    static final ParameterName PROPERTY_LIST = ParameterName.getParameterName("propertyList");
    static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    public static final String DISPLAY_ELEMENT = "displayElement";
    public static final String SPACE_CHARACTER = " ";
    public static final String TOKENIZER_CHARACTER = ",";

    static final String MY_RESOURCE_NAME = "atg.commerce.catalog.UserResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

    //--------------------------------------------------
    // Member Variables

    //--------------------------------------------------
    // Properties

    //----------------------------
    // property: Delimiter

    /** Displayable SkuAttributes is the property that contains for a given sku what properties
     * should be displayed in a drop down list
     */
    String mDisplayableSkuAttributesProperty;

    /**
     * Set the property in the product that contains a list of properties to extract from the sku.
     *
     * @param pDisplayableSkuAttributesProperty the property name
     */
    public void setDisplayableSkuAttributesProperty(String pDisplayableSkuAttributesProperty) {
  mDisplayableSkuAttributesProperty = pDisplayableSkuAttributesProperty;
    }

    /**
     * Return the name of the property in the product from which Sku properties for display
     * are obtained.
     *
     * @return the property name
     */
    public String getDisplayableSkuAttributesProperty() {
  return mDisplayableSkuAttributesProperty;
    }


    //--------------------------------------------------
    // Constructors

    //--------------------------------------------------
    // Methods

    /**
     * This method obtains the Sku object from the DynamoHttpServletRequest
     * object.  If the user needs to override where the sku object is obtained
     * this should can be done here.
     *
     * @param pRequest a value of type 'DynamoHttpServletRequest'
     * @return the sku object
     */
    public RepositoryItem getSku(DynamoHttpServletRequest pRequest) {
  return (RepositoryItem)pRequest.getObjectParameter(SKU);
    }

    /**
     * This method obtains the Product object from the DynamoHttpServletResponse
     * object.  If the user needs to override where the product object is
     * obtained this should be done here.
     *
     * @param pRequest a value of type 'DynamoHttpServletRequest'
     * @return the product object
     */
    public RepositoryItem getProduct(DynamoHttpServletRequest pRequest) {
  return (RepositoryItem)pRequest.getObjectParameter(PRODUCT);
    }

    /**
     * This method is actually responsible for obtaining the values and concatenating
     * the string together that will be rendered on output.  A List is obtained from the
     * product property that contains displayableSkuAttributes and then is iterated through
     * obtaining values for each named property.  These values are then appended onto each
     * other, using the delimiter as the separating token.
     *
     * @param pProduct the product object which contains the properties to obtain from the sku
     * @param pDelimiter token to separate the values from the sku
     * @return the string that will be rendered on output
     */
    protected String getSkuPropertyValues(RepositoryItem pSku,
           List  pProperties,
           String pDelimiter)
    {
  StringBuffer outputString = new StringBuffer();          // the output string
  String currentProperty;
  // cache the number of properties that we have and then iterate through the list
  // adding them onto our string
  int numSkuAttributes = pProperties.size();

  if (isLoggingDebug()) {
      logDebug("There are " + numSkuAttributes + " to display");
  }

  for (int index=0; index<numSkuAttributes; index++) {
      currentProperty = (String)pProperties.get(index);
      try {
    if (pSku.getItemDescriptor().hasProperty(currentProperty)) {
        outputString.append(pSku.getPropertyValue((String)pProperties.get(index)));
        if (index != numSkuAttributes)
      outputString.append(pDelimiter);
    }
      } catch (RepositoryException re) {
    if (isLoggingError())
        logError(re);
      }
  }
  return outputString.toString();
    }

    /**
     * This method is responsible for determining the concatenation of a group
     * of strings from a particular Sku object.  These strings are the various properties
     * that are associated with a sku.  One example of this might be obtaining the
     * color, size and display name for a Sku and showing this to the user in a drop down
     * menu.  This string is then set to the parameter as named by the displayElementName.
     * The user then has the ability to render this as need be.
     *
     * <P>
     *
     * The order that the properties are displayed in equates to the order of the properties
     * that are obtained from the displayableSkuAttributes property in the product.
     *
     * @param pRequest a value of type 'DynamoHttpServletRequest'
     * @param pResponse a value of type 'DynamoHttpServletResponse'
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs
     */
    public void service(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException,
         IOException
    {
  RepositoryItem SkuObject = getSku(pRequest);             // the Sku object
  RepositoryItem ProductObject = getProduct(pRequest);     // the Product object
  List propertyList = new ArrayList();                     // the list of properties to extract

  String displayElement = pRequest.getParameter(DISPLAY_ELEMENT_NAME);
  if (displayElement == null)
      displayElement = DISPLAY_ELEMENT;

  // get the delimiter char
  String delimiter = pRequest.getParameter(DELIMITER);
  if (delimiter == null)
      delimiter = SPACE_CHARACTER;

  String properties = pRequest.getParameter(PROPERTY_LIST);

  // if we don't know where to get the sku display property from, error and exit
  if (properties == null && mDisplayableSkuAttributesProperty == null) {
      if (isLoggingError()) {
    logError(ResourceUtils.getMsgResource("NO_SKU_PROPERTY_DEFINED", MY_RESOURCE_NAME, sResourceBundle));
      }
      // set the params and render the output
      pRequest.setParameter(displayElement, null);
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      return;
  }

  if (SkuObject == null) {
      if (isLoggingError()) {
    logError(ResourceUtils.getMsgResource("NO_SKU", MY_RESOURCE_NAME, sResourceBundle));
      }
      // set the params and render the output
      pRequest.setParameter(displayElement, null);
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      return;
  }


  if (properties == null) {
      // value is null, so ensure we have the product object so we can obtain the
      // list of values from it.
      if (ProductObject == null) {
    if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource("NO_PRODUCT_PROPERTY", MY_RESOURCE_NAME, sResourceBundle));
    }
    // set the params and render the output
    pRequest.setParameter(displayElement, null);
    pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    return;
      }

      try {
    if (ProductObject.getItemDescriptor().hasProperty(mDisplayableSkuAttributesProperty)) {
        // now get the list of properties that will be extracted from the Sku
        propertyList = (List)ProductObject.getPropertyValue(mDisplayableSkuAttributesProperty);

    } else {
        pRequest.setParameter(displayElement, null);
        pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        return;
    }
      } catch (RepositoryException re) {
    if (isLoggingError())
        logError(re);
      }
  } else {
      // tokenize the properties string using commas.  create a list from the resulting string
      // and use this.
      StringTokenizer st = new StringTokenizer(properties, TOKENIZER_CHARACTER);
      while (st.hasMoreTokens()) {
    propertyList.add(st.nextToken());
      }
  }

  // set the params and render the output
  pRequest.setParameter(displayElement, getSkuPropertyValues(SkuObject, propertyList, delimiter));
  pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

}   // end of class









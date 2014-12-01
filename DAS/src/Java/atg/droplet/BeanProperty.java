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

package atg.droplet;

import java.beans.*;
import java.io.*;
import java.lang.reflect.*;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;
import atg.beans.*;
import atg.core.util.ResourceUtils;

/**
 * This servlet gets or sets a property in a bean. The bean is defined
 * by the bean parameter. The property to get or set is defined by the
 * propertyName parameter.  The value may be set or read using the
 * propertyValue parameter.  In either case, the droplet renders an
 * oparam named output.
 * <p>
 * <i>Examples:</i>
 * <p>
 * To set the property value, specify the new value using the propertyValue
 * parameter, as in this example:
 * <pre><tt>
 * &lt;droplet bean="/atg/dynamo/droplet/BeanProperty"&gt;
 *   &lt;param name="bean" value="param:myBean"&gt;
 *   &lt;param name="propertyName" value="color"&gt;
 *   &lt;param name="propertyValue" value="red"&gt;
 * &lt;/droplet&gt;
 * </tt></pre>
 * When setting property values the oparam need not be used, since there's
 * no need to render output within the droplet's context.
 * <p>
 * To get the property value, leave the propertyValue parameter unset.  The
 * parameter will then take on the current value within the output oparam,
 * as in this example:
 * <pre><tt>
 * &lt;droplet bean="/atg/dynamo/droplet/BeanProperty"&gt;
 *   &lt;param name="bean" value="param:myBean"&gt;
 *   &lt;param name="propertyName" value="color"&gt;
 *   &lt;oparam name="output"&gt;
 *     The bean's color is &lt;valueof param="propertyValue"&gt;unset&lt;/valueof&gt;.
 *   &lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </tt></pre>
 * Dotted subproperty expressions of the form "a.b.c" are permitted when
 * getting a property value, though not when setting one.  When getting a
 * value using subproperty expressions, if any intermediate property is
 * null then propertyValue will also be null, as is the case when using
 * {@link atg.beans.DynamicBeans#getSubPropertyValue DynamicBeans}.
 * <p>
 *
 * @author Andrew Rickard, Matt Landau
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/BeanProperty.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.beans.DynamicBeans
 **/

public 
class BeanProperty extends DynamoServlet
{
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/BeanProperty.java#2 $$Change: 651448 $";
    
    //-------------------------------------
    // Constants
    //-------------------------------------
    public final static String BEAN        = "bean";
    public final static String PROPERTY    = "propertyName";
    public final static String VALUE       = "propertyValue";

    public final static String OUTPUT      = "output";
    //-------------------------------------
    // Member Variables
    //-------------------------------------
    protected final static String BEANRESOURCES = "atg.droplet.BeanServletResources";
    protected static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(BEANRESOURCES, atg.service.dynamo.LangLicense.getLicensedDefault());

    //-------------------------------------
    // Properties
    //-------------------------------------
    
    //-------------------------------------
    // Constructors
    //-------------------------------------
    
    /**
     * Constructs an instanceof BeanProperty
     */
    public BeanProperty() {
    }
    
    /**
     * Service the servlets request. If the input indicates a get request
     * the serviceGet method is  called if the input indicates a set request
     * then the serviceSet method is called.
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     */
    public void service(DynamoHttpServletRequest pRequest, 
                        DynamoHttpServletResponse pResponse) 
        throws ServletException, IOException {
        Object bean     = null;
        String property = null;
        Object value    = null;
        Object iParam   = null;
        
        //Get the bean param
        if((iParam = pRequest.getLocalParameter(BEAN)) != null) {
            bean = iParam;
        } else {
            if(isLoggingError())
                logError(ResourceUtils.getMsgResource("noBean", BEANRESOURCES, sResourceBundle));
        }
        
        //Get the property param
        if((iParam = pRequest.getLocalParameter(PROPERTY)) != null) {
            if(iParam instanceof String) {
                property = (String) iParam;
            } else {
                if(isLoggingError())
                    logError(ResourceUtils.getMsgResource("illegalProperty", BEANRESOURCES, sResourceBundle));
            }
        } else {
            if(isLoggingError())
                logError(ResourceUtils.getMsgResource("noProperty", BEANRESOURCES, sResourceBundle));
        }

        //Get the value param
        if((iParam = pRequest.getLocalParameter(VALUE)) != null) {
            value = iParam;
        }

        //This must be a Set
        if((bean != null) && (property != null) && (value != null)) {
            serviceSet(pRequest,pResponse,bean,property,value);

            //Service the output oparam
            pRequest.serviceLocalParameter(OUTPUT,pRequest,pResponse);
            
        } else if((bean != null) && (property != null) && (value == null)) {
            //Must be a get
            serviceGet(pRequest,pResponse,bean,property,value);
        
            //Service the output oparam
            pRequest.serviceLocalParameter(OUTPUT,pRequest,pResponse);      
        }
    }
    

    /**
     * Handles a request to get a property
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @param pBean the bean to operate on
     * @param pProperty the property to get
     * @param pValue the value of this property
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     */
    protected void serviceGet(DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse,
                              Object pBean,
                              String pProperty,
                              Object pValue) 
        throws ServletException, IOException {

      try
      {
        Object value = DynamicBeans.getSubPropertyValue(pBean, pProperty);
        pRequest.setParameter(VALUE, value);
        if(isLoggingDebug())
          logDebug("Value = " + pValue);
      }
      catch (PropertyNotFoundException e)
      {
        pRequest.setParameter(VALUE, null);
        if(isLoggingError())
          logError(e);
      }
    } 

  
    /**
     * handles a request to set a property
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @param pBean the bean to operate on
     * @param pProperty the property to set
     * @param pValue the value to set this property to
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     */
    protected void serviceSet(DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse,
                              Object pBean,
                              String pProperty,
                              Object pValue) 
        throws ServletException, IOException {
        
      try
      {
        // FIXME: handle sub-properties
        DynamicBeans.setPropertyValue(pBean, pProperty, pValue);
        if(isLoggingDebug())
          logDebug("Set value to " + pValue);
      }
      catch (PropertyNotFoundException e)
      {
        if(isLoggingError())
          logError(e);
      }
    } 
}

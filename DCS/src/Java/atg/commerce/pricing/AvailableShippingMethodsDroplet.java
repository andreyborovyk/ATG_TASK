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

package atg.commerce.pricing;

import atg.servlet.*;
import atg.nucleus.naming.ParameterName;
import atg.commerce.order.ShippingGroup;
import atg.repository.RepositoryItem;
import atg.nucleus.naming.ComponentName;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;

import java.util.List;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import atg.core.util.ResourceUtils;

/**
 * This Dynamo Servlet Bean is used to display avalable shipping methods for
 * a particular shipping group.  The class's service method calls into the
 * ShippingPricingEngine <code>getAvailableMethods</code> method to return
 * the list of shipping method codes. These codes correspond to the
 * <code>shippingMethod</code> property of the order HardgoodShippingGroup
 * class.
 *
 * <P>
 *
 * The only required input parameter is:
 * <DL>
 * <DT>shippingGroup
 * <DD>The ShippingGroup that needs to be shipped
 * </DL>
 *
 * <P>
 *
 * The following parameters are optional:
 * <DL>
 * <DT>pricingModels
 * <DD>A collection of shipping pricing models. If this parameter is null then the
 * session-scoped PricingModelHolder is resolved and the collection is fetched. The path to
 * the PricingModelHolder component is configured through the <code>userPricingModelsPath</code>
 * property.
 * <DT>profile
 * <DD>The RepositoryItem which represents the user requesting the shipping methods. If this
 * parameter is null, then the session-scoped Profile is resolved. The path to the Profile 
 * component is configured through the <code>profilePath</code> property.
 * <DT>locale
 * <DD>The locale of the user requesting the shipping methods. This parameter may be either
 * a java.util.Locale object or a String which represents a locale. If this parameter is
 * not found then by default the locale is fetched from the request. If this locale cannot
 * be determined then the default locale for this component is utilized.
 * </DL>
 *
 * <P>
 *
 * The following output parameters are defined when the service method is invoked:
 * <DL>
 * <DT>availableShippingMethods
 * <DD>A list of shipping method codes which can be used as a <code>shippingMethod</code> 
 * value in a HardgoodShippingGroup
 * <DT>output
 * <DD>An oparam which will include the availableShippingMethods parameter
 * </DL>
 * 
 * <P>
 *
 * This is an example of using this droplet to provide a select box of
 * available shipping methods which are bound to the
 * <code>shippingMethod</code> property of the first shipping group.
 *
 * <P>
 *
 * <PRE>
 * &lt;droplet bean="/atg/commerce/pricing/AvailableShippingMethods"&gt;
 * &lt;param name="shippingGroup" value="bean:ShoppingCartModifier.shippingGroup"&gt;
 * &lt;oparam name="output"&gt;
 *   &lt;select bean="ShoppingCartModifier.shippingGroup.shippingMethod"&gt;
 *   &lt;droplet bean="ForEach"&gt;
 *   &lt;param name="array" value="param:availableShippingMethods"&gt;
 *   &lt;param name="elementName" value="method"&gt;
 *   &lt;oparam name="output"&gt;
 *     &lt;option value="param:method"&gt;&lt;valueof param="method"&gt;&lt;/valueof&gt;
 *   &lt;/oparam&gt;
 *   &lt;/droplet&gt;
 *   &lt;/select&gt;
 * &lt;/oparam&gt;
 * &lt;/droplet&gt;
 *
 * </PRE>
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/AvailableShippingMethodsDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
  class AvailableShippingMethodsDroplet
  extends DynamoServlet
  {
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/AvailableShippingMethodsDroplet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
    private static final String PERFORM_MONITOR_NAME="AvailableShippingMethodsDroplet";
    static final ParameterName SHIPPING_GROUP_PARAM = ParameterName.getParameterName("shippingGroup");
    static final ParameterName PRICING_MODELS_PARAM = ParameterName.getParameterName("pricingModels");
    static final ParameterName PROFILE_PARAM = ParameterName.getParameterName("profile");
    static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

    static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    static final String AVAILABLE_SHIPPING_METHODS = "availableShippingMethods";
    static final String MY_RESOURCE_NAME = "atg.commerce.pricing.Resources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle =
    java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

    

    //-------------------------------------
    // property: ShippingPricingEngine
    /** the shipping pricing engine to consult for available shipping methods */
    ShippingPricingEngine mShippingPricingEngine;

    /**
     * the shipping pricing engine to consult for available shipping methods
     * @param pShippingPricingEngine new value to set
     */
    public void setShippingPricingEngine(ShippingPricingEngine pShippingPricingEngine)
    {mShippingPricingEngine = pShippingPricingEngine;}

    /**
     * the shipping pricing engine to consult for available shipping methods
     * @return property ShippingPricingEngine
     */
    public ShippingPricingEngine getShippingPricingEngine()
    {return mShippingPricingEngine;}
    
    

    //-------------------------------------
    // property: ProfilePath
    /** the path to the user's profile in the nucleus namespace */
    protected ComponentName mProfilePath;

    /**
     * the path to the user's profile in the nucleus namespace
     * @param pProfilePath new value to set
     */
    public void setProfilePath(String pProfilePath){
      if (pProfilePath != null)
	mProfilePath = ComponentName.getComponentName(pProfilePath);
      else
	mProfilePath = null;
    }
  
    /**
     * the path to the user's profile in the nucleus namespace
     * @return property ProfilePath
     */
    public String getProfilePath(){
      if (mProfilePath != null)
	return mProfilePath.getName();
      else
	return null;
    }
  
  

    //-------------------------------------
    // property: UserPricingModelsPath
    /** the path to the PricingModelHolder in Nucleus which holds the user's pricing models */
    protected ComponentName mUserPricingModelsPath;

    /**
     * the path to the PricingModelHolder in Nucleus which holds the user's pricing models
     * @param pUserPricingModelsPath new value to set
     */
    public void setUserPricingModelsPath(String pUserPricingModelsPath){
      if (pUserPricingModelsPath != null)
	mUserPricingModelsPath = ComponentName.getComponentName(pUserPricingModelsPath);
      else
	mUserPricingModelsPath = null;
    }
  
    /**
     * the path to the PricingModelHolder in Nucleus which holds the user's pricing models
     * @return property UserPricingModelsPath
     */
    public String getUserPricingModelsPath(){
      if (mUserPricingModelsPath != null)
	return mUserPricingModelsPath.getName();
      else
	return null;
    }
    

    //-------------------------------------
    // property: DefaultLocale
    /** the default locale for which available shipping methods should be retrieved */
    Locale mDefaultLocale;

    /**
     * the default locale for which available shipping methods should be retrieved
     * @param pDefaultLocale new value to set
     */
    public void setDefaultLocale(Locale pDefaultLocale)
    {mDefaultLocale = pDefaultLocale;}

    /**
     * the default locale for which available shipping methods should be retrieved
     * @return property DefaultLocale
     */
    public Locale getDefaultLocale()
    {return mDefaultLocale;}
    


    //-------------------------------------
    // property: UseRequestLocale
    /** flag to determine whether to ask for available shipping methods using the locale in the Request object before falling back on the default locale */
    boolean mUseRequestLocale;

    /**
     * flag to determine whether to ask for available shipping methods using the locale in the Request object before falling back on the default locale
     * @param pUseRequestLocale new value to set
     */
    public void setUseRequestLocale(boolean pUseRequestLocale)
    {mUseRequestLocale = pUseRequestLocale;}

    /**
     * flag to determine whether to ask for available shipping methods using the locale in the Request object before falling back on the default locale
     * @return property UseRequestLocale
     */
    public boolean getUseRequestLocale()
    {return mUseRequestLocale;}

    /**
     * Test property UseRequestLocale
     * @return UseRequestLocale
     */
    public boolean isUseRequestLocale()
    {return mUseRequestLocale;}
    
  
    //-------------------------------------
    // Constructors
    //-------------------------------------

  /**
   * Constructs an instanceof AvailableShippingMethodsDroplet
   */
    public AvailableShippingMethodsDroplet() {
    }

  /**
   * Performs the getting of available shipping methods within a Request scope.
   *
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException an application specific error occurred 
   * processing this request
   * @exception IOException an error occurred reading data from the request
   * or writing data to the response.
   */
    public void service(DynamoHttpServletRequest pRequest, 
			DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
    {

      String perfName = "service";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;
    
      try
      {

            List availableMethods = null;

            if (getShippingPricingEngine() != null)
            {
                ShippingGroup shippingGroup = getShippingGroup(pRequest,
                        pResponse);
                Collection pricingModels = getPricingModels(pRequest, pResponse);
                RepositoryItem profile = getProfile(pRequest, pResponse);
                Locale locale = getUserLocale(pRequest, pResponse);
                if (shippingGroup != null)
                {
                    try
                    {
                        availableMethods = getShippingPricingEngine()
                                .getAvailableMethods(shippingGroup,
                                        pricingModels, locale, profile, null);
                    }
                    catch (PricingException exc)
                    {
                        try
                        {
                            if (!perfCancelled)
                            {
                                PerformanceMonitor.cancelOperation(
                                        PERFORM_MONITOR_NAME, perfName);
                                perfCancelled = true;
                            }
                        }
                        catch (PerfStackMismatchException psm)
                        {
                            if (isLoggingWarning())
                                logWarning(psm);
                        }

                        if (isLoggingError())
                            logError(exc);
                    }
                }
                else
                {
                    String args[] = { SHIPPING_GROUP_PARAM.toString() };
                    if (isLoggingError())
                        logError(ResourceUtils.getMsgResource(
                                "missingRequiredInputParam", MY_RESOURCE_NAME,
                                sResourceBundle, args));

                }
            }
            pRequest.setParameter(AVAILABLE_SHIPPING_METHODS, availableMethods);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

       }
       finally
       {
            try
            {
                if (!perfCancelled)
                {
                    PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME,
                            perfName);
                    perfCancelled = true;
                }
            }
            catch (PerfStackMismatchException e)
            {
                if (isLoggingWarning())
                {
                    logWarning(e);
                }
            }
        }// end finally

    }

    /**
     * Get the shipping group from which we should determine the available shipping methods
     * @param pRequest the request to be processed
     * @param pResponse the response object for this request
     * @return the shipping group from which we should determine the available shipping methods
     * @exception ServletException an application specific error occurred 
     * processing this request
     * @exception IOException an error occurred reading data from the request
     * or writing data to the response.
     */
    protected ShippingGroup getShippingGroup(DynamoHttpServletRequest pRequest,
					     DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
    {
      String perfName = "getShippingGroup";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;
    
      try {
        return (ShippingGroup)pRequest.getObjectParameter(SHIPPING_GROUP_PARAM);
      }
      catch(ClassCastException exc)
      {
        String args[] = { SHIPPING_GROUP_PARAM.toString() };
        if(isLoggingError())
            logError(ResourceUtils.getMsgResource("invalidInputParam", MY_RESOURCE_NAME, sResourceBundle, args));
        return null;
      }
      finally {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }// end finally

    }


    /**
     * get the profile of the user requesting the shipping information
     * @param pRequest the request to be processed
     * @param pResponse the response object for this request
     * @return the profile of the user requesting the shipping information
     * @exception ServletException an application specific error occurred 
     * processing this request
     * @exception IOException an error occurred reading data from the request
     * or writing data to the response.
     */
    protected RepositoryItem getProfile(DynamoHttpServletRequest pRequest,
					DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
    {
      String perfName = "getProfile";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

	RepositoryItem profile = (RepositoryItem)pRequest.getObjectParameter(PROFILE_PARAM);
	if ((profile == null) && (mProfilePath != null))
	  profile = (RepositoryItem)pRequest.resolveName(mProfilePath);
	return profile;


      }
      catch(ClassCastException exc)
      {
        String args[] = { PROFILE_PARAM.toString() };
        if(isLoggingError())
            logError(ResourceUtils.getMsgResource("invalidInputParam", MY_RESOURCE_NAME, sResourceBundle, args));
        return null;
      }
      finally {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }// end finally
    }

    /**
     * Get the collection of pricing models to use for determing what shipping methods are availble.
     * @param pRequest the request to be processed
     * @param pResponse the response object for this request
     * @return the collection of pricing models to use for determing what shipping methods are availble.
     * @exception ServletException an application specific error occurred 
     * processing this request
     * @exception IOException an error occurred reading data from the request
     * or writing data to the response.
     */
    protected Collection getPricingModels(DynamoHttpServletRequest pRequest,
					  DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
    {
      String perfName = "getPricingModels";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;
    
      try {
	Collection models = (Collection)pRequest.getObjectParameter(PRICING_MODELS_PARAM);
	if ((models == null) && (mUserPricingModelsPath != null)) {
	  PricingModelHolder holder = (PricingModelHolder)pRequest.resolveName(mUserPricingModelsPath);
	  if (holder != null)
	    models = holder.getShippingPricingModels();
	}
	return models;
      }
      catch(ClassCastException exc)
      {
        String args[] = { PRICING_MODELS_PARAM.toString() };
        if(isLoggingError())
            logError(ResourceUtils.getMsgResource("invalidInputParam", MY_RESOURCE_NAME, sResourceBundle, args));
        return null;
      }
      finally {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }// end finally

    }

    /**
     * Returns the locale associated with the request. The method first searches
     * for a request paramater named <code>locale</code>. This value can be
     * either a java.util.Locale object or a String which represents the locale.
     * Next if the <code>useRequestLocale</code> property is true, then the locale
     * of the request will be returned. Finally, if the locale cannot be determined,
     * the the <code>defaultLocale</code> property is used.
     *
     * @param pRequest the request to be processed
     * @param pResponse the response object for this request
     * @return the locale to be associated with this user
     * @exception ServletException an application specific error occurred 
     * processing this request
     * @exception IOException an error occurred reading data from the request
     * or writing data to the response.
     */
    protected Locale getUserLocale(DynamoHttpServletRequest pRequest,
				   DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
    {
      String perfName = "getUserLocale";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;
    
      try {
	Object obj = pRequest.getObjectParameter(LOCALE_PARAM);
	if (obj instanceof Locale)
	  return (Locale)obj;
	else if (obj instanceof String)
	  return RequestLocale.getCachedLocale((String)obj);
	else if (isUseRequestLocale()) {
	  RequestLocale requestLocale = pRequest.getRequestLocale();
	  if (requestLocale != null)
	    return requestLocale.getLocale();
	}

	return getDefaultLocale();
      }
      finally {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }// end finally
    }


  } // end of class



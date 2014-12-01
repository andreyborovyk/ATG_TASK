/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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
  package atg.projects.store.pricing;
  
  import java.io.IOException;
  import java.util.Locale;
  
  import javax.servlet.ServletException;
  
  import atg.commerce.order.Order;
  import atg.commerce.order.ShippingGroup;
  import atg.commerce.pricing.PricingException;
  import atg.commerce.pricing.PricingModelHolder;
  import atg.commerce.pricing.ShippingPriceInfo;
  import atg.commerce.pricing.ShippingPricingEngine;
  import atg.core.util.ResourceUtils;
  import atg.core.util.StringUtils;
  import atg.nucleus.naming.ParameterName;
  import atg.service.perfmonitor.PerfStackMismatchException;
  import atg.service.perfmonitor.PerformanceMonitor;
  import atg.servlet.DynamoHttpServletRequest;
  import atg.servlet.DynamoHttpServletResponse;
  import atg.servlet.DynamoServlet;
  import atg.servlet.RequestLocale;
  import atg.userprofiling.Profile;
  
  /**
   * This Dynamo Servlet Bean is used to determine shipping costs for the shipping group 
   * with specified shipping method. The class's service method calls into the
   * ShippingPricingEngine <code>priceShippingGroup</code> method to get the ShippingPriceInfo
   * for the specified shipping group and the shipping method. The determined shipping cost is put
   * into the output parameter.
   * 
   * <P>
   *
   * The following parameters are required:
   * <DL>
   * <DT>shippingGroup
   * <DD>The ShippingGroup to price
   * <DT>shippingMethod
   * <DD>The Shipping method to price with
   * </DL> 
   *
   * <P>
   *
   * The following output parameter is defined when the service method is invoked:
   * <DL>
   * <DT>shippingPrice
   * <DD>A double value that corresponds to the shipping cost of the specified shipping group with
   * specified shipping method.
   * <DT>output
   * <DD>An oparam that is serviced if the shipping cost is determined.
   * </DL>
   * 
   * <P>
   *
   * This is an example of using this droplet to provide a price for the shipping with the 
   * specified shipping method.
   *
   * <P>
   *
   * <PRE>
   * &lt;dsp:droplet name="/atg/store/pricing/PriceShippingMethod""&gt;
   *   &lt;dsp:param name="shippingGroup" param="shippingGroup"/"&gt;
   *   &lt;dsp:param name="shippingMethod" param="shippingMethod"/"&gt;
   *   &lt;dsp:oparam name="output""&gt;
   *     &lt;dsp:getvalueof var="shippingPrice" param="shippingPrice" /"&gt;
   *     &lt;fmt:formatNumber value="${shippingPrice}" type="currency" /"&gt;
   *   &lt;/dsp:oparam"&gt;
   * &lt;/dsp:droplet"&gt;
   *
   * </PRE>
   * @author ATG
   * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/pricing/PriceShippingMethodDroplet.java#3 $$Change: 635816 $
   * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
   */
  
  public class PriceShippingMethodDroplet  extends DynamoServlet{
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/pricing/PriceShippingMethodDroplet.java#3 $$Change: 635816 $";
    
    //-------------------------------------
    // Constants
    //-------------------------------------
    private static final String PERFORM_MONITOR_NAME="PriceShippingMethodDroplet";
    
    static final ParameterName SHIPPING_GROUP_PARAM = ParameterName.getParameterName("shippingGroup");
    static final ParameterName SHIPPING_METHOD_PARAM = ParameterName.getParameterName("shippingMethod");
    
    static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    static final String SHIPPING_PRICE = "shippingPrice";
    static final String MY_RESOURCE_NAME = "atg.commerce.pricing.Resources";
    
    /** Resource Bundle **/
    private static java.util.ResourceBundle sResourceBundle =
      java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
    
    //-------------------------------------
    // Properties
    //-------------------------------------
      
  
    //-------------------------------------
    // property: ShippingPricingEngine
    /** the shipping pricing engine to price shipping methods */
    ShippingPricingEngine mShippingPricingEngine;
    
    /**
     * the shipping pricing engine to price shipping methods
     * @param pShippingPricingEngine new value to set
     */
    public void setShippingPricingEngine(ShippingPricingEngine pShippingPricingEngine)
    {mShippingPricingEngine = pShippingPricingEngine;}
    
    /**
     * the shipping pricing engine to price shipping methods
     * @return property ShippingPricingEngine
     */
    public ShippingPricingEngine getShippingPricingEngine()
    {return mShippingPricingEngine;}
    
    //-------------------------------------
    // property: DefaultLocale
    /** the default locale for which shipping method should be priced */
    Locale mDefaultLocale;
    
    /**
     * the default locale for which shipping method should be priced
     * @param pDefaultLocale new value to set
     */
    public void setDefaultLocale(Locale pDefaultLocale)
    {mDefaultLocale = pDefaultLocale;}
    
    /**
     * the default locale for which shipping method should be priced
     * @return property DefaultLocale
     */
    public Locale getDefaultLocale()
    {return mDefaultLocale;}
      
    //---------------------------------------------------------------------------
    // property: Order
    //---------------------------------------------------------------------------
    Order mOrder;
  
    /**
     * Set the Order property.
     * @param pOrder an <code>Order</code> value
     */
    public void setOrder(Order pOrder) {
      mOrder = pOrder;
    }
  
    /**
     * Return the Order property.
     * @return an <code>Order</code> value
     */
    public Order getOrder() {
      return mOrder;
    }
    
    //---------------------------------------------------------------------------
    // property: Profile
    //---------------------------------------------------------------------------
    Profile mProfile;
  
    /**
     * Set the Profile property.
     * @param pProfile a <code>Profile</code> value
     */
    public void setProfile(Profile pProfile) {
      mProfile = pProfile;
    }
  
    /**
     * Return the Profile property.
     * @return a <code>Profile</code> value
     */
    public Profile getProfile() {
      return mProfile;
    }
    
    //---------------------------------------------------------------------------
    // property: UserPricingModels
    //---------------------------------------------------------------------------
    PricingModelHolder mUserPricingModels;
  
    /**
     * Set the UserPricingModels property.
     * @param pUserPricingModels a <code>PricingModelHolder</code> value
     */
    public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
      mUserPricingModels = pUserPricingModels;
    }
  
    /**
     * Return the UserPricingModels property.
     * @return a <code>PricingModelHolder</code> value
     */
    public PricingModelHolder getUserPricingModels() {
      return mUserPricingModels;
    }
  
      
    /**
     * Performs the pricing of specified shipping method. Sets the shipping method
     * the specified shipping group to the one that is passed to the droplet. Then
     * calls shipping pricing engine to perform pricing of shipping group. And finally
     * returns back the original shipping method of the shipping group. The determined
     * price is stored into the output parameter.
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
        if (getShippingPricingEngine() != null){
          double shippingPrice = 0.0;
          
          ShippingGroup shippingGroup = getShippingGroup(pRequest, pResponse);
          Locale locale = getUserLocale(pRequest, pResponse);
          String shippingMethod = pRequest.getParameter(SHIPPING_METHOD_PARAM);
          if (shippingGroup != null && !StringUtils.isEmpty(shippingMethod)){
            try{ 
              String currentShippingMethod = shippingGroup.getShippingMethod();
              // set specified shipping method to determine its price
              shippingGroup.setShippingMethod(shippingMethod);
              
              //price shipping group with specified shipping method
              ShippingPriceInfo info = 
                getShippingPricingEngine().priceShippingGroup(getOrder(),
                                                              shippingGroup,
                                                              getUserPricingModels().getShippingPricingModels(),
                                                              locale,
                                                              getProfile(),
                                                              null);
              shippingPrice =  info.getAmount();
                      
              // set old shipping method back
              shippingGroup.setShippingMethod(currentShippingMethod);
              pRequest.setParameter(SHIPPING_PRICE, shippingPrice);
              pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
                      
            }catch (PricingException exc){
              if (isLoggingError()) logError(exc);
            }
          } else {
            String args[] = { SHIPPING_GROUP_PARAM.toString(), SHIPPING_METHOD_PARAM.toString()};
            if (isLoggingError())
              logError(ResourceUtils.getMsgResource("missingRequiredInputParam", MY_RESOURCE_NAME,
                                                     sResourceBundle, args));
    
          }
        }
      }
      finally {
        try {
          if (!perfCancelled) {
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
     * Get the shipping group from that we should price with the specified
     * shipping method.
     * @param pRequest the request to be processed
     * @param pResponse the response object for this request
     * @return the shipping group from that we should price with the specified
     * shipping method.
     * @exception ServletException an application specific error occurred 
     * processing this request
     * @exception IOException an error occurred reading data from the request
     * or writing data to the response.
     */
    protected ShippingGroup getShippingGroup(DynamoHttpServletRequest pRequest,
               DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
    {
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
    }
   
  
    /**
     * Returns the locale associated with the request. The method first searches
     * the locale of the request. If the locale cannot be determined,
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
      throws ServletException, IOException  {
      
      RequestLocale requestLocale = pRequest.getRequestLocale();
      if (requestLocale != null)
        return requestLocale.getLocale();
      return getDefaultLocale();
    }
  }

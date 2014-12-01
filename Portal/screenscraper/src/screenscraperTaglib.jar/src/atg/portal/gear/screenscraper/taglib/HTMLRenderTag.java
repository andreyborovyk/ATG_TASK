/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of thisadd
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


package atg.portal.gear.screenscraper.taglib;

import atg.core.net.URLUtils;
import atg.portal.framework.EnvironmentException;
import atg.portal.framework.EnvironmentFactory;
import atg.portal.framework.GearEnvironment;
import atg.portal.framework.Utilities;
import atg.portal.gear.screenscraper.Configuration;
import atg.portal.gear.screenscraper.HTMLFilterParser;
import atg.portal.gear.screenscraper.ScreenScraperHTTPConnection;
import atg.portal.gear.screenscraper.ScreenScraperWebClient;
import atg.portal.nucleus.NucleusComponents;

import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.io.IOException;
import java.io.Writer;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
* 
* Retrieves a web page resource specified by the Url property.
* Returns any errors occurred in the errorMessage Property.
* Must check for success property before trying to get the error messgage in the tag 
* body.
* @author Ashish Dwivedi
 * @version $Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/taglib/HTMLRenderTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/

public class HTMLRenderTag extends TagSupport {
  
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/taglib/HTMLRenderTag.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.portal.gear.screenscraper.taglib.TagResources";
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);
  
  public static final String CANT_FIND_CONFIGURATION_COMPONENT  = "cantFindConfigurationComponent";
  public static final String CANT_FIND_WEB_CLIENT_COMPONENT  = "cantFindWebClientComponent";
  public static final String CONFIGURATION_CONTEXT_PARAM_NAME  = "configurationContextParamName";
 
  public static final String ERROR_GETTING_RESOURCE_BUNDLE= "errorGettingResourceBundle";
  public static final String ERROR_GETTING_GEAR_ENVIRONMENT= "errorGettingGearEnvironment";
  
  static Configuration sConfiguration;

  //---------------------------------------------------------------------------
  // property: Url. The URL to retrieve web content for.
  String mURL; 

  public void setURL(String pURL)
  {  
    this.mURL = pURL;
  }
  
  public String getURL()
  {  
    return mURL;
  }

 //---------------------------------------------------------------------------
  // property: GearId. The gear id of the gear. Required for retrieving the 
  // the ResourceBundle.
  String mGearId; 

  public void setGearId(String pGearId)
  {  
    this.mGearId = pGearId;
  }
  
  public String getGearId()
  {  
    return mGearId;
  }

   //----------------------------------------
    // property: success. Signifies error while retrieving the web contents if false.
    private  boolean mSuccess;

    public void setSuccess(boolean pSuccess) 
	{ mSuccess = pSuccess; }

    public boolean getSuccess() 
	{ return mSuccess; }

    //----------------------------------------
    // property: errorMessage. Error string suggesting the type of error that occurred 
    // during web content retrieval
    private String mErrorMessage;

    public void setErrorMessage(String pErrorMessage) 
	  { 
      mErrorMessage = pErrorMessage; 
    }
    public String getErrorMessage() 
	  { 
      return mErrorMessage; 
    }

  //-------------------------------------
  /**
   * 
   */
  
  public int doStartTag() throws JspException{
    ScreenScraperWebClient webClient =  lookupWebClientComponent();      
    ServletResponse response = pageContext.getResponse();
    try{
      Writer out = pageContext.getOut();
    String error = webClient.retrieveWebPage(getURL(), getResourceBundle(pageContext, getGearId()), out);
    if(error!=null){
      this.setErrorMessage(error);
      this.setSuccess(false);
      pageContext.setAttribute(getId(), this);
      return( EVAL_BODY_INCLUDE);
    }
    }
    catch(IllegalArgumentException ex){
      new JspException(ex.toString());   
    }
    catch(IOException iex){
      new JspException(iex.toString());   
    }
    return(SKIP_BODY);
  }
  
   
  //-------------------------------------
  /**
   * Does a JNDI lookup for the /atg/portal/gear/screenscraper/ScreenScraperWebClient
   * component. 
   */
      
  protected   ScreenScraperWebClient lookupWebClientComponent() throws JspException{
    //any error in obtaining the component reference is fatal
    String componentJNDIName = null;
    try{
      componentJNDIName = lookupConfigurationComponent().getScreenScraperWebClientComponentJNDIName();
      ScreenScraperWebClient webClient = (ScreenScraperWebClient)Utilities.lookup(componentJNDIName);
      if (webClient == null) {
          throw new JspException
          (MessageFormat.format
          (sResourceBundle.getString(CANT_FIND_WEB_CLIENT_COMPONENT),
          new Object [] { componentJNDIName }));
        } 
        return webClient;
    }
    catch (NamingException exc) {
        throw new JspException
        (MessageFormat.format
        (sResourceBundle.getString(CANT_FIND_WEB_CLIENT_COMPONENT),
        new Object [] { componentJNDIName }) +
        ": " + exc);
      }
      catch (ClassCastException exc) {
        throw new JspException
        (MessageFormat.format
        (sResourceBundle.getString(CANT_FIND_WEB_CLIENT_COMPONENT),
        new Object [] { componentJNDIName }) +
        ": " + exc);
      }
  }


  //-------------------------------------
  /**
   * Does a JNDI lookup for the /atg/portal/gear/screenscraper/Configuration
   * component.
   * The name of the component is stored as context param. 
   */
      
  protected  Configuration lookupConfigurationComponent() throws JspException{
    //any error in obtaining the component reference is fatal
 
    if (sConfiguration == null) {
      String componentJNDIName=null;
      try {
        componentJNDIName=pageContext.getServletContext().getInitParameter(
        sResourceBundle.getString(CONFIGURATION_CONTEXT_PARAM_NAME));
        sConfiguration =
        (Configuration)
        (new InitialContext ().lookup (componentJNDIName));
        if (sConfiguration == null) {
          throw new JspException
          (MessageFormat.format
          (sResourceBundle.getString(CANT_FIND_CONFIGURATION_COMPONENT),
          new Object [] { componentJNDIName }));
        }
      }
      catch (NamingException exc) {
        throw new JspException
        (MessageFormat.format
        (sResourceBundle.getString(CANT_FIND_CONFIGURATION_COMPONENT),
        new Object [] { componentJNDIName }) +
        ": " + exc);
      }
      catch (ClassCastException exc) {
        throw new JspException
        (MessageFormat.format
        (sResourceBundle.getString(CANT_FIND_CONFIGURATION_COMPONENT),
        new Object [] { componentJNDIName }) +
        ": " + exc);
      }

    }
      return sConfiguration;
  }
  //-------------------------------------
  /**
   * 
   */
  
  public int doEndTag ()  throws JspException
  {
       return EVAL_PAGE;
  }

  //----------------------------------------
  /**
  * release the tag
  */
  public void release()
  {
    super.release();
    setURL(null);
    setErrorMessage(null);
    setSuccess(false);
  }
  
  /**
   * Retrive the resource bundle for the given gear id. 
   * Resource bundle is configurable on per gear basis. 
   */
  
  protected ResourceBundle getResourceBundle(PageContext pPageContext, String pGearId) throws JspException{
    
    HttpServletRequest request = (HttpServletRequest)pPageContext.getRequest();
    HttpServletResponse response = (HttpServletResponse)pPageContext.getResponse();
    request.setAttribute("atg.paf.Gear", pGearId);
    GearEnvironment gearEnv = null;
    String resourceBundleParamName= "";
    String resourceBundleName= "";
    try {
      gearEnv = EnvironmentFactory.getGearEnvironment(request, response);
      resourceBundleName = gearEnv.getGearInstanceParameter(lookupConfigurationComponent().getResourceBundleNameParameterName());
      return java.util.ResourceBundle.getBundle(resourceBundleName);
    }
    catch (MissingResourceException exc) {
      throw new JspException
      (MessageFormat.format
      (sResourceBundle.getString(ERROR_GETTING_RESOURCE_BUNDLE),
      new Object [] { resourceBundleName }) +
      ": " + exc);
    }
    catch(EnvironmentException ex){
      throw new JspException
      (MessageFormat.format
      (sResourceBundle.getString(ERROR_GETTING_GEAR_ENVIRONMENT),
      new Object [] {this.getClass() }) +
      ": " + ex);
    }
  }

}

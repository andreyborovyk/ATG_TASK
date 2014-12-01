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


package atg.portal.gear.xmlfeed;

import atg.servlet.DynamoHttpServletRequest;
import atg.portal.framework.*;

import javax.servlet.http.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * <p>
 * This is the base adapter/processor class to enable the xmlfeed gear. 
 * It provides basic methods to retrieve xml, xsl sources and map query 
 * parameters. It supports simple content providers with HTTP API. 
 * It can be extended to handle more protocol oriented content providers.
 * </p>
 *
 * @version $Id: //app/portal/version/10.0.3/xmlfeed/classes.jar/src/atg/portal/gear/xmlfeed/BaseQueryProcessor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class BaseQueryProcessor {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlfeed/classes.jar/src/atg/portal/gear/xmlfeed/BaseQueryProcessor.java#2 $$Change: 651448 $";



    public static final String XMLSOURCE_PARAM = "baseXmlSource";
    public static final String SHARED_HTML_XSL_PARAM = "sharedHtmlXsl";
    public static final String FULL_HTML_XSL_PARAM = "fullHtmlXsl";
    public static final String SHARED_WML_XSL_PARAM = "sharedWmlXsl";
    public static final String FULL_WML_XSL_PARAM = "fullWmlXsl";
    public static final String QUERYPARAMS_MAPPING_PARAM = "queryParametersMapping";
    public static final String SHOWFULLLINK_PARAM = "showLinkToFull";

    public static final String USERPARAM_PREFIX = "UserParam";

    public final String SHARED_MODE = "shared";
    public final String FULL_MODE = "full";
    public final String HTML_DEVICEOUTPUT = "html";
    public final String WML_DEVICEOUTPUT = "wml";


    GearEnvironment mGearEnv;
    HashMap mXslSources;

    /**
     * Initializes the instance with the right Gear Environment and loads
     * appropriate xsl sources from the instance parameters. Used by the
     * GetProcessor tag in xmlfeed taglib.
     * @param pReq The request object.
     * @param pResp The response object.
     */
    public void initialize(HttpServletRequest pReq,
                           HttpServletResponse pResp)
    {
	
	mGearEnv = getGearEnvironment(pReq, pResp);

	
	
	mXslSources = new HashMap(4);
	mXslSources.put(SHARED_MODE+HTML_DEVICEOUTPUT,
		       mGearEnv.getGearInstanceParameter(SHARED_HTML_XSL_PARAM));
	
	mXslSources.put(FULL_MODE+HTML_DEVICEOUTPUT,
		       mGearEnv.getGearInstanceParameter(FULL_HTML_XSL_PARAM));
	mXslSources.put(SHARED_MODE+WML_DEVICEOUTPUT,
		       mGearEnv.getGearInstanceParameter(SHARED_WML_XSL_PARAM));
	mXslSources.put(FULL_MODE+WML_DEVICEOUTPUT,
		       mGearEnv.getGearInstanceParameter(FULL_WML_XSL_PARAM));
	
		       
	
    }

    /**
     * Returns the xml source string. 
     * @return String pointing to the xsl source.
     */
    public String getXmlSource() {
	String baseXmlSource = mGearEnv.getGearInstanceParameter
	    (XMLSOURCE_PARAM);
	
	String xmlSource = baseXmlSource;
        String queryString = getQueryParameters();
	if (queryString != null)
	    xmlSource += queryString;

	return xmlSource;
    }
	
    /**
     * Returns the xml source string. 
     * @param pSource A basic source identifier. 
     * @return String pointing to the xsl source.
     */
    public String getXmlSource(String pSource) {
	if (pSource != null && !pSource.equals("")) 
	    return pSource;
	else
	    return getXmlSource();
    }

    /**
     * Returns the xsl source.
     * @return Xsl source string.
     */
    public String getXslSource(String pMode, String pDeviceOutput) {
	String xslSource = (String) mXslSources.get(pMode+pDeviceOutput);
	return xslSource;
    }

    /**
     * Returns the ResourceBundle for the specified display mode and device
     * output. It looks for a resource bundle named after appending 'Resource' 
     * string after the xsl file for the given mode and device output in the
     * atg.portal.gear.xmlfeed package.
     * @param pMode Display Mode
     * @param pDeviceOutput Device Output
     * @return ResourceBundle name associated with this mode and device output.
     */
    public String getXslResource(String pMode, String pDeviceOutput) {
	String source = getXslSource(pMode, pDeviceOutput);
	String resource = "";
	String fileName = parseRelativePath(source);
	resource = "atg.portal.gear.xmlfeed." + fileName + "Resource";
	return resource;
    }

    /**
     * Indicates if a link to the full view page should be displayed in the
     * shared view.
     * @return True if a link to full mode should be diplayed. False otherwise.
     */
    public boolean showLinkToFullMode() {
	boolean show = false;
	String paramShow = mGearEnv.getGearInstanceParameter(SHOWFULLLINK_PARAM);

       
	if (paramShow != null)
	    show = (Boolean.valueOf(paramShow)).booleanValue();
	return show;
    }

    
    /**
     * Maps the instanceParameters to appropriate string to be appended in 
     * the url pointing to the xml source. 
     */	
    String getQueryParameters() {
	String mappings = mGearEnv.getGearInstanceParameter
	    (QUERYPARAMS_MAPPING_PARAM);
	
	if (mappings != null) {
	    Map paramMap = XmlfeedTool.convertParamStringToMap(mappings);
	    Iterator keys = paramMap.keySet().iterator();
	    
	    HashMap newValues = new HashMap();
	    
	    while(keys.hasNext()) {
		String key = (String)keys.next();
		String val = (String)paramMap.get(key);
		String newVal = "";
		
		int index = val.indexOf(".");
		if ((index > 0) && (val != null)) {
		    
		    if (val.substring(0,index).equals(USERPARAM_PREFIX)) {
			newVal = mGearEnv.getGearUserParameter
			    (val.substring(index+1));
		    }
		    
		}
		
		if (!newVal.equals("")) {
		    newValues.put(key, newVal);
		}
		
	    }
	    String paramString = XmlfeedTool.convertMapToParamString(newValues);
	    return paramString;
	}
	else {
	    return null;
	}
    }
									 
    /**
     * Creates the Gear Environment from the given request.
     */
    GearEnvironment getGearEnvironment (HttpServletRequest pReq,
                                        HttpServletResponse pResp)
    {
	
	
	//pReq.setAttribute("atg.paf.Gear", pReq.getParameter("paf_gear_id"));
	pReq.setAttribute("atg.paf.PortalRepositoryLocation",
			  "dynamo:/atg/portal/framework/PortalRepository");
	GearEnvironment env = null;
	
	try {
	    env = EnvironmentFactory.getGearEnvironment(pReq, pResp);
	}
	catch (EnvironmentException e) {
	    System.out.println("BaseQueryProcessor.getGearEnvironment():" +
			       e.getMessage());
	}
	
	return env;
	
    }
    
    /**
     * Parses the Xsl file name from the full path. Used to generate the 
     * name of the ResourceBundle associated with an Xsl file.
     */
    String parseRelativePath(String pSource) {
	StringTokenizer s = new StringTokenizer(pSource, "/");
	String fullName = pSource;
	String result = "";

	while (s.hasMoreTokens())
	    fullName = s.nextToken();

	int index = fullName.indexOf(".");
	if (index > 0) 
	    result = fullName.substring(0,index);

	return result;
    }

    
    
    
}

























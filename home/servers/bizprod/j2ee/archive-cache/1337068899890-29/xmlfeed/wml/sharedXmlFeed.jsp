<%-- 
Page:   	sharedXmlFeed.jsp
Gear:  	 	Xmlfeed Gear
gearmode: 	content (the default mode)
displayMode: 	shared
DeviceOutput:   WML
Author:      	Malay Desai
Description: 	This is the shared view of the xmlfeed gear. It uses Jakarta 
                Xtags library to do XSLT. It can be used to display content
                from various content providers or from local file system.
--%>

<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-xtags-1.0" prefix="xtags" %>
<%@ taglib uri="/xmlfeed-taglib" prefix="xmlfeed" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%@ page import="java.io.*,java.util.*,atg.servlet.ServletUtil" %>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle id="pageBundle" baseName="atg.portal.gear.xmlfeed.XmlfeedResource"  localeAttribute="userLocale" changeResponseLocale="false" />


<%

  String origURI = pafEnv.getOriginalRequestURI();
  String gearID = pafEnv.getGear().getId();
  String className = pafEnv.getGearInstanceParameter("queryProcessorClass");
  
%>

<xmlfeed:GetProcessor id="processor" className="<%= className %>">

  <core:createUrl id="fullMode" url="<%= origURI%>">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="paf_gm" value="content"/>

  <xtags:style xml="<%= processor.getXmlSource() %>"
               xsl="<%= processor.getXslSource(processor.SHARED_MODE,
                                               processor.WML_DEVICEOUTPUT) %>">


  <i18n:bundle id="bundle" baseName="<%= processor.getXslResource(processor.SHARED_MODE, processor.WML_DEVICEOUTPUT) %>"  localeAttribute="userLocale"/>      

    <core:forEach id="param" values="<%= bundle.getKeys() %>" castClass="String"
                  elementId="key">

      <xtags:param name="<%= key %>" value="<%= bundle.getString(key) %>" />
    </core:forEach>    

    
      <%-- Used to generate links to articles when using news source --%>      
      <xtags:param name="articleGearUrl"
                   value="<%= ServletUtil.escapeHtmlString(fullMode.getNewUrl()) %>" />
  
  </xtags:style>

  <%-- Link to full view if enabled by the processor. --%>

  <core:if value="<%= processor.showLinkToFullMode() %>">
    <p align="center">
    <a href="<%= ServletUtil.escapeHtmlString(fullMode.getNewUrl()) %>"><i18n:message bundleRef="pageBundle" key="moreDetailsLinkText"/></a>
    </p>   
  </core:if>

  </core:createUrl>
</xmlfeed:GetProcessor>





</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/wml/sharedXmlFeed.jsp#2 $$Change: 651448 $--%>

<%-- 
Page:   	fullXmlFeed.jsp
Gear:  	 	Xmlfeed Gear
gearmode: 	content (the default mode)
displayMode: 	full
DeviceOutput:   WML
Author:      	Malay Desai
Description: 	This is the full view of the xmlfeed gear. It uses Jakarta 
                Xtags library to do XSLT. It can be used to display content
                from various content providers or from local file system.
--%>

<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-xtags-1.0" prefix="xtags" %>
<%@ taglib uri="/xmlfeed-taglib" prefix="xmlfeed" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<%

  String origURI = pafEnv.getOriginalRequestURI();
  String gearID = pafEnv.getGear().getId();

  String className = pafEnv.getGearInstanceParameter("queryProcessorClass");
  String articleID = (String) request.getParameter("articleID");
%>

<xmlfeed:GetProcessor id="processor" className="<%= className %>">

  <xtags:style xml="<%= processor.getXmlSource(articleID) %>"
               xsl="<%= processor.getXslSource(processor.FULL_MODE,
                                               processor.WML_DEVICEOUTPUT) %>">

  <i18n:bundle id="bundle" baseName="<%= processor.getXslResource(processor.FULL_MODE, processor.HTML_DEVICEOUTPUT) %>"  localeAttribute="userLocale" changeResponseLocale="false" />

    <core:forEach id="param" values="<%= bundle.getKeys() %>" castClass="String"
                  elementId="key">

      <xtags:param name="<%= key %>" value="<%= bundle.getString(key) %>" />
    </core:forEach>    
  </xtags:style>

</xmlfeed:GetProcessor>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/wml/fullXmlFeed.jsp#2 $$Change: 651448 $--%>

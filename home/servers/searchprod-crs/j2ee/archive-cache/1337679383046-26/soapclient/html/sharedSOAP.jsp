<%-- 
Page:   	sharedXmlFeed.jsp
Gear:  	 	Xmlfeed Gear
gearmode: 	content (the default mode)
displayMode: 	shared
DeviceOutput:   HTML
Author:      	Malay Desai
Description: 	This is the shared view of the xmlfeed gear. It uses Jakarta 
                Xtags library to do XSLT. It can be used to display content
                from various content providers or from local file system.
--%>

<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/soapclient-taglib" prefix="soap" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<%

  String origURI = pafEnv.getOriginalRequestURI();
  String gearID = pafEnv.getGear().getId();
%>

<soap:InvokeService id="SOAPResult" gearEnv="<%= pafEnv%>"/>

<table border="0"><tr>


  <core:ExclusiveIf>  
    <core:If value="<%= SOAPResult.hasError() %>">
      <core:ForEach id="errorObjects"
                    values="<%= SOAPResult.getErrorObjects() %>"
                    castClass="atg.portal.gear.soapclient.ErrorObject"
                    elementId="errorObj">

             <td><font class="small"> <%= errorObj.getMessage() %></font></td>
      </core:ForEach>
      
    </core:If>
 
    <core:DefaultCase>
      <td><font class="small"><%= SOAPResult.getResult().toString() %></font></td>
    </core:DefaultCase>
  </core:ExclusiveIf>

</tr></table>



</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/soapclient/soapclient.war/html/sharedSOAP.jsp#2 $$Change: 651448 $--%>

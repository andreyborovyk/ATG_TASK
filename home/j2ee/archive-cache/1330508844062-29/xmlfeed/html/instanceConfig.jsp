<%-- 
Page:   	instanceConfig.jsp
Gear:  	 	Xmlfeed Gear
gearmode: 	InstanceConfig
displayMode: 	full
DeviceOutput:   HTML
Author:      	Malay Desai
Description: 	This is the full view InstanceConfig mode of the xmlfeed gear.
                The community administrator can configure xml and xsl source 
                parameters using this page.
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle id="bundle" baseName="atg.portal.gear.xmlfeed.ConfigMessageResource" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButton" key="submitButton" />
<i18n:message id="resetButton" key="resetButton" />

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>

   <!-- display errors if any -->
   <%-- Page fragment that renders form success and failure messages. --%>

   <dsp:importbean bean="/atg/portal/admin/SuccessMessageProcessor"/>
   <dsp:importbean bean="/atg/portal/admin/FailureMessageProcessor"/>

   <dsp:getvalueof id="failureMessageProcessor" idtype="atg.portal.admin.I18nMessageProcessor" bean="FailureMessageProcessor">
     <dsp:getvalueof id="successMessageProcessor" idtype="atg.portal.admin.I18nMessageProcessor" bean="SuccessMessageProcessor">
<%
 failureMessageProcessor.localizeMessages(request, response);
 successMessageProcessor.localizeMessages(request, response);
%>

       <%-- Previous submission success/failure reporting --%>
       <dsp:getvalueof id="successMessages" idtype="java.util.List" bean="SuccessMessageProcessor.messages">
         <core:ForEach id="successIterator"
                       values="<%=successMessages%>"
                       castClass="String"
                       elementId="successMessage">
            <img src='<%= response.encodeURL("/gear/xmlfeed/images/info.gif")%>'/>&nbsp;&nbsp;<font class="info"><%=successMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- successMessages --%>

       <dsp:getvalueof id="failureMessages" idtype="java.util.List" bean="FailureMessageProcessor.messages">
         <core:ForEach id="failureIterator"
                       values="<%=failureMessages%>"
                       castClass="String"
                       elementId="failureMessage">
           <img src='<%= response.encodeURL("/gear/xmlfeed/images/error.gif")%>'/>&nbsp;&nbsp;<font class="error"><%=failureMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- failureMessages --%>

<%
  failureMessageProcessor.clear();
  successMessageProcessor.clear();
%>
     </dsp:getvalueof><%-- successMessages --%>
   </dsp:getvalueof><%-- failureMessages --%>

<% 
String origURI= pafEnv.getOriginalRequestURI(); 

%>

<p>
 


<dsp:form  method="post" action="<%= origURI %>">
	
  <dsp:getvalueof id="thisPageUrl" bean="/OriginatingRequest.requestURIWithQueryString">
    <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= thisPageUrl%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.cancelUrl" value="<%= thisPageUrl%>"/>
  </dsp:getvalueof>


  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_community_id" value="<%=pafEnv.getCommunity().getId() %>"/>
  <input type="hidden" name="formSubmit" value="true"/>
    
      
    <dsp:input type="hidden" bean="GearConfigFormHandler.paramType"
                         value="instance" />
    <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues"
                         value="false" />  
    <dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="true"/>


<TABLE WIDTH="80%" BORDER="0" CELLSPACING="0" CELLPADDING="0">

  
  <tr>
  <td colspan="2"><img src="/gear/xmlfeed/images/clear.gif" height="10" width="1" border="0"></td>
  </tr>


    <tr>
      <td colspan=2 >	
        <font class="large_bold"><i18n:message key="instanceConfigHeading"/></font>
      </td>
    </tr>

    <tr>
      <td colspan=2 bgcolor="#000033"><img src="/gear/xmlfeed/images/clear.gif" height=1 width=1 border=0></td>
    </tr>

    <tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
    </tr>

    <tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
    </tr>

    <tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
    </tr>



<tr><td width="50%" align="right"><font class="small">
<i18n:message key="xmlsource" /></font></td>
<td width="50%">&nbsp;&nbsp;
<dsp:input type="text" size="45"  maxlength="254" bean="GearConfigFormHandler.values.baseXmlSource"
           value='<%= pafEnv.getGearInstanceParameter("baseXmlSource") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
</tr>



<tr><td width="50%" align="right"><font class="small">
<i18n:message key="xslsharedhtml" /></b></font></td>
<td width="50%">&nbsp;&nbsp;
<dsp:input type="text" size="45"  maxlength="254" bean="GearConfigFormHandler.values.sharedHtmlXsl"
           value='<%= pafEnv.getGearInstanceParameter("sharedHtmlXsl") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
</tr>

<tr><td width="50%" align="right"><font class="small">
<i18n:message key="xslfullhtml" /></font></td>
<td width="50%">&nbsp;&nbsp;
<dsp:input type="text" size="45"  maxlength="254" bean="GearConfigFormHandler.values.fullHtmlXsl"
           value='<%= pafEnv.getGearInstanceParameter("fullHtmlXsl") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
</tr>

<tr><td width="50%" align="right"><font class="small">
<i18n:message key="xslsharedwml" /></font></td>
<td width="50%">&nbsp;&nbsp;
<dsp:input type="text" size="45"  maxlength="254" bean="GearConfigFormHandler.values.sharedWmlXsl"
           value='<%= pafEnv.getGearInstanceParameter("sharedWmlXsl") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
</tr>

<tr><td width="50%" align="right"><font class="small">
<i18n:message key="xslfullwml" /></font></td>
<td width="50%">&nbsp;&nbsp;
<dsp:input type="text" size="45"  maxlength="254" bean="GearConfigFormHandler.values.fullWmlXsl"
           value='<%= pafEnv.getGearInstanceParameter("fullWmlXsl") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
</tr>



<tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
</tr>  
 <td>&nbsp;</td>
<td align="left">
&nbsp;&nbsp;
 <dsp:input type="submit" bean="GearConfigFormHandler.confirm" value="<%= submitButton %>" />
&nbsp;&nbsp;&nbsp;
  <input type="submit" value="<%= resetButton %>" />
 </td></tr>

<tr>
      <td colspan=2><img src="/gear/xmlfeed/images/clear.gif" height=10 width=1 border=0></td>
</tr>
</table>
 
 

</dsp:form> 
</paf:InitializeGearEnvironment>
</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/html/instanceConfig.jsp#2 $$Change: 651448 $--%>

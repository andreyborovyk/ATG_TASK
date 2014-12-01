<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle id="bundle" baseName="atg.portal.gear.search.ConfigMessageResource" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButton" key="submitButton" />
<i18n:message id="resetButton" key="resetButton" />

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>
<% 
String origURI= pafEnv.getOriginalRequestURI(); 
String successUrl = request.getParameter("paf_success_url");
String clearGif = response.encodeURL("images/clear.gif");
String infoGif = response.encodeURL("images/info.gif");
%>

<%-- put feedback message here it will be on a white back ground
     a br tag before and after 
--%>


<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="GearConfigFormHandler.formError"/>

  <dsp:oparam name="true">
    <br/>
    <font class="error">

      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="GearConfigFormHandler.formExceptions"/>
        <dsp:oparam name="output">
          <dsp:valueof param="message"></dsp:valueof><br/>
        </dsp:oparam>
      </dsp:droplet>

     <%-- reset form errors --%>
     <dsp:setvalue bean="GearConfigFormHandler.resetFormExceptions" />

    </font>
  </dsp:oparam>


  <dsp:oparam name="false">

    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param name="array" bean="GearConfigFormHandler.formMessages" />
      <dsp:oparam name="output">
        <br/>
        &nbsp;<img src="<%= infoGif %>">&nbsp;&nbsp;<font class="smaller">
        <dsp:valueof param="element" /></font><br/>
      </dsp:oparam>
    </dsp:droplet> 
    
   <%-- reset form messages --%>
   <dsp:setvalue bean="GearConfigFormHandler.resetFormMessages" />

  </dsp:oparam>
</dsp:droplet> 


<TABLE WIDTH="98%" bgcolor="#bad8ec" BORDER="0" CELLSPACING="0" CELLPADDING="4">
<tr><td>
<br>
<TABLE WIDTH="80%" bgcolor="#bad8ec" BORDER="0" CELLSPACING="0" CELLPADDING="0">


<dsp:form  method="post" action="<%= origURI %>">
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="" value="<%=pafEnv.getGearMode() %>"/>

  <dsp:getvalueof id="thisPageUrl" bean="/OriginatingRequest.requestURIWithQueryString">
    <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= thisPageUrl%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.cancelUrl" value="<%= thisPageUrl%>"/>
  </dsp:getvalueof>
    <dsp:input type="hidden" bean="GearConfigFormHandler.paramType"  value="instance" />
    <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues"  value="false" />  
    <dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="true"/>


    <tr>
      <td colspan=2 ><font class="medium_bold"><i18n:message key="instanceConfigHeading"/></font>    </td>
    </tr>

    <tr>
      <td colspan=2 bgcolor="#000033"><img src="<%= clearGif %>" height=1 width=1 border=0></td>
    </tr>

    <tr>
      <td colspan=2><img src="<%= clearGif %>" height=15 width=1 border=0></td>
    </tr>

    <tr>
    <td width="50%" align="right"><font class="small"><i18n:message key="maxResultsPerPage" /></font></td>
<td width="50%">&nbsp;&nbsp;<dsp:input type="text" size="3"  maxlength="254" bean="GearConfigFormHandler.values.resultsPerPage" value='<%= pafEnv.getGearInstanceParameter("resultsPerPage") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="<%= clearGif %>" height=10 width=1 border=0></td>
</tr>

    <tr>
      <td colspan=2><img src="<%= clearGif %>" height=15 width=1 border=0></td>
    </tr>

    <tr>
    <td width="50%" align="right"><font class="small"><i18n:message key="maxResults" /></font></td>
<td width="50%">&nbsp;&nbsp;<dsp:input type="text" size="6"  maxlength="254" bean="GearConfigFormHandler.values.maxResults" value='<%= pafEnv.getGearInstanceParameter("maxResults") %>'/>
</td></tr>

<tr>
      <td colspan=2><img src="<%= clearGif %>" height=10 width=1 border=0></td>
</tr>

 <td>&nbsp;</td>
<td align="left">
&nbsp;&nbsp;
 <dsp:input type="submit" bean="GearConfigFormHandler.confirm" value="<%= submitButton %>" />
&nbsp;&nbsp;&nbsp;
 <dsp:input type="submit"  bean="GearConfigFormHandler.cancel" value="<%= resetButton %>" />
<br><br><br><br>

 </td></tr>

</dsp:form> 

</table>
 </td></tr>
</table>
 

</paf:InitializeGearEnvironment>
</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/search/search.war/html/instanceConfig.jsp#2 $$Change: 651448 $--%>

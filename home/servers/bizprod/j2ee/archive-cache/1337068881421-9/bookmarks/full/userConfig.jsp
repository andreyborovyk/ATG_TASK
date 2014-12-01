<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:includeOnly/>
<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">
<dsp:setvalue param="paf" value="<%= pafEnv %>"/>
<%
String paf_gear_id = null;
String gearContext = null;
String redirectUrl = null;

paf_gear_id = pafEnv.getGear().getId();
gearContext = pafEnv.getGear().getServletContext();

String bundleName = pafEnv.getGearInstanceParameter("resourceBundle");
String context = pafEnv.getGear().getServletContext();
String errorimg = context + "/images/error.gif";
String infoimg = context + "/images/info.gif";
%>
<i18n:bundle baseName="<%= bundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="editBookmarksTitle" key="editBookmarksTitle"/>
<i18n:message id="userConfigMessage" key="userConfigMessage"/>
<i18n:message id="untitled" key="untitled"/>
<i18n:message id="noErrorMessage" key="noErrorMessage"/>
<i18n:message id="linkLabel" key="linkLabel"/>
<i18n:message id="linkNameLabel" key="linkNameLabel"/>
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="moreButton" key="moreButton"/>
<i18n:message id="cancelButton" key="cancelButton"/>

<dsp:importbean bean="/atg/portal/gear/bookmarks/BookmarksForEach"/>
<dsp:importbean bean="/atg/portal/gear/bookmarks/BookmarksFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<font class="large_bold"><center><%= editBookmarksTitle %></center></font><br>

<font class="small"><center>&nbsp;<%= userConfigMessage %>&nbsp;</center></font>

<!-- display errors if any -->
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="BookmarksFormHandler.formError" />
  <dsp:oparam name="true">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" bean="BookmarksFormHandler.formExceptions"/>
      <dsp:oparam name="output">
        <dsp:getvalueof id="errorCode" idtype="java.lang.String" param="element.getErrorCode()">
          <font class="error"><img src='<%= response.encodeURL(errorimg)%>'
          >&nbsp;&nbsp;<i18n:message key="<%= errorCode %>"/></font><br/>
        </dsp:getvalueof>
      </dsp:oparam>
    </dsp:droplet>
    <dsp:setvalue bean="BookmarksFormHandler.resetFormExceptions"/>
  </dsp:oparam>
</dsp:droplet>


<%-- display info messages if any --%> 
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="BookmarksFormHandler.formInfo"/>
  <dsp:oparam name="true">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" bean="BookmarksFormHandler.formMessages"/>
      <dsp:oparam name="output">
        <dsp:getvalueof id="infoMsg" idtype="java.lang.String" param="element">
          <font class="info"><img src='<%= response.encodeURL(infoimg)%>'
          >&nbsp;&nbsp;<i18n:message key="<%= infoMsg %>"/></font>
        </dsp:getvalueof>
      </dsp:oparam>
    </dsp:droplet>
    <dsp:setvalue bean="BookmarksFormHandler.resetFormMessages"/> 
  </dsp:oparam>
</dsp:droplet>

<dsp:form method="POST" action="<%= pafEnv.getOriginalRequestURI() %>">
<dsp:droplet name="BookmarksForEach">
  <dsp:param name="extra" param="extra"/>
  
  <dsp:oparam name="outputStart">
    <center>
    <table cellpadding="3">
  </dsp:oparam>
  
  <dsp:oparam name="output">
    <dsp:getvalueof id="index" param="index">
    <tr>
      <td>&nbsp;</td>
      <td>
        <font class="small"><%= linkLabel %><dsp:input type="hidden" bean='<%= "BookmarksFormHandler.linkIds[" + index + "]" %>' paramvalue="id"/></font>
      </td>
      <td>&nbsp;</td>
      <td>
        <font class="small"><dsp:input type="text" maxlength="300" bean='<%= "BookmarksFormHandler.links[" + index + "]" %>' paramvalue="link"/></font>
      </td>
      <td>
       <font class="small"><%= linkNameLabel %></font>
      </td>
      <td>
       <font class="small"><dsp:input type="text" maxlength="100" bean='<%= "BookmarksFormHandler.names[" + index + "]" %>' paramvalue="name"/></font>
      </td>
    </tr>
    </dsp:getvalueof>
  </dsp:oparam>
  
  <dsp:oparam name="outputEnd">
      </table>
    </center>
  </dsp:oparam>

  <dsp:oparam name="more">       
    <center>
      <%-- 
        Parameters to pass in on the submit.  These are not strictly needed, but
        are a good habit to get into.
      --%>
      <input type="hidden" name="paf_dm" value="full"/>
      <input type="hidden" name="paf_gear_id" value="<%= paf_gear_id %>"/>
      <input type="hidden" name="extra" value="5"/>
      <input type="hidden" name="paf_gm" value="userConfig"/>      

      <core:createUrl id="moreURL" url="<%= pafEnv.getOriginalRequestURI() %>">
        <core:urlParam param="paf_dm" value="full"/>
        <core:urlParam param="paf_gm" value="userConfig"/>
        <core:urlParam param="paf_gear_id" value="<%= paf_gear_id %>"/>
        <core:urlParam param="extra" value="5"/>
        <dsp:input type="hidden" bean="BookmarksFormHandler.moreURL" value="<%= moreURL.getNewUrl() %>"/>
      </core:createUrl>      
 
      <core:createUrl id="successURL" url="<%= pafEnv.getOriginalRequestURI() %>">
        <core:urlParam param="paf_dm" value="full"/>
        <core:urlParam param="paf_gm" value="userConfig"/>
        <core:urlParam param="paf_gear_id" value="<%= paf_gear_id %>"/>
        <core:urlParam param="extra" value="5"/>
        <dsp:input type="hidden" bean="BookmarksFormHandler.successURL" value="<%= successURL.getNewUrl() %>"/>
      </core:createUrl>      
      <%-- For the cancel URL, we don't need to do anything. --%>
      <dsp:input type="hidden" bean="BookmarksFormHandler.cancelURL" value="<%= pafEnv.getOriginalRequestURI() %>"/>
      <%-- Same with the editErrorURL. --%>
      <dsp:input type="hidden" bean="BookmarksFormHandler.failureURL" value="<%= pafEnv.getOriginalRequestURI() %>"/>      
      <%-- The buttons themselves. --%>
      <dsp:input type="submit" bean="BookmarksFormHandler.submit" value="<%= submitButton %>"/>
      <dsp:input type="submit" bean="BookmarksFormHandler.more" value="<%= moreButton %>"/>
    </center>
  </dsp:oparam>

 </dsp:droplet>
</dsp:form>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/bookmarks/bookmarks.war/full/userConfig.jsp#2 $$Change: 651448 $--%>

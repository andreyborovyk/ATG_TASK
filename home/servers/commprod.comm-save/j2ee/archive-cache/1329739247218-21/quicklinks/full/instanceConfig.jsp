<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<dsp:importbean bean="/atg/portal/gear/quicklinks/QuicklinksForEach"/>
<dsp:importbean bean="/atg/portal/gear/quicklinks/QuicklinksFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<paf:InitializeGearEnvironment id="pafEnv">
<paf:hasCommunityRole roles="leader,gear-manager">
<%

  String bundleName = pafEnv.getGearInstanceParameter("resourceBundle");

  String paf_success_url = request.getParameter("paf_success_url");
  String paf_page_url    = request.getParameter("paf_page_url");

  /* paf_community_id is required to be passed back to the configure page. */
  String paf_community_id = request.getParameter("paf_community_id"); 
  String paf_gear_id = null;
  paf_gear_id = pafEnv.getGear().getId();


   String communityAdminURI =  request.getParameter("paf_success_url");
// this has been changed to use the complete paf_success_url as the exit url 
// succes and cancel urls

 %>


<i18n:bundle baseName="<%= bundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="editQuicklinksTitle" key="editQuicklinksTitle"/>
<i18n:message id="instanceConfigMessage" key="instanceConfigMessage"/>
<i18n:message id="untitled" key="untitled"/>
<i18n:message id="noErrorMessage" key="noErrorMessage"/>
<i18n:message id="linkLabel" key="linkLabel"/>
<i18n:message id="linkNameLabel" key="linkNameLabel"/>
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="moreButton" key="moreButton"/>
<i18n:message id="renderPageErrorMessage" key="renderPageErrorMessage"/>

<%-- set a parameter so we can get to it without JSP escaping --%>
<dsp:setvalue param="paf" value="<%= pafEnv %>"/>
<blockquote>
<font class="large_bold"><%= editQuicklinksTitle %></font><br>
<font class="small"><%= instanceConfigMessage %></font>
</blockquote>

      <!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="QuicklinksFormHandler.formError" />
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="QuicklinksFormHandler.formExceptions"/>
               <dsp:oparam name="output">
                  <dsp:getvalueof id="errorCode" idtype="java.lang.String" param="element.getErrorCode()">
                  <font class="error"><img src='<%= response.encodeURL("images/error.gif")%>'
                  >&nbsp;&nbsp;<i18n:message key="<%= errorCode %>"/></font><br/>
                  </dsp:getvalueof>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:setvalue bean="QuicklinksFormHandler.resetFormExceptions"/>
         </dsp:oparam>
      </dsp:droplet>

      <%-- display info messages if any --%> 
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="QuicklinksFormHandler.formInfo"/>
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="QuicklinksFormHandler.formMessages"/>
               <dsp:oparam name="output">
                  <dsp:getvalueof id="infoMsg" idtype="java.lang.String" param="element">
                  <font class="info"><img src='<%= response.encodeURL("images/info.gif")%>'
                  >&nbsp;&nbsp;<i18n:message key="<%= infoMsg %>"/></font>
                  </dsp:getvalueof>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:setvalue bean="QuicklinksFormHandler.resetFormMessages"/> 
         </dsp:oparam>
      </dsp:droplet>

<dsp:form method="POST" action="<%= paf_success_url %>">

      <core:CreateUrl id="thisFormUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
        <core:UrlParam param="paf_gear_id" value="<%= paf_gear_id %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%= paf_page_url %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= paf_success_url %>"/>
        <core:UrlParam param="paf_community_id" value="<%= paf_community_id %>"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
        <core:UrlParam param="paf_dm" value="full"/>
        <dsp:input type="hidden" bean="QuicklinksFormHandler.successURL"  value="<%= thisFormUrl.getNewUrl() %>"/>
      </core:CreateUrl>

<dsp:droplet name="QuicklinksForEach">
  <dsp:param name="extra" param="extra"/>

  <dsp:oparam name="outputStart">

   <table cellpadding="3">
  </dsp:oparam>

  <dsp:oparam name="output">
    <tr>
      <td>&nbsp;</td>
      <td><font class="small">
        <%= linkLabel %>
        <%-- This hidden input attaches the row to the repository item --%>
        <dsp:input type="hidden" bean='<%=
        "QuicklinksFormHandler.linkIds[" + atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("index") + "]"
        %>' paramvalue="id"/>
      </font></td>
      <td>&nbsp;</td>
      <td><font class="small">
        <dsp:input type="text" maxlength="300" bean='<%=
        "QuicklinksFormHandler.links[" + atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("index") + "]"
        %>' paramvalue="link"/>
      </font></td>
      <td><font class="small"><%= linkNameLabel %></font></td>
      <td><font class="small">
         <dsp:input type="text" maxlength="100" bean='<%=
         "QuicklinksFormHandler.names[" + atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("index") + "]"
         %>' paramvalue="name"/>
      </font></td>
    </tr>
  </dsp:oparam>

  <dsp:oparam name="outputEnd">
   </table>
  </dsp:oparam>

  <dsp:oparam name="more">
    <center>
      <input type="hidden" name="paf_community_id" value="<%= paf_community_id %>"/>
      <input type="hidden" name="paf_gear_id" value="<%= paf_gear_id %>"/>
      <input type="hidden" name="paf_page_url" value="<%= paf_page_url %>"/>
      <input type="hidden" name="paf_success_url" value="<%= paf_success_url %>"/>

      <core:CreateUrl id="moreURL" url="<%= pafEnv.getOriginalRequestURI() %>">
        <core:UrlParam param="paf_gear_id" value="<%= paf_gear_id %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%= paf_page_url %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= paf_success_url %>"/>
        <core:UrlParam param="paf_community_id" value="<%= paf_community_id %>"/>
        <core:UrlParam param="extra" value="5"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
        <core:UrlParam param="paf_dm" value="full"/>
        <dsp:input type="hidden" bean="QuicklinksFormHandler.moreURL"  value="<%= moreURL.getNewUrl() %>"/>
      </core:CreateUrl>

<font class="small">
      <dsp:input type="submit" bean="QuicklinksFormHandler.submit"     value="<%= submitButton %>"/>
      <dsp:input type="submit" bean="QuicklinksFormHandler.more"       value="<%= moreButton %>"/>
</font>
    </center>
  </dsp:oparam>

 </dsp:droplet>

</dsp:form>

</paf:hasCommunityRole>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/quicklinks/quicklinks.war/full/instanceConfig.jsp#2 $$Change: 651448 $--%>

<%-- 
Page:   	configAlerts.jsp
Gear:  	 	Doc Exchange Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by InstanceConfig.jsp, and renders and handles a form to 
      		set configuration options for gear alert preferences
--%>

<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<paf:hasCommunityRole roles="leader,gear-manager">

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">
<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="formTitle" key="configAlertsFormTitle"/>
<i18n:message id="formInstructions" key="configAlertsFormInstructions"/>
<i18n:message id="alertOptionsLabel" key="alertOptionsLabel"/>
<i18n:message id="alertNoOptionDesc" key="alertNoOptionDesc"/>
<i18n:message id="alertLockedOptionDesc" key="alertLockedOptionDesc"/>
<i18n:message id="alertOpenedOptionDesc" key="alertOpenedOptionDesc"/>
<i18n:message id="finishButton" key="finishButton"/>
<% 
   String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String gearName = pafEnv.getGear().getName(response.getLocale());
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String successURL = request.getParameter("paf_success_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
%>


<jsp:useBean id="alertFormInput" scope="request" class="atg.portal.admin.AlertConfigBean">
  <jsp:setProperty name="alertFormInput" property="*"/>
</jsp:useBean>

<!-- handle the form if it was filled in -->
<core:If value="<%= alertFormInput.getHandleForm() %>" >
  <paf:handleAlertConfig id="alertConfResult" 
                    formData="<%= alertFormInput %>" 
		    gearEnv="<%= pafEnv %>">
    <core:ExclusiveIf>
       <core:If value="<%= alertConfResult.getSuccess() %>" >
          <font class="info"><img src='<%= response.encodeURL("images/info.gif")%>'
                  >&nbsp;&nbsp;<i18n:message key="successMessage"/></font>
       </core:If>
      <%-- if not, display errors --%>
      <core:DefaultCase>
       <%=alertConfResult.getErrorMessage()%>
      </core:DefaultCase>
    </core:ExclusiveIf>
  </paf:handleAlertConfig>
</core:If>


<%-- BEGIN FORM --%>
<core:CreateUrl id="formURL" url="<%= origURI%>">
  <core:UrlParam param="config_page" value="alert"/>
  <core:UrlParam param="paf_dm" value="full"/>
  <core:UrlParam param="paf_gm" value="instanceConfig"/>
  <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
  <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
  <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
  <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
  <form ACTION="<%= formURL.getNewUrl() %>" METHOD="POST">
</core:CreateUrl>

  <%--
   Sets form encoding to the same as the encoding of this response.
   This hidden field will let the form processing code know what the character encoding of the POSTed data is. 
   --%>
  <input type="hidden" name="_dyncharset" value="<%= response.getCharacterEncoding() %>">
  <input type="hidden" name="handleForm" value="true">

  <input type="hidden" name="atg.paf.RedirectNoIncludeURL" value="docexch-url">
  <input type="hidden" name="paf_gear_id" value="<%= gearID %>">
  <input type="hidden" name="paf_dm" value="full">
  <input type="hidden" name="paf_gm" value="instanceConfig">
  <input type="hidden" name="config_page" value="alert">
  <input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
  <input type="hidden" name="paf_page_url" value="<%= pageURL %>"/>
  <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>


  <%@ include file="configPageTitleBar.jspf" %>

  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
    <tr><td>
    <table cellpadding="1" cellspacing="0" border="0">

     <tr>
       <td colspan=2><font class="smaller" color="#000000"><%=alertOptionsLabel%></font></td>
     </tr>

    <tr>
      <td colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=5 width=1 border=0></td>
    </tr>

    <% String globalAlertMode = pafEnv.getGearInstanceParameter("globalAlertMode"); %>
    <tr>
      <td colspan=2>
      <core:exclusiveIf>
        <core:if value='<%= globalAlertMode.equals("no") %>'>
          <input type="radio" name="gearAlertPref" value="no" checked><font class="smaller" color="#000000">&nbsp;<%=alertNoOptionDesc%></font>
        </core:if>
        <core:defaultCase>
          <input type="radio" name="gearAlertPref" value="no"><font class="smaller" color="#000000">&nbsp;<%=alertNoOptionDesc%></font>
        </core:defaultCase>
      </core:exclusiveIf>
      </td>
    </tr>

    <tr>
      <td colspan=2> 
      <core:exclusiveIf>
        <core:if value='<%= globalAlertMode.equals("yes_locked") %>'>
          <input type="radio" name="gearAlertPref" value="yes_locked" checked><font class="smaller" color="#000000">&nbsp;<%=alertLockedOptionDesc%></font>
        </core:if>
        <core:defaultCase>
          <input type="radio" name="gearAlertPref" value="yes_locked"><font class="smaller" color="#000000">&nbsp;<%=alertLockedOptionDesc%></font>
        </core:defaultCase>
      </core:exclusiveIf>
      </td>
    </tr>

<%-- not supporting this one just yet...
    <tr>
      <td colspan=2> 
      <input type="radio" name="gearAlertPref" value="yes_opened"><font size="2" color="#000000">&nbsp;<%=alertOpenedOptionDesc%></font>
      </td>
    </tr>
--%>
    <tr>
      <td colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></td>
    </tr>

    <tr>
      <td><INPUT type="submit" value="<%=finishButton%>">&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
  </table>
</td></tr></table><br><br>

</form>

</dex:DocExchPage>
</paf:InitializeGearEnvironment>

</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/configAlerts.jsp#2 $$Change: 651448 $--%>

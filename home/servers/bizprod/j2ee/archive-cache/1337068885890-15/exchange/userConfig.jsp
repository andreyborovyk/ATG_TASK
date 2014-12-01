<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<i18n:bundle baseName="atg.portal.gear.exchange.Exchange" changeResponseLocale="false" />

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler" />

<% String origURI = request.getRequestURI();
   String gearId = gearEnv.getGear().getId();
   String pageId = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityId = request.getParameter("paf_community_id");
   String successURL =  request.getParameter("paf_success_url");
%>

<dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="true"/>

<dsp:form enctype="multipart/form-data" method="post" action="<%= gearEnv.getOriginalRequestURI() %>" >

  <input type="hidden" name="paf_gear_id" value="<%= gearId %>" />
  <input type="hidden" name="paf_dm" value="<%= gearEnv.getDisplayMode() %>" />
  <input type="hidden" name="paf_gm" value="<%= gearEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_id" value="<%= pageId %>" />
  <input type="hidden" name="paf_page_url" value="<%= pageURL %>" />
  <input type="hidden" name="paf_community_id" value="<%= communityId %>" />
  <input type="hidden" name="paf_success_url" value="<%= successURL %>" />

  <dsp:input type="hidden" bean="GearConfigFormHandler.successURL"
                         value="<%= successURL %>" />
  <dsp:input type="hidden" bean="GearConfigFormHandler.paramType"
                         value="user" />
  <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues"
                         value="false" />  

    <blockquote><font class="small_bold"><br />
    <i18n:message key="USER_CONFIG_TITLE">
    <i18n:messageArg value="<%= gearEnv.getGear().getName(response.getLocale()) %>"/>
    </i18n:message>
    </font>
    
    <hr/>
    <table>
  <tr>
    <td><font class="smaller_bold"><i18n:message key="USER_NAME"/></font></td>
    <td>
      <dsp:input type="text" maxlength="254" bean="GearConfigFormHandler.values.userID"
           value='<%= gearEnv.getGearUserParameter("userID") %>'/>
    </td>
  </tr>
  <tr>
    <td><font class="smaller-bold"><b><i18n:message key="PASSWORD"/></font></td>
    <td>
      <dsp:input type="password" maxlength="254" bean="GearConfigFormHandler.values.password"
           value='<%= gearEnv.getGearUserParameter("password") %>'/>
    </td>
  </tr>
    </table>
<!--    <br/>
    <font size="2"><b>Show:</b></font><br/>
    <dsp:input type="checkbox" bean="GearConfigFormHandler.values.showInbox" checked='<%= "true".equals(gearEnv.getGearUserParameter("showInbox")) %>'/>
    <font size=2><b><i18n:message key="INBOX"/></b></font><br/>
    <dsp:input type="checkbox" bean="GearConfigFormHandler.values.showCalendar" checked='<%= "true".equals(gearEnv.getGearUserParameter("showCalendar")) %>'/>
    <font size=2><b><i18n:message key="CALENDAR"/></b></font><br/>
    <dsp:input type="checkbox" bean="GearConfigFormHandler.values.showContacts" checked='<%= "true".equals(gearEnv.getGearUserParameter("showContacts")) %>'/>
    <font size=2><b><i18n:message key="CONTACTS"/></b></font><br/>
    <dsp:input type="checkbox" bean="GearConfigFormHandler.values.showTasks" checked='<%= "true".equals(gearEnv.getGearUserParameter("showTasks")) %>'/>
    <font size=2><b><i18n:message key="TASKS"/></b></font><br/>
    <br/>
-->
    <table>
    <tr>
      <i18n:message key="UPDATE" id="doneStr"/>
      <i18n:message key="CANCEL" id="cancelStr"/>
      <td align="right"><dsp:input type="submit" bean="GearConfigFormHandler.confirm" value="<%= doneStr %>"/></td>
      <td align="left"><dsp:input type="submit" bean="GearConfigFormHandler.cancel" value="<%= cancelStr %>"/></td>
    </tr>
  </table>

</dsp:form>
    
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/exchange-passthrough/exchange.war/userConfig.jsp#1 $$Change: 651360 $--%>

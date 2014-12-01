<%-- 
Page:   	configDisplayParams.jsp
Gear:  	 	Discussion Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and renders and handles a form to 
      		set some gear instance parameters for the discussion gear.
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>


<dsp:page>

<i18n:bundle baseName="atg.gears.discussion.discussionAdmin" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="finishButtonLabel" key="finish-button-label"/>
<i18n:message id="cancelButtonLabel" key="cancel-button-label"/>

<paf:InitializeGearEnvironment id="pafEnv">

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String pafSuccessURL = atg.servlet.ServletUtil.escapeURLString(request.getParameter("paf_success_url"));
%>

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>

<dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="foo"/>

<i18n:message id="i18n_quote" key="html_quote"/>
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="change-display-title">
 <i18n:messageArg value='<%= pafEnv.getGear().getName(response.getLocale()) %>'/>
  <i18n:messageArg value="<%=i18n_quote%>"/>
</i18n:message>  
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%">

<dsp:form enctype="multipart/form-data" method="post" action="<%= origURI %>">
	

  <input type="hidden" name="paf_gear_id" value="<%= gearID %>"/>
  <input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
  <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
  <input type="hidden" name="paf_success_url" value="<%= pafSuccessURL %>"/>

  <core:CreateUrl id="successUrl" url="<%= origURI %>">
    <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
    <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
    <core:UrlParam param="paf_success_url" value="<%= pafSuccessURL%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl()%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.cancelUrl" value="<%= successUrl.getNewUrl()%>"/>
  </core:CreateUrl>



<tr><td>
        <font class="smaller">Use the forms below to change the display attributes of this gear instance.
        </td></tr></table>
  
<img src='/gear/discussion/images/clear.gif' height="1" width="1" border="0"><br>

	
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">

    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="forumCountSharedLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="GearConfigFormHandler.values.forumDisplayCountShared" 
	value='<%= pafEnv.getGearInstanceParameter("forumDisplayCountShared")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/discussion/images/clear.gif" height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="forumCountFullLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="GearConfigFormHandler.values.forumDisplayCountFull" 
	value='<%= pafEnv.getGearInstanceParameter("forumDisplayCountFull")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/discussion/images/clear.gif" height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="topicCountSharedLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="GearConfigFormHandler.values.topicDisplayCountShared" 
	value='<%= pafEnv.getGearInstanceParameter("topicDisplayCountShared")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/discussion/images/clear.gif" height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="topicCountFullLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="GearConfigFormHandler.values.topicDisplayCountFull" 
	value='<%= pafEnv.getGearInstanceParameter("topicDisplayCountFull")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/discussion/images/clear.gif" height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="threadCountLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="GearConfigFormHandler.values.threadDisplayCount" 
	value='<%= pafEnv.getGearInstanceParameter("threadDisplayCount")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/discussion/images/clear.gif" height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="textLenSharedLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="GearConfigFormHandler.values.displayTextLengthShared" 
	value='<%= pafEnv.getGearInstanceParameter("displayTextLengthShared")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/discussion/images/clear.gif" height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="textLenFullLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="GearConfigFormHandler.values.displayTextLengthFull" 
	value='<%= pafEnv.getGearInstanceParameter("displayTextLengthFull")%>'/>
      </td>
    </tr>

    <tr>
      <td colspan=2 ><img src="/gear/discussion/images/clear.gif" height=10 width=1 border=0></td>
    </tr>
    <tr VALIGN="top" ALIGN="left"> 
      <td align="left">&nbsp;</td>
      <td align="left"><dsp:input type="submit" value="<%=finishButtonLabel%>" bean="GearConfigFormHandler.confirm"/>&nbsp;&nbsp;&nbsp;<dsp:input type="submit" value="<%=cancelButtonLabel%>" bean="GearConfigFormHandler.cancel"/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/discussion/images/clear.gif" height=10 width=1 border=0></td>
    </tr>
</dsp:form>
  </TABLE>



</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/admin/configDisplayParams.jsp#2 $$Change: 651448 $--%>

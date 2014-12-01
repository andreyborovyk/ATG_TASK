<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/poll-taglib" prefix="poll" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gear.poll.PollResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="alertNoOptionDesc" key="alertNoOptionDesc"/>
<i18n:message id="alertLockedOptionDesc" key="alertLockedOptionDesc"/>
<i18n:message id="alertOpenedOptionDesc" key="alertOpenedOptionDesc"/>
<i18n:message id="backtogearLink" key="backtogear-link"/>

<paf:InitializeGearEnvironment id="pafEnv">

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String successURL = request.getParameter("paf_success_url");
   String communityID = request.getParameter("paf_community_id");

 %>

<%-- handle poll selection if form was submitted --%>
<core:IfNotNull value='<%= request.getParameter("handleForm")%>'>
  <core:If value='<%= request.getParameter("handleForm")%>'>
    <poll:setGearPoll id="setResult"
		    pollId='<%= request.getParameter("gearPoll") %>'
		    gearId="<%= gearID %>">
      <core:IfNot value="<%= setResult.getSuccess() %>" >
  	   <font class="error"><b><i18n:message key="error-msg-label"/>:&nbsp;<%= setResult.getErrorMessage() %></b></font>
      </core:IfNot>
      <core:If value="<%= setResult.getSuccess() %>" >
  	  <img src="/gear/poll/images/info.gif">&nbsp;&nbsp;<font class="smaller"><i18n:message key="current-poll-feedback-msg"/></font>
      </core:If>
    </poll:setGearPoll>
  </core:If>
</core:IfNotNull>

<core:demarcateTransaction id="pollInstanceConfigXA">

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="admin_current_poll_title"/>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><i18n:message key="admin_current_poll_intro"/>
        </td></tr></table>
  <img src="/gear/poll/images/clear.gif" height=1 width=1 border=0><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">





   <%-- change poll controls --%>

	  <poll:getPoll id="currentPoll"
	         gearId="<%= gearID %>">

 <core:CreateUrl id="formURL" url="<%= origURI %>">
  <form ACTION="<%= formURL.getNewUrl() %>" METHOD="POST">
 </core:CreateUrl>

<%--
 Sets form encoding to the same as the encoding of this response.
 This hidden field will let the form processing code know what the character encoding of the POSTed data is. 
 --%>
   <input type="hidden" name="_dyncharset" value="<%= response.getCharacterEncoding() %>">
   <input type="hidden" name="handleForm" value="true">
   <input type="hidden" name="paf_gear_id" value="<%= gearID %>"/>
   <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
   <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
   <input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
   <input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
   <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
   <input type="hidden" name="paf_success_url" value="<%= successURL %>"/>
   <input type="hidden" name="config_page" value="setCurrentPoll"/>

   <poll:getAllPolls id="polls">
   <tr>
      <td colspan="2">
        <table border="0" cellpadding="0" cellspacing="0">
 	<!--<tr>
	 <td>
	  <font class="smaller_bold"><i18n:message key="current-poll-title"/>&nbsp;</font>
	 </td>
	</tr>
        <tr>
           <td colspan=2><img src="/gear/poll/images/clear.gif" height=5 width=1 border=0></td>
        </tr>-->
 	<tr>
	 <td>
	  <font class="smaller"><i18n:message key="set-poll-msg"/>&nbsp;</font>
	 </td>
	</tr>
	<tr>
	 <td>
	   <select name="gearPoll">
   	    <option value="clear"><i18n:message key="clear-poll-label"/>
	    <core:ForEach id="pollForEach"
	        values="<%= polls.getPollList() %>"
	        castClass="atg.repository.RepositoryItem"
	        elementId="poll">
	    <option value="<%=poll.getRepositoryId()%>" <core:If value="<%= poll.getRepositoryId() == currentPoll.getPollId()%>">selected</core:If> ><%=poll.getPropertyValue("title")%></option>
	    </core:ForEach>
	   </select>
	 </td>
         </tr>
        <tr>
           <td colspan=2><img src="/gear/poll/images/clear.gif" height=10 width=1 border=0></td>
        </tr>
	 <tr>
	  <td><input type="submit" value="<i18n:message key="submit-button-label"/>"></td>
	 </tr>
    </poll:getAllPolls>
         <tr>
            <td colspan=2><img src="/gear/poll/images/clear.gif" height=3 width=1 border=0></td>
         </tr>
        </table>
       </td>
      </tr>
</form>
</poll:getPoll>






   <%-- end change poll controls --%>



    <tr>
      <td colspan=2><img src="/gear/poll/images/clear.gif" height=10 width=1 border=0></td>
    </tr>

</TABLE>

</td></tr></table><br><br>
</core:demarcateTransaction>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/setCurrentPoll.jsp#2 $$Change: 651448 $--%>

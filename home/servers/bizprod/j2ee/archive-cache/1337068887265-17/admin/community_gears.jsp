<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
  
<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
%>
<%--     reset the session scoped form handler --%>
<dsp:setvalue bean="CommunityFormHandler.reset"/>

<dsp:form action="community.jsp" method="POST">

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
	<font class="pageheader_edit"><img src='<%= response.encodeURL("images/write.gif")%>' height="15" width="28" alt="" border="0"><i18n:message key="admin-community-gears-header"/>&nbsp;<%= adminEnv.getCommunity().getName(response.getLocale()) %>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller"><i18n:message key="admin-community-gears-part1"/>
	</td></tr></table>
   
  <img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0"><br>
 <table bgcolor="#BAD8EC" width="100%" cellpadding=1 cellspacing=0 border=0>
  <tr>
  <td><img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="10" border="0"></td>
  <td><img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="10" border="0"></td>
  <td><img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="400" border="0"></td>
  </tr>
  <tr bgcolor="#BAD8EC">
   <td colspan="3"><font class="smaller">

<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  String dsp_community_id = "";
  if(cm != null) {
    dsp_community_id = cm.getId();
  }
%> 
    <dsp:getvalueof id="dsp_page_url" idtype="java.lang.String" param="paf_page_url">
<%--Set the community id into the form before redering the form since the form handler needs it for
    getting the current gear definition folders      --%>
    <dsp:setvalue bean="CommunityFormHandler.id" value="<%=dsp_community_id%>"/><img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0">


   </font></td>
  </tr>
<script language="Javascript">
var saveChecks = new Array();


function clearChecks(){
  for ( i=0;i< document.forms[0].elements.length; i++) {
   if ((document.forms[0].elements[i].name.indexOf("gearDefinitionFolderIds")  > -1 ) &&
       (document.forms[0].elements[i].name.indexOf("_D:") == -1 ) &&
       (document.forms[0].elements[i].checked) )  
   {
      saveChecks[i] = i;
      document.forms[0].elements[i].checked = false;
   } else {
      saveChecks[i] = -1;
   }
  }
}

function restoreChecks() {
  for ( i=0;i< document.forms[0].elements.length; i++) {
   if ((document.forms[0].elements[i].name.indexOf("gearDefinitionFolderIds")  > -1) && 
       (document.forms[0].elements[i].name.indexOf("_D:") == -1) &&
       ( saveChecks[i] > -1 )) 
   {
       document.forms[0].elements[i].checked = true;
   }  
  }
}

function useFolders() {
  for ( i=0;i< document.forms[0].elements.length; i++) {
   if ((document.forms[0].elements[i].name.indexOf("hasFolders")  > -1) && 
       (document.forms[0].elements[i].name.indexOf("_D:") == -1))
   {  
      if ( document.forms[0].elements[i].value == "true")  
      {
         document.forms[0].elements[i].checked = true;
      }
   }  
  }
}
</script>

   <tr bgcolor="#BAD8EC">
    <td colspan="1"><dsp:input type="radio" bean="CommunityFormHandler.hasFolders" value="false" onclick="clearChecks()" /></td>
    <td colspan="2"><font class="small"><i18n:message key="admin-community-gears-radio-all-folders"/></font></td>
   </tr>
   <tr bgcolor="#BAD8EC">
    <td colspan="1"><dsp:input type="radio" bean="CommunityFormHandler.hasFolders" value="true" onclick="restoreChecks()"/></td>
    <td colspan="2"><font class="small"><i18n:message key="admin-community-gears-radio-all-folders-below"/></font></td>
   </tr>

 <admin:GetAllItems id="items">
  <% int checkCount = 0 ; 
     // the javascript above that saves and restores the checkbox states are
     // KEYED  off of the name of the checkbox in the foreach loop below.
     // changing the name of the property below ( "gearDefinitionFolderIds"  ) 
     // also change the refernces in the JavaScript above 
  %>
  <core:ForEach id="folders"
                 values="<%= items.getGearDefinitionFolders(atg.portal.framework.Comparators.getFolderComparator()) %>"
                castClass="atg.portal.framework.folder.GearDefinitionFolder"
                elementId="folder">
   <tr>
    <td>&nbsp;</td>
    <td>

       <dsp:input type="checkbox" bean="CommunityFormHandler.gearDefinitionFolderIds" value="<%=folder.getId()%>" onclick="useFolders()" />


   </td>
   <td><font class="small"><%=folder.getName()%></font></td>
  </tr>

    <core:ForEach id="geardefinitions"
                  values="<%= folder.getGearDefinitionSet(atg.portal.framework.Comparators.getGearDefinitionComparator()) %>"
                  castClass="atg.portal.framework.GearDefinition"
                  elementId="geardef">
     <tr>
      <td colspan="2">&nbsp;</td>
      <td nowrap colspan="1"><font class="smaller">&nbsp;&nbsp;<%= geardef.getName(response.getLocale()) %></font></td>
     </tr>       
    </core:ForEach>
  </core:ForEach>
 </admin:GetAllItems>



<tr><td></td><td colspan="2"><br>

<dsp:getvalueof id="mode" idtype="java.lang.String" param="mode">

 <core:CreateUrl id="templateUrl" url="/portal/admin/community.jsp">
  <core:UrlParam param="mode" value="<%=mode%>"/>
  <core:UrlParam param="paf_communty_id" value="<%=dsp_community_id%>"/>
  <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
   <% String targetSuccessURL = portalServletResponse.encodePortalURL(templateUrl.getNewUrl(),portalContext); %>

 <dsp:input type="hidden" bean="CommunityFormHandler.successURL" value="<%=targetSuccessURL%>"/>
 <dsp:input type="hidden" bean="CommunityFormHandler.failureURL" value="<%=targetSuccessURL%>"/>
 <dsp:input type="hidden" bean="CommunityFormHandler.cancelURL" value="/portal/admin/community.jsp"/>

 </core:CreateUrl>

</dsp:getvalueof>


<i18n:message id="save01" key="save" />
<dsp:input type="submit" bean="CommunityFormHandler.updateGearDefinitionFolders" value="<%= save01 %>" name="Save"/>&nbsp;&nbsp;&nbsp;
<i18n:message id="cancel01" key="cancel" />
<dsp:input type="submit" bean="CommunityFormHandler.cancel" value="<%= cancel01 %>" name="Cancel"/><br><br>
</td></tr>
 </table>

</dsp:getvalueof>
</div>

  <% } catch (Exception e) { %>
  <core:setTransactionRollbackOnly id="rollbackOnlyXA">
    <core:ifNot value="<%= rollbackOnlyXA.isSuccess() %>">
      The following exception was thrown:
      <pre>
       <%= rollbackOnlyXA.getException() %>
      </pre>
    </core:ifNot>
  </core:setTransactionRollbackOnly>
  <% } %>
</core:demarcateTransaction>

</dsp:form>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_gears.jsp#2 $$Change: 651448 $--%>

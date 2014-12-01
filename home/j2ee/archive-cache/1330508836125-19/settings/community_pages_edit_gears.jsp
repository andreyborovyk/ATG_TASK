<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/PageGearsFormHandler"/>

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PageGearsFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="PageGearsFormHandler.resetFormExceptions"/>   
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="PageGearsFormHandler.reset"/>   
  </dsp:oparam>
</dsp:droplet>

<dsp:form name="gearList" action="community_pages.jsp" method="POST" synchronized="/atg/portal/admin/PageGearsFormHandler">


<%@include file="fragments/community_pages_edit_nav.jspf"%>



<%
atg.servlet.DynamoHttpServletRequest dynamoRequest=atg.servlet.ServletUtil.getDynamoRequest(request);
String dsp_page_id = dynamoRequest.getParameter("paf_page_id");
String dsp_ppage_id = dynamoRequest.getParameter("paf_ppage_id");
String dsp_page_url = dynamoRequest.getParameter("paf_page_url");
String dsp_paf_community_id = dynamoRequest.getParameter("communityId"); 
%>

<dsp:setvalue bean="PageGearsFormHandler.pageId" value="<%= dsp_page_id %>" />




<%-- The list of gears that are in community are shown as unselected. The list of gears
               that are in page property of the regions in a page are shown as selected. --%>

   <admin:GetAllItems id="items">

   <core:CreateUrl id="returnURL" url="community_gears.jsp">
    <core:UrlParam param="paf_community_id" value="<%=dsp_paf_community_id%>"/>
    <core:UrlParam param="paf_page_id" value="<%=dsp_page_id%>"/>
    <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>  
     <dsp:setvalue param="paf_success_url"  value="<%= returnURL.getNewUrl() %>" />
   </core:CreateUrl>

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="adminbody"><i18n:message key="community_pages_content_helper"/></font><br><br>

<%
 int totalGears =  adminEnv.getCommunity().getGearSet().size();
 int splitGear  = 100;
 int countGear  = 1 ;
 boolean splitCol = (totalGears > 15) ;
 if ( splitCol ) {
   splitGear =(totalGears / 2)+1;
 }
%>


<table border=0 cellspacing=0 cellpadding=1 width="95%">
<tr><td valign="top">
<table border=0 cellspacing=0 cellpadding=1 width="95%">

 <core:ForEach id="allgears"
                   values="<%= adminEnv.getCommunity().getGearSet(atg.portal.framework.Comparators.getGearComparator()) %>"
                   castClass="atg.portal.framework.Gear"
                   elementId="gear">

 <tr>
  <td width="5" nowrap><font size="smaller">&nbsp;</font></td>
  <td width="10"><dsp:input type="checkbox" 
                            bean="PageGearsFormHandler.gears" 
                            value="<%= gear.getId() %>"  /></td>

  <td width="95%" nowrap><font class="adminbody">&nbsp;&nbsp;<%= gear.getName(response.getLocale()) %></font></td>
 </tr>

<%
 if (splitCol) {
   if ( splitGear == countGear ) {
%>
 </table></td>
 <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
 <td valign="top">
<table border=0 cellspacing=0 cellpadding=1>
<% }
}
 countGear++;
%>
 </core:ForEach>
</table>
<br><br>

</td></tr>
</table>

<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"   value='<%=request.getParameter("paf_community_id")%>'/>
  <core:UrlParam param="paf_page_id"        value='<%=request.getParameter("paf_page_id")%>'/>
  <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>

   <dsp:input type="hidden"  bean="PageGearsFormHandler.successURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>
   <dsp:input type="hidden"  bean="PageGearsFormHandler.failureURL" value="<%=CpagesURLsuccess.getNewUrl()%>"/>

</core:CreateUrl>

<i18n:message id="submitLabel" key="update"/>
<i18n:message id="resetLabel"  key="reset"/>

   <dsp:input onclick="return isEmptyList();" type="SUBMIT" value="<%=submitLabel%>" bean="PageGearsFormHandler.updateGearsAdminMode"/>&nbsp;&nbsp;&nbsp;
   <input type="RESET"  value="<%=resetLabel%>"  />


<script language="Javascript">
<!--

 function isEmptyList() {
   var formRef = eval("document.forms['gearList']");
   if(formRef.elements.length > -1 ) {
      var isNotEmpty = false;
      for ( i = 0 ; i < formRef.elements.length ; i++ ) {
          if (formRef.elements[i].name=="/atg/portal/admin/PageGearsFormHandler.gears") {
             if(formRef.elements[i].checked)  isNotEmpty = true;
          }
      }
   }
   if (! isNotEmpty ) {
     isNotEmpty = confirm('<i18n:message key="community_pages_gears_confirm_no_gears_js_messages"/>');
   }
   return isNotEmpty;
 }

//-->
</script>
   </admin:GetAllItems>
  
  </td></tr></table>

</dsp:form>
</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_pages_edit_gears.jsp#2 $$Change: 651448 $--%>

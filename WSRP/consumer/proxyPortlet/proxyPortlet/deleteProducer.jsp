
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>



<dspel:page>

<dspel:importbean bean="/atg/wsrp/consumer/ProducerFormHandler"/>

<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String)request.getParameter("paf_gear_def_id")); %>


<paf:hasRole roles="portal-admin">

<c:set var="gear_def_id" value="${param['paf_gear_def_id']}"/>

<%
    gearDef = atg.portal.framework.Utilities.getPortalRepositoryTools().
            getGearDefinition((String)request.getAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION));
%>

<%!
     atg.portal.framework.GearDefinition gearDef;

     String getRelativeUrl(String url)
     {
         StringBuffer sb = new StringBuffer(gearDef.getServletContext());
         sb.append(url);
         return sb.toString();
     }
%>

<fmt:bundle basename="atg.wsrp.consumer.ProxyPortletInstallConfigResources">

<c:set var="page_url"><%= getRelativeUrl("/InstallConfig.jsp") %></c:set>
<dspel:form method="post" action="${page_url}">

    <c:url var="failureURL" value="/InstallConfig.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="installConfig"/>
	<c:param name="paf_gear_def_id" value="${gear_def_id}"/>
	<c:param name="config_page" value="Producer1"/>
    </c:url>

    <dspel:input type="hidden" bean="ProducerFormHandler.failureURL" value="${failureURL}"/>

  <%-- required fields for navigation and processing --%>
  <c:url var="successURL" value="/InstallConfig.jsp">
    <c:param name="paf_dm" value="full"/>
    <c:param name="paf_gm" value="installConfig"/>
    <c:param name="paf_gear_def_id" value="${gear_def_id}"/>
    <c:param name="config_page" value="Producer1"/>
  </c:url>

  <dspel:input type="hidden" bean="ProducerFormHandler.successURL" value="${successURL}"/>


  <input type="hidden" name="paf_gear_def_id" value="<c:out value="${gear_def_id}"/>"/>
  <input type="hidden" name="paf_dm" value="full"/>
  <input type="hidden" name="paf_gm" value="<%=gearDef.getGearMode() %>"/>



<font class="small">
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
<fmt:message key="producer-delete-header"/></font>
</td></tr></table>
</td></tr></table>
<%--<img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border=""><br>--%>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td><font class="smaller">


<tr>
<td><font class="smaller">

 <fmt:message key="producer-delete-confirm">
        <c:set var="bold"><fmt:message key="html-bold"/></c:set>
        <fmt:param value="${bold}"/>

        <c:set var="producerName"><%=request.getParameter("producer_name")%></c:set>
        <fmt:param value="${producerName}"/>

        <c:set var="endBold"><fmt:message key="html-end-bold"/></c:set>
        <fmt:param value="${endBold}"/>
 </fmt:message>

</font>
<br><br>


<fmt:message key="yes" var="yes01"/>
<dspel:input type="submit" bean="ProducerFormHandler.deleteProducer" value="${yes01}" name="Yes"/>
<input type="hidden" name="producer_id" value='<%=request.getParameter("producer_id")%>'/>


<fmt:message key="no" var="no01"/>&nbsp;&nbsp;&nbsp;
<input type="submit" value='<c:out value="${no01}"/>' name="No"/>

</td></tr></table>

</dspel:form>


</fmt:bundle>

</paf:hasRole>


</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/deleteProducer.jsp#2 $$Change: 651448 $--%>

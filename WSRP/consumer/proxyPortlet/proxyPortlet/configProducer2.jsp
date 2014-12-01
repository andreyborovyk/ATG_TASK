 <%--
 Configure Producer page for the InstallConfig gear mode for the consumer proxy portlet.
 --%>

<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>



<dspel:page>

<paf:hasRole roles="portal-admin">

<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String)request.getParameter("paf_gear_def_id")); %>

<%@ include file="top_nav_bar.jspf"%>

<c:set var="gear_def_id"><%=request.getParameter("paf_gear_def_id")%></c:set>

<dspel:importbean bean="/atg/wsrp/consumer/ProducerFormHandler" var="ProducerFormHandler"/>


<%@ include file="form_messages.jspf"%>

<%--<paf:InitializeGearEnvironment id="pafEnv">--%>

<%-- get all internationalized strings that will be used on this page --%>

<fmt:bundle basename="atg.wsrp.consumer.ProxyPortletInstallConfigResources">


<%-- producerName would have been set to "Continue" as specified by <dspel:input> for "Continue" submit button.
So reassign the producer name --%>
<%--<dspel:setvalue bean="ProducerFormHandler.producerName" paramvalue="producer_name"/>--%>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<!-- begin form -->
<c:set var="page_url"><%= getRelativeUrl("/InstallConfig.jsp") %></c:set>
<%--<dspel:form method="post" name="configForm2" action='<%= getRelativeUrl("/InstallConfig.jsp") %>'>--%>
<dspel:form method="post" name="configForm2" action="${page_url}">

    <c:url var="failure_url" value="/InstallConfig.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="installConfig"/>
	<c:param name="paf_gear_def_id" value="${gear_def_id}"/>
	<c:param name="config_page" value="Producer2"/>
    </c:url>

    <dspel:input type="hidden" bean="ProducerFormHandler.failureURL" value="${failure_url}"/>

  <%-- required fields for navigation and processing --%>
  <%--<core:CreateUrl id="successUrl" url='<%= getRelativeUrl("/InstallConfig.jsp") %>'>
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gm" value="installConfig"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <core:UrlParam param="config_page" value="Producer3"/>
    <dspel:input type="hidden" bean="ProducerFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>--%>

  <input type="hidden" name="paf_gear_def_id" value="<c:out value="${gear_def_id}"/>"/>
  <input type="hidden" name="paf_dm" value="full"/>
  <input type="hidden" name="paf_gm" value="<%=gearDef.getGearMode() %>"/>
  <input type="hidden" name="config_page" value="Producer3"/>

</td></tr></table>
 <img src='<%= getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0><br>

 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
        <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>

        <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
      <font class="pageheader_edit">
           <fmt:message key="props-producer"/>
    </td></tr></table>
  </td></tr></table>
</TD></TR>



    <TR>
      <TD WIDTH="15%" align="left" valign="top">
        <font class="smaller"><fmt:message key="url-producer"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="85%" align="left" valign="top">
      <dspel:input type="text" name="producer_url" bean="ProducerFormHandler.producerURL" size="39"/>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="15%" align="left" valign="top">
        <font class="smaller"><fmt:message key="desc-producer"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="85%" align="left" valign="top">
      <dspel:textarea name="producer_description" bean="ProducerFormHandler.producerDescription" rows="2" cols="33"/>
      </TD>
    </TR>


    <TR>
    <TD colspan=2><br></TD>
    </TR>


    <TR>
    <TD colspan=2>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
      <font class="pageheader_edit">
           <fmt:message key="add-props-producer"/>
    </td></tr></table>
    </td></tr></table>

    </TD></TR>


    <TR>
      <TD WIDTH="30%" align="left" valign="top">
        <font class="smaller"><fmt:message key="serv-desc-url"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="70%" align="left" valign="top">
      <dspel:input type="text" name="producer_service_desc_url" bean="ProducerFormHandler.serviceDescriptionURL" size="39"/>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="30%" align="left" valign="top">
        <font class="smaller"><fmt:message key="markup-url"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="70%" align="left" valign="top">
      <dspel:input type="text" name="markup_url" bean="ProducerFormHandler.markupURL" size="39"/>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="30%" align="left" valign="top">
        <font class="smaller"><fmt:message key="reg-url"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="70%" align="left" valign="top">
      <dspel:input type="text" name="registration_url" bean="ProducerFormHandler.registrationURL" size="39"/>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="30%" align="left" valign="top">
        <font class="smaller"><fmt:message key="mgmt-url"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="70%" align="left" valign="top">
      <dspel:input type="text" name="portlet_mgmt_url" bean="ProducerFormHandler.portletManagementURL" size="39"/>
      </TD>
    </TR>


    <TR>
    <TD colspan=2></TD>
    </TR>



    <TR><TD colspan=2><br></TD></TR>

    <TR>
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left">
      <c:set var="continueBtn"><fmt:message key="continueButton"/></c:set>
        <dspel:input type="submit" value="${continueBtn}" bean="ProducerFormHandler.serviceDescription"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dspel:form>



<%--</paf:InitializeGearEnvironment>--%>

</fmt:bundle>

</paf:hasRole>

</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/configProducer2.jsp#2 $$Change: 651448 $--%>

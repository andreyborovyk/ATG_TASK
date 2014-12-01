<%@ page import="oasis.names.tc.wsrp.v1.types.PropertyDescription"%>
 <%--
 Register Producer page for the InstallConfig gear mode for the consumer proxy portlet.
 --%>

<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>



<dspel:page>

<paf:hasRole roles="portal-admin">

  <% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>

<%@ include file="top_nav_bar.jspf"%>

<c:set var="gear_def_id"><%=request.getParameter("paf_gear_def_id")%></c:set>

<dspel:importbean bean="/atg/wsrp/consumer/ProducerFormHandler" var="ProducerFormHandler"/>



  <%@ include file="form_messages.jspf"%>

<%--<paf:InitializeGearEnvironment id="pafEnv">--%>

<fmt:bundle basename="atg.wsrp.consumer.ProxyPortletInstallConfigResources">


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>


<!-- begin form -->
<c:set var="page_url"><%= getRelativeUrl("/InstallConfig.jsp") %></c:set>
<dspel:form method="post" name="configForm3" action="${page_url}">

    <c:url var="failureURL" value="/InstallConfig.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="installConfig"/>
	<c:param name="paf_gear_def_id" value="${gear_def_id}"/>
	<c:param name="config_page" value="Producer3"/>
    </c:url>

    <dspel:input type="hidden" bean="ProducerFormHandler.failureURL" value="${failureURL}"/>

  <c:url var="success_url" value="/InstallConfig.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="installConfig"/>
	<c:param name="paf_gear_def_id" value="${gear_def_id}"/>
    <c:param name="config_page" value="Producer4"/>
  </c:url>

    <dspel:input type="hidden" bean="ProducerFormHandler.successUrl" value="${success_url}"/>

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
           <fmt:message key="props-reg"/>
    </td></tr></table>
  </td></tr></table>
</TD></TR>



    <TR>
      <TD WIDTH="25%" align="left" valign="top">
        <font class="smaller"><fmt:message key="name-consumer"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%" align="left" valign="top">
      <input type="text" name="consumer_name" size="33" value="ATG WSRP Consumer Proxy-Portlet"/>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="25%" align="left" valign="top">
        <font class="smaller"><fmt:message key="agent-consumer"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%" align="left" valign="top">
      <input type="text" name="consumer_agent" size="33" value='<c:out value= "${ProducerFormHandler.consumerAgent}"/>'/>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="25%" align="left" valign="top">
        <font class="smaller"><fmt:message key="get-method"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%" align="left" valign="top">
      <font class="smaller">
      <input type="radio" name="get_support" value="true">True&nbsp;&nbsp;<input type="radio" name="get_support" value="false" checked>False
      </font>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="25%" align="left" valign="top">
        <font class="smaller"><fmt:message key="modes"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%" align="left" valign="top">
      <font class="smaller">
      <dspel:input type="checkbox" readonly="true" bean="ProducerFormHandler.view"/>&nbsp;View&nbsp;&nbsp;
      <dspel:input type="checkbox" bean="ProducerFormHandler.edit"/>&nbsp;Edit&nbsp;&nbsp;
      <dspel:input type="checkbox" bean="ProducerFormHandler.help"/>&nbsp;Help&nbsp;&nbsp;
      <dspel:input type="checkbox" bean="ProducerFormHandler.preview"/>&nbsp;Preview&nbsp;&nbsp;

      </font>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="25%" align="left" valign="top">
        <font class="smaller"><fmt:message key="win-states"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%" align="left" valign="top">
      <font class="smaller">
      <dspel:input type="checkbox" readonly="true" bean="ProducerFormHandler.normal"/>&nbsp;Normal&nbsp;&nbsp;
      <dspel:input type="checkbox" bean="ProducerFormHandler.minimized"/>&nbsp;Minimized&nbsp;&nbsp;
      <dspel:input type="checkbox" bean="ProducerFormHandler.maximized"/>&nbsp;Maximized&nbsp;&nbsp;
      <dspel:input type="checkbox" bean="ProducerFormHandler.solo"/>&nbsp;Solo&nbsp;&nbsp;

      </font>
      </TD>
    </TR>
    <TR>
      <TD WIDTH="25%" align="left" valign="top">
        <font class="smaller"><fmt:message key="user-scope"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%" align="left" valign="top">
      <font class="smaller">
      <input type="radio" name="user_scope" value="wsrp:perUser" checked>Per User&nbsp;&nbsp;<input type="radio" name="user_scope" value="wsrp:forAll">For All
      </font>
      </TD>
    </TR>

    <TR>
    <TD colspan=2><br></TD>
    </TR>

<!-- any additional registration properties if reqd by the producer -->
<c:if test="${ProducerFormHandler.registrationPropertyDescription != null}">
<%-- registrationPropertyDescription in service description is not null so we do need some additional properties to be provided by user --%>

<%-- if registrationPropertyDescription in service description is not null so we do need some additional properties to be provided by user --%>
  <dspel:getvalueof var="propertiesArray" bean="ProducerFormHandler.registrationPropertyDescription.propertyDescriptions">
  <c:forEach var="propertyDescription" items="${propertiesArray}" varStatus="status">

  <c:if test="${status.index==0}">

    <%-- Display a header for the properties in the array: --%>
    <TR>
    <TD colspan=2>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
      <font class="pageheader_edit">
           <fmt:message key="add-props-reg"/>
    </td></tr></table>
    </td></tr></table>

    </TD></TR>

  </c:if>


    <%--
    The PropertyDescription WSRP structure has a required "name" property while the "label"
    property is optional which is a short human readable name for the property.
    All the registration property values will be passed to the form handler as request parameters
    corresponding to their names. If this label is present (not null) then the UI will show that
    label value for the particular property and if the label is not present then the UI will use
    the name itself in UI.
    --%>

    <c:choose>
    <c:when test="${propertyDescription.label == null}">
    <%-- label property is null for the current PropertyDescription so we got to use name property --%>
    <c:set var="propertyLabel"><c:out value="${propertyDescription.name}"/></c:set>
    </c:when>
    <c:otherwise>
    <%-- label property is present for the current PropertyDescription so use its value --%>
    <c:set var="propertyLabel"><c:out value="${propertyDescription.label.value}"/></c:set>
    </c:otherwise>
    </c:choose>

    <TR>
      <TD WIDTH="30%" align="left" valign="top">
        <font class="smaller">
        <c:out value="${propertyLabel}"/>
        <fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="70%" align="left" valign="top">
      <%--the input parameter will be taken on the name basis --%>
      <input type="text" name="<c:out value="${propertyDescription.name}"/>" size="30"/>
      </TD>
    </TR>

</c:forEach>
    </dspel:getvalueof>

<%-- finish stuff for registration properties --%>
    <TR>
    <TD colspan=2></TD>
    </TR>

</c:if>

    <TR><TD colspan=2><br></TD></TR>

    <TR>
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left">
      <c:set var="continueBtn"><fmt:message key="continueButton"/></c:set>
        <dspel:input type="submit" value="${continueBtn}" bean="ProducerFormHandler.registrationContext"/>
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
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/configProducer3.jsp#2 $$Change: 651448 $--%>

<%--
 New/Existing WSRP Producer page for the InstallConfig gear mode for the consumer proxy portlet.
 --%>

<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>



<dspel:page>

<dspel:importbean bean="/atg/wsrp/consumer/ProducerFormHandler" var="ProducerFormHandler"/>

<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>

<paf:hasRole roles="portal-admin">

<%@ include file="top_nav_bar.jspf"%>

<%@ include file="form_messages.jspf"%>

<c:set var="gear_def_id"><%=request.getParameter("paf_gear_def_id")%></c:set>


<fmt:bundle basename="atg.wsrp.consumer.ProxyPortletInstallConfigResources">

<%   String style1= " class='smaller'"; %>

<c:choose>
  <c:when test="${ProducerFormHandler.formError == true}" >
    <dspel:setvalue bean="ProducerFormHandler.resetFormExceptions"/>
  </c:when>
  <c:otherwise>
    <dspel:setvalue bean="ProducerFormHandler.reset"/>
  </c:otherwise>
</c:choose>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<!-- begin form -->
<c:set var="page_url"><%= getRelativeUrl("/InstallConfig.jsp") %></c:set>
<%--<dsp:form method="post" name="configForm1" formid="form1" action='<%= getRelativeUrl("/InstallConfig.jsp") %>'>--%>
<dspel:form method="post" name="configForm1" formid="form1" action="${page_url}">

    <c:url var="failure_url" value="/InstallConfig.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="installConfig"/>
	<c:param name="paf_gear_def_id" value="${gear_def_id}"/>
	<c:param name="config_page" value="Producer1"/>
    </c:url>

    <dspel:input type="hidden" bean="ProducerFormHandler.failureURL" value="${failure_url}"/>

  <input type="hidden" name="paf_gear_def_id" value="<c:out value="${gear_def_id}"/>"/>
  <input type="hidden" name="paf_dm" value="full"/>
  <input type="hidden" name="paf_gm" value="<%=gearDef.getGearMode() %>"/>
  <input type="hidden" name="config_page" value="Producer2"/>

</td></tr></table>
 <img src='<%= getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0/><br>

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
           <fmt:message key="new-producer"/>
    </td></tr></table>
  </td></tr></table>
</TD></TR>


    <TR>
      <TD WIDTH="15%" align="left" valign="top">
        <font class="smaller"><fmt:message key="name-producer"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="85%" align="left" valign="top">
        <dspel:input type="text" name="producer_name" bean="ProducerFormHandler.producerName" size="30"/>

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
           <fmt:message key="existing-producer"/>
    </td></tr></table>
    </td></tr></table>

    </TD></TR>

    <TR>
    <TD colspan=2></TD>
    </TR>

<dspel:getvalueof var="producerArray" bean="ProducerFormHandler.allRegisteredProducers">

<c:set var="counter" value="0"/>
<c:forEach var="ExistingProducerDataObject" items="${producerArray}" begin="0" step="1" varStatus="status">

    <%-- Display a header beforing looping thru producers in the array: --%>
    <c:if test="${status.index==0}">

    <TR>
    <TD WIDTH="20%" align="left" valign="top">
      <font class="smaller"><fmt:message key="registered-producer"/><fmt:message key="separator"/>&nbsp;&nbsp;
    </TD>
    <TD WIDTH="80%" align="left" valign="top">

    <%-- Table for showing up the already registered producers --%>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="75%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">

    </c:if>

<%-- display this output for each of the offerd portlet elements: --%>

<!-- producer -->
    <tr>
    <td width="60%" align="left" valign="top">
      <font class="medium">
      <c:out value="${ExistingProducerDataObject.producerName}"/>
      </td>
      <td width="20%" align="left" valign="top">

      <%-- create the "Modify" link for the current producer --%>
      <c:url var="modify_url" value="/InstallConfig.jsp">
	  <c:param name="paf_dm" value="full"/>
	  <c:param name="paf_gm" value="installConfig"/>
	  <c:param name="paf_gear_def_id" value="${gear_def_id}"/>
	  <c:param name="config_page" value="Producer2"/>
      <%-- pass the producer's name & repository ID as a parameter to identify this producer --%>
      <c:param name="producer_id" value="${ExistingProducerDataObject.repositoryId}"/>
      <c:param name="producer_name" value="${ExistingProducerDataObject.producerName}"/>
      </c:url>

      <dspel:a href="${modify_url}" bean="ProducerFormHandler.modifyRegistration" value="true">
      <font class=<%=style1%> color="#0000FF"><fmt:message key="modify-producer"/></font>
      </dspel:a>

      </td>

      <td width="20%" align="left" valign="top">

      <%-- create the "Delete" link for the current producer --%>
      <c:url var="delete_url" value="/InstallConfig.jsp">
	  <c:param name="paf_dm" value="full"/>
	  <c:param name="paf_gm" value="installConfig"/>
	  <c:param name="paf_gear_def_id" value="${gear_def_id}"/>
	  <c:param name="config_page" value="Delete"/>
      <%-- pass the producer's name & repository ID as a parameter to identify this producer --%>
      <c:param name="producer_id" value="${ExistingProducerDataObject.repositoryId}"/>
      <c:param name="producer_name" value="${ExistingProducerDataObject.producerName}"/>
      </c:url>

      <dspel:a href="${delete_url}" bean="ProducerFormHandler.modifyRegistration" value="false">
        <font class=<%=style1%> color="#0000FF"><fmt:message key="delete-producer"/></font>
      </dspel:a>

      </td>
    </tr>

  <c:if test="${status.last}">

    </table>
    </td></tr></table>  <%-- finish up table for already registered producers --%>

  </c:if>

   <c:set var="counter" value="${counter+1}"/>
</c:forEach>

    <%-- if we don't have already registered producers then counter would still be zero after the loop --%>
    <c:if test="${counter==0}">
        <tr><td colspan=2><b>There are no registered producers!</b></td></tr>
    </c:if>


</dspel:getvalueof>

    </TD>
    </TR>

    <TR><TD colspan=2><br></TD></TR>

    <TR>
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left">

      <%--<dsp:input type="submit" bean="ProducerFormHandler.producerName" value="<%=continueButton%>"/>--%>
      <input type="submit" value="<fmt:message key="continueButton"/>"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dspel:form>

</fmt:bundle>

</paf:hasRole>

</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/configProducer1.jsp#2 $$Change: 651448 $--%>

<%@ page import="atg.wsrp.consumer.persistence.om.RepositoryPortletDescription"%>

<%--
Manage Portlet page for the InstallConfig gear mode for the consumer proxy portlet.
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

<dspel:getvalueof var="producerName" vartype="java.lang.String" bean="ProducerFormHandler.producerName"/>
<dspel:getvalueof var="serviceDescription" vartype="oasis.names.tc.wsrp.v1.types.ServiceDescription" bean="ProducerFormHandler.serviceDescription"/>


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<!-- begin form -->
<c:set var="page_url"><%= getRelativeUrl("/InstallConfig.jsp") %></c:set>
<dspel:form method="post" name="configForm4" action="${page_url}">

    <c:url var="failure_url" value="/InstallConfig.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="installConfig"/>
	<c:param name="paf_gear_def_id" value="${gear_def_id}"/>
	<c:param name="config_page" value="Producer4"/>
    </c:url>

    <dspel:input type="hidden" bean="ProducerFormHandler.failureURL" value="${failure_url}"/>

  <c:url var="success_url" value="/InstallConfig.jsp">
    <c:param name="config_page" value="Producer1"/>
    <c:param name="paf_gear_def_id" value="${gear_def_id}"/>
    <c:param name="paf_dm" value="full"/>
    <c:param name="paf_gm" value="installConfig"/>
  </c:url>

  <dspel:input type="hidden" bean="ProducerFormHandler.successUrl" value="${success_url}"/>

  <input type="hidden" name="paf_gear_def_id" value="<c:out value="${gear_def_id}"/>"/>
  <input type="hidden" name="paf_dm" value="full"/>
  <input type="hidden" name="paf_gm" value="installConfig"/>
  <%--<input type="hidden" name="config_page" value="Producer1"/>--%>

</td></tr></table>

<img src='<%= getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0/><br>

 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
<%--        <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>--%>
    <TR>
    <TD colspan=2 VALIGN="top" ALIGN="left">
      <font class="pageheader_edit">
      <%-- This message should come up ONLY if every thing is fine during the register operation otherwise we need to
      display failure message and in that case producer offered portlets list will not be shown. --%>
      <c:if test="${ProducerFormHandler.registrationRequired == true}" >
      <fmt:message key="success-msg">
        <c:set var="producer_name">
        <c:out value="${producerName}"/>
        </c:set>
        <fmt:param value="${producer_name}"/>
      </fmt:message>
      </c:if>

      </font>
    </TD>
    </TR>

        <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="75%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
    <tr>
    <td width="35%" align="left" valign="top">
    <font class="smaller"><fmt:message key="init-cookie"/><fmt:message key="separator"/>&nbsp;&nbsp;</font>
    </td>
    <td width="65%" align="left" valign="top">
    <font class="smaller">
    <c:out value="${serviceDescription.requiresInitCookie}"/>
    </font>
    </td>
    </tr>
    <tr>
    <td width="35%" align="left" valign="top">
    <font class="smaller"><fmt:message key="reg"/><fmt:message key="separator"/>&nbsp;&nbsp;</font>
    </td>
    <td width="65%" align="left" valign="top">
    <font class="smaller">
    <c:out value="${serviceDescription.requiresRegistration}"/>
    </font>
    </td>
    </tr>
    <tr>
    <td width="35%" align="left" valign="top">
    <font class="smaller"><fmt:message key="custom-mode"/><fmt:message key="separator"/>&nbsp;&nbsp;</font>
    </td>
    <td width="65%" align="left" valign="top">

    <%--If producer supports custom modes then display them with a sub-table for item name & description for each custom mode --%>

    <c:choose>
        <c:when test="${serviceDescription.customModeDescriptions == null}">
            <font class="smaller">-</font>
        </c:when>
        <c:otherwise>

            <c:forEach var="CustomModeDescription" items="${serviceDescription.customModeDescriptions}" varStatus="customModesStatus">

            <%-- TODO proper table/formatting may be provided here for showing up custom portlet modes --%>

            <font class="smaller">
            <c:out value="${CustomModeDescriptions.itemName}"/> &nbsp; <fmt:message key="separator"/> &nbsp;
            <c:choose>
                <c:when test="${CustomModeDescriptions.description != null}">
                    <c:out value="${CustomModeDescriptions.description.value}"/>
                </c:when>
                <c:otherwise>
                -
                </c:otherwise>
            </c:choose>
            </font>

            </c:forEach>

        </c:otherwise>
    </c:choose>

    </td>
    </tr>
    <tr>
    <td width="35%" align="left" valign="top">
    <font class="smaller"><fmt:message key="custom-win-state"/><fmt:message key="separator"/>&nbsp;&nbsp;</font>
    </td>
    <td width="65%" align="left" valign="top">

    <%--If producer supports custom window states then display them with a sub-table for item name & description for each custom window state --%>
    <c:choose>
        <c:when test="${serviceDescription.customWindowStateDescriptions == null}">
            <font class="smaller">-</font>
        </c:when>
        <c:otherwise>

            <c:forEach var="CustomWindowStateDescription" items="${serviceDescription.customWindowStateDescriptions}" varStatus="customStatesStatus">

            <%-- TODO proper table/formatting may be provided here for showing up custom window states --%>

            <font class="smaller">
            <c:out value="${CustomWindowStateDescription.itemName}"/> &nbsp; <fmt:message key="separator"/> &nbsp;
            <c:choose>
                <c:when test="${CustomWindowStateDescription.description != null}">
                    <c:out value="${CustomWindowStateDescription.description.value}"/>
                </c:when>
                <c:otherwise>
                -
                </c:otherwise>
            </c:choose>
            </font>

            </c:forEach>

        </c:otherwise>
    </c:choose>

    </td>
    </tr>

    </table>
  </td></tr></table>
</TD>
</TR>

<%-- All the offered portlets from just registered producer will come here --%>

<%--<dspel:getvalueof var="offeredPortletArray" bean="ProducerFormHandler.serviceDescription.offeredPortlets">--%>
<c:set var="offeredPortletArray" value="${serviceDescription.offeredPortlets}"/>

<c:set var="counter" value="0"/>
<c:forEach var="OfferedPortlet" items="${offeredPortletArray}" varStatus="status">

    <%-- Display a header before looping thru portlets in the array: --%>
    <c:if test="${status.index==0}">

    <TR>
    <TD colspan=2>
    <font class="pageheader_edit">
    <br><fmt:message key="offered-portlet-msg"/><br></font>
    </TD>
    <TR>

    <TR>
    <TD colspan=2>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="75%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="1" bordercolor="#666666" bgcolor="#BAD8EC" width="100%">
    <tr>
    <td bgcolor="#CBD1D7" width="35%" align="center" valign="top"><font class="pageheader_edit">Offered Portlet</font></td>
    <td bgcolor="#CBD1D7" width="55%" align="center" valign="top"><font class="pageheader_edit">Description</font></td>
    <td bgcolor="#CBD1D7" width="10%" align="center" valign="top"><font class="pageheader_edit">Add</font></td>
    </tr>

    </c:if>

<%-- display this output for each of the offerd portlet elements: --%>
    <tr>
    <td width="35%" align="left" valign="top">

<%-- All the offered portlets will be shown by their display name. However in case, display name is not present
the portlets will be shown by their title and if title is also not present then short title will be tried.
(WSRP spec. page 20 sec. 45)--%>

 <c:choose>             <%-- start checking for the display name --%>
    <c:when test="${OfferedPortlet.displayName == null}" >
    <%--display name is null so look for the short title --%>
        <c:choose>      <%-- start checking for short title --%>
        <c:when test="${OfferedPortlet.shortTitle == null}">
        <%--short title is also null so look for the title --%>
            <c:choose>      <%-- start checking for title --%>
            <c:when test="${OfferedPortlet.title == null}">
               <%-- TODO handle the case when display name, title and short title all are null --%>
               <%-- what to do ??? display name, title, short title all are null --%>
            </c:when>
            <c:otherwise>
            <%-- title is present so show title --%>
            <font class="smaller"><c:out value="${OfferedPortlet.title.value}"/></font>
            </c:otherwise>
            </c:choose>     <%-- end checking for title --%>
        </c:when>
        <c:otherwise>
        <%--short title is present so show it --%>
        <font class="smaller"><c:out value="${OfferedPortlet.shortTitle.value}"/></font>
        </c:otherwise>
        </c:choose>         <%-- end checking for short title --%>
    </c:when>
    <c:otherwise>
    <%--display name is present so show it --%>
    <font class="smaller"><c:out value="${OfferedPortlet.displayName.value}"/></font>
    </c:otherwise>
</c:choose>                 <%-- end checking for display name --%>

    </td>
    <td width="55%" align="left" valign="top">
    <font class="smaller">
    <c:out value="${OfferedPortlet.description.value}"/> &nbsp;&nbsp;
    </font></td>
    <td width="10%" align="center" valign="top">

     <input type="checkbox" name="<c:out value="${offeredPortletArray[ counter ].portletHandle}"/>" value="true"/>

   </td>
    </tr>

    <%--    "outputEnd" --%>
    <c:if test="${status.last}">

    </table>
    </td></tr></table> <%-- table for the offered portlets --%>

    </TD></TR>

    </c:if>

    <c:set var="counter" value="${counter+1}"/>

    </c:forEach>

    <%-- if we don't have any offered portlets then counter would still be zero after the loop --%>
    <c:if test="${counter==0}">
        <tr><td colspan=2><b>There are no portlets offered by the producer "<c:out value="${producerName}"/>"!</b></td></tr>
    </c:if>

    <TR>
    <TD colspan=2></TD>
    </TR>
    <%-- show all the existing portlets --%>
<dspel:getvalueof var="producerArray" bean="ProducerFormHandler.allRegisteredProducers">

<c:set var="counter2" value="0"/>
<c:forEach var="ExistingProducerDataObject" items="${producerArray}" varStatus="producerStatus">

    <%-- Display a header beforing looping thru producers in the array: --%>
    <c:if test="${producerStatus.index==0}">

    <TR>
    <TD colspan=2>
    <br><font class="pageheader_edit">
    <fmt:message key="consumer-portlet-msg"/><br></font>
    </TD>
    </TR>

     <TR>
     <TD colspan=2>

     <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="90%">
     <tr><td>
        <table cellpadding="4" cellspacing="0" border="1" bordercolor="#666666" bgcolor="#BAD8EC" width="100%">
          <tr>
          <td bgcolor="#CBD1D7" width="20%" align="center" valign="top"><font class="pageheader_edit">Portlet</font></td>
          <td bgcolor="#CBD1D7" width="40%" align="center" valign="top"><font class="pageheader_edit">Description</font></td>
          <td bgcolor="#CBD1D7" width="20%" align="center" valign="top"><font class="pageheader_edit">Producer</font></td>
          <td bgcolor="#CBD1D7" width="15%" align="center" valign="top"><font class="pageheader_edit">Type</font></td>
          <td bgcolor="#CBD1D7" width="5%" align="center" valign="top"><font class="pageheader_edit">Remove</font></td>
          </tr>

    </c:if>     <%-- finish header stuff for the producer loop --%>

  <%-- get the Set of available portlets --%>
  <c:if test="${ExistingProducerDataObject.availablePortlets != null}">

 <%-- further loop thru the available portlets array from the current producer --%>
  <c:forEach var="portletDescription" items="${ExistingProducerDataObject.availablePortlets}" varStatus="availablePortletsStatus">

     <tr>
     <td width="20%" align="left" valign="top">

 <c:choose>             <%-- start checking for the display name --%>
    <c:when test="${portletDescription.displayName == null}" >
    <%--display name is null so look for the short title --%>
        <c:choose>      <%-- start checking for short title --%>
        <c:when test="${portletDescription.shortTitle == null}">
        <%--short title is also null so look for the title --%>
            <c:choose>      <%-- start checking for title --%>
            <c:when test="${portletDescription.title == null}">
               <%-- TODO handle the case when display name, title and short title all are null --%>
               <%-- what to do ??? display name, title, short title all are null --%>
            </c:when>
            <c:otherwise>
            <%-- title is present so show title --%>
            <font class="smaller"><c:out value="${portletDescription.title.value}"/></font>
            </c:otherwise>
            </c:choose>     <%-- end checking for title --%>
        </c:when>
        <c:otherwise>
        <%--short title is present so show it --%>
        <font class="smaller"><c:out value="${portletDescription.shortTitle.value}"/></font>
        </c:otherwise>
        </c:choose>         <%-- end checking for short title --%>
    </c:when>
    <c:otherwise>
    <%--display name is present so show it --%>
    <font class="smaller"><c:out value="${portletDescription.displayName.value}"/></font>
    </c:otherwise>
</c:choose>                 <%-- end checking for display name --%>

     </td>
     <td width="40%" align="left" valign="top"><font class="smaller">

     <c:choose>
         <c:when test="${portletDescription.description != null}" >
              <c:out value="${portletDescription.description.value}"/>
         </c:when>
         <c:otherwise>
              -
         </c:otherwise>
     </c:choose>
     &nbsp;&nbsp;
     </font></td>

     <td width="20%" align="left" valign="top"><font class="smaller">
     <c:out value="${ExistingProducerDataObject.producerName}"/>
     </font></td>


     <td width="15%" align="left" valign="top"><font class="smaller">
     <c:out value="${portletDescription.portletType}"/>
     </font></td>
     <td width="5%" align="center" valign="top"><font class="smaller">
     <input type="checkbox" name="<c:out value="${portletDescription.repositoryId}"/>" value="true"/>
     <%--<dspel:input type="checkbox" bean='<%="ProducerFormHandler.allRegisteredProducers[" + prodIndex + "].availablePortlets["+ portletIndex +"].markedToDelete"%>'/>--%>
     </font></td>
     </tr>

 </c:forEach>    <%-- End of forEach tag for available portlets--%>

</c:if>     <%-- end if tag for availablePortlets array --%>


    <%-- start "outputEnd" for the producer loop --%>
    <c:if test="${producerStatus.last}">
         </table>
         </td></tr></table>

         </TD></TR>

    </c:if>     <%-- finish "outputEnd" for the producer loop --%>

<c:set var="counter2" value="${counter2+1}"/>
</c:forEach>        <%--ForEach tag ends for available producers--%>

</dspel:getvalueof>

    <TR><TD colspan=2><br></TD></TR>

    <TR>
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left">
        <c:set var="finish_btn"><fmt:message key="finishButton"/></c:set>

        <dspel:input type="submit" value="${finish_btn}" bean="ProducerFormHandler.collectValues"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0/></TD>
    </TR>
  </TABLE>

</dspel:form>


</fmt:bundle>

</paf:hasRole>

</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/configProducer4.jsp#2 $$Change: 651448 $--%>

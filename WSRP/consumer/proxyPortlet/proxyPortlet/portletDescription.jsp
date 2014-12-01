<%@ page import="atg.portal.framework.Gear,
                 atg.portal.framework.GearDefinition,
                 atg.wsrp.consumer.persistence.om.RepositoryPortletDescription,
                 oasis.names.tc.wsrp.v1.types.MarkupType"%>
 <%--
 Portlet Description page which is reached through InstanceConfig gear mode main page
 for the consumer proxy portlet.
 It will show all the portlet description for a particular WSRP portlet along with a
 refresh button to update the portlet description.
 --%>

<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>


<dspel:page>


<dspel:importbean bean="/atg/wsrp/consumer/PortletFormHandler"/>

<%@ include file="form_messages.jspf"%>



<body bgcolor="#BAD8EC">


<dspel:getvalueof var="portletDescription" vartype="atg.wsrp.consumer.persistence.om.RepositoryPortletDescription" bean="PortletFormHandler.portletDescriptionDataObj">

<%
    Gear gear = atg.portal.framework.RequestUtilities.getGear(request);
    String gearID = gear.getId();
%>
<c:set var="gear_id"><%=gearID %></c:set>


<c:set var="PORTALSERVLETREQUEST"><%= atg.portal.servlet.Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= atg.portal.servlet.Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="community_id"             value="${community.id}"/>
<c:set var="page"                     value="/portal/admin/community.jsp"/>
<c:set var="successpage"              value="community_gears.jsp"/>
<c:set var="producerID"               value="${param['producer_id']}"/>



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
            <c:set var="portletName"><c:out value="${portletDescription.title.value}"/></c:set>
            </c:otherwise>
            </c:choose>     <%-- end checking for title --%>
        </c:when>
        <c:otherwise>
        <%--short title is present so show it --%>
        <c:set var="portletName"><c:out value="${portletDescription.shortTitle.value}"/></c:set>
        </c:otherwise>
        </c:choose>         <%-- end checking for short title --%>
    </c:when>
    <c:otherwise>
    <%--display name is present so show it --%>
    <c:set var="portletName"><c:out value="${portletDescription.displayName.value}"/></c:set>
    </c:otherwise>
</c:choose>                 <%-- end checking for display name --%>



<%-- get all internationalized strings that will be used on this page --%>

<fmt:bundle basename="atg.wsrp.consumer.ProxyPortletInstanceConfigResources">


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
    <c:url var="backURL" context="/portal/settings" value="/community_gears_configure_additional.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="instanceConfig"/>
	<c:param name="paf_gear_id" value="${gear_id}"/>
	<c:param name="paf_page_url" value="${page}"/>
	<c:param name="paf_success_url" value="${successpage}"/>
    <c:param name="paf_community_id" value="${community_id}"/>
    <%--<c:param name="instanceConfigCase" value="Use"/>--%>
    </c:url>


    <c:url var="formURL" context="/portal/settings" value="/community_gears_configure_additional.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="instanceConfig"/>
	<c:param name="paf_gear_id" value="${gear_id}"/>
	<c:param name="paf_page_url" value="${page}"/>
	<c:param name="paf_success_url" value="${successpage}"/>
    <c:param name="paf_community_id" value="${community_id}"/>
	<c:param name="instanceConfigCase" value="Refresh"/>
    <c:param name="producer_id" value="${producerID}"/>
    <c:param name="portlet_id" value="${portletDescription.repositoryId}"/>

    </c:url>

     <dspel:form method="post" action="${formURL}">

</td></tr></table>


    <c:url var="failureURL" context="/portal/settings" value="/community_gears_configure_additional.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="instanceConfig"/>
	<c:param name="paf_gear_id" value="${gear_id}"/>
	<c:param name="paf_community_id" value="${community_id}"/>
	<c:param name="instanceConfigCase" value="RefreshFailed"/>
    <c:param name="config_page" value="portlet_description"/>
    <c:param name="producer_id" value="${producerID}"/>
    <c:param name="portlet_id" value="${portletDescription.repositoryId}"/>
    </c:url>

    <dspel:input type="hidden" bean="PortletFormHandler.failureURL" value="${failureURL}"/>


    <c:url var="successURL1" context="/portal/settings" value="/community_gears_configure_additional.jsp">
	<c:param name="paf_dm" value="full"/>
	<c:param name="paf_gm" value="instanceConfig"/>
	<c:param name="paf_gear_id" value="${gear_id}"/>
	<c:param name="paf_community_id" value="${community_id}"/>
	<c:param name="instanceConfigCase" value="RefreshSuccessful"/>
    <c:param name="config_page" value="portlet_description"/>
    <c:param name="producer_id" value="${producerID}"/>
    <c:param name="portlet_id" value="${portletDescription.repositoryId}"/>
    </c:url>

    <dspel:input type="hidden" bean="PortletFormHandler.successURL" value="${successURL1}"/>


<%--<img src='<%= getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0><br>--%>

  <!-- Title table -->
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
     <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
       <font class="pageheader_edit">
       <fmt:message key="portletDescMainHeading"/>
       </td></tr></table>
  </td></tr></table>


  <!-- instructions table -->
  <table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller">
        <fmt:message key="top-info-lbl2">
            <c:set var="bold"><fmt:message key="html-bold"/></c:set>
            <fmt:param value="${bold}"/>

            <fmt:param value="${portletName}"/>

            <c:set var="endBold"><fmt:message key="html-end-bold"/></c:set>
            <fmt:param value="${endBold}"/>
        </fmt:message>

  </td><td align="right"><%--<a href="<c:out value="${backURL}"/>">Back</a>--%>
    <dspel:a href="${backURL}" bean="PortletFormHandler.repositoryID" value="">
    Back
    </dspel:a>
  </td></tr></table>

<%--  <img src='<%= getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"> --%>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">

    <%--provide some room--%>
    <TR>
      <TD colspan="2" align="left" valign="top"><font class="smaller"><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="portlet-name"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletName}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="portlet-desc"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller">
                <c:choose>
                    <c:when test="${portletDescription.description != null}" >
                        <c:out value="${portletDescription.description.value}"/>
                    </c:when>
                    <c:otherwise>
                        -
                    </c:otherwise>
                </c:choose>

      <br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="portlet-handle"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.portletHandle}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="portlet-group"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.groupID}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="mime-type"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller">

      <c:forEach var="markupTypes" items="${portletDescription.markupTypes}" varStatus="status">
        <c:out value="${markupTypes.mimeType}"/>
      </c:forEach>

        <br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="uses-get"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.usesMethodGet}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="markup-secure"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.defaultMarkupSecure}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="only-secure"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.onlySecure}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="user-ctx"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.userContextStoredInSession}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="template"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.templatesStoredInSession}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="user-specific-states"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.hasUserSpecificState}"/><br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="url-template"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.doesUrlTemplateProcessing}"/><br></font></TD>
    </TR>

    <%--<TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="keywords"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><%=portletDescription.getKeywords()%><br></font></TD>
    </TR>--%>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="user-categories"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller">
      <c:forEach var="userCategory" items="${portletDescription.userCategories}" varStatus="status">
        <c:out value="${userCategory}"/>
      </c:forEach>
        <br></font></TD>
    </TR>

    <TR>
      <TD width="30%" align="left" valign="top"><font class="smaller"><fmt:message key="last-modified"/><fmt:message key="separator"/>&nbsp;&nbsp;</font></TD>
      <TD width="70%" align="left" valign="top"><font class="smaller"><c:out value="${portletDescription.lastModifiedDate}"/><br></font></TD>
    </TR>

    <%--provide some room--%>
    <TR>
      <TD colspan="2" align="left" valign="top"><font class="smaller"><br></font></TD>
    </TR>


    <TR>
      <TD colspan="2" align="center" valign="top">
        <input type="hidden" name="instanceConfigCase" value="Refresh"/>
        <%--<input type="hidden" name="producer_id" value="<c:out value="${producerID}"/>"/>--%>
        <dspel:setvalue bean="PortletFormHandler.repositoryID" value="${portletDescription.repositoryId}"/>
        <input type="submit" value="<fmt:message key="refreshButton"/>"/>
        </TD>
    </TR>

</table>


</dspel:form>
</body>

</fmt:bundle>

</dspel:getvalueof>



</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/portletDescription.jsp#2 $$Change: 651448 $--%>

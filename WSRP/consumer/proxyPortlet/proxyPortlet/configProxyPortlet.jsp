<%@ page import="atg.portal.framework.Gear,
                 atg.portal.framework.GearDefinition,
                 atg.wsrp.consumer.persistence.om.RepositoryPortletDescription"%>
 <%--
 Configure current proxy portlet page with an WSRP portlet available to the consumer.
 --%>

<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>



<dspel:page>

<% request.setAttribute(atg.portal.framework.RequestAttributes.COMMUNITY, request.getAttribute(atg.portal.framework.RequestAttributes.COMMUNITY)); %>
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
<c:set var="community_id"            value="${community.id}"/>
<c:set var="page"                     value="/portal/admin/community.jsp"/>
<c:set var="successpage"              value="community_gears.jsp"/>

<%--<paf:encodeURL var="pageURL" url="/portal/settings/community_gears_configure_additional.jsp"/>--%>

<dspel:importbean var="portletFormHandler" bean="/atg/wsrp/consumer/PortletFormHandler"/>

<%@ include file="form_messages.jspf"%>

<c:choose>
  <c:when test="${PortletFormHandler.formError == true}" >
    <dspel:setvalue bean="PortletFormHandler.resetFormExceptions"/>
  </c:when>
  <c:otherwise>
    <dspel:setvalue bean="PortletFormHandler.reset"/>
  </c:otherwise>
</c:choose>


<fmt:bundle basename="atg.wsrp.consumer.ProxyPortletInstanceConfigResources">

  <!-- Title table -->
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
     <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
       <font class="pageheader_edit">
       <fmt:message key="instanceConfigMainHeading"/></font>
       </td></tr></table>
  </td></tr></table>

  <!-- instructions table -->
  <table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><fmt:message key="top-info-lbl"/></font>
  </td></tr></table>

<%--  <img src='<%= getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"> --%>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">

    <%--provide some room--%>
    <TR>
      <TD nowrap colspan="3" align="left" valign="top"><font class="small"><br></font></TD>
    </TR>

  <!-- table for portlet and producer to which current proxy portlet instance is configured for use -->
  <%
      String producerID = gear.getGearInstanceParameter("producerID");
      String portletDescriptionID = gear.getGearInstanceParameter("portletDescriptionID");
      String producerName = null;
      oasis.names.tc.wsrp.v1.types.PortletDescription portletDesc = null;
      if(producerID!=null && portletDescriptionID!=null)
      {
          atg.wsrp.consumer.persistence.ConsumerPersistenceManager persistenceManager =
                  atg.wsrp.consumer.Utilities.getConsumerPersistenceManager();

          atg.apache.wsrp4j.consumer.Producer producer = persistenceManager.getProducer(producerID);
          if(producer!=null)
              producerName = producer.getName();

          portletDesc = persistenceManager.getPortletDescription(portletDescriptionID);
      }

      if(portletDesc!=null && producerName!=null)
      {
          String portletName = null;
          if(portletDesc.getDisplayName()!=null)
              portletName = portletDesc.getDisplayName().getValue();
          else if(portletDesc.getShortTitle()!=null)
              portletName = portletDesc.getShortTitle().getValue();
          else if(portletDesc.getTitle()!=null)
              portletName = portletDesc.getTitle().getValue();
  %>
  <tr><td nowrap colspan="3" align="left" valign="top">
        <font class="smaller">Current proxy-Portlet instance is configured to use <b><%=portletName%></b>
        on <b><%=producerName%></b>.
        </font>
  </td></tr>
    <%--provide some room--%>
    <TR>
      <TD nowrap colspan="3" align="left" valign="top"><font class="small"><br></font></TD>
    </TR>
  <% } %>


  <%-- Registered prodcuers & their offered portlets added to the consumer --%>
    <%-- show all the existing portlets --%>
<dspel:getvalueof var="producerArray" bean="PortletFormHandler.allRegisteredProducers"/>
<c:set var="counter" value="0"/>
<c:forEach var="ExistingProducerDataObject" items="${producerArray}" varStatus="status">
    <TR>
      <TD nowrap colspan="3" align="left" valign="top"><font class="small"><b><c:out value="${ExistingProducerDataObject.producerName}"/></b></font></TD>
    </TR>

  <c:if test="${ExistingProducerDataObject.availablePortlets != null}">

 <%-- further loop thru the available portlets array from the current producer --%>
  <c:forEach var="portletDescription" items="${ExistingProducerDataObject.availablePortlets}" varStatus="availablePortletsStatus">

  <TR>
  <TD nowrap align="left" valign="top">&nbsp;&nbsp;&nbsp;

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

  <font class="smaller"><c:out value="${portletName}"/></font>
  </TD>
  <TD nowrap align="left" valign="top">
                <%-- link for configfuring this portlet with current proxy portlet instance --%>

                    <c:url var="useURL" context="/portal/settings" value="/community_gears_configure_additional.jsp">
	                <c:param name="paf_dm" value="full"/>
	                <c:param name="paf_gm" value="instanceConfig"/>
	                <c:param name="paf_gear_id" value="${gear_id}"/>
	                <c:param name="paf_page_url" value="${page}"/>
	                <c:param name="paf_success_url" value="${successpage}"/>
                    <c:param name="paf_community_id" value="${community_id}"/>
                    <c:param name="producer_id" value="${ExistingProducerDataObject.repositoryId}"/>
                    <c:param name="instanceConfigCase" value="Use"/>
                    <c:param name="portlet_name" value="${portletName}"/>
                    </c:url>

                    <dspel:a href="${useURL}" bean="PortletFormHandler.repositoryID" value="${portletDescription.repositoryId}">
                    <font class="smaller" color="#0000FF"><fmt:message key="use-portlet-link"/></font>
                    </dspel:a>

                </TD>

                <TD nowrap align="left" valign="top">
                <%-- link for portlet description --%>

                    <c:url var="portletDescURL" context="/portal/settings" value="/community_gears_configure_additional.jsp">
	                <c:param name="paf_dm" value="full"/>
	                <c:param name="paf_gm" value="instanceConfig"/>
	                <c:param name="paf_gear_id" value="${gear_id}"/>
	                <c:param name="paf_page_id" value="${page_id}"/>
	                <c:param name="paf_page_url" value="${page}"/>
	                <c:param name="paf_success_url" value="${successpage}"/>
                    <c:param name="paf_community_id" value="${community_id}"/>
                    <c:param name="instanceConfigCase" value="PortletDescription"/>
                    <c:param name="producer_id" value="${ExistingProducerDataObject.repositoryId}"/>
                    <c:param name="portlet_id" value="${portletDescription.repositoryId}"/>
                    </c:url>

                    <dspel:a href="${portletDescURL}" bean="PortletFormHandler.repositoryID" value="${portletDescription.repositoryId}">
                    <font class="smaller" color="#0000FF"><fmt:message key="portlet-desc-link"/></font>
                    </dspel:a>

                </TD>
                </TR>

    </c:forEach>       <%--ForEach ends for available portlets--%>

    </c:if>     <%-- end if tag for availablePortlets array --%>


    <%--provide some room after one producer and its portlets --%>
    <TR>
      <TD nowrap colspan="3" align="left" valign="top"><font class="small"><br></font></TD>
    </TR>

<c:set var="counter" value="${counter+1}"/>

</c:forEach>    <%--ForEach ends for the producer --%>

    <%--provide some room after one producer and its portlets --%>
    <TR>
      <TD nowrap colspan="3" align="left" valign="top"><font class="small"><br></font></TD>
    </TR>

</table>

</fmt:bundle>

</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/configProxyPortlet.jsp#2 $$Change: 651448 $--%>

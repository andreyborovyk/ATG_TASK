<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin ActivityPortlet's index.jsp -->
<fmt:setBundle var="activitiesBundle" basename="atg.epub.portlet.ActivitiesPortlet.Resources"/>

<c:if test='${!empty param["noCache"]}'>
  <!-- do not cache the AJAX request -->
  <%
    javax.servlet.http.HttpServletResponse pResponse = (javax.servlet.http.HttpServletResponse) response;
    pResponse.setHeader("Pragma", "no-cache");
    pResponse.addDateHeader("Expires", 0);
    pResponse.setHeader("Cache-Control", "no-cache");
    pResponse.setHeader("Cache-Control", "no-store");
  %>
</c:if>

<div id="activitySources">
  <table cellspacing="0" cellpadding="0" width="100%" id="otheroperations" class="BCCContentTable">

    <tr id="opHeaderMain">
      <td><h2><fmt:message key="portlet-name" bundle="${activitiesBundle}"/></h2></td>

      <td align="right">

        <c:url var="closeAllUrl" value="/portlets/ActivitiesPortlet/index.jsp">
          <c:param name="operateAll" value="false"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <fmt:message var="closeAllString" key="close-all" bundle="${activitiesBundle}"/>
        <dspel:a id="opCloseAll" href="#" title="${closeAllString}" onclick="issueRequest('${closeAllUrl}', 'activitySources')"> </dspel:a>


        <c:url var="openAllUrl" value="/portlets/ActivitiesPortlet/index.jsp">
          <c:param name="operateAll" value="true"/>
          <c:param name="noCache" value="true"/>
        </c:url>

        <fmt:message var="openAllString" key="open-all" bundle="${activitiesBundle}"/>
        <dspel:a id="opOpenAll" href="#" title="${openAllString}" onclick="issueRequest('${openAllUrl}', 'activitySources')"> </dspel:a>

      </td>
    </tr>

    <dspel:include page="getActivitySources.jsp" flush="true"/>

  </table>
</div>

</dsp:page>

<!-- End ActivityPortlet's index.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/portlets/ActivitiesPortlet/index.jsp#2 $$Change: 651448 $--%>

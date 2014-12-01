<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin ActivityPortlet's getActivitySources.jsp -->
<fmt:setBundle var="activitiesBundle" basename="atg.epub.portlet.ActivitiesPortlet.Resources"/>

<dspel:importbean var="activityManager" bean="/atg/bizui/activity/ActivityManager"/>
<dspel:importbean var="activityState" bean="/atg/bizui/ui/state/ActivityState"/>


<c:if test='${!empty param["operateAll"]}'>
  <biz:elementState var="results" elementState="${activityState}" value='${param["operateAll"]}'/>
</c:if>

<c:forEach var="activitySource" items="${activityManager.activitySources}">
  <c:if test="${not empty activitySource.userActivities}">
    <tr>
      <td colspan="2" class="innerElement" id="<c:out value='child${activitySource.activitySourceId}'/>">

        <dspel:include page="getActivities.jsp" flush="true">
          <dspel:param name="activitySourceId" value="${activitySource.activitySourceId}"/>
          <dspel:param name="changeState" value="false"/>
        </dspel:include>

      </td>
    </tr>
  </c:if>
</c:forEach>

</dsp:page>


<!-- End ActivityPortlet's getActivitySources.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/portlets/ActivitiesPortlet/getActivitySources.jsp#2 $$Change: 651448 $--%>

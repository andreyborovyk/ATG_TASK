<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="pafrt"   uri="http://www.atg.com/taglibs/portal/paf-rt1_3" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin ActivityPortlet's getActivities.jsp -->
<dspel:importbean var="activityManager" bean="/atg/bizui/activity/ActivityManager"/>
<dspel:importbean var="activityState" bean="/atg/bizui/ui/state/ActivityState"/>

<dspel:getvalueof var="asid" param="activitySourceId"/>

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

<biz:getActivitySource var="activitySource" activitySourceId="${asid}"/>

<biz:elementState var="results" elementState="${activityState}" key="${asid}"/>
<c:if test='${param["changeState"] == "true"}'>
  <biz:elementState var="results" elementState="${activityState}" key="${asid}" value="${! results.expanded}"/>
</c:if>

<table cellspacing="0" cellpadding="0" width="100%">
  <tr>

    <c:choose>
      <c:when test='${results.expanded}'><c:set var="expanded" value="opHeaderOpen"/></c:when>
      <c:otherwise><c:set var="expanded" value="opHeaderClose"/></c:otherwise>
    </c:choose>

    <c:url var="asUrl" value="/portlets/ActivitiesPortlet/getActivities.jsp">
      <c:param name="activitySourceId" value="${asid}"/>
      <c:param name="changeState" value="true"/>
      <c:param name="noCache" value="true"/>
    </c:url>

    <td colspan="2" class="<c:out value='${expanded}'/>" id="<c:out value='parent${asid}'/>"
        onclick="displayToggle('parent<c:out value="${asid}"/>', 'opHeaderOpen', 'opHeaderClose');issueRequest('<c:out value="${asUrl}"/>', 'child<c:out value="${asid}"/>')">

      <dspel:a iclass="opTitle" href="#">
        <c:out value="${activitySource.activitySourceName}"/>
      </dspel:a>

    </td>
  </tr>


  <c:if test="${results.expanded}">
    <c:forEach var="activity" items="${activitySource.userActivities}">

      <%
         atg.bizui.activity.Activity activity = (atg.bizui.activity.Activity) pageContext.findAttribute("activity");
         if (activity.getDestinationPage().getURL().indexOf('?') == -1)
           pageContext.setAttribute("urlHasParameters", Boolean.FALSE);
         else
           pageContext.setAttribute("urlHasParameters", Boolean.TRUE);
         if (activity.getDestinationPage2() != null && activity.getDestinationPage2().getURL() != null && activity.getDestinationPage2().getURL().indexOf('?') > -1)
           pageContext.setAttribute("url2HasParameters", Boolean.TRUE);
         else
           pageContext.setAttribute("url2HasParameters", Boolean.FALSE);
      %>

      <web-ui:encodeParameterValue var="workflowDefName" value="${activity.displayName}"/>

      <c:choose>
        <c:when test='${activity.activityType == "workflow" && activity.destinationPage.page != null}'>
          <paf:context var="portalContext"/>
          <c:set target="${portalContext}" property="page" value="${activity.destinationPage.page}"/>
          <paf:encodeURL var="activityURL" url="${activity.destinationPage.URL}" context="${portalContext}">
            <pafrt:param name="projectView" value="<%=new Integer(99)%>"/> <%-- 99 is the create process form --%>
            <paf:param name="workflowDefinition" value='${activity.processName}'/>
            <paf:param name="workflowDefName" value='${workflowDefName}'/>
            <paf:param name="activity" value='${activity.id}'/>
          </paf:encodeURL>
        </c:when>

        <c:when test='${activity.activityType == "workflow" && activity.destinationPage.page == null}'>
          <c:url var="activityURL" value="${activity.destinationPage.URL}" context="${activity.destinationPage.URLContext}">
            <c:param name="workflowDefinition" value='${activity.processName}'/>
            <c:param name="workflowDefName" value='${workflowDefName}'/>
            <c:param name="activity" value='${activity.id}'/>
          </c:url>
        </c:when>

        <c:otherwise>
          <c:choose>
            <c:when test="${urlHasParameters}">
              <c:set var="activityURL" value="${activity.destinationPage.URL}&activity=${activity.id}"/>
            </c:when>
            <c:otherwise>
              <c:set var="activityURL" value="${activity.destinationPage.URL}?activity=${activity.id}"/>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>

      <c:choose>
        <c:when test="${url2HasParameters}">
          <c:set var="activityURL2" value="${activity.destinationPage2.URL}&activity=${activity.destinationPage2.activityId}"/>
        </c:when>
        <c:otherwise>
          <c:set var="activityURL2" value="${activity.destinationPage2.URL}?activity=${activity.destinationPage2.activityId}"/>
        </c:otherwise>
      </c:choose>

      <tr>
        <td align="left">

          <dspel:a href="${activityURL}" iclass="opItem" title="${activity.description}">
            <c:out value="${activity.displayName}"/>
          </dspel:a>

        </td>

        <td align="right">

          &nbsp;
          <c:if test='${activity.displayName2 != null}'>
            <dspel:a href="${activityURL2}" title="${activity.description2}">
              <c:out value="${activity.displayName2}"/>
            </dspel:a>
          </c:if>

        </td>
      </tr>

    </c:forEach>
  </c:if>

</table>

</dsp:page>

<!-- End ActivityPortlet's getActivities.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/portlets/ActivitiesPortlet/getActivities.jsp#2 $$Change: 651448 $--%>

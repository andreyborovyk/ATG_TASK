<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<div id="contentHeader">
  <div id="alert"></div>

  <pws:getCurrentContext var="context"/>

  <h2><fmt:message key="current-project" bundle="${bundle}"/>
    <c:choose>

      <c:when test='${context.process == null}'>
        <fmt:message key="no-current-project" bundle="${bundle}"/>
      </c:when>

      <c:otherwise>
        <biz:getProcessURL var="processInfo" process="${context.process}"/>
        <dsp:a title="${context.process.description}" href="${processInfo.URL}">
          <c:out value='${context.process.displayName}'/>
        </dsp:a>
      </c:otherwise>

    </c:choose>
  </h2>

  <h2><fmt:message key="current-task" bundle="${bundle}"/>
    <c:choose>

      <c:when test='${context.task == null}'>
        <fmt:message key="no-current-task" bundle="${bundle}"/>
      </c:when>

      <c:otherwise>
        <biz:getTaskURL var="taskInfo" task="${context.task}"/>
        <dsp:a title="${context.task.taskDescriptor.description}" href="${taskInfo.URL}">
          <c:out value='${context.task.taskDescriptor.displayName}'/>
        </dsp:a>
      </c:otherwise>

    </c:choose>
  </h2>
</div>

</dsp:page>

<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/includes/user_tools.jsp#2 $$Change: 651448 $--%>

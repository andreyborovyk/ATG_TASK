<%--
  JSP, showing number with current step for current section of search project page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_step.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- current project step --%>
  <d:getvalueof param="step" var="step"/>
  <%-- part of search project page --%>
  <d:getvalueof param="section" var="section"/>

  <c:set var="stepStatus" value=""/>
  <c:if test="${section < step}">
    <c:set var="stepStatus" value="done"/>
  </c:if>
  <c:if test="${section == step}">
    <c:set var="stepStatus" value="current"/>
  </c:if>
  <c:if test="${step != 4}">
    <span class="stepNumber <c:out value='${stepStatus}'/>">
      <c:out value="${section}"/><fmt:message key="project_step.delimiter"/>
    </span>
  </c:if> 
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_step.jsp#2 $$Change: 651448 $--%>

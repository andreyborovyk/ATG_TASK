<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dspel:page>
  <c:catch var="ex">
  <%-- This page outputs JSON format --%>
  
  <dspel:importbean var="previewProfileRequestProcessor" bean="/atg/userprofiling/PreviewProfileRequestProcessor"/>
  <dspel:importbean var="previewContext" bean="/atg/userprofiling/preview/PreviewContext"/>

  {"users":[
  <dspel:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
    <dspel:param name="queryRQL" value="ALL ORDER BY lastName, firstName SORT ASC CASE IGNORECASE"/>
    <dspel:param name="repository" value="/atg/userprofiling/ProfileAdapterRepository"/>
    <dspel:param name="itemDescriptor" value="user"/>
    <dspel:oparam name="output">
      {
        "name":"<dspel:valueof param='element.firstName'/> <dspel:valueof param='element.lastName'/>",
        "id":"<dspel:valueof param='element.repositoryId'/>"
        <dspel:getvalueof var="userId" param="element.repositoryId" scope="request"/>
        <c:if test="${previewContext.previewUserId == userId}">,"selected":true</c:if>
      },
    </dspel:oparam>
  </dspel:droplet>

  <%-- add anonymous user --%>
  {
    "name":"Anonymous User",
    "id":"<c:out value='${previewProfileRequestProcessor.anonymousPreviewIdValue}'/>"
    <c:if test='${previewContext.previewUserId == previewProfileRequestProcessor.anonymousPreviewIdValue}'>
      ,"selected":true
    </c:if>
  },

  ],
  "error":null,
  "success":true}
  </c:catch>
  <c:if test="${ex != null}">
    {"users":[],
      "error":<c:out value="${ex}"/>,
      "success":false}
    <% 
      Exception e = (Exception) pageContext.findAttribute("ex");
      e.printStackTrace(System.err);
    %>
  </c:if>
</dspel:page>

<%-- Example output format:
{"success":true,
 "error":null,
 "users":[
  {"name":"James Smith","id":"1","selected":true},
  {"name":"John Johnson","id":"2"},
  {"name":"Robert Williams","id":"3"},
  {"name":"Michael Jones","id":"4"},
  {"name":"William Brown","id":"5"},
  {"name":"David Davis","id":"6"},
  {"name":"Richard Miller","id":"7"},
  {"name":"Charles Wilson","id":"8"},
  {"name":"Joseph Moore","id":"9"},
  {"name":"Thomas Taylor","id":"10"}
]}
--%>      

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/preview/getPreviewUsers.jsp#2 $$Change: 651448 $--%>

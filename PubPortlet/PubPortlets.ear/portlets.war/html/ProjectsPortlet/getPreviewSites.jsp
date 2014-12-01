<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dspel:page>
  <c:catch var="ex">
  <%-- This page outputs JSON format --%>
  
  <dspel:importbean var="previewManager" bean="/atg/dynamo/service/preview/PreviewManager"/>
  
  <c:choose>
  
    <c:when test="${previewManager.sitesEnabled == true}">
      
      <c:choose>
        <c:when test="${! empty previewManager.previewSites}">
          <dspel:droplet name="/atg/dynamo/droplet/ForEach">        
            <dspel:param name="array" bean="/atg/dynamo/service/preview/PreviewManager.previewSites"/>

            <dspel:oparam name="outputStart">
              {"sites":[
            </dspel:oparam>

            <dspel:oparam name="output">
                {
                  "name":"<dspel:valueof param='element.label'/>",
                  "id":"<dspel:valueof param='element.id'/>"
                  <dspel:getvalueof var="siteSelected" param="element.selected"/>
                  <c:if test="${siteSelected == true}">,"selected":true</c:if>
                },
            </dspel:oparam>

            <dspel:oparam name="outputEnd">
                ],
                "error":null,
                "success":true,
                "enabled":true}
            </dspel:oparam>
          </dspel:droplet>
        </c:when>
        <c:otherwise>
          {"sites":[],
            "error":null,
            "success":true,
            "enabled":false}
        </c:otherwise>
      </c:choose>
      
    </c:when>
    
    <c:otherwise>
      {"sites":[],
        "error":null,
        "success":true,
        "enabled":false}
    </c:otherwise>
    
  </c:choose>
  
  </c:catch>
  <c:if test="${ex != null}">
    {"sites":[],
      "error":<c:out value="${ex}"/>,
      "success":false}
    <% 
      Exception e = (Exception) pageContext.findAttribute("ex");
      e.printStackTrace(System.err);
    %>
  </c:if>
</dspel:page>

<%-- Example output format:
{"sites":[
  {
    "name":"Site A",
    "id":"10000"
  },
  {
    "name":"Site B",
    "id":"10001",
    "selected":true
  },
  ],
  "error":null,
  "success":true}
--%>

<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/getPreviewSites.jsp#2 $$Change: 651448 $--%>

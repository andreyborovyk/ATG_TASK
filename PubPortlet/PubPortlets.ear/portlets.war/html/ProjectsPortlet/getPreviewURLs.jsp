<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>

<dspel:page>
  <dspel:importbean var="PreviewURLManager" bean="/atg/dynamo/service/preview/PreviewURLManager"/>
  <dspel:importbean var="previewContext" bean="/atg/userprofiling/preview/PreviewContext"/>
  <c:set var="sessionUrl" value="${previewContext.previewURL}"/>

  <%-- unescape the assetURI URL parameter --%>
  <c:set var="escapedAssetURI" value="${param.assetURI}"/>
  <%
    String assetURI = (String) pageContext.findAttribute("escapedAssetURI");
    String unescapedAssetURI = atg.core.net.URLUtils.unescapeUrlString(assetURI);
    pageContext.setAttribute("unescapedAssetURI", unescapedAssetURI);
  %>

  <%-- start the JSON output string --%>
  <c:out value='{"urls":[' escapeXml='false'/>

  <c:catch var="ex">
  <%-- This page outputs JSON format --%>

    <pws:getCurrentProject var="projectContext"/>    
    
    <pws:getAsset uri="${unescapedAssetURI}" var="assetVersion" workspaceName="${projectContext.project.workspace}"/>
    <c:choose>
      <c:when test='${assetVersion.virtualFile != null}'>
        <c:set var="item" value="${assetVersion.virtualFile}"/>
      </c:when>
      <c:when test='${assetVersion.repositoryItem != null}'>
        <c:set var="item" value="${assetVersion.repositoryItem}"/>
      </c:when>
    </c:choose>
    
    <biz:getItemMapping var="imap" mode="${param.mode}" readOnlyMode="${param.readOnlyMode}" item="${item}"
            mappingName="${param.mappingName}" showExpert="true" displayId="true"/>

    <%-- add the view mapping and item mapping preview URLs --%>
    <c:if test="${ ! empty imap.attributes.atgPreviewURL }">
      <c:choose>
        <c:when test="${param.mappingName == 'AssetManager'}">
          <pws:parsePreviewURL var="imapPreviewURL" url="${imap.attributes.atgPreviewURL.value}" asset="${assetVersion.item}"/>
        </c:when>
        <c:otherwise>
          <pws:parsePreviewURL var="imapPreviewURL" url="${imap.attributes.atgPreviewURL}" asset="${assetVersion.item}"/>
        </c:otherwise>
      </c:choose>
      <c:if test="${! empty imapPreviewURL}">
	      {
		"url":"<c:out value='${imapPreviewURL}' escapeXml='false'/>",
		"name":"<c:out value='${imapPreviewURL}' escapeXml='false'/>",
		"id":"<c:out value='${imap.name}' escapeXml='false'/>",
		"as":true
		<%
		  String sessionUrl = (String) pageContext.findAttribute("sessionUrl");
		  if (!atg.core.util.StringUtils.isBlank(sessionUrl)) {
		    int qindex = sessionUrl.indexOf('?');
		    int jsindex = sessionUrl.indexOf(";jsessionid=");
		    int index = (qindex < jsindex ? qindex : jsindex);
		    if (index == -1 && qindex != -1)
		      index = qindex;
		    else if (index == -1 && jsindex != -1)
		      index = jsindex;
		    if (index != -1)
		      sessionUrl = sessionUrl.substring(0, index);
		  }

		  String currentUrl = (String) pageContext.findAttribute("imapPreviewURL");
		  if (!atg.core.util.StringUtils.isBlank(currentUrl)) {
		    int qindex = currentUrl.indexOf('?');
		    int jsindex = currentUrl.indexOf(";jsessionid=");
		    int index = (qindex < jsindex ? qindex : jsindex);
		    if (index == -1 && qindex != -1)
		      index = qindex;
		    else if (index == -1 && jsindex != -1)
		      index = jsindex;
		    if (index != -1)
		      currentUrl = currentUrl.substring(0, index);
		  }

		  Boolean alreadySelected = (Boolean) pageContext.getAttribute("alreadySelected");
		  if (alreadySelected == null)
		    alreadySelected = Boolean.FALSE;
		  if (!alreadySelected.booleanValue() && currentUrl != null && sessionUrl != null) {
		    boolean urlsEqual = currentUrl.equals(sessionUrl);
		    if (urlsEqual) {
		      pageContext.setAttribute("selected", (urlsEqual ? Boolean.TRUE : Boolean.FALSE));
		      pageContext.setAttribute("alreadySelected", Boolean.TRUE);
		      System.out.println("selecting above");
		    }
		  }
		  else
		    pageContext.setAttribute("selected", Boolean.FALSE);
		%>
		<c:if test="${selected}">,"selected":true</c:if>
	      },
      </c:if>
    </c:if>

    <c:forEach items="${imap.viewMappings}" var="view">

      <c:if test="${ ! empty view.attributes.atgPreviewURL }">
        <c:choose>
          <c:when test="${param.mappingName == 'AssetManager'}">
            <pws:parsePreviewURL var="vmapPreviewURL" url="${view.attributes.atgPreviewURL.value}" asset="${assetVersion.item}"/>
          </c:when>
          <c:otherwise>
            <pws:parsePreviewURL var="vmapPreviewURL" url="${view.attributes.atgPreviewURL}" asset="${assetVersion.item}"/>
          </c:otherwise>
        </c:choose>
        <c:if test="${! empty vmapPreviewURL}">
		{
		  "url":"<c:out value='${vmapPreviewURL}' escapeXml='false'/>",
		  "name":"<c:out value='${vmapPreviewURL}' escapeXml='false'/>",
		  "id":"<c:out value='${view.name}' escapeXml='false'/>",
		  "as":true
		  <%
		    String sessionUrl = (String) pageContext.findAttribute("sessionUrl");
		    if (!atg.core.util.StringUtils.isBlank(sessionUrl)) {
		      int qindex = sessionUrl.indexOf('?');
		      int jsindex = sessionUrl.indexOf(";jsessionid=");
		      int index = (qindex < jsindex ? qindex : jsindex);
		      if (index == -1 && qindex != -1)
			index = qindex;
		      else if (index == -1 && jsindex != -1)
			index = jsindex;
		      if (index != -1)
			sessionUrl = sessionUrl.substring(0, index);
		    }

		    String currentUrl = (String) pageContext.findAttribute("vmapPreviewURL");
		    if (!atg.core.util.StringUtils.isBlank(currentUrl)) {
		      int qindex = currentUrl.indexOf('?');
		      int jsindex = currentUrl.indexOf(";jsessionid=");
		      int index = (qindex < jsindex ? qindex : jsindex);
		      if (index == -1 && qindex != -1)
			index = qindex;
		      else if (index == -1 && jsindex != -1)
			index = jsindex;
		      if (index != -1)
			currentUrl = currentUrl.substring(0, index);
		    }

		    Boolean alreadySelected = (Boolean) pageContext.getAttribute("alreadySelected");
		    if (alreadySelected == null)
		      alreadySelected = Boolean.FALSE;
		    if (!alreadySelected.booleanValue() && currentUrl != null && sessionUrl != null) {
		      boolean urlsEqual = currentUrl.equals(sessionUrl);
		      if (urlsEqual) {
			pageContext.setAttribute("selected", (urlsEqual ? Boolean.TRUE : Boolean.FALSE));
			pageContext.setAttribute("alreadySelected", Boolean.TRUE);
		      }
		    }
		    else
		      pageContext.setAttribute("selected", Boolean.FALSE);
		  %>
		  <c:if test="${selected}">,"selected":true</c:if>
		},
	</c:if>
      </c:if>
    </c:forEach>

    <%-- add the PreviewURLManager preview URLs --%>
    <dspel:droplet name="/atg/dynamo/droplet/ForEach">
      <dspel:param name="array" bean="PreviewURLManager.previewURLs" />

      <dspel:oparam name="output">        
        <dspel:getvalueof var="protocol" param="element.protocol" scope="request"/>
        <dspel:getvalueof var="hostName" param="element.host.hostName" scope="request"/>
        <dspel:getvalueof var="port" param="element.host.port" scope="request"/>
        <dspel:getvalueof var="defaultPath" param="element.path.defaultPath" scope="request"/>
        
        <c:choose>
          <c:when test="${empty hostName}">
            <c:set var="previewURL" value="${defaultPath}"/>
          </c:when>
          <c:otherwise>
            <c:set var="previewURL" value="${protocol}://${hostName}:${port}${defaultPath}"/>
          </c:otherwise>
        </c:choose>
        <pws:parsePreviewURL var="previewURL" asset="${assetVersion.item}" url="${previewURL}"/>
        <c:if test="${! empty previewURL}">
		<dspel:getvalueof var="currentUrl" param="element.path.defaultPath" scope="request"/>
		<%
		  String sessionUrl = (String) pageContext.findAttribute("sessionUrl");
		  if (!atg.core.util.StringUtils.isBlank(sessionUrl)) {
		    int qindex = sessionUrl.indexOf('?');
		    int jsindex = sessionUrl.indexOf(";jsessionid=");
		    int index = (qindex < jsindex ? qindex : jsindex);
		    if (index == -1 && qindex != -1)
		      index = qindex;
		    else if (index == -1 && jsindex != -1)
		      index = jsindex;
		    if (index != -1)
		      sessionUrl = sessionUrl.substring(0, index);
		  }

		  String currentUrl = (String) pageContext.findAttribute("currentUrl");
		  if (!atg.core.util.StringUtils.isBlank(currentUrl)) {          
		    int qindex = currentUrl.indexOf('?');
		    int jsindex = currentUrl.indexOf(";jsessionid=");
		    int index = (qindex < jsindex ? qindex : jsindex);
		    if (index == -1 && qindex != -1)
		      index = qindex;
		    else if (index == -1 && jsindex != -1)
		      index = jsindex;
		    if (index != -1)
		      currentUrl = currentUrl.substring(0, index);
		  }            

		  Boolean alreadySelected = (Boolean) pageContext.getAttribute("alreadySelected");
		  if (alreadySelected == null)
		    alreadySelected = Boolean.FALSE;
		  if (!alreadySelected.booleanValue() && currentUrl != null && sessionUrl != null) {
		    boolean urlsEqual = currentUrl.equals(sessionUrl);
		    if (urlsEqual) {
		      pageContext.setAttribute("selected", (urlsEqual ? Boolean.TRUE : Boolean.FALSE));
		      pageContext.setAttribute("alreadySelected", Boolean.TRUE);
		    }
		  }
		  else
		    pageContext.setAttribute("selected", Boolean.FALSE);
		%>
		{
		  "url":"<c:out value="${previewURL}" escapeXml='false'/>",
		  "name":"<dspel:valueof param='element.displayName'/>",
		  "id":"<dspel:valueof param='element.name'/>"
		  <c:if test="${selected}">,"selected":true</c:if>
		},
	</c:if>
      </dspel:oparam>
    </dspel:droplet>
    
    <%-- end the JSON output string --%>
    <c:out value='],"error":null,"success":true}' escapeXml="false"/>

  </c:catch>

  <c:if test="${ex != null}">
    <%-- output exception in JSON format --%>
    <c:out value='],"success":false,"error":"${ex}"}' escapeXml="false"/>
    <%--
    <% 
      Exception e = (Exception) pageContext.findAttribute("ex");
      e.printStackTrace(System.err);
    %>
    --%>
  </c:if>
</dspel:page>

<%-- Example output format:
{"success":true,
 "error":null,
 "urls":[
  {"url":"http://blah:8080/lorem/ipsum/delors/roomba", "name":"URL Name", "id":"1", "as":true},
  {"url":"http://blah:8081", "name":"URL Name", "id":"2", "as":true},
  {"url":"http://blah:8082", "name":"URL Name", "id":"3", "as":true},
  {"url":"http://blah:8083", "name":"URL Name", "id":"4", "selected": true},
  {"url":"http://blah:8084", "name":"URL Name", "id":"5"},
]}
--%>      
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/getPreviewURLs.jsp#2 $$Change: 651448 $--%>

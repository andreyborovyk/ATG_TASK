<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt"               %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui_rt"             %>

<dspel:page>

<fmt:setBundle var="resourceBundle" basename="atg.service.preview.Resources"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

  <link rel="stylesheet" type="text/css" href="/WebUI/css/previewFrame.css"/>
  
  <script type="text/javascript">
    function changeUser(selectObject) {
      var selectedUserId = selectObject.options[selectObject.selectedIndex].value;
      var url = parent.preview.location.href;
      var atgPreviewIdString = "atgPreviewId=";
      
      var index = url.indexOf(atgPreviewIdString);
      if (index != -1) {
        var ampIndex = url.indexOf("&", index);
        var str1 = url.substring(0, index + atgPreviewIdString.length);

        // in the case that the atgPreviewId is the last param, then don't try to get
        // the rest of the string
        var str2 = "";
        if (ampIndex != -1)
          str2 = url.substring(ampIndex);

        var newUrl = str1 + selectedUserId + str2;

        parent.preview.location.href = newUrl;
      }
      else { // atgPreviewId is not in url so add it
        var sepChar = "&";
        var qIndex = url.indexOf("?");
        if (qIndex == -1)
          sepChar="?";        
        parent.preview.location.href = url + sepChar + atgPreviewIdString + selectedUserId;
      }
    }   
    
    function changeSite(selectObject) {
      var selectedSiteId = selectObject.options[selectObject.selectedIndex].value;
      var url = parent.preview.location.href;
      var pushSiteParamString = "pushSite=";
      var stickySiteParamString = "&stickySite=setSite";
      
      var index = url.indexOf(pushSiteParamString);
      if (index != -1) {
        var ampIndex = url.indexOf("&", index);
        var str1 = url.substring(0, index + pushSiteParamString.length);

        // in the case that the pushSite is the last param, then don't try to get
        // the rest of the string
        var str2 = "";
        if (ampIndex != -1)
          str2 = url.substring(ampIndex);

        var newUrl = str1 + selectedSiteId + str2;

        parent.preview.location.href = newUrl;
      }
      else { // pushSite is not in url so add it
        var sepChar = "&";
        var qIndex = url.indexOf("?");
        if (qIndex == -1)
          sepChar="?";        
        parent.preview.location.href = url + sepChar + pushSiteParamString + selectedSiteId + stickySiteParamString;
      }
    }   
    
    function updateDropdownLists() {
      updateUserDropdown();
      updateSiteDropdown();
    }
    
    function updateUserDropdown() {
      var selectObject = document.getElementById("userSelect");
      if (selectObject != null) {
        var url = parent.location.href;
        var atgPreviewIdString = "atgPreviewId=";

        var index = url.indexOf(atgPreviewIdString);
        var ampIndex = url.indexOf("&", index);

        var userId = "";
        if (ampIndex != -1)
          userId = url.substring(index + atgPreviewIdString.length, ampIndex);
        else
          userId = url.substring(index + atgPreviewIdString.length);

        if (selectObject.value != userId) {
          for (var i = 0; i < selectObject.length; i++) {
            if (selectObject.options[i].value == userId) {
              selectObject.selectedIndex = i;
              break;
            }
          }
        }
      }
    }

    function updateSiteDropdown() {
      var selectObject = document.getElementById("siteSelect");
      if (selectObject != null) {    
        var url = parent.location.href;
        var pushSiteParamString = "pushSite=";

        var index = url.indexOf(pushSiteParamString);
        var ampIndex = url.indexOf("&", index);

        var siteId = "";
        if (ampIndex != -1)
          siteId = url.substring(index + pushSiteParamString.length, ampIndex);
        else
          siteId = url.substring(index + pushSiteParamString.length);

        if (selectObject.value != siteId) {
          for (var i = 0; i < selectObject.length; i++) {
            if (selectObject.options[i].value == siteId) {
              selectObject.selectedIndex = i;
              break;
            }
          }
        }
      }
    }
  </script>
</head>

<body onload="window.focus()">
  <div id="globalHeader">
    <div id="logoHeader">

      <div id="logoHeaderRight" style="width:800px;">

          <dspel:importbean var="previewProfileRequestProcessor" bean="/atg/userprofiling/PreviewProfileRequestProcessor"/>
          <dspel:importbean var="previewManager" bean="/atg/dynamo/service/preview/PreviewManager"/>

          <c:if test="${previewManager.sitesEnabled == true}">
            <fmt:message key="on-site" bundle="${resourceBundle}"/>

            <dspel:droplet name="/atg/dynamo/droplet/ForEach">
              <dspel:param name="array" bean="/atg/dynamo/service/preview/PreviewManager.previewSites"/>
              <dspel:oparam name="outputStart">
                <select name="siteSelect" id="siteSelect" dojoType="Select" onChange="changeSite(this)">
              </dspel:oparam>
              <dspel:oparam name="output">
                <dspel:getvalueof var="currentPreviewSiteId" param='element.id'/>
                <dspel:getvalueof var="label" param='element.label'/>
                <web-ui:truncateString var="displayName" string="${label}" addEllipsis="true" finalLength="40"/>
                <option value="<dspel:valueof param='element.id'/>"
                  <c:if test="${param.pushSite == currentPreviewSiteId}">selected</c:if>
                >
                  <c:out value="${displayName}"/>
                </option>
              </dspel:oparam>
              <dspel:oparam name="outputEnd">
                </select>
              </dspel:oparam>
            </dspel:droplet>

            <span>|</span>
          </c:if>
          
          <fmt:message key="as-user" bundle="${resourceBundle}"/>

          <select name="userSelect" id="userSelect" dojoType="Select" onChange="changeUser(this)">
            <dspel:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
              <dspel:param name="queryRQL" value="ALL ORDER BY lastName, firstName SORT ASC CASE IGNORECASE"/>
              <dspel:param name="repository" value="/atg/userprofiling/ProfileAdapterRepository"/>
              <dspel:param name="itemDescriptor" value="user"/>
              <dspel:oparam name="output">
                <dspel:getvalueof var="currentPreviewUserId" param='element.repositoryId'/>
                <dspel:getvalueof var="firstName" param='element.firstName'/>
                <dspel:getvalueof var="lastName" param='element.lastName'/>
                <c:set var="userFullName" value="${firstName} ${lastName}"/>
                <web-ui:truncateString var="userDisplayName" string="${userFullName}" addEllipsis="true" finalLength="40"/>
                <option value="<dspel:valueof param='element.repositoryId'/>"
                  <c:if test="${param.atgPreviewId == currentPreviewUserId}">selected</c:if>
                >
                  <c:out value="${userDisplayName}"/>
                </option>
              </dspel:oparam>
            </dspel:droplet>

            <%-- add anonymous user --%>
            <option value="<c:out value='${previewProfileRequestProcessor.anonymousPreviewIdValue}'/>"
              <c:if test="${param.atgPreviewId == previewProfileRequestProcessor.anonymousPreviewIdValue}">selected</c:if>
            >
              Anonymous User
            </option>
          </select>

          <%--dspel:droplet name="/atg/dynamo/service/preview/UserLookupDroplet">
            <dspel:param name="id" param="atgPreviewId"/>
            <dspel:oparam name="output">
              <dspel:valueof param='element.firstName'/> <dspel:valueof param='element.lastName'/>
            </dspel:oparam>
            <dspel:oparam name="empty">
              <fmt:message key="empty-user" bundle="${resourceBundle}"/>
            </dspel:oparam>
          </dspel:droplet--%>

          <span>|</span>

          <a href='<c:out value="${param.url}"/>' onClick="updateDropdownLists()"
             title='<fmt:message key="back-to-launch-landing-page" bundle="${resourceBundle}"/> <c:out value="${param.url}"/>'
             target='preview'><fmt:message key="preview-landing-page" bundle="${resourceBundle}"/></a>

          <span>|</span>

          <a href='javascript:window.document.location=window.preview.location.href'
             title='<fmt:message key="view-page-outside-frame" bundle="${resourceBundle}"/>'
             target='_top'><fmt:message key="remove-frame" bundle="${resourceBundle}"/></a>
      </div>

      <div id="logoHeaderLeft">
        <h1><fmt:message key="preview-title" bundle="${resourceBundle}"/></h1>
      </div>

    </div>
  </div>
</body>
</html>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/preview/title.jsp#2 $$Change: 651448 $--%>

<%--

  Browse picker main page - mimics look and feel of BIZUI


  @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowse.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $

--%>


<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,java.util.*" errorPage="/error.jsp" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>


<dspel:page>

<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>

<fmt:setBundle var="bundle" basename="atg.bizui.assetbrowse" />

<c:set var="logoutURL" value="/atg/user/logout.jsp?paf_portalId=default&paf_communityId=100001&paf_pageId=100001&paf_dm=shared"/>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <%@ include file="includes/head.jsp" %>
</head>

  <%@ include file="assetPickerConstants.jsp" %>

  <%-- 
	The following block of code handles redirection back to the bcc for viewing asset details or history.
        Must be done before any response is written
  --%>   
  <%-- get BrowseAssetInfo --%>
  <c:choose>
    <c:when test="${ ! empty param.assetInfo }">
      <c:set var="assetInfoPath" value="${param.assetInfo}"/>
    </c:when>
    <c:otherwise>
      <c:set var="assetInfoPath" 
        value="/atg/epub/servlet/BrowseAssetInfo"/>
    </c:otherwise>
  </c:choose>
  <dspel:importbean bean="${assetInfoPath}" var="assetInfo"/>

  <c:set target="${assetInfo}" property="clearContext" value="true"/>

  <c:set var="apView" value="${ param.apView }"/>
  <c:set var="assetView" value="${assetInfo.attributes.assetview}"/>
  <c:set var="assetURI" value="${assetInfo.attributes.asseturi}"/>
  <c:choose>
    <c:when test="${ ( assetView eq ASSET_PROPERTIES || assetView eq ASSET_HISTORY ) && apView ne AP_CONTAINER && apView ne AP_ITEM_TYPE_SELECT }">
      <c:redirect url="${ASSET_PORTLET_URL}" context="${BCC_CONTEXT}">
        <c:param name="assetView" value="${assetView}"/>
        <c:param name="assetInfo" value="${assetInfoPath}"/>
        <c:param name="assetURI" value="${assetURI}"/>
        <c:param name="clearContext" value="true"/>
        <c:param name="clearAttributes" value="true"/>
      </c:redirect>
    </c:when>
  </c:choose>

<body>
  <!-- legacy message -->
    <div id="legacyBrowserMessage">You have an older non-standards compliant browser. We recomend 
	that you upgrade to a more modern browser.
    </div>
  <!-- end legacy message -->

  <!-- branding banner -->
  <%@ include file="includes/branding_banner.jsp" %>
  <!-- end branding banner -->

  <!-- contentHolder -->
  <div id="newContentHolder">

    <%@ include file="assetBrowsePicker.jsp" %>

    <!-- footer -->
    <%@ include file="includes/footer.jsp" %>
    <!-- end of footer -->

  </div>
  <!-- end of contentHolder -->

<p></p>
<p></p>
</body>
</html>

</dspel:page>
<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowse.jsp#2 $$Change: 651448 $--%>

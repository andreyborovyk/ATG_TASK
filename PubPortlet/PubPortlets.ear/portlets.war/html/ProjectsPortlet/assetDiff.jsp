<%--
  Asset Differences Page (for the Project Portlet)
  This page uses a fragment to show the diffs between 2 versions of an asset.

  @version $Id $$Change $
  @updated $DateTime $ $Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin ProjectsPortlet's assetDiff.jsp -->
<dspel:page>
<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>

<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>
<c:set target="${assetInfo.context.attributes}" property="pageView"
          value="${ASSET_DIFF_VIEW}"/>

<%-- Define page parameters to be accessed from the details fragment, specific to this portlet --%>
<c:set var="formHandlerPath" value="/atg/epub/servlet/ProjectDiffFormHandler" scope="page"/>
<c:set var="itemMappingMode" value="conflict" scope="page"/>
<portlet:renderURL  var="cancelDiffActionURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSET_HISTORY_VIEW")%>'/>
</portlet:renderURL>

<%@ include file="../AssetsPortlet/assetDiffDetails.jspf" %>
</dspel:page>
<!-- End ProjectsPortlet's assetDiff.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetDiff.jsp#2 $$Change: 651448 $--%>

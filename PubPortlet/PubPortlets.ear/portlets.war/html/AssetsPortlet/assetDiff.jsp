<%--
  Asset Differences Page (for the Asset Portlet)
  This page uses a fragment to show the diffs between 2 versions of an asset.

  @param assetURI     The publishing asset's URI (session scoped, unused).

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/assetDiff.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin AssetPortlet's assetDiff.jsp -->

<portlet:defineObjects/>

<%@ page import="atg.epub.portlet.asset.AssetPortlet" %>

<%-- Define page parameters to be accessed from the details fragment, specific to this portlet --%>
<c:set var="formHandlerPath" value="/atg/epub/servlet/AssetDiffFormHandler" scope="page"/>
<c:set var="itemMappingMode" value="diff" scope="page"/>
<portlet:renderURL var="cancelDiffActionURL">
  <portlet:param name="assetView" value='<%= ""+AssetPortlet.ASSET_HISTORY %>'/>
</portlet:renderURL>

</dsp:page>


<%@ include file="assetDiffDetails.jspf" %>
<!-- End AssetPortlet's assetDiff.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/assetDiff.jsp#2 $$Change: 651448 $--%>

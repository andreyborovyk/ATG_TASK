<%--
  Default CA tree browse plugin page

  @param assetView

    Determines which page to use to view asset properties. The
    condensed view is used within the asset picker, and the full
    view is used in the browse tab. If null, will display search 
    results

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/treeBrowse.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin treeBrowse.jsp -->

<dspel:page>

<c:catch var="ex">

  <%@ include file="/assetPicker/assetPickerConstants.jsp" %>

  <c:set var="assetView" value="${ param.assetView }"/>
  
  <c:choose>
    <c:when test="${ assetView eq ASSET_DETAILS_CONDENSED }"> 
      <dspel:include page="${ ASSET_DETAILS_CONDENSED_PAGE }"/>
    </c:when>
    <c:when test="${ assetView eq ASSET_DETAILS }">
      <dspel:include page="${ ASSET_DETAILS_CONDENSED }"/>
    </c:when>
    <c:otherwise>
      <dspel:include page="${ TREE_VIEW_PAGE }"/>
    </c:otherwise>
  </c:choose>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in treeBrowse.jsp:");
    tt.printStackTrace();
  }
%>

</dspel:page>

<!-- End treeBrowse.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/treeBrowse.jsp#2 $$Change: 651448 $--%>

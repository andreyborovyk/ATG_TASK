<%--
  Asset History (for the Asset Portlet)

  @param   projectView          (currently not used)
  @param   assetURI             the publishing asset's URI (session scoped)
  @param   workspaceName        the workspace's name

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/assetHistory.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>

<!-- Begin AssetPortlet's assetHistory.jsp -->

<dspel:page>
  <portlet:defineObjects/>

  <jsp:useBean id="assetPortlet" class="atg.epub.portlet.asset.AssetPortlet"/>

  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

  <c:if test="${ ! empty param.projectAssetVersion }">
    <c:set var="assetVersion" value="${param.projectAssetVersion}"/>
  </c:if>


  <%-- Define page parameters to be accessed from the details fragment, specific to this portlet --%>
  <c:set var="assetHistoryDetailsURI" value="${assetURI}" scope="page"/>
  <c:set var="formHandlerPath" value="/atg/epub/servlet/AssetDiffFormHandler" scope="page"/>
  <c:set var="portletViewAttribute" value="assetView" scope="page"/>
  <c:set var="diffSuccessView" value="${assetPortlet.ASSET_DIFF}" scope="page"/>

  <%-- set vars for last location, used in AssetBrowse web-app --%>
  <c:set target="${assetInfo.attributes}" property="assetview" value="${assetPortlet.ASSET_HISTORY}"/>
  <c:set target="${assetInfo.attributes}" property="asseturi" value="${assetURI}"/>

<c:choose>
  <c:when test="${ empty assetVersion || empty assetHistoryDetailsURI }">
    <%@ include file="assetHistoryDetails.jspf" %>
  </c:when>
  
  <c:when test="${ ! empty assetVersion && ! empty assetHistoryDetailsURI}">
    <pws:getAsset uri="${assetHistoryDetailsURI}" var="theAsset"/>
    
    <c:forEach items="${theAsset.asset.allVersions}" var="av">
      <c:if test="${av.version == assetVersion}">
        <c:if test="${ ! empty av.repositoryItem }">
          <c:set var="item" value="${av.repositoryItem}"/>
          <c:set var="assetVersionURI" value="${av.URI.URIString}"/>
        </c:if>
        <c:if test="${ ! empty av.virtualFile }">
          <c:set var="item" value="${av.virtualFile}"/>
        </c:if>
      </c:if>
    </c:forEach>
    
    <%@ include file="../AssetsPortlet/assetPropertyView.jspf" %>
    
    <div class="contentActions">
      <table cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td width="100%" class="blankSpace">&nbsp;
          </td>
          <td>
            <portlet:renderURL var="clearAssetVersionURL">
            </portlet:renderURL>
            <td class="buttonImage">
              <a href='<c:out value="${clearAssetVersionURL}"/>'
                 class="mainContentButton modify" onmouseover="status='';return true;">
                Cancel
              </a>
            </td>
          </td>
          <td class="blankSpace">&nbsp;
          </td>
        </tr>
      </table>
    </div>    
  </c:when>
</c:choose>

</dspel:page>

<!-- End AssetPortlet's assetHistory.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/assetHistory.jsp#2 $$Change: 651448 $--%>

<%@ page import="javax.portlet.*,atg.portal.servlet.*,atg.portal.framework.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's createAsset.jsp -->

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<dspel:page>

<portlet:defineObjects/>
<dspel:importbean bean="/atg/web/ATGSession" var="atgSession"/>
<%@ include file="projectConstants.jspf" %>

  <%-- GET THE CURRENT PROJECT ID --%>
  <pws:getCurrentProject var="projectContext"/>

  <c:set var="project" value="${projectContext.project}"/>
  <c:set var="process" value="${projectContext.process}"/>
  <c:set var="currentProjectId" value="${project.id}"/>
  <c:set var="projectView" value="${atgSession.projectView}"/>

  <%-- BREADCRUMB/LINKS --%>

<%-- this bean used to get the default process portlet ID  --%>
<dspel:importbean bean="/atg/bizui/portlet/PortletConfiguration" var="bizuiPortletConfig"/>
<portlet:renderURL  var="defaultURL"/>
<c:set var="listURL"><c:out escapeXml="false" value="${defaultURL}&processPortlet=${bizuiPortletConfig.defaultProcessPortletID}&${PROJECT_PORTLET_VIEW_ATTRIBUTE}=${LIST_VIEW}"/></c:set>


  <portlet:renderURL  var="assetsURL">
    <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSETS_VIEW")%>'/>
    <portlet:param name="tab" value='<%=(String)pageContext.getAttribute("ASSETS_VIEW")%>'/>
  </portlet:renderURL>

  <%-- 
    The AssetInfo component for these pages
  --%>
  <c:if test="${empty requestScope.assetInfoPath}">
    <c:set var="assetInfoPath" scope="request"
      value="/atg/epub/servlet/ProcessAssetInfo"/>
  </c:if>
  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

  <%-- 
     The clearContext and clearAttributes parameters will be set to
     'true' when user comes to this page by picking an existing asset
     from the process assets list. 
  --%>
  <c:if test="${ 'true' == param.clearContext }">
    <c:set target="${assetInfo}" property="clearContext" value="true"/>
  </c:if>

  <c:if test="${ 'true' == param.clearAttributes }">
    <c:set target="${assetInfo}" property="clearAttributes" value="true"/>
  </c:if>

  <%--
    The 'firstAssetEditContextLevel' is the first context level at which
    the assetEditPage is involved in asset editing. If the client of this
    page has not set this variable then use the default, 1, or the top
    context. If it has been set, then display the 'back to parent asset'
    link at context levels equal to firstAssetEditContextLevel or greater.
    
    Also, if this variable has been set then use the the contextActionURL
    in the assetInfo's attributes (NOTE: this is not the same as the context
    attributes!) instead of the normal contextActionURL created in 
    contextForm.jspf. 

    The following properties of assetInfo (not context attributes!) can
    be used:

      assetInfo.attributes.firstAssetEditContextLevel

        The first context level at which 'normal' asset editing
        is occurring. This is also the level at which a custom
        back link and contextActionURL, if specified, will be used.

      assetInfo.attributes.contextActionURL

        Custom contextActionURL to be used as the action in
        the ContextFormHandler at context level firstAssetEditContextLevel.
        Use this to return to a different page

      assetInfo.attributes.backToText

        Text to use in the back-to URL, shown only in the 
        firstAssetEditContextLevel

      assetInfo.attributes.backToURL

        Link to use in the back-to URL, shown only in the 
        firstAssetEditContextLevel. Probably should be
        either "javascript:popContext()" or 
        "javascript:updateContextInformation()"
  --%>

  <c:choose>
    <c:when test="${ ! empty assetInfo.attributes.firstAssetEditContextLevel }">
      <c:set var="firstAssetEditContextLevel"
        value="${ assetInfo.attributes.firstAssetEditContextLevel }"/>
    </c:when>
    <c:otherwise>
      <c:set var="firstAssetEditContextLevel" value="1"/>
    </c:otherwise>
  </c:choose>

  <div id="intro">
    <h2>
      <span class="pathSub">
        <fmt:message key="process-detail-label" bundle="${projectsBundle}"/> - </span>
          <c:out value="${process.displayName}"/> - <fmt:message key="create-new-asset-breadcrumb" bundle="${projectsBundle}"/> 
          <c:out value="${assetInfo.context.attributes.displayName}"/>

    </h2>
    <p class="path">
      <c:choose>
        <%--
          If the assetInfo's 'backToText' attribute has been set,
          and we are at the firstAssetEditContextLevel use the
          custom backToText and backToURL.
        --%>
        <c:when test="${ ! empty assetInfo.attributes.backToText }">
          <c:if test="${ assetInfo.contextLevel == firstAssetEditContextLevel }">
            <a href='<c:out value="${assetInfo.attributes.backToURL}"/>' onmouseover="status='';return true;">
              &laquo;&nbsp;
              <c:out value="${ assetInfo.attributes.backToText }"/>
            </a>
          </c:if>
        </c:when>
        <c:otherwise>
          <a href='<c:out value="${listURL}"/>' onmouseover="status='';return true;">
            &laquo;&nbsp;<fmt:message key="project-list-link-text" bundle="${projectsBundle}"/>
          </a>&nbsp;&nbsp;
          <a href='<c:out value="${assetsURL}"/>' onmouseover="status='';return true;">
            &laquo;&nbsp;<fmt:message key="back-to-process-link" bundle="${projectsBundle}"/>
          </a>
        </c:otherwise>
      </c:choose>
      &nbsp;
    </p>
    <p>
      <fmt:message key="create-asset-helper-text" bundle="${projectsBundle}"/>
    </p>
  </div>

  <c:if test="${ assetInfo.contextLevel == firstAssetEditContextLevel }">
    <c:if test="${ ! empty assetInfo.attributes.contextActionURL }">
      <c:set var="contextActionURL" scope="request"
        value="${assetInfo.attributes.contextActionURL}"/>
    </c:if>
  </c:if>

  <%-- ASSET DETAIL TABLETS --%>
  <table cellpadding="0" border="0" cellspacing="0" width="100%">
    <tr>
      <c:if test="${ assetInfo.contextLevel > firstAssetEditContextLevel }">
        <td class="backToPage">
          <a href="javascript:popContext();" onmouseover="status='';return true;">&laquo; 
            <fmt:message key="backToParentAsset" bundle="${projectsBundle}"/> : 
          </a>
        </td>
      </c:if>

      <%-- asset properties tablet --%>
      <td class="contentTab contentTabAsset">
        <span class="tabOn">
          <fmt:message key="asset-properties-tablet" bundle="${projectsBundle}"/>
        </span>
      </td>
      <td class="contentTabShadow" width="100%">
        &nbsp;
      </td>
    </tr>
    <tr>
      <%-- asset properties tablet --%>
      <td class="contentTabOnBorderAsset">
      </td>
      <td class="contentTabOffBorder">
      </td>
    </tr>
  </table>

  <%-- ASSET INFO  --%>
  <table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBarAsset">
    <tr>
      <td>
        &nbsp;
      </td>
    </tr>
  </table>

 <dspel:include page="assetEditPage.jsp"/>

 <%@ include file="contextForm.jspf" %>

</dspel:page>

<!-- End ProjectsPortlet's createAsset.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/createAsset.jsp#2 $$Change: 651448 $--%>

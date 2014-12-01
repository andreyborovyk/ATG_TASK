<%--
  Asset Properties Page

  @param   assetURI           the publishing asset's URI or null

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>
<%@ page import="atg.epub.portlet.asset.AssetPortlet" %>
<%@ page import="atg.security.*" %>
<%@ page import="atg.versionmanager.*" %>
<%@ page import="atg.repository.*" %>
<%@ page import="atg.epub.PublishingConfiguration" %>
<%@ page import="atg.epub.pws.taglib.GetAssetTag" %>
<%@ page import="atg.epub.pws.taglib.GetAssetTag.Results" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<!-- Begin AssetPortlet's assetProperties.jsp -->

<fmt:setBundle var="assetsBundle" basename="atg.epub.portlet.AssetsPortlet.Resources"/>

<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>
<jsp:useBean id="assetPortlet" class="atg.epub.portlet.asset.AssetPortlet"/>

<portlet:defineObjects/>

<%--
   The clearContext parameter will be set to 'true' when user comes to
   this page by picking an existing asset from the process assets 
   list. It will be false or empty when viewing a linked asset of the
   initial asset.
--%>
<c:if test="${ 'true' == param.clearContext }">
  <c:set target="${assetInfo}" property="clearContext" value="true"/>
</c:if>

<c:set var="debug" value="false" scope="request"/>

<c:if test="${debug}">
  Asset URI param: <c:out value="${param.assetURI}"/>
  <br />
  Context Asset URI: <c:out value="${assetInfo.context.attributes.assetURI}"/>
  <br />
  Asset URI: <c:out value="${assetURI}"/>
  <br />
</c:if>

<!-- set vars for last location, used in AssetBrowse web-app -->
<c:set target="${assetInfo.attributes}" property="assetview" value="${assetPortlet.ASSET_PROPERTIES}"/>
<c:set target="${assetInfo.attributes}" property="asseturi" value="${assetURI}"/>

<c:set var="projectHandlerPath"
  value="/atg/epub/servlet/ProjectFormHandler"/>

<dspel:importbean bean="${projectHandlerPath}" 
  var="projectForm"/>

<script language="JavaScript">

  // URLs to bring user to various processes
  var projectURLs = new Object();

  function okToSubmit() {
    var ok = false;
    var form = document.getElementById( "projectForm" );
    var projectId = document.getElementById( "gotoProjectId" ).value;
    if ( projectId ) {
      ok = true;
    }
    return ok;
  }

  function submitProjectAction() {
    if ( ! okToSubmit() ) {
      alert( "<fmt:message key="no-process-selected" bundle="${assetsBundle}"/>" );
    }
    else {
      var form = document.getElementById( "projectForm" );
      var projectId = document.getElementById( "gotoProjectId" ).value;
      var changeToProcess = document.getElementById("toProcess").checked;
      var atype = document.getElementById( 'atype' );
      if ( changeToProcess ) {
        form.action = projectURLs[projectId].replace( /&amp;/g, "&" );
      }
      form.submit();
    }
  }

</script>

<c:choose>
  <c:when test="${ ! empty param.assetURI }">
    <c:set var="assetURI" value="${param.assetURI}"/>
    <c:if test="${debug}">
      using param.assetURI
      <br />
    </c:if>
  </c:when>
  <c:when test="${ ! empty assetInfo.context.attributes.assetURI}">
    <c:set var="assetURI" value="${assetInfo.context.attributes.assetURI}"/>
    <c:if test="${debug}">
      using context's assetURI
      <br />
    </c:if>
  </c:when>
</c:choose>

<c:set var="isRepositoryItem" value="false"/>

<%-- Get the repository item or virtual file --%>
<c:catch var="ex">
  <c:choose>
    <c:when test="${ ! empty assetURI }">
      <pws:getAsset uri="${assetURI}" var="assetVersion">
        <c:set value="${assetVersion.asset.mainVersion}" 
          var="mainVersion" scope="page"/>
        <c:set var="hasAccess" value="true"/>
        <c:choose>
          <c:when test="${ ! empty mainVersion.virtualFile }">
            <c:set value="${mainVersion.virtualFile}"
              var="item" scope="page"/>
            <c:if test="${debug}">
              virtual file: <c:out value="${mainVersion.virtualFile}"/>
              <br />
              id : <c:out value="${mainVersion.virtualFile}"/>
              <br />
            </c:if>
          </c:when>
          <c:otherwise>
            <c:set value="${mainVersion.repositoryItem}"
              var="item" scope="page"/>
            <c:set var="isRepositoryItem" value="true"/>
            <c:if test="${debug}">
              repository item: <c:out value="${item}"/>
              <br />
            </c:if>

            <%-- Even if the item is not a secured repository item, you can pass it into this droplet
                 and the 'true' oparam will be rendered. This keeps you from having to check if the
                 item is secure or not before you use this droplet --%>
            <dspel:droplet name="/atg/repository/secure/HasItemAccess">
              <dspel:param name="secureItem" value="${assetVersion.repositoryItem}"/>
              <dspel:param name="right" value="write"/>
              <dspel:oparam name="true">
                <c:set var="hasAccess" value="true"/>
              </dspel:oparam>
              <dspel:oparam name="false">
                <c:set var="hasAccess" value="false"/>
              </dspel:oparam>
              <dspel:oparam name="error">
                <c:set var="hasAccess" value="false"/>
                <br/>
                <span style="color: #FF0000">
                  <b><dspel:valueof param="errorMessage"/></b>
                </span>
                <br/>
              </dspel:oparam>
            </dspel:droplet>

          </c:otherwise>
        </c:choose>
      </pws:getAsset>
    </c:when>
  </c:choose>
</c:catch>

<c:if test="${ ex != null }">
  <c:out value="${ex}"/>
</c:if>

<c:if test="${ ! empty item  }">

  <!-- Modify stuff to go in this table -->
  <%@ include file="assetPropertyView.jspf" %>

  <portlet:actionURL var="addAssetActionURL">
    <portlet:param name="assetView"
      value='<%= ""+AssetPortlet.ASSET_PROPERTIES %>'/>
  </portlet:actionURL>

  <c:if test="${ hasAccess == 'true' }">
    <dspel:form formid="projectForm" id="projectForm"
      action="${addAssetActionURL}" method="post">

      <dspel:input type="hidden" value="1" priority="-10"
        bean="ProjectFormHandler.performAssetAction"/>

      <dspel:input type="hidden" 
        bean="ProjectFormHandler.asset" value="${assetURI}" />

      <div class="contentActions">
        <table cellpadding="0" cellspacing="0" border="0">
          <tr>
            <td width="100%" class="blankSpace">&nbsp;
              <dspel:select iclass="medium" priority="-1" bean="ProjectFormHandler.assetAction">
                <dspel:option label="Modify" value="${projectForm.ADD_ASSET_ACTION}">
                  <fmt:message key="modify-asset" bundle="${assetsBundle}"/>
                </dspel:option>
                <dspel:option label="Delete" value="${projectForm.DEL_ASSET_ACTION}">
                  <fmt:message key="delete-asset" bundle="${assetsBundle}"/>
                </dspel:option>
              </dspel:select>

              <fmt:message key="this-asset" bundle="${assetsBundle}"/>
              <pws:getProjects var="projects" sortProperties="displayName">
                <dspel:select bean="ProjectFormHandler.projectId" id="gotoProjectId" priority="10">
                  <dspel:option value=""><fmt:message key="select-one" bundle="${assetsBundle}"/></dspel:option>
                  <c:forEach items="${projects.projects}" var="project">
                    <c:if test="${project.editable}">
                       <dspel:option value="${project.id}">
                        <c:out value="${project.displayName}"/>
                        <dspel:tomap var="creator" value="${project.creator}"/>
                            - <c:out value="${creator.firstName} ${creator.lastName}"/>
                        (<fmt:formatDate value="${project.creationDate}" 
                          type="BOTH" dateStyle="SHORT" timeStyle="SHORT"/>)
                      </dspel:option>
                      <script language="JavaScript">
                        projectURLs[ '<c:out value="${project.id}"/>' ] = 
                          '/atg/bcc/process?projectId=<c:out value="${project.id}"/>&projectView=5';
                      </script>
                    </c:if>
                  </c:forEach>
                </dspel:select>
              </pws:getProjects>

              <input type="checkbox" class="checkbox" id="toProcess" priority="10"/>
              <label for="toProcess">
                <fmt:message key="bring-to-process" bundle="${assetsBundle}"/>
                &nbsp;&nbsp;
              </label>
              <!-- Submit -->
              <a href="javascript:submitProjectAction()" class="goButton" id="addToProces" onmouseover="status='';return true;">
                <fmt:message key="go" bundle="${assetsBundle}"/>
              </a>
            </td>
            <td>
              <c:if test="${ ! empty previewURL }">
                <%-- 
                  the preview() method is defined in the included 
                  previewURLParser.jspf file
                --%>
                <a href="javascript:preview();"
                  class="mainContentButton buttonImage" onmouseover="status='';return true;">
                  <fmt:message key="preview" bundle="${assetsBundle}"/>
                </a>
              </c:if>
            </td>
            <td class="blankSpace">&nbsp;
            </td>
          </tr>
        </table>
      </div> <%-- "contentActions" --%>
    </dspel:form>
  </c:if>

  <%@ include file="../ProjectsPortlet/previewURLParser.jspf" %>

</c:if> <!-- ! empty item -->

</dsp:page>


<!-- End AssetPortlet's assetProperties.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/assetProperties.jsp#2 $$Change: 651448 $--%>

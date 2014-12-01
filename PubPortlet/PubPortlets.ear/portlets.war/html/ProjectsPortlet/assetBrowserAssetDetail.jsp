<%--
  Asset Properties Page

  @param   assetURI           the publishing asset's URI

  @version $Id $$Change $
  @updated $DateTime $$Author $

  Note: There are two AssetInfo stacks in use here, one
  which is the AssetInfo of the client of the picker, 
  called, "processAssetInfo" and one which is used by 
  this page to display the asset, which is called
  "assetInfo".
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!-- Begin assetBrowserAssetDetail.jsp -->

<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<dspel:page>
<style>
  <dspel:include otherContext="${initParam['atg.bizui.ContextPath']}"
    page="/templates/style/css/style.jsp"/>
</style>

<script language="JavaScript" type="text/javascript">
  <dspel:include otherContext="${initParam['atg.bizui.ContextPath']}"
    page="/templates/page/html/scripts/scripts.js"/>
</script>


<fmt:setBundle var="projectsBundle" 
  basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<c:set var="debug" value="false" scope="request"/>

<%--
  This is the AssetInfo used to display information
  about the currently selected asset
--%>
<c:set var="assetInfoPath"
  value="/atg/epub/servlet/AssetBrowserAssetInfo"/>
<dspel:importbean var="assetInfo"
   bean="${assetInfoPath}"/>

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

<pws:getCurrentProject var="projectContext"/>

<c:if test="${ ! empty projectContext }">
  <c:set value="${projectContext.project.workspace}" var="workspace"/>
</c:if>

<c:catch var="ex">

  <fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

  <dspel:page>

    <c:set var="contextFormHandlerPath" 
      value="/atg/epub/servlet/ContextFormHandler"/>
    <dspel:importbean var="contextFormHandler" 
      bean="${contextFormHandler}"/>

    <%--
      This is the AssetInfo used by the current asset in the
      process.
    --%>
    <c:choose>
      <c:when test="${ ! empty param.assetInfo }">
        <c:set var="processAssetInfoPath" value="${param.assetInfo}"/>
      </c:when>
      <c:otherwise>
        <c:set var="processAssetInfoPath" 
          value="/atg/epub/servlet/ProcessAssetInfo"/>
      </c:otherwise>
    </c:choose>
    <dspel:importbean bean="${processAssetInfoPath}" var="processAssetInfo"/>

    <c:set var="projectHandlerPath"
      value="/atg/epub/servlet/ProjectFormHandler"/>
    <dspel:importbean bean="${projectHandlerPath}"
      var="projectFormHandler"/>

    <c:set var="assetPickerHandlerPath"
      value="/atg/epub/servlet/AssetPickerFormHandler"/>
    <dspel:importbean bean="${assetPickerHandlerPath}"
      var="assetPickerHandler"/>

    <%-- set browser mode: 'project' | 'pick' --%>
    <c:set var="mode" value="${processAssetInfo.attributes.browserMode}"
      scope="page"/>

    <c:if test="${debug}">
      Asset URI param: <c:out value="${param.assetURI}"/>
      <br />
      Asset URI from context: <c:out value="${assetInfo.context.attributes.assetURI}"/>
      <br />
    </c:if>

    <%-- get assetURI --%>
    <c:choose>
      <c:when test="${ ! empty param.assetURI }">
        <c:set var="assetURI" value="${param.assetURI}"/>
        <c:set target="${assetInfo.context.attributes}" property="assetURI"
          value="${assetURI}"/>
      </c:when>
      <c:otherwise>
        <c:set var="assetURI" value="${assetInfo.context.attributes.assetURI}"/>
      </c:otherwise>
    </c:choose>

    <%-- Set target form handler information based on mode --%>
    <c:choose>
      <c:when test="${'pick' == mode}">
        <c:set var="formHandlerPath" value="${assetPickerHandlerPath}"/>
        <c:set var="formHandlerAction" value="${assetPickerHandlerPath}.action"/>
        <c:set var="apPropertyName" 
          value="${processAssetInfo.attributes.assetPickerPropertyName}"/>
      </c:when>
      <c:otherwise>
        <c:set var="formHandlerPath" value="${projectHandlerPath}"/>
        <c:set var="formHandlerAction" value="${projectHandlerPath}.assetAction"/>
      </c:otherwise>
    </c:choose>

    <c:url var="assetDetailURL" value="assetBrowserAssetDetail.jsp">
      <c:param name="assetInfo" value="${assetInfoPath}"/>
    </c:url>

    <script language="JavaScript">

      function getContextForm() { return document.getElementById("contextForm"); }

      function pushContext( assetURI ) {
        var form = getContextForm();
        form.elements[ "<c:out value="${contextFormHandlerPath}.assetURI"/>" ].value = 
          assetURI;
        form.elements[ "<c:out value="${contextFormHandlerPath}.contextOp"/>" ].value = 
          <c:out value="${assetInfo.CONTEXT_PUSH}"/>;
        form.submit();
      }

      function popContext() {
        var form = getContextForm();
        form.elements[ "<c:out value="${contextFormHandlerPath}.contextOp"/>" ].value = 
          <c:out value="${assetInfo.CONTEXT_POP}"/>;
        form.submit();
      }

      function closeAssetBrowserWindow() {
        if ( "hide" == '<c:out value="${processAssetInfo.attributes.assetPickerCloseAction}"/>' )
          parent.hideIframe( "assetBrowser" );
        else 
          refreshParentWindow();
      }

      // Called to submit the action form, which is either
      // the project form or the picker form
      function submitBrowseActionForm() {
        var form = document.getElementById( "browseActionForm" );
        <c:choose>
          <c:when test="${mode == 'pick'}">
            var input = document.getElementById( "viewedAssetInfo" ).value;
            var values = input.match( /\[(.*)\]:\[(.*)\]:\[(.*)\]/ );
            if ( values.length == 4 ) {
              var selected = new Object();
              selected.id = values[1];
              selected.displayName = values[2];
              selected.uri = values[3];
              parent[ parent.document.onSelect ]( selected ); 
            }
            else {
              alert( "unable to parse result: " + input );
            }
            form.elements['<c:out value="${formHandlerAction}"/>'].value = "1";
          </c:when>
          <c:when test="${mode == 'project'}">
            form.elements['<c:out value="${formHandlerAction}"/>'].value = 
              '<c:out value="${projectFormHandler.ADD_ASSET_ACTION}"/>';
          </c:when>
        </c:choose>
        form.submit();
        if ( "pick" == '<c:out value="${mode}"/>' )
          closeAssetBrowserWindow();
      }

      function submitRemoveSelectedAssets() {
        var form = document.getElementById("browseActionForm");
        form.elements['<c:out value="${formHandlerAction}"/>'].value = 
          '<c:out value="${projectFormHandler.DEL_ASSET_ACTION}"/>';
        form.submit();
      }

    </script>

    <html>

      <body class="assetBrowser" onload="fireOnLoad();">

        <div id="assetBrowserHeader">
          <h2>
            Asset Browser
          </h2>
        </div>

        <div id="assetBrowserContent">

          <!-- Get the repository item or virtual file -->
          <c:if test="${ ! empty assetURI }">
            <pws:getAsset uri="${assetURI}" var="asset" workspaceName="${workspace}">
              <c:choose>
                <c:when test="${ ! empty asset.workingVersion }">
                  <c:set value="${asset.workingVersion}" var="asset"/>
                </c:when>
                <c:otherwise>
                  <c:set value="${asset.asset.mainVersion}" var="asset"/>
                </c:otherwise>
              </c:choose>
              <c:if test="${asset.repositoryItem != null}">
                <c:set value="${asset.repositoryItem}" var="item" scope="request"/>
              </c:if>
              <c:if test="${asset.virtualFile != null}">
                <c:set value="${asset.virtualFile}" var="item" scope="request"/>
              </c:if>
            </pws:getAsset>
          </c:if>

          <c:if test="${ ! empty item }">

            <dspel:getvalueof var="expert" bean="/atg/userprofiling/Profile.expert"/>

            <biz:getItemMapping item="${item}" var="imap" mode="view" 
              showExpert="${expert}">

              <!-- Set the item mapping into a request scoped variable -->
              <c:set value="${imap}" var="imap" scope="request"/>

              <div class="contentActions">
                <table cellpadding="0" cellspacing="0" border="0">
                  <tr>
                    <td>&nbsp;</td>
                    <td></td>
                    <td width="100%" class="rightAlign">

                      <c:if test="${ assetInfo.contextLevel > 1 }">
                        <a href="javascript:popContext();" onmouseover="status='';return true;">
                         &laquo; Back to Parent
                        </a>
                      </c:if>
                      &nbsp;&nbsp; 

                      <c:url var="backToResultsURL" value="assetBrowserSearchResults.jsp">
                        <c:param name="assetInfo" value="${processAssetInfoPath}"/>
                      </c:url>
                      <a href='<c:out value="${backToResultsURL}"/>' onmouseover="status='';return true;">
                        &laquo;Back to Results
                      </a>
                      &nbsp;&nbsp; 
                      <c:url var="newSearchURL" value="assetBrowser.jsp">
                        <c:param name="assetInfo" value="${processAssetInfoPath}"/>
                      </c:url>
                      <a href='<c:out value="${newSearchURL}"/>' onmouseover="status='';return true;">
                        &laquo;New Search
                      </a>
                      &nbsp;&nbsp;
                    </td>
                  </tr>
                </table>
              </div> <!-- contentActions -->

              <table cellpadding="0" cellspacing="0" border="0"
                style="background-color: #F3F2ED; width: 655px; margin: 0 auto 0 auto; border-left: 1px solid #AAAEB0; border-bottom: 1px solid #A3A3A3">
                <tr>
                  <td class="blankSpace">&nbsp;</td>
                  <td class="assetBrowserAlertMessage">
                    Currently viewing Asset: 
                    <c:choose>
                      <c:when test="${asset.virtualFile != null}">
                        <c:out value="${item.absolutePath}"/>
                      </c:when>
                      <c:otherwise>
                        <c:out value="${item.itemDisplayName}"/>
                      </c:otherwise>
                    </c:choose>
                    <td class="rightAlign">
                      <div class="displayResults">&nbsp;</div>
                    </td>
                  </td>
                </tr>
              </table>

              <div id="assetBrowserResults">
                <div id="nonTableContent">

                  <c:if test="${debug}">
                    item path: <c:out value="${imap.itemPath}"/>
                    <br />
                    item type: <c:out value="${imap.itemName}"/>
                    <br />
                    form handler path: <c:out value="${imap.formHandler.path}"/>
                    <br />
                  </c:if>

                  <!-- Import form handler specified in mapping -->
                  <dspel:importbean var="formHandler" bean="${imap.formHandler.path}"/>

                  <!-- Make the form handler EL variable request scoped -->
                  <c:set value="${formHandler}" var="formHandler" scope="request"/>

                  <%-- Update context with current asset information --%>
                  <c:set target="${assetInfo.context}" property="assetFormHandler"
                    value="${formHandler}"/>
                  <c:set target="${assetInfo.context.attributes}" property="asset"
                    value="${item}"/>
                  <c:set target="${assetInfo.context.attributes}" property="transient"
                    value="false"/>
                  <c:set target="${formHandler}" property="assetInfo"
                    value="${assetInfo}"/>

                  <!-- Transfer mapping attributes to form handler -->
                  <c:forEach items="${imap.formHandler.attributes}" var="attr">
                    <c:set target="${formHandler.attributes}" 
                      property="${attr.key}" value="${attr.value.value}"/>
                  </c:forEach>

                  <c:set target="${formHandler}" property="itemType"
                    value="${imap.itemName}"/>
                  <c:set target="${formHandler}" property="componentPath"
                    value="${imap.itemPath}"/>

                  <!-- Set the item to edit, and its type, into the form handler -->
                  <c:set target="${formHandler}" property="item" value="${item}"/>

                  <c:set value="${formHandler.assetEditor}" target="${imap}"
                    property="itemEditor"/>

                  <%-- 
                    If the form handler defines a value dictionary, set its
                    name in the mapping
                   --%>
                  <c:if test="${ ! empty imap.formHandler.attributes.atgFormValueDict }">
                    <c:set target="${imap}" property="valueDictionaryName"
                      value="${imap.formHandler.attributes.atgFormValueDict.value}"/>
                  </c:if>

                  <!-- Set the name of the Form's value dictionary property -->
                  <c:set target="${imap}" property="valueDictionaryName"
                    value="${imap.formHandler.attributes.atgFormValueDict.value}"/>

                  <%-- Asset view form: controls viewing of asset data --%>
                  <dspel:form action="${actionURL}" method="post" formid="assetActionForm" 
                    id="assetActionForm">

                    <script language="JavaScript">
                      function submitViewChange( viewNumber ) {
                        var form = document.getElementById( "assetActionForm" );
                        form.elements["<c:out value="${imap.formHandler.path}.view"/>"].value = 
                          viewNumber;
                        form.elements["<c:out value="${imap.formHandler.path}.actionType"/>"].value = 
                          "setView";
                        form.submit();
                      }
                    </script>

                    <c:set target="${formHandler}" property="loggingDebug"
                      value="${debug}"/>
                    <c:set target="${formHandler}" property="loggingInfo"
                      value="${debug}"/>
                    <c:set target="${formHandler}" property="loggingWarning"
                      value="${debug}"/>
                    <c:set target="${formHandler}" property="item"
                      value="${item}"/>

                    <c:if test="${empty formHandler.view}">
                      <c:set target="${formHandler}" var="view" value="0"/>
                      Empty!
                    </c:if>

                    <dspel:input type="hidden" value="${debug}" priority="100"
                      bean="${imap.formHandler.path}.loggingDebug"/>
                    <dspel:input type="hidden" value="${assetInfoPath}" priority="100"
                      bean="${imap.formHandler.path}.assetInfoPath"/>
                    <dspel:input type="hidden" bean="${imap.formHandler.path}.actionType" 
                      value="none" priority="-1"/>
                    <dspel:input type="hidden" bean="${imap.formHandler.path}.view" 
                      value="1" />

                    <div class="contentActions">

                      <table cellpadding="0" cellspacing="0" border="0">
                        <tr>
                          <td class="blankSpace">
                            &nbsp;
                          </td>
                          <td>
                            <c:forEach items="${imap.viewMappings}" var="aView" varStatus="vstat">
                              <c:if test="${vstat.first}">
                                <c:set value="${aView}" var="view" scope="request"/>
                              </c:if>
                              <c:choose>
                                <c:when test="${ vstat.index == formHandler.view }">
                                  <c:set value="${aView}" var="view" scope="request"/>
                                  <c:out value="${view.displayName}"/>
                                </c:when>
                                <c:otherwise>
                                  <a href='javascript:submitViewChange("<c:out value="${vstat.index}"/>");'
                                    onmouseover="status='';return true;">
                                    <c:out value="${aView.displayName}"/>
                                  </a>
                                </c:otherwise>
                              </c:choose>
                              <c:if test="${ ! vstat.last }">
                                |
                              </c:if>
                            </c:forEach>
                          </td>
                          <td class="blankSpace" width="100%">&nbsp;
                          </td>
                        </tr>
                      </table>

                    </div> <!-- contentActions -->

                    <!-- list search form handler errors -->
                    <c:forEach items="${formHandler.formExceptions}" var="ex">
                      <br />
                      <b><c:out value="${ex.message}"/></b>
                    </c:forEach>

                    <c:if test="${debug}">
                      <br />
                      View Name: <c:out value="${view.name}"/>
                      <br />
                      Description: <c:out value="${view.description}"/>
                      <br />
                    </c:if>

                    <dspel:include page="${view.uri}"/>

                  </dspel:form>
                  
                  <%-- This does not work, the parameters are ignored by the submit --%>	
                  <c:url value="assetBrowserAssetDetail.jsp" var="addActionURL">
                    <c:param name="assetInfo" value="${processAssetInfoPath}"/>
                    <c:param name="assetURI" value="${assetURI}"/>
                  </c:url>

                  <pws:createVersionManagerURI object="${item}" var="uri"> 

                    <%-- Browse Action Form: Add asset to project or pick asset --%>
                    <dspel:form formid="browseActionForm" id="browseActionForm" action="${addActionURL}">
                      <%-- the form handler's action property --%>
                      <dspel:input type="hidden" bean="${formHandlerAction}" 
                        value="1" priority="-1"/>
                      <c:choose>
                        <c:when test="${ 'project' == mode }">
                          <dspel:input type="hidden" bean="ProjectFormHandler.projectId"
                            beanvalue="/atg/web/ATGSession.assetBrowserProjectId"/>
                          <dspel:input bean="ProjectFormHandler.assets"
                            type="hidden" value="${uri}"/>
                          <dspel:input type="hidden" bean="ProjectFormHandler.performAssetAction" 
                            value="1" priority="-10"/>
                        </c:when>
                        <c:when test="${ 'pick' == mode }">
                          <dspel:input type="hidden" value="${processAssetInfoPath}" priority="100"
                            bean="${assetPickerHandlerPath}.assetInfoPath"/>
                          <c:choose>
                            <%-- virtual file picked --%>
                            <c:when test="${ item.class.name == 'atg.vfs.repository.RepositoryVirtualFile' }">
                              <dspel:input bean="${assetPickerHandlerPath}.resultURIs"
                                type="hidden" id="viewedAssetInfo"
                                value="[${item.canonicalPath}]:[${item.canonicalPath}]:[${uri}]"/>
                            </c:when>
                            <%-- repository item picked --%>
                            <c:otherwise>
                              <dspel:input bean="${assetPickerHandlerPath}.resultURIs"
                                type="hidden" id="viewedAssetInfo"
                                value="[${item.repositoryId}]:[${item.itemDisplayName}]:[${uri}]"/>
                            </c:otherwise>
                          </c:choose>
                        </c:when>
                      </c:choose>
                    </dspel:form>
                  </pws:createVersionManagerURI>
                </div> <!-- nonTableContent -->
              </div> <!-- assetBrowserResults -->

              <div class="okAlt">
                <table cellpadding="0" cellspacing="0" border="0">
                  <tr>
                    <td width="100%" class="blankSpace">
                      &nbsp;
                    </td>
                    <c:if test="${ assetInfo.contextLevel == 1 }">
                      <c:if test="${ 'project' == mode }">
                        <td>
                          <a href="javascript:submitRemoveSelectedAssets()" 
                            class="mainContentButton go" onmouseover="status='';return true;">
                            <fmt:message key="remove-this-asset-in-process" 
                              bundle="${projectsBundle}"/>
                          </a>
                        </td>
                      </c:if>
                      <td>
                        <a href="javascript:submitBrowseActionForm();" 
                          class="mainContentButton modify" onmouseover="status='';return true;">
                          <c:choose>
                            <c:when test="${ 'project' == mode }">
                              <fmt:message key="add-asset-to-this-process" bundle="${projectsBundle}"/>
                            </c:when>
                            <c:when test="${ 'pick' == mode }">
                              <fmt:message key="select-asset" bundle="${projectsBundle}"/>
                            </c:when>
                          </c:choose>
                        </a>
                      </td>
                    </c:if>
                    <td class="buttonImage">
                      <a href="javascript:closeAssetBrowserWindow()" 
                        class="mainContentButton cancel" onmouseover="status='';return true;">
                        <c:choose>
                          <c:when test="${'pick' == processAssetInfo.attributes.browserMode}">
                            <fmt:message key="cancel-button" bundle="${projectsBundle}"/>
                          </c:when>
                          <c:otherwise>
                            <fmt:message key="done-button" bundle="${projectsBundle}"/>
                          </c:otherwise>
                        </c:choose>
                      </a>
                    </td>
                    <td>
                      &nbsp;
                    </td>
                  </tr>
                </table>
              </div> <!-- okAlt -->
            </biz:getItemMapping>
          </c:if>

        </div>

        <c:url var="assetDetailURL" value="assetBrowserAssetDetail.jsp">
          <c:param name="assetInfo" value="${processAssetInfoPath}"/>
        </c:url>

        <dspel:form id="contextForm" formid="contextForm" method="post" 
          action="${assetDetailURL}" enctype="multipart/form-data">
          <dspel:input type="hidden" value="${debug}" priority="100"
            bean="${contextFormHandlerPath}.loggingDebug"/>
          <dspel:input type="hidden" value="${assetInfoPath}" priority="100"
            bean="${contextFormHandlerPath}.assetInfoPath"/>
          <dspel:input type="hidden" value="${assetInfo.CONTEXT_PUSH}" 
            priority="-1" bean="${contextFormHandlerPath}.contextOp"/>
          <dspel:input type="hidden" value="1"
            bean="${contextFormHandlerPath}.assetURI"/>
          <dspel:input type="hidden" priority="-10" value="1"
            bean="${contextFormHandlerPath}.contextAction"/>
        </dspel:form>

      </body>
    </html>

  </dspel:page>

</c:catch>

<c:if test="${ ! empty ex }">
  <c:out value="${ex}"/>
</c:if>
</dspel:page>
<!-- End assetBrowserAssetDetail.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetBrowserAssetDetail.jsp#2 $$Change: 651448 $--%>

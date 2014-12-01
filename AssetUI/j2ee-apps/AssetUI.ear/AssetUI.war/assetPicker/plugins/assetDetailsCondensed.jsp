<%--
  Asset property details
  
  @param assetInfo
    Client path to nucleus AssetInfo context object
    Source: client

  @param clientView
    Constant which determines the client URL (see: atg.web.assetpicker.Constants
    Source: container

  @param clearContext
    Parameter which determines if the asset context in the asset picker
    should be cleared
    Source: repositorySearchResults.jsp
  
  @param clearAttributes
    Parameter which determines if the asset context attributes in the 
    asset picker should be cleared
    Source: repositorySearchResults.jsp
  
  @param assetURI
    Version manager URI of the asset to be displayed in the container
    Source: repositorySearchResults.jsp

  Container Javascript methods called:
    getAssetTypes()
      Returns an associative array of the asset type which to search
      consisting of the following keys and values:
        itemType            - asset type
        componentPath       - component path to repository/vfs
        itemDisplayProperty - item's display name property used for sorting
        encodedType         - assetType:componentPath

  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/assetDetailsCondensed.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin assetDetailsCondensed.jsp -->

<dspel:page>

<dspel:importbean var="config" bean="/atg/assetui/Configuration"/>
<fmt:setBundle var="assetuiBundle" basename="${config.resourceBundle}"/>

<c:catch var="ex">
  <%@ include file="/assetPicker/assetPickerConstants.jsp" %>
  
  <c:set var="debug" value="false" />

  <%-- asset URI --%>
  <c:set var="assetURI" value="${ param.assetURI }" />
  
  <%-- these parameters determine if the asset context is cleared --%>
  <c:set var="clearContext" value="${ param.clearContext }" />
  <c:set var="clearAttributes" value="${ param.clearAttributes}" />

  <%-- calling client URL --%>
  <c:choose>
    <c:when test="${ param.clientView eq ASSET_PICKER_IFRAME }">
      <c:set var="clientURL" value="${ ASSET_PICKER_IFRAME_URL }"/>
    </c:when>
    <c:when test="${ param.clientView eq BROWSE_TAB }">
      <c:set var="clientURL" value="${ BROWSE_TAB_URL }"/>
    </c:when>
    <c:otherwise>
      <c:set var="clientURL" value="${ ASSET_PICKER_IFRAME_URL }"/>
    </c:otherwise>
  </c:choose>

  <%--
    This is the AssetInfo used to display information
    about the currently selected asset in the picker
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
  <script language="JavaScript">
    var clearContext = "<c:out value='${param.clearContext}'/>";
    var clearAttributes = "<c:out value='${param.clearAttributes}'/>";
  </script>
  
  <c:if test="${ 'true' == param.clearContext }">
    <c:set target="${assetInfo}" property="clearContext" value="true"/>
  </c:if>
  <c:if test="${ 'true' == param.clearAttributes }">
    <c:set target="${assetInfo}" property="clearAttributes" value="true"/>
  </c:if>

  <%-- get/set assetURI --%>
  <c:choose>
    <c:when test="${ ! empty assetURI }">
      <c:set var="assetURI" value="${ param.assetURI }"/>
      <c:set target="${ assetInfo.context.attributes }" property="assetURI"
        value="${assetURI}"/>
    </c:when>
    <c:otherwise>
      <c:set var="assetURI" value="${assetInfo.context.attributes.assetURI}"/>
    </c:otherwise>
  </c:choose>

  <%-- gettign current project and workspace --%>
  <pws:getCurrentProject var="projectContext"/>
  <c:if test="${ ! empty projectContext }">
    <c:set value="${projectContext.project.workspace}" var="workspace"/>
  </c:if>

  <c:set var="contextFormHandlerPath" 
    value="/atg/epub/servlet/ContextFormHandler"/>
  <dspel:importbean var="contextFormHandler" 
    bean="${contextFormHandler}"/>

  <%--
    This is the AssetInfo used by the current asset in the
    process (if there is one).
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

  <%-- Get the repository item or virtual file --%>
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
        <c:set var="assetId" value="${item.repositoryId}"/>
        <c:set var="assetDisplayName" value="${item.itemDisplayName}"/>
      </c:if>
      <c:if test="${asset.virtualFile != null}">
        <c:set value="${asset.virtualFile}" var="item" scope="request"/>
        <c:set var="assetId" value="${item.canonicalPath}"/>
        <c:set var="assetDisplayName" value="${item.absolutePath}"/>
      </c:if>
    </pws:getAsset>
  </c:if>

  <div id="assetBrowserContentBody">
    <c:if test="${ ! empty item }">
      <biz:getItemMapping var="imap" item="${item}" mode="view">

        <%-- Set the item mapping into a request scoped variable --%>
        <c:set value="${imap}" var="imap" scope="request"/>

        <%-- Import form handler specified in mapping --%>
        <dspel:importbean var="formHandler" bean="${imap.formHandler.path}"/>

        <%-- Make the form handler EL variable request scoped --%>
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

        <%-- Transfer mapping attributes to form handler --%>
        <c:forEach items="${imap.formHandler.attributes}" var="attr">
          <c:set target="${formHandler.attributes}" 
            property="${attr.key}" value="${attr.value.value}"/>
        </c:forEach>

        <c:set target="${formHandler}" property="itemType"
          value="${imap.itemName}"/>
        <c:set target="${formHandler}" property="componentPath"
          value="${imap.itemPath}"/>

        <%-- Set the item to edit, and its type, into the form handler --%>
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

        <%-- Set the name of the Form's value dictionary property --%>
        <c:set target="${imap}" property="valueDictionaryName"
          value="${imap.formHandler.attributes.atgFormValueDict.value}"/>


        <c:url var="itemViewActionURL" value="${ clientURL }">
          <c:param name="assetInfo" value="${ processAssetInfoPath }"/>
          <c:param name="assetURI" value="${ assetURI }"/>
          <c:param name="clearContext" value="false"/>
          <c:param name="clearAttributes" value="false"/>
          <c:param name="assetView" value="${ ASSET_DETAILS_CONDENSED }"/>
        </c:url>
        
        <%
          String displayName = (String)pageContext.findAttribute("assetDisplayName");
          if ( displayName != null && displayName.length() > 60 )
            displayName = displayName.substring(0,59) + "...";
        %>

        <%-- Asset view form: controls viewing of asset data --%>
        <dspel:form action="${itemViewActionURL}" method="post" formid="itemViewForm" 
          id="itemViewForm">

          <div id="assetListHeader">
            <div id="assetListHeaderRight"></div>
            <div id="assetListHeaderLeft"><fmt:message key='viewing' bundle='${assetuiBundle}' /> <strong title='<c:out value="${assetDisplayName}"/>'><%= displayName %></strong></div>
          </div>

          <div id="assetListSubHeader">

            <c:url var="backToPickerURL" value="${ clientURL }">
              <c:param name="assetInfo" value="${processAssetInfoPath}"/>
              <c:param name="clearContext" value="y"/>
              <c:param name="clearAttributes" value="y"/>
            </c:url>

            <div id="assetListSubHeaderRight">
              <a href="<c:out value='${backToPickerURL}'/>" >
                &lt;&lt; <fmt:message key='back' bundle='${assetuiBundle}' />
              </a>
            </div>

            <div id="assetListSubHeaderLeft">
              <c:if test="${ assetInfo.contextLevel > 1 }">
                <a href="javascript:popContext();" onmouseover="status='';return true;">
                 &laquo; <fmt:message key='back-to-parent' bundle='${assetuiBundle}' /> &nbsp;
                </a>
              </c:if>
              
              <c:forEach items="${imap.viewMappings}" var="aView" varStatus="vstat">
                <c:if test="${vstat.first}">
                  <c:set value="${aView}" var="view" scope="request"/>
                </c:if>
                <c:if test="${ vstat.count < 1 }">
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
                    &nbsp;|&nbsp;
                  </c:if>
                </c:if>
              </c:forEach>

              <c:forEach items="${formHandler.formExceptions}" var="ex">
                <br />
                <b><c:out value="${ex.message}"/></b>
              </c:forEach>
            </div> <%-- assetListSubHeaderLeft --%>

          </div>

          <div id="scrollContainer">
            <c:if test="${debug}">
              Asset URI param: <c:out value="${param.assetURI}"/>
              <br />
              Asset URI from context: <c:out value="${assetInfo.context.attributes.assetURI}"/>
              <br />
              item path: <c:out value="${imap.itemPath}"/>
              <br />
              item type: <c:out value="${imap.itemName}"/>
              <br />
              form handler path: <c:out value="${imap.formHandler.path}"/>
              <br />
              View Name: <c:out value="${view.name}"/>
              <br />
              Description: <c:out value="${view.description}"/>
              <br />
              View Name: <c:out value="${view.name}"/>
              <br />
              Description: <c:out value="${view.description}"/>
              <br />
            </c:if>

            <script language="JavaScript">
              function submitViewChange( viewNumber ) {
                var form = document.getElementById( "itemViewForm" );
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

            <dspel:include otherContext="${view.contextRoot}" page="${view.uri}"/>

          </div>

          <table id="scrollFooter">
            <tr>
              <td id="footerCount">
                <div id="footerCountRight"></div>
                <div id="footerCountLeft"></div>
              </td>
            </tr>
          </table>

        </dspel:form>
        
      </biz:getItemMapping>
    </c:if>
  </div>

  <script language="JavaScript">

    function getContextForm() { return document.getElementById("contextForm"); }

    function pushContext( assetURI ) {
      var form = getContextForm();
      form.elements[ "<c:out value='${contextFormHandlerPath}.assetURI'/>" ].value = 
        assetURI;
      form.elements[ "<c:out value='${contextFormHandlerPath}.contextOp'/>" ].value = 
        <c:out value="${assetInfo.CONTEXT_PUSH}"/>;
      form.submit();
    }

    function popContext() {
      var form = getContextForm();
      form.elements[ "<c:out value="${contextFormHandlerPath}.contextOp"/>" ].value = 
        <c:out value="${assetInfo.CONTEXT_POP}"/>;
      form.submit();
    }
    
    //
    // Returns an array of of associative arrays, each consisting of the
    // following keys and values:
    //   id          - the asset's ID
    //   displayName - the asset's display name
    //   uri         - the asset's version manager URI
    //
    function getSelectedAssets() {

      var selectedResults = new Array();

      var selectedAssetURI = "<c:out value='${ assetURI }' />";

      // if there's an asset in the asset picker context, return it
      if( selectedAssetURI != "") {
        var selectedResult = new Object();
        selectedResult.id          = "<c:out value='${ assetId }' />";
        selectedResult.displayName = "<c:out value='${ assetDisplayName }' />";
        selectedResult.uri         = selectedAssetURI;

        selectedResults[0] = selectedResult;
      }
      
      return selectedResults;
    }
  </script>

  <c:url var="assetDetailURL" value="${ clientURL }">
    <c:param name="assetInfo" value="${ processAssetInfoPath }"/>
    <c:param name="clearContext" value="false"/>
    <c:param name="clearAttributes" value="false"/>
    <c:param name="assetView" value="${ ASSET_DETAILS_CONDENSED }"/>
  </c:url>

  <%-- ContextFormHandler for pushing/popping assets --%>
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

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in assetDetailsCondensed.jsp:");
    tt.printStackTrace();
  }
%>
</dspel:page>

<!-- End assetDetailsCondensed.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/assetDetailsCondensed.jsp#2 $$Change: 651448 $--%>

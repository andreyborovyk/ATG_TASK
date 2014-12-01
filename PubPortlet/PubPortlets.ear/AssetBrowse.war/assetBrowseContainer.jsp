<%--
  Browse picker plugin container page

  @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowseContainer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>


<%@ page import="java.io.*,java.util.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin browseAssetPickerContainer.jsp -->

<dspel:page>

<c:catch var="ex">

  <%@ include file="assetPickerConstants.jsp" %>

  <fmt:setBundle var="bundle" basename="atg.bizui.assetbrowse" />

  <dspel:importbean var="config" bean="/atg/assetui/Configuration"/>
  <dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dspel:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dspel:importbean  bean="/atg/epub/servlet/RepositoryAssetFormHandler"/>

  <%-- set to true to see lots of debugging information --%>
  <c:set var="debug" value="false" scope="request"/>

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

  <c:set var="searchFormHandlerPath" 
        value="/atg/web/assetbrowse/servlet/AssetBrowserSearchFormHandler"/>

  <%-- Get the AssetPickerFormHandler --%>
  <c:set var="assetPickerFormHandlerPath" 
    value="/atg/web/assetbrowse/servlet/AssetPickerFormHandler"/>
    <dspel:importbean bean="${assetPickerFormHandlerPath}"
    var="assetPickerFormHandler"/>

  <c:set var="projectHandlerPath"
    value="/atg/epub/servlet/ProjectFormHandler"/>

  <dspel:importbean bean="${projectHandlerPath}" 
    var="projectForm"/>

  <%-- select mode --%>
  <c:set target="${assetInfo.attributes}" 
	property="isAllowMultiSelect" value="true"/>
  <c:set var="isAllowMultiSelect" value="${assetInfo.attributes.isAllowMultiSelect}"/>

  <%-- asset component path --%>
  <c:if test="${ ! empty assetInfo.attributes.componentPath}">
    <c:set var="componentPath" value="${assetInfo.attributes.componentPath}"/>
  </c:if>

  <%-- asset item type --%>
  <c:if test="${ ! empty assetInfo.attributes.itemType }">
    <c:set var="itemType" value="${assetInfo.attributes.itemType}"/>
  </c:if>

  <%-- view configuration, must not be null, set to empty string --%>
  <c:set target="${assetPickerFormHandler}" property="viewConfiguration" value=""/>

  <%-- iterate asset types to display currently viewed type --%>
  <pws:getVersionedAssetTypes var="assetTypes">
    <c:forEach items="${assetTypes}" var="result">
      <c:forEach items="${result.types}" var="type">

        <c:if test="${ empty componentPath}">
          <c:set var="componentPath" value="${result.componentPath}"/>
          <c:set var="itemType" value="${type.typeName}"/>
          <c:set target="${assetInfo.attributes}" property="itemType" value="${type.typeName}"/>
        </c:if>

        <c:if test="${itemType==type.typeName }">
          <c:set var="itemTypeDisplayName" value="${type.displayName}"/>
        </c:if>
      </c:forEach>
    </c:forEach>
  </pws:getVersionedAssetTypes>

  <%-- save asset view to session --%>
  <c:set target="${assetInfo.attributes}" property="assetview" value="${SEARCH_RESULTS}"/>
  <c:set target="${assetInfo.attributes}" property="asseturi" value=""/>

  <script language="JavaScript">

    var projectURLs = new Object();

    //
    // Returns the selected asset type to the plugin
    //
    function getAssetType() {

      var assetType             = new Object();
        assetType.itemType      = "<c:out value='${itemType}'/>";
        assetType.componentPath = "<c:out value='${componentPath}'/>";
        assetType.encodedType   = assetType.componentPath 
                                    + ":" 
                                    + assetType.itemType;
        assetType.itemDisplayProperty
          = getItemDisplayProperty( assetType.componentPath, assetType.itemType );

      return assetType;
    }
    
  </script>
  
  <c:if test="${debug}">
    <p>componentPath: <c:out value="${componentPath}"/>
    <p>itemType: <c:out value="${itemType}"/>
    <p>itemTypeDisplayName: <c:out value="${itemTypeDisplayName}"/>

    <p>selectMode: <c:out value="${isAllowMultiSelect}"/>
    
    <biz:getItemMapping var="imap" itemName="${itemType}" itemPath="${componentPath}" mode="pick" />

    <c:if test="${ !empty imap }"> <%-- or something --%>

      <p>imap.description: <c:out value="${imap.description}"/>
      <p>imap fh path: <c:out value="${imap.formHandler.path}"/>

      <c:set var="viewMappings" value="${imap.viewMappings}"/>

      <p>viewMappings/view:
      <ol>        
        <c:forEach var="viewMapping" items="${viewMappings}" varStatus="i">
          <c:if test="${i.first}">
            <c:set var="view" value="${viewMapping}" />
          </c:if>
          <li>viewMapping: <c:out value="${viewMapping.description}"/>, 
              view: <c:out value="${viewMapping.uri}"/>, 
              class: <c:out value="${viewMapping.class.name}"/>
              attributes: <c:out value="${viewMapping.attributes}"/>
        </c:forEach>
      </ol>
    </c:if>
  </c:if>

  <biz:getItemMapping var="imap" itemName="${itemType}" itemPath="${componentPath}" mode="pick" />  

    <c:if test="${ !empty imap }"> 
      <c:set var="viewMappings" value="${imap.viewMappings}"/>

      <c:url var="selectionURL" value="${BROWSE_TAB_URL}">
        <c:param name="assetInfo" value="${assetInfoPath}"/>
        <c:param name="apView" value="${ AP_CONTAINER }"/>
      </c:url>

    <div id="mainContent">
      <div id="intro">
        <h2><span class="pathSub"><fmt:message key="page-label-browse" bundle="${bundle}"/>:</span> <c:out value="${itemTypeDisplayName}"/> </h2>
	<p class="path">&nbsp;</p>
      </div>  <!-- intro -->

      <dspel:droplet name="Switch">
        <dspel:param bean="RepositoryAssetFormHandler.formError" name="value"/>
        <dspel:oparam name="true">
          <ul>
          <dspel:droplet name="ErrorMessageForEach">
            <dspel:param bean="RepositoryAssetFormHandler.formExceptions" name="exceptions"/>
            <dspel:oparam name="output">
              <li><div class="error"><dspel:valueof param="message"/></div>
            </dspel:oparam>
          </dspel:droplet>
          </ul>
        </dspel:oparam>
      </dspel:droplet>

      <!-- begin content -->
      <table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
	<td valign="top">

          <!-- Tab selection form -->
          <dspel:form formid="abSelectForm" name="abSelectForm" id="abSelectForm" action="${selectionURL}" method="post">

	  <table cellpadding="0" border="0" cellspacing="0">
	    <!-- Tabs Row -->
	    <tr>
	    <td class="contentTabShadow" style="width: 20px;">&nbsp;</td>

	      <c:set var="currentview" value="${assetPickerFormHandler.view}" />
	      <c:forEach var="viewMapping" items="${viewMappings}" varStatus="i">

                <c:if test="${i.first}">
                  <c:set var="selectedView" value="${viewMapping}" />

	          <c:if test="${ empty currentview }">
                    <c:set var="currentview" value="${viewMapping.attributes.tabDisplayName}" />
		  </c:if>

                </c:if>
                <c:choose>
                  <c:when test="${ viewMapping.attributes.tabDisplayName eq currentview }">
                    <c:set var="selectedView" value="${viewMapping}"/>

		      <td class="contentTab contentTabAsset"><span class="tabOn"><c:out value="${viewMapping.attributes.tabDisplayName}"/></span></td>

                  </c:when>
                  <c:otherwise>
	           
		    <td class="contentTab contentTabAsset"><a href='javascript:submitViewChange( "<c:out value="${viewMapping.attributes.tabDisplayName}"/>" );' class="tabOff"><c:out value="${viewMapping.attributes.tabDisplayName}"/></a></td>

                  </c:otherwise>
                </c:choose>
              </c:forEach>
              <td class="contentTabShadow tabNote">
                &nbsp;
              </td>
	    </tr>
               
            <!-- Below Tabs Row, must iterate again for proper formatting -->
	    <tr>
	    <td class="contentTabOffBorder"></td>
              <c:forEach var="viewMapping" items="${viewMappings}" varStatus="i">
                <c:choose>
                  <c:when test="${ viewMapping.attributes.tabDisplayName eq currentview }">
		    <td class="contentTabOnBorderAsset"></td>		
                  </c:when>
                  <c:otherwise>
		    <td class="contentTabOffBorder"></td>
                  </c:otherwise>
                </c:choose>
              </c:forEach>

	    <td class="contentTabOffBorder"></td>
	    </tr>
 	  </table>

          <table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBarAsset">
	    <tr>
	    <td>
	      <table cellpadding="2" cellspacing="0" border="0" id="searchOptions">
		<tr>
		<td class="formLabel formLabelRequired"><fmt:message key="page-label-search-repository" bundle="${bundle}"/>:</td>
		<td>
                  <select class="formElement" name="selectedRepository" id="selectedRepository"
	              onchange='submitSelectionChange( document.abSelectForm.selectedRepository[ document.abSelectForm.selectedRepository.options.selectedIndex ].value, "<c:out value="${currentview}"/>" );'>
                  </select>
                </td>
		</tr>
		<tr>
		<td class="formLabel"><fmt:message key="page-label-search-assettype" bundle="${bundle}"/>:</td>
		<td>
                  <select class="formElement" name="selectedItemType" id="selectedItemType" onchange='submitViewChange( "<c:out value="${currentview}"/>" );'></select>
                </td>
		</tr>
	      </table>		
	    </td>
	    </tr>
    	  </table>

          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.view"
            value=""
            id="view" name="view" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.componentPath"
            id="componentPath" name="componentPath" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.itemType"
            id="itemType" name="itemType" />
          <dspel:input type="hidden" priority="100"
            bean="${assetPickerFormHandlerPath}.clearSearchResults"
            id="clearSearchResults" name="clearSearchResults" 
            value="true" />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.assetInfoPath"
            value="${assetInfoPath}"
            id="assetInfoPath" name="assetInfoPath" />
          <dspel:input type="hidden" priority="-10" value="1"
            bean="${assetPickerFormHandlerPath}.configureAssetInfo"  />
          <dspel:input type="hidden" priority="10"
            bean="${assetPickerFormHandlerPath}.apView"
            value="${AP_CONTAINER}"
            id="apView" name="apView" />

          </dspel:form>

          <!-- Construct plugin url to include --> 

          <c:if test="${debug}">
            <p> selectedView.contextRoot : <c:out value="${selectedView.contextRoot}"/>
          </c:if>

          <c:url var="itemViewURI" value="${selectedView.uri}">
            <c:param name="assetInfo" value="${assetInfoPath}"/>
            <c:param name="searchForm" value="${searchFormHandlerPath}"/>            
            <c:param name="isCondensed" value="false"/>
	    <c:param name="isAllowMultiSelect" value="true"/>
	    <c:param name="showRefresh" value="true"/>            
	    <c:param name="bccContext" value="${BCC_CONTEXT}"/>
	    <c:param name="assetBrowseContext" value="${ASSETBROWSE_CONTEXT}"/>
            <c:param name="clientView" value="${BROWSE_TAB}"/>
            <c:if test="${ ! empty imap.attributes.treeComponent }">
              <c:param name="treeComponent" value="${imap.attributes.treeComponent.value}"/>
            </c:if>
          </c:url>

          <dspel:include otherContext="${selectedView.contextRoot}" src="${itemViewURI}" />

          <!-- Project actions  -->

          <dspel:form formid="projectForm" id="projectForm" action="${selectionURL}" method="post">

            <dspel:input type="hidden" value="true" priority="100"
              bean="ProjectFormHandler.loggingDebug"/>

            <dspel:input type="hidden" value="1" priority="-10"
              bean="ProjectFormHandler.performAssetAction"/>

            <dspel:input type="hidden" bean="ProjectFormHandler.pipeDelimitedAssetURIList"
              name="pipeDelimitedAssetURIList"/>

            <div class="contentActions">
              <table cellpadding="0" cellspacing="0" border="0">
                <tr>	
                <td class="paddingLeft" width="100%">

                  <dspel:select iclass="medium" priority="-1" bean="ProjectFormHandler.assetAction">
                    <dspel:option label="Modify" value="${projectForm.ADD_ASSET_ACTION}">
                      <fmt:message key="select-option-modify" bundle="${bundle}"/>
                    </dspel:option>
                    <dspel:option label="Delete" value="${projectForm.DEL_ASSET_ACTION}">
                      <fmt:message key="select-option-delete" bundle="${bundle}"/>
                    </dspel:option>
                  </dspel:select>

                  select assets in project: 
                  <pws:getProjects var="projects" sortProperties="displayName">
                    <dspel:select bean="ProjectFormHandler.projectId" id="gotoProjectId" priority="10">
                      <dspel:option value=""><fmt:message key="select-option-selectone" bundle="${bundle}"/>:</dspel:option>
                      <c:forEach items="${projects.projects}" var="project">
                        <c:if test="${project.editable}">
                          <dspel:option value="${project.id}">
                            <c:out value="${project.displayName}"/> -
                            <dspel:tomap var="creator" value="${project.creator}"/>
                            - <c:out value="${creator.firstName} ${creator.lastName}"/>
                            (<fmt:formatDate value="${project.creationDate}" 
                              type="BOTH" dateStyle="SHORT" timeStyle="SHORT"/>)
                          </dspel:option>
  
                          <script language="JavaScript">
                             projectURLs[ '<c:out value="${project.id}"/>' ] = 
                              '/atg/bcc/process?projectId=<c:out value="${project.id}"/>&projectView=3';
                          </script>

                        </c:if>
                      </c:forEach>
                    </dspel:select>
                  </pws:getProjects>

                  <input type="checkbox" class="checkbox" id="toProcess" priority="10"/>
                  <label for="toProcess">
                    <fmt:message key="page-label-to-process" bundle="${bundle}"/>:    &nbsp;&nbsp;
                  </label>
                  <!-- Submit -->
                  <a href="javascript:submitProjectAction()" class="goButton" id="addToProces" onmouseover="status='';return true;"><fmt:message key="page-link-submit-project-form" bundle="${bundle}"/></a>
                </td>	
                <td class="blankSpace">
                </td>
	        </tr>
	      </table>
            </div>

	  </dspel:form>


        </td>
	</tr>	
      </table>

    </div> <!-- mainContent -->
    </c:if> <!-- imap -->

    <script language="JavaScript">

      //
      // Button Actions
      //
      function submitProjectAction() {

        var form = document.getElementById( "projectForm" );
        var projectId = document.getElementById( "gotoProjectId" ).value;

        if (! projectId ) {
          alert( "<fmt:message key="no-process-selected" bundle="${bundle}"/>" );
          return ;
        }

        var changeToProcess = document.getElementById("toProcess").checked;

        var selectedAssets = getSelectedAssets();
        var assetList = new String();
        for( var i=0; i<selectedAssets.length; i++) {
          assetList += selectedAssets[i].uri + "|";
        }

        form.pipeDelimitedAssetURIList.value = assetList;
  
        if ( changeToProcess ) {
	  form.action = projectURLs[projectId];
         }

        form.submit();

      }

      //
      // Submit view change, a pulldown is changed
      //  
      function submitSelectionChange( selectedRepository, selectedTabIndex ) {
 
        var abSelectForm = document.getElementById("abSelectForm");
	populateTypePullDown( selectedRepository ) ;

        document.abSelectForm.componentPath.value =
          document.abSelectForm.selectedRepository.value;
        document.abSelectForm.itemType.value =
          document.abSelectForm.selectedItemType.value;

        abSelectForm.submit();

      }

      //
      // Refresh the current view 
      //  
      function refreshCurrentView() {
        submitViewChange('<c:out value="${currentview}"/>');
      }        

      //
      // Submit view change, a tab is clicked
      //
      function submitViewChange( selectedTabIndex ) {

        var abSelectForm = document.getElementById("abSelectForm");
        abSelectForm.view.value = selectedTabIndex;

        document.abSelectForm.componentPath.value =
          document.abSelectForm.selectedRepository.value;
        document.abSelectForm.itemType.value =
          document.abSelectForm.selectedItemType.value;

        abSelectForm.submit();
      }

      loadAssetPicker();

    </script>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in assetPickerContainer.jsp:");
    tt.printStackTrace();
  }
%>

</dspel:page>

<!-- End assetPickerContainer.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowseContainer.jsp#2 $$Change: 651448 $--%>

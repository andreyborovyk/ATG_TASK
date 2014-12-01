<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<c:set var="ATGSessionPath" value="/atg/web/ATGSession"/>
<dspel:importbean var="ATGSession" bean="${ATGSessionPath}"/>
<dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dspel:importbean bean="/atg/dynamo/droplet/Switch"/>

<c:set var="assetBrowserCreateURL" value="${ATGSession.assetBrowserCreateURL}" />

<%-- Create form action--%>
<c:url var="createFormAction" value="${ ASSET_PICKER_IFRAME_URL }">
  <c:param name="apView" value="${ AP_ITEM_TYPE_SELECT }"/>
  <c:param name="assetInfo" value="${assetInfoPath}"/>
  <c:param name="wndCmd" value="close"/>
</c:url>

<%-- Handle create repository assets --%>
<c:set var="repositoryAssetFormHandlerPath"
  value="/atg/epub/servlet/RepositoryAssetFormHandler"/>
<dspel:importbean var="repositoryAssetFormHandler" 
  bean="${repositoryAssetFormHandlerPath}"/>

<dspel:setvalue 
  bean="${repositoryAssetFormHandlerPath}.requireIdOnCreate"
  value="false"/>


<dspel:form action="${createFormAction}" method="post" 
  formid="repositoryAssetCreateForm" name="repositoryAssetCreateForm" >
  <dspel:input type="hidden" priority="100"
    bean="${repositoryAssetFormHandlerPath}.loggingDebug"
    value="${debug}"/>
  <dspel:input type="hidden" priority="100"
    bean="${repositoryAssetFormHandlerPath}.assetInfoPath"
    value="${assetInfoPath}"/>
  <%-- populate me please --%>
  <dspel:input type="hidden" priority="10" 
    id="repositoryContextOp" name="repositoryContextOp"
    bean="${repositoryAssetFormHandlerPath}.contextOp" 
    value="0"/>
  <dspel:input type="hidden" priority="10" 
    id="repositoryCreateType" name="repositoryCreateType"
    bean="${repositoryAssetFormHandlerPath}.encodedPathAndItemType"/>
  <dspel:input type="hidden" priority="10" 
    id="repositoryAssetDisplayName" name="repositoryAssetDisplayName"
    bean="${repositoryAssetFormHandlerPath}.displayName"/>
  <%-- handle method --%>
  <dspel:input type="hidden" priority="-10"
    bean="${repositoryAssetFormHandlerPath}.createTransient" 
    value="1" />
</dspel:form>


<%-- Handle create file assets --%>
<c:set var="fileAssetFormHandlerPath"
  value="/atg/epub/servlet/FileAssetFormHandler"/>
<dspel:importbean var="fileAssetFormHandler" 
  bean="${fileAssetFormHandlerPath}"/>

<%-- File Create Form Handler --%>
<dspel:form action="${createFormAction}" method="post" 
  formid="fileAssetCreateForm" name="fileAssetCreateForm">
  <dspel:input priority="100"
    bean="${fileAssetFormHandlerPath}.loggingDebug"
    value="${debug}"/>
  <dspel:input type="hidden" priority="100"
    bean="${fileAssetFormHandlerPath}.assetInfoPath"
    value="${assetInfoPath}"/>
  <%-- populate me please --%>
  <dspel:input type="hidden" priority="100" 
    id="fileContextOp" name="fileContextOp"
    bean="${fileAssetFormHandlerPath}.contextOp" 
    value="0"/>
  <dspel:input type="hidden" priority="10" 
    id="fileCreateType" name="fileCreateType"
    bean="${fileAssetFormHandlerPath}.encodedPathAndItemType"/>
  <dspel:input type="hidden" priority="10" 
    id="fileAssetDisplayName" name="fileAssetDisplayName"
    bean="${fileAssetFormHandlerPath}.displayName"/>
  <%-- handle method --%>
  <dspel:input type="hidden" priority="-10"
    bean="${fileAssetFormHandlerPath}.createTransient" 
    value="1" />
</dspel:form>

<script language="Javascript">
  function closeAfterCreate() {
    var createURL = '<c:out escapeXml="false" value="${assetBrowserCreateURL}"/>';

    if ( createURL )
      parent.window.location.href = createURL;
    else {
      refreshParentWindow();
    }
  }    

  var wndCmd = '<c:out value="${param.wndCmd}"/>';
  var hasRepositoryFormError =  '<dspel:valueof bean="/atg/epub/servlet/RepositoryAssetFormHandler.formError"/>';
  // Only close the window if the form worked ok.
  // Otherwise keep it open so the user can read the error message
  if ( wndCmd == "close" && hasRepositoryFormError == "false")
    closeAfterCreate();

  var typeCode = null;

  //
  // Create new asset
  //
  function createNewAsset( componentPath, itemType ) {

    // set the appropriate form handler depending if this is a repo/file asset
    var displayName = null;
    typeCode = null;

    for (var i=0; i<assetTypes.length; i++) {
      if ( assetTypes[i].componentPath == componentPath 
        && assetTypes[i].typeName == itemType ) {

        displayName = assetTypes[i].displayName;
        typeCode = assetTypes[i].typeCode;
      }
    }

    var encodedPathAndType = 
      componentPath
      + ":" 
      + itemType;

    var createForm = null;

    if ( typeCode == "repository" ) {
      // populate the repositoryCreateForm
      createForm = document.repositoryAssetCreateForm;

      // creating asset from projectAssets.page, start with new context
      createForm.repositoryContextOp.value = 
        '<c:out value="${assetInfo.CONTEXT_CLEAR}"/>';
      createForm.repositoryCreateType.value = 
        encodedPathAndType;
      createForm.repositoryAssetDisplayName.value = 
        displayName;
    }
    else if ( typeCode == "file" ) {
      // populate the fileCreateForm
      createForm = document.fileAssetCreateForm;

      // creating asset from projectAssets.page, start with new context
      createForm.fileContextOp.value = 
        '<c:out value="${assetInfo.CONTEXT_CLEAR}"/>';
      createForm.fileCreateType.value = 
        encodedPathAndType;
      createForm.fileAssetDisplayName.value = 
        displayName;
    }

    if ( '<c:out value="${assetInfo.attributes.createMode}" />' == 'nested' ) {
      // Creating an asset as a referenced asset.
      // Use save the form data of the parent before moving to the new asset.
      //   we move to the next page... Let the last callback of the chain be the function
      //   that takes the user to the next page.
      submitSaveParentAndCreate();
    }
    else {
      // creating an asset in project mode
      createForm.submit();
    }
  }

  //
  // Helper method to save parent asset and create child asset
  //
  function submitSaveParentAndCreate() {
    // call the callback to save the form contents before moving to the new asset
    if (true == parent.fsOnSubmit()) {
      parent.postFsOnSubmit = submitSaveParentFinal;
    }
    else {
      parent.postFsOnSubmit = null;
      // Wait a bit for the submit to complete...
      setTimeout( 'submitSaveParentFinal()', 1000);
    }
  }

  //
  // Helper method to save parent asset and create child asset
  //
  function submitSaveParentFinal() {
    var config = null;
    if ( parent[ 'getAssetBrowserConfig' ] != undefined )
      config = parent.getAssetBrowserConfig();

    var createForm = null;

    // creating asset while editing existing asset, push current context
    if ( typeCode == "repository" ) {
      createForm = document.repositoryAssetCreateForm;

      if ( config != null ) {
        createForm.repositoryContextOp.value = 
          config.contextOp;
      }
      else {
        createForm.repositoryContextOp.value = 
          '<c:out value="${assetInfo.CONTEXT_PUSH}"/>';
      }
    }
    else if ( typeCode == "file" ) {
      createForm = document.fileAssetCreateForm;

      if ( config != null ) {
        createForm.fileContextOp.value = 
          config.contextOp;
      }
      else {
        createForm.fileContextOp.value = 
          '<c:out value="${assetInfo.CONTEXT_PUSH}"/>';
      }
    }
    createForm.submit();
  }
</script>

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/createAsset.jsp#3 $$Change: 655984 $--%>

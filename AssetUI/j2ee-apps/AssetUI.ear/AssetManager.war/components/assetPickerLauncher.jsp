<%--
  assetPickerLauncher.jsp
  Fragment for launching the asset picker.

  @param launchPickerFunction  The name of the function to use for launching picker.
                               Should be unique on the page.

  @param isPickingParent       True if this is a parent picker.  False for a regular item link.

  @param repositoryPath        The nucleus path to the repository of interest. This is the repository
                               of the pickable type if isPickingParent is false. If isPickingParent
                               is true, then it is the repository of the child whose parent we want
                               to pick.
          
  @param repositoryName        The name of the repository
          
  @param assetType             The pickable type, if isPickingParent is false.  If isPickingParent
                               is true, then this is the type of the child whose parent we want to pick.

  @param assetTypeCode         The type code for the pickable type.  Default is "repository".  You can
                               also set "file" or other types.

  @param onSelectFunction      The name of the function to invoke when the user clicks OK on the picker.

  @param onSelectData          Any data that should be included during onSelect

  @param assetPickerTitle      Optional. The string to show at the top of the asset picker pop up.

  @param assetPickerHeader     Optional. The string to show just below the top of the asset picker pop up.

  @param allowMultiSelect      Optional. Default is false. Whether to allow multiselect in the picker.

--%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="allowMultiSelect"      param="allowMultiSelect"/>
  <dspel:getvalueof var="assetPickerHeader"     param="assetPickerHeader"/>
  <dspel:getvalueof var="assetPickerTitle"      param="assetPickerTitle"/>
  <dspel:getvalueof var="assetType"             param="assetType"/>  
  <dspel:getvalueof var="assetTypeCode"         param="assetTypeCode"/>
  <dspel:getvalueof var="isPickingParent"       param="isPickingParent"/>
  <dspel:getvalueof var="launchPickerFunction"  param="launchPickerFunction"/>
  <dspel:getvalueof var="onSelectData"          param="onSelectData"/>  
  <dspel:getvalueof var="onSelectFunction"      param="onSelectFunction"/>  
  <dspel:getvalueof var="repositoryName"        param="repositoryName"/>
  <dspel:getvalueof var="repositoryPath"        param="repositoryPath"/>


  <%-- DEBUG --%>
  <c:set var="debug" value="${false}"/>
  <c:if test="${debug}">
    isPickingParent = <c:out value="${isPickingParent}"/><br />
    launchPickerFunction = <c:out value="${launchPickerFunction}"/><br />
    repositoryPath = <c:out value="${repositoryPath}"/><br />
    repositoryName = <c:out value="${repositoryName}"/><br />
    assetType = <c:out value="${assetType}"/><br />
  </c:if>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:if test="${empty assetTypeCode}">
    <c:set var="assetTypeCode" value="repository"/>
  </c:if>

  <script type="text/javascript">
    
    function <c:out value="${launchPickerFunction}"/>() {
      var pickerLauncher = new atg.assetmanager.AssetPickerLauncher();
  
      // array to contain only those types to be pickable in browser
      // these pickable types will be the possible parent types for current item type
      var allowableTypes = new Array( 0 );
      var allowableTypesCommaList = "";


      <%-- get the tree for the asset type. --%>
      <web-ui:getTree var="treepath"
                      repository="${repositoryPath}"
                      itemType="${assetType}"
                      treeRegistry="${sessionInfo.treeRegistryPath}"/>

      <c:choose>
        <c:when test="${not empty treepath and isPickingParent}">
          <%-- in this case, the tree should be used to get the parent types for this type. --%>
          <web-ui:getPossibleParentTypes var="typelist"
                                         type="${assetType}"
                                         tree="${treepath}"/>
          // Put only a single type in the picker.assetTypes list so that we skip the screen
          // where you have to pick a repository and item type.  Put all the types in
          // allowableCommaTypesList because this will allow multiple pickable types.
          <c:forEach var="parenttype" items="${typelist}" varStatus="loop">
            <c:set var="index" value="${loop.index}"/>
            var assetType = new Object();
            assetType.typeCode       = '<c:out value="${assetTypeCode}"/>';
            assetType.displayName    = '<c:out value="${parenttype}"/>';
            assetType.typeName       = '<c:out value="${parenttype}"/>';
            assetType.repositoryName = '<c:out value="${repositoryName}"/>';
            assetType.componentPath  = '<c:out value="${repositoryPath}"/>';
            assetType.createable     = "false";
            <c:choose>
              <c:when test="${index == 0}">
                allowableTypes[ allowableTypes.length ] = assetType;
              </c:when>
              <c:otherwise>
                allowableTypesCommaList += ",";
              </c:otherwise>
            </c:choose>
            allowableTypesCommaList += '<c:out value="${parenttype}"/>';
          </c:forEach>
        </c:when>  <%-- not empty tree path and isPickingParent --%>
        <c:when test="${not isPickingParent}">
          <%-- in this case, we have a tree, but we are not picking a parent.  we are 
               picking an asset of the type specified. --%>
          var assetType = new Object();
          assetType.typeCode       = 'repository';
          assetType.displayName    = '<c:out value="${assetType}"/>';
          assetType.typeName       = '<c:out value="${assetType}"/>';
          assetType.repositoryName = '<c:out value="${repositoryName}"/>';
          assetType.componentPath  = '<c:out value="${repositoryPath}"/>';
          assetType.createable     = "true";
          allowableTypes[ 0 ] = assetType;
          allowableTypesCommaList += '<c:out value="${assetType}"/>';
        </c:when> <%-- not picking parent --%>
        <c:otherwise> 
           <%-- no tree, but trying to pick parent is the only other choice.  should never get here. --%>
        </c:otherwise> 
      </c:choose>

      <c:if test="${not empty treepath}">
        pickerLauncher.treeComponent          = "<c:out value='${treepath}'/>";
      </c:if>

      pickerLauncher.assetTypes             = allowableTypes;
      pickerLauncher.pickableTypesCommaList = allowableTypesCommaList;
      pickerLauncher.pickerURL              = '<c:out value="${config.assetPickerRoot}${config.assetPicker}"/>?apView=1&';
      <c:if test="${empty assetPickerTitle}">
        <fmt:message var="assetPickerTitle" key="propertyEditor.assetPickerTitle"/>
        <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
      </c:if>
      pickerLauncher.assetPickerTitle       = "<c:out value='${assetPickerTitle}'/>";
      pickerLauncher.onSelectFunction       = "<c:out value='${onSelectFunction}'/>";
      pickerLauncher.callerData             = "<c:out value='${onSelectData}'/>";
      pickerLauncher.assetPickerHeader      = "<c:out value='${assetPickerHeader}'/>";
      pickerLauncher.allowMultiSelect       = "<c:out value='${allowMultiSelect}'/>";

      pickerLauncher.invoke();
    }
  </script>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerLauncher.jsp#2 $$Change: 651448 $--%>

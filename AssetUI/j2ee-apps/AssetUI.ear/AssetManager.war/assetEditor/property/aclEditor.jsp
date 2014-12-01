<%--
  ACL editor panel.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/aclEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="profile"
                    bean="/atg/userprofiling/Profile"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <script type="text/javascript"
          src="<c:out value='${config.contextRoot}'/>/scripts/acl.js">
  </script>

  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="repositoryPath" value="${profile.repository.absoluteName}"/>

  <%-- Enable the editor controls if the user has writeACL rights and the
       asset is otherwise editable. --%>
  <asset-ui:getAssetAccess var="itemAccess" item="${requestScope.atgCurrentAsset.asset}"/>
  <c:set var="aclEditable" value="${itemAccess.writeAcl and requestScope.atgIsAssetEditable}"/>
  <c:choose>
    <c:when test="${aclEditable}">
      <c:set var="btnsClass"          value="buttonSmall"/>
      <c:set var="disableAssetPicker" value="return true"/>
      <c:set var="disabled"           value=""/>
    </c:when>
    <c:otherwise>
      <c:set var="btnsClass"          value="buttonSmall disabled"/>
      <c:set var="disableAssetPicker" value="return false"/>
      <c:set var="disabled"           value="disabled=true"/>
    </c:otherwise>
  </c:choose>

  <%-- Asset Picker for role --%>
  <dspel:include page="/components/assetPickerLauncher.jsp">
    <dspel:param name="assetType"            value="role"/>
    <dspel:param name="isPickingParent"      value="false"/>
    <dspel:param name="launchPickerFunction" value="assetPickerforRole"/>
    <dspel:param name="onSelectFunction"     value="onPrincipalsSelected"/>
    <dspel:param name="repositoryName"       value="${profile.repository.repositoryName}"/>
    <dspel:param name="repositoryPath"       value="${repositoryPath}"/>
    <dspel:param name="allowMultiSelect"     value="true"/>
    <dspel:param name="onSelectData"         value="role"/>
  </dspel:include>

  <%-- Asset Picker for organization --%>
  <dspel:include page="/components/assetPickerLauncher.jsp">
    <dspel:param name="assetType"            value="organization"/>
    <dspel:param name="isPickingParent"      value="false"/>
    <dspel:param name="launchPickerFunction" value="assetPickerforOrganization"/>
    <dspel:param name="onSelectFunction"     value="onPrincipalsSelected"/>
    <dspel:param name="repositoryName"       value="${profile.repository.repositoryName}"/>
    <dspel:param name="repositoryPath"       value="${repositoryPath}"/>
    <dspel:param name="allowMultiSelect"     value="true"/>
    <dspel:param name="onSelectData"         value="org"/>
  </dspel:include>

  <%-- Asset Picker for user --%>
  <dspel:include page="/components/assetPickerLauncher.jsp">
    <dspel:param name="assetType"            value="user"/>
    <dspel:param name="isPickingParent"      value="false"/>
    <dspel:param name="launchPickerFunction" value="assetPickerforUser"/>
    <dspel:param name="onSelectFunction"     value="onPrincipalsSelected"/>
    <dspel:param name="repositoryName"       value="${profile.repository.repositoryName}"/>
    <dspel:param name="repositoryPath"       value="${repositoryPath}"/>
    <dspel:param name="allowMultiSelect"     value="true"/>
    <dspel:param name="onSelectData"         value="user"/>
  </dspel:include>

  <script type="text/javascript">
    aclEditable = <c:out value="${aclEditable}"/>;
    var principal = "";

    aclEditorStrings.allowLabel         = "<fmt:message key='aclDropdown.allow'/>";
    aclEditorStrings.denyLabel          = "<fmt:message key='aclDropdown.deny'/>";
    aclEditorStrings.unspecifiedLabel   = "<fmt:message key='aclDropdown.unspecified'/>";
    aclEditorStrings.removePersonaTitle = "<fmt:message key='aclEditor.remove'/>";
    aclEditorStrings.accessRightTooltip = "<fmt:message key='aclEditor.accessRightTooltip'/>";

    /* Callback method for asset picker for principals
     *
     * @param  pSelected  An object containing info about the selected asset
     * @param  pData      Data specified by the code that invoked the picker
     *                    (namely, the type of principal selected)
     */
    function onPrincipalsSelected(selected, pData) {

      if(typeof selected == "object" && Object.prototype.toString.apply(selected) == "[object Array]") {

        var principal = pData;

        for(var i=0; i<selected.length;i++) {
          var uri = selected[i].uri;
          var roleName = uri.substring(uri.lastIndexOf("/")+1, uri.length);
          var persona = "Profile$"+ principal + "$" + roleName;
          var aclString = document.getElementById("aclText").value;
          var classN = getElementsByClassName(persona);
          var principalDisplay = principal + "Heading";
          document.getElementById(principalDisplay).style.display = "";

          // Renders the row on UI
          if( aclString.indexOf(persona) == -1 ) {
            if( classN == "" ) {
              addRowForPersona(principal, persona, selected[i].displayName);
            }
            else {
              showRow(persona);
            }
          }
        }
        markAssetModified();
      }
    }

    registerOnLoad(frameInit);

  </script>

  <div class="dataTableActions smallHeight">
    <span class="addNew">
      <a href="javascript:assetPickerforRole()"
         class="<c:out value='${btnsClass}'/>"
         onclick='<c:out value='${disableAssetPicker}'/>'
        ><fmt:message key="aclEditor.addRole"/></a>
      <a href="javascript:assetPickerforOrganization()"
         class="<c:out value='${btnsClass}'/>"
         onclick='<c:out value='${disableAssetPicker}'/>'
        ><fmt:message key="aclEditor.addOrganization"/></a>
      <a href="javascript:assetPickerforUser()"
         class="<c:out value='${btnsClass}'/>"
         onclick='<c:out value='${disableAssetPicker}'/>'
        ><fmt:message key="aclEditor.addUser"/></a>
    </span>
    <p>
      <input type="checkbox" class="defaultTrigger" id="default"
             onClick="hideACL()" <c:out value='${disabled}'/>
      /><fmt:message key="aclEditor.defaultSecurity"/>
    </p>
  </div>

  <div style="display:none">
    <dspel:input type="text" id="aclText" size="100"
                 converter="nullable"
                 bean="${requestScope.formHandlerPath}.itemAcl"/>

    <dspel:getvalueof var="values" bean="${requestScope.formHandlerPath}.itemAcl"/>

    <asset-ui:getPrincipalDisplayName var="strIdDispNameKeyValue" itemAcl="${values}"/>

    <input type="text" id="aclKeyValue" size="100" value="<c:out value='${strIdDispNameKeyValue}'/>"/>
  </div>

  <table class="data priceListSecurity">
    <thead>
      <tr>
        <th class="wideLabel">
          <fmt:message key="aclEditor.princ"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.list"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.read"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.write"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.delete"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.view"/><br/>
          <fmt:message key="aclEditor.owner"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.set"/><br/>
          <fmt:message key="aclEditor.owner"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.view"/><br/>
          <fmt:message key="aclEditor.acs"/><br/>
          <fmt:message key="aclEditor.rights"/>
        </th>
        <th class="center">
          <fmt:message key="aclEditor.set"/><br/>
          <fmt:message key="aclEditor.acs"/><br/>
          <fmt:message key="aclEditor.rights"/>
        </th>
        <th class="iconCell">
          &nbsp;
        </th>
      </tr>
    </thead>

    <%-- Eventually want to display the effective ACL here, for when no ACL is
         explicitly specified.
    <tbody class="default">
    </tbody>
    --%>

    <%-- Displays the role principals --%>
    <tbody>
      <tr id="roleHeading">
        <th colspan="10">
          <fmt:message key="aclEditor.roles"/>
          [
          <a href="#" class="priceSecurityTrigger" id="principalRolesId">
            <fmt:message key="aclEditor.hide"/>
          </a>
          ]
        </th>
      </tr>
    </tbody>
    <tbody id="role">
    </tbody>

    <%-- Displays the user principals --%>
    <tbody>
      <tr id="userHeading">
        <th colspan="10">
          <fmt:message key="aclEditor.users"/>
          [
          <a href="#" class="priceSecurityTrigger" id="principalUserId">
            <fmt:message key="aclEditor.hide"/>
          </a>
          ]
        </th>
      </tr>
    </tbody>
    <tbody id="user">
    </tbody>

    <%-- Displays the organization principals --%>
    <tbody>
      <tr id="orgHeading">
        <th colspan="10">
          <fmt:message key="aclEditor.organizations"/>
          [
          <a href="#" class="priceSecurityTrigger" id="principalOrgId">
            <fmt:message key="aclEditor.hide"/>
          </a>
          ]
        </th>
      </tr>
    </tbody>
    <tbody id="org">
    </tbody>

  </table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/aclEditor.jsp#2 $$Change: 651448 $--%>

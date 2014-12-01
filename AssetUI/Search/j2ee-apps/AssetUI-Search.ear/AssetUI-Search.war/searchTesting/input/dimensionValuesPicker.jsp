<%--
  Select Dimension Values Popup for Search Testing. 
  Allows to select dimension values to find search configuration, Used in selectSearchConfig.jsp.

  @param  formHandlerPath Nucleus path to the SearchTestingFormHandler

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/dimensionValuesPicker.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                 %>

<dspel:page>

  <dspel:getvalueof var="siteId" param="siteId"/>

  <dspel:getvalueof var="formHandlerPath" param="formHandlerPath" />
  <dspel:importbean var="formHandler" bean="${formHandlerPath}" />

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>

  <dspel:importbean bean="/atg/search/config/SearchDimensionManagerService" var="searchDimensionManagerService" />
  <dspel:importbean var="externalUserProfiles" bean="/atg/userprofiling/ProfileAdapterRepository"/>

  <dspel:importbean bean="/atg/userprofiling/PreviewProfileRequestProcessor" var="previewProfileRequestProcessor" />

  <script type="text/javascript">
    function selectDimensionValueOnClick() {
      var el = document.getElementById('tempSelectByDimensionValues');
      if (el)
        el.checked = true;
    }

    function doOnUserProfileSelect(elems) {
      document.getElementById('tempUserProfileID').value = elems.id;
      document.getElementById('tempUserProfileDisplayName').innerHTML = elems.displayName;
    }

    function showUserProfileSelectPopup() {
      var el = document.getElementById('tempSelectByUserProfile');
      if (el)
        el.checked = true;

      <c:set var="itemName" value="user" />
      <web-ui:getTree var="treepath"
                      repository="${externalUserProfiles.absoluteName}"
                      itemType="${itemName}"
                      treeRegistry="${sessionInfo.treeRegistryPath}"/>

      var requestUrl;
      var repositoryPath = '<c:out value="${externalUserProfiles.absoluteName}" />';
      var repositoryName = '<c:out value="${externalUserProfiles.repositoryName}" />';
      var itemType = '<c:out value="${itemName}" />';
      var treePath = '<c:out value="${treepath}" />';
      var pickerURL = '/AssetManager/assetPicker.jsp?apView=1';
      var allowMulti = 'false';

      var allowableTypes = new Array(0);
      var assetType = new Object();
      assetType.typeCode       = "repository";
      assetType.displayName    = "NotUsed";
      assetType.repositoryName = repositoryName;
      assetType.createable     = "false";
      assetType.typeName       = itemType;
      assetType.componentPath  = repositoryPath;
      allowableTypes[allowableTypes.length] = assetType;

      var picker = new AssetBrowser();
      picker.mapMode                 = "AssetManager.assetPicker";
      picker.clearAttributes         = "true";
      picker.pickerURL               = pickerURL + "&";
      picker.assetPickerTitle        = '<fmt:message key="searchTestingDimensionValuesPicker.selectItem.pickerTitle"/>';
      picker.browserMode             = "pick";
      picker.isAllowMultiSelect      = allowMulti;
      picker.createMode              = "none";
      picker.onSelect                = "doOnUserProfileSelect";
      picker.callerData              = requestUrl;
      picker.closeAction             = "hide";
      picker.assetTypes              = allowableTypes;
      picker.assetPickerParentWindow = top;
      picker.assetPickerFrameElement = top.document.getElementById("iFrame");
      picker.assetPickerDivId        = "browser";

      if (treePath) {
        picker.defaultView = "Browse";
        var viewConfiguration = new Array();
        viewConfiguration.treeComponent = treePath;
        viewConfiguration.itemTypes = itemType;
        picker.viewConfiguration = viewConfiguration;
      }
      else {
        picker.defaultView = "Search";
        picker.mapMode = "AssetManager.assetPicker.search";
      }

      picker.assetPickerParentWindow.checkedItems = new Array();

      picker.assetPickerParentWindow.currentPicker = picker;
      picker.encodeURLOptions();
      picker.assetPickerFrameElement.src = picker.encodeURLOptions();
      top.showIframe(picker.assetPickerDivId, picker.onSelect);
    }

    function dimensionValuesPickerOnClickOK() {
      var values = dimensionValuesPickerGetValues();
      if (values.testDimensionType == '<c:out value="${formHandler.selectByUserProfileValue}" />' && values.userProfileID == '') {
        alert('<fmt:message key="searchTestingDimensionValuesPicker.noPreviewUserProfileSelected"/>');
        top.assetManagerDialog.hide();
        return;
      }

      top.assetManagerDialog.hide();
      top.assetManagerDialog.execute(values);
    }

    function dimensionValuesPickerGetValues() {
      var values = {};

      var dimensionType = document.getElementById('tempDimensionTypeId');
      if (dimensionType) {
        values.testDimensionType = dimensionType.value;
      } else {
        var elems = document.getElementsByTagName('input');
        if (elems) {
          for (i = elems.length; --i >= 0; ) {
            if ('tempDimensionType' == elems[i].getAttribute('name') && elems[i].checked) {
              values.testDimensionType = elems[i].value;
              break;
            }
          }
        }
      }

      <c:forEach items="${searchDimensionManagerService.searchDimensions}" var="searchDimension">
        var sel = document.getElementById('<c:out value="${searchDimension.dimensionName}Select" />');
        if (sel)
          values.<c:out value="${searchDimension.dimensionName}Select" /> = sel.options[sel.selectedIndex].value;
      </c:forEach>

      var userProfileID = document.getElementById('tempUserProfileID');
      if (userProfileID)
        values.userProfileID = userProfileID.value;

      return values;
    }
  </script>

  <div id="dimValPicker" style="display: block; width: 500px">
    <p class="atg_subhead">
      <c:choose>
        <c:when test="${!empty previewProfileRequestProcessor}">
          <fmt:message key="searchTestingDimensionValuesPicker.description.text"/>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingDimensionValuesPicker.description2.text"/>
        </c:otherwise>
      </c:choose>
    </p>
    <ul class="atg_formVerticalList atg_selectionBullets atg_indent">
      <li>
        <label>
          <c:choose>
            <c:when test="${!empty previewProfileRequestProcessor}">
              <input type="radio"
                     id="tempSelectByDimensionValues"
                     name="tempDimensionType"
                     class="atg_radioBullet"
                     <c:if test="${formHandler.selectDimensionType ne formHandler.selectByUserProfileValue}">checked="checked"
                     </c:if>
                     value='<c:out value="${formHandler.selectByDimensionValuesValue}" />'/>
            </c:when>
            <c:otherwise>
              <input type="hidden"
                     id="tempDimensionTypeId"
                     value='<c:out value="${formHandler.selectByDimensionValuesValue}" />'/>
            </c:otherwise>
          </c:choose>
          <fmt:message key="searchTestingDimensionValuesPicker.enterDimension.radio"/>
        </label>
        <dl class="atg_smerch_configList atg_nestedValues">
          <c:forEach items="${searchDimensionManagerService.searchDimensions}" var="searchDimension">
            <dt>
              <fmt:message key="searchTestingDimensionValuesPicker.dimension.text">
                <fmt:param>
                  <c:out value="${searchDimension.displayName}"/>
                </fmt:param>
              </fmt:message>
            </dt>
            <dd>
              <c:choose>
                <c:when test="${not empty siteId and 'site' eq searchDimension.dimensionName}">
                  <select id='<c:out value="${searchDimension.dimensionName}Select" />' style="display: none">
                    <option selected="selected" value="<c:out value='${siteId}' />"></option>
                  </select>
                  <dspel:getvalueof bean="${searchDimension.absoluteName}.siteMap.${siteId}.name" var="dimensionValue" />
                  <c:out value="${dimensionValue}" />
                </c:when>
                <c:otherwise>
                  <dspel:getvalueof bean="${formHandlerPath}.dimensionValues.${searchDimension.dimensionName}" var="dimensionValue" />
                  <select id='<c:out value="${searchDimension.dimensionName}Select" />' onclick="selectDimensionValueOnClick(); return true;">
                    <option selected="selected" value=""><fmt:message key="searchTestingDimensionValuesPicker.all.others.option"/></option>
                    <c:choose>
                      <c:when test="${searchDimensionManagerService.languageDimensionService eq searchDimension}">
                        <c:forEach items="${searchDimension.languageMap}" var="entry">
                          <option
                              <c:if test="${dimensionValue eq entry.key}">selected="selected"
                              </c:if>
                              value='<c:out value="${entry.key}" />'><assetui-search:getLocaleName locale="${entry.value}" /></option>
                        </c:forEach>
                      </c:when>
                      <c:otherwise>
                        <c:forEach items="${searchDimension.dimensionValues}" var="entry">
                          <option
                              <c:if test="${dimensionValue eq entry}">selected="selected"
                              </c:if>
                              value='<c:out value="${entry}" />'><c:out value="${entry}" /></option>
                        </c:forEach>
                      </c:otherwise>
                    </c:choose>
                  </select>
                </c:otherwise>
              </c:choose>
            </dd>
          </c:forEach>
        </dl>
      </li>
      <c:if test="${!empty previewProfileRequestProcessor}">
        <li>
          <label>
            <input type="radio"
                   id="tempSelectByUserProfile"
                   name="tempDimensionType"
                   class="atg_radioBullet"
                   <c:if test="${formHandler.selectDimensionType eq formHandler.selectByUserProfileValue}">checked="checked"
                   </c:if>
                   value='<c:out value="${formHandler.selectByUserProfileValue}" />'/>
            <fmt:message key="searchTestingDimensionValuesPicker.useDimension.radio"/>
          </label>
          <dl class="atg_smerch_configList">
            <dt><fmt:message key="searchTestingDimensionValuesPicker.previewProfile.text"/></dt>
            <dd>
              <dspel:getvalueof bean="${formHandlerPath}.value.previewUserId" var="tempPreviewUserId" />
              <input type="hidden" id="tempUserProfileID" value="<c:out value="${tempPreviewUserId}" />" />
              <span id="tempUserProfileDisplayName"><c:out value="${formHandler.previewUserName}" /></span>
              <input type="button" value="<fmt:message key="searchTestingDimensionValuesPicker.select.link"/>" onclick="showUserProfileSelectPopup(); return false;" />
            </dd>
          </dl>
        </li>
      </c:if>
    </ul>
    <div class="atg_formActionPop">
      <input type="submit" value="<fmt:message key="searchTestingDimensionValuesPicker.ok.button"/>" onclick="dimensionValuesPickerOnClickOK(); return false;" />
      <input type="button" value="<fmt:message key="searchTestingDimensionValuesPicker.cancel.button"/>" onclick="top.assetManagerDialog.hide(); return false;" />
    </div>
  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/dimensionValuesPicker.jsp#2 $$Change: 651448 $--%>

<%--
  This page represents a picker for the Search Testing Environments. Used in inputs.jsp

  @param  formHandlerPath Nucleus path to the SearchTestingFormHandler

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/environmentPicker.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="pws" uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="formHandlerPath" param="formHandlerPath" />
  <dspel:importbean var="formHandler" bean="${formHandlerPath}" />

  <c:set var="testEnvironmentModel" value="${formHandler.testEnvironmentModel}" />

  <dspel:importbean var="multisiteService" bean="/atg/search/routing/MultisiteService"/>

  <script type="text/javascript">
    function environmentPickerGetValues() {
      var values = {};

      var contentLabel = document.getElementById('searchTestingEnvironmentContentLabelDropdown');
      if (contentLabel)
        values.contentLabel = contentLabel.value;

      var targetType = document.getElementById('searchTestingEnvironmentTargetTypeDropdown');
      if (targetType)
        values.targetType = targetType.value;

      var checkbox = document.getElementById('searchTestingEnvironmentTestChangeCheckbox');
      if (checkbox)
        values.testingMode = checkbox.checked ? 'true' : 'false';

      return values;
    }
  </script>

  <div id="srchEnvPicker" style="display: block; width: 500px">
    <p class="atg_subhead"><fmt:message key="searchTestingEnvironmentPicker.selectEnvironment.text"/></p>
    <ul class="atg_formVerticalList atg_selectionBullets atg_indent">
      <li>
        <label>
          <fmt:message key="searchTestingEnvironmentPicker.contentLabel.text" />
          <select id="searchTestingEnvironmentContentLabelDropdown">
            <c:forEach var="contentLabel" items="${multisiteService.contentLabels}">
              <option
                value="<c:out value='${contentLabel}' />"
                <c:if test="${testEnvironmentModel.contentLabel eq contentLabel}">
                  selected="selected"
                </c:if>
                ><c:out value="${contentLabel}" /></option>
            </c:forEach>
          </select>
        </label>
      </li>
      <li>
        <label>
          <fmt:message key="searchTestingEnvironmentPicker.targetType.text" />
          <select id="searchTestingEnvironmentTargetTypeDropdown">
            <c:forEach var="targetType" items="${multisiteService.targetTypes}">
              <option
                value="<c:out value='${targetType}' />"
                <c:if test="${testEnvironmentModel.targetType eq targetType}">
                  selected="selected"
                </c:if>
                ><c:out value="${targetType}" /></option>
            </c:forEach>
          </select>
        </label>
      </li>
    </ul>
    <ul class="atg_formVerticalList atg_selectionBullets atg_indent">
      <c:choose>
        <c:when test="${not formHandler.projectActive}">
          <li class="atg_disableSection">
        </c:when>
        <c:otherwise>
          <li>
        </c:otherwise>
      </c:choose>
        <label>
          <input 
            type="checkbox"
            id="searchTestingEnvironmentTestChangeCheckbox"
            <c:if test="${not formHandler.projectActive}">
              disabled="disabled"
            </c:if>
            <c:if test="${testEnvironmentModel.testingMode}">
              checked="checked"
            </c:if>
            /> <fmt:message key="searchTestingEnvironmentPicker.testUndeployed.checkbox"/>
        </label>
        <div class="srchEnvPicker_comment">
          <fmt:message key="searchTestingEnvironmentPicker.testUndeployed.comment"/>
        </div>
      </li>
    </ul>
    <div class="atg_formActionPop">
      <input type="submit" value="<fmt:message key="searchTestingEnvironmentPicker.ok.button"/>" onclick="top.assetManagerDialog.hide(); top.assetManagerDialog.execute(environmentPickerGetValues()); return false;" />
      <input type="button" value="<fmt:message key="searchTestingEnvironmentPicker.cancel.button"/>" onclick="top.assetManagerDialog.hide(); return false;" />
    </div>
  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/environmentPicker.jsp#2 $$Change: 651448 $--%>

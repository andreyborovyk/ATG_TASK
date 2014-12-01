<%--
  Asset diff table for asset manager UI.
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/diff/propertyDiffs.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"           %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramFormHandler"             param="formHandler"/>
  <dspel:getvalueof var="paramFormHandlerPath"         param="formHandlerPath"/>
  <dspel:getvalueof var="paramLocalItemViewMapping"    param="localItemViewMapping"/>
  <dspel:getvalueof var="paramOriginalItemViewMapping" param="originalItemViewMapping"/>
  
  <fmt:setBundle basename="${requestScope.managerConfig.resourceBundle}"/>

  <%-- set revertAllowed to true if the asset is editable and revert is not a forbidden operation --%>
  <c:set var="revertAllowed" value="${false}"/>
  <c:if test="${requestScope.atgIsAssetEditable}">
    <asset-ui:isOperationAllowed
      operation="revert"
      assetURI="${paramFormHandler.assetURI}">
      <c:set var="revertAllowed" value="${true}"/>
    </asset-ui:isOperationAllowed>
  </c:if>

  <table class="mergeTable">
    <caption><fmt:message key="propertyDiffs.mergeTable"/></caption>
    <tr>
      <c:if test="${revertAllowed}">
        <td class="checkBoxCell">
          <%--
          <div class="checkBoxIcon">
          </div>
          --%>
        </td>
      </c:if>
      <td class="propertyHeader">
        <fmt:message key="commonDiff.property"/>
      </td>
      <td class="mergeHeader">
        <fmt:message key="commonDiff.originalVersion"/>
        (<c:out value="${paramFormHandler.originalAssetVersion.version}"/>)
      </td>
      <td class="mergeHeader">
        <fmt:message key="commonDiff.yourVersion"/>
        (<c:out value="${paramFormHandler.localAssetVersion.version}"/>)
      </td>
    </tr>
    
    <c:forEach var="propertyMapping" items="${paramOriginalItemViewMapping.propertyMappings}">
      <tr>
        <c:set scope="request" var="mpv" value="${propertyMapping.value}"/>
        <c:if test="${requestScope.mpv.mapped}">
          <c:set var="propertyName" value="${requestScope.mpv.propertyName}"/>
          <c:if test="${revertAllowed}">
            <td class="checkBoxCell">
              <dspel:input type="checkbox"
                           bean="${paramFormHandlerPath}.selectedPropertiesForRevert"
                           value="${propertyName}"/>
            </td>
          </c:if>
          <td class="property">
            <c:out value="${requestScope.mpv.propertyDescriptor.displayName}"/>
          </td>
          <td class="valueCell">
            <c:if test="${not empty requestScope.mpv.uri}">
              <dspel:include otherContext="${requestScope.mpv.contextRoot}" page="${requestScope.mpv.uri}"/>
            </c:if>
          </td>
          <td class="valueCell">
            <c:set scope="request" var="mpv"
                   value="${paramLocalItemViewMapping.propertyMappings[propertyName]}"/>
            <c:if test="${not empty requestScope.mpv.uri}">
              <dspel:include otherContext="${requestScope.mpv.contextRoot}" page="${requestScope.mpv.uri}"/>
            </c:if>
          </td>
        </c:if>
      </tr>
    </c:forEach>
    <c:if test="${revertAllowed}">
      <tr>
        <td colspan="4" class="controls">
          <a href="javascript:document.diffForm.revert.click()" class="buttonSmall" title="<fmt:message key='propertyDiffs.revert.title'/>">
            <span>
              <fmt:message key="propertyDiffs.revert"/>
            </span>
          </a>
        </td>
      </tr>
    </c:if>
  </table>
  <br/>

  <dspel:input id="revert"
               type="submit"
               style="display:none"
               bean="${paramFormHandlerPath}.revert"/>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/diff/propertyDiffs.jsp#2 $$Change: 651448 $ --%>

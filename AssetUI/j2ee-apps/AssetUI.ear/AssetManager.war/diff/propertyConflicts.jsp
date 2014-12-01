<%--
  Asset conflict table for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/diff/propertyConflicts.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramFormHandler"             param="formHandler"/>
  <dspel:getvalueof var="paramFormHandlerPath"         param="formHandlerPath"/>
  <dspel:getvalueof var="paramHeadItemViewMapping"     param="headItemViewMapping"/>
  <dspel:getvalueof var="paramLocalItemViewMapping"    param="localItemViewMapping"/>
  <dspel:getvalueof var="paramOriginalItemViewMapping" param="originalItemViewMapping"/>
  <dspel:getvalueof var="paramSuggestItemViewMapping"  param="suggestItemViewMapping"/>

  <fmt:setBundle basename="${requestScope.managerConfig.resourceBundle}"/>
  
  <c:set var="numColumns" value="0"/>

  <table class="mergeTable">
    <caption><fmt:message key="propertyConflicts.conflictsTable"/></caption>
    <tr>
      <c:set var="numColumns" value="${numColumns + 1}"/>
      <td class="propertyHeader">
        <fmt:message key="commonDiff.property"/>
      </td>
      <c:choose>
        <c:when test="${requestScope.atgIsAssetEditable}">
          <c:set var="numColumns" value="${numColumns + 6}"/>
          <td class="mergeHeader" colspan="2">
            <fmt:message key="commonDiff.originalVersion"/>
            (<c:out value="${paramFormHandler.originalAssetVersion.version}"/>)
          </td>
          <td class="mergeHeader" colspan="2">
            <fmt:message key="propertyConflicts.theirVersion"/>
            (<c:out value="${paramFormHandler.headAssetVersion.version}"/>)
          </td>
          <td class="mergeHeader" colspan="2">
            <fmt:message key="commonDiff.yourVersion"/>
            (<c:out value="${paramFormHandler.localAssetVersion.version}"/>)
          </td>
          <c:if test="${paramSuggestItemViewMapping ne null}">
            <c:set var="numColumns" value="${numColumns + 2}"/>
            <td class="mergeHeader" colspan="2">
              <fmt:message key="commonDiff.suggestedVersion"/>
            </td>
          </c:if>
        </c:when>
        <c:otherwise>
          <c:set var="numColumns" value="${numColumns + 3}"/>
          <td class="mergeHeader">
            <fmt:message key="commonDiff.originalVersion"/>
            (<c:out value="${paramFormHandler.originalAssetVersion.version}"/>)
          </td>
          <td class="mergeHeader">
            <fmt:message key="propertyConflicts.theirVersion"/>
            (<c:out value="${paramFormHandler.headAssetVersion.version}"/>)
          </td>
          <td class="mergeHeader">
            <fmt:message key="commonDiff.yourVersion"/>
            (<c:out value="${paramFormHandler.localAssetVersion.version}"/>)
          </td>
        </c:otherwise>
      </c:choose>
    </tr>

    <c:forEach var="propertyMapping" 
               items="${paramOriginalItemViewMapping.propertyMappings}"
               varStatus="status">
      <tr>
        <c:set scope="request" var="mpv" value="${propertyMapping.value}"/>
        <c:if test="${requestScope.mpv.mapped}">
          <c:set var="propertyName" value="${requestScope.mpv.propertyName}"/>
          <td class="property">
            <c:out value="${requestScope.mpv.propertyDescriptor.displayName}"/>
          </td>
          <c:if test="${requestScope.atgIsAssetEditable}">
            <td class="checkBoxCell">
              <dspel:input bean="${paramFormHandlerPath}.selectedVersionsForMerge.${propertyName}"
                           type="radio" value="original"/>
            </td>
          </c:if>
          <td class="valueCell">
            <c:if test="${not empty requestScope.mpv.uri}">
              <dspel:include otherContext="${requestScope.mpv.contextRoot}" page="${requestScope.mpv.uri}"/>
            </c:if>
          </td>
          <c:if test="${requestScope.atgIsAssetEditable}">
            <td class="checkBoxCell">
              <dspel:input bean="${paramFormHandlerPath}.selectedVersionsForMerge.${propertyName}"
                           type="radio" value="head"/>
            </td>
          </c:if>
          <td class="valueCell">
            <c:set scope="request" var="mpv"
                   value="${paramHeadItemViewMapping.propertyMappings[propertyName]}"/>
            <c:if test="${not empty requestScope.mpv.uri}">
              <dspel:include otherContext="${requestScope.mpv.contextRoot}" page="${requestScope.mpv.uri}"/>
            </c:if>
          </td>
          <c:if test="${requestScope.atgIsAssetEditable}">
            <td class="checkBoxCell">
              <dspel:input bean="${paramFormHandlerPath}.selectedVersionsForMerge.${propertyName}"
                           type="radio" value="local"/>
            </td>
          </c:if>
          <td class="valueCell">
            <c:set scope="request" var="mpv"
                   value="${paramLocalItemViewMapping.propertyMappings[propertyName]}"/>
            <c:if test="${not empty requestScope.mpv.uri}">
              <dspel:include otherContext="${requestScope.mpv.contextRoot}" page="${requestScope.mpv.uri}"/>
            </c:if>
          </td>

          <c:if test="${requestScope.atgIsAssetEditable && paramSuggestItemViewMapping ne null}">
            <c:choose>
              <c:when test="${paramSuggestItemViewMapping.propertyMappings[propertyName] ne null}">
                <td class="checkBoxCell">
                  <dspel:input bean="${paramFormHandlerPath}.selectedVersionsForMerge.${propertyName}"
                               type="radio" value="suggestion"/>
                </td>

                <td class="valueCell">
                  <c:set scope="request" var="mpv"
                         value="${paramSuggestItemViewMapping.propertyMappings[propertyName]}"/>
                  <c:if test="${not empty requestScope.mpv.uri}">
                    <dspel:include otherContext="${requestScope.mpv.contextRoot}" page="${requestScope.mpv.uri}">
                      <dspel:param name="formHandler" value="${paramFormHandler}"/>
                    </dspel:include>
                  </c:if>
                </td>
              </c:when>
              <c:otherwise>
                <td colspan="2"></td>
              </c:otherwise>
            </c:choose>
          </c:if>

        </c:if>
      </tr>

      <c:if test="${requestScope.atgIsAssetEditable && status.last}">
        <dspel:input type="hidden"
                     bean="${paramFormHandlerPath}.propertyConflictCount"
                     value="${status.count}"/>
      </c:if>
    </c:forEach>

    <c:if test="${requestScope.atgIsAssetEditable}">
      <tr>
        <td colspan="<c:out value='${numColumns}'/>" class="controls">
          <a href="javascript:document.diffForm.merge.click()" class="buttonSmall" title="<fmt:message key='propertyConflicts.merge.title'/>">
            <span>
              <fmt:message key="propertyConflicts.merge"/>
            </span>
          </a>
        </td>
      </tr>
    </c:if>
  </table>
  <br/>

  <dspel:input id="merge"
               type="submit"
               style="display:none"
               bean="${paramFormHandlerPath}.merge"/>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/diff/propertyConflicts.jsp#2 $$Change: 651448 $ --%>

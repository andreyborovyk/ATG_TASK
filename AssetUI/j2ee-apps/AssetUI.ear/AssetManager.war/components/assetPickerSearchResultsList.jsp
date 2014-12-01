<%--
  This page is based on components/list.jsp.  Its functionality should
  eventually be merged into that page.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchResultsList.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <c:set var="debug" value="false"/>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="listPageNumber" param="listPageNumber"/>
  <dspel:getvalueof var="allowMultiSelect" param="allowMultiSelect"/>

  <dspel:importbean var="preferences"
                    bean="/atg/web/assetmanager/Preferences"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <c:set var="assetBrowserPath"
         value="/atg/web/assetmanager/list/AssetPickerSearchAssetBrowser"/>
  <dspel:importbean var="assetBrowser"
                    bean="${assetBrowserPath}"/>

  <%-- If requested, update the page number in the asset browser --%>
  <c:if test="${not empty listPageNumber}">
    <c:set target="${assetBrowser}" property="currentPageIndex" value="${listPageNumber - 1}"/>
  </c:if>

  <c:set var="inputType" value="radio"/>
  <c:if test ="${allowMultiSelect}">
    <c:set var="inputType" value="checkbox"/>
  </c:if>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title></title>
      <dspel:include page="/components/head.jsp"/>
    </head>

    <body style="background: #fff" onload="parent.atg.assetpicker.checkboxes.initializeCheckboxes('resultSetItemCheckbox')">

      <c:set var="assets" value="${assetBrowser.currentPageAssets}"/>
      <c:choose>
        <c:when test="${not empty assets}">
          <table id="assetListContentTable" cellpadding="0" cellspacing="0">

            <%-- Loop through all of the assets on the current page --%>
            <c:forEach var="assetWrapper" items="${assets}">

              <c:set var="assetURI" value="${assetWrapper.uri}"/>
              <c:set var="itemId" value="${assetWrapper.id}"/>
              <c:set var="itemDisplayName" value="${assetWrapper.displayName}"/>
              <%
                String s = (String) pageContext.getAttribute("assetURI");
                s = s.replaceAll("'", "\\\\'");
                s = s.replaceAll("\"", "\\\\\"");
                pageContext.setAttribute("assetURI", s);
                s = (String) pageContext.getAttribute("itemId");
                s = s.replaceAll("'", "\\\\'");
                s = s.replaceAll("\"", "\\\\\"");
                pageContext.setAttribute("itemId", s);
                s = (String) pageContext.getAttribute("itemDisplayName");
                s = s.replaceAll("'", "\\\\'");
                s = s.replaceAll("\"", "\\\\\"");
                pageContext.setAttribute("itemDisplayName", s);
              %>
              <c:set var="checkboxFunction" value="parent.atg.assetpicker.checkboxes.checkItem('${assetURI}', '${itemId}', '${itemDisplayName}')"/>

              <tr>
                <td class="checkboxCell">
                  <input id="checkbox_<c:out value='${assetURI}'/>"
                         type="<c:out value='${inputType}'/>" name="resultSetItemCheckbox"
                         onclick="<c:out value='${checkboxFunction}' escapeXml='true'/>"/>
                </td>

                <%-- Find any custom styles that need to be applied to this table cell --%>
                <web-ui:invoke var="customStyle"
                               componentPath="/atg/web/service/CustomStyleFinder"
                               method="getStyle">
                  <web-ui:parameter value="${assetWrapper}"/>
                  <web-ui:parameter value="StyleFinder_listCellClass"/>
                </web-ui:invoke>

                <td class="nameCell <c:out value='${customStyle}'/>"
                    id="<c:out value='nameCell_${assetURI}'/>">

                  <%-- Determine the asset icon --%>
                  <web-ui:getIcon var="iconUrl"
                                  container="${assetWrapper.containerPath}"
                                  type="${assetWrapper.type}"
                                  assetFamily="RepositoryItem"/>

                  <c:set var="tooltip" value=""/>
                  <c:if test="${preferences.showIdToolTips}">
                    <c:set var="tooltip" value="${assetWrapper.id}"/>
                  </c:if>
                  <a id="<c:out value='nameLink_${assetURI}'/>"
                     title="<c:out value='${tooltip}'/>">
                    <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>
                    <c:out value="${assetWrapper.displayName}"/>
                  </a>
                </td>
              </tr>
            </c:forEach>
          </table>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchResults.noMatch"/>
        </c:otherwise>
      </c:choose>
    </body>
  </html>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchResultsList.jsp#2 $$Change: 651448 $ --%>

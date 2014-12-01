<%--
  This page is launched from the linked property editors.  If there are sub-types
  of the item descriptor, the user must select one before creating the asset.

  This page is also used for specifying a key for a linked map property.

  If subtype or key aren't necessary, go directly to creating the asset.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/subTypeSelection.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"    uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


  <pws:getItemSubTypes var="subtypes"
    repositoryPath="${param.repositoryPathName}"
    itemType="${param.itemDescriptorName}"/>

  <dspel:test var="subTypesInfo" value="${subtypes}"/>

  <c:url var="createTransientURL" value="/assetEditor/createTransientAsset.jsp">
    <c:param name="containerPath" value="${param.repositoryPathName}"/>
    <c:choose>
      <c:when test="${subTypesInfo.size eq 1}">
        <c:param name="assetType" value="${subtypes[0].itemDescriptorName}"/>
      </c:when>
      <c:otherwise>
        <c:param name="assetType" value="${param.itemDescriptorName}"/>
      </c:otherwise>
    </c:choose>
    <c:param name="linkPropertyName" value="${param.linkPropertyName}"/>
    <c:param name="linkPropertyIndex" value="${param.linkPropertyIndex}"/>
    <c:param name="createErrorURL" value="${param.cancelSubTypeURL}"/>
    <c:param name="pushContext" value="true"/>
  </c:url>

  <c:set var="renderPage" value="${subTypesInfo.size > 1 || param.requireMapKey == 'true'}"/>

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title></title>

      <dspel:include page="/components/head.jsp"/>

      <script type="text/javascript">

        // This page features a header, a scrolling content panel,
        // and a footer.  To ensure that the footer appears at the bottom of
        // the panel when we are in an iframe, we have to position it after the
        // page has loaded.

        function layoutComponentsOrRedirect() {
        <c:choose>

          <c:when test="${renderPage}">
            var iframe       = parent.document.getElementById("rightPaneIframe");
            var header       = document.getElementById("rightPaneHeader");
            var contentPanel = document.getElementById("rightPaneContent");
            var footer       = document.getElementById("rightPaneScrollFooter");
            if (iframe && header && contentPanel && footer) {
              contentPanel.style.height = iframe.offsetHeight
                                        - footer.offsetHeight
                                        - header.offsetHeight + "px";
              footer.style.position = "absolute";
              footer.style.top = contentPanel.offsetTop
                               + contentPanel.offsetHeight
                               + "px";
            }
          </c:when>

          <c:otherwise>
            var redirectURL = "<c:out value='${createTransientURL}' escapeXml='false'/>";
            document.location = redirectURL;
          </c:otherwise>

        </c:choose>
        }

      </script>
    </head>

    <body id="framePage" onload="layoutComponentsOrRedirect()">

      <%-- If there is only one type, go directly to creating the asset. --%>

      <script type="text/javascript">

        function nextButtonClicked() {

          // Get the currently selected asset type.
          // Its create URL is stored in the select menu

          var selectButton = document.getElementById("subTypeSelect");
          var propPage;
          if (selectButton != null) {
            var selIndex = selectButton.selectedIndex;
            propPage = selectButton.options[selIndex].value;
          }
          else {
            propPage = "<c:out value='${createTransientURL}' escapeXml='false'/>";
          }

          var keyInputField = document.getElementById("mapKey");
          if (keyInputField != null) {
            var keystring = keyInputField.value;
            if (keystring == "") {
              alert("<fmt:message key='subTypeSelection.keyRequired'/>");
              return;
            }
            propPage = propPage + "&linkPropertyKey=" + keyInputField.value;
          }

          var thisframe =  parent.document.getElementById("rightPaneIframe");

          if (thisframe != null) thisframe.src = propPage;

        }

      </script>

    <c:if test="${renderPage}">

      <div id="rightPaneHeader">
        <div id="rightPaneHeaderRight">
        </div>
        <div id="rightPaneHeaderLeft">
        </div>
      </div>
      <%-- <div id="topFade"> Block this, since it makes the top of the page unclickable
      </div> --%>
      <div id="rightPaneContent">
        <h3 class="contentHeader">
          <fmt:message key="subTypeSelection.header">
            <fmt:param value="${param.itemDescriptorName}"/>
          </fmt:message>
        </h3>
        <table class="formTable2">
          <c:if test="${subTypesInfo.size != 1}">
            <tr>
              <td class="formLabel2">
                <fmt:message key="subTypeSelection.selectSubType"/>
              </td>
              <td>
                <select class="propertyEditSelect" id="subTypeSelect">

                  <c:forEach var="subtype" items="${subtypes}">

                    <c:url var="createURL" value="/assetEditor/createTransientAsset.jsp">
                      <c:param name="containerPath" value="${param.repositoryPathName}"/>
                      <c:param name="assetType" value="${subtype.itemDescriptorName}"/>
                      <c:param name="linkPropertyName" value="${param.linkPropertyName}"/>
                      <c:param name="linkPropertyIndex" value="${param.linkPropertyIndex}"/>
                      <c:param name="pushContext" value="true"/>
                    </c:url>

                    <web-ui:getItemDescriptorDisplayName var="displayName" itemDescriptor="${subtype}"/>
                    <option value="<c:out value='${createURL}'/>"><c:out value="${displayName}"/></option>

                  </c:forEach>
                </select>

              </td>
            </tr>
          </c:if>
          <c:if test="${param.requireMapKey == 'true'}">
            <tr>
              <td class="formLabel2">
                <fmt:message key="subTypeSelection.key"/>
              </td>
              <td>
                <input id="mapKey"/>
              </td>
            </tr>
          </c:if>
        </table>
      </div>

      <table id="rightPaneScrollFooter">
        <tr>
          <td id="rightPaneFooter">
            <div>
              <div id="rightPaneFooterRight">

                <a href="javascript:nextButtonClicked()"
                  class="button" title="<fmt:message key='common.next.title'/>">
                  <span><fmt:message key="common.next"/></span></a>
                <a href="<c:out value='${param.cancelSubTypeURL}' escapeXml='false'/>"
                   class="button" title="<fmt:message key='common.cancel.title'/>">
                  <span><fmt:message key="common.cancel"/></span></a>
              </div>
              <div id="rightPaneFooterLeft">
              </div>
            </div>
          </td>
        </tr>
      </table>

    </c:if>

    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/subTypeSelection.jsp#2 $$Change: 651448 $--%>

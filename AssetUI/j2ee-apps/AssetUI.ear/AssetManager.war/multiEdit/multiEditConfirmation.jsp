<%--
  Confirmation page after a multi edit operation (ApplyToAll, List, Transformation)

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/multiEditConfirmation.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="multiEditSessionInfo" value="${sessionInfo.multiEditSessionInfo}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set scope="request" var="managerConfig" value="${sessionInfo.taskConfiguration}"/>
  <fmt:setBundle basename="${managerConfig.editorConfiguration.resourceBundle}"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title></title>

      <dspel:include page="/components/head.jsp"/>

    </head>

    <body id="framePage" onload="layoutAssetEditor()" onresize="layoutAssetEditor()" class="<c:out value='${config.dojoThemeClass}'/>">


      <%-- Right Pane Header : Includes right content header --%>
      <div id="rightPaneHeader">
        <div id="rightPaneHeaderRight"></div>
        <div id="rightPaneHeaderLeft">
          <c:set var="assetCount" value="${multiEditSessionInfo.totalAssetCountEstimate}"/>
          <fmt:message key="assetEditor.statusBar.currentlyEditing"/>
          <fmt:message key="assetEditor.statusBar.nAssets">
            <fmt:param value="${assetCount}"/>
          </fmt:message>
        </div>
      </div>

      <%-- Right pane fade-out effect --%>
      <%-- <div id="topFade"></div> Block this, it makes the top of the page unclickable --%>

      <%-- Right Pane Properties Content : Includes right pane properties content --%>
      <div id="subNav"></div>
      <div id="rightPaneContent">

        <%--
        <p id="toggleDetails"><a href="#">&nbsp;</a></p>
        --%>

        <h3 class="contentHeader"><fmt:message key="multiEditConfirmation.header"/></h3>

        <%--
        <div class="positionBox">
          <div class="noBox" id="box">
            <h3>This is The Title of the Toggled Box</h3>
            <p>Lorem ipsum dolor sit amet...</p>
            <div><a class="more" href="#" id="closePageInfo">Close More Details</a></div>
          </div>
        </div>
        --%>

        
        <div class="multiEditConfirm">

        <form action="#">


          <c:if test="${multiEditSessionInfo.multiEditStoppedCount > 0}">
            <fieldset>
              <legend><fmt:message key="multiEditConfirmation.stopped.header"/></legend>
              <p class="fieldsetIntro">
                <fmt:message  key="multiEditConfirmation.stopped.intro">
                  <fmt:param value="${multiEditSessionInfo.multiEditStoppedCount}"/>
                </fmt:message>
              </p>
              <%-- Use a grid widget to display the property. --%>
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/assetEditor/grid/itemComponentEditor.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="numElements" value="${multiEditSessionInfo.multiEditStoppedCount}"/>
                <dspel:param name="uniqueId" value="multiEditStoppedId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditStoppedList"/>
                <dspel:param name="drilldownFunction" value="editAsset"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </fieldset>
          </c:if>

          <fieldset>
            <legend><fmt:message key="multiEditConfirmation.successful.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message  key="multiEditConfirmation.successful.intro">
                <fmt:param value="${multiEditSessionInfo.multiEditSuccessCount}"/>
              </fmt:message>
             </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${multiEditSessionInfo.multiEditSuccessCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/assetEditor/grid/itemComponentEditor.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="numElements" value="${multiEditSessionInfo.multiEditSuccessCount}"/>
                <dspel:param name="uniqueId" value="multiEditSuccessId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditSuccessList"/>
                <dspel:param name="drilldownFunction" value="editAsset"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </c:if>
          </fieldset>

          <fieldset>
            <legend><fmt:message key="multiEditConfirmation.unsuccessful.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message key="multiEditConfirmation.unsuccessful.intro">
                <fmt:param value="${multiEditSessionInfo.multiEditErrorCount}"/>
              </fmt:message>
            </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${multiEditSessionInfo.multiEditErrorCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/assetEditor/grid/itemComponentEditor.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="numElements" value="${multiEditSessionInfo.multiEditErrorCount}"/>
                <dspel:param name="uniqueId" value="multiEditErrorId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditErrorList"/>
                <dspel:param name="drilldownFunction" value="editAsset"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </c:if>
          </fieldset>

          <fieldset>
            <legend><fmt:message key="multiEditConfirmation.noop.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message  key="multiEditConfirmation.noop.intro">
                <fmt:param value="${multiEditSessionInfo.multiEditNoopCount}"/>
              </fmt:message>
            </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${multiEditSessionInfo.multiEditNoopCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/assetEditor/grid/itemComponentEditor.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="numElements" value="${multiEditSessionInfo.multiEditNoopCount}"/>
                <dspel:param name="uniqueId" value="multiEditNoopId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditNoopList"/>
                <dspel:param name="drilldownFunction" value="editAsset"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </c:if>
          </fieldset>


        </form>
        </div>
      </div>

      <table id="rightPaneScrollFooter" style="clear: both;">
        <tr>
          <td id="rightPaneFooter">

            <%-- Right Pane Footer Action Buttons : Includes right pane footer action buttons --%>
            <div>
              <div id="rightPaneFooterRight">
                <fmt:message var="exitTitle" key="assetEditor.multiEdit.exit"/>
                <dspel:a href="javascript:parent.changeMode('Single','null','${multiEditPropertyGroupIndex}')"
                   iclass="button" title="${exitTitle}"><span><fmt:message
                   key="assetEditor.multiEdit.exit"/></span></dspel:a>
              </div>
              <div id="rightPaneFooterLeft"></div>
            </div>
          </td>
        </tr>
      </table>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/multiEditConfirmation.jsp#2 $$Change: 651448 $--%>

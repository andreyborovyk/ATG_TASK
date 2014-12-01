<%--
  Override property weigtings dialog for the baseSearchConfigPropertyEditor.jsp

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/overridePropWeightsDialog.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- h1><span id="confirmSaveTitle"><fmt:message key="baseSearchConfigPropertyEditor.overrideRankingProperties.title"/></span></h1 --%>
  <div id="overridePropWeightsFromNewBaseDialogBody">
    <p id="confirmSaveMessage">
      <fmt:message key="baseSearchConfigPropertyEditor.overrideRankingProperties.question"/>
    </p>
  </div>
  <div id="saveCreateConfirmDialogFooter">
    <a class="button" id="confirmSave" href="javascript:top.assetManagerDialog.execute(true)">
      <span id="confirmSaveButton">
        <fmt:message key="baseSearchConfigPropertyEditor.overrideRankingProperties.yes"/>
      </span>
    </a>
    <span id="confirmNoSaveButtonOuter">
      <a class="button" id="confirmNoSave" href="javascript:top.assetManagerDialog.execute(false);">
        <span id="confirmNoSaveButton">
          <fmt:message key="baseSearchConfigPropertyEditor.overrideRankingProperties.no"/>
        </span>
      </a>
      
    </span>
  </div>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/overridePropWeightsDialog.jsp#2 $$Change: 651448 $--%>
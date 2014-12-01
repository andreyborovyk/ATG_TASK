<%--
  Page fragment that displays a header of expression panel.

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigHeaderRender.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <table class="atg_dataTable atg_smerch_rulesTable">
    <thead>
      <tr>
        <th class="atg_smerch_orderCell"><fmt:message key="searchConfigHeaderRender.order" /></th>
        <th class="atg_iconCell"><fmt:message key="searchConfigHeaderRender.enable" /></th>
        <th><fmt:message key="searchConfigHeaderRender.rule" /></th>
        <th class="atg_iconCell"></th>
      </tr>
    </thead>
    <tbody>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigHeaderRender.jsp#2 $$Change: 651448 $--%>

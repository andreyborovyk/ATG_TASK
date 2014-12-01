<%--
  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/updateBaseSearchConfigLocaleFilter.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
  <dspel:setvalue bean="/atg/search/web/assetmanager/BaseSearchConfigLocaleFilter.language" value="${param.language}"/>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/updateBaseSearchConfigLocaleFilter.jsp#2 $$Change: 651448 $--%>

<%--
  Default property viewer for derivedSearchConfig

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/derivedSearchConfigViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config"
                    bean="/atg/search/web/assetmanager/Configuration"/>
  <dspel:importbean var="assetManagerConfig"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <c:set var="bundleName" value="${config.resourceBundle}"/>
  <fmt:setBundle var="bundle" basename="${bundleName}"/>

  <dspel:droplet name="/atg/search/web/droplet/DerivedSearchConfigList">
    <%-- Input Parameters --%>
    <dspel:param name="baseSearchConfig" value="${requestScope.atgCurrentAsset.asset}"/>
  
    <%-- Open Parameters --%>
    <dspel:oparam name="empty">
      <strong><fmt:message key="derivedSearchConfigViewer.noSearchConfigurations" bundle="${bundle}"/></strong>
    </dspel:oparam>
    
    <dspel:oparam name="outputStart">
      <fmt:message key="derivedSearchConfigViewer.tableHeaderText" bundle="${bundle}"/>
      <div class="atg_smerch_config_table">
      <table class="atg_dataTable">
      <tbody>
    </dspel:oparam>

    <dspel:oparam name="output">
      <dspel:getvalueof var="index" param="index"/>
      <dspel:getvalueof var="asset" param="element"/>

      <c:url var="linkURL" context="${assetManagerConfig.contextRoot}" value="/assetEditor/editAsset.jsp">
        <c:param name="assetURI" value="${asset.URI}"/>
        <c:param name="pushContext" value="true"/>
        <c:param name="linkPropertyName" value="${mpv.propertyName}"/>
      </c:url>
      
      <c:choose>
        <c:when test="${index % 2 == 0}">
          <tr>
        </c:when>
        <c:otherwise>
          <tr class="atg_altRow">
        </c:otherwise>
      </c:choose>
      <td>
        <a href="javascript:<c:out value='${mpv.uniqueId}'/>_pushContext('<c:out value='${linkURL}' escapeXml='false'/>');"><dspel:valueof param="element.displayName"/></a>
      </td></tr>
    </dspel:oparam>

    <dspel:oparam name="outputEnd">
      </tbody>
      </table>
      <div class="atg_pagination"><div class="atg_resultTotal"><dspel:valueof param="size"/> <fmt:message key="derivedSearchConfigViewer.searchConfigurations" bundle="${bundle}"/></div></div>
      </div>
    </dspel:oparam>

    <dspel:oparam name="error">
      <p><dspel:valueof param="element"/>
    </dspel:oparam>
  </dspel:droplet>
  
  <script type="text/javascript">
    function <c:out value="${mpv.uniqueId}"/>_pushContext( linkURL ) {
      // force a save of the current asset, then move to link URL
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(linkURL,null,null,null,null,true);
    }
  </script>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/derivedSearchConfigViewer.jsp#2 $$Change: 651448 $--%>

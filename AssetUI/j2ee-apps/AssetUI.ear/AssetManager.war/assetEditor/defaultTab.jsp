<%--
  Default editor tab for a repository item asset.  This generates a categorized
  list of properties that should be displayed in the tab, and displays them
  using a configurable page fragment.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/defaultTab.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Determine the path of the component that generates the property list.
       The default can be overridden using an itemViewMapping attribute. --%>
  <c:set var="filterPath" value="${requestScope.atgItemViewMapping.attributes.tabPropertyFilter}"/>
  <c:if test="${empty filterPath}">
    <c:set var="filterPath" value="/atg/web/assetmanager/editor/TabPropertyFilter"/>
  </c:if>

  <%-- Invoke the component method that generates the property list. --%>
  <web-ui:invoke var="categories" componentPath="${filterPath}" method="findTabProperties">
    <web-ui:parameter value="${requestScope.atgCurrentAsset}"/>
    <web-ui:parameter value="${requestScope.atgItemViewMapping}"/>
    <web-ui:parameter value="${requestScope.atgItemMapping}"/>
  </web-ui:invoke>

  <%-- Render the page fragment that displays the property list.  The default
       page path can be overridden using an itemViewMapping attribute. --%>
  <c:set var="layoutPage" value="${requestScope.atgItemViewMapping.attributes.layoutPage}"/>
  <c:if test="${empty layoutPage}">
    <c:set var="layoutPage" value="defaultTabLayout.jsp"/>
  </c:if>
  <dspel:include otherContext="${requestScope.atgItemViewMapping.attributes.layoutPageContext}"
                 page="${layoutPage}">
    <dspel:param name="categories" value="${categories}"/>
  </dspel:include>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/defaultTab.jsp#2 $$Change: 651448 $--%>

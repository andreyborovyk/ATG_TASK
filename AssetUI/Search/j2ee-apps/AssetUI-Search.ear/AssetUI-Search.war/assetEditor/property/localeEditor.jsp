<%--
  The locale property editor

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/localeEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>
  <%-- Get the resource bundle used by the RefinementRepository --%>
  <fmt:setBundle var="refinementBundle" basename="atg.repository.search.refinement.RefinementRepositoryTemplateResources"/>
  
  <%-- <td> supplied by propertyContainer.jsp --%>
    <div class="formLabel">
      <%-- Property name --%>
      <fmt:message bundle="${refinementBundle}" key="locale"/>:
    </div>
  </td>
  <td>
    <dspel:include page="localeSelector.jsp"/>
  <%-- </td> supplied by propertyContainer.jsp --%>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/localeEditor.jsp#2 $$Change: 651448 $--%>

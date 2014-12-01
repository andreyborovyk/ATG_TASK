<%--
  header for collection editor

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/folderChildrenHeader.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="propertyView" value="${requestScope.mpv}"/>

     <thead>
        <tr class="header">
          <th class="formValueCell tableOrder">
            <fmt:message key="folderChildrenHeader.order"/>
          </th>
          <th class="formValueCell">
            <fmt:message key="folderChildrenHeader.${propertyView.uniqueId}"/>
          </th>
          <th class="formValueCell">
            &nbsp;
          </th>
          <th class="formValueCell">
            &nbsp;
          </th>
        </tr>
     </thead>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/folderChildrenHeader.jsp#2 $$Change: 651448 $--%>

<%--
  Extra fragment for viewing a role's inherited access rights
  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler
  
  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/inheritedAccessRightsViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<!-- Begin AssetUI's /assetEditor/property/user/inheritedAccessRightsViewer.jsp -->

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"    uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <table class="data" cellspacing="0" cellpadding="0">
    <thead>
      <tr>
        <th><fmt:message key="roleEditor.allAccessRightsLabel"/><br />
            <fmt:message key="roleEditor.allAccessRightsExplanation"/></th>
      </tr>
    </thead>
    <tbody>

      <dspel:sort var="sorter" values="${formHandler.role.accessRights}">
        <dspel:orderBy property="displayName"/>
      </dspel:sort>
      <c:set var="assetList" value="${sorter.sortedArray}"/>

      <c:forEach items="${assetList}" var="accessRight" varStatus="status">
        <c:choose>
          <c:when test="${ status.index % 2 != 0 }">
            <c:set var="rowClass" value="alt"/>
          </c:when>
          <c:otherwise>
            <c:set var="rowClass" value=""/>
          </c:otherwise>
        </c:choose>
        <tr class='<c:out value="${rowClass}"/>' name='<c:out value="${rowname}"/>'>
          <td class="formValueCell"><c:out value="${accessRight.displayName}"/></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

</dspel:page>
<!-- End AssetUI's /assetEditor/property/user/inheritedAccessRightsViewer.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/inheritedAccessRightsViewer.jsp#2 $$Change: 651448 $--%>

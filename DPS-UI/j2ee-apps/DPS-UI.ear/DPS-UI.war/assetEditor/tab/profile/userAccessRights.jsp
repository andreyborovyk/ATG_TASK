<%--
  AccessRights tab for a user.

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/profile/userAccessRights.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<!-- Begin AssetUI's /assetEditor/tab/userAccessRights.jsp -->

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <fieldset>
    <legend><span><fmt:message key="userEditor.accessRights.fieldSetLabel"/></span></legend>
    <br />
    <table class="data" cellspacing="0" cellpadding="0">
      <thead>
        <tr>
          <th><fmt:message key="userEditor.accessRightsLabel"/><br />
              <fmt:message key="userEditor.accessRightsExplanation"/></th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${formHandler.user.accessRights}" var="accessRight" varStatus="status">
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
  </fieldset>
</dspel:page>
<!-- End AssetUI's /assetEditor/tab/userAccessRights.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/profile/userAccessRights.jsp#2 $$Change: 651448 $--%>

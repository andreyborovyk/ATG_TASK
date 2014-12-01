<%--
  Extra fragment for viewing a user's effective principals
  @param  formHandler   The form handler
  
  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/userEffectivePrincipalsViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<!-- Begin AssetUI's /assetEditor/property/user/userEffectivePrincipalsViewer.jsp -->

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <dspel:importbean var="assmanconfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <table class="data" cellspacing="0" cellpadding="0">
    <thead>
      <tr>
        <th><fmt:message key="userEditor.effectivePrincipalsLabel"/><br />
            <fmt:message key="userEditor.effectivePrincipalsExplanation"/></th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${formHandler.user.effectivePrincipals}" var="principal" varStatus="status">
        <c:choose>
          <c:when test="${ status.index % 2 != 0 }">
            <c:set var="rowClass" value="alt"/>
          </c:when>
          <c:otherwise>
            <c:set var="rowClass" value=""/>
          </c:otherwise>
        </c:choose>

        <tr class='<c:out value="${rowClass}"/>' name='<c:out value="${rowname}"/>'>
          <td class="formValueCell">
  
            <web-ui:isAssignableFrom var="isGroup" className="${principal.class.name}"
                    instanceOfClassName="atg.userdirectory.repository.RepositoryItemGroupRole"/>
            <c:choose> 
              <c:when test="${isGroup}">
                <web-ui:getIcon var="iconUrl"
                                container="/atg/epub/file/PublishingFileRepository"
                                type="profileGroup"
                                assetFamily="RepositoryItem"/>
                <c:set var="iconname" value="/atg/ui/userdirectory/images/PrincipalGroup.gif"/>
              </c:when>
              <c:otherwise>
                <%-- find the icon in the usual way --%>
                <web-ui:getIcon var="iconUrl"
                                container="${formHandler.repositoryPathName}"
                                type="${principal.repositoryItem.itemDescriptor.itemDescriptorName}"
                                assetFamily="RepositoryItem"/>
              </c:otherwise>
            </c:choose>

            <%-- display the icon --%>
            <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>

            <%-- display the name --%>
            <c:out value="${principal.name}"/>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</dspel:page>

<!-- End AssetUI's /assetEditor/property/user/userEffectivePrincipalsViewer.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/userEffectivePrincipalsViewer.jsp#2 $$Change: 651448 $--%>

<%--
  Tab for viewing a role's members.
  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler
  
  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/profile/roleMembers.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<!-- Begin AssetUI's /assetEditor/property/user/roleMembers.jsp -->

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <dspel:importbean var="assmanconfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>
  <c:set var="role" value="${formHandler.role}"/>

  <%-- Sorry... gotta use inline Java here... --%>
  <%
    atg.userdirectory.repository.RoleImpl role =
            (atg.userdirectory.repository.RoleImpl)pageContext.findAttribute("role");
    java.util.Collection usermembers = role.getUserMembersSortOnLogin(0,-1,1);
    pageContext.setAttribute("usermembers", usermembers);
    java.util.Collection userassignees = role.getAssigneeUsersSortOnLogin(0,-1,1);
    pageContext.setAttribute("userassignees", userassignees);
  %>

  <fieldset>
    <legend><fmt:message key="roleEditor.members.fieldSetLabel"/></legend>
    <br /> 

    <table class="data" cellspacing="0" cellpadding="0">
      <thead>
        <tr>
          <th><fmt:message key="roleEditor.assigneesLabel"/></th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${userassignees}" var="member" varStatus="status">
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
              <web-ui:getIcon var="iconUrl"
                              container="${formHandler.repositoryPathName}"
                              type="${member.repositoryItem.itemDescriptor.itemDescriptorName}"
                              assetFamily="RepositoryItem"/>
              <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>
              <c:out value="${member.name}"/>
            </td>              
          </tr>
        </c:forEach>
      </tbody>
    </table>
    
    <br />


    <table class="data" cellspacing="0" cellpadding="0">
      <thead>
        <tr>
          <th><fmt:message key="roleEditor.membersLabel"/><br />
              <fmt:message key="roleEditor.membersExplanation"/></th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${usermembers}" var="member" varStatus="status">
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
              <web-ui:getIcon var="iconUrl"
                              container="${formHandler.repositoryPathName}"
                              type="${member.repositoryItem.itemDescriptor.itemDescriptorName}"
                              assetFamily="RepositoryItem"/>
              <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>
              <c:out value="${member.name}"/>
            </td>              
          </tr>
        </c:forEach>
      </tbody>
    </table>

  </fieldset>

<script type="text/javascript">

  function roleAssignees_pushContext( linkURL ) {
    // force a save of the current asset, then move to link URL
    parent.atg.assetmanager.saveBeforeLeaveAsset(linkURL);
  }

</script>



</dspel:page>
<!-- End AssetUI's /assetEditor/property/user/roleMembers.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/profile/roleMembers.jsp#2 $$Change: 651448 $--%>

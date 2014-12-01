<%--
  Extra fragment for viewing an organizations's complete member list
  @param  view            A request scoped, MappedView item for this view
  @param  formHandler     The form handler
  @param  formHandlerPath The form handler path

  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/orgAllMembersViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<!-- Begin DPS-UI's /assetEditor/property/profile/orgAllMembersViewer.jsp -->

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>
  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>

  <c:if test="${empty formHandlerPath}">
    <c:set var="formHandlerPath" value="${formHandler.absoluteName}"/>
  </c:if>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <dspel:importbean var="assmanconfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <fmt:setBundle var="assmanbundle" basename="${assmanconfig.resourceBundle}"/>

  <%-- Get the total number of all members of the current organization --%>
  <c:set var="numElements" value="${formHandler.allMembersCount}"/>

  <%-- Construct a unique id --%>
  <c:set var="uniqueId" value="${requestScope.uniqueAssetID}allmembers"/>
  <c:set var="componentContextRoot" value="${config.contextRoot}"/>
  <c:set var="componentURI" value="/assetEditor/property/profile/grid/itemOrgMembers.jsp"/>

  <%-- Display all members --%>
  <table class="formTable">
    <tr>
      <td colspan="2">
       <div id="propEditorDiv_allmembers"
             style="display:inline">
        <div>
          <label for="propertyDisplayNameLabel">
            <fmt:message key="orgEditor.allMembersLabel"/><br/>
                <fmt:message key="orgEditor.allMembersExplanation"/>
          </label>
        </div>
        <c:if test="${not empty numElements && numElements > 0}">
            <dspel:include otherContext="${config.contextRoot}" page="/assetEditor/property/profile/grid/orgAllMembersGrid.jsp">
              <dspel:param name="componentContextRoot" value="${componentContextRoot}"/>
              <dspel:param name="componentURI" value="${componentURI}"/>
              <dspel:param name="modelPageSize" value="20"/>
              <dspel:param name="keepRows" value="100"/>
              <dspel:param name="numElements" value="${numElements}"/>
              <dspel:param name="uniqueId" value="${uniqueId}"/>
              <dspel:param name="useColumnHeaders" value="${false}"/>
              <dspel:param name="showIdColumn" value="${false}"/>
              <dspel:param name="showIconColumn" value="${true}"/>
              <dspel:param name="showNumItems" value="${true}"/>
              <dspel:param name="formHandlerPath" value="${formHandlerPath}"/>
            </dspel:include>
        </c:if>
       </div>
       </td>
    </tr>
  </table>
</dspel:page>
<!-- End DPS-UI's /assetEditor/property/profile/orgAllMembersViewer.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/orgAllMembersViewer.jsp#2 $$Change: 651448 $--%>

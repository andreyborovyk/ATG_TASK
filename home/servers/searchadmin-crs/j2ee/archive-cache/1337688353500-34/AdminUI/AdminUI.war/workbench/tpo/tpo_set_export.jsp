<%--
  JSP,  allow export TPO Set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_export.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="tpoSetId" var="tpoId"/>
  <d:getvalueof param="level" var="level"/>

  <d:getvalueof var="activeProjectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId" />

  <c:url var="downloadUrl" value="/download"/> 
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <form method="POST" action="${downloadUrl}">
      <input name="itemType" value="tpo" type="hidden" class="unchanged" />
      <input name="itemId" value="${tpoId}" type="hidden" class="unchanged" />

      <div id="paneContent">
        <fmt:message var="exportMessage" key="tpo_set.export.message"/>
        <c:if test="${not empty exportMessage}"><p>
          <c:out value="${exportMessage}"/>
        </p></c:if>
        <h3>
          <fmt:message key="tpo_set.export.message.tpo"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <fmt:message key="tpo_set.export.tpo_name"/>
              </td>
              <td>
                <tpo:textProcessingOptionsSetFindByPrimaryKey textProcessingOptionsSetId="${tpoId}" var="tpoSet"/>
                <c:out value="${tpoSet.name}"/>
              </td>
            </tr>
          </tbody>
        </table>
        <h3>
          <fmt:message key="tpo_set.export.message.file"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <fmt:message key="tpo_set.export.settings"/>
              </td>
              <td>
                <ul class="simple">
                  <li>
                    <input type="radio" id="settings_true" value="true" name="allOptions" checked="true" class="unchanged" />
                    <label for="settings_true">
                      <fmt:message key="tpo_set.export.settings.all"/>
                    </label>
                  </li>
                  <li>
                    <input type="radio" id="settings_false" value="false" name="allOptions" class="unchanged" />
                    <label for="settings_false">
                      <fmt:message key="tpo_set.export.settings.default"/>
                    </label>
                  </li>
                </ul>
              </td>
            </tr>

            <c:if test="${level eq 'content'}">
              <c:if test="${activeProjectId != null}">
                <tr id="activeProjectSettings_rightPane">
                  <td class="label">
                    <label for="searchProjectSettings">
                      <span id="searchProjectSettings"></span>
                      <fmt:message key="tpo_set.export.settings.search.project"/>
                    </label>
                  </td>
                  <td>
                    <d:getvalueof var="activeProjectTPOSet"
                                  bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectTPOSet"/>
                    <input type="checkbox" name="activeProjectTPO" value="${activeProjectTPOSet.id}" class="unchanged" />
                    <fmt:message key="tpo_set.export.settings.project"/>
                    <c:if test="${not empty activeProjectTPOSet}">
                      <c:out value=" ${activeProjectTPOSet.name}"/>
                    </c:if>
                    <c:if test="${empty activeProjectTPOSet}">
                      <%--TODO: should it be none or default name of tpo set? --%>
                      <fmt:message key="active_project.environment.none" />
                    </c:if>
                  </td>
                </tr>
              </c:if>

              <script type="text/javascript">
                function refreshRightPane(obj) {
                  <c:url value="${tpoPath}/tpo_set_export.jsp" var="activeProjectUrl">
                    <c:param name="level" value="content"/>
                    <c:param name="tpoSetId" value="${tpoId}"/>
                  </c:url>
                  loadRightPanel('${activeProjectUrl}');
                }
              </script>
            </c:if>
          </tbody>
        </table>
      </div>

      <div id="paneFooter">
        <fmt:message key="tpo_set.export.buttons" var="updateButtonTitle"/>
        <fmt:message key="tpo_set.export.buttons.tooltip" var="updateButtonToolTip"/>
        <input type="submit" value="${updateButtonTitle}" title="${updateButtonToolTip}"
               name="export" class="unchanged" />

        <fmt:message key="tpo_set.export.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="tpo_set.export.buttons.cancel.tooltip" var="cancelButtonToolTip"/>
        <%-- Cancel button --%>
        <tags:backButton value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_export.jsp#2 $$Change: 651448 $--%>

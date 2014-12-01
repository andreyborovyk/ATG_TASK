<%--
  JSP,  Export topic set. Allow export topic set to file

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_export.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="topicSetId" var="topicSetId"/>

  <topic:topicSetFindByPrimaryKey topicSetId="${topicSetId}" var="topicSet"/>

  <c:url var="downloadUrl" value="/download"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <form method="POST" action="${downloadUrl}">
      <input name="itemType" value="topic" type="hidden" class="unchanged" />
      <input name="itemId" value="${topicSetId}" type="hidden" class="unchanged" />

      <div id="paneContent">
        <%-- Table contain necessary field and info for import topic --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td colspan="2" align="left"><h3><fmt:message key="topicset_export.table.title.export_from_topicset"/></h3></td>
            </tr>
            <tr>
              <td class="label">
                <label for="topicSetName">
                  <fmt:message key="topicset_export.table.name"/>
                </label>
              </td>
              <td>
                <c:out value="${topicSet.name}"/>
              </td>
            </tr>
            <tr>
              <td colspan="2" align="left"><h3><fmt:message key="topicset_export.table.title.export_to_xml"/></h3></td>
            </tr>
            <tr>
              <td class="label">
                <label for="includeMacros">
                  <fmt:message key="topicset_export.table.global_macros"/>
                </label>
              </td>
              <td>
                <ul class="simple">
                  <li>
                    <input type="checkbox" name="includeMacros" value="true" class="unchanged" />
                    <fmt:message key="topicset_export.table.global_macros.export"/>
                  </li>
                </ul>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div id="paneFooter">
        <%-- Export button--%>
        <fmt:message key="topic_set.export.buttons" var="updateButtonTitle"/>
        <fmt:message key="topic_set.export.buttons.tooltip" var="updateButtonToolTip"/>
        <input type="submit" name="export" value="${updateButtonTitle}" title="${updateButtonToolTip}" class="unchanged" />
        <%-- Cancel button--%>
        <fmt:message key="new_topic_set.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="new_topic_set.buttons.cancel.tooltip" var="cancelButtonToolTip"/>
        <tags:backButton value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_export.jsp#2 $$Change: 651448 $--%>

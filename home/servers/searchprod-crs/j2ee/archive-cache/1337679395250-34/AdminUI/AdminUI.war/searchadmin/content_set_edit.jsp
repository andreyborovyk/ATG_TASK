<%--
Initial Content set settings for new search project page; add/edit new content set popup page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_set_edit.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="formHandler" bean="/atg/searchadmin/adminui/formhandlers/ManagePartitionFormHandler"/>
  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <span id="partitionNameAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
          <fmt:message key="content_set_edit.name"/>
        </td>
        <td>
          <d:input bean="ManagePartitionFormHandler.partitionName" name="partitionName" type="text" iclass="textField"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <span id="partitionDescriptionAlert"></span>
          <fmt:message key="content_set_edit.description"/>
        </td>
        <td>
          <d:input bean="ManagePartitionFormHandler.partitionDescription" name="partitionDescription" type="text" iclass="textField"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <span id="contentLabelAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
          <fmt:message key="content_set_edit.content_label"/>
        </td>
        <td>
          <c:set var="labels" value="${formHandler.allContentLabels}"/>
          <c:if test="${empty labels}">
            <span style="color:red;">
          </c:if>
          <tags:select bean="/atg/searchadmin/adminui/formhandlers/ManagePartitionFormHandler.contentLabel"
              items="${formHandler.allContentLabels}"  emptyMessageKey="content_set_edit.content_label.empty" />
          <c:if test="${empty labels}">
            </span>
          </c:if>
          <fmt:message key="content_set_edit.content_label.help">
            <fmt:param>
              <a href="searchadmin/global_settings.jsp" onclick="return loadRightPanel(this.href)">
                <fmt:message key="content_set_edit.content_label.help.link" /></a></fmt:param>
          </fmt:message>
          <p><fmt:message key="content_set_edit.content_label.note" /></p>
        </td>
      </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_set_edit.jsp#1 $$Change: 651360 $--%>

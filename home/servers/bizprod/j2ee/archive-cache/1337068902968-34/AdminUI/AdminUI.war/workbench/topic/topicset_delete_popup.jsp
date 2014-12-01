<%--
Delete topic set popup.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="topicSetId" var="topicSetId" scope="page"/>

  <topic:topicSetFindByPrimaryKey topicSetId="${topicSetId}" var="topicSet"/>

  <c:url var="targetURL" value="${topicPath}/global_general.jsp"/>
  <c:url var="errorURL" value="${topicPath}/topicset_delete_popup.jsp">
    <c:param name="topicSetId" value="${topicSetId}" />
  </c:url>
  <d:form action="${errorURL}" method="POST">
    <%-- Id of content set, used to be deleted --%>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTopicSetFormHandler.topicSetId"
             value="${topicSetId}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTopicSetFormHandler.successURL"
             value="${targetURL}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTopicSetFormHandler.errorURL"
             value="${errorURL}"/>

    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="topic_delete.topicset.question1">
          <fmt:param>
            <strong>
              <fmt:message key="topic_delete.topicset.question2">
                <fmt:param>
                  <c:out value="${topicSet.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
      <p>
        <fmt:message key="topic_delete.topicset.used_in_search_projects"/>
      </p>
      <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${topicSetId}" itemType="topics"/>
      <ul>
        <c:choose>
          <c:when test="${empty projects}">
            <li>
              <fmt:message key="topic_delete.topicset.used_in_search_projects.none"/>
            </li>
          </c:when>
          <c:otherwise>
            <c:forEach items="${projects}" var="currentProject">
              <li>
                <c:out value="${currentProject.name}"/>
              </li>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="topic_delete.topicset.delete.button" var="deleteButtonTitle"/>
      <fmt:message key="topic_delete.topicset.delete.button.tooltip" var="deleteButtonToolTip"/>
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTopicSetFormHandler.delete" type="submit"
               value="${deleteButtonTitle}" iclass="formsubmitter"
               title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='topic_delete.topicset.cancel.button'/>"
             onclick="closePopUp()" title="<fmt:message key='topic_delete.topicset.cancel.button.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_delete_popup.jsp#2 $$Change: 651448 $--%>

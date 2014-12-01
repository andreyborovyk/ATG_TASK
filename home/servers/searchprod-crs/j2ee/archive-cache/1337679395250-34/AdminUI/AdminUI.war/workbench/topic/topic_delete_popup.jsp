<%--
JSP provides popup for deleting topic

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="topicId" var="topicId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTopicFormHandler"/>

  <topic:topicFindByPrimaryKey topicId="${topicId}" var="topic" />
  <c:set var="parent" value="${topic.parent}" />
  <c:choose>
    <c:when test="${parent.nodeType eq 'Topic'}">
      <c:url value="${topicPath}/topic.jsp" var="targetURL">
        <c:param name="topicId" value="${parent.id}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url value="${topicPath}/topicset.jsp" var="targetURL">
        <c:param name="topicSetId" value="${parent.id}"/>
      </c:url>
    </c:otherwise>
  </c:choose>
  <c:url value="${topicPath}/topicset_delete_popup.jsp" var="actionUrl">
    <c:param name="topicId" value="${topicId}"/>
  </c:url>

  <d:form action="${actionUrl}" method="POST">

    <d:input type="hidden" bean="DeleteTopicFormHandler.errorURL" value="${actionUrl}"/>
    <d:input type="hidden" bean="DeleteTopicFormHandler.successURL" value="${targetURL}"/>

    <div class="content">
      <p>
        <fmt:message key="topic_delete_popup.question1">
          <fmt:param>
            <strong>
              <fmt:message key="topic_delete_popup.question2">
                <fmt:param>
                  <c:out value="${topic.name}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <d:input type="hidden" bean="DeleteTopicFormHandler.topicId" value="${topicId}" name="topicId"/>
      <fmt:message key="topic_delete_popup.button.delete" var="deleteButtonTitle"/>
      <fmt:message key="topic_delete_popup.button.delete.tooltip" var="deleteButtonToolTip"/>

      <d:input bean="DeleteTopicFormHandler.deleteTopic" type="submit" value="${deleteButtonTitle}"
               iclass="formsubmitter" title="${deleteButtonToolTip}" name="delete"/>
      <input type="button" value="<fmt:message key='topic_delete_popup.button.cancel'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='topic_delete_popup.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_delete_popup.jsp#2 $$Change: 651448 $--%>

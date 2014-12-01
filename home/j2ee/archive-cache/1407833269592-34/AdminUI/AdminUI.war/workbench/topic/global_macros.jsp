<%--
  JSP,  Patterns Macros tab

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/global_macros.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <c:set var="formHandlerName" value="/atg/searchadmin/workbenchui/formhandlers/GlobalMacrosFormHandler"/>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/GlobalMacrosFormHandler" var="macrosFormHandler"/>
  <d:getvalueof param="macrosType" var="macrosType"/>
  <d:getvalueof param="topicSetId" var="topicSetId"/>
  <d:getvalueof param="topicId" var="topicId"/>
  <c:url var="successURL"  value="${topicPath}/global_macros.jsp" >
    <c:param name="topicSetId" value="${topicSetId}"/>
    <c:param name="macrosType" value="${macrosType}"/>
    <c:param name="topicId" value="${topicId}"/>
  </c:url>
  <c:url var="errorURL"  value="${topicPath}/global_macros.jsp" >
    <c:param name="topicSetId" value="${topicSetId}"/>
    <c:param name="macrosType" value="${macrosType}"/>
    <c:param name="topicId" value="${topicId}"/>
  </c:url>
  <c:if test="${macrosType eq 'topic'}">
    <c:url var="successSaveURL" value="${topicPath}/topic.jsp">
      <c:param name="topicId" value="${topicId}"/>
      <c:param name="tab" value="2"/>
    </c:url>
  </c:if>
  <c:if test="${macrosType eq 'global'}">
    <c:url var="successSaveURL" value="${topicPath}/topicset.jsp">
      <c:param name="topicSetId" value="${topicSetId}"/>
    </c:url>
  </c:if>
  <c:if test="${macrosType eq 'head'}">
    <c:url var="successSaveURL" value="${topicPath}/global_general.jsp"/>
  </c:if>

  <admin-validator:validate beanName="GlobalMacrosFormHandler"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form method="POST" action="${errorURL}">
      <div id="paneContent">
        <p><fmt:message key="global_macro.message.title"/></p>
        <admin-ui:initializeFormHandler handler="${macrosFormHandler}"/>

        <d:include src="/workbench/macros_edit.jsp">
          <d:param name="formHandlerName" value="${formHandlerName}"/>
          <d:param name="macroMessage" value="global_macros.title"/>
          <d:param name="level" value="global" />
        </d:include>

      </div>
        <div id="paneFooter">
        <d:input bean="${formHandlerName}.successURL"   type="hidden"
                 value="${successURL}" name="successURL"/>
        <d:input bean="${formHandlerName}.successsaveURL"   type="hidden"
                 value="${successSaveURL}" name="successSaveURL"/>
        <d:input bean="${formHandlerName}.errorURL" type="hidden"
                 value="${errorURL}" name="errorURL"/>

        <fmt:message key="edit_topic_set.buttons.save" var="updateButtonTitle"/>
        <fmt:message key="edit_topic_set.buttons.save.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="${formHandlerName}.saveMacros" onclick="return checkForm();"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}" iclass="formsubmitter"/>

        <fmt:message key="new_topic_set.buttons.cancel" var="cancelButton"/>
        <fmt:message key="new_topic_set.buttons.cancel.tooltip" var="cancelButtonTooltip"/>
        <%-- Cancel button --%>
        <d:input type="submit" bean="${formHandlerName}.cancel"
                 value="${cancelButton}" title="${cancelButtonTooltip}" iclass="formsubmitter"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/global_macros.jsp#2 $$Change: 651448 $--%>

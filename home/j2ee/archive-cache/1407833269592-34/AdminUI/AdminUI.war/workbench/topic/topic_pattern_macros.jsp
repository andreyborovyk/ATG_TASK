<%--
  JPS, showing pattern macros page for topic.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_pattern_macros.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="topicId" var="topicId"/>

  <c:set var="formHandlerName" value="/atg/searchadmin/workbenchui/formhandlers/TopicMacrosFormHandler"/>
  
  <c:if test="${empty topicId}">
    <d:getvalueof bean="${formHandlerName}.id" var="topicId"/>
  </c:if>

  <admin-validator:validate beanName="TopicMacrosFormHandler"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="topic_pattern_macros.jsp" method="POST">
      <div id="paneContent">
        <p>
          <fmt:message key="topic_pattern_macros.head"/>
        </p>

         <%-- Here we include page, containing table with topic set pattern macroses--%>
        <d:getvalueof bean="/atg/searchadmin/workbenchui/formhandlers/TopicMacrosFormHandler" var="macrosFormHandler"/>
        <admin-ui:initializeFormHandler handler="${macrosFormHandler}">
          <admin-ui:param name="topicId" value="${topicId}"/>
        </admin-ui:initializeFormHandler>
        <d:include src="/workbench/macros_edit.jsp">
          <d:param name="formHandlerName" value="${formHandlerName}"/>
          <d:param name="macroMessage" value="pattern_macros.title"/>
          <d:param name="level" value="local" />
        </d:include>
      </div>
      <%--  URL definitions --%>
      <c:url var="errorURL" value="${topicPath}/topic_pattern_macros.jsp"/>
      <c:url var="successURL" value="${topicPath}/topic.jsp">
        <c:param name="topicId" value="${topicId}" />
        <c:param name="tab" value="2"/>
      </c:url>

      <div id="paneFooter">
        <d:input bean="${formHandlerName}.successURL" type="hidden" value="${successURL}" name="successURL"/>
        <d:input bean="${formHandlerName}.errorURL" type="hidden" value="${errorURL}" name="errorURL"/>
        <d:input bean="${formHandlerName}.id" type="hidden" value="${topicId}" name="topicId"/>

        <fmt:message key="topic_pattern_macros.save" var="updateButtonTitle"/>
        <fmt:message key="topic_pattern_macros.save.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="${formHandlerName}.saveMacros" onclick="return checkForm();" name="saveMacros"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}" iclass="formsubmitter"/>

        <%-- Cancel button --%>
        <fmt:message key="topic_pattern_macros.cancel" var="cancelButtonTitle"/>
        <fmt:message key="topic_pattern_macros.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="${formHandlerName}.cancel"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}" iclass="formsubmitter"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_pattern_macros.jsp#2 $$Change: 651448 $--%>

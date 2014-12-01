<%@ page contentType="application/json; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<dsp:page>

<c:catch var="exception">
  <%
    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(pageContext.getRequest().getInputStream()));
    String requestBody = "";
    String requestLine;
    while ((requestLine = in.readLine()) != null) {
      requestBody += requestLine;
    }
    atg.json.JSONObject jsonRequest = new atg.json.JSONObject(requestBody);
    atg.json.JSONArray params = jsonRequest.getJSONArray("params");
    if (params != null) {
      pageContext.setAttribute("isDebug", new Boolean(params.getBoolean(0)));
    }
    String method = jsonRequest.getString("method");
    if (method.compareToIgnoreCase("readMessages") == 0) {
  %>
  <dsp:importbean bean="/atg/web/messaging/MessageTools" var="messageTools"/>
  <c:set value="${messageTools.userMessagingSlotComponent}" var="slotComponent"/>
  <dsp:droplet name="/atg/targeting/TargetingArray">
    <dsp:param name="targeter" value="${slotComponent}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof id="messageArray" param="elements"/>
      <%
        java.util.List messageList = new java.util.ArrayList();
        Object[] messageArray = (Object[])pageContext.getAttribute("messageArray");
        for (int i = 0; i < messageArray.length; i++) {
          if (messageArray[i] instanceof atg.web.messaging.UserMessage) {
            messageList.add(messageArray[i]);
          }
        }
        pageContext.setAttribute("messageList", messageList);
      %>
      <c:choose>
      <c:when test="${not empty messageList}">
        <json:object>
          <json:array name="messages" items="${messageList}" var="msg">
            <json:object>
              <json:property name="summary" value="${msg.summary}"/>
              <json:property name="type" value="${msg.type}"/>
              <fmt:formatDate value="${msg.datetime}" pattern="EEE, d MMM yyyy HH:mm:ss Z" var="msgDate"/>
              <json:property name="datetime" value="${msgDate}"/>
              <c:if test="${msg.class.name eq 'atg.web.messaging.RequestMessage'}">
                <c:if test="${not empty msg.messageDetails}">
                  <json:array name="details" items="${msg.messageDetails}" var="detail">
                    <json:object>
                      <json:property name="description" value="${detail.description}"/>
                      <c:if test="${detail.class.name eq 'atg.web.messaging.PropertyMessageDetail'}">
                        <json:property name="propertyName" value="${detail.propertyName}"/>
                        <json:property name="propertyPath" value="${detail.propertyPath}"/>
                      </c:if>
                      <c:if test="${isDebug and not empty detail.cause}">
                        <json:object name="cause">
                          <json:property name="class" value="${detail.cause.class.name}"/>
                          <json:property name="message" value="${detail.cause.message}"/>
                          <json:property name="stacktrace" value="${detail.causeStacktrace}"/>
                        </json:object>
                      </c:if>
                      <c:if test="${isDebug and not empty detail.source}">
                        <json:object name="source">
                          <json:property name="class" value="${detail.source.class.name}"/>
                          <json:property name="message" value="${detail.source.message}"/>
                          <json:property name="stacktrace" value="${detail.stacktrace}"/>
                        </json:object>
                      </c:if>
                    </json:object>
                  </json:array>
                </c:if>
                <c:if test="${isDebug}">
                  <json:property name="requestUrl" value="${msg.requestUrl}"/>
                </c:if>
              </c:if>
              <c:if test="${isDebug}">
                <json:property name="priority" value="${msg.priority}"/>
                <json:property name="timestamp" value="${msg.timestamp}"/>
              </c:if>
            </json:object>
          </json:array>
        </json:object>
      </c:when>
      <c:otherwise>
        <json:object>
        </json:object>
      </c:otherwise>
      </c:choose>
    </dsp:oparam>
    <dsp:oparam name="empty">
      <json:object>
      </json:object>
    </dsp:oparam>
  </dsp:droplet>
  <%
    }
  %>
</c:catch>
<c:if test="${exception ne null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
    ee.printStackTrace();
  %>
</c:if>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/widget/messaging/readMessages.jsp#2 $$Change: 651448 $--%>

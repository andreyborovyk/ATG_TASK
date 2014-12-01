<html>
  
<%@ include file="header.jspf" %>

<head>
  <title>SQL-JMS Admin - Message</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- calculate navigation states --%>
<jsp:setProperty name="dmsContext" property="currentPage" value="viewMessage.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure the message id is set --%>
<core:urlParamValue id="messageId" param="messageId">
  <core:ifNull value="<%= messageId.getValue() %>">
    <core:redirect url="main.jsp"/>
  </core:ifNull>
</core:urlParamValue>

<core:demarcateTransaction id="pageXA">

<core:urlParamValue id="messageId" param="messageId">
  <dms:message id="message" messageId="<%= messageId.getValue() %>">
    <jsp:setProperty name="dmsContext" property="message" 
                     value="<%= message.getMessage() %>"/>
  </dms:message>
</core:urlParamValue>

<%-- set the message id in the navigation state --%>
<dms:setNavigationParam navigation="<%= currentNav %>" param="messageId"
                        paramValue="<%= dmsContext.getMessage().getId() %>"/>

<p>

<%-- print the title --%>
<h1>View Message</h1>

<p>

<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>

<p>

<table border=1>
  <tr>
    <td><b>Id</b>
    <td><%= dmsContext.getMessage().getId() %>
  </tr>
  <tr>
    <td><b>Message Class</b>
    <td><%= dmsContext.getMessage().getMessageClass() %>
  </tr>
  <tr>
    <td><b>Has Properties</b>
    <td><%= dmsContext.getMessage().isHasProperties() %>
  </tr>
  <tr>
    <td><b>Reference Count</b>
    <td><%= dmsContext.getMessage().getReferenceCount() %>
  </tr>
  <tr>
    <td><b>Timestamp</b>
    <td>
      <dms:dateFormat id="timestampDate" date="<%= dmsContext.getMessage().getTimestamp() %>">
        <%= timestampDate %>
      </dms:dateFormat>
  </tr>
  <tr>
    <td><b>CorrelationId</b>
    <td><%= dmsContext.getMessage().getCorrelationId() %>
  </tr>
  <tr>
    <td><b>Reply To</b>
    <td><%= dmsContext.getMessage().getReplyTo() %>
  </tr>
  <tr>
    <td><b>Destination</b>
    <td><%= dmsContext.getMessage().getDestination() %>
  </tr>
  <tr>
    <td><b>Delivery Mode</b>
    <td><%= dmsContext.getMessage().getDeliveryMode() %>
  </tr>
  <tr>
    <td><b>Redelivered</b>
    <td><%= dmsContext.getMessage().isRedelivered() %>
  </tr>
  <tr>
    <td><b>Type</b>
    <td><%= dmsContext.getMessage().getType() %>
  </tr>
  <tr>
    <td><b>Expiration</b>
    <td>
      <dms:dateFormat id="expirationDate" date="<%= dmsContext.getMessage().getExpiration() %>">
        <%= expirationDate %>
      </dms:dateFormat>
  </tr>
  <tr>
    <td><b>Priority</b>
    <td><%= dmsContext.getMessage().getPriority() %>
  </tr>
</table>

<%-- print all the properties --%>
<core:if value="<%= dmsContext.getMessage().isHasProperties() %>">

<p>
<h2>Properties</h2>
<p>
<table border=1>
  <tr>
    <th>Name
    <th>Data Type
    <th>Value
  </tr>
  <core:forEach id="propsForEach" values="<%= dmsContext.getMessage().getProperties() %>"
                elementId="property" castClass="atg.sqljmsadmin.DMSMessageProperty">
    <tr>
      <td><%= property.getName() %>
      <td><%= property.getDataType() %>
      <td><%= property.getValue() %>
    </tr>
  </core:forEach>
</table>  

</core:if>

</core:demarcateTransaction>

</body>
</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/viewMessage.jsp#2 $$Change: 651448 $--%>

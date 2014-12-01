<html>
  
<%@ include file="header.jspf" %>

<head>
  <title>SQL-JMS Admin - Topic Subscriptions</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- calculate navigation states --%>
<jsp:setProperty name="dmsContext" property="currentPage" value="topicSubs.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure the topic id is set --%>
<core:urlParamValue id="topicId" param="topicId">
  <core:ifNull value="<%= topicId.getValue() %>">
    <core:redirect url="main.jsp"/>
  </core:ifNull>
</core:urlParamValue>

<%-- handle deletions --%>
<core:ifNotNull value="<%= entryData.getAction() %>">
  <%@ include file="delete.jspf" %>
</core:ifNotNull>

<p>
<core:demarcateTransaction id="pageXA">

<core:urlParamValue id="topicId" param="topicId">
  <dms:topic id="topic" topicId="<%= topicId.getValue() %>">
    <jsp:setProperty name="dmsContext" property="topic" value="<%= topic.getTopic() %>"/>
  </dms:topic>
</core:urlParamValue>

<%-- set the topic id in the navigation state --%>
<dms:setNavigationParam navigation="<%= currentNav %>" param="topicId"
                        paramValue="<%= dmsContext.getTopic().getId() %>"/>

<%-- print the title --%>
<h1>Subscriptions for Topic: <%= dmsContext.getTopic().getName() %></h1>

<p>

<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>

<p>

<%-- print a list of all the topic subscriptions --%>
<h3>Topic Subscriptions</h3>
<table border=1>
  <tr>
    <th>Id
    <th>Name
    <th>Client
    <th>Durable
    <th>Entries
    <th>
    <th>
  </tr>
  <core:sort id="topicSort" values="<%= dmsContext.getTopic().getSubscriptions() %>">
    <core:orderBy property="name"/>
  <core:forEach id="topicSubs" values="<%= topicSort.getSortedArray() %>"
                elementId="topicSub" castClass="atg.sqljmsadmin.DMSTopicSubscription">
    <tr>
      <td><%= topicSub.getId() %>
      <td><%= topicSub.getName() %>
      <td>
	<core:exclusiveIf>
	  <core:ifNotNull value="<%= topicSub.getClient() %>">
	    <%= topicSub.getClient().getId() %> <%= topicSub.getClient().getName() %>
	  </core:ifNotNull>
	  <core:defaultCase>
	    -
	  </core:defaultCase>
	</core:exclusiveIf>		       
      <td><%= topicSub.isDurable() %>
      <td><%= topicSub.getTopicEntries().length %>
      <td>
        <core:createUrl id="topicEntriesUrl" url="topicEntries.jsp">
	  <core:urlParam param="topicSubId" value="<%= topicSub.getId() %>"/>
	  <a href="<%= topicEntriesUrl %>">topic entries</a>
	</core:createUrl>
      <td>
        <core:createUrl id="deleteTopicSubUrl" url="topicSubs.jsp">
	  <core:urlParam param="topicId" value="<%= dmsContext.getTopic().getId() %>"/>
	  <core:urlParam param="topicSubId" value="<%= topicSub.getId() %>"/>
	  <core:urlParam param="action" value="<%= dmsContext.kDeleteTopicSubscription %>"/>
	  <a href="<%= deleteTopicSubUrl %>">delete</a>
	</core:createUrl>
    </tr>
  </core:forEach>
  </core:sort>
</table>

</core:demarcateTransaction>

</body>
</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/topicSubs.jsp#2 $$Change: 651448 $--%>

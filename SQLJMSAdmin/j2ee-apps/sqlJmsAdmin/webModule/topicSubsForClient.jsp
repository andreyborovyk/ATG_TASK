<html>
  
<%@ include file="header.jspf" %>

<jsp:useBean id="topicSubComparator" class="atg.sqljmsadmin.taglib.DMSTopicSubComparator"/>

<head>
  <title>SQL-JMS Admin - Topic Subscriptions For Client</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- calculate navigation states --%>
<jsp:setProperty name="dmsContext" property="currentPage" value="topicSubsForClient.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure the client id is set --%>
<core:urlParamValue id="clientId" param="clientId">
  <core:ifNull value="<%= clientId.getValue() %>">
    <core:redirect url="main.jsp"/>
  </core:ifNull>
</core:urlParamValue>

<%-- handle deletions --%>
<core:ifNotNull value="<%= entryData.getAction() %>">
  <%@ include file="delete.jspf" %>
</core:ifNotNull>

<core:demarcateTransaction id="pageXA">

<core:urlParamValue id="clientId" param="clientId">
  <dms:client id="client" clientId="<%= clientId.getValue() %>">
    <jsp:setProperty name="dmsContext" property="client" value="<%= client.getClient() %>"/>
  </dms:client>
</core:urlParamValue>


<%-- set the client id in the navigation state --%>
<dms:setNavigationParam navigation="<%= currentNav %>" param="clientId"
                        paramValue="<%= dmsContext.getClient().getId() %>"/>

<%-- print the title --%>
<h1>Topic Subscriptions for Client: 
    <%= dmsContext.getClient().getId() %>
    <%= dmsContext.getClient().getName() %>
</h1>


<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>


<%-- print a list of the topic subscriptions --%>
<h3>Topic Subscriptions</h3>
<table border=1>
  <tr>
    <th>Id
    <th>Topic
    <th>Name
    <th>Durable
    <th>Entries
    <th>
    <th>
  </tr>
  <core:sort id="topicSubSort" values="<%= dmsContext.getClient().getSubscriptions() %>"
             comparator="<%= topicSubComparator %>">
  <core:forEach id="topicSubs" values="<%= topicSubSort.getSortedArray() %>"
                elementId="topicSub" castClass="atg.sqljmsadmin.DMSTopicSubscription">
    <tr>
      <td><%= topicSub.getId() %>
      <td><%= topicSub.getTopic().getName() %>
      <td><%= topicSub.getName() %>
      <td><%= topicSub.isDurable() %>
      <td><%= topicSub.getTopicEntries().length %>
      <td>
        <core:createUrl id="topicEntriesUrl" url="topicEntries.jsp">
	  <core:urlParam param="topicSubId" value="<%= topicSub.getId() %>"/>
	  <a href="<%= topicEntriesUrl %>">topic entries</a>
	</core:createUrl>
      <td>
        <core:createUrl id="deleteTopicSubUrl" url="topicSubsForClient.jsp">
	  <core:urlParam param="clientId" value="<%= dmsContext.getClient().getId() %>"/>
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
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/topicSubsForClient.jsp#2 $$Change: 651448 $--%>

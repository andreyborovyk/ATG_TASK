<html>

<%@ include file="header.jspf" %>

<head>
  <title>SQL-JMS Admin</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- clear the stack --%>
<dms:clearStack stack="<%= navStack %>"/>

<%-- set the navigtion state that leads to this page --%>
<dms:resetNavigation navigation="<%= currentNav %>"/>
<jsp:setProperty name="currentNav" property="referringPage" value="main.jsp"/>

<%-- handle deletions --%>
<core:ifNotNull value="<%= entryData.getAction() %>">
  <%@ include file="delete.jspf" %>
</core:ifNotNull>

<core:demarcateTransaction id="pageXA">

<h1>SQL-JMS Admin</h1>

<p>
<h3>Clients</h3>
<p>
<table border=1>
  <dms:admin id="clients">
    <core:exclusiveIf>
      <core:ifNull value="<%= clients.getClients() %>">
        <tr><td>There are currently no SQL-JMS Clients
      </core:ifNull>
      <core:defaultCase>
        <tr>
	  <th>Id
	  <th>Name
	  <th>
	  <th>
	  <th>
	</tr>
	<core:sort id="clientSort" values="<%= clients.getClients() %>">
	  <core:orderBy property="name"/>

        <core:forEach id="clientsForEach"
	              values="<%= clientSort.getSortedArray() %>"
		      elementId="client"
		      castClass="atg.sqljmsadmin.DMSClient">
          <tr>	      
	    <td><%= client.getId() %>
	    <td><%= client.getName() %>
	    <td>
	      <core:createUrl id="queuesUrl"
	                      url="queuesByClient.jsp">
		<core:urlParam param="clientId" value="<%= client.getId() %>"/>
		<a href="<%= queuesUrl %>">queues</a>
	      </core:createUrl>
	    <td>
	      <core:createUrl id="topicSubscriptionsUrl"
	                      url="topicSubsForClient.jsp">
		<core:urlParam param="clientId" value="<%= client.getId() %>"/>
		<a href="<%= topicSubscriptionsUrl %>">topic subscriptions</a>
	      </core:createUrl>
	    <td>
	      <core:createUrl id="deleteClientUrl" url="main.jsp">
	        <core:urlParam param="clientId" value="<%= client.getId() %>"/>
	        <core:urlParam param="action" value="<%= dmsContext.kDeleteClient %>"/>
		<a href="<%= deleteClientUrl %>">delete</a>
	      </core:createUrl>
	  </tr>
	</core:forEach>
	</core:sort>
      </core:defaultCase>
    </core:exclusiveIf>
  </dms:admin>
</table>

<p>
<h3>Queues</h3>
<p>
<table border=1>
  <dms:admin id="queues">
    <core:exclusiveIf>
      <core:ifNull value="<%= queues.getQueues() %>">
        <tr><td>There are currently no SQL-JMS Queues
      </core:ifNull>
      <core:defaultCase>
        <tr>
	  <th>Id
	  <th>Name
	  <th>Entries
	  <th>
	  <th>
	</tr>
	<core:sort id="queueSort" values="<%= queues.getQueues() %>">
	  <core:orderBy property="name"/>
	<core:forEach id="queuesForEach"
	              values="<%= queueSort.getSortedArray() %>"
		      elementId="queue"
		      castClass="atg.sqljmsadmin.DMSQueue">
	  <tr>
	    <td><%= queue.getId() %>
	    <td><%= queue.getName() %>
	    <td><%= queue.getQueueEntries().length %>
	    <td>
	      <core:createUrl id="queueUrl"
	                      url="queueEntries.jsp">
	        <core:urlParam param="queueId" value="<%= queue.getId() %>"/>
		<a href="<%= queueUrl %>">queue entries</a>
	      </core:createUrl>
	    <td>
	      <core:createUrl id="deleteQueueUrl" url="main.jsp">
	        <core:urlParam param="queueId" value="<%= queue.getId() %>"/>
	        <core:urlParam param="action" value="<%= dmsContext.kDeleteQueue %>"/>
		<a href="<%= deleteQueueUrl %>">delete</a>
	      </core:createUrl>
	  </tr>            
	</core:forEach>               
	</core:sort>
      </core:defaultCase>      
    </core:exclusiveIf>
  </dms:admin>
</table>

<p>
<h3>Topics</h3>
<p>
<table border=1>
  <dms:admin id="topics">
    <core:exclusiveIf>
      <core:ifNull value="<%= topics.getTopics() %>">
        <tr><td>There are currently no SQL-JMS Topics
      </core:ifNull>
      <core:defaultCase>
        <tr>
	  <th>Id
	  <th>Name
	  <th>Subscriptions
	  <th>
	  <th>
	</tr>
	<core:sort id="topicSort" values="<%= topics.getTopics() %>">
	  <core:orderBy property="name"/>
	<core:forEach id="topicsForEach"
	              values="<%= topicSort.getSortedArray() %>"
		      elementId="topic"
		      castClass="atg.sqljmsadmin.DMSTopic">
	  <tr>
	    <td><%= topic.getId() %>
	    <td><%= topic.getName() %>
	    <td><%= topic.getSubscriptions().length %>
	    <td>
	      <core:createUrl id="topicUrl"
	                      url="topicSubs.jsp">
	        <core:urlParam param="topicId" value="<%= topic.getId() %>"/>
		<a href="<%= topicUrl %>">topic subscriptions</a>
	      </core:createUrl>
	    <td>
	      <core:createUrl id="deleteTopicUrl" url="main.jsp">
	        <core:urlParam param="topicId" value="<%= topic.getId() %>"/>
	        <core:urlParam param="action" value="<%= dmsContext.kDeleteTopic %>"/>
		<a href="<%= deleteTopicUrl %>">delete</a>
	      </core:createUrl>
	  </tr>            
	</core:forEach>               
	</core:sort>
      </core:defaultCase>      
    </core:exclusiveIf>
  </dms:admin>
</table>

</core:demarcateTransaction>

</body>
</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/main.jsp#2 $$Change: 651448 $--%>

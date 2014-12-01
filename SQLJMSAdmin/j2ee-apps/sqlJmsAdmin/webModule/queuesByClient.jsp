<html>
  
<%@ include file="header.jspf" %>

<head>
  <title>SQL-JMS Admin - Queues by Client</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- calculate navigation states --%>
<jsp:setProperty name="dmsContext" property="currentPage" value="queuesByClient.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure the client id is set, if not, redirect back to main page --%>
<core:urlParamValue id="clientId" param="clientId">
  <core:exclusiveIf>
    <core:ifNull value="<%= clientId.getValue() %>">
      <core:Redirect url="main.jsp"/>
    </core:ifNull>
    <core:defaultCase>
      <dms:client id="client" clientId="<%= clientId.getValue() %>">
        <jsp:setProperty name="dmsContext" property="client" 
	                 value="<%= client.getClient() %>"/>
      </dms:client>
    </core:defaultCase>
  </core:exclusiveIf>
</core:urlParamValue>

<%-- handle deletions --%>
<core:ifNotNull value="<%= entryData.getAction() %>">
  <%@ include file="delete.jspf" %>
</core:ifNotNull>

<core:demarcateTransaction id="pageXA">

<%-- add the client id to the params for this page --%>
<dms:setNavigationParam navigation="<%= currentNav %>" param="clientId" 
                        paramValue="<%= dmsContext.getClient().getId() %>"/>

<h1>Queues for Client: <%= dmsContext.getClient().getName() %></h1>
<p>

<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>

<p>

    <table border=1>
      <tr>
        <th>Id
	<th>Name
	<th>All Entries
	<th>
	<th>
      </tr>
      <core:sort id="queueSort" values="<%= dmsContext.getClient().getQueues() %>">
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
	    <core:createUrl id="queueEntriesUrl"
	                    url="queueEntriesForClient.jsp">
	      <core:urlParam param="queueId" value="<%= queue.getId() %>"/>
	      <core:urlParam param="clientId" value="<%= dmsContext.getClient().getId() %>"/>
	      <a href="<%= queueEntriesUrl %>">queue entries</a>
	    </core:createUrl>
          <td>
	    <core:createUrl id="deleteQueueUrl" url="queuesByClient.jsp">
	      <core:urlParam param="queueId" value="<%= queue.getId() %>"/>
	      <core:urlParam param="clientId" value="<%= dmsContext.getClient().getId() %>"/>
	      <core:urlParam param="action" value="<%= dmsContext.kDeleteQueue %>"/>
	      <a href="<%= deleteQueueUrl %>">delete</a>
	    </core:createUrl>
	</tr>
      </core:forEach>
      </core:sort>
    </table>

</core:demarcateTransaction>

</body>

</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/queuesByClient.jsp#2 $$Change: 651448 $--%>

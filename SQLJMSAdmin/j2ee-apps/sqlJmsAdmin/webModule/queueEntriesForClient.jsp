<html>
  
<%@ include file="header.jspf" %>

<jsp:useBean id="queueEntryComparator" class="atg.sqljmsadmin.taglib.DMSQueueEntryComparator"/>

<head>
  <title>SQL-JMS Admin - Queue Entries</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<jsp:setProperty name="dmsContext" property="currentPage" value="queueEntriesForClient.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure the queueId and clientId params are set 
     if not, redirect back to main page --%>
<core:urlParamValue id="queueId" param="queueId">
  <core:exclusiveIf>
    <core:ifNull value="<%= queueId.getValue() %>">
      <core:Redirect url="main.jsp"/>
    </core:ifNull>
    <core:defaultCase>
      <core:urlParamValue id="clientId" param="clientId">
        <core:exclusiveIf>
          <core:ifNull value="<%= clientId.getValue() %>">
	    <core:Redirect url="main.jsp"/>
	  </core:ifNull>
	  <core:defaultCase>
	    <dms:queue id="queue" queueId="<%= queueId.getValue() %>">
              <jsp:setProperty name="dmsContext" property="queue"
	                       value="<%= queue.getQueue() %>"/>
            </dms:queue>
	    <dms:client id="client" clientId="<%= clientId.getValue() %>">
	      <jsp:setProperty name="dmsContext" property="client"
	                       value="<%= client.getClient() %>"/>
	    </dms:client> 
	  </core:defaultCase>
	</core:exclusiveIf>
      </core:urlParamValue>
    </core:defaultCase>
  </core:exclusiveIf>
</core:urlParamValue>

<%-- set the clientId and queueId in the navigation params --%>
<dms:setNavigationParam  navigation="<%= currentNav %>" param="queueId"
                         paramValue="<%= dmsContext.getQueue().getId() %>"/>
<dms:setNavigationParam  navigation="<%= currentNav %>" param="clientId"
                         paramValue="<%= dmsContext.getClient().getId() %>"/>

<%-- handle deletions --%>
<core:ifNotNull value="<%= entryData.getAction() %>">
  <%@ include file="delete.jspf" %>
</core:ifNotNull>

<p>

<core:demarcateTransaction id="pageXA">


<%-- print the title --%>
<h1>
Queue Entries for 
Queue: <%= dmsContext.getQueue().getName() %> for 
Client: <%= dmsContext.getClient().getName() %>
</h1>

<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>

<form method=POST action="queueEntriesForClient.jsp">

<%-- print list of queue entries --%>
<p>
<h3>Pending Queue Entries</h3>

<table border=1>
  <tr>
    <th>
    <th>Queue Id
    <th>Message Id
    <th>Type
    <th>Timestamp
    <th>Expiration
    <th>Priority
    <th>
    <th>
  </tr>
  <core:sort id="queueEntriesSort" values="<%= dmsContext.getQueue().pendingMessages() %>"
             comparator="<%= queueEntryComparator %>">
  <core:forEach id="clientQueueEntries"
                values="<%= queueEntriesSort.getSortedArray() %>"
		elementId="queueEntry"
		castClass="atg.sqljmsadmin.DMSQueueEntry">
    <core:ifEqual object1="<%= queueEntry.getClient().getId() %>"
	          object2="<%= dmsContext.getClient().getId() %>">
      <tr>
        <td>
	  <dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
	                         long2="<%= queueEntry.getId().getMessageId() %>">
	    <input type=checkbox name="entryIds" value="<%= encodeKey.getEncodedKey() %>">
          </dms:encodeCompoundKey>
        <td><%= queueEntry.getId().getQueueId() %>
	<td><%= queueEntry.getId().getMessageId() %>
	  <core:exclusiveIf>
	    <core:ifNotNull value="<%= queueEntry.getMessage() %>">
	      <core:createUrl id="messageUrl"
	                      url="viewMessage.jsp">
	        <core:urlParam param="messageId" 
	                       value="<%= queueEntry.getMessage().getId() %>"/>
  	        <td><%= queueEntry.getMessage().getType() %>
	        <td>
		  <dms:dateFormat id="timestampDate" 
		                  date="<%= queueEntry.getMessage().getTimestamp() %>">
		    <%= timestampDate %>
		  </dms:dateFormat>
	        <td>
		  <dms:dateFormat id="expirationDate"
		                  date="<%= queueEntry.getMessage().getExpiration() %>">
		    <%= expirationDate %>
		  </dms:dateFormat>
	        <td><%= queueEntry.getMessage().getPriority() %>
	        <td><a href="<%= messageUrl %>">view message</a>
              </core:createUrl>
            </core:ifNotNull>
	    <core:defaultCase>
              <td>-
	      <td>-
	      <td>-
	      <td>-
	      <td>-
	    </core:defaultCase>
	  </core:exclusiveIf>
	<%--
	<core:createUrl id="moveQueueEntryUrl" url="queueEntriesForClient.jsp">
	  <core:urlParam param="action" value="<%= dmsContext.kMoveQueueEntry %>"/>
	  <core:urlParam param="clientId" value="<%= dmsContext.getClient().getId() %>"/>
	  <core:urlParam param="queueId" value="<%= queueEntry.getId().getQueueId() %>"/>
	  <dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
	                         long2="<%= queueEntry.getId().getMessageId() %>">
            <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
          </dms:encodeCompoundKey>
	  <td><a href="<%= moveQueueEntryUrl %>">move</a>
	</core:createUrl>
        --%>
	<core:createUrl id="deleteQueueEntryUrl" url="queueEntriesForClient.jsp">
	  <core:urlParam param="action" value="<%= dmsContext.kDeleteQueueEntry %>"/>
	  <core:urlParam param="clientId" 
	                 value="<%= dmsContext.getClient().getId() %>"/>
	  <core:urlParam param="queueId" 
	                 value="<%= queueEntry.getId().getQueueId() %>"/>
	  <dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
	                         long2="<%= queueEntry.getId().getMessageId() %>">
            <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
          </dms:encodeCompoundKey>
          <td><a href="<%= deleteQueueEntryUrl %>">delete</a>
	</core:createUrl>
      </tr>
    </core:ifEqual>
  </core:forEach>
  </core:sort>
</table>
<p>
<pre>
*Note: If a pending queue entry is deleted from an active SQL JMS system, there
       is still the possiblity of a receiver consuming the queue entry even after 
       the queue entry has been deleted.
</pre>

<p>

<p>

<input type="hidden" name="queueId" value="<%= dmsContext.getQueue().getId() %>">
<input type="hidden" name="clientId" value="<%= dmsContext.getClient().getId() %>">
<input type="hidden" name="queueSource" value="true">

<%@ include file="pendingQueueEntryFormSubmit.jspf" %>

</form>

</core:demarcateTransaction>

</body>
</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/queueEntriesForClient.jsp#2 $$Change: 651448 $--%>

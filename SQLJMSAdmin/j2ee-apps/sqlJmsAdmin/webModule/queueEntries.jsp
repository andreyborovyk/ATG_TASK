<html>
  
<%@ include file="header.jspf" %>

<jsp:useBean id="queueEntryComparator" class="atg.sqljmsadmin.taglib.DMSQueueEntryComparator"/>

<head>
  <title>SQL-JMS Admin - Queue Entries</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- calculate navigation states --%>
<jsp:setProperty name="dmsContext" property="currentPage" value="queueEntries.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure that the queueId is set, if not, redirect back 
     to main page --%>
<core:urlParamValue id="queueId" param="queueId">
  <core:exclusiveIf>
    <core:ifNull value="<%= queueId.getValue() %>">
      <core:redirect url="main.jsp"/>
    </core:ifNull>
    <core:defaultCase>
      <dms:queue id="queue" queueId="<%= queueId.getValue() %>">
        <jsp:setProperty name="dmsContext" property="queue"
	                 value="<%= queue.getQueue() %>"/>
      </dms:queue>
    </core:defaultCase>
  </core:exclusiveIf>
</core:urlParamValue>

<%-- set the queue id in the navigation state --%>
<dms:setNavigationParam navigation="<%= currentNav %>" param="queueId"
                        paramValue="<%= dmsContext.getQueue().getId() %>"/>

<%-- handle deletions --%>
<core:ifNotNull value="<%= entryData.getAction() %>">
  <%@ include file="delete.jspf" %>
</core:ifNotNull>

<p>

<core:demarcateTransaction id="pageXA">

<%-- print the title --%>
<h1>Queue Entries for Queue: <%= dmsContext.getQueue().getName() %></h1>

<p>

<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>

<p>
<form method=POST action="queueEntries.jsp">    

<%-- print pending and unhandled queue entries --%>
<p>
<h3>Pending Queue Entries</h3>
<table border=1>
   <tr>
     <th>
     <th>Queue Id
     <th>Message Id
     <th>Handling Client
     <th>Type
     <th>Timestamp
     <th>
     <th>
  </tr>
  <core:sort id="pendingQueueEntriesSort" values="<%= dmsContext.getQueue().pendingMessages() %>"
             comparator="<%= queueEntryComparator %>">
  <core:forEach id="pendingQueueEntries"
               values="<%= pendingQueueEntriesSort.getSortedArray() %>"
	       elementId="queueEntry"
	       castClass="atg.sqljmsadmin.DMSQueueEntry">
    <tr>
      <td>
	<dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
	                           long2="<%= queueEntry.getId().getMessageId() %>">
	  <input type=checkbox name="entryIds" value="<%= encodeKey.getEncodedKey() %>">
        </dms:encodeCompoundKey>
      <td><%= queueEntry.getId().getQueueId() %>
      <td><%= queueEntry.getId().getMessageId() %>
      <td>
        <core:exclusiveIf>
	  <core:ifNotNull value="<%= queueEntry.getClient() %>">
            <%= queueEntry.getClient().getId() %> <%= queueEntry.getClient().getName() %>
	  </core:ifNotNull>
	  <core:defaultCase>
	    -
	  </core:defaultCase>
	</core:exclusiveIf>
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
	    <td><a href="<%= messageUrl %>">view message</a>
          </core:createUrl>
        </core:ifNotNull>
	<core:defaultCase>
	  <td>-
	  <td>-
	  <td>-
	</core:defaultCase>
      </core:exclusiveIf>

      <%-- move queue entry --%>

      <%--
      <core:createUrl id="moveQueueEntryUrl" url="queueEntries.jsp">
	<core:urlParam param="queueId" 
	             value="<%= queueEntry.getId().getQueueId() %>"/>
        <dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
                                  long2="<%= queueEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
        </dms:encodeCompoundKey>
	<core:urlParam param="action" value="<%= dmsContext.kMoveQueueEntry %>"/>
        <td><a href="<%= moveQueueEntryUrl %>">move</a>
      </core:createUrl>
      --%>

      <%-- delete queue entry --%>
      <core:createUrl id="deleteQueueEntryUrl" url="queueEntries.jsp">
	<core:urlParam param="queueId" 
           value="<%= queueEntry.getId().getQueueId() %>"/>
        <dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
                                long2="<%= queueEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
        </dms:encodeCompoundKey>
	<core:urlParam param="action" value="<%= dmsContext.kDeleteQueueEntry %>"/>
        <td><a href="<%= deleteQueueEntryUrl %>">delete</a>
      </core:createUrl>
    </tr>
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
<input type="hidden" name="queueId" value="<%= dmsContext.getQueue().getId() %>">
<input type="hidden" name="queueSource" value="true">

<%@ include file="pendingQueueEntryFormSubmit.jspf" %>

</form>


<form method=POST action="queueEntries.jsp">    
<p>
<h3>Unhandled Queue Entries</h3>

<table border=1>
  <tr>
    <th>
    <th>Queue Id
    <th>Message Id
    <th>Type
    <th>Timestamp
    <th>
    <th>
    <th>
  </tr>
  <core:sort id="unhandledQueueEntriesSort" 
             values="<%= dmsContext.getQueue().unhandledMessages() %>"
             comparator="<%= queueEntryComparator %>">
  <core:forEach id="unhandledQueueEntries"
              values="<%= unhandledQueueEntriesSort.getSortedArray() %>"
	      elementId="queueEntry"
	      castClass="atg.sqljmsadmin.DMSQueueEntry">
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
	                  value="<%= queueEntry.getMessage().getId().toString() %>"/>
	      <td><%= queueEntry.getMessage().getType() %>
	      <td>
	        <dms:dateFormat id="timestampDate"
		            date="<%= queueEntry.getMessage().getTimestamp() %>">
		  <%= timestampDate %>
		</dms:dateFormat>
	      <td><a href="<%= messageUrl %>">view message</a>
            </core:createUrl>
          </core:ifNotNull>
	  <core:defaultCase>
            <td>-
	    <td>-
	    <td>-
	  </core:defaultCase>
        </core:exclusiveIf>
      <core:createUrl id="moveQueueEntryUrl" url="queueEntries.jsp">
        <core:urlParam param="queueId" 
	           value="<%= queueEntry.getId().getQueueId() %>"/>
        <dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
                                  long2="<%= queueEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
        </dms:encodeCompoundKey>
        <core:urlParam param="action" value="<%= dmsContext.kMoveQueueEntry %>"/>
        <td><a href="<%= moveQueueEntryUrl %>">move</a>
      </core:createUrl>
      <core:createUrl id="deleteQueueEntryUrl" url="queueEntries.jsp">
        <core:urlParam param="queueId" 
	               value="<%= queueEntry.getId().getQueueId() %>"/>
        <dms:encodeCompoundKey id="encodeKey" long1="<%= queueEntry.getId().getQueueId() %>"
                                  long2="<%= queueEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
        </dms:encodeCompoundKey>
        <core:urlParam param="action" value="<%= dmsContext.kDeleteQueueEntry %>"/>
        <td><a href="<%= deleteQueueEntryUrl %>">delete</a>
      </core:createUrl>
    </tr>
  </core:forEach>
  </core:sort>
</table>

<p>

<input type="hidden" name="queueId" value="<%= dmsContext.getQueue().getId() %>">
<input type="hidden" name="queueSource" value="true">

<%@ include file="queueEntryFormSubmit.jspf" %>

</form>

</core:demarcateTransaction>

</body>

</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/queueEntries.jsp#2 $$Change: 651448 $--%>

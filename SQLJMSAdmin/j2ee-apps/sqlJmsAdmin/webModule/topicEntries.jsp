<html>
  
<%@ include file="header.jspf" %>

<jsp:useBean id="topicEntryComparator" class="atg.sqljmsadmin.taglib.DMSTopicEntryComparator"/>

<head>
  <title>SQL-JMS Admin - Topic Entries</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- calculate navigation states --%>
<jsp:setProperty name="dmsContext" property="currentPage" value="topicEntries.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure the topic subscription id is set --%>
<core:urlParamValue id="topicSubId" param="topicSubId">
  <core:exclusiveIf>
    <core:ifNull value="<%= topicSubId.getValue() %>">
      <core:redirect url="main.jsp"/>
    </core:ifNull>
    <core:defaultCase>
      <dms:topicSubscription id="topicSub" topicSubscriptionId="<%= topicSubId.getValue() %>">
        <jsp:setProperty name="dmsContext" property="topicSubscription" 
                     value="<%= topicSub.getTopicSubscription() %>"/>
      </dms:topicSubscription>
    </core:defaultCase>
  </core:exclusiveIf>
</core:urlParamValue>

<%-- set the topic subscription id in the navigation state --%>
<dms:setNavigationParam navigation="<%= currentNav %>" param="topicSubId"
                        paramValue="<%= dmsContext.getTopicSubscription().getId() %>"/>

<%-- handle deletions --%>
<core:ifNotNull value="<%= entryData.getAction() %>">
  <%@ include file="delete.jspf" %>
</core:ifNotNull>

<p>
<core:demarcateTransaction id="pageXA">

<%-- print the title --%>
<h1>Entries for Topic Subscription: <%= dmsContext.getTopicSubscription().getName() %></h1>
<h2>Topic: <%= dmsContext.getTopicSubscription().getTopic().getName() %></h2>

<core:exclusiveIf>
  <core:ifNotNull value="<%= dmsContext.getTopicSubscription().getClient() %>">
    <h2>Client: <%= dmsContext.getTopicSubscription().getClient().getId() %>
                <%= dmsContext.getTopicSubscription().getClient().getName() %>
    </h2>
  </core:ifNotNull>
  <core:defaultCase>
    <h2>Client: -
    </h2>
  </core:defaultCase>
</core:exclusiveIf>
<p>

<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>

<p>

<form method=POST action="topicEntries.jsp">

<%-- print a list of all the pending topic entries --%>
<h3>Pending Topic Entries</h3>
<table border=1>
  <tr>
    <th>
    <th>Topic Subscription Id
    <th>Message Id
    <th>Type
    <th>Timestamp
    <th>
    <th>
  </tr>
  <core:sort id="pendingTopicEntriesSort"
             values="<%= dmsContext.getTopicSubscription().pendingMessages() %>"
	     comparator="<%= topicEntryComparator %>">
  <core:forEach id="pendingTopicEntries"
                values="<%= pendingTopicEntriesSort.getSortedArray() %>"
		elementId="topicEntry"
		castClass="atg.sqljmsadmin.DMSTopicEntry">
    <tr>
      <td>
	<dms:encodeCompoundKey id="encodeKey" 
	                       long1="<%= topicEntry.getId().getSubscriptionId() %>"
	                       long2="<%= topicEntry.getId().getMessageId() %>">
	  <input type=checkbox name="entryIds" value="<%= encodeKey.getEncodedKey() %>">
        </dms:encodeCompoundKey>
      <td><%= topicEntry.getId().getSubscriptionId() %>
      <td><%= topicEntry.getId().getMessageId() %>
      <core:exclusiveIf>
	<core:ifNotNull value="<%= topicEntry.getMessage() %>">
	  <core:createUrl id="messageUrl"
	                  url="viewMessage.jsp">
	    <core:urlParam param="messageId" 
	                   value="<%= topicEntry.getMessage().getId().toString() %>"/>
	    <td><%= topicEntry.getMessage().getType() %>
	    <td>
	      <dms:dateFormat id="timestampDate"
	               date="<%= topicEntry.getMessage().getTimestamp() %>">
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
      <%--
      <core:createUrl id="moveTopicEntryUrl" url="topicEntries.jsp">
	<core:urlParam param="topicSubId" value="<%= topicEntry.getId().getSubscriptionId() %>"/>
	<core:urlParam param="action" value="<%= dmsContext.kMoveTopicEntry %>"/>
        <dms:encodeCompoundKey id="encodeKey"
	                       long1="<%= topicEntry.getId().getSubscriptionId() %>"
		               long2="<%= topicEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
	</dms:encodeCompoundKey>
        <td><a href="<%= moveTopicEntryUrl %>">move</a>
      </core:createUrl>
      --%>
      <core:createUrl id="deleteTopicEntryUrl" url="topicEntries.jsp">
	<core:urlParam param="topicSubId" value="<%= topicEntry.getId().getSubscriptionId() %>"/>
	<core:urlParam param="action" value="<%= dmsContext.kDeleteTopicEntry %>"/>
        <dms:encodeCompoundKey id="encodeKey"
	                       long1="<%= topicEntry.getId().getSubscriptionId() %>"
		               long2="<%= topicEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
	</dms:encodeCompoundKey>
        <td><a href="<%= deleteTopicEntryUrl %>">delete</a>
      </core:createUrl>
    </tr>
  </core:forEach>
  </core:sort>
</table>
<p>
<pre>
*Note: If a pending topic entry is deleted from an active SQL JMS system, there
       is still the possiblity of a receiver consuming the topic entry even after
       the topic entry has been deleted.
</pre>
<p>
<input type="hidden" name="topicSubId" value="<%= dmsContext.getTopicSubscription().getId() %>">
<input type="hidden" name="queueSource" value="false">

<%@ include file="pendingTopicEntryFormSubmit.jspf" %>

</form>
<p>

<form method=POST action="topicEntries.jsp">

<%-- print a list of all the unhandled topic entries --%>
<h3>Unhandled Topic Entries</h3>
<table border=1>
  <tr>
    <th>
    <th>Topic Subscription Id
    <th>Message Id
    <th>Type
    <th>Timestamp
    <th>
    <th>
  </tr>
  <core:sort id="unhandledTopicEntriesSort"
             values="<%= dmsContext.getTopicSubscription().unhandledMessages() %>"
	     comparator="<%= topicEntryComparator %>">
  <core:forEach id="unhandledTopicEntries"
                values="<%= unhandledTopicEntriesSort.getSortedArray() %>"
		elementId="topicEntry"
		castClass="atg.sqljmsadmin.DMSTopicEntry">
    <tr>
      <td>
	<dms:encodeCompoundKey id="encodeKey" 
	                       long1="<%= topicEntry.getId().getSubscriptionId() %>"
	                       long2="<%= topicEntry.getId().getMessageId() %>">
	  <input type=checkbox name="entryIds" value="<%= encodeKey.getEncodedKey() %>">
        </dms:encodeCompoundKey>
      <td><%= topicEntry.getId().getSubscriptionId() %>
      <td><%= topicEntry.getId().getMessageId() %>
        <core:exclusiveIf>
	  <core:ifNotNull value="<%= topicEntry.getMessage() %>">
	    <core:createUrl id="messageUrl"
	                    url="viewMessage.jsp">
	      <core:urlParam param="messageId" 
	                     value="<%= topicEntry.getMessage().getId().toString() %>"/>
	      <td><%= topicEntry.getMessage().getType() %>
	      <td>
	        <dms:dateFormat id="timestampDate"
		                date="<%= topicEntry.getMessage().getTimestamp() %>">
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
      <core:createUrl id="moveTopicEntryUrl" url="topicEntries.jsp">
	<core:urlParam param="topicSubId" value="<%= topicEntry.getId().getSubscriptionId() %>"/>
	<core:urlParam param="action" value="<%= dmsContext.kMoveTopicEntry %>"/>
        <dms:encodeCompoundKey id="encodeKey"
	                       long1="<%= topicEntry.getId().getSubscriptionId() %>"
		               long2="<%= topicEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
	</dms:encodeCompoundKey>
        <td><a href="<%= moveTopicEntryUrl %>">move</a>
      </core:createUrl>
      <core:createUrl id="deleteTopicEntryUrl" url="topicEntries.jsp">
	<core:urlParam param="topicSubId" value="<%= topicEntry.getId().getSubscriptionId() %>"/>
	<core:urlParam param="action" value="<%= dmsContext.kDeleteTopicEntry %>"/>
        <dms:encodeCompoundKey id="encodeKey"
	                       long1="<%= topicEntry.getId().getSubscriptionId() %>"
		               long2="<%= topicEntry.getId().getMessageId() %>">
	  <core:urlParam param="entryId" value="<%= encodeKey.getEncodedKey() %>"/>
	</dms:encodeCompoundKey>
        <td><a href="<%= deleteTopicEntryUrl %>">delete</a>
      </core:createUrl>
    </tr>
  </core:forEach>
  </core:sort>
</table>

<p>

<input type="hidden" name="topicSubId" value="<%= dmsContext.getTopicSubscription().getId() %>">
<input type="hidden" name="queueSource" value="false">

<%@ include file="topicEntryFormSubmit.jspf" %>

</form>

</core:demarcateTransaction>

</body>
</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/topicEntries.jsp#2 $$Change: 651448 $--%>

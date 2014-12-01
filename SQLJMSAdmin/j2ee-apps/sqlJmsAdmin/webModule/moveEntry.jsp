<html>
  
<%@ page errorPage="error.jsp" %>

<%@ taglib uri="/dmsAdminTaglib" prefix="dms" %>
<%@ taglib uri="/coreTaglib" prefix="core" %>
<jsp:useBean id="dmsContext" scope="request" class="atg.sqljmsadmin.taglib.DMSAdminContext"/>
<jsp:useBean id="navStack" scope="session" class="java.util.Stack"/>
<jsp:useBean id="currentNav" scope="session" class="atg.sqljmsadmin.taglib.DMSNavigationState"/>
<jsp:useBean id="entryData" scope="session" class="atg.sqljmsadmin.taglib.UpdateInfoBean"/>
<jsp:useBean id="moveData" scope="request" class="atg.sqljmsadmin.taglib.MoveEntriesFormBean"/>

<jsp:setProperty name="moveData" property="*"/>

<head>
  <title>SQL-JMS Admin - Select a new destination</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<%-- calculate navigation states --%>
<jsp:setProperty name="dmsContext" property="currentPage" value="moveEntry.jsp"/>
<%@ include file="navigation.jspf" %>

<%-- make sure the action has been set,
     if not, redirect to main page  --%>
<core:ifNull value="entryData.getAction() %>">
  <core:redirect url="main.jsp"/>  
</core:ifNull>

<%-- make sure the entry id or entry ids have data, 
     if not, redirect back to main page --%>
<core:test id="emptyTest" value="entryData.getEntryIds() %>">
  <core:if value="<%= emptyTest.isEmpty() %>">
    <core:redirect url="main.jsp"/>
  </core:if>
</core:test>

<%-- handle moves --%>
<core:if value="<%= moveData.isHandleForm() %>">
  <%@ include file="handleMove.jspf" %>
</core:if>

<h1>Select a new destination</h1>

<p>
<%-- create url back to previous page --%>
<%@ include file="backlink.jspf" %>

<p>

<core:demarcateTransaction id="pageXA">

<form method=POST action="moveEntry.jsp">

  <%-- list all the queues and topics --%>
  <h2>Queues</h2>
  <dms:admin id="queues">
    <core:exclusiveIf>
      <core:ifNull value="<%= queues.getQueues() %>">
        there are currently no queues
      </core:ifNull>
 
      <core:defaultCase>
        <table border=1>
	  <tr>
	    <th>
	    <th>Id
	    <th>Name
	    <th>Entries
          </tr>
	  <core:sort id="queueSort" values="<%= queues.getQueues() %>">
	    <core:orderBy property="name"/>
	  <core:forEach id="queuesForEach" values="<%= queueSort.getSortedArray() %>"
	                elementId="queue" castClass="atg.sqljmsadmin.DMSQueue">
	    <tr>
	      <td>
	        <dms:encodeCompoundKey id="encodeId" long1="<%= queue.getId() %>"
		                       long2="<%= new Long(0) %>">
	          <input type=radio name="destination" value="<%= encodeId.getEncodedKey() %>">
		</dms:encodeCompoundKey>
	      <td><%= queue.getId() %>
	      <td><%= queue.getName() %>
	      <td><%= queue.getQueueEntries().length %>
	    </tr>
          </core:forEach>
	  </core:sort>
	</table>
      </core:defaultCase>
    </core:exclusiveIf>
  </dms:admin>

  <h2>Topics</h2>
  <dms:admin id="topics">
    <core:exclusiveIf>
      <core:ifNull value="<%= topics.getTopics() %>">
        there are currently no topics
      </core:ifNull>
 
      <core:defaultCase>
        <table border=1>
	  <tr>
	    <th>
	    <th>Id
	    <th>Name
	    <th>Subscriptions
          </tr>
	  <core:sort id="topicSort" values="<%= topics.getTopics() %>">
	    <core:orderBy property="name"/>
	  <core:forEach id="topicsForEach" values="<%= topicSort.getSortedArray() %>"
	                elementId="topic" castClass="atg.sqljmsadmin.DMSTopic">
	    <tr>
	      <td>
	        <dms:encodeCompoundKey id="encodeId" long1="<%= topic.getId() %>"
		                       long2="<%= new Long(1) %>">
	          <input type=radio name="destination" value="<%= encodeId.getEncodedKey() %>">
		</dms:encodeCompoundKey>
	      <td><%= topic.getId() %>
	      <td><%= topic.getName() %>
	      <td><%= topic.getSubscriptions().length %>
	    </tr>
          </core:forEach>
	  </core:sort>
	</table>
      </core:defaultCase>
    </core:exclusiveIf>
  </dms:admin>

<p>

<input type="hidden" name="handleForm" value="true">
<input type="submit" name="move" value="Move Entries">

</form>

</core:demarcateTransaction>

</body>
</html>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/moveEntry.jsp#2 $$Change: 651448 $--%>

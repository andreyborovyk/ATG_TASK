<%-- 
Page:   	discussionFull.jsp
Gear:  	 	Discussion Gear
gearmode: 	content (the default mode)
displayMode: 	full

Author:      	Jeff Banister
Description: 	

  This is the full-page view of the discussion board gear.  This page serves as a 
  shell, and will render one of following views based on the "action" parameter:

  Topic list for a forum:	action="topic_list"
  Full thread list for a topic:	action="thread_list"
  Post message form:		action="post_message"
  Full forum listing:		action="forum_list"   or NULL  (default display)

 Note that the discussionPostForm page is included with a static include ( %@ include )
 vs the dynamic jsp:include.  This is done because this is a form which performs a
 redirection, which is illegal inside a dynamically included page.
--%>

<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>


<core:IfNotNull value='<%= request.getParameter("action")%>'>

   <core:ExclusiveIf>

      <core:If value='<%= request.getParameter("action").equals("topic_list")%>'> 
	<jsp:include page="/topicList.jsp" flush="true"/>
      </core:If>

      <core:If value='<%= request.getParameter("action").equals("post_message")%>'> 
	<%@ include file="discussionPostForm.jspf" %>
      </core:If> 

      <core:If value='<%= request.getParameter("action").equals("thread_list")%>'> 
	<jsp:include page="/threadList.jsp" flush="true"/>
      </core:If> 

      <core:DefaultCase> 
	<jsp:include page="/fullForumList.jsp" flush="true"/>
      </core:DefaultCase> 

   </core:ExclusiveIf>
</core:IfNotNull> 

<core:IfNull value='<%= request.getParameter("action")%>'> 
	<jsp:include page="/fullForumList.jsp" flush="true"/>
</core:IfNull> 


</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/discussionFull.jsp#2 $$Change: 651448 $--%>

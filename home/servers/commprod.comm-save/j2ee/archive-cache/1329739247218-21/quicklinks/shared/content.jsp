<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>

<core:demarcateTransaction id="quicklinksSharedPageXA">
<% try { %>
<paf:InitializeGearEnvironment id="pafEnv">

<%-- set a parameter so we can get to it without JSP escaping --%>
<dsp:setvalue param="paf" value="<%= pafEnv %>"/>
<%
String folderId = pafEnv.getGearInstanceParameter("linksFolderId");
if (folderId == null) folderId = "";

String bundleName = pafEnv.getGearInstanceParameter("resourceBundle");
%>
<i18n:bundle baseName="<%= bundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="noBookmarks" key="noBookmarks"/>
<i18n:message id="untitled" key="untitled"/>

<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
  <dsp:param name="repository" value="/atg/portal/gear/bookmarks/BookmarksRepository" />
  <dsp:param name="itemDescriptor" value="bookmark-folder"/>
  <dsp:param name="id" value="<%= folderId %>"/>
  <dsp:param name="queryRQL" value="id = :id"/>
  <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
  <dsp:oparam name="output">
    <dsp:getvalueof id="theFolder" idtype="atg.repository.RepositoryItem" param="element">
    <ul>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" value='<%= theFolder.getPropertyValue("children")%>'/>
      <font class="small">
      <dsp:oparam name="output">
<%
 String href = null;
 String name = null;

 atg.servlet.DynamoHttpServletRequest dynamoRequest = atg.servlet.ServletUtil.getDynamoRequest(request);
 atg.repository.RepositoryItem elementItem = (atg.repository.RepositoryItem)dynamoRequest.getObjectParameter("element");
 if(elementItem != null) {
   href = (String)elementItem.getPropertyValue("link"); 
   name = (String)elementItem.getPropertyValue("name");
   if((name == null) || (name.equals("")))
     name = href;
 }
 if(href != null) { 
%>
        <li><a href="<%= href %>"><%= name %></a><br>
<%
}
%>
      </dsp:oparam>
      </font>
      <dsp:oparam name="empty"><font class="small"><%= noBookmarks %></font></dsp:oparam>
    </dsp:droplet>
    </ul>
    </dsp:getvalueof>
  </dsp:oparam>
  <dsp:oparam name="empty"><font class="small"><%= noBookmarks %></font></dsp:oparam>
</dsp:droplet>

</paf:InitializeGearEnvironment>

<% } catch (Exception e) { %>
    <core:setTransactionRollbackOnly id="rollbackOnlyXA">
    <core:ifNot value="<%= rollbackOnlyXA.isSuccess() %>">
        Could not rollback transaction:
        <pre>
        <%= rollbackOnlyXA.getException() %>
        </pre>
    </core:ifNot>
    </core:setTransactionRollbackOnly>
<% } %>
</core:demarcateTransaction>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/quicklinks/quicklinks.war/shared/content.jsp#2 $$Change: 651448 $--%>

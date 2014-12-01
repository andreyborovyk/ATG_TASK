<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<%-- set a parameter so we can get to it without JSP escaping --%>
<dsp:setvalue param="paf" value="<%= pafEnv %>"/>

<%
String folderId = pafEnv.getGearInstanceParameter("linksFolderId");
if (folderId == null) folderId = "";
String bundleName = pafEnv.getGearInstanceParameter("resourceBundle");
%>
<i18n:bundle baseName="<%= bundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="shortNoBookmarks" key="shortNoBookmarks"/>
<i18n:message id="untitled" key="untitled"/>

<p>
<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
  <dsp:param name="repository" value="/atg/portal/gear/bookmarks/BookmarksRepository" />
  <dsp:param name="itemDescriptor" value="bookmark-folder"/>
  <dsp:param name="id" value="<%= folderId %>"/>
  <dsp:param name="queryRQL" value="id = :id"/>
  <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
  <dsp:oparam name="output">

    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="element.children"/>
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
        <a href="<%= atg.servlet.ServletUtil.escapeHtmlString(href) %>"><%= atg.servlet.ServletUtil.escapeHtmlString(name) %></a><br/>
<%
}
%>
      </dsp:oparam>
      <dsp:oparam name="empty"><b><%= shortNoBookmarks %></b></dsp:oparam>
    </dsp:droplet>

  </dsp:oparam>
  <dsp:oparam name="empty"><b><%= shortNoBookmarks %></b></dsp:oparam>
</dsp:droplet>
</p>

</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/quicklinks/quicklinks.war/shared/wml/content.jsp#2 $$Change: 651448 $--%>

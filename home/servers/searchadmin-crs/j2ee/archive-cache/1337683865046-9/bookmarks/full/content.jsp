<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:includeOnly/>
<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<%
String bmgFolderId = pafEnv.getGearUserParameter("bmgFolderId");
String bundleName = pafEnv.getGearInstanceParameter("resourceBundle");
%>
<i18n:bundle baseName="<%= bundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="noBookmarks" key="noBookmarks"/>
<i18n:message id="untitled" key="untitled"/>

<%-- full page --%>
<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
  <dsp:param name="repository" value="/atg/portal/gear/bookmarks/BookmarksRepository" />
  <dsp:param name="itemDescriptor" value="bookmark-folder"/>
  <dsp:param name="id" value="<%= bmgFolderId %>"/>
  <dsp:param name="queryRQL" value="id = :id"/>
  <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
  <dsp:oparam name="output">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="element.children"/>
      <dsp:oparam name="output">
       <font class="small"> <a href='<%=
             ((atg.repository.RepositoryItem) atg.servlet.ServletUtil.getDynamoRequest(request).getObjectParameter("element"))
            .getPropertyValue("link")
        %>'><dsp:valueof param="element.name"><%= untitled %></dsp:valueof></a></font><br>
      </dsp:oparam>
      <dsp:oparam name="empty"><%= noBookmarks %></dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="empty"><%= noBookmarks %></dsp:oparam>
</dsp:droplet>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/bookmarks/bookmarks.war/full/content.jsp#2 $$Change: 651448 $--%>

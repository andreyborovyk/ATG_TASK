<!-- Begin contacts Gear display -->
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gears.contacts.contacts" changeResponseLocale="false" />

<card id="contacts" title="<i18n:message key='search-title'/>">

<core:IfNotNull value='<%= request.getParameter("action") %>' >
  <core:ExclusiveIf>
    <core:If value='<%= request.getParameter("action").equals("profile") %>'>
      <jsp:include page="/wml/contactsProfile.jsp"flush="true" />
    </core:If>
    <core:If value='<%= request.getParameter("action").equals("search") %>' >
      <jsp:include page="/wml/contactsSearch.jsp" flush="true"/>
    </core:If>
    <core:DefaultCase>
      <jsp:include page="/wml/contactsError.jsp" flush="true" />
    </core:DefaultCase>
  </core:ExclusiveIf>
</core:IfNotNull>

<core:IfNull value='<%= request.getParameter("action") %>'>
  <jsp:include page="/wml/contactsError.jsp" flush="true" />
</core:IfNull>

</card>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/wml/fullContacts.jsp#2 $$Change: 651448 $--%>

<!-- Begin contacts Gear display -->
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<core:IfNotNull value='<%= request.getParameter("action") %>' >
  <core:ExclusiveIf>
    <core:If value='<%= request.getParameter("action").equals("profile") %>'>
      <jsp:include page="/html/contactsProfile.jsp"flush="true" />
    </core:If>
    <core:If value='<%= request.getParameter("action").equals("search") %>' >
      <jsp:include page="/html/contactsSearch.jsp" flush="true"/>
    </core:If>

  </core:ExclusiveIf>
</core:IfNotNull>

<core:IfNull value='<%= request.getParameter("action") %>'>
  <jsp:include page="/html/contactsError.jsp" flush="true" />
</core:IfNull>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/html/fullContacts.jsp#2 $$Change: 651448 $--%>

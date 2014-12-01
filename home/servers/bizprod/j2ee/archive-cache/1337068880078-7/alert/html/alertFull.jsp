<!-- Begin contacts Gear display -->
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>

<core:IfNotNull value='<%= request.getParameter("action") %>' >
  <core:ExclusiveIf>
    <core:If value='<%= request.getParameter("action").equals("search") %>' >
      <jsp:include page="/html/alertSearch.jsp" flush="true"/>
    </core:If>
    <core:DefaultCase>
      <jsp:include page="/html/alertSearch.jsp" flush="true" />
    </core:DefaultCase>
  </core:ExclusiveIf>
</core:IfNotNull>

<core:IfNull value='<%= request.getParameter("action") %>'>
  <jsp:include page="/html/alertSearch.jsp" flush="true" />
</core:IfNull>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/alert/alert.war/html/alertFull.jsp#1 $$Change: 651360 $--%>

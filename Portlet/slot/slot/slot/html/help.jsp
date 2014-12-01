<%--
  Help page for Slot portlet
  Display help information for slot portlet.
  
  @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/help.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page errorPage="/error.jsp"%>

<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="atg-dspjspEL"                  %>

<fmt:setBundle var="slotbundle" basename="atg.portlet.slot.slot"/>

<dspel:page>
<p class="help"><fmt:message key="help" bundle="${slotbundle}"/></p>
</dspel:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/help.jsp#2 $$Change: 651448 $--%>

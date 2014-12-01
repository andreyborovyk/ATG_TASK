<%--
  @param   mpv  A request scoped, MappedPropertyView item for this view
  @param   componentProperty
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ page import="java.io.*,java.util.*" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
 <%-- Use the string collection editor to edit a collection of dates --%>
 <dspel:include page="../string/simpleComponentEdit.jsp"/>
</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/date/calendarPopupComponentEdit.jsp#2 $$Change: 651448 $--%>

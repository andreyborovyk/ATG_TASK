<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>

<dsp:page>
<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
%>
<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="regionMap"                value="${page.regions}"/>

<center>
  <table border="0" width="96%" cellpadding="0" cellspacing="0">
    <tr>
      <td valign="top" width="75%">
        <c:set var="gears" value="${regionMap['75_25_Left'].gears}"/>
        <%@ include file="region.jspf" %>
      </td>

      <td>&nbsp;&nbsp;&nbsp;</td>

      <td valign="top" width="25%">
        <c:set var="gears" value="${regionMap['75_25_Right'].gears}"/>
        <%@ include file="region.jspf" %> 
      </td>
    </tr>
  </table>
</center>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/templates/layout/html/75_25.jsp#2 $$Change: 651448 $--%>

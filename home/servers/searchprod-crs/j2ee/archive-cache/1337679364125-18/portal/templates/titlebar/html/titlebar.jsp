<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setBundle var="templatesbundle" basename="atg.portal.templates"/>
<dsp:page>
<% 
  //Community
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  
  //Gear
  Gear gear = (Gear)request.getAttribute(Attribute.GEAR);
 
  //Edit
  boolean showEdit = false;
  boolean showAbout = false;
  boolean showHelp = false;
  if((cm != null) &&
     (gear != null)) {
    showEdit = cm.isAllowPersonalizedPages() && 
               gear.hasMode("userConfig","full");
    showAbout = gear.hasMode("about","full");
    showHelp = gear.hasMode("help","full");
  }
  pageContext.setAttribute("showEdit",new Boolean(showEdit));
  pageContext.setAttribute("showAbout",new Boolean(showAbout));
  pageContext.setAttribute("showHelp",new Boolean(showHelp));
 
  GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
  GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);  
  GearContextImpl gearContext  = null;
  if((gearServletResponse != null) &&
     (gearServletRequest != null)) {
    gearContext = new GearContextImpl(gearServletRequest);
   
    //Full Mode
    gearContext.setDisplayMode(DisplayMode.FULL);
  }    
%>
<c:set var="GEARSERVLETREQUEST"><%= Attribute.GEARSERVLETREQUEST %></c:set>
<c:set var="GEARSERVLETRESPONSE"><%= Attribute.GEARSERVLETRESPONSE %></c:set>
<c:set var="gearServletRequest"       value="${requestScope[GEARSERVLETREQUEST]}"/>
<c:set var="gearServletResponse"      value="${requestScope[GEARSERVLETRESPONSE]}"/>
<c:set var="community"                value="${gearServletRequest.community}"/>
<c:set var="page"                     value="${gearServletRequest.page}"/>
<c:set var="gearName"                 value="${gearServletRequest.gear.name}"/>
<c:set var="gearTitleTextColor"       value="#${page.colorPalette.gearTitleTextColor}"/>
<c:set var="gearTitleBackgroundColor" value="#${page.colorPalette.gearTitleBackgroundColor}"/>

<table border="0" width="100%" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" bgcolor="<c:out value="${gearTitleBackgroundColor}"/>" width="78%">
      <font color="<c:out value="${gearTitleTextColor}"/>"><c:out value="${gearName}"/></font>
    </td>
    <td align="right" bgcolor="<c:out value="${gearTitleBackgroundColor}"/>" width="22%">
      <c:if test="${showEdit ==true}">
<%
  gearContext.setGearMode(GearMode.USERCONFIG);
%>
        <a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI(),gearContext) %>"><font color="<c:out value="${gearTitleTextColor}"/>"><fmt:message key="titlebar-control-edit" bundle="${templatesbundle}"/></font></a>
      </c:if>&nbsp;
      <c:if test="${showAbout == true}">
<%
  gearContext.setGearMode(GearMode.ABOUT);
%>
        <a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI(),gearContext) %>"><font color="<c:out value="${gearTitleTextColor}"/>"><fmt:message key="titlebar-control-about" bundle="${templatesbundle}"/></font></a>  
      </c:if>&nbsp;
<%
  gearContext.setGearMode(GearMode.HELP);
%>
      <c:if test="${showHelp == true}">
        <a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI(),gearContext) %>"><font color="<c:out value="${gearTitleTextColor}"/>"><fmt:message key="titlebar-control-help" bundle="${templatesbundle}"/></font></a>
      </c:if>
    </td>
  </tr>
</table>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/templates/titlebar/html/titlebar.jsp#2 $$Change: 651448 $--%>

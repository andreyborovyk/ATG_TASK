<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<%
  GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
  GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);

  GearContextImpl gearContext  = null;
  if((gearServletResponse != null) &&
     (gearServletRequest != null)) 
    gearContext = new GearContextImpl(gearServletRequest);
%>

<i18n:bundle baseName="atg.gears.helloworld.helloworld" localeAttribute="userLocale" changeResponseLocale="false" />

<h3><i18n:message key="helloworld-shared"/></h3>
<%
  //Full Mode
  gearContext.setDisplayMode(DisplayMode.FULL);
%>
<p>
 <i18n:message key="clickhere">
    <% String newFullGearUrlString = "<a href=\"" + gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI(),gearContext) + "\">"; %>
    <i18n:messageArg value="<%= newFullGearUrlString %>"/>
    <i18n:messageArg value="</a>"/>
 </i18n:message>
</p>

<h3><i18n:message key="gear-environment"/></h3>
<ul>
 <li><i18n:message key="orig-request-uri-label"/><%= gearServletRequest.getPortalRequestURI() %></li>
 <li><i18n:message key="community-label"/><%= gearServletRequest.getCommunity() %></li>
 <li><i18n:message key="page-label"/><%= gearServletRequest.getPage() %></li>
 <li><i18n:message key="displaymode-label"/><%= gearServletRequest.getDisplayMode() %></li>
 <li><i18n:message key="gear-label"/><%= gearServletRequest.getGear() %></li>
</ul>

</dsp:page>


<%-- @version $Id: //app/portal/version/10.0.3/helloworld/helloworld.war/html/sharedHelloWorld.jsp#1 $$Change: 651360 $--%>

<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" contentType="text/vnd.wap.wml" %>
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
<p mode="nowrap">
  <big>
    <b><i18n:message key="helloworld-shared"/></b>
  </big>
</p>


<%
  //Full Mode
  gearContext.setDisplayMode(DisplayMode.FULL);
%>
<p mode="nowrap">
  <i18n:message key="clickhere">
    <% String newFullGearUrlString = "<a href=\"" + gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI(),gearContext) + "\">"; %>
    <i18n:messageArg value="<%= newFullGearUrlString %>"/>
    <i18n:messageArg value="</a>"/>
  </i18n:message>
</p>


<p mode="nowrap">
  <b><i18n:message key="gear-environment"/> </b>
  <br/>
  <table columns="2">
    <tr><td><i18n:message key="orig-request-uri-label"/></td><td><i18n:message key="orig-request-uri-label"/><%= gearServletRequest.getPortalRequestURI() %></td></tr>
    <tr><td><i18n:message key="community-label"/></td><td><%= gearServletRequest.getCommunity() %></td></tr>
    <tr><td><i18n:message key="page-label"/></td><td><%= gearServletRequest.getPage() %></td></tr>
    <tr><td><i18n:message key="displaymode-label"/></td><td><%= gearServletRequest.getDisplayMode() %></td></tr>
    <tr><td><i18n:message key="gear-label"/></td><td><%= gearServletRequest.getGear() %></td></tr>
  </table>
</p>

</dsp:page>



<%-- @version $Id: //app/portal/version/10.0.3/helloworld/helloworld.war/wml/sharedHelloWorld.jsp#1 $$Change: 651360 $--%>

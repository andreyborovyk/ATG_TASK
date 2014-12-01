<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" contentType="text/vnd.wap.wml" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<%
  GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
  GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>

<i18n:bundle baseName="atg.gears.helloworld.helloworld" localeAttribute="userLocale" changeResponseLocale="false" />

<card id="gear<%= gearServletRequest.getGear().getId() %>" title="<%= gearServletRequest.getGear().getName(response.getLocale()) %>">

 <p mode="nowrap" align="center">
  <big>
   <b><i18n:message key="helloworld-about"/></b>
  </big>
 </p>

 <p mode="wrap">
  <i18n:message key="helloworld-description"/>
 </p>

</card>

</dsp:page>


<%-- @version $Id: //app/portal/version/10.0.3/helloworld/helloworld.war/wml/aboutHelloWorld.jsp#1 $$Change: 651360 $--%>

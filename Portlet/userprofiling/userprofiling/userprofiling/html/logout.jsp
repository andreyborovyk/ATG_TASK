<%@ page import="javax.portlet.*,,atg.portal.servlet.*" errorPage="/error.jsp"%>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setBundle basename="atg.portlet.userprofiling.userprofiling"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%
  RenderRequest renderRequest = (RenderRequest)request.getAttribute("javax.portlet.request");
  RenderResponse renderResponse = (RenderResponse)request.getAttribute("javax.portlet.response");
  PortalServletResponse portalServletResponse = (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);  
  String actionURL = response.encodeURL(request.getRequestURI());
  String successURL = portalServletResponse.encodePortalURL(portalServletRequest.getPortalContextPath() + portalServletRequest.getPage().getPageURI());  
  String errorURL = portalServletResponse.encodePortalURL(portalServletRequest.getPortalContextPath() + portalServletRequest.getPage().getPageURI());
%>
<dsp:page>

<%-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

  <br /><br />
  <blockquote>
  <dsp:droplet name="Switch">
    <dsp:param name="value" bean="ProfileFormHandler.profile.transient"/>
    <dsp:oparam name="false">


     <dsp:form action="<%= actionURL %>" method="post">
        <dsp:input type="hidden" bean="ProfileFormHandler.logoutSuccessURL" value="<%= successURL %>"/>
        <dsp:input type="hidden" bean="ProfileFormHandler.logoutErrorURL" value="<%= errorURL %>"/>
        <dsp:input type="hidden" bean="ProfileFormHandler.expireSessionOnLogout" value="true"/>

        <dsp:droplet name="Switch">
          <dsp:param name="value" bean="ProfileFormHandler.formError"/>
          <dsp:oparam name="true">
  
            <font color="cc0000"><strong><ul>
            <dsp:droplet name="ProfileErrorMessageForEach">
              <dsp:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
              <dsp:oparam name="output">
	           <li><dsp:getvalueof id="msg" idtype="java.lang.String" param="message"><%= msg %></dsp:getvalueof></li>
              </dsp:oparam>
            </dsp:droplet>
            </ul></strong></font>

          </dsp:oparam>
        </dsp:droplet>

       <fmt:message key="logout-message-goodbye"/>
       <br /><br />
       <fmt:message var="logoutbutton" key="logout-button-submit"/>
       <dsp:input type="submit" value="<%= pageContext.findAttribute("logoutbutton") %>" bean="ProfileFormHandler.logout"/>
          

      </dsp:form>


    </dsp:oparam>
    <dsp:oparam name="default">

       
      
    </dsp:oparam>
  </dsp:droplet>
 <blockquote>
 
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/userprofiling/userprofiling/html/logout.jsp#2 $$Change: 651448 $--%>

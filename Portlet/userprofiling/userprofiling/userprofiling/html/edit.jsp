<%@ page import="javax.portlet.*,atg.portal.servlet.*" errorPage="/error.jsp"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<%--
     This page is an edit form, which uses the ProfileFormHandler to edit a new 
     userid. This page is intended to serve as the default sample edit page, and can be
     updated or replace for a given portal implementation.          
--%>

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

  <br /><br />
  <blockquote>
  <dsp:getvalueof id="profile" idtype="atg.userprofiling.Profile" bean="Profile">
      <core:if value="<%= !profile.isTransient() %>">

        <dsp:form action="<%= actionURL %>" method="post">
          <dsp:input type="hidden" bean="ProfileFormHandler.updateSuccessURL" value="<%= successURL %>"/>
          <dsp:input type="hidden" bean="ProfileFormHandler.updateErrorURL" value="<%= errorURL %>"/>
       
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

          <%--
           This ensures that we update the same profile that we edit.
           It guards against the session expiring while this form is
           displayed (and updating the anonymous profile) or the user
           logging out and in again as a different user in a different
           window
          --%>
          <dsp:input bean="ProfileFormHandler.updateRepositoryId" beanvalue="ProfileFormHandler.repositoryId" type="hidden"/>
          <table width="100%" border="0">
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-firstName"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="35" bean="ProfileFormHandler.value.firstName"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-lastName"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.lastName"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-email"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.email"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-address1"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.address1"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-address2"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.address2"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-city"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.city"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-state"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.state"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-postalCode"/>:</td>
              <td><dsp:input type="text" size="10" maxsize="10" bean="ProfileFormHandler.value.homeAddress.postalCode"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-country"/>:</td>
              <td><dsp:input type="text" size="30" maxsize="30" bean="ProfileFormHandler.value.homeAddress.country"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-phoneNumber"/>:</td>
              <td><dsp:input type="text" size="20" maxsize="30" bean="ProfileFormHandler.value.homeAddress.phoneNumber"/></td>
            </tr>
            <tr>
              <td valign="middle" align="right"><fmt:message key="edit-label-dateOfBirth"/>:</td>
              <td><dsp:input type="text" size="10" maxsize="10" date="M/dd/yyyy" bean="ProfileFormHandler.value.dateOfBirth"/></td>
            </tr>

            <fmt:message var="editbutton" key="edit-button-submit"/>
            <tr>
              <td valign="middle" align="right"></td>
              <td><dsp:input type="submit" value="<%= pageContext.findAttribute("editbutton") %>" bean="ProfileFormHandler.update"/></td>
            </tr>
          </table>

        </dsp:form>

      </core:if>   
   
  </dsp:getvalueof>
  </blockquote>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/userprofiling/userprofiling/html/edit.jsp#2 $$Change: 651448 $--%>

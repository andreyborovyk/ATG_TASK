<%@ page import="javax.portlet.*" errorPage="/error.jsp"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<%
 RenderRequest renderRequest = (RenderRequest)request.getAttribute("javax.portlet.request");
 RenderResponse renderResponse = (RenderResponse)request.getAttribute("javax.portlet.response");
 String viewName = renderRequest.getParameter("view");
%>
<fmt:setBundle basename="atg.portlet.userprofiling.userprofiling"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:page>

<dsp:getvalueof id="profile" idtype="atg.userprofiling.Profile" bean="Profile">
  <core:exclusiveIf>
      <core:if value="<%= !profile.isTransient() %>">
<%
 PortletURL welcomeURI = renderResponse.createRenderURL();
 PortletURL logoutURI = renderResponse.createRenderURL();
 logoutURI.setParameter("view","logout");
 PortletURL editURI = renderResponse.createRenderURL();
 editURI.setParameter("view","edit");
%>

        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td align="right" valign="top">
<% if("logout".equals(viewName)) { %>             
              <a href="<%= editURI %>"><fmt:message key="edit-link"/></a>&nbsp;
              <a href="<%= welcomeURI %>"><fmt:message key="welcome-link"/></a>
<% } else if("edit".equals(viewName)) { %>
              <a href="<%= welcomeURI %>"><fmt:message key="welcome-link"/></a>&nbsp;             
              <a href="<%= logoutURI %>"><fmt:message key="logout-link"/></a>
<% } else { %>
              <a href="<%= editURI %>"><fmt:message key="edit-link"/></a>&nbsp;
              <a href="<%= logoutURI %>"><fmt:message key="logout-link"/></a>
<% } %>
            </td>
          </tr>
        </table>

        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td align="left" valign="top">
<% if("logout".equals(viewName)) { %>
              <jsp:include page="logout.jsp"/>
<% } else if("edit".equals(viewName)) { %>
              <jsp:include page="edit.jsp"/>
<% } else { %>
              <fmt:message key="welcome-message"/>&nbsp;<dsp:getvalueof id="login" idtype="java.lang.String" bean="Profile.login"><%= login %></dsp:getvalueof>
<% } %>
            </td>
          </tr>
        </table>

      </core:if>   
      <core:defaultCase>

<%
 PortletURL loginURI = renderResponse.createRenderURL();
 PortletURL registerURI = renderResponse.createRenderURL();
 registerURI.setParameter("view","register");
%>

        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td align="right" valign="top">
<% if("register".equals(viewName)) { %>            
              <a href="<%= loginURI %>"><fmt:message key="login-link"/></a>
<% } else { %>                        
              <a href="<%= registerURI %>"><fmt:message key="register-link"/></a>
<% } %>
            </td>
          </tr>
        </table>
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td align="left" valign="top">
<% if("register".equals(viewName)) { %>
              <jsp:include page="register.jsp"/>
<% } else { %>
              <jsp:include page="login.jsp"/>
<% } %>
            </td>
          </tr>
        </table>

      </core:defaultCase>
  </core:exclusiveIf>
</dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/portlet/userprofiling/userprofiling/html/index.jsp#2 $$Change: 651448 $--%>

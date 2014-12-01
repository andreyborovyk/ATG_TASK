<%@ page import="java.io.*,java.util.*,java.util.Enumeration,atg.portal.servlet.*" errorPage="/error.jsp" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<%
 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>
<form>
  <textarea cols="50" rows="20" >
==================
HttpServletRequest
==================
class: <%= request.getClass() %>
remoteAddr: <%= request.getRemoteAddr() %>
remoteHost: <%= request.getRemoteHost() %>
serverName: <%= request.getServerName() %>
serverPort: <%= request.getServerPort() %>
scheme: <%= request.getScheme() %>
protocol: <%= request.getProtocol() %>
contentType: <%= request.getContentType() %>
contentLength: <%= request.getContentLength() %>
characterEncoding: <%= request.getCharacterEncoding() %>
locale: <%= request.getLocale() %>
isSecure: <%= request.isSecure() %>

parameters:
<%
  Enumeration paramNames = request.getParameterNames();
  while(paramNames.hasMoreElements()) {
    String name = (String)paramNames.nextElement();
    String [] values =  request.getParameterValues(name);
    out.print(" " + name + "=[");
    for(int i = 0 ; i < values.length ; i++) {
      out.print(values[i]);
      if(i != (values.length - 1))
        out.print(",");
    }
      out.println("]");
  }
%>

attributes:
<%
  Enumeration attrNames = request.getAttributeNames();
  while(attrNames.hasMoreElements()) {
    String name = (String)attrNames.nextElement();
    Object value =  request.getAttribute(name);
    String valueString = null;
    if(value != null) {
      if((value instanceof String) ||
         (value instanceof Number) ||
         (value instanceof Boolean) ||
         (value instanceof Character) ||
         (value instanceof Class))
        valueString = value.toString();
      else
        valueString = "class:" + value.getClass().getName();
    }
    out.println(" " + name + "=[" + valueString + "]");
  }
%>

contextPath: <%= request.getContextPath() %>
servletPath: <%= request.getServletPath() %>
pathInfo: <%= request.getPathInfo() %>
pathTranslated: <%= request.getPathTranslated() %>
requestURI: <%= request.getRequestURI() %>
queryString: <%= request.getQueryString() %>
sessionId: <%= request.getSession().getId() %>
requestedSessionId: <%= request.getRequestedSessionId() %>
isRequestedSessionIdFromCookie: <%= request.isRequestedSessionIdFromCookie() %>
isRequestedSessionIdFromURL: <%= request.isRequestedSessionIdFromURL() %>
isRequestedSessionValid: <%= request.isRequestedSessionIdValid() %>
method: <%= request.getMethod() %>
authType: <%= request.getAuthType() %>
remoteUser: <%= request.getRemoteUser() %>
userPrincipal: <%= request.getUserPrincipal() %>

headers:
<%
  Enumeration headerNames = request.getHeaderNames();
  while(headerNames.hasMoreElements()) {
    String name = (String)headerNames.nextElement();
    String value =  request.getHeader(name);
    out.println(" " +  name + "=[" + value + "]");
   } 
%> 

cookies:
<%
  Cookie [] cookies = request.getCookies();
  if(cookies != null) {
    for(int i = 0 ; i < cookies.length ; i++) {
     out.println(" " + cookies[i].getName() + "=[" + cookies[i].getValue() + "]");
    }
  }
%>
  </textarea>
</form>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/portlets/servlet-dump/content/html/content.jsp#2 $$Change: 651448 $--%>

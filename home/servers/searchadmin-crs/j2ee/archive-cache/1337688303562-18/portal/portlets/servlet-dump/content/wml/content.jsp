<%@ page contentType="text/vnd.wap.wml" import="java.io.*,java.util.*,java.util.Enumeration,atg.portal.servlet.*" errorPage="/error.jsp" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<%
 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>
<p mode="nowrap">
    =====================================================<br/>
    HttpServletRequest<br/>
    =====================================================<br/>
class: <%= request.getClass() %><br/>
remoteAddr: <%= request.getRemoteAddr() %><br/>
remoteHost: <%= request.getRemoteHost() %><br/>
serverName: <%= request.getServerName() %><br/>
serverPort: <%= request.getServerPort() %><br/>
scheme: <%= request.getScheme() %><br/>
protocol: <%= request.getProtocol() %><br/>
contentType: <%= request.getContentType() %><br/>
contentLength: <%= request.getContentLength() %><br/>
characterEncoding: <%= request.getCharacterEncoding() %><br/>
locale: <%= request.getLocale() %><br/>
isSecure: <%= request.isSecure() %><br/>
parameters:<br/>
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
      out.println("]<br/>");
  }
%>
attributes:<br/>
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
    out.println(" " + name + "=[" + valueString + "]<br/>");
  }
%>
contextPath: <%= request.getContextPath() %><br/>
servletPath: <%= request.getServletPath() %><br/>
pathInfo: <%= request.getPathInfo() %><br/>
pathTranslated: <%= request.getPathTranslated() %><br/>
requestURI: <%= request.getRequestURI() %><br/>
queryString: <%= request.getQueryString() %><br/>
sessionId: <%= request.getSession().getId() %><br/>
requestedSessionId: <%= request.getRequestedSessionId() %><br/>
isRequestedSessionIdFromCookie: <%= request.isRequestedSessionIdFromCookie() %><br/>
isRequestedSessionIdFromURL: <%= request.isRequestedSessionIdFromURL() %><br/>
isRequestedSessionValid: <%= request.isRequestedSessionIdValid() %><br/>
method: <%= request.getMethod() %><br/>
authType: <%= request.getAuthType() %><br/>
remoteUser: <%= request.getRemoteUser() %><br/>
userPrincipal: <%= request.getUserPrincipal() %><br/>
headers:<br/>
<%
  Enumeration headerNames = request.getHeaderNames();
  while(headerNames.hasMoreElements()) {
    String name = (String)headerNames.nextElement();
    String value =  request.getHeader(name);
    out.println(" " + name + "=[" + value + "]<br/>");
  } 
%> 
cookies:
<%
  Cookie [] cookies = request.getCookies();
  if(cookies != null) {
    for(int i = 0 ; i < cookies.length ; i++) {
      out.println(" " + cookies[i].getName() + "=[" + cookies[i].getValue() + "]<br/>");
    }
  }
%>
</p>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/portlets/servlet-dump/content/wml/content.jsp#2 $$Change: 651448 $--%>

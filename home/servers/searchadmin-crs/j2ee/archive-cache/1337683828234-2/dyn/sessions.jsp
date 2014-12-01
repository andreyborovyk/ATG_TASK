<%@ page import="atg.servlet.*,atg.servlet.sessiontracking.*,java.util.*,atg.nucleus.*" %>
<h1>System Nucleus Session Contexts</h1>
<TABLE width="650" border="1">
<TR><TH>Session Object</TH><TH>Id</TH><TH>Creation Time</TH><TH>Last Accessed</TH><TH>ServletContext</TH><TH>Name Context</TH><TH>NameContext ID</TH><TH>Bound to Session?</TH><TH>Restored?</TH><TH>Child Apps</TH></TR>
<%
// Use this for post ATG 6.0.0 versions
Nucleus nSystem = Nucleus.getSystemNucleus();
// Use this line for ATG 6.0.0 and earlier
Nucleus nWebApp = Nucleus.getGlobalNucleus();
GenericSessionManager manager = (GenericSessionManager)nSystem.resolveName("/atg/dynamo/servlet/sessiontracking/GenericSessionManager");

Enumeration sessions = manager.listValidSessions();
while (sessions.hasMoreElements()) {
 try {
 HttpSession s = (HttpSession) sessions.nextElement();
 SessionNameContext context = (SessionNameContext) manager.getElement(s.getId());
 String color = "white";
 if (context.mRestored) color="yellow";
 try {
   s.getCreationTime();
 } catch (IllegalStateException e) {
   // Signal an expired session with red
   color="red";
 }
 try {
 out.println("<TR bgcolor="+color +"><TD>" + s + "</TD>");
 out.print("<TD>" + s.getId() +"</TD>");
 try {
   out.print("<TD>" + new java.util.Date(s.getCreationTime()) +"</TD>");
  } catch (IllegalStateException e) {
   out.print("<TD> EXPIRED</TD>");
 }
   out.print("<TD>" + new java.util.Date(s.getLastAccessedTime()) +"</TD>");
 out.println("<TD>" + s.getServletContext()+ "</TD>");
 out.println("<TD>" + context+ "</TD>");
 out.println("<TD>" + context.getSessionId()+ "</TD>");
 out.println("<TD>" + context.mBoundToSession+ "</TD>");
 out.println("<TD>" + context.mRestored+ "</TD>");
 out.println("<TD>");
 if (context != null) {
  Iterator iter = context.mChildServletContexts.iterator();
  out.print("<UL>");
  while (iter.hasNext())
  out.print("<LI>" + iter.next());
  out.print("</UL>");
 }
 out.println("</TD>");
 out.println("</TR>");
 } catch (Exception e) {
 out.println("<TR><TD>" + s + "</TD><TD>" +e.getMessage() +"</TD></TR>");
 }

 s = null;
 context = null;
 } catch (Exception e) {
  out.println("<TR><TD><B>e.getMessage()</B></TD></TR>");
}
}
sessions = null;
%>
</TABLE>

<HR>

<h1>Web App Nucleus Session Contexts</h1>
<TABLE width="650" border="1">
<TR><TH>Session Object</TH><TH>Id</TH><TH>Creation Time</TH><TH>Last Accessed</TH><TH>ServletContext</TH><TH>Name Context</TH><TH>NameContext ID</TH><TH>Bound to Session?</TH><TH>Restored?</TH></TR>
<%

GenericSessionManager managerWebApp = (GenericSessionManager)nWebApp.resolveName("/atg/dynamo/servlet/sessiontracking/GenericSessionManager");

Enumeration webAppSessions = managerWebApp.listValidSessions();
while (webAppSessions.hasMoreElements()) {
 HttpSession s = (HttpSession) webAppSessions.nextElement();
 SessionNameContext context = (SessionNameContext) managerWebApp.getElement(s.getId());
 String color = "white";
 if (context.mRestored) color="yellow";
 try {
   s.getCreationTime();
 } catch (IllegalStateException e) {
   // Signal an expired session with red
   color="red";
 }
 try {
 out.println("<TR bgcolor="+color +"><TD>" + s + "</TD>");
 out.print("<TD>" + s.getId() +"</TD>");
 try {
   out.print("<TD>" + new java.util.Date(s.getCreationTime()) +"</TD>");
  } catch (IllegalStateException e) {
   out.print("<TD> EXPIRED</TD>");
 }
   out.print("<TD>" + new java.util.Date(s.getLastAccessedTime()) +"</TD>");
 out.println("<TD>" + s.getServletContext()+ "</TD>");
 out.println("<TD>" + context+ "</TD>");
 out.println("<TD>" + context.getSessionId()+ "</TD>");
 out.println("<TD>" + context.mBoundToSession+ "</TD>");
 out.println("<TD>" + context.mRestored+ "</TD>");
 out.println("</TR>");
 } catch (Exception e) {
 out.println("<TR><TD>" + s + "</TD><TD>" +e.getMessage() +"</TD></TR>");
 }

 s = null;
 context = null;
}
webAppSessions = null;
%>
</TABLE>
<%-- @version $Id: //product/DAS/version/10.0.3/templates/DAF/j2ee-apps/dyn/sessions.jsp#2 $$Change: 651448 $--%>

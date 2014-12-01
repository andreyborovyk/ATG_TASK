<%@ page import="atg.servlet.*"%>
<%@ page import="atg.nucleus.*"%>
<%
   ServletUtil.getSessionNameContext(null,request.getSession(),Nucleus.getGlobalNucleus());
   request.setAttribute(ServletUtil.PARENT_SESSION_ID_ATTR,request.getSession().getId());
   ServletContext ctx = pageContext.getServletContext();
   ServletContext other = ctx.getContext("/sshare");
   RequestDispatcher rd = other.getRequestDispatcher("/test.jsp");
   rd.forward(request, response);
%>
<%-- @version $Id: //product/DAS/version/10.0.3/templates/DAF/j2ee-apps/dyn/forward.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@page import="java.io.*,java.util.*,javax.servlet.jsp.*,java.net.*,java.io.*,java.util.*,atg.portal.framework.*" %>

<%
    request.setAttribute(RequestAttributes.PORTAL_REPOSITORY_LOCATION,
             "dynamo:/atg/portal/framework/PortalRepository");
    request.setAttribute(RequestAttributes.GEAR, request.getParameter("paf_gear_id"));
%>
<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<%
    String hostURL = gearEnv.getGearInstanceParameter("hostURL");
    String userID = gearEnv.getGearUserParameter("userID");
    String password = gearEnv.getGearUserParameter("password");

    URL url = null;
    String proto = null;
    String host = null;
    String path = null;
    int port = -1;
    try
    {
        url = new URL(hostURL);
    }
    catch (MalformedURLException mue)
    {
        throw new javax.servlet.jsp.JspException("The configured Outlook Web Access URL is invalid.  Please contact your Portal Administrator to have it fixed.");
    }
    proto = url.getProtocol();
    host = url.getHost();
    path = url.getPath();
    port = url.getPort();

    response.addHeader("Window-target", "_blank");
    if (userID != null && !userID.equals(""))
    {
        StringBuffer meta = new StringBuffer("<meta http-equiv=\"Refresh\" content=\"0; url=");
        meta.append(proto);
        meta.append("://");
        meta.append(userID);
        meta.append(":");
        meta.append(password);
        meta.append("@");
        meta.append(host);
        if (port != -1)
        {
            meta.append(":");
            meta.append(new Integer(port).toString());
        }
        //meta.append("/");
        if (path != null && !path.equals(""))
        {
            meta.append(path);
            meta.append("/");
        }
        meta.append(userID);
        meta.append("/");
        meta.append(request.getParameter("content"));
        meta.append("\">");
        response.getWriter().print(meta.toString());
    }
    else
    {
        StringBuffer meta = new StringBuffer("<meta http-equiv=\"Refresh\" content=\"60; url=");
        meta.append(proto);
        meta.append("//");
        meta.append(host);
        meta.append("/");
        if (path != null && !path.equals(""))
        {
            meta.append(path);
        }
        meta.append("\">");
        response.getWriter().print(meta.toString());
    }

%>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/exchange-passthrough/exchange.war/proxy.jsp#1 $$Change: 651360 $--%>

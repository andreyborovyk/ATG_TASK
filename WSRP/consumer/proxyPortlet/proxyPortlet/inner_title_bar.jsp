<%--
Inner title bar for showing up the links for window states and portlet modes.
The title bar will be rendered for each proxy-portlet instance and can have different links
as per the portlet description corresponding to the current proxy portlet for which it is being rendered.
--%>

<%@ page import="oasis.names.tc.wsrp.v1.types.PortletDescription,
                 oasis.names.tc.wsrp.v1.types.MarkupType,
                 atg.apache.wsrp4j.consumer.WSRPPortlet,
                 javax.portlet.WindowState,
                 atg.apache.wsrp4j.util.WindowStates,
                 javax.portlet.PortletMode,
                 atg.apache.wsrp4j.util.Modes,
                 javax.portlet.WindowStateException,
                 javax.portlet.PortletModeException,
                 java.io.IOException" %>


<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>


<%-- as we would be using renderRequest and renderResponse objects make sure we define them --%>
<portlet:defineObjects/>

<%
    String contentType = (String)renderRequest.getAttribute("contentType");
    PortletDescription portletDescription = (PortletDescription)renderRequest.getAttribute("portletDescription");
    WSRPPortlet portlet = (WSRPPortlet)renderRequest.getAttribute("portlet");

    /** The supported Portlet modes (viz. edit_defaults, about etc.) should be fetched from the Portlet description
     *  stored in repository and the links should be created accordingly. So, if about mode is not supported by the
     *  current portlet then it should not be created & displayed.
     */
    // TODO instead of text we could use image icons for the various links in the inner title bar

    // table cells & the action URLs will be created based on the window states & portlet modes supported by portlet
    boolean minimizeAvailable=false, maximizeAvailable=false;
    boolean supportsHelp=false, supportsEdit=false, supportsAbout=false;

    MarkupType [] markupTypes = portletDescription.getMarkupTypes();
    for(int i=0; i<markupTypes.length; i++) {
        if(markupTypes[i].getMimeType().equals(contentType))
        {
            String [] windowStates = markupTypes[i].getWindowStates();
            String [] portletModes  = markupTypes[i].getModes();
            //check for supported window states
            for(int j=0; j<windowStates.length; j++) {

                // skip if we have a custom window state as we are not supporting any custom window states
                if(!windowStates[j].startsWith("wsrp:"))
                    continue;

                if(WindowState.MINIMIZED.equals(
                        WindowStates.getJsrPortletStateFromWsrpState(WindowStates.fromString(windowStates[j]))
                )) {
                    minimizeAvailable = true;
                }
                if(WindowState.MAXIMIZED.equals(
                        WindowStates.getJsrPortletStateFromWsrpState(WindowStates.fromString(windowStates[j]))
                )) {
                    maximizeAvailable = true;
                }
            }
            //check for supported portlet modes
            for(int j=0; j<portletModes.length; j++) {

                // skip if we have a custom portlet mode as we are not supporting any custom portlet mode
                if(!portletModes[j].startsWith("wsrp:"))
                    continue;

                try {
                    if (PortletMode.EDIT.equals(Modes.getJsrPortletModeFromWsrpMode(Modes.fromValue(portletModes[j]))
                    )) {
                        supportsEdit = true;
                    }
                } catch (IllegalStateException iex) {
                }
                try {
                    if (PortletMode.HELP.equals(Modes.getJsrPortletModeFromWsrpMode(Modes.fromValue(portletModes[j]))
                    )) {
                        supportsHelp = true;
                    }
                } catch (IllegalStateException iex) {
                }
            }
            // no need to go further as we got the MarkupType for the current content type
            break;
        }
    }

%>


<table bgcolor="#cccccc" border="1" width="100%" cellpadding="0" cellspacing="0">
<tr>
    <td width="60%">
    <font color="#000000" size="2"><b>
<%-- give portlet's actual title on left of the inner title bar --%>
 <c:choose>             <%-- start checking for the display name --%>
    <c:when test="${portletDescription.displayName == null}" >
    <%--display name is null so look for the short title --%>
        <c:choose>      <%-- start checking for short title --%>
        <c:when test="${portletDescription.shortTitle == null}">
        <%--short title is also null so look for the title --%>
            <c:choose>      <%-- start checking for title --%>
            <c:when test="${portletDescription.title == null}">
               <%-- TODO handle the case when display name, title and short title all are null --%>
               <%-- what to do ??? display name, title, short title all are null --%>
            </c:when>
            <c:otherwise>
            <%-- title is present so show title --%>
            <c:out value="${portletDescription.title.value}"/>
            </c:otherwise>
            </c:choose>     <%-- end checking for title --%>
        </c:when>
        <c:otherwise>
        <%--short title is present so show it --%>
        <c:out value="${portletDescription.shortTitle.value}"/>
        </c:otherwise>
        </c:choose>         <%-- end checking for short title --%>
    </c:when>
    <c:otherwise>
    <%--display name is present so show it --%>
    <c:out value="${portletDescription.displayName.value}"/>
    </c:otherwise>
</c:choose>                 <%-- end checking for display name --%>

    </b></font></td>


<%
    // for minimize only if it is needed (i.e. not already in minimized mode) else normal ...

    if(portlet.getWindowState().equals(WindowState.MINIMIZED) ||
            renderRequest.getWindowState().equals(WindowState.MINIMIZED))
    {
        // we should come here only after a minimize request has been made either through inner title bar or
        // through a "minimze" link provided in the portlet markup itself
        javax.portlet.PortletURL normalURL = renderResponse.createRenderURL();
        normalURL.setParameter("Mode", "Normal");
        try {
            normalURL.setWindowState(javax.portlet.WindowState.NORMAL);
        }
        catch (WindowStateException ex) {
            ex.printStackTrace();
        }

%>
        <td width="10%" align="center">
        <a href='<%=normalURL.toString()%>'>
        <Font color="#000000" size="2">Normal</font>
        </a>
        </td>
<%
    }
    else
    {
        // provide minimize link only if the portlet supports minimzed window state
        if(minimizeAvailable)
        {
            javax.portlet.PortletURL minURL = renderResponse.createRenderURL();
            minURL.setParameter("Mode", "Min");
            try {
                minURL.setWindowState(javax.portlet.WindowState.MINIMIZED);
            } catch (WindowStateException ex) {
                ex.printStackTrace();
            }
    %>

        <td width="10%" align="center">
        <a href='<%=minURL.toString()%>'>
        <Font color="#000000" size="2">Min</font>
        </a>
        </td>

<%      }
    }

    // for maximize only if it is needed (i.e. not already in maximized mode) else normal ...

    if(portlet.getWindowState().equals(WindowState.MAXIMIZED) ||
            renderRequest.getWindowState().equals(WindowState.MAXIMIZED))
    {
        // we should come here only after a maximize request has been made either through inner title bar or
        // through a "maximze" link provided in the portlet markup itself
        javax.portlet.PortletURL normalURL = renderResponse.createRenderURL();
        normalURL.setParameter("Mode", "Normal");
        try {
            normalURL.setWindowState(javax.portlet.WindowState.NORMAL);
        }
        catch (WindowStateException ex) {
            ex.printStackTrace();
        }

%>
        <td width="10%" align="center">
        <a href='<%=normalURL.toString()%>'>
        <Font color="#000000" size="2">Normal</font>
        </a>
        </td>
<%
    }
    else
    {
        //for maximize...
        if(maximizeAvailable)
        {
            javax.portlet.PortletURL maxURL = renderResponse.createRenderURL();
            maxURL.setParameter("Mode", "Max");
            try {
                maxURL.setWindowState(javax.portlet.WindowState.MAXIMIZED);
            }
            catch (WindowStateException ex) {
                ex.printStackTrace();
            }
%>
        <td width="10%" align="center">
        <a href='<%=maxURL.toString()%>'>
        <Font color="#000000" size="2">Max</font>
        </a>
        </td>
<%
        }
    }

    //for edit portlet mode similar minimize/normal and maximize/normal show edit link if it is needed (i.e. not
    // already in edit mode) else a back link to go back to view mode ...
    if(supportsEdit)
    {
        if(portlet.getPortletMode().equals(PortletMode.EDIT) ||
                renderRequest.getPortletMode().equals(PortletMode.EDIT))
        {
            javax.portlet.PortletURL viewURL = renderResponse.createRenderURL();
            viewURL.setParameter("Mode", "View");
            try {
                viewURL.setPortletMode(javax.portlet.PortletMode.VIEW);
            } catch (PortletModeException ex) {
                ex.printStackTrace();
            }
%>
        <td width="10%" align="center">
        <a href='<%=viewURL.toString()%>'>
        <Font color="#000000" size="2">View</font>
        </a>
        </td>
<%
        }
        else
        {
            javax.portlet.PortletURL editURL = renderResponse.createRenderURL();
            editURL.setParameter("Mode", "Edit");
            try {
                editURL.setPortletMode(javax.portlet.PortletMode.EDIT);
            } catch (PortletModeException ex) {
                ex.printStackTrace();
            }
%>
        <td width="10%" align="center">
        <a href='<%=editURL.toString()%>'>
        <Font color="#000000" size="2">Edit</font>
        </a>
        </td>
<%
        }
    }
    // TODO check for about mode if it is present... (from cutom modes???)
    if(supportsAbout)
    {
        javax.portlet.PortletURL aboutURL = renderResponse.createRenderURL();
        aboutURL.setParameter("Mode", "About");
%>
        <td width="10%" align="center">
        <a href='<%=aboutURL.toString()%>'>
        <Font color="#000000" size="2">About</font>
        </a>
        </td>
<%
    }
    //for help...
    if(supportsHelp)
    {
        javax.portlet.PortletURL helpURL = renderResponse.createRenderURL();
        helpURL.setParameter("Mode", "Help");
        try {
            helpURL.setPortletMode(PortletMode.HELP);
        } catch (PortletModeException ex) {
            ex.printStackTrace();
        }
%>
        <td width="10%" align="center">
        <a href='<%=helpURL.toString()%>'>
        <Font color="#000000" size="2">Help</font>
        </a>
        </td>
<%
    }
%>


</tr>
</table>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/inner_title_bar.jsp#2 $$Change: 651448 $--%>

<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setBundle var="templatesbundle" basename="atg.portal.templates"/>

<dsp:page>

<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="gearTextColor"            value="#${page.colorPalette.gearTextColor}"/>
<c:set var="gearBackgroundColor"      value="#${page.colorPalette.gearBackgroundColor}"/>
<c:set var="GEAR"><%= Attribute.GEAR %></c:set>
<c:set var="GEARMODE"><%= Attribute.GEARMODE %></c:set>
<c:set var="gear"                     value="${requestScope[GEAR]}"/>
<c:set var="gearMode"                 value="${requestScope[GEARMODE]}"/>

<% 
 pageContext.setAttribute("displayMode",DisplayMode.SHARED);
%>
<paf:context var="portalContext"/>
<c:set target="${portalContext}" property="displayMode" value="${displayMode}"/>
<paf:encodeURL var="backURL" context="${portalContext}" url="${portalServletRequest.portalRequestURI}"/>

<font>
<a href="<c:out value="${backURL}"/>"><fmt:message key="link_full_view_back" bundle="${templatesbundle}"/></a>
</font>
<br><br>

    <center>
    <table border="0" width="96%" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top" width="100%">


  <%-- 
    --  Render a titlebar for the gear
    --
    --  Note: For improved performace you may not want to dispatch out to a gear 
    --  titlebar. In  this case comment out the tag below and inline your titlebar
    --  code.
    --%>
  <paf:titlebar gear="${gear}"/>

  <%--  
    --  Render the pre gear treatment 
    --  
    --  Note: For improved performance we don't dispatch out to gear pre treatment. If support is
    --  needed for dynamic gear pre treatment uncomment the following tag.
    --  <paf:titlebar gear="${gear}" type="pre"/>
    --%>
  <table border="0" cellpadding="1" cellspacing="0" width="100%">
    <tr>
      <td valign="top" bgcolor="<c:out value="${gearTextColor}"/>">
        <table border="0" width="100%" cellpadding="0" cellspacing="0">
          <tr>
            <td valign="top" bgcolor="<c:out value="${gearBackgroundColor}"/>">
       
              <%-- 
                -- Render the contents of the gear.
                --
                -- Note: Dispatches out to registered gear. Contents
                -- may be retrieved from a contents cache. 
                --%>
              <font color="<c:out value="${gearTextColor}"/>">

                <% 
                  pageContext.setAttribute("gearContextType",GearContext.class);
                %>              
                <paf:context var="gearContext" type="${gearContextType}"/>
                <c:set target="${gearContext}" property="gear" value="${gear}"/>
                <c:set target="${gearContext}" property="gearMode" value="${gearMode}"/>
                <paf:include context="${gearContext}"/>     

              </font>
             
            </td>
          </tr>          
        </table>
      </td>
    </tr>
  </table>
  <%--
    -- Render the post gear treatment
    --
    -- Note: For improved performance we don't dispatch out to gear post treatment. If support is
    -- needed for dynamic gear post treatment uncomment the following tag.
    -- <paf:titlebar gear="${gear}" type="post"/>
    --%>
  <br/>


        </td>
      </tr>
    </table>
    </center>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/templates/layout/html/full.jsp#2 $$Change: 651448 $--%>

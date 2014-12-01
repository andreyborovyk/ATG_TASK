<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setLocale value="${request.locale}"/>
<fmt:setBundle var="templatesbundle" basename="atg.portal.templates"/>

<dsp:page>

<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="communityName"            value="${community.name}"/>
<c:set var="pageName"                 value="${page.name}"/>
<c:set var="bodyTagData"              value="${page.colorPalette.bodyTagData}"/>
<c:set var="pageTextColor"            value="#${page.colorPalette.pageTextColor}"/>
<c:set var="pageBackgroundColor"      value="#${page.colorPalette.pageBackgroundColor}"/>
<c:set var="gearTitleTextColor"       value="#${page.colorPalette.gearTitleTextColor}"/>
<c:set var="gearTitleBackgroundColor" value="#${page.colorPalette.gearTitleBackgroundColor}"/>
<c:set var="communityPages"           value="${portalServletRequest.communityPages.pages}"/>
<c:set var="cssURL"                   value="${community.style.CSSURL}"/>
<c:if test="${cssURL != null}">
 <paf:encodeURL var="cssURL" url="${cssURL}"/>
</c:if>

<c:set var="contextPath"><%= request.getContextPath() %></c:set>
<paf:encodeURL var="adminURL"     url="/portal/settings/community_settings.jsp"/>
<paf:encodeURL var="customizeURL" url="/portal/settings/user.jsp"/>
<paf:encodeURL var="loginURL"     url="${contextPath}/userprofiling/login.jsp"/>
<paf:encodeURL var="logoutURL"    url="${contextPath}/userprofiling/logout.jsp"/>

<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>

<html>

  <head>

    <title><c:out value="${communityName}"/> - <c:out value="${pageName}"/></title>
    <link rel="stylesheet" type="text/css" href='<c:out value="${cssURL}"/>' src='<c:out value="${cssURL}"/>'/>

  </head>

  <body <c:out value="${bodyTagData}" escapeXml="false"/> >

    <%--
      -- Header
      --%>
    <table border="0" cellpadding="1" cellspacing="0" width="100%">
      <tr>
       <td valign="top" bgcolor='<c:out value="${pageTextColor}"/>'>
   
        <table border="0" width="100%" cellpadding="3" cellspacing="0">
          <tr>
            <%--
              --  Left Header
              --%>
            <td valign="top" align="left" bgcolor='<c:out value="${pageBackgroundColor}"/>'>
              <font color='<c:out value="${pageTextColor}"/>'>
                <b><c:out value="${communityName}"/> - <c:out value="${pageName}"/></b><br/>

                <paf:hasCommunityRole roles="leader,portal-admin">
                  <a href='<c:out value="${adminURL}"/>'><fmt:message key="page-label-administer" bundle="${templatesbundle}"/></a><br/> 
                </paf:hasCommunityRole>

                <c:if test="${community.allowPersonalizedPages == true && profile.transient == false}" >
                  <a href='<c:out value="${customizeURL}"/>'><fmt:message key="page-label-customize" bundle="${templatesbundle}"/></a>
                </c:if>

              </font>
            </td>
            <%--
              --  Right Header
              --%>
            <td valign="top" align="right" bgcolor='<c:out value="${pageBackgroundColor}"/>'>
              <font color='<c:out value="${pageTextColor}"/>'>
              <c:choose>
                <c:when test="${profile.transient == false}">
                  
                  <fmt:message key="page-message-logged-in" bundle="${templatesbundle}"/> <b><dsp:valueof bean="Profile.login"/></b><br/>
                  <a href='<c:out value="${logoutURL}"/>'><fmt:message key="page-label-logout" bundle="${templatesbundle}"/></a>
                   
                </c:when>   
                <c:otherwise>

                  <a href='<c:out value="${loginURL}"/>'><fmt:message key="page-label-login" bundle="${templatesbundle}"/></a><br/>&nbsp;

                </c:otherwise>
              </c:choose>
              </font>
            </td>
          </tr>
        </table>


       </td>
      </tr>
    </table>

      <%--
      -- Page Tabs
      --%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td bgcolor='<c:out value="${gearTitleBackgroundColor}"/>'><font size="-3" style="font-size:.1em;">&nbsp;</font></td>
</tr>
<tr><td valign="bottom">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>

<td width="5" bgcolor='<c:out value="${gearTitleBackgroundColor}"/>'>&nbsp;&nbsp;&nbsp;</td>

    <c:forEach var="communityPage"
               items="${communityPages}">
                  <paf:context var="portalContext"/>
                  <c:set target="${portalContext}" property="page" value="${communityPage}"/>
                  <paf:encodeURL var="communityPageURL" context="${portalContext}" url="${portalServletRequest.portalRequestURI}"/>

                  <paf:renderTab page="${portalContext.page}">
		    <paf:defaultTab>
<td bgcolor='<c:out value="${gearTitleBackgroundColor}"/>' nowrap><nobr><font size="-1">&nbsp;&nbsp;<a href="<c:out value="${communityPageURL}"/>"><font color="<c:out value="${gearTitleTextColor}"/>" ><c:out value="${communityPage.name}"/></font></a>&nbsp;&nbsp;</font></nobr></td>
		    </paf:defaultTab>
		    
		    <paf:currentTab>

<td bgcolor='<c:out value="${gearTitleTextColor}"/>' nowrap><nobr><font size="-1">&nbsp;&nbsp;<b><a href="<c:out value="${communityPageURL}"/>"><font color="<c:out value="${gearTitleBackgroundColor}"/>"><c:out value="${communityPage.name}"/></font></a></b>&nbsp;&nbsp;</font></nobr></td>

		    </paf:currentTab>
		  </paf:renderTab>

<td bgcolor='<c:out value="${gearTitleTextColor}"/>'><font size="-2" color='<c:out value="${gearTitleTextColor}"/>'>|</font></td>

    </c:forEach>
 
<td width="95%" bgcolor='<c:out value="${gearTitleBackgroundColor}"/>'>&nbsp;</td>
<tr>
</table></td>
<tr>
</table>
 <br />

    <%--
      -- Layout
      --%>
    <paf:layout/>

    <%--
      -- Footer
      --%>
    <hr/>

  </body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/templates/page/html/shared.jsp#2 $$Change: 651448 $--%>

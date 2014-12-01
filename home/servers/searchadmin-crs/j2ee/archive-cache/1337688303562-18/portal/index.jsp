<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<dsp:page>


<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Range"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<fmt:setBundle var="portalbundle" basename="atg.portal.portal" />

<%--
    Generic Portal Page.
--%>
<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="portal"                   value="${portalServletRequest.portal}"/>
<c:set var="device"                   value="${portalServletRequest.device}"/>
<c:set var="portalName"               value="${portal.name}"/>
<c:set var="communitySet"             value="${portal.communitySet}"/>
<c:set var="bodyTagData"              value="${page.colorPalette.bodyTagData}"/>
<c:set var="pageTextColor"            value="#${page.colorPalette.pageTextColor}"/>
<c:set var="pageBackgroundColor"      value="#${page.colorPalette.pageBackgroundColor}"/>
<c:set var="gearTitleTextColor"       value="#${page.colorPalette.gearTitleTextColor}"/>
<c:set var="gearTitleBackgroundColor" value="#${page.colorPalette.gearTitleBackgroundColor}"/>
<c:set var="mimeType"                 value="${device.mimeType}"/>

<c:set var="cssURL"                   value="${community.style.CSSURL}"/>
<c:if test="${cssURL != null}"> 
 <paf:encodeURL var="cssURL" url="${cssURL}"/>
</c:if>
<c:choose>
 <c:when test="${mimeType == 'text/vnd.wap.wml'}">
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">
<wml>
  <card id='<fmt:message key="portal-title" bundle="${portalbundle}" />' title='<fmt:message key="portal-title" bundle="${portalbundle}"/>'>
    <p><fmt:message key="portal-title" bundle="${portalbundle}"/> - <c:out value="${portalName}"/></p>
  </card>
</wml>
 </c:when>
 <c:otherwise>
<html>
  <head>

    <title><fmt:message key="portal-title" bundle="${portalbundle}"/> - <c:out value="${portalName}"/></title>
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
                <b><fmt:message key="portal-title" bundle="${portalbundle}"/> - <c:out value="${portalName}"/></b><br/>
              </font>
            </td>
            <%--
              --  Right Header
              --%>
              <c:choose>
                <c:when test="${profile.transient == false}">
                  <td valign="top" align="right" bgcolor='<c:out value="${pageBackgroundColor}"/>'>
                    <font color='<c:out value="${pageTextColor}"/>'>
                      
                        <fmt:message key="portal-message-logged-in" bundle="${portalbundle}"/> <b><dsp:valueof bean="Profile.login"/></b><br/>   
      
                    </font>
                  </td>
                </c:when>   
                <c:otherwise>
                  <td valign="top" align="right" bgcolor='<c:out value="${pageBackgroundColor}"/>'>
                   
                  </td>
                </c:otherwise>
              </c:choose>
          </tr>
        </table>


       </td>
      </tr>
    </table>

    <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td bgcolor='<c:out value="${gearTitleBackgroundColor}"/>' ><font size="-3" style="font-size:.1em;">&nbsp;</font></td>
    </tr>
    <tr>
      <td valign="bottom">
       <table border="0" cellpadding="0" cellspacing="0" width="100%">
         <tr>
           <td width="5%" bgcolor='<c:out value="${gearTitleBackgroundColor}"/>' >&nbsp;&nbsp;&nbsp;</td>
           <td width="15%" fgcolor='<c:out value="${gearTitleBackgroundColor}"/>' ><fmt:message key="portal-label-communities" bundle="${portalbundle}"/></td>
           <td width="80%" bgcolor='<c:out value="${gearTitleBackgroundColor}"/>' >&nbsp;&nbsp;&nbsp;</td>
         <tr>
       </table>
     </td>
   <tr>
   </table>
   <br />
   <table cellpadding="0" cellspacing="1" border="0" width="80%">
        <tr><td><fmt:message key="portal-label-community-name" bundle="${portalbundle}"/>&nbsp;</td> <td><fmt:message key="portal-label-community-creation-date" bundle="${portalbundle}"/>&nbsp;</td> <td><fmt:message key="portal-label-community-last-modified" bundle="${portalbundle}"/>&nbsp;</td> <td><fmt:message key="portal-label-community-enabled" bundle="${portalbundle}"/>&nbsp;</td> <td><fmt:message key="portal-label-community-description" bundle="${portalbundle}"/>&nbsp;</td></tr>
   </table>
   <hr/>
   
    <dsp:droplet name="Range">
      <dsp:param name="array" value="${communitySet}"/>
      <dsp:param name="howMany" value="10"/>
      <dsp:param name="sortProperties" value="+name"/>
      <dsp:oparam name="outputStart">
       
        <table cellpadding="0" cellspacing="1" border="0" width="85%">        
      </dsp:oparam>
      <dsp:oparam name="output">

          <tr><td><a href='<dsp:valueof param="element.portal.contextPath"/><dsp:valueof param="element.communityURI"/>'><dsp:valueof param="element.name"></a></dsp:valueof>&nbsp;</td> <td><dsp:valueof param="element.creationDate" converter="date" format="d-MMM-yyyy H:mm"></dsp:valueof>&nbsp;</td> <td><dsp:valueof param="element.lastmodified" converter="date" format="d-MMM-yyyy H:mm"></dsp:valueof>&nbsp;</td> <td><dsp:valueof param="element.enabled"></dsp:valueof></td> <td><dsp:valueof param="element.description"></dsp:valueof>&nbsp;</td></tr>
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
        </table>

        <dsp:droplet name="Switch">
          <dsp:param name="value" param="hasPrev"/>
          <dsp:oparam name="true">
<c:set var="prevURL"><%= response.encodeURL(request.getRequestURI()) %></c:set>
<dsp:a href="${prevURL}">
  <dsp:param name="start" param="prevStart"/><fmt:message key="portal-label-previous" bundle="${portalbundle}"/> <dsp:valueof param="prevHowMany"/>&nbsp;
</dsp:a>
           
         </dsp:oparam>
       </dsp:droplet>
       <dsp:droplet name="Switch">
         <dsp:param name="value" param="hasNext"/>
         <dsp:oparam name="true">
<c:set var="nextURL"><%= response.encodeURL(request.getRequestURI()) %></c:set>
<dsp:a href="${nextURL}">
  <dsp:param name="start" param="nextStart"/><fmt:message key="portal-label-next" bundle="${portalbundle}"/> <dsp:valueof param="nextHowMany"/>&nbsp;
</dsp:a>
         </dsp:oparam>
       </dsp:droplet>

      </dsp:oparam>
    </dsp:droplet>

    <%--
      -- Footer
      --%>
    <hr/>

    
  </body>
</html>
 </c:otherwise>
</c:choose>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/index.jsp#2 $$Change: 651448 $--%>



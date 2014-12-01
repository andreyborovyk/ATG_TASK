<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:bundle basename="atg.portal.access">

<dsp:page>
<dsp:importbean bean="/atg/userprofiling/InternalProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="InternalProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="bodyTagData"              value="${page.colorPalette.bodyTagData}"/>
<c:set var="cssURL"                   value="${community.style.CSSURL}"/>
<c:if test="${cssURL != null}">
 <paf:encodeURL var="cssURL" url="${cssURL}"/>
</c:if>

<c:set var="uri" value="${portalServletRequest.portalContextPath}/bcc"/>

<c:set var="successURL" value="${param.successURL}"/>
<c:if test="${successURL == null}">
 <c:set var="successURL" value="${uri}"/>
</c:if>
<paf:encodeURL var="errorURL" url="${uri}"/>
<c:set var="actionURL" value="${uri}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>

    <title><fmt:message key="access-denied-title"/></title>
    <link rel="icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
    <link rel="SHORTCUT ICON" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url context='/atg' value='/templates/style/css/login.css'/>" type="text/css" />

  </head>
  <body class="login">
    <div id="centered">

      <dsp:droplet name="Switch">
        <dsp:param name="value" bean="InternalProfileFormHandler.profile.transient"/>
        <dsp:oparam name="false">

          <dsp:form action="${actionURL}" method="post">
            <dsp:input type="hidden" bean="InternalProfileFormHandler.logoutSuccessURL" value="${successURL}"/>
            <dsp:input type="hidden" bean="InternalProfileFormHandler.logoutErrorURL" value="${errorURL}"/>

            <dsp:droplet name="Switch">
              <dsp:param name="value" bean="InternalProfileFormHandler.formError"/>
              <dsp:oparam name="true">

                <strong><ul>
                  <dsp:droplet name="ProfileErrorMessageForEach">
                    <dsp:param name="exceptions" bean="InternalProfileFormHandler.formExceptions"/>
                    <dsp:oparam name="output">
                      <li> <dsp:valueof param="message"/> </li>
                    </dsp:oparam>
                  </dsp:droplet>
                </ul></strong>

              </dsp:oparam>
            </dsp:droplet>

          <div class="logoutMiddle">
            <fmt:message key="access-denied-heading"/><br/><br/>
            <fmt:message key="access-denied-message"/>
          </div>

          <div class="logoutBottom">
            <fmt:message var="logoutbutton" key="logout" />
            <dsp:input iclass="buttonSmall" type="submit" value="${logoutbutton}" bean="InternalProfileFormHandler.logout"/>
          </div>

          </dsp:form>

        </dsp:oparam>    
      </dsp:droplet>

    </div>
  </body>
</html>
</dsp:page>
</fmt:bundle>
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/access/html/accessDenied.jsp#2 $$Change: 651448 $--%>

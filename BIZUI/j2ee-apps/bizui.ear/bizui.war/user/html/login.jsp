<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%--
     This page is a login form, which uses the ProfileFormHandler to authenticate a userid
     and password. This page is intended to serve as the default sample login page, and can be
     updated or replace for a given portal implementation.          
--%>
<fmt:setBundle var="userprofilingbundle" basename="atg.portal.userprofiling" />

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

<c:choose>
 <c:when test="${page != null}">
  <c:set var="uri" value="${portalServletRequest.portalContextPath}${page.pageURI}"/>
 </c:when>
 <c:when test="${community != null}">
  <c:set var="uri" value="${portalServletRequest.portalContextPath}${community.communityURI}"/>
 </c:when>
 <c:otherwise>
  <c:set var="uri" value="${portalServletRequest.portalContextPath}"/>
 </c:otherwise>
</c:choose>
<c:set var="successURL" value="${param.successURL}"/>
<c:if test="${successURL == null}">
 <c:set var="successURL" value="${uri}"/>
</c:if>
<paf:encodeURL var="errorURL" url="${uri}"/>
<c:set var="actionURL" value="${uri}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!-- Do not edit this line.  Used by templates/page/html/scripts/scripts.js  during issueRequest() ajax calls to determine that a session is invalid and the login form has been served within a div tag.  LOGINFORMSERVEDBYAJAX --> 

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>

    <title><fmt:message key="login-title" bundle="${userprofilingbundle}"/></title>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="SHORTCUT ICON" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon" />
<!--    <style type="text/css" media="all">
      @import url("<c:out value='${cssURL}'/>");
    </style>
-->
  <link rel="stylesheet" href="<c:url context='/atg' value='/templates/style/css/login.css'/>" type="text/css" />

  </head>
  <body onload="document.loginForm.loginName.focus()">
    <div id="centered"> 
      <table border="0" class="wh100p"> 
        <tr> 
          <td class="loginBottom"> 

            <table align="right"> 
              <tr> 
                <td class="loginMiddle" valign="top">

            <dsp:droplet name="Switch">
              <dsp:param name="value" bean="InternalProfileFormHandler.formError"/>
              <dsp:oparam name="true">
              <br />
                <p>
                  <span class="error">
                    <strong>
                    <dsp:droplet name="ProfileErrorMessageForEach">
                      <dsp:param name="exceptions" bean="InternalProfileFormHandler.formExceptions"/>
                      <dsp:oparam name="output">
                        <center><dsp:valueof param="message"/></center>
                      </dsp:oparam>
                    </dsp:droplet>
                    </strong>
                  </span>
                </p>
              </dsp:oparam>
            </dsp:droplet>

            <c:choose>
              <c:when test="${profile.transient == false}">
                <fmt:message key="login-message-logged-in" bundle="${userprofilingbundle}"/>&nbsp;<dsp:valueof bean="Profile.login"/>
              </c:when>   
              <c:otherwise>

                  <dsp:form name="loginForm" action="${actionURL}" method="post">
                    <dsp:input type="hidden" bean="InternalProfileFormHandler.loginSuccessURL" value="${successURL}"/>
                    <dsp:input type="hidden" bean="InternalProfileFormHandler.loginErrorURL" value="${errorURL}"/>
                    <table class="wh100p loginTable" border="0"> 
                      <tr> 
                        <td colspan="2"> </td> 
                      </tr>
                      <tr> 
                        <td class="formLabel"> 
                          <span class="loginLabel"><fmt:message key="login-label-username" bundle="${userprofilingbundle}"/></span> 
                        </td> 
                        <td> 
                          <!-- for displaying login errors --> 
                          <span class="error" style="display:none;">* ErrorMessage</span> 
                          <!--input id="loginName" name="login" class="loginTxtField" maxlength="20" type="text" required="true"/--> 
                          <dsp:input name="login" id="loginName" iclass="loginTxtField" type="text" maxlength="40" bean="InternalProfileFormHandler.value.login"/>
                        </td> 
                      </tr> 
                      <tr> 
                        <td class="formLabel"> 
                          <span class="loginLabel"><fmt:message key="login-label-password" bundle="${userprofilingbundle}"/></span> 
                        </td> 
                        <td> 
                          <!-- for displaying login errors --> 
                          <span class="error" style="display:none;">* ErrorMessage</span> 
                          <!--input id="loginPassword" class="loginTxtField" maxlength="20" type="password" required="true"/--> 
                          <dsp:input id="loginPassword" iclass="loginTxtField" type="password" maxlength="35" bean="InternalProfileFormHandler.value.password"/>
                        </td> 
                      </tr> 
                      <tr align="right"> 
                        <td colspan="2" class="padRight10"> 
                          <fmt:message var="loginbutton" key="login-button-submit" bundle="${userprofilingbundle}"/>
                          <dsp:input iclass="buttonSmall go" type="submit" value="${loginbutton}" bean="InternalProfileFormHandler.login"/>
                          <!--a href="#" class="buttonSmall go" title="Change"> 
                            <span>Log In</span> 
                          </a--> 
                        </td> 
                      </tr> 
                    </table> 
                  </dsp:form> 

              </c:otherwise>
            </c:choose>

                </td> 
              </tr> 
            </table> 
          </td> 
        </tr> 
      </table> 
    </div> 

  </body>
</html>
</dsp:page>
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/user/html/login.jsp#2 $$Change: 651448 $--%>

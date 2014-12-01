<%@ page contentType="text/vnd.wap.wml" import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp" %>
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<%--
     This page is a registration form, which uses the ProfileFormHandler to register a new 
     userid. This page is intended to serve as the default sample registration page, and can be
     updated or replace for a given portal implementation.          
--%>
<fmt:setBundle var="userprofilingbundle" basename="atg.portal.userprofiling" />

<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:choose>
 <c:when  test="${page != null}">
  <c:set var="uri" value="${portalServletRequest.portalContextPath}${page.pageURI}"/>
 </c:when>
 <c:when  test="${community != null}">
  <c:set var="uri" value="${portalServletRequest.portalContextPath}${community.communityURI}"/>
 </c:when>
 <c:otherwise>
  <c:set var="uri" value="${portalServletRequest.portalContextPath}"/>
 </c:otherwise>
</c:choose>
<c:set var="successURL" value="${param.successURL}"/>
<c:if test="${successURL == null}">
 <paf:encodeURL var="successURL" url="${uri}"/>
</c:if>
<paf:encodeURL var="errorURL" url="${uri}"/>
<paf:encodeURL var="actionURL" url="${uri}"/>

<wml>

    <card id="register" title="<fmt:message key="register-title" bundle="${userprofilingbundle}"/>">

      <%-- 
           This form should not show what the 
           current profile attributes are so 
           we will disable the ability to extract
           default values from the profile. 
      --%>
      <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>


        <c:choose>
          <c:when test="${profile.transient == false}">

            <%-- 
                 A profile has already 
                 been logged in 
            --%>
            <p>
            <fmt:message key="login-message-logged-in" bundle="${userprofilingbundle}"/>&nbsp;<dsp:valueof bean="Profile.login"/></p>
            <do type="prev" label="Prev">
              <prev/>
            </do>

          </c:when>   
          <c:otherwise>
 
            <dsp:droplet name="Switch">
	      <dsp:param bean="ProfileFormHandler.formError" name="value"/>
	      <dsp:oparam name="true">
 
  	        <dsp:droplet name="ProfileErrorMessageForEach">
                  <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
                  <dsp:oparam name="output">

                    <p><dsp:valueof param="message"/></p>

                  </dsp:oparam>
  	        </dsp:droplet>

              </dsp:oparam>
            </dsp:droplet>  



            <p><fmt:message key="register-label-username" bundle="${userprofilingbundle}"/>:<input type="text" name="login" title="<fmt:message key="login-label-username" bundle="${userprofilingbundle}"/>" size="20" maxlength="20" value=""/></p>

            <p><fmt:message key="register-label-password" bundle="${userprofilingbundle}"/>:<input type="password" name="password" title="<fmt:message key="login-label-password" bundle="${userprofilingbundle}"/>" size="10" maxlength="35" value=""/></p>

            <p><fmt:message key="register-label-confirm-password" bundle="${userprofilingbundle}"/>:<input type="password" name="confirmPassword" title="<fmt:message key="register-label-confirm-password" bundle="${userprofilingbundle}"/>" size="10" maxlength="35" value=""/></p>

          
            <p><anchor title="<fmt:message key="register-button-submit" bundle="${userprofilingbundle}"/>"><fmt:message key="register-button-submit" bundle="${userprofilingbundle}"/>
	      <dsp:go href="${actionURL}" >
                <dsp:postfield bean="ProfileFormHandler.confirmPassword" value="true"/>
	        <dsp:postfield bean="ProfileFormHandler.createSuccessURL" value="${successURL}" />
                <dsp:postfield bean="ProfileFormHandler.createErrorURL" value="${errorURL}" />
	        <dsp:postfield bean="ProfileFormHandler.value.login" value="$login" />
	        <dsp:postfield bean="ProfileFormHandler.value.password" value="$password" />
	        <dsp:postfield bean="ProfileFormHandler.value.confirmpassword" value="$confirmPassword" />
	        <dsp:postfield bean="ProfileFormHandler.create" value=" " />
	      </dsp:go>
            </anchor></p>

            <do type="prev" label="Prev">
              <prev/>
            </do>  

          </c:otherwise>
        </c:choose>

    </card>  

</wml>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/userprofiling/wml/register.jsp#2 $$Change: 651448 $--%>

<%@ page contentType="text/vnd.wap.wml" import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp" %>
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<%--
     This page is an edit form, which uses the ProfileFormHandler to update an existing  
     userid. This page is intended to serve as the default sample edit page, and can be
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

    <card id="edit" title="<fmt:message key="edit-title" bundle="${userprofilingbundle}"/>">

      <%-- 
           This form should show what the 
           current profile attributes are so 
           we will enable the ability to extract
           default values from the profile. 
      --%>
      <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="true"/>

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

            <p><fmt:message key="edit-label-firstName" bundle="${userprofilingbundle}"/>:<input type="text" name="firstName" title="<fmt:message key="edit-label-firstName" bundle="${userprofilingbundle}"/>" size="30" maxlength="35" value="<dsp:valueof bean="ProfileFormHandler.value.firstName"/>"/></p>

            <p><fmt:message key="edit-label-lastName" bundle="${userprofilingbundle}"/>:<input type="text" name="lastName" title="<fmt:message key="edit-label-lastName" bundle="${userprofilingbundle}"/>" size="30" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.lastName"/>"/></p>

            <p><fmt:message key="edit-label-email" bundle="${userprofilingbundle}"/>:<input type="text" name="email" title="<fmt:message key="edit-label-email" bundle="${userprofilingbundle}"/>" size="30" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.email"/>"/></p>
            
            <p><fmt:message key="edit-label-address1" bundle="${userprofilingbundle}"/>:<input type="text" name="address1" title="<fmt:message key="edit-label-address1" bundle="${userprofilingbundle}"/>" size="30" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.homeAddress.address1"/>"/></p>

            <p><fmt:message key="edit-label-address2" bundle="${userprofilingbundle}"/>:<input type="text" name="address2" title="<fmt:message key="edit-label-address2" bundle="${userprofilingbundle}"/>" size="30" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.homeAddress.address2"/>"/></p>

            <p><fmt:message key="edit-label-city" bundle="${userprofilingbundle}"/>:<input type="text" name="city" title="<fmt:message key="edit-label-city" bundle="${userprofilingbundle}"/>" size="30" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.homeAddress.city"/>"/></p>

            <p><fmt:message key="edit-label-state" bundle="${userprofilingbundle}"/>:<input type="text" name="state" title="<fmt:message key="edit-label-state" bundle="${userprofilingbundle}"/>" size="30" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.homeAddress.state"/>"/></p>

            <p><fmt:message key="edit-label-postalCode" bundle="${userprofilingbundle}"/>:<input type="text" name="postalCode" title="<fmt:message key="edit-label-postalCode" bundle="${userprofilingbundle}"/>" size="10" maxlength="12" value="<dsp:valueof bean="ProfileFormHandler.value.homeAddress.postalCode"/>"/></p>

            <p><fmt:message key="edit-label-country" bundle="${userprofilingbundle}"/>:<input type="text" name="country" title="<fmt:message key="edit-label-country" bundle="${userprofilingbundle}"/>" size="30" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.homeAddress.country"/>"/></p>

            <p><fmt:message key="edit-label-phoneNumber" bundle="${userprofilingbundle}"/>:<input type="text" name="phoneNumber" title="<fmt:message key="edit-label-phoneNumber" bundle="${userprofilingbundle}"/>" size="20" maxlength="30" value="<dsp:valueof bean="ProfileFormHandler.value.homeAddress.phoneNumber"/>"/></p>

            <p><fmt:message key="edit-label-dateOfBirth" bundle="${userprofilingbundle}"/>:<input type="text" name="dateOfBirth" title="<fmt:message key="edit-label-dateOfBirth" bundle="${userprofilingbundle}"/>" size="10" maxlength="10" date="M/dd/yyyy" value="<dsp:valueof bean="ProfileFormHandler.value.dateOfBirth"/>"/></p>
          
            <p><anchor title="<fmt:message key="edit-button-submit" bundle="${userprofilingbundle}"/>"><fmt:message key="edit-button-submit" bundle="${userprofilingbundle}"/>
	      <dsp:go href="${actionURL}" >
                <dsp:postfield bean="ProfileFormHandler.updateRepositoryId" value="${profile.repositoryId}"/>

	        <dsp:postfield bean="ProfileFormHandler.createSuccessURL" value="${successURL}" />
                <dsp:postfield bean="ProfileFormHandler.createErrorURL" value="${errorURL}" />
                <dsp:postfield bean="ProfileFormHandler.value.firstName" value="$firstName"/>
                <dsp:postfield bean="ProfileFormHandler.value.lastName" value="$lastName"/>
                <dsp:postfield bean="ProfileFormHandler.value.email" value="$email"/>
                <dsp:postfield bean="ProfileFormHandler.value.homeAddress.address1" value="$address1"/>
                <dsp:postfield bean="ProfileFormHandler.value.homeAddress.address2" value="$address2"/>
                <dsp:postfield bean="ProfileFormHandler.value.homeAddress.city" value="$city"/>
                <dsp:postfield bean="ProfileFormHandler.value.homeAddress.state" value="$state"/>
                <dsp:postfield bean="ProfileFormHandler.value.homeAddress.postalCode" value="$postalCode"/>
                <dsp:postfield bean="ProfileFormHandler.value.homeAddress.country" value="$country"/>
                <dsp:postfield bean="ProfileFormHandler.value.homeAddress.phoneNumber" value="$phoneNumber"/>
                <dsp:postfield bean="ProfileFormHandler.value.dateOfBirth" value="$dateOfBirth"/>

	        <dsp:postfield bean="ProfileFormHandler.update" value=" " />
	      </dsp:go>
            </anchor></p>

            <do type="prev" label="Prev">
              <prev/>
            </do>  
          

    </card>  

</wml>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/userprofiling/wml/edit.jsp#2 $$Change: 651448 $--%>

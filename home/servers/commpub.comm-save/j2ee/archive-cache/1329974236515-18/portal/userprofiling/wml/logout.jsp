<%@ page contentType="text/vnd.wap.wml" import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setBundle var="userprofilingbundle" basename="atg.portal.userprofiling" />

<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. --%>
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

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

    <card id="logout" title="<fmt:message key="logout-title" bundle="${userprofilingbundle}"/>">

      <dsp:droplet name="Switch">
        <dsp:param bean="Profile.transient" name="value"/>
        <dsp:oparam name="false">
          
          <p>
            <fmt:message key="logout-message-goodbye" bundle="${userprofilingbundle}"/>
          </p>
          
          <do type="accept" label="<fmt:message key="logout-button-submit" bundle="${userprofilingbundle}"/>">
            <dsp:go href="${actionURL}">
              <dsp:postfield bean="ProfileFormHandler.logoutSuccessURL" value="${successURL}" />
              <dsp:postfield bean="ProfileFormHandler.logoutErrorURL" value="${errorURL}" />
              <dsp:postfield bean="ProfileFormHandler.expireSessionOnLogout" value="false"/>
              <dsp:postfield bean="ProfileFormHandler.logout" value=" " />
            </dsp:go>
          </do>
          <do type="prev" label="Prev">
            <prev/>
          </do>

        </dsp:oparam>
	<dsp:oparam name="default">

          <do type="prev" label="Prev">
            <prev/>
          </do>

        </dsp:oparam>
      </dsp:droplet>

    </card>

</wml>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/userprofiling/wml/logout.jsp#2 $$Change: 651448 $--%>

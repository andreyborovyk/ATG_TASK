<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setBundle var="userprofilingbundle" basename="atg.portal.userprofiling" />

<dsp:page>


<dsp:importbean bean="/atg/userprofiling/InternalProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

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


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title><fmt:message key="changepassword-title" bundle="${userprofilingbundle}"/></title>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="SHORTCUT ICON" href="<c:url value='/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url context='/atg' value='/templates/style/css/login.css'/>" type="text/css" />

  </head>


  <body>
    <div id="centered"> 
      <table border="0" class="wh100p"> 
        <tr> 
          <td class="loginBottom"> 
            <table align="right"> 
              <tr> 
                <td class="loginMiddle" valign="top"><div class="loginHeader">Change Password</div> 
		      <dsp:form name="changePassword" action="/atg/bcc/home" method="post">
		        <dsp:input bean="InternalProfileFormHandler.changePasswordSuccessURL" type="hidden" value="/atg/bcc/home"/>
			<dsp:input bean="InternalProfileFormHandler.changePasswordErrorURL" type="hidden" value="/atg/user/changepassword.jsp"/>
		
		        <!-- set this form to require that the supplied password value should be the same
		             as the confirm password parameter -->
		        <dsp:input bean="InternalProfileFormHandler.confirmPassword" type="hidden" value="true"/>

		        <dsp:droplet name="Switch">
		          <dsp:param bean="InternalProfileFormHandler.formError" name="value"/>
		          <dsp:oparam name="true">

		        <br />
                	<p>
                  	  <span class="error">
	                    <strong>

		              <dsp:droplet name="ProfileErrorMessageForEach">
		                <dsp:param bean="InternalProfileFormHandler.formExceptions" name="exceptions"/>
		                <dsp:oparam name="output">
		                  <li> <dsp:valueof param="message"/>
		                </dsp:oparam>
		              </dsp:droplet>

			    </strong>
	                  </span>
        	        </p>

		          </dsp:oparam>
		        </dsp:droplet>

		        <table class="wh100p loginTable" border="0">
		          <tr> 
		            <td colspan="2"> </td> 
		          </tr>
		          <tr>
		            <td class="formLabel"><span class="loginLabel"><fmt:message key="changepassword-originalpwd" bundle="${userprofilingbundle}"/></span></td>
		            <td><dsp:input bean="InternalProfileFormHandler.value.oldpassword" maxlength="20" type="password" /></td>
		          </tr>

		          <tr>
		            <td class="formLabel"><span class="loginLabel"><fmt:message key="changepassword-newpwd" bundle="${userprofilingbundle}"/></span></td>
		            <td><dsp:input bean="InternalProfileFormHandler.value.password" maxlength="20" type="password" /></td>
		          </tr>

		          <tr>
		            <td class="formLabel"><span class="loginLabel"><fmt:message key="changepassword-newpwdconfirm" bundle="${userprofilingbundle}"/></span></td>
		            <td><dsp:input bean="InternalProfileFormHandler.value.confirmpassword" maxlength="20" type="password"/></td>
		          </tr>

		          <tr>
		            <td valign="middle" align="right"></td>
		            <td><dsp:input iclass="buttonSmall go" bean="InternalProfileFormHandler.changePassword" type="submit" value="save"/></td>
		          </tr>

		        </table>
		      </dsp:form>

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
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/user/html/changepassword.jsp#2 $$Change: 651448 $--%>

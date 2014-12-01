<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<HTML><HEAD><TITLE>Logout</TITLE></HEAD>

<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<div align=center>

<img src="images/banner-login.gif">
<br><br><br>

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">
<dsp:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="../index.jsp"/>
<table border=0 cellpadding=4 cellspacing=0>
  <tr>
    <td>
    <strong><font size=+1>
    <dsp:droplet name="Switch">
      <dsp:param bean="Profile.userType" name="value"/>
      <dsp:oparam name="default">
        Thank you for visiting, 
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param bean="Profile.firstname" name="value"/>
          <dsp:oparam name="unset">
            <dsp:valueof bean="Profile.login"/>.
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:valueof bean="Profile.firstname"/>.
          </dsp:oparam>
        </dsp:droplet>

      </dsp:oparam>
      <dsp:oparam name="guest">
        Thank you for visiting!
      </dsp:oparam>
    </dsp:droplet>
    </font></strong>
    <p>

    <dsp:droplet name="Switch">
      <dsp:param bean="ProfileFormHandler.formError" name="value"/>
      <dsp:oparam name="true">
        <font color=cc0000><STRONG><UL>
        <dsp:droplet name="ProfileErrorMessageForEach">
          <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
          <dsp:oparam name="output">
	          <LI> <dsp:valueof param="message"/>
          </dsp:oparam>
        </dsp:droplet>
        </UL></STRONG></font>
      </dsp:oparam>
    </dsp:droplet>

    <dsp:input bean="ProfileFormHandler.logout" type="SUBMIT" value=" Logout "/>

    </dsp:form>
    </td>
  </tr>
</table>

</div>
</BODY>
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/logout.jsp#2 $$Change: 651448 $--%>

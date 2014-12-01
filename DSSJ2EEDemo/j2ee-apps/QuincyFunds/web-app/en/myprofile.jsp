<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<HTML><HEAD><TITLE>My Profile</TITLE></HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <!-- if not logged in -->
  <dsp:oparam name="guest">
    <table border=0 cellpadding=4 cellspacing=0 width=400>
      <tr valign=top>
        <td width=120 bgcolor=#003366 rowspan=2>
        <!-- left bar navigation -->
        <dsp:include page="nav.jsp" ></dsp:include></td>
        <td>
        <table border=0>
          <tr>
            <td colspan=2><img src="images/banner-editgoals.gif"></td>
          </tr>
          <tr valign=top>
            <td><h2>My Profile</h2>
            You are not currently logged in. If you wish to edit your profile
            please <dsp:a href="login.jsp">log in</dsp:a> first.</td>
          </tr>
        </table></td>
      </tr>
    </table>
  </dsp:oparam>

<dsp:oparam name="default">
<!-- Use the pathInfo of this request as the action for the form -->
<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">
<dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="index.jsp"/>
<table border=0 cellpadding=4 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="nav.jsp" ></dsp:include></td>
      
    <td>
    <table border=0>
      <tr>
        <td colspan=2><img src="images/banner-editgoals.gif"></td>
      </tr>
      <tr valign=top>
        <td><h2>My Profile</h2>

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

        <!--
           This ensures that we update the same profile that we edit.
           It guards against the session expiring while this form is
           displayed (and updating the anonymous profile) or the user
           logging out and in again as a different user in a different
           window
          -->

        <dsp:input bean="ProfileFormHandler.updateRepositoryId" beanvalue="ProfileFormHandler.repositoryId" type="HIDDEN"/>

        <table border=0 cellpadding=2>
          <tr>
            <td align=right>First Name </td>
            <td><dsp:input bean="ProfileFormHandler.value.firstName" maxlength="35" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>Last Name </td>
            <td><dsp:input bean="ProfileFormHandler.value.lastName" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>Email </td>
            <td><dsp:input bean="ProfileFormHandler.value.email" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>Address </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address1" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right></td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address2" maxlength="30" size="30" type="TEXT"/></td>
          </tr>
          <tr>
            <td align=right>City </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.city" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr valign=top>
            <td align=right>State/Province </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.state" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>ZIP/Postal Code </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.postalCode" maxlength="10" size="10" type="TEXT"/></td>
          </tr>
          <tr>
            <td align=right>Country </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.country" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>Phone Number </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.phoneNumber" maxlength="20" size="30" type="TEXT"/>
            </td>
          </tr>

      <tr>
        <td align=right>Birthday (MM/DD/YYYY)</td>
        <td><dsp:input bean="ProfileFormHandler.value.dateOfBirth" date="M/dd/yyyy" maxlength="10" size="10" type="text"/></td>
      </tr>
   
          <tr>
            <td align=right>Gender </td>
            <td>
            <dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="male"/>Male
            <dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="female"/>Female
            </td>
          </tr>

          <tr>
            <td align=right>User Type </td>
            <td>
            <dsp:input bean="ProfileFormHandler.value.userType" type="radio" value="investor"/>Investor
            <dsp:input bean="ProfileFormHandler.value.userType" type="radio" value="broker"/>Broker
            </td>
          </tr>

          <tr>
            <td align=right>Aggressive Index </td>
            <td>
            <dsp:input bean="ProfileFormHandler.value.aggressiveIndex" maxlength="5" size="5" type="TEXT"/></td>
          </tr>
<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
	<dsp:oparam name="investor">
	     <tr>
            <td align=right>Asset Value </td>
            <td>
            <dsp:input bean="ProfileFormHandler.profile.assetValue" maxlength="8" size="8" type="TEXT"/></td>
          </tr>
	</dsp:oparam>
   	<dsp:oparam name="broker">
	     <tr>
            <td align=right>Commission Percentage </td>
            <td>
            <dsp:input bean="ProfileFormHandler.profile.commissionPercentage" maxlength="5" size="5" type="TEXT"/></td>
          </tr>
	</dsp:oparam>
</dsp:droplet>


          <tr valign=bottom>
            <td align=right></td>
            <td><br><dsp:input bean="ProfileFormHandler.update" type="SUBMIT" value=" Save "/>
            <INPUT TYPE="reset" VALUE=" Reset "></td>
          </tr>

        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>
</dsp:form>
</dsp:oparam>
</dsp:droplet>
</BODY>
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/myprofile.jsp#2 $$Change: 651448 $--%>

 <%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>Sign up</title></head>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<div align=center>
<dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="POST">
<dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="../index.jsp"/>
<table border=0 cellpadding=4 width=550>
  <tr>
    <td><img src="images/banner-signup.gif"><br><br></td>
  </tr>
  <tr>
    <td colspan=2><font size=+2>Hi <dsp:valueof bean="Profile.login"/> </font><br>
    To make this site as useful as possible to you we would like to gather some personal information that will help us and your broker deliver exactly the information you need to be a successful investor.</td>
  </tr>

  <dsp:droplet name="Switch">
    <dsp:param bean="ProfileFormHandler.formError" name="value"/>
    <dsp:oparam name="true">
    
    <dsp:droplet name="ProfileErrorMessageForEach">
      <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
      <dsp:oparam name="output">
        <tr><td align=center><font color=cc0000><b><dsp:valueof param="message"/></b></td></tr>
      </dsp:oparam>
    </dsp:droplet>
    
    </dsp:oparam>
  </dsp:droplet>  

    <dsp:input bean="ProfileFormHandler.value.userType" type="hidden" value="investor"/>
  <tr>
    <td>
    <table border=0 cellpadding=4>
      <tr>  
        <td colspan=2><font size=+1><b>Personal Information</b></font></td>
      </tr>
      <tr>
        <td align=right>First Name </td>
        <td><dsp:input bean="ProfileFormHandler.value.firstname" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Last Name </td>
        <td><dsp:input bean="ProfileFormHandler.value.lastname" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Address </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address1" maxlength="30" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td align=right> </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address2" maxlength="30" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td valign=middle align=right>City </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.city" maxlength="30" size="25" type="TEXT"/> </td> 
      </tr>
      <tr>
        <td valign=middle align=right>State/Province</td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.state" maxlength="25" size="25" type="TEXT"/></td>
      </tr>
      
      <tr>
        <td align=right>ZIP/Postal Code </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.postalcode" maxlength="10" size="10" type="TEXT"/></td>
      </tr>

      <tr>
        <td align=right>Country </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.country" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td align=right>Email </td>
        <td><dsp:input bean="ProfileFormHandler.value.email" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Birthday (MM/DD/YYYY)</td>
        <td><dsp:input bean="ProfileFormHandler.value.dateOfBirth" date="M/dd/yyyy" maxlength="10" size="10" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Gender </td>
        <td><dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="male"/>Male
            <dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="female"/>Female
        </td>
      </tr>
      <tr>
        <td valign=top align=right>Choose a Broker </td>  
        <td>
        <dsp:droplet name="/atg/targeting/TargetingForEach">
          <dsp:param bean="/atg/registry/RepositoryTargeters/UserProfiles/BrokerList" name="targeter"/>
          <dsp:param name="fireContentEvent" value="false"/>
          <dsp:param name="fireContentTypeEvent" value="false"/>
          <dsp:oparam name="output">
            <dsp:input bean="ProfileFormHandler.value.brokerId" paramvalue="element.repositoryId" type="radio"/>  
            <dsp:valueof param="element.firstname"/>
            <dsp:valueof param="element.lastname"/><br>
          </dsp:oparam>
        </dsp:droplet></td>
      </tr>
      <tr>
        <td align=right>Language</td>
        <td><dsp:select bean="ProfileFormHandler.value.locale">
              <dsp:option value="en_US"/>English
              <dsp:option value="fr_FR"/>French
              <dsp:option value="de_DE"/>German
              <dsp:option value="ja_JP"/>Japanese
            </dsp:select>
        </td>
      </tr>  
      <tr>
        <td>Send me information through email</td>
        <td><dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="yes"/>Yes
            <dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="no"/>No
        </td>
      </tr>
      <tr>
        <td valign=middle align=right></td>
        <td><dsp:input bean="ProfileFormHandler.update" type="SUBMIT" value=" Save "/>
        <INPUT TYPE="RESET" VALUE=" Reset "></td>
      </tr>
    </table> 
  </td> 
  </tr>
</table>
</dsp:form>
</BODY>
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/signup2.jsp#2 $$Change: 651448 $--%>

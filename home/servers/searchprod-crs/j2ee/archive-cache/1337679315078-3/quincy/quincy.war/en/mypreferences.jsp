<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>My Preferences</title></head>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <!-- not logged in -->
  <dsp:oparam name="guest">
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
            <td> <h2>My Preferences</h2>
              You are not currently logged in. If you wish to edit your 
              preferences please <dsp:a href="login.jsp">log in</dsp:a> first.</td>
          </tr>
        </table></td>
      </tr>
    </table>
  </dsp:oparam>

  <dsp:oparam name="default">

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
       <td><h2>My Preferences</h2>
      <dsp:droplet name="Switch">
        <dsp:param bean="ProfileFormHandler.formError" name="value"/>
        <dsp:oparam name="true">
          <font color=cc0000><STRONG><UL>
          <dsp:droplet name="ProfileErrorMessageForEach">
            <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
            <dsp:oparam name="output">
              <LI><dsp:valueof param="message"/>
            </dsp:oparam>
          </dsp:droplet>
        </UL></STRONG></font>
      </dsp:oparam>
    </dsp:droplet>
        

        <dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">
        <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="../index.jsp"/>

        <table border=0 cellpadding=4>

          <tr valign=top>
            <td align=right>News Topics </td>
             <td><dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="market"/>General Market News<BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="international"/>International Markets <BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="growth"/>Growth Markets<BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="tax"/>Taxes</td>
          </tr>
          <tr valign=top>
            <td align=right>Home Page </td>
            <td> Display <dsp:input bean="ProfileFormHandler.value.numberNewsItems" maxlength="2" size="2" type="text"/> news items.<br>
             Display <dsp:input bean="ProfileFormHandler.value.numberFeatureItems" maxlength="2" size="2" type="text"/> feature articles.</td>
          </tr>
          <tr valign=top>   
            <td align=right>Broker </td>
            <td>
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/UserProfiles/BrokerList" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="output">
                 <dsp:input bean="ProfileFormHandler.value.brokerId" paramvalue="element.repositoryId" type="radio"/>
                 <dsp:valueof param="element.firstName"/> 
                 <dsp:valueof param="element.lastName"/><br>
             </dsp:oparam>
            </dsp:droplet>
            </td>
          </tr>
          <tr>
            <td align=right>Language </td>
            <td><dsp:select bean="ProfileFormHandler.value.locale">
                 <dsp:option value="en_US"/>English
                 <dsp:option value="fr_FR"/>French
                 <dsp:option value="de_DE"/>German
                 <dsp:option value="ja_JP"/>Japanese
               </dsp:select>
            </td>
          </tr>  
          <tr>
            <td align=right>Send me information through email</td>
        <td><dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="yes"/>Yes
            <dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="no"/>No
        </td>
          </tr>

          <tr>
             <td></td>
             <td><br><dsp:input bean="ProfileFormHandler.update" type="submit" value=" Save "/>
             <input type="reset" value=" Reset "></td>
           </tr>
         </table>
         </dsp:form>
         </td>
       </tr>
     </table>
     </td>
      </tr>
    </table>
  </dsp:oparam>
</dsp:droplet>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/mypreferences.jsp#2 $$Change: 651448 $--%>

 <%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>My Goals</title></head>

<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>
<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <!-- not logged in -->
  <dsp:oparam name="guest">
    You are not currently logged in. If you wish to modify your goals you
    need to register or login.
    <dsp:a href="login.jsp">Login</dsp:a>
  </dsp:oparam>

  <dsp:oparam name="default">
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
           <td>
           <table width=430>
             <tr>
               <td colspan=5>
               <b>Change your investment goals with Quincy</b>
               <p>
               By telling us what your investment plans are, we are better able to
               help you meet your goals. We can match your actual portfolio with
               what you expect it to earn, to make sure that you're on the right 
               path. It's easy! 
               <p>
               <b>Tell us about your investment style</b>
               <br>&nbsp;<br></td>
             </tr>

             <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="POST">
    <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="index.jsp"/>
             <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="/de/index.jsp"/>

             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="aggressive"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-aa.gif"></td>
               <td>&nbsp;</td>
               <td>Aggressive ( high risk/return )</td>
             </tr>

             <tr>
                <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="moderate"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-bb.gif"></td>
               <td>&nbsp;</td>
               <td>Moderate</td>
             </tr>
  
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.strategy" name="style" type="radio" value="conservative"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-cc.gif"></td>
               <td>&nbsp;</td>
               <td>Conservative ( low risk/return )</td>
             </tr>  
     
             <tr>
               <td colspan=5>&nbsp;<br>
               <b>Now tell us how far you're looking into the future</b>
               <br>&nbsp;</td>
             </tr>
  
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="short-term"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-sr.gif"></td>
               <td>&nbsp;</td> 
               <td>Short-term</td>
             </tr>
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="long-term"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-lr.gif"></td>
               <td>&nbsp;</td> 
               <td>Long-term</td>
             </tr>
             <tr>
               <td><dsp:input bean="ProfileFormHandler.value.goals" name="range" type="radio" value="retirement-focus"/></td>
               <td>&nbsp;</td>
               <td><img src="images/spec-rr.gif"></td>
               <td>&nbsp;</td> 
               <td>Retirement-focus</td> 
            </tr>
     
            <tr>
              <td colspan=5><br>
              <dsp:input bean="ProfileFormHandler.update" type="submit" value="Change My Goals"/>
              <br>&nbsp;<br>&nbsp;</td>
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
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/mygoals.jsp#2 $$Change: 651448 $--%>

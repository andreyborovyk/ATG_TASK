<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/targeting/TargetingForEach" />

<HTML> 
 <HEAD>
  <TITLE>Investing in Quincy Funds just got easier!</TITLE>
 </HEAD>

 <BODY BGCOLOR="#FFFFFF" TEXT="#00214A" VLINK="#637DA6" LINK="#E87F02">
  <font size=2>Dear
   <dsp:droplet name="Switch">
    <dsp:param name="value" bean="Profile.firstname" />
    <dsp:oparam name="unset">
       Sir or Madam,
    </dsp:oparam>
    <dsp:oparam name="default">
      <b><dsp:valueof bean="Profile.firstName"></dsp:valueof></b>
      <b><dsp:valueof bean="Profile.lastName"></dsp:valueof></b>,
    </dsp:oparam>
   </dsp:droplet>
  </font>

  <font size=2><p>
   Investing in Quincy Funds just got easier and more rewarding.
   <p>Check out the range of mutual funds, bonds and market funds that we
   now offer:
   <p>
 
   <!-- Stock -->
   <dsp:droplet name="TargetingForEach">
    <dsp:param name="targeter" 
	       bean="/atg/registry/RepositoryTargeters/Funds/stockFunds" />
    <dsp:param name="fireContentEvent" value="false" />
    <dsp:param name="fireContentTypeEvent" value="false" />
    <dsp:oparam name="outputStart">
     <b>Stock Mutual Funds</b>
      <ul>
    </dsp:oparam>

    <dsp:oparam name="output">
     <dsp:a href="../en/fund.jsp">
      <dsp:param name="ElementId" param="element.repositoryId" />
      <dsp:valueof param="element.fundName"></dsp:valueof>
     </dsp:a> - 
     <dsp:valueof param="element.symbol"></dsp:valueof><br>
    </dsp:oparam>

    <dsp:oparam name="outputEnd">
     </ul>
    </dsp:oparam>
  </dsp:droplet>

  <!-- Bond -->
  <dsp:droplet name="TargetingForEach">
   <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/bondFunds" 
	      name="targeter" />
   <dsp:param name="fireContentEvent" value="false" />
   <dsp:param name="fireContentTypeEvent" value="false" />
   <dsp:oparam name="outputStart">
    <b>Bond Mutual Funds</b>
      <ul>
   </dsp:oparam>
  
   <dsp:oparam name="output">
    <font size=2>
     <dsp:a href="../en/fund.jsp">
      <dsp:param name="ElementId" param="element.repositoryId" />
      <dsp:valueof param="element.fundName"></dsp:valueof>
     </dsp:a> - 
     <dsp:valueof param="element.symbol"></dsp:valueof><br>
    </font>
   </dsp:oparam>

   <dsp:oparam name="outputEnd">
	</ul>
   </dsp:oparam>
  </dsp:droplet>
 
  <!-- Market -->
  <dsp:droplet name="TargetingForEach">
   <dsp:param bean="/atg/registry/RepositoryTargeters/Funds/marketFunds" 
	      name="targeter" />
   <dsp:param name="fireContentEvent" value="false" />
   <dsp:param name="fireContentTypeEvent" value="false" />
   <dsp:oparam name="outputStart">
    <font size=2>
     <b>Money Market Mutual Funds</b>
      <ul>	
   </dsp:oparam>

   <dsp:oparam name="output">
    <dsp:a href="../en/fund.jsp">
     <dsp:param name="ElementId" param="element.repositoryId" />
     <dsp:valueof param="element.fundName"></dsp:valueof>
    </dsp:a> - 
    <dsp:valueof param="element.symbol"></dsp:valueof><br>
   </dsp:oparam>

   <dsp:oparam name="outputEnd">
     </ul>
    </font>	
   </dsp:oparam>
  </dsp:droplet>

  <font size=2>
   Whatever your investment goals, we have the mix of funds to suit you.
   <p>
  </font>
 </body>
</html>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/funds.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/targeting/TargetingRange" />

<HTML> 
 <HEAD>
  <TITLE>Latest News and Features From Quincy Funds</TITLE>
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

   <p> Check out the latest news and features at Quincy Funds online:<p>

   <!-- news -->
   <b>News:</b>
   <ul>
    <dsp:droplet name="TargetingRange">
     <dsp:param bean="/atg/registry/RepositoryTargeters/News/NewsEmail" 
	        name="targeter" />
     <dsp:param name="fireContentEvent" value="false" />
     <dsp:param name="fireContentTypeEvent" value="false" />
     <dsp:param name="howMany" bean="Profile.numbernewsitems" />
     <dsp:param name="sortProperties" value="+headline" />

     <dsp:oparam name="output">
      <li><dsp:valueof param="element.headline"></dsp:valueof></li>
     </dsp:oparam>
	
     <dsp:oparam name="empty">  
      <li>No news today</li>
     </dsp:oparam>
    </dsp:droplet>
   </ul>

   <b>Features:</b>
   <ul>
    <dsp:droplet name="TargetingRange">
     <dsp:param bean="/atg/registry/RepositoryTargeters/Features/FeaturesEmail" 
                name="targeter" />
      <dsp:param name="fireContentEvent" value="false" />
      <dsp:param name="fireContentTypeEvent" value="false" />
      <dsp:param name="howMany" bean="Profile.numberfeatureitems" />
      <dsp:param name="sortProperties" value="+title" />

      <dsp:oparam name="outputStart">
      </dsp:oparam>

      <dsp:oparam name="output">
       <li>
	<font size=2>
         <b><dsp:valueof param="element.title"></dsp:valueof></b><br>
	    <dsp:valueof param="element.headline"></dsp:valueof> 
	</font>
       </li>
      </dsp:oparam>

      <dsp:oparam name="empty">
       <li><font size=2>No Features today.</li>
      </dsp:oparam>
    </dsp:droplet>
   </ul>

   <p>
  </body>
 </html>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/news.jsp#2 $$Change: 651448 $--%>

 <%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<HTML> <HEAD>
<TITLE>Quincy Funds - <dsp:valueof bean="Profile.usertype"/> home</TITLE>
</HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>
<!-- remove this note when all content is translated -->
<font size=2 color=666666>Note: only the features and navigation bar have been localized.<p></font>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.usertype" name="value"/>
  <dsp:oparam name="broker">
    <dsp:include page="brokerhome.jsp" ></dsp:include>
  </dsp:oparam>

  <dsp:oparam name="investor">
    <dsp:include page="investorhome.jsp" ></dsp:include>
  </dsp:oparam>
  
  <dsp:oparam name="default">
    <dsp:include page="guesthome.jsp" ></dsp:include>
  </dsp:oparam>
</dsp:droplet>

</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/index.jsp#2 $$Change: 651448 $--%>

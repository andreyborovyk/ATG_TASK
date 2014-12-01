<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<HTML> <HEAD>
<TITLE>Quincy Funds - <dsp:valueof bean="Profile.usertype"/> ÉzÅ[ÉÄ</TITLE>
</HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

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
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/index.jsp#2 $$Change: 651448 $--%>

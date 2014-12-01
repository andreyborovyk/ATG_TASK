<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/dynamo/servlet/RequestLocale.locale.language" name="value"/>
  <dsp:oparam name="fr"><%response.sendRedirect("fr");%></dsp:oparam>
  <dsp:oparam name="de"><%response.sendRedirect("de");%></dsp:oparam>
  <dsp:oparam name="ja"><%response.sendRedirect("ja");%></dsp:oparam>
  <dsp:oparam name="en"><%response.sendRedirect("en");%></dsp:oparam>
  <dsp:oparam name="default"><%response.sendRedirect("en/default.jsp");%></dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/redirect.jsp#2 $$Change: 651448 $--%>

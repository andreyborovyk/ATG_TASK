<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:setvalue bean="/atg/dynamo/servlet/RequestLocale.refresh" value=" "/>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/dynamo/servlet/RequestLocale.locale.language" name="value"/>
  <dsp:oparam name="fr">
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="fr/index.jsp"/>
      </dsp:droplet>
 </dsp:oparam>
  <dsp:oparam name="de">
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="de/index.jsp"/>
      </dsp:droplet>
 </dsp:oparam>
  <dsp:oparam name="ja">
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="ja/index.jsp"/>
      </dsp:droplet>
 </dsp:oparam>
  <dsp:oparam name="en">
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="en/index.jsp"/>
      </dsp:droplet>
 </dsp:oparam>
   <dsp:oparam name="default">
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="en/index.jsp"/>
      </dsp:droplet>
</dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/index.jsp#2 $$Change: 651448 $--%>

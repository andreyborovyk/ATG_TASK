<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param bean="/atg/userprofiling/Profile.activePromotions" name="array"/>
  <dsp:oparam name="outputStart">
    <b>以下のプロモーションがあります:</b><p>
  </dsp:oparam>
  <dsp:oparam name="output">
    <dsp:valueof param="element.promotion.displayName"/><br>
  </dsp:oparam>
  
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/DisplayActivePromotions.jsp#2 $$Change: 651448 $--%>

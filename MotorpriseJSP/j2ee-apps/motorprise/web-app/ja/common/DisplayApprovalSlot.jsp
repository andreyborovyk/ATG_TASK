<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:droplet name="/atg/targeting/TargetingFirst">
  <dsp:param bean="/atg/registry/Slots/ApprovalsSlot" name="targeter"/>
  <dsp:param name="howMany" value="1"/>
  <dsp:oparam name="output">
    <dsp:valueof param="element.data"/>
  </dsp:oparam>
  <dsp:oparam name="empty">
    承認は必要ありません。
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/DisplayApprovalSlot.jsp#2 $$Change: 651448 $--%>

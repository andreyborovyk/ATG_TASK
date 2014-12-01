<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.costCenters"/>
  <dsp:param name="elementName" value="costCenter"/>

  <dsp:oparam name="outputStart">
    <tr valign=top>
      <td align=right><span class=smallb>コストセンタ</span></td>
      <td width=75%>
  </dsp:oparam>

  <dsp:oparam name="output">
    <dsp:valueof param="costCenter.Identifier">N/A</dsp:valueof> -
      <dsp:droplet name="ForEach">
        <dsp:param bean="Profile.costCenters" name="array"/>
        <dsp:param name="elementName" value="CC"/>
        <dsp:oparam name="output">
          <dsp:droplet name="Switch">
            <dsp:param name="value" param="CC.identifier"/>
            <dsp:getvalueof id="ccId" param="costCenter.Identifier" idtype="String">
              <dsp:oparam name="<%=ccId%>">
                <dsp:valueof param="CC.description"/>
              </dsp:oparam>
            </dsp:getvalueof>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet> <BR>
  </dsp:oparam>

  <dsp:oparam name="outputEnd">
    </td>
    </tr>
  </dsp:oparam>

</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/displayCostCenterInfo.jsp#2 $$Change: 651448 $--%>

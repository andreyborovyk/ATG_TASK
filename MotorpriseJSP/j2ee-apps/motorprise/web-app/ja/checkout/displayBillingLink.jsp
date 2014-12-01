<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


          <dsp:droplet name="/atg/projects/b2bstore/order/ItemsSplitbyType">
            <dsp:param bean="ShoppingCart.current" name="order"/>
            <dsp:param name="type" value="paymentGroup"/>

            <dsp:oparam name="true">
              <tr valign=top>
                <td></td>
                <td><span class=smallb><dsp:a href="split_payment.jsp?init=false">êøãÅÇÃï“èW</dsp:a></span></td>
              </tr>
            </dsp:oparam>

						<dsp:oparam name="false">
							<dsp:droplet name="ForEach">
								<dsp:param bean="ShoppingCart.current.paymentGroups" name="array"/>
								<dsp:oparam name="outputStart">
									<dsp:droplet name="Switch">
								<dsp:param name="value" param="size"/>
								<dsp:oparam name="1">
									<tr>
										<td></td>
										<td><span class=smallb><dsp:a href="billing.jsp?init=true">êøãÅÇÃï“èW </dsp:a></span></td>
									</tr>       
								</dsp:oparam>
								<dsp:oparam name="default">
									<tr>
										<td></td>
										<td><span class=smallb><dsp:a href="split_payment_order.jsp?init=false">êøãÅÇÃï“èW </dsp:a></span></td>
									</tr>       
								</dsp:oparam>
							</dsp:droplet>

								
								</dsp:oparam>
					    </dsp:droplet>
						
				    </dsp:oparam>
						</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/displayBillingLink.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<%/* A shopping cart-like display of order information */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>

<dsp:droplet name="IsNull">
  <dsp:param name="value" param="order.shippingGroups"/>
  <dsp:oparam name="true">
   <p>このオーダーには、配達グループがありません。
  </dsp:oparam>
  <dsp:oparam name="false">
    <table>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="order.shippingGroups"/>
      <dsp:param name="elementName" value="sg"/>
      <dsp:oparam name="empty">
	配達グループがありません。<br>
      </dsp:oparam>
      <dsp:oparam name="output">
	<tr valign=top>
	  <td>
	      <b>次のアイテムを配達します：</b><br>
	      <dsp:droplet name="ForEach">
		<dsp:param name="array" param="sg.commerceItemRelationships"/>
		<dsp:param name="elementName" value="itemRel"/>
		<dsp:oparam name="empty">
		  この配達グループにはアイテムがありません。
		</dsp:oparam>
		<dsp:oparam name="output">
		  <dsp:droplet name="Switch">
		    <dsp:param name="value" param="itemRel.relationshipTypeAsString"/>
		    <dsp:oparam name="SHIPPINGQUANTITY">
		      の <dsp:valueof param="itemRel.quantity">数量が指定されていません。</dsp:valueof>
		    </dsp:oparam> 
		    <dsp:oparam name="SHIPPINGQUANTITYREMAINING">
		      未配達分： 
		    </dsp:oparam> 
	          </dsp:droplet>
		  <dsp:valueof param="itemRel.commerceItem.auxiliaryData.catalogRef.displayName">表示名なし。</dsp:valueof>
		  <br>
	        </dsp:oparam>
	      </dsp:droplet>
	  </td>
	  <td>    
	      <p><b>次の情報を使用：</b><br>
	      <dsp:droplet name="Switch">
		<dsp:param name="value" param="sg.shippingGroupClassType"/>
		<dsp:oparam name="hardgoodShippingGroup">
		  <dsp:valueof param="sg.shippingMethod">配達方法の指定なし</dsp:valueof>による配達先：<BR>
		  <dsp:valueof param="sg.shippingAddress.lastName"/> 
		  <dsp:valueof param="sg.shippingAddress.firstName"/><BR>
		  <dsp:valueof param="sg.shippingAddress.address1"/> 
		  <dsp:valueof param="sg.shippingAddress.address2"/><BR>
		  <dsp:valueof param="sg.shippingAddress.city"/>, 
		  <dsp:valueof param="sg.shippingAddress.state"/> 
		  <dsp:valueof param="sg.shippingAddress.postalCode"/><BR>
	       </dsp:oparam>
		<dsp:oparam name="b2bHardgoodShippingGroup">
		  <dsp:valueof param="sg.shippingMethod">配達方法の指定なし</dsp:valueof>による配達先：<BR>
		  <dsp:valueof param="sg.shippingAddress.lastName"/> 
		  <dsp:valueof param="sg.shippingAddress.firstName"/><BR>
		  <dsp:valueof param="sg.shippingAddress.jobTitle"/><BR>
		  <dsp:valueof param="sg.shippingAddress.companyName"/><BR>
		  <dsp:valueof param="sg.shippingAddress.address1"/> 
		  <dsp:valueof param="sg.shippingAddress.address2"/><BR>
		  <dsp:valueof param="sg.shippingAddress.city"/>, 
		  <dsp:valueof param="sg.shippingAddress.state"/> 
		  <dsp:valueof param="sg.shippingAddress.postalCode"/><BR>
	       </dsp:oparam>
	       <dsp:oparam name="electronicShippingGroup">
		 電子メールによる配達先：<BR>
		 <dsp:valueof param="sg.emailAddress">不明な電子メールアドレス</dsp:valueof><BR>
	       </dsp:oparam>
	     </dsp:droplet>
	 </td>
       </tr>
       <tr></tr>
      </dsp:oparam>
    </dsp:droplet>    
    </table>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/fulfillment/DisplayShippingInfo.jsp#2 $$Change: 651448 $--%>

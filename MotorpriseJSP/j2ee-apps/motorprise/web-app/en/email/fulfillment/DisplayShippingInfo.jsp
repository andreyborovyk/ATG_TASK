<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%/* A shopping cart-like display of order information */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>

<dsp:droplet name="IsNull">
  <dsp:param name="value" param="order.shippingGroups"/>
  <dsp:oparam name="true">
   <p>There are no shipping groups in this order.
  </dsp:oparam>
  <dsp:oparam name="false">
    <table>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="order.shippingGroups"/>
      <dsp:param name="elementName" value="sg"/>
      <dsp:oparam name="empty">
	There were no shipping groups.<br>
      </dsp:oparam>
      <dsp:oparam name="output">
	<tr valign=top>
	  <td>
	      <b>The following items ship:</b><br>
	      <dsp:droplet name="ForEach">
		<dsp:param name="array" param="sg.commerceItemRelationships"/>
		<dsp:param name="elementName" value="itemRel"/>
		<dsp:oparam name="empty">
		  There were no items in this shipping group.
		</dsp:oparam>
		<dsp:oparam name="output">
		  <dsp:droplet name="Switch">
		    <dsp:param name="value" param="itemRel.relationshipTypeAsString"/>
		    <dsp:oparam name="SHIPPINGQUANTITY">
		      <dsp:valueof param="itemRel.quantity">No quantity specified</dsp:valueof> of
		    </dsp:oparam> 
		    <dsp:oparam name="SHIPPINGQUANTITYREMAINING">
		      The rest of 
		    </dsp:oparam> 
	          </dsp:droplet>
		  <dsp:valueof param="itemRel.commerceItem.auxiliaryData.catalogRef.displayName">No display name.</dsp:valueof>
		  <br>
	        </dsp:oparam>
	      </dsp:droplet>
	  </td>
	  <td>    
	      <p><b>Using this information:</b><br>
	      <dsp:droplet name="Switch">
		<dsp:param name="value" param="sg.shippingGroupClassType"/>
		<dsp:oparam name="hardgoodShippingGroup">
		  <i>Ship via <dsp:valueof param="sg.shippingMethod">No shipping method</dsp:valueof> to:</i><BR>
		  <dsp:valueof param="sg.shippingAddress.firstName"/> 
		  <dsp:valueof param="sg.shippingAddress.lastName"/><BR>
		  <dsp:valueof param="sg.shippingAddress.address1"/> 
		  <dsp:valueof param="sg.shippingAddress.address2"/><BR>
		  <dsp:valueof param="sg.shippingAddress.city"/>, 
		  <dsp:valueof param="sg.shippingAddress.state"/> 
		  <dsp:valueof param="sg.shippingAddress.postalCode"/><BR>
	       </dsp:oparam>
		<dsp:oparam name="b2bHardgoodShippingGroup">
		  <i>Ship via <dsp:valueof param="sg.shippingMethod">No shipping method</dsp:valueof> to:</i><BR>
		  <dsp:valueof param="sg.shippingAddress.firstName"/> 
		  <dsp:valueof param="sg.shippingAddress.lastName"/><BR>
		  <dsp:valueof param="sg.shippingAddress.jobTitle"/><BR>
		  <dsp:valueof param="sg.shippingAddress.companyName"/><BR>
		  <dsp:valueof param="sg.shippingAddress.address1"/> 
		  <dsp:valueof param="sg.shippingAddress.address2"/><BR>
		  <dsp:valueof param="sg.shippingAddress.city"/>, 
		  <dsp:valueof param="sg.shippingAddress.state"/> 
		  <dsp:valueof param="sg.shippingAddress.postalCode"/><BR>
	       </dsp:oparam>
	       <dsp:oparam name="electronicShippingGroup">
		 <i>Ship via email to:</i><BR>
		 <dsp:valueof param="sg.emailAddress">unknown email address</dsp:valueof><BR>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/fulfillment/DisplayShippingInfo.jsp#2 $$Change: 651448 $--%>

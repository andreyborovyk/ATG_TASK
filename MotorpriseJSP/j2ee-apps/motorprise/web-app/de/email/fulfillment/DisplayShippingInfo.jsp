<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%/* A shopping cart-like display of order information */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>

<dsp:droplet name="IsNull">
  <dsp:param name="value" param="order.shippingGroups"/>
  <dsp:oparam name="true">
   <p>Dieser Auftrag enthält keine Versandgruppen.
  </dsp:oparam>
  <dsp:oparam name="false">
    <table>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="order.shippingGroups"/>
      <dsp:param name="elementName" value="sg"/>
      <dsp:oparam name="empty">
	Es waren keine Versandgruppen vorhanden.<br>
      </dsp:oparam>
      <dsp:oparam name="output">
	<tr valign=top>
	  <td>
	      <b>Folgende Artikel werden versandt:</b><br>
	      <dsp:droplet name="ForEach">
		<dsp:param name="array" param="sg.commerceItemRelationships"/>
		<dsp:param name="elementName" value="itemRel"/>
		<dsp:oparam name="empty">
		  In dieser Versandgruppe waren keine Artikel vorhanden.
		</dsp:oparam>
		<dsp:oparam name="output">
		  <dsp:droplet name="Switch">
		    <dsp:param name="value" param="itemRel.relationshipTypeAsString"/>
		    <dsp:oparam name="SHIPPINGQUANTITY">
		      <dsp:valueof param="itemRel.quantity">Keine Menge angegeben</dsp:valueof> von
		    </dsp:oparam> 
		    <dsp:oparam name="SHIPPINGQUANTITYREMAINING">
		      Der Rest von
		    </dsp:oparam> 
	          </dsp:droplet>
		  <dsp:valueof param="itemRel.commerceItem.auxiliaryData.catalogRef.displayName">Kein Anzeigename.</dsp:valueof>
		  <br>
	        </dsp:oparam>
	      </dsp:droplet>
	  </td>
	  <td>    
	      <p><b>Folgende Daten werden verwendet:</b><br>
	      <dsp:droplet name="Switch">
		<dsp:param name="value" param="sg.shippingGroupClassType"/>
		<dsp:oparam name="hardgoodShippingGroup">
		  <i>Versand per <dsp:valueof param="sg.shippingMethod">Keine Versandart</dsp:valueof> an:</i><BR>
		  <dsp:valueof param="sg.shippingAddress.firstName"/> 
		  <dsp:valueof param="sg.shippingAddress.lastName"/><BR>
		  <dsp:valueof param="sg.shippingAddress.address1"/> 
		  <dsp:valueof param="sg.shippingAddress.address2"/><BR>
		  <dsp:valueof param="sg.shippingAddress.city"/>, 
		  <dsp:valueof param="sg.shippingAddress.state"/> 
		  <dsp:valueof param="sg.shippingAddress.postalCode"/><BR>
	       </dsp:oparam>
		<dsp:oparam name="b2bHardgoodShippingGroup">
		  <i>Versand per <dsp:valueof param="sg.shippingMethod">Keine Versandart</dsp:valueof> an:</i><BR>
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
		 <i>Versand per E-Mail an:</i><BR>
		 <dsp:valueof param="sg.emailAddress">unbekannte E-Mail-Adresse</dsp:valueof><BR>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/fulfillment/DisplayShippingInfo.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="http://www.atg.com/dsp.tld" prefix="dsp" %>
<dsp:page>

<%/* A shopping cart-like display of order information  */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>

<dsp:droplet name="IsNull">
  <dsp:param name="value" param="order.paymentGroups"/>
  <dsp:oparam name="true">
   <p>There are no payment groups in this order.
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="order.paymentGroups"/>
      <dsp:param name="elementName" value="pg"/>
      <dsp:oparam name="empty">
	There were no payment groups.<br>
      </dsp:oparam>
      <dsp:oparam name="output">
	<dsp:droplet name="Switch">
	  <dsp:param name="value" param="pg.paymentMethod"/>
	  <dsp:oparam name="creditCard">
            <p><b>Payment information: credit card</b><br>
	    Amount = <dsp:valueof converter="currency" param="pg.amount"/><br>
	    <i>Pay with <dsp:valueof param="pg.creditCardType"/> :</i> #
	    <dsp:valueof converter="creditcard" groupingsize="4" param="pg.creditCardNumber">no number</dsp:valueof><BR>
	    Expiration date is <dsp:valueof param="pg.expirationMonth"/>/
	    <dsp:valueof param="pg.expirationYear"/><br>
	    <i>Billing address:</i><br>
	    <dsp:valueof param="pg.billingAddress.firstName"/> 
	    <dsp:valueof param="pg.billingAddress.lastName"/><BR>
	    <dsp:valueof param="pg.billingAddress.address1"/> 
	    <dsp:valueof param="pg.billingAddress.address2"/><BR>
	    <dsp:valueof param="pg.billingAddress.city"/>, 
	    <dsp:valueof param="pg.billingAddress.state"/> 
	    <dsp:valueof param="pg.billingAddress.postalCode"/><BR>
	  </dsp:oparam>
	  <dsp:oparam name="giftCertificate">
            <p><b>Payment information: <dsp:valueof param="pg.paymentMethod"/></b><br>
	    Amount = <dsp:valueof converter="currency" param="pg.amount"/><br>
	    <i>Pay with gift certificate <dsp:valueof param="pg.giftCertificateNumber"/> :</i><br>
	  </dsp:oparam>
          <dsp:oparam name="default">
            <p><b>Payment information: <dsp:valueof param="pg.paymentMethod"/></b><br>
	    Amount = <dsp:valueof converter="currency" param="pg.amount"/><br>
          </dsp:oparam>
	</dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>    
  </dsp:oparam>
</dsp:droplet>


</dsp:page>
<%-- @version $Id: //product/DCS/version/10.0.3/release/Fulfillment/html/en/email_templates/jsp/DisplayPaymentInfo.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyConversionFormatter"/>

<dsp:droplet name="/atg/targeting/TargetingForEach">
  <dsp:param bean="/atg/registry/Slots/PreferredVendorSpecials" name="targeter"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:oparam name="outputStart">
    <table border=0 cellpadding=0 cellspacing=0 width=195>
      <tr> 
        <td>
        <table border=0 cellpadding=3 cellspacing=0 width=100%>
          <tr>
            <td bgcolor="#999999" height=16>
            <span class=smallbw>
             <dsp:droplet name="Switch">
              <dsp:param bean="Profile.transient" name="value"/>
              <dsp:oparam name="false">
                Sonderangebote für bevorzugte Anbieter
              </dsp:oparam>
              <dsp:oparam name="true"> 
                Produkt-Sonderangebote
              </dsp:oparam>
            </dsp:droplet>
            </span></td>
          </tr>
        </table></td>
      </tr>
      <tr>
        <td><dsp:img src="../images/d.gif"/></td>
      </tr>
  </dsp:oparam>
  <dsp:oparam name="outputEnd">
    </table>
  </dsp:oparam>
  <dsp:oparam name="output">
    <tr valign=top>
     <td>
     <table border=0 cellpadding=0 cellspacing=0 width=100% bgcolor="#E5E5E5">
       <tr><td><dsp:img src="../images/d.gif"/></td></tr>
       <tr>
         <td> 
         <table border=0 cellpadding=4 width=100%>
          <tr>
          <td valign="top">
            
	  <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
	  <dsp:getvalueof id="imageURL" idtype="java.lang.String" param="element.smallImage.url">
	  <dsp:a page="<%=urlStr%>">
	    <dsp:param name="id" param="element.repositoryId"/>
	    <dsp:param name="navAction" value="jump"/>
	    <dsp:param name="Item" param="element"/>
            <font color="#000000">
	    <core:switch value="<%=imageURL%>">
	      <core:case value="<%=null%>">
		<img border="1" hspace="10" src="../images/default-small.jpg"></core:case>
	      <core:defaultCase>
	        <img border="1" hspace="10" src="<%=imageURL%>"></core:defaultCase>
	    </core:switch>
	    </font>
	  </dsp:a>
	  </dsp:getvalueof>
	  </dsp:getvalueof>
          </td>

          <td>
          <span class="smallb">
	  <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
          <dsp:a page="<%=urlStr%>">
            <dsp:param name="id" param="element.repositoryId"/>
            <dsp:param name="navAction" value="jump"/>
            <dsp:param name="Item" param="element"/>
            <dsp:valueof param="element.displayName">Kein Name</dsp:valueof>
          </dsp:a>
	  </dsp:getvalueof>
              
            </span><br>
            <span class=small><dsp:valueof param="element.description">Keine Beschr.</dsp:valueof>.<br>
            <dsp:droplet name="Switch">
              <dsp:param bean="Profile.transient" name="value"/>
              <dsp:oparam name="false">
                <dsp:img src="../images/d.gif" vspace="2"/><br>
                <b>Preis:
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="element.childSKUs"/>
                  <dsp:param name="elementName" value="SKU"/>
                  <dsp:oparam name="output">
                    <dsp:droplet name="/atg/commerce/pricing/PriceItem">
                      <dsp:param name="product" param="element"/>
                      <dsp:param name="item" param="SKU"/>
                      <dsp:param name="elementName" value="PricingCommerceItem"/>
                      <dsp:oparam name="output">
                         <%-- Display price for locale of user: --%>
                         <dsp:droplet name="CurrencyConversionFormatter">
                           <dsp:param name="currency" param="PricingCommerceItem.priceInfo.amount"/>
                           <dsp:param name="locale" bean="Profile.priceList.locale"/>
                           <dsp:param name="targetLocale" bean="Profile.priceList.locale"/>
                           <dsp:param name="euroSymbol" value="&euro;"/>
                           <dsp:oparam name="output">
                             <span class=small><dsp:valueof valueishtml="<%=true%>" param="formattedCurrency">Kein Preis</dsp:valueof></span>
                           </dsp:oparam>
                         </dsp:droplet>
                         <br>
                        <%-- Display price in DM for German users: --%>
                        <dsp:droplet name="CurrencyConversionFormatter">
                          <dsp:param name="currency" param="PricingCommerceItem.priceInfo.amount"/>
                          <dsp:param name="locale" bean="Profile.priceList.locale"/>
                          <dsp:param name="targetLocale" value="de_DE"/>
                          <dsp:oparam name="output">
                            <span class=small><dsp:valueof param="formattedCurrency">Kein Preis</dsp:valueof></span>    
                          </dsp:oparam>
                        </dsp:droplet> 
                      </dsp:oparam>
                    </dsp:droplet>            
                  </dsp:oparam>
                </dsp:droplet></b><br><dsp:img src="../images/d.gif" vspace="2"/><br>
              </dsp:oparam>

            </dsp:droplet></td>
          </tr>
        </table></td>
      <tr><td bgcolor="#999999"><dsp:img src="../images/d.gif"/></td></tr>
      <tr><td bgcolor="#FFFFFF"><dsp:img src="../images/d.gif"/></td></tr>
    </table>
   
</dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/common/SpecialSlot.jsp#2 $$Change: 651448 $--%>

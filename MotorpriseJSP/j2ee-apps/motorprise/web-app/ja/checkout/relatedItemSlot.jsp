<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:droplet name="/atg/targeting/TargetingArray">
  <dsp:param bean="/atg/registry/Slots/RelatedItemsOfCart" name="targeter"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:oparam name="output">

  <dsp:droplet name="/atg/commerce/catalog/ForEachItemInCatalog">
  <dsp:param name="array" param="elements"/>

  <dsp:oparam name="outputStart">
    <table border=0 cellpadding=0 cellspacing=0 width=195>
      <tr> 
        <td>
        <table border=0 cellpadding=3 cellspacing=0 width=100%>
          <tr>
            <td bgcolor="#999999" height=16>

            <span class=smallbw>関連アイテム</span></td>
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
            <dsp:valueof param="element.displayName">名前なし</dsp:valueof>
          </dsp:a>
	  </dsp:getvalueof>
              
            </span><br>
            <span class=small><dsp:valueof param="element.description">説明なし</dsp:valueof>.<br>
            <dsp:droplet name="Switch">
              <dsp:param bean="Profile.transient" name="value"/>
              <dsp:oparam name="false">
                <dsp:img src="../images/d.gif" vspace="2"/><br>
                <b>価格：
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="element.childSKUs"/>
                  <dsp:param name="elementName" value="SKU"/>
                  <dsp:oparam name="output">
                    <dsp:droplet name="/atg/commerce/pricing/PriceItem">
                      <dsp:param name="product" param="element"/>
                      <dsp:param name="item" param="SKU"/>
                      <dsp:param name="elementName" value="PricingCommerceItem"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof id="pval0" param="PricingCommerceItem.priceInfo.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>                
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
 
</dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/relatedItemSlot.jsp#2 $$Change: 651448 $--%>

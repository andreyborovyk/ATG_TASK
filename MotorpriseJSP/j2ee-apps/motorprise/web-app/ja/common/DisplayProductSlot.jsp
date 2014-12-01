<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:droplet name="/atg/targeting/TargetingForEach">
  <dsp:param bean="/atg/registry/Slots/PreferredVendorSpecials" name="targeter"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:oparam name="outputStart">
    <table border=0 cellpadding=0 cellspacing=0>
  </dsp:oparam>
  <dsp:oparam name="outputEnd">
    </table>
  </dsp:oparam>
  <dsp:oparam name="output">
       <tr valign=top>
         <td><img border="0" src="<dsp:valueof param="element.smallImage.url"/>"></td>
         <td width=125 align=right><span class="smallb">特価</span>
           <span class=specialprice> $39.99</span> &nbsp;<br>
           <span class=smallb>表示価格 $49.99</span> &nbsp;</td>
       </tr>
       <tr>  
         <td colspan=2><b><dsp:a href="product.jsp">
           <dsp:valueof param="element.displayName"/></dsp:a></b></td>
       </tr>
       <tr>
         <td><dsp:img src="../images/d.gif" vspace="17"/></td>
       </tr>
   </dsp:oparam>
   <dsp:oparam name="empty">
     本日の特価品はありません。
   </dsp:oparam>
 </dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/DisplayProductSlot.jsp#2 $$Change: 651448 $--%>

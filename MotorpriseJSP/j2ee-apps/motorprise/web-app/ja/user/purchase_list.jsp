<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<%/* if we had accepted a listId any user can see an abritrarly gift list by guessin the sequential numbers,
 so we pass in instead in index into the current users list of purchase lists...*/%>
 
 <DECLAREPARAM NAME="listNumber" CLASS="java.lang.Integer" 
    DESCRIPTION="the gilft list number in the current users profile">

<%/* should we do some error checking?*/%>

<dsp:setvalue param="item" beanvalue="Profile.purchaseLists[param:listNumber]"/> 

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" 購入リスト"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">私のアカウント</dsp:a> &gt; <dsp:a href="purchase_lists.jsp">購入リスト</dsp:a> &gt; 
  <dsp:valueof  param="item.eventName"/></td>

  </tr>
  <tr valign=top>
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td><span class=big>私のアカウント</span></td>
      </tr>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr>
        <td><dsp:form action="purchase_list.jsp" method="post"><b>
          購入リスト - 
          <dsp:valueof  param="item.eventName"/></b>
        </td>
      </tr>
      <tr>
        <td colspan=2><span class="small">このページでの編集結果は、購入リストに保存されます。カートでの編集結果は、そのオーダーにのみ保存されます。</span></td>
      </tr>
      <tr>
        <td>

          <dsp:droplet name="ErrorMessageForEach">
            <dsp:param bean="GiftlistFormHandler.formExceptions" name="exceptions"/>
            <dsp:oparam name="outputStart">
              <font color="red">エラー：</font>
            </dsp:oparam>
            <dsp:oparam name="output">
              <font color="blue">
              <dsp:valueof param="propertyName"/> :: 
              <dsp:valueof param="message"/>
              </font>
            </dsp:oparam>
          </dsp:droplet>
	    <dsp:droplet name="ErrorMessageForEach">
            <dsp:param bean="CartModifierFormHandler.formExceptions" name="exceptions"/>
            <dsp:oparam name="outputStart">
              <font color="red">エラー：</font>
            </dsp:oparam>
            <dsp:oparam name="output">
              <font color="blue">
              <dsp:valueof param="propertyName"/> :: 
              <dsp:valueof param="message"/>
              </font>
            </dsp:oparam>
          </dsp:droplet>

        </td>
      </tr>
    <tr>
      <td>  
        <table border=0 cellpadding=4 cellspacing=1 width=100%>
          <dsp:droplet name="ForEach">
            <dsp:param param="item.giftListItems" name="array"/>
            <dsp:oparam name="outputStart">
          <tr bgcolor="#666666">
            <td colspan=2><span class=smallbw>部品番号</span></td>
            <td colspan=2><span class=smallbw>名前</span></td>
            <td colspan=2><span class=smallbw>数量</span></td>
            <td colspan=2><span class=smallbw>削除</span></td>
          </tr>
          </dsp:oparam>

          
            <dsp:oparam name="empty">
              <tr>
                <td>購入リストにアイテムがありません</td>
              </tr>
            </dsp:oparam>
            <dsp:oparam name="output">
	      <dsp:setvalue param="gift-item" paramvalue="element"/>
	      <%@ include file="purchase_list_item.jspf" %>
	    </dsp:oparam>
          </dsp:droplet>

          <tr>
            <td colspan=12><hr color=#666666 size=0></td>
          </tr>

        </table>     
        </td>
      </tr>
      <tr>
        <td><p><br>
            <!-- add button goes to cart -->
            <!--jeremey is working on this now-->
            <%--<dsp:input cart" bean="GiftlistFormHandler.value=" type="submit" list add to/>--%>
            <input name="listNumber" type="hidden" value="<dsp:valueof param="listNumber"/>">
            <!-- update button returns to this page -->
          <dsp:input bean="GiftlistFormHandler.updateGiftlistItems" type="submit" value="更新"/>         
          <!-- delete button goes to confirm and then  returns to order management -->

          <dsp:input bean="GiftlistFormHandler.deleteGiftlist" type="submit" value="削除"/>
          <dsp:input bean="GiftlistFormHandler.giftlistId" paramvalue="item.repositoryId" type="hidden"/> 
      <dsp:input bean="GiftlistFormHandler.deleteGiftlistSuccessURL" type="hidden" value="purchase_lists.jsp"/>
          <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL" name="addITemSuccessURL" type="HIDDEN" value="../checkout/cart.jsp"/>
          <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrder" type="submit" value="現在のオーダーに追加"/>

        </td>
      </tr>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
       

    </table>
    </dsp:form>
    </td>
  </tr>

</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/purchase_list.jsp#2 $$Change: 651448 $--%>

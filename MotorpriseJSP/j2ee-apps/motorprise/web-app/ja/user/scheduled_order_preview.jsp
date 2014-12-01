<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/scheduled/ScheduledOrderFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<DECLAREPARAM NAME="scheduledOrderId" CLASS="java.lang.String" DESCRIPTION="The id of the Scheduled order to manipulate">
<DECLAREPARAM NAME="createNew" CLASS="java.lang.String" DESCRIPTION="Determines whether create new scheduled order or not">

<dsp:droplet name="IsNull">
  <dsp:param name="value" param="scheduledOrderId"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="ScheduledOrderFormHandler.deleteSuccessURL" value="scheduled_order_new.jsp"/>    
  </dsp:oparam>
  <dsp:oparam name="false">
    <!--if we pass in scheduledOrderId, then we want to display the recurringOrder stringt from the db.-->
    <dsp:setvalue bean="ScheduledOrderFormHandler.repositoryId" paramvalue="scheduledOrderId"/>
    <dsp:setvalue bean="ScheduledOrderFormHandler.deleteSuccessURL" value="scheduled_orders.jsp"/> 
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="IsNull">
  <dsp:param name="value" bean="ScheduledOrderFormHandler.repositoryId"/>
  <dsp:oparam name="true">
  </dsp:oparam>
  <dsp:oparam name="false">
     <dsp:setvalue bean="ScheduledOrderFormHandler.deleteSuccessURL" value="scheduled_orders.jsp"/> 
  </dsp:oparam>
</dsp:droplet>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" スケジュール済みオーダー"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <!--breadcrumbs-->
  <dsp:droplet name="IsNull">
    <dsp:param bean="ScheduledOrderFormHandler.repositoryId" name="value"/>
    <dsp:oparam name="false">
      <tr bgcolor="#DBDBDB" > 
        <td colspan=2 height=18> &nbsp; <span class=small>
        <dsp:a href="my_account.jsp">私のアカウント</dsp:a> &gt; <dsp:a href="scheduled_orders.jsp">スケジュール済みオーダー</dsp:a> &gt;
        <dsp:valueof bean="ScheduledOrderFormHandler.value.name"/></td>
      </tr>
    </dsp:oparam>
    <dsp:oparam name="true">
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="createNew"/>
        <dsp:oparam name="new">
          <tr bgcolor="#DBDBDB" > 
            <td colspan=2 height=18> &nbsp; <span class=small>
            <dsp:a href="my_account.jsp">私のアカウント</dsp:a> &gt; 
            <dsp:a href="scheduled_orders.jsp">スケジュール済みオーダー</dsp:a> &gt; 
            <dsp:a href="scheduled_order_new.jsp">スケジュール済みオーダーの作成</dsp:a> &gt; 
            <dsp:a href="scheduled_order_calendar.jsp"><dsp:valueof bean="ScheduledOrderFormHandler.value.name"/></dsp:a> &gt; 
            オーダーのプレビュー</td>
          </tr>
        </dsp:oparam>
        <dsp:oparam name="default">
          <tr bgcolor="#DBDBDB" > 
            <td colspan=2 height=18> &nbsp; <span class=small>
            <dsp:a href="../checkout/current_order.jsp">現在のオーダー</dsp:a> &gt; 
            <dsp:a href="../checkout/shipping.jsp">配達</dsp:a> &gt; 
            <dsp:a href="../checkout/billing.jsp">請求</dsp:a> &gt; 
            <dsp:a href="../checkout/confirmation.jsp">確認</dsp:a> &gt; 
            <dsp:a href="scheduled_order_new.jsp">スケジュール済みオーダーの作成</dsp:a> &gt; 
            <dsp:a href="scheduled_order_calendar.jsp"><dsp:valueof bean="ScheduledOrderFormHandler.value.name"/></dsp:a> &gt; 
            オーダーのプレビュー<dsp:valueof param="createNew"/></td>
          </tr>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>
      <table border=0 cellpadding=4 width=80%>
        <tr><td><img src="../images/d.gif"></td></tr>
        <tr valign=top>
          <td colspan=3><span class=big>私のアカウント</span></td>
        </tr>
        <tr><td><img src="../images/d.gif"></td></tr>
        <!--error messages-->
    
        <tr valign=top>
          <td><dsp:include page="../common/FormError.jsp"></dsp:include></td>  
        </tr>
        <tr>
            <td><table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;スケジュール済みオーダー</td></tr></table>
        
              <dsp:getvalueof id="deleteURL" idtype="String" bean="ScheduledOrderFormHandler.deleteSuccessURL">
<%--              <dsp:form action="bean:ScheduledOrderFormHandler.deleteSuccessURL" method="post"> --%>
              <dsp:form action="<%=deleteURL%>" method="post"> 
              
              <table border=0 cellpadding=4>
                <tr valign=bottom>
                  <td align=right><span class=smallb>名前</span></td>
                  <td><dsp:valueof bean="ScheduledOrderFormHandler.value.name"/></td>
                </tr>
                <tr valign=bottom>
                  <td align=right><span class=smallb>スケジュール</span></td>
                  <td width=75%>
                    <dsp:valueof bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.scheduleString"/></td>
                </tr>
                <tr valign=top>
                  <td align=right><span class=smallb>開始日</span></td>
                    <dsp:droplet name="Switch">
                      <dsp:param bean="Profile.locale" name="value"/>
                      <dsp:oparam name="ja_JP">
                        <td><dsp:valueof bean="ScheduledOrderFormHandler.value.startDate" date="yyyy'年'MM'月'dd'日' HH:mm"/></td>
                      </dsp:oparam>
                      <dsp:oparam name="en_US">
                        <td><dsp:valueof bean="ScheduledOrderFormHandler.value.startDate" date="MMM d, yyyy h:mm a"/></td>
                      </dsp:oparam>
                      <dsp:oparam name="de_DE">
                        <td><dsp:valueof bean="ScheduledOrderFormHandler.value.startDate" date="d MMM yyyy HH:mm"/></td>
                      </dsp:oparam>
                    </dsp:droplet>
                </tr>
                <tr valign=top>
                  <td align=right><span class=smallb>終了日</span></td>
                  <dsp:droplet name="Switch">
                    <dsp:param bean="ScheduledOrderFormHandler.complexScheduledOrderMap.endDate.userInputFields.mode" name="value"/>
                    <dsp:oparam name="definite">
                      <dsp:droplet name="Switch">
                        <dsp:param bean="Profile.locale" name="value"/>
                      <dsp:oparam name="ja_JP">
                        <td><dsp:valueof bean="ScheduledOrderFormHandler.value.endDate" date="yyyy'年'MMMdd'日' HH:mm"/></td>
                      </dsp:oparam>
                        <dsp:oparam name="en_US">
                          <td><dsp:valueof bean="ScheduledOrderFormHandler.value.endDate" date="MMM d,yyyy h:mm a"/></td>
                        </dsp:oparam>
                        <dsp:oparam name="de_DE">
                          <td><dsp:valueof bean="ScheduledOrderFormHandler.value.endDate" date="d MMM yyyy HH:mm"/></td>
                        </dsp:oparam>
                      </dsp:droplet>      
                    </dsp:oparam>
                    <dsp:oparam name="indefinite">
                      <td>無期限</td>
                    </dsp:oparam> 
                  </dsp:droplet>
                </tr>
                <tr>
                  <td></td>
                  <td>
                    <dsp:droplet name="IsNull">
                      <dsp:param bean="ScheduledOrderFormHandler.repositoryId" name="value"/>
                      <dsp:oparam name="false">
                       <span class=smallb><dsp:a href="scheduled_order_new.jsp">
                       <dsp:param bean="ScheduledOrderFormHandler.repositoryId" name="scheduledOrderId"/> 
                       オーダー情報の編集</dsp:a></span>
                      </dsp:oparam>
                      <dsp:oparam name="true">
                       <span class=smallb><dsp:a href="scheduled_order_new.jsp">
                       オーダー情報の編集</dsp:a></span>
                      </dsp:oparam>
                    </dsp:droplet> 
                  </td>
                </tr>
                        
        <dsp:getvalueof id="pval0" bean="ScheduledOrderFormHandler.complexScheduledOrderMap.templateOrderId.userInputFields.order"><dsp:include page="../checkout/displayOrder.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof> 
        <tr valign=top>
          <td></td>
          <td>
            <p>
            <dsp:droplet name="IsNull">
              <dsp:param name="value" bean="ScheduledOrderFormHandler.repositoryId"/>
              <dsp:oparam name="true">
                <dsp:input bean="ScheduledOrderFormHandler.createErrorURL" type="hidden" value="scheduled_order_new.jsp"/>
                <dsp:input bean="ScheduledOrderFormHandler.createSuccessURL" type="hidden" value="scheduled_orders.jsp"/>
                <dsp:input bean="ScheduledOrderFormHandler.create" type="submit" value="スケジュール済みオーダーの作成"/>
              </dsp:oparam>
              <dsp:oparam name="false">
                <dsp:input bean="ScheduledOrderFormHandler.deleteSuccessURL" type="hidden" value="scheduled_orders.jsp"/>
                <dsp:input bean="ScheduledOrderFormHandler.deleteErrorURL" type="hidden" value="scheduled_order_preview.jsp"/>
                <dsp:input bean="ScheduledOrderFormHandler.delete" type="submit" value="スケジュール済みオーダーの削除"/><p>
                <span class=smallb><dsp:a href="scheduled_orders.jsp">スケジュール済みオーダーに戻る</dsp:a></span>
              </dsp:oparam>
            </dsp:droplet>
        </tr>
    </table>
    </dsp:form>
    </dsp:getvalueof>
    
  </td>
</tr>
<tr>
  <td></td>
  <td><p><br></td>
</tr>

<!-- vertical space -->
<tr><td><img src="../images/d.gif" vspace=0></td></tr>

</table>
<%-- </dsp:form> --%>
</td>
</tr>

</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/scheduled_order_preview.jsp#2 $$Change: 651448 $--%>

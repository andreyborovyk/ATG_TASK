<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/For"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/commerce/order/scheduled/ScheduledOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/scheduled/ScheduledOrderLookup"/>
<dsp:importbean bean="/atg/projects/b2bstore/order/OrderRepositoryQueryServlet"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<DECLAREPARAM NAME="scheduledOrderId" CLASS="java.lang.String" DESCRIPTION="The id of the Scheduled order to manipulate">
<DECLAREPARAM NAME="createNew" CLASS="java.lang.String" DESCRIPTION="Determines whether create new scheduled order or not">

<dsp:droplet name="IsNull">
  <dsp:param name="value" bean="ScheduledOrderFormHandler.value.name"/>
  <dsp:oparam name="true">
    <dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Scheduled Order"/></dsp:include>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Scheduled Orders"/></dsp:include>
  </dsp:oparam>
</dsp:droplet>


<dsp:droplet name="IsNull">
  <dsp:param name="value" param="scheduledOrderId"/>
  <dsp:oparam name="true">
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue param="scheduledOrderId" beanvalue="ScheduledOrderFormHandler.repositoryId" />
  </dsp:oparam>
</dsp:droplet>

<dsp:setvalue param="ScheduledOrderId" beanvalue="ScheduledOrderFormHandler.repositoryId"/>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <dsp:droplet name="IsNull">
    <dsp:param bean="ScheduledOrderFormHandler.repositoryId" name="value"/>
    <dsp:oparam name="false"> 
      <tr bgcolor="#DBDBDB" > 
        <td colspan=2 height=18> &nbsp; <span class=small>
          <dsp:a href="my_account.jsp">My Account</dsp:a> &gt;
          <dsp:a href="scheduled_orders.jsp">Scheduled Orders</dsp:a> &gt; 
          <dsp:a href="scheduled_order_preview.jsp"><dsp:valueof bean="ScheduledOrderFormHandler.value.name"/></dsp:a> &gt;
          <dsp:a href="scheduled_order_new.jsp">Edit Scheduled Order</dsp:a> &gt; 
          Edit Order Placement</td>
      </tr>
    </dsp:oparam>
    <dsp:oparam name="true">
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="createNew"/>
        <dsp:oparam name="new">
          <tr bgcolor="#DBDBDB" > 
            <td colspan=2 height=18> &nbsp; <span class=small>
            <dsp:a href="my_account.jsp">My Account</dsp:a> &gt;
            <dsp:a href="scheduled_orders.jsp">Scheduled Orders</dsp:a> &gt; 
            Create Scheduled Order</td>
          </tr>
        </dsp:oparam>
        <dsp:oparam name="default">
          <tr bgcolor="#DBDBDB" > 
            <td colspan=2 height=18> &nbsp; <span class=small>
            <dsp:a href="my_account.jsp">Current Order</dsp:a> &gt;
            <dsp:a href="../checkout/shipping.jsp">Shipping</dsp:a> &gt; 
            <dsp:a href="../checkout/billing.jsp">Billing</dsp:a> &gt;
            <dsp:a href="../checkout/confirmation.jsp">Confirmation</dsp:a> &gt;
            <dsp:a href="scheduled_order_new.jsp">Create Scheduled Order</dsp:a> &gt;
            <dsp:valueof bean="ScheduledOrderFormHandler.value.name"/></td>
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
              <td colspan=3><span class=big>My Account</span></td>
            </tr>
            <tr><td><img src="../images/d.gif"></td></tr>
            <tr>
              <td><dsp:include page="../common/FormError.jsp"></dsp:include></td>
            </tr>
            <tr>
              <td><table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr>
                <td class=box-top>&nbsp;Scheduled Order</td></tr></table>

                <dsp:form action="scheduled_order_preview.jsp" method="post">
                <table border=0 cellpadding=8>
                  <tr valign=bottom>
                    <td align=right><span class=smallb>Order name</span></td>
                    <td><dsp:valueof bean="ScheduledOrderFormHandler.value.name" /></td>
                  </tr>
                  <tr>
                    <td align=right><span class=smallb>Schedule type</span></td>
                    <td> <dsp:valueof bean="ScheduledOrderFormHandler.moveToMode" /> scheduled order</td>
                  </tr>
                  
                  <dsp:droplet name="Switch">
                    <dsp:param bean="ScheduledOrderFormHandler.moveToMode" name="value"/> 
                    <dsp:oparam name="Daily">
                      <tr>
                        <td align="right"><span class=smallb>Order placement</span></td>
                        <td align="left">
                          <dsp:select bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.scheduleMode" name="select" size="1">
                            <dsp:option value="onceDaily"/>once a day.
                            <dsp:option value="twiceDaily"/>twice a day.
                          </dsp:select>
                        </td>
                      </tr>
                      <%@ include file="day_of_week.jspf" %>

                    </dsp:oparam>
                    <dsp:oparam name="Weekly">
                      <dsp:input bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.scheduleMode" type="hidden" value="Weekly"/>
                      <%@ include file="day_of_week1.jspf"%>        
                      <tr>
                        <td align="right" valign="top"><span class=smallb>Week(s) of month</span></td>
                        <td align="left">
                        <table border="0">
                          <tr>
                            <td><dsp:input bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.occurrenceInMonth" name="occurrenceInMonth" type="checkbox" value="1"/>1st. </td>
                            <td><dsp:input bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.occurrenceInMonth" name="occurrenceInMonth" type="checkbox" value="2"/>2nd. </td>
                            <td><dsp:input bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.occurrenceInMonth" name="occurrenceInMonth" type="checkbox" value="3"/>3rd. </td>
                            <td><dsp:input bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.occurrenceInMonth" name="occurrenceInMonth" type="checkbox" value="4"/>4th. </td>
                            <td><dsp:input bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.occurrenceInMonth" name="occurrenceInMonth" type="checkbox" value="5"/>5th. </td>
                          </tr>
                          <tr><td colspan=5><span class=help>Uncheck to ship selected weeks</span></td></tr>
                        </table>
                      </tr>
                    </dsp:oparam>
                    <dsp:oparam name="Monthly">
                      <tr>
                        <td align="right"><span class=smallb>Order Placement</span></td>
                        <td align="left">
                          <dsp:select bean="ScheduledOrderFormHandler.complexScheduledOrderMap.calendarSchedule.userInputFields.scheduleMode" name="select" size="1">
                            <dsp:option value="onceMonthly"/>once a month.
                            <dsp:option value="biMonthly"/>every two months.
                            <dsp:option value="quarterly"/>every quarter.
                          </dsp:select>
                        </td>
                      </tr>             
                    </dsp:oparam>
                    <dsp:oparam name="default">
                    </dsp:oparam>
                  </dsp:droplet>
                  
                  <%@ include file="start_end_date.jspf" %>                   
                      

         <!-- Preview button for weekly scheduled order -->
              <tr valign=top>
                <td></td>
                <td><p>

                  <dsp:droplet name="IsNull">
                    <dsp:param name="value" bean="ScheduledOrderFormHandler.repositoryId"/>
                    <dsp:oparam name="true">
                      <dsp:input bean="ScheduledOrderFormHandler.verifySuccessURL" type="hidden" value="scheduled_order_preview.jsp?source=scheduledOrder&createNew=new"/>
                      <dsp:input bean="ScheduledOrderFormHandler.verifyErrorURL" type="hidden" value="scheduled_order_calendar.jsp"/>
                      <dsp:input bean="ScheduledOrderFormHandler.verify" type="submit" value="Continue"/>
                    </dsp:oparam>
                    <dsp:oparam name="false">
                      <dsp:input bean="ScheduledOrderFormHandler.updateSuccessURL" type="hidden" value="scheduled_orders.jsp"/>
                      <dsp:input bean="ScheduledOrderFormHandler.updateErrorURL" type="hidden" value="scheduled_order_calendar.jsp"/>
                      <dsp:input bean="ScheduledOrderFormHandler.update" type="submit" value="Update"/><p>
                      <span class=smallb><dsp:a href="scheduled_orders.jsp">Return to scheduled orders</dsp:a></span>
                    </dsp:oparam>
                  </dsp:droplet>
              </tr>
          
    <%--          </dsp:form>  --%>
          </table>
        </td>
      </tr>
      <tr>
        <td></td>
        <td><p><br>
          
        </td>
      </tr>
    
      <!-- vertical space -->
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

  </table>
  </dsp:form>
</td>
</tr>

</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/scheduled_order_calendar.jsp#2 $$Change: 651448 $--%>

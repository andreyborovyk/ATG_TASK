<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Open Orders"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">My Account</dsp:a> &gt;
     Open Orders</td>
  

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <!-- vertical space -->
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td colspan=2><span class=big>My Account</span></td>
      </tr>
    <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>

      <tr>
        <td colspan=2><table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Open Orders</td></tr></table></td>
      </tr>
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
      <tr>
        <td>

         <dsp:getvalueof id="profileId" idtype="java.lang.String" bean="Profile.id">
          <%-- profileId:  <%=profileId%> --%>
          <dsp:droplet name="OrderLookup">
            <dsp:param value="<%=profileId%>" name="userId"/>
            <dsp:param name="state" value="open"/>
            <dsp:param name="startIndex" param="startIndex"/>
            <dsp:oparam name="output">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="result"/>
                <dsp:oparam name="outputStart">
                  <table border=0 width=95%>
                    <tr valign=top>
                      <td width=33%><span class="small"><b>Order #</b></span></td>
                      <td width=33%><span class="small"><b>Date ordered</b></span></td>
                      <td width=33%><span class="small"><b>Status</b></span></td>
                   </tr>
                   <tr>
                     <td colspan=3><hr size=1 color="#666666"></td>
                   </tr>

                </dsp:oparam>

                <dsp:oparam name="output">
                  <tr>
                    <dsp:droplet name="Switch">
                    <dsp:param name="value" param="element.originOfOrder"/>
                    <dsp:oparam name="default">
                      <td><dsp:a href="order.jsp">
                      <dsp:param name="orderId" param="element.id"/>
                      <dsp:valueof param="element.id"/><dsp:param name="orderState" param="state"/>
                      </dsp:a></td>
                    </dsp:oparam>
                    <dsp:oparam name="scheduledOrder">
                      <td><dsp:a href="order.jsp">
                      <dsp:param name="orderId" param="element.id"/>
                      <dsp:valueof param="element.id"/>
                      </dsp:a>(ScheduledOrder)</td>
                    </dsp:oparam> 
                    </dsp:droplet>
                    <td><dsp:valueof date="MMMMM d, yyyy" param="element.submittedDate"/></td>
                    <td><dsp:valueof param="element.stateAsUserResource"/></td>
                  </tr>

                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                  <tr></tr>
                  <tr>
                    <td colspan=3>
                      <hr size=1 color="#666666">

                      <%/* see if paging thru results is necessary */%>
                      <dsp:droplet name="Compare">
                        <dsp:param name="obj1" param="total_count"/>
                        <dsp:param name="obj2" bean="OrderLookup.defaultNumOrders"/>
                      
                        <dsp:oparam name="default">
                          <dsp:droplet name="Switch">
                            <dsp:param name="value" param="total_count"/> 
                            <dsp:oparam name="1"> 
                              There is 1 open order.
                            </dsp:oparam>
                            <dsp:oparam name="default">
                               There are <dsp:valueof param="total_count"></dsp:valueof>
                               open orders.                        
                            </dsp:oparam>
                          </dsp:droplet>
                        </dsp:oparam>  

                        <%/* page through results if there are more than 10.*/%>
                        <dsp:oparam name="greaterthan">
                          Now viewing orders
                          <dsp:valueof param="startRange"></dsp:valueof> - 
                          <dsp:valueof param="endRange"></dsp:valueof> out of 
                          <dsp:valueof param="total_count"></dsp:valueof>

                          &nbsp; &nbsp;
                          <dsp:droplet name="IsEmpty">
                            <dsp:param name="value" param="previousIndex"/>
                            <dsp:oparam name="false">
                              <dsp:a href="orders_open.jsp">
                                <dsp:param name="startIndex" param="previousIndex"/>
                                &lt;&lt; Previous</dsp:a>
                            </dsp:oparam>
                          </dsp:droplet>
                          <dsp:droplet name="IsEmpty">
                            <dsp:param name="value" param="nextIndex"/>
                            <dsp:oparam name="false">
                              <dsp:a href="orders_open.jsp">
                                <dsp:param name="startIndex" param="endRange"/>
                                Next &gt;&gt;</dsp:a>
                            </dsp:oparam>
                          </dsp:droplet>
                        </dsp:oparam> 
                      </dsp:droplet><%/* End Compare droplet */%>


                    </td>
                  </tr>
                </dsp:oparam>

                
              </dsp:droplet>
            </dsp:oparam>
            <dsp:oparam name="empty">
              <%/* no open orders for user */%>
                You have no open orders.
            </dsp:oparam>
            <dsp:oparam name="error">
              <dsp:valueof param="errorMsg"/>
            </dsp:oparam>
          </dsp:droplet>
</dsp:getvalueof>
         </table>
         </td>

       </tr>
       </div>
    </table>
    </td>
  </tr>

</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/orders_open.jsp#2 $$Change: 651448 $--%>

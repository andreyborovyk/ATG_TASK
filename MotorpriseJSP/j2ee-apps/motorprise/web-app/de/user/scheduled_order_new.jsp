<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/commerce/order/scheduled/ScheduledOrderFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/For"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/commerce/order/scheduled/ScheduledOrderLookup"/>
<dsp:importbean bean="/atg/projects/b2bstore/order/OrderRepositoryQueryServlet"/>
<dsp:importbean bean="/atg/projects/b2bstore/order/OrderQueryServlet"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Regelmäßige Bestellungen"/></dsp:include>

<DECLAREPARAM NAME="scheduledOrderId" CLASS="java.lang.String" DESCRIPTION="The id of the Scheduled order to manipulate">
<DECLAREPARAM NAME="createNew" CLASS="java.lang.String" DESCRIPTION="Determines whether create new sheduled order or not">


<dsp:droplet name="IsNull">
  <dsp:param name="value" param="scheduledOrderId"/>
  <dsp:oparam name="true">
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="ScheduledOrderFormHandler.repositoryId" paramvalue="scheduledOrderId"/>
  </dsp:oparam>
</dsp:droplet>
          
<dsp:setvalue param="scheduledOrderId" beanvalue="ScheduledOrderFormHandler.repositoryId"/>





          
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>  
   
  
  <dsp:droplet name="IsNull">
    <dsp:param  name="value" bean="ScheduledOrderFormHandler.repositoryId"/>
    <dsp:oparam name="false"> 
      <tr bgcolor="#DBDBDB" > 
        <td colspan=2 height=18> &nbsp; <span class=small>
          <dsp:a href="my_account.jsp">Mein Konto</dsp:a> &gt;
          <dsp:a href="scheduled_orders.jsp">Regelmäßige Bestellungen</dsp:a> &gt; 
          <dsp:a href="scheduled_order_preview.jsp"><dsp:valueof bean="ScheduledOrderFormHandler.value.name"/></dsp:a> &gt;
          Regelmäßige Bestellung bearbeiten</td>
      </tr>
    </dsp:oparam>
    <dsp:oparam name="true">
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="createNew"/>
        <dsp:oparam name="new">
          <tr bgcolor="#DBDBDB" > 
            <td colspan=2 height=18> &nbsp; <span class=small>
            <dsp:a href="my_account.jsp">Mein Konto</dsp:a> &gt;
            <dsp:a href="scheduled_orders.jsp">Regelmäßige Bestellungen</dsp:a> &gt; 
            Regelmäßige Bestellung anlegen</td>
          </tr>
        </dsp:oparam>
        <dsp:oparam name="default">
          <tr bgcolor="#DBDBDB" > 
            <td colspan=2 height=18> &nbsp; <span class=small>
            <dsp:a href="my_account.jsp">Aktuelle Bestellung</dsp:a> &gt;
            <dsp:a href="../checkout/shipping.jsp">Versand</dsp:a> &gt; 
            <dsp:a href="../checkout/billing.jsp">Rechnung</dsp:a> &gt;
            <dsp:a href="../checkout/confirmation.jsp">Bestätigung</dsp:a> &gt;
            Regelmäßige Bestellung anlegen</td>
          </tr>
      </dsp:oparam>
    </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
          

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
   <td valign="top"  width=745>  
   <table border=0 cellpadding=4 width=80%>
     <tr><td><img src="../images/d.gif"></td></tr>
     <tr valign=top>
       <td colspan=3><span class=big>Mein Konto</span></td>
     </tr>

     <tr valign=top>
       <td><img src="../images/d.gif"></td>
     </tr>
     <!--error messages-->
     <tr>
       <td> <dsp:include page="../common/FormError.jsp"></dsp:include> </td>
     </tr>       
     <tr>
       <td><table width=100% cellpadding=3 cellspacing=0 border=0>
     <tr>
       <td class=box-top>&nbsp;Regelmäßige Bestellung</td></tr></table></td>
     </tr>
     <tr><td><img src="../images/d.gif" hspace=27></td></tr>
     <!-- To decide whether there is a template order or fulfilled order-->
     <tr>
       <td>
         <dsp:droplet name="OrderLookup">
           <dsp:param name="userId" bean="Profile.repositoryid"/>
           <dsp:param name="state" value="closed"/>
           <dsp:param name="queryTotal" value="false"/>
           <dsp:oparam name="output">
             <dsp:setvalue param="result" paramvalue="result"/> 
           </dsp:oparam>
         </dsp:droplet>
  
         <dsp:droplet name="IsNull">
           <dsp:param name="value" param="prototypeOrderId"/>
           <dsp:oparam name="true">
             <dsp:droplet name="OrderLookup">
               <dsp:param name="userId" bean="Profile.repositoryId"/>
               <dsp:param name="state" value="closed"/>
               <dsp:param name="queryTotal" value="false"/>
                 <dsp:oparam name="output">
                   <dsp:droplet name="IsEmpty">
                     <dsp:param name="value" param="result"/>
                       <dsp:oparam name="true">
                         Sie haben derzeit keine <dsp:a href="orders_filled.jsp">Ausgeführten Bestellungen</dsp:a>, um eine regelmäßige Bestellung anzulegen.  <p>Sie können eine regelmäßige Bestellung auf der Basis einer aktuellen Bestellung erstellen. Wählen Sie dazu am Ende des Checkout-Vorgangs die Option "Regelmäßige Bestellung anlegen".
                       </dsp:oparam>
                       <dsp:oparam name="false">
                         <dsp:form formid="fromOrder" action="scheduled_order_new.jsp" method="post">
                         <table border=0 cellpadding=3 width=100%>
                           <tr>
                             <td colspan=2>

                             
                               <dsp:droplet name="IsNull">
                                 <dsp:param name="value" bean="ScheduledOrderFormHandler.repositoryId"/>
                                 <dsp:oparam name="false"><span class=small>Sie k&ouml;nnen den Namen, den Bestellrhythmus und die Bestelldaten Ihrer Bestellung bearbeiten.</span>
                                 </dsp:oparam>
                                 <dsp:oparam name="true"><span class=small>Regelmäßige Bestellung auf Basis Ihrer <dsp:a href="orders_filled.jsp">Ausgeführten Bestellungen</dsp:a> anlegen. Die regelmäßigen Bestellungen haben dieselben Versand- und Rechnungsdaten wie die ausgeführte Bestellung. <p>Sie können eine regelmäßige Bestellung auch auf Basis einer aktuellen Bestellung anlegen. Wählen Sie dazu am Ende des Checkout-Vorgangs die Option "Regelmäßige Bestellung anlegen".</span>
                                 </dsp:oparam>
                               </dsp:droplet> 
                               
                               
                             </td>
                           </tr>
                           <tr><td><img src="../images/d.gif"></td></tr>
                           <tr valign=bottom>
                             <td valign="top" align=right><span class=smallb>Name</span></td>
                             <td><dsp:input bean="ScheduledOrderFormHandler.value.name" name="name" size="35" type="text"/><br>
                             <span class=help>Geben Sie einen Namen für Ihre regelmäßige Bestellung ein (z.B. Wöchentliches Zündkerzen-Sortiment).</span></td>
                           </tr>
                           <tr>
                             <td align=right><span class=smallb>Bestellrhythmus</span></td>
                             <td align="left">
                               <dsp:select bean="ScheduledOrderFormHandler.moveToMode" name="select" size="1">
                                 <dsp:option value=""/>Bestellrhythmus festlegen
                                 <dsp:option value="Daily"/>Täglich
                                 <dsp:option value="Weekly"/>Wöchentlich
                                 <dsp:option value="Monthly"/>Monatlich
                               </dsp:select>
                             </td>
                           </tr>

                           <dsp:droplet name="IsNull">
                           <dsp:param name="value" bean="ScheduledOrderFormHandler.repositoryId"/>
                           <dsp:oparam name="true">
                           <tr>
                             <td align="right" valign=top><span class=smallb>Bestellung</span></td>
                             <td>
                               <dsp:select bean="ScheduledOrderFormHandler.complexScheduledOrderMap.templateOrderId.userInputFields.orderId">
                                 <dsp:droplet name="ForEach">
                                   <dsp:param name="array" param="result"/>
                                   <dsp:oparam name="outputStart">
                                     <dsp:option value=""/>  Wählen Sie eine ausgeführte Bestellung
                                   </dsp:oparam>
                                   
                                   <dsp:oparam name="output">
                                     <dsp:getvalueof id="elementId" idtype="String" param="element.id">
                                       <dsp:option value="<%=elementId%>"/><dsp:valueof param="element.id"/>
                                     </dsp:getvalueof>
                                   </dsp:oparam>  
                                   
                                 </dsp:droplet>
                               </dsp:select>
<br>


                             </td> 
                           </tr>

                           </dsp:oparam>
                           <dsp:oparam name="false">
                           </dsp:oparam>
                           </dsp:droplet>
                           <tr><td><img src="../images/d.gif"></td></tr>
                           <tr> 
                             <td></td>
                             <input name="createNew" type="hidden" value="<dsp:valueof param="createNew"/>"> 
                             <dsp:input bean="ScheduledOrderFormHandler.moveToErrorURL" type="hidden" value="scheduled_order_new.jsp"/>      
                             <dsp:input bean="ScheduledOrderFormHandler.moveToSuccessURL" type="hidden" value="scheduled_order_calendar.jsp?createNew=new"/> 
                             <td><dsp:input bean="ScheduledOrderFormHandler.moveToURL" type="submit" value="Weiter"/></td>
                           </tr>                            
                           <!-- vertical space -->
                           <tr><td><img src="../images/d.gif" vspace=0></td></tr>
                         </table>
                         </dsp:form>
                       </dsp:oparam>
                     </dsp:droplet><!--end IsEmpty-->
                   </dsp:oparam>
                 </dsp:droplet><!--end OrderLookup-->
               </dsp:oparam>  
               <dsp:oparam name="false">
                      
                 <dsp:form formid="newOrder" action="scheduled_order_new.jsp" method="post">
                 <table border=0 cellpadding=3>
                   <tr valign=bottom>
                     <td valign="top" align=right><span class=smallb>Name</span></td>
                     <td><dsp:input bean="ScheduledOrderFormHandler.value.name" name="name" size="25" type="text"/><br>
                         <span class=help>Geben Sie einen Namen für Ihre Regelmäßige Bestellung ein.</span></td>
                   </tr>
                      
                   <tr>
                     <td><span class=smallb>Bestellrhythmus</span></td>
                     <td align="left">
                       <dsp:select bean="ScheduledOrderFormHandler.moveToMode" name="select1" size="1">
                         <dsp:option value=""/>Bestellrhythmus festlegen
                         <dsp:option value="Daily"/>Täglich
                         <dsp:option value="Weekly"/>Wöchentlich
                         <dsp:option value="Monthly"/>Monatlich
                       </dsp:select>
                     </td>
                   </tr>

                   <dsp:droplet name="Switch">
                     <dsp:param name="value" param="prototypeOrderId"/>
                     <dsp:oparam name="a">
                     </dsp:oparam>
                     <dsp:oparam name="default">
                       <dsp:input bean="ScheduledOrderFormHandler.complexScheduledOrderMap.templateOrderId.userInputFields.orderId" paramvalue="prototypeOrderId" type="HIDDEN"/>
                     </dsp:oparam>
                   </dsp:droplet>
                   <dsp:input bean="ScheduledOrderFormHandler.moveToErrorURL" type="hidden" value="scheduled_order_new.jsp?prototypeOrderId=a"/>                         
                   <tr> 
                     <td></td>     
                     <dsp:input bean="ScheduledOrderFormHandler.moveToSuccessURL" type="hidden" value="scheduled_order_calendar.jsp"/> 
                     <td><dsp:input bean="ScheduledOrderFormHandler.moveToURL" type="submit" value="Weiter"/></td>
                   </tr>                                              
                   <!-- vertical space -->
                   <tr><td><img src="../images/d.gif" vspace=0></td></tr>
                 </table>
                 </dsp:form>
               </dsp:oparam>  
             </dsp:droplet><!--end IsNull-->
           </td>
         </tr>
      </table>
      </td>
    </tr>
  </table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/user/scheduled_order_new.jsp#2 $$Change: 651448 $--%>

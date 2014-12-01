<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  êøãÅêÊèZèäÇÃëIë"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="company_admin.jsp">âÔé–ä«óù</dsp:a> &gt; <dsp:a href="payment_edit.jsp">éxï•Ç¢ï˚ñ@</dsp:a> &gt; <dsp:a href="payment_method_create.jsp">éxï•Ç¢ÇÃçÏê¨</dsp:a> &gt; êøãÅêÊèZèäÇÃëIë
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
   <dsp:form action="payment_details_edit.jsp" method="post">
   <input name="nickName" type="hidden" value="<dsp:valueof param="nickName"/>">
     <input name="creditCardType" type="hidden" value="<dsp:valueof param="creditCardType"/>">
       <input name="firstName" type="hidden" value="<dsp:valueof param="firstName"/>">
         <input name="lastName" type="hidden" value="<dsp:valueof param="lastName"/>">
           <input name="creditCardNumber" type="hidden" value="<dsp:valueof param="creditCardNumber"/>">
             <input name="expirationMonth" type="hidden" value="<dsp:valueof param="expirationMonth"/>">
               <input name="" type="hidden" value="<dsp:valueof param=""/>">
                <input name="expirationYear" type="hidden" value="<dsp:valueof param="expirationYear"/>">
               
   	
        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>âÔé–ä«óù</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;êøãÅêÊèZèäÇÃëIë</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
          
          
            <td valign="top" colspan=2 height="92"> 
              <dsp:droplet name="TableForEach">
                <dsp:param bean="Profile.currentOrganization.billingAddrs" name="array"/>
                <dsp:param name="elementName" value="billingAddress"/>
                <dsp:param name="numColumns" value="2"/>
                <dsp:oparam name="outputStart"><table width="100%" border="0"></dsp:oparam>
                <dsp:oparam name="outputEnd"></table></dsp:oparam>
                <dsp:oparam name="outputRowStart"><tr></dsp:oparam>
                <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
                <dsp:oparam name="output">
                  <dsp:droplet name="IsEmpty">
                    <dsp:param name="value" param="billingAddress"/>
                    <dsp:oparam name="true">
                      <td></td>
                    </dsp:oparam>
                  <dsp:oparam name="false">
                    <td valign="top"><input name="billingAddress" type="radio" checked="<%=false%>" value="<dsp:valueof param="billingAddress.id"/>"></td>
                    <td>
                      <dsp:getvalueof id="pval0" param="billingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </td>
                  </dsp:oparam>
                  </dsp:droplet>
               </dsp:oparam>
               </dsp:droplet>
               </td>
               </tr>
                
                            
        <tr><td>&nbsp;</td></tr>
        <tr><td colspan="3" align="left"><input type="submit" name="submit1" value="ëIë"></td></tr>
              </table>
            </td>
          </tr>
    
          <tr> 
            <td><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        
          <%--  vertical space --%>
          <tr> 
            <td><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        </table>
    </dsp:form>
    </td>
  </tr>


</table>
</div>
</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/payment_address_edit.jsp#2 $$Change: 651448 $--%>

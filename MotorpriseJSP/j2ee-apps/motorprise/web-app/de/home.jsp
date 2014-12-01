<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:setvalue bean="/atg/dynamo/servlet/RequestLocale.refresh" value=""/>

<%-- If the user hits this page with his or her request locale set to en_US --%>
<%-- we will automatically redirect to the English home page instead.       --%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/dynamo/servlet/RequestLocale.locale" name="value"/>
  <dsp:oparam name="en_US">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../en/home.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="ja_JP">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../ja/home.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Produktkatalog"/></dsp:include>

<!-- table to contain whole page -->
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="common/BrandNav.jsp"></dsp:include></td>
  </tr>

  <!-- breadcrumbs -->
  <tr bgcolor="#DBDBDB"> 
    <td colspan=2 height=18><span class="small">&nbsp; Produktkatalog &nbsp;</span> </td>
  </tr>
  
  <tr valign=top>
    <td width=175>

    <!-- catalog categories -->
    <dsp:include page="common/CatalogNav.jsp"></dsp:include>
    
    <!-- incentives slot -->
    <dsp:include page="common/Incentive.jsp"></dsp:include>
    </td>

    <td width=625>
    <!-- main content area -->
         
    <table border=0 cellpadding=0 cellspacing=0 width=100%>
      <tr valign=top>
        <td> &nbsp; </td>
        <td width=55%>   
        <!-- active orders -->
        <table border=0 cellpadding=6 width=100%>
          <tr>
            <td>
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param bean="Profile.transient" name="value"/>
              <dsp:oparam name="false">
                <dsp:include page="user/ActiveOrders.jsp">
                </dsp:include>
              </dsp:oparam>
              <dsp:oparam name="true">
                <!-- sign up text for anonymous -->
                <dsp:droplet name="/atg/targeting/TargetingFirst">
                  <dsp:param bean="/atg/registry/Slots/SignUpMessage" name="targeter"/>
                  <dsp:param name="howMany" value="1"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.url">
                      <dsp:include page="<%=urlStr%>"></dsp:include>
                    </dsp:getvalueof>
                  </dsp:oparam>  
                </dsp:droplet>

              </dsp:oparam>  
            </dsp:droplet> 
 
            <p>
            <dsp:getvalueof id="pval0" bean="Profile.transient">
              <dsp:include page="common/DisplayPromos.jsp">
                <dsp:param name="value" value="<%=pval0%>"/>
              </dsp:include>
            </dsp:getvalueof>
      </td>
      </tr>
     </table>
    </td>
    
    <td width=40% align=right>
    <dsp:img src="images/d.gif" vspace="1"/><br>
    <dsp:include page="common/SpecialSlot.jsp"></dsp:include>
    <p>
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
      <dsp:param bean="Profile.transient" name="value"/>
      <dsp:oparam name="true">
        <table border=0><tr><td>
          <dsp:include page="common/ChooseLocale.jsp"></dsp:include><br>
        </td></tr></table>
      </dsp:oparam>
   </dsp:droplet>
   </td>
   </tr>
   </table>
   </td>
    
    
  </tr>
  	
  <tr>
    <td height=16 bgcolor="#666666" align=middle colspan=2>
      <dsp:include page="common/Copyright.jsp"></dsp:include>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/home.jsp#2 $$Change: 651448 $--%>

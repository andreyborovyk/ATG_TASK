<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/ProfileLookUp"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>


<%-- Check if the user had selected any properties to be edited in the previous page.  --%>
<%-- If none were selected, redirect to the previous page with an error message        --%>


<dsp:droplet name="IsEmpty">
<dsp:param name="value" param="roles"/>
<dsp:oparam name="true">
  <dsp:droplet name="IsEmpty">
  <dsp:param name="value" param="shippingAddrs"/>
  <dsp:oparam name="true">
    <dsp:droplet name="IsEmpty">
    <dsp:param name="value" param="paymentMethods"/>
    <dsp:oparam name="true">
      <dsp:droplet name="IsEmpty">
      <dsp:param name="value" param="costCenters"/>
      <dsp:oparam name="true">
        <dsp:droplet name="IsEmpty">
	<dsp:param name="value" param="purchaseLimit"/>
	<dsp:oparam name="true">

	<core:createUrl id="errorPage" url="multiple_user_edit2.jsp">
	  <core:urlParam param="errorMessage" value="属性が選択されていません"/>
	  <core:urlParam param="targetPage" value="multiple_user_edit.jsp"/>
	  <core:redirect url="<%=errorPage.getNewUrl()%>"/>
	</core:createUrl>

	</dsp:oparam>
	</dsp:droplet>
      </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  </dsp:droplet>
</dsp:oparam>
</dsp:droplet>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  複数のユーザの編集"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
      <dsp:a href="company_admin.jsp">会社管理</dsp:a> &gt; 
      <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
      複数のユーザの編集</span></td>
  </tr>
 
  <tr valign=top> 
   <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>会社管理</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>

      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;複数のユーザの編集</td></tr></table>
        </td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td>
        <dsp:form action="multiple_user_edit4.jsp" method="post">
        <dsp:input bean="MultiUserUpdateFormHandler.roleUpdateMethod" type="hidden" value="replace"/>
        <table border=0 cellpadding=4 cellspacing=0 width=100%>
        
          <dsp:droplet name="ProfileLookUp">
	    <%-- Assume all users are in the same organization and use the first user to find it. --%>
	    <dsp:param bean="MultiUserUpdateFormHandler.repositoryIds[0]" name="id"/>
            <dsp:param name="elementName" value="user"/>
            <dsp:oparam name="output">
        
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="roles"/>
                <dsp:oparam name="false">

                  <tr valign=right>
                  <td align=right><span class=smallb>役割</span></td>
                  <td>
                    <dsp:droplet name="ForEach">
                      <dsp:param name="array" param="user.parentOrganization.relativeRoles"/>
                      <dsp:param name="elementName" value="role"/>
                      <dsp:oparam name="output"> 
		        <dsp:input bean="MultiUserUpdateFormHandler.roleIds" paramvalue="role.repositoryId" name="userRoles" type="checkbox"/>
			<dsp:valueof param="role.name">未指定の役割</dsp:valueof><br>
                      </dsp:oparam>
                      <dsp:oparam name="empty">
                         役割がありません
                      </dsp:oparam>
                    </dsp:droplet>
                    </td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>
              <%-- display shipping addresses --%>
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="shippingAddrs"/>
                <dsp:oparam name="false">

                  <tr valign=right>
                    <td align=right valign=top><span class=smallb>配達先住所</span></td>
                    <td>
                    <dsp:droplet name="TableForEach">
                       <dsp:param name="array" param="user.parentOrganization.shippingAddrs"/>
                       <dsp:param name="elementName" value="shippingAddress"/>
                       <dsp:param name="numColumns" value="2"/>
                       <dsp:oparam name="outputStart"><table border=0 width=100%></dsp:oparam>
                       <dsp:oparam name="outputEnd"></table></dsp:oparam>
                       <dsp:oparam name="outputRowStart"><tr></dsp:oparam>
                       <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
                       <dsp:oparam name="output">
                    <dsp:droplet name="IsEmpty">
                    <dsp:param name="value" param="shippingAddress"/>
                    <dsp:oparam name="false">
                         <td valign="top">
                         <%-- individual table for each address --%>
                         <table border=0 width=100%>
			                   <tr>
			                   <td valign="top">
                             
                             <dsp:droplet name="/atg/dynamo/droplet/Format">
                             <dsp:param name="keyValue" param="key"/>
                             <dsp:param name="repositoryId" param="shippingAddress.repositoryId"/>
                             <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                             <dsp:oparam name="output">

			                          <dsp:input bean="MultiUserUpdateFormHandler.value.shippingAddrs.keysAndRepositoryIds" paramvalue="message" name="userShippingAddresses" type="checkbox" />
                             </dsp:oparam>
                             </dsp:droplet>

                           <td>
                             <dsp:getvalueof id="pval0" param="shippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                           </td>
                         </tr>
                         </table>
                         </td>
                     </dsp:oparam>
                     </dsp:droplet>
                       </dsp:oparam>
                       <dsp:oparam name="empty">
                         住所がありません。
                       </dsp:oparam>
                    </dsp:droplet>  <% /* end TableForEach */ %> 
                    </td>
                  </tr>  
                </dsp:oparam>
              </dsp:droplet>  <%-- End of IsEmpty --%>
              
              
              <%-- display payment methods --%>
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="paymentMethods"/>
                <dsp:oparam name="false">

                  <tr valign=right>
                    <td align=right><span class=smallb>クレジットカード</span></td>
                    <td>
                      <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="user.parentOrganization.paymentTypes"/>
                        <dsp:param name="elementName" value="paymentType"/>
                        <dsp:oparam name="output">

                             <dsp:droplet name="/atg/dynamo/droplet/Format">
                             <dsp:param name="keyValue" param="key"/>
                             <dsp:param name="repositoryId" param="paymentType.repositoryId"/>
                             <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                             <dsp:oparam name="output">

			                             <dsp:input bean="MultiUserUpdateFormHandler.value.paymentTypes.keysAndRepositoryIds" paramvalue="message" name="userPaymentTypes" type="checkbox"/>  <dsp:valueof param="key">未指定の支払い方法</dsp:valueof><br>
                             </dsp:oparam>
                             </dsp:droplet>

                        </dsp:oparam>
                        <dsp:oparam name="empty">
                          支払い方法がありません。
                        </dsp:oparam>
                      </dsp:droplet>
                    </td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>  
      
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="costCenters"/>
                <dsp:oparam name="false">

                  <tr valign=right>
                    <td align=right><span class=smallb>コストセンタ</span></td>
                    <td>
                      <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="user.parentOrganization.costCenters"/>
                        <dsp:param name="elementName" value="costCenter"/>
                        <dsp:oparam name="output"> 
                          <dsp:input bean="MultiUserUpdateFormHandler.value.costCenters.repositoryIds" paramvalue="costCenter.repositoryid" name="userCostCenters" type="checkbox"/> 
			  <dsp:valueof param="costCenter.description"> 未指定のコストセンタ </dsp:valueof><br>
                        </dsp:oparam>
                        <dsp:oparam name="empty">
                          コストセンタがありません。
                        </dsp:oparam>
                      </dsp:droplet>
                    </td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>  
        
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="purchaseLimit"/>
                <dsp:oparam name="false">

                  <tr valign=right>
                    <td align=right><span class=smallb>購入制限</span></td>


                     <dsp:setvalue bean="MultiUserUpdateFormHandler.value.orderPriceLimit" value="0.00"/>
                 <td>   <dsp:input bean="MultiUserUpdateFormHandler.value.orderPriceLimit" name="userOrderPriceLimit" size="10" type="text" converter="currency" locale="Profile.priceList.locale"/>    

                    
                    </td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>  
              
            <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
            <tr>
              <td></td> 

          
              <td><dsp:input bean="MultiUserUpdateFormHandler.update" type="submit" value="選択済みユーザに対する変更"/></td>
            </tr>
          </table>          
        </dsp:oparam>
      </dsp:droplet>    
    </dsp:form>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/multiple_user_edit3.jsp#2 $$Change: 651448 $--%>

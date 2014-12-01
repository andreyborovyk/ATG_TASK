<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
<dsp:importbean bean="/atg/dynamo/droplet/ArrayIncludesValue"/>


<dsp:setvalue bean="MultiUserUpdateFormHandler.clear" value=""/> 
<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  ユーザプロファイルの編集"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentUser.parentOrganization.name"/></dsp:a> &gt; 
    <dsp:a href="users.jsp">ユーザの表示</dsp:a> &gt; 
    <dsp:a href="user.jsp">ユーザアカウント</dsp:a> &gt; 
    ユーザプロファイルの編集</span></td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <% /*  main content area */ %>
    <td  valign="top" width=745>
    <dsp:form action="user.jsp" method="post">
      <dsp:input bean="MultiUserUpdateFormHandler.repositoryIds" beanvalue="Profile.currentUser.id" type="hidden"/>  
      <dsp:input bean="MultiUserUpdateFormHandler.UpdateErrorURL" type="hidden" value="user_edit_info.jsp"/>
      <dsp:input bean="MultiUserUpdateFormHandler.confirmPassword" type="hidden" value="false"/>
      <input name="userId" type="hidden" value="<dsp:valueof param="userId"/>">
      <dsp:input bean="MultiUserUpdateFormHandler.value.shippingAddrs.updateMode" type="hidden" value="replace"/>
    
      <table border=0 cellpadding=4 width=80%>
        <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
        <tr valign=top>
          <td colspan=2><span class=big>会社管理</span><br><span class=little><dsp:valueof bean="Profile.currentUser.parentOrganization.name" /></span></td>
        </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
  <td colspan=2>
<% /* Uncomment this code  <dsp:include page="../common/FormError.jsp" flush="true"></dsp:include> */%></td>
      </tr>
      
      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;ユーザプロファイルの編集</td></tr></table>
        </td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
  
        <tr valign=top>
          <td align=right><span class=smallb>ユーザ</span></td>
          <td><dsp:valueof bean="Profile.currentUser.lastName" />&nbsp;<dsp:valueof bean="Profile.currentUser.firstName" /></td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb>ビジネスユニット</span></td>
          <td><dsp:valueof bean="Profile.currentUser.parentOrganization.name">値なし</dsp:valueof></td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb>役割</span></td>
          <td>
            <dsp:droplet name="ForEach">
              <dsp:param bean="Profile.currentUser.roles" name="array"/>
              <dsp:param name="elementName" value="role"/>
              <dsp:oparam name="output">
             
                  <dsp:valueof param="role.name"/> - <dsp:valueof param="role.relativeTo.name"/><br>
             
              </dsp:oparam>
              <dsp:oparam name="empty">
                役割が割り当てられていません<br>
              </dsp:oparam>
            </dsp:droplet>
            <br><span class=smallb><dsp:a href="user_add_role.jsp">役割の追加</dsp:a></span>&nbsp;|&nbsp;<span class=smallb><dsp:a href="user_remove_role.jsp">役割の削除</dsp:a></span>
          </td> 
        </tr>
 
        <tr valign=top>
          <td align=right><span class=smallb>配達先住所</span></td>
          <td width=75%>
       
          <dsp:droplet name="TableForEach">
            <dsp:param bean="Profile.currentUser.parentOrganization.shippingAddrs" name="array"/>
            <dsp:param name="elementName" value="shippingAddress"/>
            <dsp:param name="numColumns" value="2"/>
            <dsp:oparam name="empty">
              このユーザに利用できる配達先住所はありません。
            </dsp:oparam>
            <dsp:oparam name="outputStart">
              <table border=0 cellpadding=2 cellspacing=0 width=100%>
                <tr><td colspan=2><span class=help>チェックマークの付いた住所はユーザが利用できます。</span></td></tr>
                <tr><td><dsp:img src="../images/d.gif"/></td></tr>
            </dsp:oparam>
            <dsp:oparam name="outputEnd"></table></dsp:oparam>
            <dsp:oparam name="outputRowStart"><tr valign=top></dsp:oparam>
            <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>

            <dsp:oparam name="output">
       
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="shippingAddress"/>
                <dsp:oparam name="true">
                  <td></td>
                </dsp:oparam>
                <dsp:oparam name="false">
                  <td>
                  <table border=0 width=100%>
                    <tr>
                      <td valign=top>
                      <dsp:droplet name="ArrayIncludesValue">
                         <dsp:param bean="Profile.currentUser.shippingAddrs" name="array"/>
                         <dsp:param name="value" param="shippingAddress"/>
                         <dsp:oparam name="true">
                           <dsp:droplet name="/atg/dynamo/droplet/Format">
                             <dsp:param name="keyValue" param="key"/>
                             <dsp:param name="repositoryId" param="shippingAddress.repositoryId"/>
                             <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                             <dsp:oparam name="output">
                               <dsp:input bean="MultiUserUpdateFormHandler.value.shippingAddrs.keysAndRepositoryIds" paramvalue="message" type="checkbox" checked="<%=true%>"/></td>
       
                             </dsp:oparam>
                           </dsp:droplet>
                         </dsp:oparam>
                         <dsp:oparam name="false">
 
                           <dsp:droplet name="/atg/dynamo/droplet/Format">
                             <dsp:param name="keyValue" param="key"/>
                             <dsp:param name="repositoryId" param="shippingAddress.repositoryId"/>
                             <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                             <dsp:oparam name="output">
                               <dsp:input bean="MultiUserUpdateFormHandler.value.shippingAddrs.keysAndRepositoryIds" paramvalue="message" type="checkbox"/></td>
      
                            </dsp:oparam>
                          </dsp:droplet>  <% /* End of Format */ %>
                        </dsp:oparam>
                      </dsp:droplet>  <% /* End of ArrayIncludesValue */ %>
                    </td>
                    <td>
                    <dsp:getvalueof id="pval0" param="shippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                    </td>
                    </tr>
                  </table>
                  </td>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
          </td>
        </tr>

        <tr valign=top>
          <td align=right><span class=smallb>請求先住所</span></td>
          <td width=75%>
    
          <dsp:droplet name="TableForEach">
            <dsp:param bean="Profile.currentUser.parentOrganization.billingAddrs" name="array"/>
            <dsp:param name="elementName" value="billingAddress"/>
            <dsp:param name="numColumns" value="2"/>
            <dsp:oparam name="empty">
              このユーザに利用できる請求先住所はありません。
            </dsp:oparam>
            <dsp:oparam name="outputStart">
              <table border=0 cellpadding=2 cellspacing=0 width=100%>
                <tr><td colspan=2><span class=help>チェックマークの付いた住所はユーザが利用できます。</span></td></tr>
                <tr><td><dsp:img src="../images/d.gif"/></td></tr>
            </dsp:oparam>
            <dsp:oparam name="outputEnd"></table></dsp:oparam>
            <dsp:oparam name="outputRowStart"><tr valign=top></dsp:oparam>
            <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
              
            <dsp:oparam name="output">
    
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="billingAddress"/>
                <dsp:oparam name="true">
                  <td></td>
                </dsp:oparam>
                <dsp:oparam name="false">
                  <td>
                  <table border=0 width=100%>
                    <tr>
                    <td valign=top>
                      <dsp:droplet name="ArrayIncludesValue">
                        <dsp:param bean="Profile.currentUser.billingAddrs" name="array"/>
                        <dsp:param name="value" param="billingAddress"/>
                        <dsp:oparam name="true">
                          <dsp:droplet name="/atg/dynamo/droplet/Format">
                            <dsp:param name="keyValue" param="key"/>
                            <dsp:param name="repositoryId" param="billingAddress.repositoryId"/>
                            <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                            <dsp:oparam name="output">

                              <dsp:input bean="MultiUserUpdateFormHandler.value.billingAddrs.keysAndRepositoryIds" paramvalue="message" type="checkbox" checked="<%=true%>"/></td>
      
                            </dsp:oparam>
                          </dsp:droplet>  <% /* End of Format */ %>
                        </dsp:oparam>
                        <dsp:oparam name="false">
                          <dsp:droplet name="/atg/dynamo/droplet/Format">
                            <dsp:param name="keyValue" param="key"/>
                            <dsp:param name="repositoryId" param="billingAddress.repositoryId"/>
                            <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                            <dsp:oparam name="output">
                              <dsp:input bean="MultiUserUpdateFormHandler.value.billingAddrs.keysAndRepositoryIds" paramvalue="message" type="checkbox"/></td>
      
                            </dsp:oparam>
                          </dsp:droplet>  <% /* End of Format */ %>
                          
                        </dsp:oparam>
                      </dsp:droplet>  <% /* End of ArrayIncludesValue */ %>
                      <td>
                      <dsp:getvalueof id="pval0" param="billingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                    </td>
                    </tr>
                  </table>
                  </td>
                </dsp:oparam>
              </dsp:droplet>  <% /* End of IsEmpty (billing address) */ %>
            </dsp:oparam>
          </dsp:droplet>  <% /* End of TableForEach */ %>
          </td>
        </tr>
   
        <tr valign=top>
          <td align=right><span class=smallb>クレジットカード</span></td>
          <td>
          <dsp:droplet name="ForEach">
            <dsp:param bean="Profile.currentUser.parentOrganization.paymentTypes" name="array"/>
            <dsp:param name="elementName" value="paymentType"/>
            <dsp:oparam name="empty">
              このユーザが利用できるクレジットカードはありません。
            </dsp:oparam>
            <dsp:oparam name="output">
             <dsp:droplet name="ArrayIncludesValue">
               <dsp:param bean="Profile.currentUser.paymentTypes" name="array"/>
               <dsp:param name="value" param="paymentType"/>
               <dsp:oparam name="true">
                 <dsp:droplet name="/atg/dynamo/droplet/Format">
                   <dsp:param name="keyValue" param="key"/>
                   <dsp:param name="repositoryId" param="paymentType.repositoryId"/>
                   <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                   <dsp:oparam name="output">
     
                     <dsp:input bean="MultiUserUpdateFormHandler.value.paymentTypes.keysAndRepositoryIds" paramvalue="message" type="checkbox" checked="<%=true%>"/>
           
                   </dsp:oparam>
                 </dsp:droplet> <% /*  End of Format */ %>
                 
               <dsp:valueof param="key">未指定の支払い方法 </dsp:valueof> <br>
               </dsp:oparam>
               <dsp:oparam name="false">
                 <dsp:droplet name="/atg/dynamo/droplet/Format">
                   <dsp:param name="keyValue" param="key"/>
                   <dsp:param name="repositoryId" param="paymentType.repositoryId"/>
                   <dsp:param name="format" value="{keyValue}={repositoryId}"/>
                   <dsp:oparam name="output">
                     <dsp:input bean="MultiUserUpdateFormHandler.value.paymentTypes.keysAndRepositoryIds" paramvalue="message" type="checkbox"/>
           
                   </dsp:oparam>
                 </dsp:droplet> <% /* End of Format */ %>
                 
                 <dsp:valueof param="key">未指定の支払い方法 </dsp:valueof> <br>
               </dsp:oparam>
             </dsp:droplet> <% /* End of ArrayIncludesValue */ %>
           </dsp:oparam>
          </dsp:droplet>  <% /* End of ForEach (payment types) */ %>
          </td>
        </tr>

        <tr valign=top>
          <td align=right><span class=smallb>クレジットカードの認証</span></td>
          <td>
          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.currentUser.creditCardAuthorized" name="value"/>
            <dsp:oparam name="true">はい
              <dsp:input bean="MultiUserUpdateFormHandler.value.creditCardAuthorized" type="checkbox" checked="<%=true%>" />
            </dsp:oparam>
           <dsp:oparam name="false">いいえ
             <dsp:input bean="MultiUserUpdateFormHandler.value.creditCardAuthorized" type="checkbox" checked="<%=false%>" />
            </dsp:oparam>
          </dsp:droplet>
          &nbsp;このユーザのクレジットカードの使用を認証します。</td>
        </tr>

         <tr valign=top>
           <td align=right><span class=smallb>インボイスの認証</span></td>
           <td>
           <dsp:droplet name="Switch">
             <dsp:param bean="Profile.currentUser.invoiceRequestAuthorized" name="value"/>
             <dsp:oparam name="true">
               <dsp:input bean="MultiUserUpdateFormHandler.value.invoiceRequestAuthorized" type="checkbox" checked="<%=true%>"/>
             </dsp:oparam>
             <dsp:oparam name="false">
               <dsp:input bean="MultiUserUpdateFormHandler.value.invoiceRequestAuthorized" type="checkbox" checked="<%=false%>"/>
             </dsp:oparam>
           </dsp:droplet>
           &nbsp;このユーザのインボイス要求を認証します。</td>
        </tr>

        <tr valign=top>
          <td align=right><span class=smallb>コストセンタ</span></td>
          <td>
          <dsp:droplet name="ForEach">
             <dsp:param bean="Profile.currentUser.parentOrganization.costCenters" name="array"/>
             <dsp:param name="elementName" value="costCenter"/>
             <dsp:oparam name="empty">
              このユーザが利用できるコストセンタはありません。
            </dsp:oparam>
            <dsp:oparam name="output">
              <dsp:droplet name="ArrayIncludesValue">
                <dsp:param bean="Profile.currentUser.costCenters" name="array"/>
                <dsp:param name="value" param="costCenter"/>
                <dsp:oparam name="true">
                  <dsp:input bean="MultiUserUpdateFormHandler.value.costCenters.repositoryIds" paramvalue="costCenter.repositoryId" type="checkbox" checked="<%=true%>"/>
                   <dsp:valueof param="costCenter.identifier"/> - <dsp:valueof param="costCenter.description"/> <br>
                </dsp:oparam>
                <dsp:oparam name="false">
                  <dsp:input bean="MultiUserUpdateFormHandler.value.costCenters.repositoryIds" paramvalue="costCenter.repositoryId" type="checkbox"/>
                  <dsp:valueof param="costCenter.identifier"/> - <dsp:valueof param="costCenter.description"/> <br>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
          </td> 
        </tr>


        <tr valign=top>
          <td align=right><span class=smallb>承認</span></td>
          <td>
          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.currentUser.approvalRequired" name="value"/>
            <dsp:oparam name="true">
              <dsp:input bean="MultiUserUpdateFormHandler.value.approvalRequired" type="checkbox" checked="<%=true%>"/>
            </dsp:oparam>
            <dsp:oparam name="false">
              <dsp:input bean="MultiUserUpdateFormHandler.value.approvalRequired" type="checkbox" checked="<%=false%>"/>
            </dsp:oparam>
          </dsp:droplet>
          &nbsp;購入制限を超えるオーダーにはすべて承認が必要です。</td>
        </tr>

        <tr valign=top>
          <td align=right><span class=smallb>購入制限</span></td>
          <td><span class=help>承認が必要な場合にのみ必要です。承認が必要であり、購入制限が設定されていない場合は、すべてのオーダーについて承認が必要です。<br>
          
          <dsp:setvalue bean="MultiUserUpdateFormHandler.value.orderPriceLimit" beanvalue="Profile.CurrentUser.orderPriceLimit"/>
          <dsp:input bean="MultiUserUpdateFormHandler.value.orderPriceLimit" size="10" type="text" converter="currency" locale="Profile.priceList.locale"/>                   
          &nbsp;<span class=help>会社の制限は 
          <dsp:getvalueof id="pval0" bean="Profile.currentUser.parentOrganization.orderPriceLimit"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>.</span><br>
              <br>
          </td>
        </tr>
        
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="4"/></td></tr>

      <tr>
        <td></td>
        <td>
        <dsp:input bean="MultiUserUpdateFormHandler.update" type="submit" value=" 保存 "/>
        &nbsp; <input type="submit" value="キャンセル">
        </td>
      </tr>
    </table>
    </dsp:form>
    </td>
  </tr>

</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/user_edit_info.jsp#2 $$Change: 651448 $--%>

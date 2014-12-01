<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" 私のアカウント"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">私のアカウント</dsp:a> &gt; <dsp:a href="my_profile.jsp">私のプロファイル</dsp:a> &gt; 会社情報の編集</td>
  </tr>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td  valign="top" width=745>
    <dsp:form action="my_profile.jsp" method="post">
    <table border=0 cellpadding=4 width=90%>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>私のアカウント</span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <!--display error to the user -->
      <tr>
        <td colspan=2>
        <dsp:include page="../common/FormError.jsp"></dsp:include></td>
      </tr>

      <dsp:input bean="B2BRepositoryFormHandler.repositoryId" beanvalue="Profile.id" type="hidden"/>
      <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="user"/>
      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;アカウントのデフォルト情報の編集</td></tr></table>
        </td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      
      <tr>
        <td colspan=2><span class=small>
           会社情報からデフォルトを選択してください。
           </span></td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      
      <tr>
        <td align=right><span class=smallb>デフォルトのコストセンタ</span></td>
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.costCenters" name="value"/>
          <dsp:oparam name="true">
            コストセンタがありません。
          </dsp:oparam>
          <dsp:oparam name="false">


          <dsp:select bean="B2BRepositoryFormHandler.value.defaultCostCenter.REPOSITORYID">
          <dsp:getvalueof id="defaultCCId" idtype="java.lang.String" bean="Profile.defaultCostCenter.id">
          <dsp:droplet name="ForEach">
            <dsp:param bean="Profile.costcenters" name="array"/>
            <dsp:oparam name="output">
              <dsp:getvalueof id="ccId" idtype="java.lang.String" param="element.id">  
              
              <dsp:droplet name="Switch">
                <dsp:param name="value" value="<%=ccId%>"/>
                <dsp:oparam name="<%=defaultCCId%>">
                  <dsp:option selected="<%=true%>" value="<%=ccId%>"/>
                    <dsp:valueof  param="element.identifier"/> -
                    <dsp:valueof  param="element.description"/>
                </dsp:oparam>
                <dsp:oparam name="default">
                  <dsp:option value="<%=ccId%>"/>
                    <dsp:valueof  param="element.identifier"/> -
                    <dsp:valueof  param="element.description"/>
                </dsp:oparam>
              </dsp:droplet>
              </dsp:getvalueof>

            </dsp:oparam>
          </dsp:droplet>
          </dsp:getvalueof>
          </dsp:select>
        </dsp:oparam>
      </dsp:droplet>

         </td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>デフォルトの配達先住所</span></td>
        <td width=75%>

            <dsp:getvalueof id="defaultShippingId" idtype="java.lang.String" bean="Profile.defaultShippingAddress.repositoryId">
            <!--display shipping addresses in table -->
            <dsp:droplet name="TableForEach">
              <dsp:param name="array" bean="Profile.shippingaddrs"/>
              <dsp:param name="numColumns" value= "2"/>
              <dsp:oparam name="outputStart"><table border=0 cellpadding=0 cellpadding=0 width=100%></dsp:oparam>
              <dsp:oparam name="outputEnd"></table></dsp:oparam>
              <dsp:oparam name="outputRowStart"><tr valign=top></dsp:oparam>
              <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
              <dsp:oparam name="output">
                <!--check to see if there is address for tablecell-->
                <dsp:droplet name="IsEmpty">
                  <dsp:param param="element" name="value" />
                  <dsp:oparam name="true">
                    <td></td>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <td>
                    <table border=0 cellpadding=3 width=100%>
                      <tr valign=top>
                        <td>
                        
                        <dsp:getvalueof id="shippingId" idtype="java.lang.String" param="element.repositoryId">
  
                        <dsp:droplet name="Switch">
                          <dsp:param name="value" value="<%=shippingId%>" />
                          <dsp:oparam name="<%=defaultShippingId%>">
                            <dsp:input bean="B2BRepositoryFormHandler.value.defaultShippingAddress.REPOSITORYID" value="<%=shippingId%>" type="radio" checked="<%=true%>"/>
                          </dsp:oparam>
                          <dsp:oparam name="default">
                            <dsp:input bean="B2BRepositoryFormHandler.value.defaultShippingAddress.REPOSITORYID" value="<%=shippingId%>" type="radio" />
                          </dsp:oparam>
                        </dsp:droplet>
                        
                        </dsp:getvalueof>
                        </td>
                        <td>
                          <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" param="element"/></dsp:include>
                        </td>
                      </tr>
                    </table>
                    </td>
                  </dsp:oparam>
                </dsp:droplet><!--end IsEmpty-->
              </dsp:oparam>
            </dsp:droplet><!--end TableForEach-->

          </dsp:getvalueof>
        </td>
      </tr>


      <dsp:droplet name="Switch">
      <dsp:param bean="Profile.invoiceRequestAuthorized" name="value"/>
      <dsp:oparam name="true">
       <tr valign=top>
        <td align=right><span class=smallb>デフォルトの請求先住所</span></td>
        <td width=75%>

          <dsp:getvalueof id="defaultBillingId" idtype="java.lang.String" bean="Profile.defaultBillingAddress.repositoryId">
            <!--display billing addresses in table -->
            <dsp:droplet name="TableForEach">
              <dsp:param name="array" bean="Profile.billingaddrs"/>
              <dsp:param name="numColumns" value="2"/>
              <dsp:oparam name="outputStart"><table border=0 cellpadding=0 cellpadding=0 width=100%></dsp:oparam>
              <dsp:oparam name="outputEnd"></table></dsp:oparam>
              <dsp:oparam name="outputRowStart"><tr valign=top></dsp:oparam>
              <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
              <dsp:oparam name="output">
                <!--check to see if there is address for table cell-->
                <dsp:droplet name="IsEmpty">
                  <dsp:param param="element" name="value" />
                  <dsp:oparam name="true">
                    <td></td>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <td>
                    <table border=0 cellpadding=3 width=100%>
                      <tr valign=top>
                        <td>
                        
                        <dsp:getvalueof id="billingId" idtype="java.lang.String" param="element.repositoryId">
                        <dsp:droplet name="Switch">
                          <dsp:param name="value" value="<%=billingId%>" />
                          <dsp:oparam name="<%=defaultBillingId%>">
                            <dsp:input bean="B2BRepositoryFormHandler.value.defaultBillingAddress.REPOSITORYID" value="<%=billingId%>" type="radio" checked="<%=true%>"/>
                                            </dsp:oparam>
                          <dsp:oparam name="default">
                            <dsp:input bean="B2BRepositoryFormHandler.value.defaultBillingAddress.REPOSITORYID" value="<%=billingId%>" type="radio" />
                          </dsp:oparam>
                        </dsp:droplet>
                        </dsp:getvalueof>
                        </td>
                        <td>
                          <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" param="element"/></dsp:include>
                        </td>
                      </tr>
                    </table>
                    </td>
                  </dsp:oparam>
                </dsp:droplet><!--end IsEmpty-->
              </dsp:oparam>
            </dsp:droplet><!--end TableForEach-->

          </dsp:getvalueof>
        </td>
      </tr>
     </dsp:oparam>
     </dsp:droplet>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=4></td></tr>
      <tr>
        <td></td>
        <td>

            <dsp:input bean="B2BRepositoryFormHandler.updateSuccessURL" type="hidden" value="my_profile.jsp"/>
            <dsp:input bean="B2BRepositoryFormHandler.updateErrorURL" type="hidden" value="edit_company_info.jsp"/>

            <dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value="選択したデフォルトの保存"/>
            <input type="submit" value="キャンセル">
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/edit_company_info.jsp#2 $$Change: 651448 $--%>

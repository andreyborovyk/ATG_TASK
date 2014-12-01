<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/AddressLookUp"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>


<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  請求先住所の編集"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    <dsp:a href="billing_addresses.jsp">請求先住所</dsp:a> &gt; 請求先住所の編集</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
   <dsp:form action="billing_addresses.jsp" method="post">
   <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="contactInfo"/>
   <dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="billingId" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.updateSuccessURL" type="hidden" value="billing_addresses.jsp"/>
  
   <%--  Construct the URL to redirect to, in case of an error during form submission --%>
    <dsp:droplet name="Format">
    <dsp:param name="value1" param="billingId"/>
    <dsp:param name="value2" param="nickName"/>
    <dsp:param name="format" value="billing_address_edit.jsp?billingId={value1}&nickName={value2}"/>
    <dsp:oparam name="output">
      <dsp:setvalue paramvalue="message" valueishtml="<%=true%>" param="updateErrorURL"/>
      <dsp:input bean="B2BRepositoryFormHandler.updateErrorURL" paramvalue="updateErrorURL" type="hidden"/>
     </dsp:oparam>
    </dsp:droplet>
    

   
        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>会社管理</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top" height="27"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;請求先住所の編集</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr> 
            <td><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2> 
              
        
     <dsp:droplet name="AddressLookUp">
     <dsp:param name="id" param="billingId"/>
     <dsp:param name="elementName" value="billingAddress"/>
     <dsp:oparam name="output">
        <table width="100%" border="0">
                <tr> 
                  <td align=right width="18%"><span class=smallb>ニックネーム</span></td>
                  <td width=67%><dsp:valueof param="nickName">ニックネームなし</dsp:valueof></td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>会社名</span></td>
                  <td width=67%><dsp:input bean="B2BRepositoryFormHandler.value.companyName" paramvalue="billingAddress.companyName" size="30" type="text"/></td>
                </tr>
                <tr> 
                  <td valign="top" align="right" width="18%"><span class=smallb>住所</span></td>
                  <td width="67%"> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.address1" paramvalue="billingAddress.address1" size="30" type="text"/>
                    <br>
                    <dsp:input bean="B2BRepositoryFormHandler.value.address2" paramvalue="billingAddress.address2" size="30" type="text"/>
                  </td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>市町村</span></td>
                  <td width="67%"><dsp:input bean="B2BRepositoryFormHandler.value.city" paramvalue="billingAddress.city" size="30" type="text"/></td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>都道府県</span></td>
                  <td width="67%"><dsp:input bean="B2BRepositoryFormHandler.value.state" paramvalue="billingAddress.state" size="10" type="text"/></td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>郵便番号</span></td>
                  <td valign="top" width="67%"> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.postalCode" paramvalue="billingAddress.postalCode" size="10" type="text"/>
                  </td>
                </tr>
                <tr> 
                  <td align=right width="18%"><span class=smallb>国</span></td>
                  <td width="67%"> 
                    <dsp:select bean="B2BRepositoryFormHandler.value.country">
                       <%@ include file="../common/CountryPicker.jspf" %>
                    </dsp:select>
                  </td>
                </tr>
                <tr> 
                  <td height="56" width="18%"></td>
                  <td height="56" width="67%"> 
                   
                    <dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value=" 保存 "/>
                    &nbsp; 
                   
                    <input type="submit" value=" キャンセル" name="submit">
                    </td>
                </tr>
				      </table>
              </dsp:oparam>
              </dsp:droplet>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/billing_address_edit.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  配達先住所の作成"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    <dsp:a href="shipping_edit.jsp">配達先住所</dsp:a> &gt; 配達先住所の作成</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    <dsp:include page="../common/FormError.jsp"></dsp:include> 
     <dsp:form action="shipping_edit.jsp" method="post">
       <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="contactInfo"/>
       <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
       <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
       <dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="shippingAddrs"/>
       <dsp:input bean="B2BRepositoryFormHandler.requireIdOnCreate" type="hidden" value="false"/>
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="COMPANYNAME"/>
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="ADDRESS1"/> 
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="CITY"/> 
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="POSTALCODE"/> 
       <dsp:input bean="B2BRepositoryFormHandler.createErrorURL" type="hidden" value="shipping_address_create.jsp"/>
       <dsp:input bean="B2BRepositoryFormHandler.createSuccessURL" type="hidden" value="shipping_edit.jsp"/>
        
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
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;配達先住所の作成</td>
                </tr>
              </table>
            </td>
          </tr>
      <tr>
        <td colspan=2><span class=small>ニックネームフィールドを使用すると、正確な住所を知らなくても、この住所を識別できます。他の配達先住所のニックネームとは異なるニックネームにする必要があります。会社名と同じ名前を使用できます。</span></td>
      </tr>

          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr> 
            <td align=right><span class=smallb>ニックネーム</span></td>
            <td width=75%><dsp:input bean="B2BRepositoryFormHandler.updateKey" size="30" type="text" value=" "/></td>
          </tr>

          <tr> 
            <td align=right><span class=smallb>会社名</span></td>
            <td width=75%><dsp:input bean="B2BRepositoryFormHandler.value.companyName" size="30" type="text" value=""/></td>
          </tr>
          <tr>
            <td align=right valign="top"><span class=smallb>住所</span></td>
            <td> 
              <dsp:input bean="B2BRepositoryFormHandler.value.address1" size="30" type="text" value=""/>
              <br>
              <dsp:input bean="B2BRepositoryFormHandler.value.address2" name="text" size="30" type="text" value=""/>
            </td>
          </tr>
          <tr>
            <td align=right><span class=smallb>市町村</span></td>
            <td> 
              <dsp:input bean="B2BRepositoryFormHandler.value.city" size="30" type="text" value=""/>
            </td>
          </tr>
          <tr> 
            <td align=right><span class=smallb>都道府県</span></td>
            <td> 
              <dsp:input bean="B2BRepositoryFormHandler.value.state" size="10" type="text" value=""/>
            </td>
          </tr>
          <tr>
            <td align=right><span class=smallb>郵便番号</span></td>
            <td valign="top"> 
              <dsp:input bean="B2BRepositoryFormHandler.value.postalCode" size="10" type="text" value=""/>
            </td>
          </tr>
          <tr>
            <td align=right><span class=smallb>国</span></td>
            <td> 
              <dsp:select bean="B2BRepositoryFormHandler.value.country">
                <%@ include file="../common/CountryPicker.jspf" %>
              </dsp:select>
            </td>
          </tr>
          <tr> 
            <td><dsp:img src="../images/d.gif"/></td>
            <td><br>
              <dsp:input bean="B2BRepositoryFormHandler.create" type="submit" value="保存"/>
              &nbsp; 
              <input type="submit" value="キャンセル">
            </td>


        </table>
      </dsp:form>
      </td>
    </tr>
</table>

</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/shipping_address_create.jsp#2 $$Change: 651448 $--%>

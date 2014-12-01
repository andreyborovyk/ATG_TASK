<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  コストセンタの作成"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    <dsp:a href="cost_centers.jsp">コストセンタ</dsp:a> &gt; コストセンタの追加
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    

   <%-- Display errors if any --%>

   <dsp:include page="../common/FormError.jsp"></dsp:include>
 
   <dsp:form action="cost_centers.jsp" method="post">
   <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="cost-center"/>
   <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
   <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="costCenters"/>
   <dsp:input bean="B2BRepositoryFormHandler.requireIdOnCreate" type="hidden" value="false"/>
   <dsp:input bean="B2BRepositoryFormHandler.value.owner" beanvalue="Profile.id" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.createErrorURL" type="hidden" value="cost_center_create.jsp"/>

    <dsp:img src="../images/d.gif" hspace="300"/><br>
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
                  <td class=box-top>&nbsp;コストセンタの追加</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan=2 height="92"> 
              <table width="100%" border="0">
                <tr>
                  <td></td><td><span class=smallb>ID 番号</span></td>
                  <td width="1"><dsp:img src="../images/d.gif"/></td><td><span class=smallb>名前</span></td> 
                </tr>        
                <tr>
                  <td></td><td width="10%"><dsp:input bean="B2BRepositoryFormHandler.value.identifier" maxlength="10" size="10" type="text" value=" "/></td>
                  <td width> - </td><td><dsp:input bean="B2BRepositoryFormHandler.value.description" maxlength="25" size="25" type="text" value=" "/></td> 
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr><td></td><td colspan="3" align="left"><dsp:input bean="B2BRepositoryFormHandler.create" type="submit" value="保存"/></td></tr>
              </table>
        </dsp:form>
            </td>
          </tr>
    
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        
          <%--  vertical space --%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        </table>
    
    </td>
  </tr>


</table>
</div>
</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/cost_center_create.jsp#2 $$Change: 651448 $--%>

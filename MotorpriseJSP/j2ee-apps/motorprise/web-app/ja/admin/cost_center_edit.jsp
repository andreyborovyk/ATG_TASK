<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  コストセンタの編集"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    <dsp:a href="cost_centers.jsp">コストセンタ</dsp:a> &gt コストセンタの編集
    </span> </td>
  </tr>
  <tr valign="top"> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
    <%-- Display errors if any --%>

    <dsp:include page="../common/FormError.jsp"></dsp:include> 

    <dsp:form action="cost_centers.jsp" method="post">
     <dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="ccId" type="hidden"/>
     <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="cost-center"/>
     <dsp:input bean="B2BRepositoryFormHandler.cancelURL" type="hidden" value="cost_centers.jsp"/>

     <dsp:input bean="B2BRepositoryFormHandler.updateSuccessURL" type="hidden" value="cost_centers.jsp"/>
  
     <%--  Construct the URL to redirect to, in case of an error during form submission --%>
     <dsp:droplet name="Format">
     <dsp:param name="value1" param="ccId"/>
     <dsp:param name="value2" param="identifier"/>
     <dsp:param name="value3" param="description"/>
     <dsp:param name="format" value="cost_center_edit.jsp?ccId={value1}&identifier={value2}&description={value3}"/>
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
            <td colspna=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;コストセンタの編集</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan=2 height="92"> 
              <table width="100%" cellpadding=0 cellspacing=0 border="0">
                <tr>
                  <td><span class=smallb>ID 番号</span></td>
                  <td>&nbsp;<td><span class=smallb>名前</span></td> 
                </tr>        
                <tr>
                  <td width="10%"><dsp:input bean="B2BRepositoryFormHandler.value.identifier" paramvalue="identifier" maxlength="10" size="10" type="text"/></td>
                  <td align=center> - </td><td><dsp:input bean="B2BRepositoryFormHandler.value.description" paramvalue="description" maxlength="25" size="25" type="text"/></td> 
                </tr>
                <tr><td colspan=3>&nbsp;</td></tr>
                <tr>
             
          <td colspan="3" align="left"><dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value="保存"/> &nbsp;<dsp:input bean="B2BRepositoryFormHandler.cancel" type="submit" value="キャンセル"/></td>
                </tr>
              </table>
            </td>
          </tr>
    
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        
          <%--  vertical space --%>
          <tr> 
            <td colspan=><img src="../images/d.gif" vspace=0></td>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/cost_center_edit.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>



<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Unternehmensverwaltung"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    <dsp:a href="users.jsp">Benutzer anzeigen</dsp:a> &gt;
    <dsp:a href="user.jsp">Benutzerkonto</dsp:a> &gt; 
    <dsp:a href="user_edit_info.jsp"><dsp:param bean="Profile.currentUser.id" name="userId"/>Benutzerprofil bearbeiten</dsp:a> &gt;
    Rolle entfernen
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>  
    
        <table border=0 cellpadding=4 width=80%>
          <tr>
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          
          <tr> 
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Benutzerrolle entfernen</td>
                </tr>
              </table>
            </td>
          </tr>
          
          <dsp:form action="user_edit_info.jsp" method="post">
          <dsp:input bean="MultiUserUpdateFormHandler.repositoryIds" beanvalue="Profile.currentUser.id" type="hidden"/>  
          <dsp:input bean="MultiUserUpdateFormHandler.roleUpdateMethod" type="hidden" value="remove"/>
          <tr><td colspan=2><span class=small>Zu entfernende Rollen f�r diesen Benutzer ausw�hlen</span></td></tr>
        
          <tr valign=top>
            <td align=right><span class=smallb>Rollen</span></td>
            <td>
             <dsp:select bean="MultiUserUpdateFormHandler.roleIds">
               <dsp:droplet name="ForEach">
                 <dsp:param bean="Profile.currentUser.roles" name="array"/>
                 <dsp:param name="elementName" value="role"/>
                 <dsp:param name="indexName" value="roleIndex"/>
                 <dsp:oparam name="output">
                  <dsp:getvalueof id="orgId" idtype="java.lang.String" bean="Profile.currentOrganization.repositoryId">
                   <dsp:droplet name="Switch">
                    <dsp:param name="value" param="role.relativeTo.repositoryId"/>
                    <dsp:oparam name="<%=orgId%>">
                        <dsp:getvalueof id="roleId" idtype="java.lang.String" param="role.repositoryId">
                        <dsp:option value="<%=roleId%>"/><dsp:valueof param="role.name">Unbenannte Rolle</dsp:valueof> - <dsp:valueof param="role.relativeTo.name"/> 
                        </dsp:getvalueof>
                    </dsp:oparam>

                    <dsp:oparam name="org3004back">
                       <dsp:option value="empty"/>  <dsp:valueof param="role.relativeTo.repositoryId"> Keine Rolle</dsp:valueof> - leer -
                       <dsp:option value="empty1"/> <dsp:getvalueof id="repId" bean="Profile.currentOrganization.repositoryId"> Wert ist: <%=repId%></dsp:getvalueof> 
                    </dsp:oparam>
                   </dsp:droplet>

                  </dsp:getvalueof>
                  
                 </dsp:oparam>
    
              
               </dsp:droplet>
             </dsp:select>
            </td> 
          </tr>
          <tr>
            <td></td> 
            <td><dsp:input bean="MultiUserUpdateFormHandler.update" type="submit" value="Entfernen"/>&nbsp;<input type="submit" value="Abbrechen"></td>
          </tr>

          </dsp:form>
    
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
           
        
          <% /*  vertical space */ %>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/user_remove_role.jsp#2 $$Change: 651448 $--%>

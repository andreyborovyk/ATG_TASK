<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Document Exchange Gear
   gearmode = content 
   displaymode = full
  
   This fragment is included by DocExchFull when the value
   of request parameter dexmode=delete.  It presents a form
   in which the user can confirm or cancel the deletion of
   a new.
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= gearEnv %>">

<dsp:importbean bean="/atg/portal/gear/docexch/DocumentFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/portal/gear/docexch/PermissionsDroplet"/>
<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />

<% String lineColor = "#666666"; %>

<%-- check write permission before presenting a form --%>
<dsp:droplet name="PermissionsDroplet">
   <dsp:param name="pafEnv" value="<%= gearEnv %>"/>

   <dsp:oparam name="mayNotWrite">
     <font class="error"><blockquote><br>
       <i18n:message key="generalPermissionDeniedMsg"/>
     </blockquote></font>
   </dsp:oparam>

   <dsp:oparam name="mayWrite">

      <dsp:setvalue bean="DocumentFormHandler.itemDescriptorName" 
           value="<%= dexpage.getItemDescriptorName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.repositoryPathName" 
           value="<%= dexpage.getRepositoryPath() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.repositoryId" 
           paramvalue="documentid"/>
      
      <%
        // set up some variables that we will need later for form fields 
        String titleBeanValue = 
        new StringBuffer("DocumentFormHandler.value.").append(dexpage.getTitlePropertyName()).toString();
        String gearInstanceRefBeanValue = 
        new StringBuffer("DocumentFormHandler.value.").append(dexpage.getGearIdPropertyName()).toString();
      %>
      
      <dsp:getvalueof id="doctitle" bean="<%= titleBeanValue %>">
        <i18n:message id="deleteItemFormTitle" key="deleteItemFormTitle">
          <i18n:messageArg value="<%= doctitle %>"/>
        </i18n:message>
        <i18n:message id="confirmDelete" key="confirmDelete">
          <i18n:messageArg value="<%= doctitle %>"/>
        </i18n:message>
      <i18n:message id="cancelDeleteButton" key="cancelButton"/>
      <i18n:message id="deleteButton" key="deleteButton"/>
      
      
      
      <P><!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="/atg/portal/gear/docexch/DocumentFormHandler.formError" />
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="DocumentFormHandler.formExceptions"/>
               <dsp:oparam name="output">
                  <dsp:getvalueof id="errorMsg" idtype="atg.droplet.DropletException" param="element">
                  <font class="error"><img src='<%= dexpage.getRelativeUrl("/images/error.gif")%>'
                  >&nbsp;&nbsp;<i18n:message key="<%= errorMsg.getErrorCode() %>"/></font><br/>
                  </dsp:getvalueof>
               </dsp:oparam>
            </dsp:droplet>
         </dsp:oparam>
      </dsp:droplet>
      

      <%-- Is this document accessible in this gear environment? --%> 
      <dsp:getvalueof id="docElement" bean="DocumentFormHandler.repositoryItem" idtype="atg.repository.RepositoryItem">
      <dex:isDocInGear gearEnv="<%= gearEnv %>" document="<%= docElement %>">


      <dsp:form method="post" action="<%= gearEnv.getOriginalRequestURI() %>">
      
        <input type="hidden" name="paf_gear_id" value="<%= gearEnv.getGear().getId()%>"/>
        <input type="hidden" name="paf_community_id" value="<%= gearEnv.getCommunity().getId() %>"/>
        <input type="hidden" name="paf_dm" value="full"/>
        <input type="hidden" name="dexmode" value="list"/>
        <input type="hidden" name="prevpaf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
        <dsp:input type="hidden" bean="DocumentFormHandler.repositoryId" paramvalue="documentid"/>
        <dsp:input type="hidden" bean="<%= gearInstanceRefBeanValue %>" value="<%= gearEnv.getGear().getId()%>"/>
        <dsp:input type="hidden" bean="DocumentFormHandler.annotationRefPropertyName" value="<%= dexpage.getAnnotationRefPropertyName() %>"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.repositoryPathName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.itemDescriptorName"/>
      
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="prevpaf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
           <core:UrlParam param="paf_dm" value='<%= request.getParameter("prevpaf_dm") %>'/>
           <core:UrlParam param="paf_gm" value="content"/>
           <core:UrlParam param="dexmode" value="list"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <dsp:input type="hidden" bean="DocumentFormHandler.deleteSuccessURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="prevpaf_dm" value='<%= request.getParameter("prevpaf_dm") %>'/>
           <core:UrlParam param="paf_dm" value="full"/>
           <core:UrlParam param="paf_gm" value="content"/>
           <core:UrlParam param="dexmode" value="delete"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <dsp:input type="hidden" bean="DocumentFormHandler.deleteErrorURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
      
      
      
        <TABLE WIDTH="667" BORDER="0" CELLSPACING="0" CELLPADDING="0">
          <TR>
            <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></TD>
          </TR>
          <TR>
            <TD colspan=2>
              <font class="medium_bold">&nbsp;
                <%= deleteItemFormTitle %>
              </font>
            </TD>
          </TR>
          <TR>
            <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></TD>
          </TR>
          <TR>
          <TR>
            <TD colspan=2 bgcolor="<%= lineColor %>"><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"></TD>
          </TR>
          <TR>
            <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
          </TR>
          <TR>
            <TD colspan=2 >
              <font class="medium">&nbsp;&nbsp;<%= confirmDelete %></font>          
            </TD>
          </TR>
          <TR>
            <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
          </TR>
      	  <TR colspan=2 VALIGN="top" ALIGN="left"> 
      	    
      	    <TD>
                   &nbsp;&nbsp;<dsp:input type="submit" value="<%= deleteButton %>" bean="DocumentFormHandler.delete"/>
                   &nbsp;&nbsp;&nbsp;
                   <dsp:input type="submit" value="<%= cancelDeleteButton %>" bean="DocumentFormHandler.cancelDelete"/>
            </TD>
      	  </TR>
      	  <TR>
            <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
          </TR>
      </TABLE>

     </dsp:form>

     </dex:isDocInGear>
     </dsp:getvalueof>


    </dsp:getvalueof>


   </dsp:oparam> <%-- end mayWrite --%>
</dsp:droplet>

</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/deleteDoc.jsp#2 $$Change: 651448 $--%>

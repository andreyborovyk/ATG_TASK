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
   of request parameter dexmode=editStatus.  It presents a form
   that the user may fill out to edit status of the document.
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= gearEnv %>">
<dsp:importbean bean="/atg/portal/gear/docexch/DocumentFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/portal/gear/docexch/PermissionsDroplet"/>
<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
<% String lineColor = "#666666"; %>


<%-- check permission before presenting a form --%>
<dsp:droplet name="PermissionsDroplet">
   <dsp:param name="pafEnv" value="<%= gearEnv %>"/>

   <dsp:oparam name="mayNotUpdateStatus">
     <font class="error"><blockquote><br>
       <i18n:message key="generalPermissionDeniedMsg"/>
     </blockquote></font>
   </dsp:oparam>

   <dsp:oparam name="mayUpdateStatus">

      <i18n:message id="updateButton" key="updateButton"/>
      <i18n:message id="cancelButton" key="cancelButton"/>
      <i18n:message id="separator" key="labelFieldSeparator"/>
      <%
        // set up some variables that we will need later for form fields 
        String statusBeanValue = new StringBuffer("DocumentFormHandler.value.").append(dexpage.getStatusPropertyName()).toString();
        String titleBeanValue = new StringBuffer("DocumentFormHandler.value.").append(dexpage.getTitlePropertyName()).toString();      
      %>

      <dsp:setvalue bean="DocumentFormHandler.itemDescriptorName" 
           value="<%= dexpage.getItemDescriptorName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.repositoryPathName" 
           value="<%= dexpage.getRepositoryPath() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.repositoryId" 
           paramvalue="documentid"/>
      
      <!-- display errors if any -->
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
        <core:UrlParamValue id="prevpaf_dm" param="prevpaf_dm">
          <input type="hidden" name="prevpaf_dm" value="<%= prevpaf_dm %>"/>
        </core:UrlParamValue>
        <input type="hidden" name="paf_dm" value="full"/>
        <input type="hidden" name="dexmode" value="oneItem"/>
        <core:UrlParamValue id="documentId" param="documentid">
          <input type="hidden" name="documentid" value="<%= documentId %>"/>
        </core:UrlParamValue>

        <dsp:input type="hidden" priority="<%=99%>" bean="DocumentFormHandler.repositoryId"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.repositoryPathName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.itemDescriptorName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.maxDescriptionLength"
              value="<%= dexpage.getDescriptionMaxLength() %>"/>
      
      
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="prevpaf_dm" value='<%= request.getParameter("prevpaf_dm") %>'/>
           <core:UrlParam param="paf_dm" value="<%= gearEnv.getDisplayMode() %>"/>
           <core:UrlParam param="paf_gm" value="<%= gearEnv.getGearMode() %>"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <core:UrlParam param="documentid" value='<%= request.getParameter("documentid") %>'/>
           <core:UrlParam param="dexmode" value="oneItem"/>
           <dsp:input type="hidden" bean="DocumentFormHandler.updateSuccessURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
      
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="prevpaf_dm" value='<%= request.getParameter("prevpaf_dm") %>'/>
           <core:UrlParam param="paf_dm" value="<%= gearEnv.getDisplayMode() %>"/>
           <core:UrlParam param="paf_gm" value="<%= gearEnv.getGearMode() %>"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <core:UrlParam param="documentid" value='<%= request.getParameter("documentid") %>'/>
           <core:UrlParam param="dexmode" value="editStatus"/>
           <dsp:input type="hidden" bean="DocumentFormHandler.updateErrorURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
      
        <TABLE WIDTH="667" BORDER="0" CELLSPACING="0" CELLPADDING="0">
      
             <TR>
               <TD colspan="2" ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></TD>
              </TR>
              <TR>
                <TD colspan="2">
                  <font class="medium_bold">&nbsp;
                    <dsp:getvalueof id="doctitle" bean="<%= titleBeanValue %>">
                    <i18n:message key="updateStatusFormTitle">
                      <i18n:messageArg value="<%= doctitle %>"/>
                    </i18n:message>
                  </dsp:getvalueof>
                  </font>
                </TD>
              </TR>
             <TR>
               <TD colspan="2" ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></TD>
              </TR>
              <TR>
                <TD colspan=2 bgcolor="<%= lineColor %>"><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"></TD>
              </TR>
              <TR>
                <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
              </TR>
      
              <TR VALIGN="top" ALIGN="left"> 
                <TD WIDTH="25%" align="right"><font class="small"><i18n:message key="statusColumnHeader"/><%= separator %>&nbsp;&nbsp;</font></TD>
                <TD WIDTH="75%">
      	           <dsp:select priority="<%=50%>" bean="<%= statusBeanValue %>">
        	     <dsp:option value="1"/><i18n:message key="statusChoice1"/>
        	     <dsp:option value="2"/><i18n:message key="statusChoice2"/>
        	     <dsp:option value="3"/><i18n:message key="statusChoice3"/>
        	     <dsp:option value="4"/><i18n:message key="statusChoice4"/>
        	     <dsp:option value="5"/><i18n:message key="statusChoice5"/>
      	           </dsp:select>
                 </TD>
              </TR>
              <TR>
                <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
              </TR>
                
              
              <TR VALIGN="top" ALIGN="left"> 
                <TD>&nbsp;</TD>
                <TD>
                        <dsp:input type="submit" value="<%= updateButton %>" bean="DocumentFormHandler.update"/>
                        &nbsp;&nbsp;&nbsp;
                        <dsp:input type="submit" value="<%= cancelButton %>" bean="DocumentFormHandler.cancel"/>
                      </TD>
              </TR>
              <TR>
                <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
              </TR>
        </TABLE>
      
      </dsp:form>

     </dex:isDocInGear>
     </dsp:getvalueof>

   </dsp:oparam> <%-- end mayEditStatus --%>
</dsp:droplet>

</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/editStatusForm.jsp#2 $$Change: 651448 $--%>

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
   of request parameter dexmode=edit.  It presents a form
   that the user may fill out to edit the metadata of an 
   existing  a new document.
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
     <font class="medium"><blockquote><br>
       <i18n:message key="generalPermissionDeniedMsg"/>
     </blockquote></font>
   </dsp:oparam>

   <dsp:oparam name="mayWrite">

      <%
        // set up some variables that we will need later for form fields 
        String docValue = "DocumentFormHandler.value.";
        String titleBeanValue = docValue + dexpage.getTitlePropertyName();
        String descriptionBeanValue = docValue + dexpage.getDescriptionPropertyName();
        String filenameBeanValue = docValue + dexpage.getFilenamePropertyName();
        String gearInstanceRefBeanValue = docValue + dexpage.getGearIdPropertyName();
	String clearImgUrl = dexpage.getRelativeUrl("/images/clear.gif");
      %>
      <%--
        Test to make sure documentId is there.  This gives us a heads up if we missed a 
        parameter somewhere.
      --%> 
      <core:urlParamValue id="documentId" param="documentid">
        <core:test id="documentIdTest" value="<%= documentId %>">
          <core:if value="<%= documentIdTest.isEmpty() %>">
            <dsp:getvalueof id="handlerObj" bean="DocumentFormHandler">
              <core:cast id="docHandler" value="<%= handlerObj %>" castClass="atg.portal.gear.docexch.DocumentFormHandler">
                <core:if value="<%= docHandler.isLoggingDebug() %>">
                  <!-- documentId is empty. -->
                  <%
                    docHandler.logDebug("docexch: editDoc.jsp: documentId is empty.");
                  %>
                </core:if>
              </core:cast>
            </dsp:getvalueof>
          </core:if>
        </core:test>
      </core:urlParamValue>
      <%-- 
        i18n messages
      --%>
      <i18n:message id="updateButton" key="updateButton"/>
      <i18n:message id="cancelButton" key="cancelButton"/>
      <i18n:message id="separator" key="labelFieldSeparator"/>
      <i18n:message id="overrideFileNameInstructions" key="overrideFileNameInstructions"/>
      <i18n:message id="overrideFileNameLabel" key="overrideFileNameLabel"/>

      
      <dsp:setvalue bean="DocumentFormHandler.itemDescriptorName" 
           value="<%= dexpage.getItemDescriptorName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.repositoryPathName" 
           value="<%= dexpage.getRepositoryPath() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.fileDataPropertyName" 
           value="<%= dexpage.getFileDataPropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.filenamePropertyName" 
           value="<%= dexpage.getFilenamePropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.fileSizePropertyName" 
           value="<%= dexpage.getFileSizePropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.mimeTypePropertyName" 
           value="<%= dexpage.getMimeTypePropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.authorPropertyName" 
           value="<%= dexpage.getAuthorPropertyName() %>"/>
      
      <dsp:setvalue bean="DocumentFormHandler.repositoryId" 
           paramvalue="documentid"/>
      
      <%-- 
        display errors if any 
      --%>
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
      

      <dsp:form enctype="multipart/form-data" method="post" action="<%= gearEnv.getOriginalRequestURI() %>">
      
        <input type="hidden" name="paf_gear_id" value="<%= gearEnv.getGear().getId()%>"/>
        <input type="hidden" name="paf_community_id" value="<%= gearEnv.getCommunity().getId() %>"/>
        <core:UrlParamValue id="prevpaf_dm" param="prevpaf_dm">
          <input type="hidden" name="prevpaf_dm" value="<%= prevpaf_dm.getValue() %>"/>
        </core:UrlParamValue>
        <input type="hidden" name="paf_dm" value="full"/>
        <input type="hidden" name="dexmode" value="oneItem"/>
        <core:UrlParamValue id="documentId" param="documentid">
          <input type="hidden" name="documentid" value="<%= documentId %>"/>
        </core:UrlParamValue>
        <dsp:input type="hidden" priority="<%=99%>" bean="DocumentFormHandler.repositoryId" paramvalue="documentid"/>
        <dsp:input type="hidden" bean="<%= gearInstanceRefBeanValue %>"
             value="<%= gearEnv.getGear().getId() %>"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.maxFileSizeBytes" 
             value='<%= gearEnv.getGearInstanceParameter("maxFileSize") %>'/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.repositoryPathName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.itemDescriptorName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.filenamePropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.fileDataPropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.fileSizePropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.mimeTypePropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.authorPropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.maxDescriptionLength"
              value="<%= dexpage.getDescriptionMaxLength() %>"/>
      
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParamValue id="prevpaf_dm" param="prevpaf_dm">
             <core:UrlParam param="prevpaf_dm" value="<%= prevpaf_dm %>"/>
           </core:UrlParamValue>
           <core:UrlParam param="paf_dm" value="full"/>
           <core:UrlParam param="paf_gm" value="content"/>
           <core:UrlParam param="dexmode" value="oneItem"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <core:UrlParamValue id="documentId" param="documentid">
             <core:UrlParam param="documentid" value="<%= documentId %>"/>
           </core:UrlParamValue>
           <dsp:input type="hidden" bean="DocumentFormHandler.updateSuccessURL" value="<%= thisFormUrl.getNewUrl() %>"/>
           <dsp:input type="hidden" bean="DocumentFormHandler.cancelURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParamValue id="prevpaf_dm" param="prevpaf_dm">
             <core:UrlParam param="prevpaf_dm" value="<%= prevpaf_dm %>"/>
           </core:UrlParamValue>
           <core:UrlParam param="paf_dm" value="full"/>
           <core:UrlParam param="paf_gm" value="content"/>
           <core:UrlParam param="dexmode" value="edit"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <core:UrlParamValue id="documentId" param="documentid">
             <core:UrlParam param="documentid" value="<%= documentId %>"/>
           </core:UrlParamValue>
	   <dsp:getvalueof id="doctitle" bean="<%= titleBeanValue %>">
	     <paf:encodeUrlParam param="doctitle" value="<%= doctitle %>"/>
           </dsp:getvalueof>
           <dsp:input type="hidden" bean="DocumentFormHandler.updateErrorURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
        
        <TABLE WIDTH="667" BORDER="0" CELLSPACING="0" CELLPADDING="0">
      
             <TR>
               <TD colspan="2" ><img src="<%= clearImgUrl %>" height="3" width="1" border="0"></TD>
              </TR>
              <TR>
                <TD colspan="2">
                  <font class="medium_bold">&nbsp;
                  <dsp:getvalueof id="doctitle" bean="<%= titleBeanValue %>">
                   <i18n:message key="editItemFormTitle">
                      <i18n:messageArg value="<%= doctitle %>"/>
                    </i18n:message>
                  </dsp:getvalueof>
                  </font>
                </TD>
              </TR>
             <TR>
               <TD colspan="2" ><img src="<%= clearImgUrl %>" height="3" width="1" border="0"></TD>
              </TR>
              <TR>
                <TD colspan=2 bgcolor='<%= lineColor %>"><img src="<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"></TD>
              </TR>
              <TR>
                <TD colspan=2><img src="<%= clearImgUrl %>" height=10 width=1 border=0></TD>
              </TR>
      
              <TR VALIGN="top" ALIGN="left"> 
                <TD WIDTH="25%" align="right"><font class="small"><i18n:message key="titleColumnHeader"/><%= separator %>&nbsp;&nbsp;</font></TD>
                <TD WIDTH="75%"><dsp:input type="text" size="35" maxlength="<%= dexpage.getTitleMaxLength() %>" priority="<%=50%>"
                bean="<%= titleBeanValue %>"/></TD>
              </TR>
              <TR>
                <TD colspan=2 ><img src="<%= clearImgUrl %>" height=4 width=1 border=0></TD>
              </TR>
                
              <TR VALIGN="top" ALIGN="left"> 
                <TD align="right"><font class="small"><i18n:message key="descriptionColumnHeader"/><%= separator %>&nbsp;&nbsp;</font></TD>
                <TD><dsp:textarea cols="35" rows="4" priority="<%=50%>"
                   bean="<%= descriptionBeanValue %>" wrap="true"></dsp:textarea></TD>
              </TR>
              <TR>
                <TD colspan=2 ><img src="<%= clearImgUrl %>" height=4 width=1 border=0></TD>
              </TR>
              <TR>
                <TD colspan=2 ><img src="<%= clearImgUrl %>" height=4 width=1 border=0></TD>
              </TR>      
              <TR>
                <TD colspan=2 ><img src="<%= clearImgUrl %>" height=4 width=1 border=0></TD>
              </TR>
              
              <TR VALIGN="top" ALIGN="left"> 
                <TD align="right"><font class="small"><i18n:message key="currentAttachmentLabel"/>&nbsp;&nbsp;</font></TD>
                <TD>
                  <font class="small"><dsp:valueof bean="<%= filenameBeanValue %>"><i18n:message key="untitled"/></dsp:valueof></font>
                </TD>
              </TR>
              
              <TR>
                <TD colspan=2 ><img src="<%= clearImgUrl %>" height=4 width=1 border=0></TD>
              </TR>
              <TR VALIGN="top" ALIGN="left" > 
      
                <TD align="right"><font class="small"><i18n:message key="changeAttachmentLabel"/>&nbsp;&nbsp;</font><BR>
                </TD>
                <TD><font class="medium"><dsp:input type="file" bean="DocumentFormHandler.uploadedFile"/></font></TD>
              </TR>
              <TR>
                <TD><img src="<%= clearImgUrl %>" height=10 width=1 border=0></TD>
                <TD><font class="small">
                   <i18n:message key="maxFileSizeStatement">
                     <i18n:messageArg value="<%= dexpage.getMaxFileSize() %>"/>
                   </i18n:message>
                </font></TD>
              </TR>
              
              <TR>
                <TD colspan=2 ><img src="<%= clearImgUrl %>" height=10 width=1 border=0></TD>
              </TR>

              <TR VALIGN="top" ALIGN="left" >
                <TD align="right"><font class="small"><%= overrideFileNameLabel %>&nbsp;&nbsp;</font><BR>
                </TD>
                <TD><dsp:input type="text" size="35" bean="DocumentFormHandler.overrideFileName"/></TD>
              </TR>
              <TR>
                <TD><img src="<%= clearImgUrl %>" height=10 width=1 border=0></TD>
                <TD><font class="smaller"><%= overrideFileNameInstructions %></font></TD>
              </TR>

              <TR>
                <TD colspan=2 ><img src="<%= clearImgUrl %>" height=10 width=1 border=0></TD>
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
                <TD colspan=2><img src="<%= clearImgUrl %>" height=10 width=1 border=0></TD>
              </TR>
        </TABLE>
      
       </dsp:form>
     </dex:isDocInGear>
     </dsp:getvalueof>
   </dsp:oparam> <%-- end mayWrite --%>
</dsp:droplet>

</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/editDoc.jsp#2 $$Change: 651448 $--%>

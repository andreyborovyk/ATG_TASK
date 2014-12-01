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
   of request parameter dexmode=upload.  It presents a form
   that the user may fill out to create a new document.
--%>
<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= gearEnv %>">

<dsp:importbean bean="/atg/portal/gear/docexch/PermissionsDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/portal/gear/docexch/DocumentFormHandler"/>
<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />

<%-- check write permission before presenting a form --%>
<dsp:droplet name="PermissionsDroplet">
   <dsp:param name="pafEnv" value="<%= gearEnv %>"/>

   <dsp:oparam name="mayNotWrite">
     <font class="error"><blockquote><br>
       <i18n:message key="generalPermissionDeniedMsg"/>
     </blockquote></font>
   </dsp:oparam>

   <dsp:oparam name="mayWrite">

      <i18n:message id="maxFileSizeStatement" key="maxFileSizeStatement">
        <i18n:messageArg value="<%= dexpage.getMaxFileSize() %>"/>
      </i18n:message>
      <i18n:message id="attachmentLabel" key="attachmentLabel"/>
      <i18n:message id="requiredLabel" key="requiredLabel"/>
      <i18n:message id="titleColumnHeader" key="titleColumnHeader"/>
      <i18n:message id="descriptionColumnHeader" key="descriptionColumnHeader"/>
      <i18n:message id="newItemLinkText" key="newItemLinkText"/>
      <i18n:message id="separator" key="labelFieldSeparator"/>
      <i18n:message id="createButton" key="createButton"/>
      <i18n:message id="cancelButton" key="cancelButton"/>
      <i18n:message id="overrideFileNameInstructions" key="overrideFileNameInstructions"/>
      <i18n:message id="overrideFileNameLabel" key="overrideFileNameLabel"/>

      <!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="/atg/portal/gear/docexch/DocumentFormHandler.formError" />
         <dsp:oparam name="true">
            <br><br>
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

      <!-- set properties in the form handler -->
      <dsp:setvalue bean="DocumentFormHandler.itemDescriptorName"
           value="<%= dexpage.getItemDescriptorName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.repositoryPathName"
           value="<%= dexpage.getRepositoryPath() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.filenamePropertyName"
           value="<%= dexpage.getFilenamePropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.fileDataPropertyName"
           value="<%= dexpage.getFileDataPropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.fileSizePropertyName"
           value="<%= dexpage.getFileSizePropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.mimeTypePropertyName"
           value="<%= dexpage.getMimeTypePropertyName() %>"/>
      <dsp:setvalue bean="DocumentFormHandler.authorPropertyName"
           value="<%= dexpage.getAuthorPropertyName() %>"/>

      <%
        // set up some variables that we will need later for form fields
        String docValue = "DocumentFormHandler.value.";
        String titleBeanValue = docValue + dexpage.getTitlePropertyName();
        String descriptionBeanValue = docValue + dexpage.getDescriptionPropertyName();
        String gearInstanceRefBeanValue = docValue + dexpage.getGearIdPropertyName();
      %>
      <!-- begin form -->
      <dsp:form enctype="multipart/form-data" method="post" action="<%= gearEnv.getOriginalRequestURI() %>">

        <input type="hidden" name="prevpaf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
        <input type="hidden" name="paf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
        <input type="hidden" name="dexmode" value="list"/>
        <input type="hidden" name="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <input type="hidden" name="paf_community_id" value="<%= gearEnv.getCommunity().getId() %>"/>
        <dsp:input type="hidden" bean="<%= gearInstanceRefBeanValue %>"
             value="<%= gearEnv.getGear().getId() %>"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.repositoryPathName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.itemDescriptorName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.filenamePropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.fileDataPropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.fileSizePropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.mimeTypePropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.authorPropertyName"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.maxFileSizeBytes"
             value="<%= dexpage.getMaxFileSize() %>"/>
        <dsp:input type="hidden" priority="<%=100%>" bean="DocumentFormHandler.maxDescriptionLength"
             value="<%= dexpage.getDescriptionMaxLength() %>"/>

        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="prevpaf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
           <core:UrlParam param="paf_dm" value="full"/>
           <core:UrlParam param="paf_gm" value="content"/>
           <core:UrlParam param="dexmode" value="oneItem"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <dsp:input type="hidden" bean="DocumentFormHandler.createSuccessURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="prevpaf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
           <core:UrlParam param="paf_dm" value="full"/>
           <core:UrlParam param="paf_gm" value="content"/>
           <core:UrlParam param="dexmode" value="upload"/>
           <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
           <dsp:input type="hidden" bean="DocumentFormHandler.createErrorURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>
        <core:CreateUrl id="thisFormUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="paf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
           <core:ifNot value='<%= "shared".equals(atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm")) %>'>
             <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
             <core:UrlParam param="dexmode" value="list"/>
	   </core:ifNot>  
           <dsp:input type="hidden" bean="DocumentFormHandler.cancelURL" value="<%= thisFormUrl.getNewUrl() %>"/>
        </core:CreateUrl>

        <BR>
        <TABLE WIDTH="667" BORDER="0" CELLSPACING="0" CELLPADDING="2" ALIGN=CENTER>
          <TR>
            <TD colspan=2 align="center"><font class="large_bold"><%= newItemLinkText %></font></TD>
          </TR>
          <TR>
            <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=15 width=1 border=0></TD>
          </TR>
          <TR VALIGN="top" ALIGN="left">
            <TD WIDTH="25%" align="right"><font class="small"><%= titleColumnHeader %><%= separator %>&nbsp;&nbsp;</font></TD>
            <TD WIDTH="75%"><dsp:input type="text" size="35" maxlength="<%= dexpage.getTitleMaxLength() %>"
                                 bean="<%= titleBeanValue %>" /></TD>
          </TR>
          <TR>
            <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
          </TR>
          <TR VALIGN="top" ALIGN="left">
            <TD align="right"><font class="small"><%= descriptionColumnHeader %><%= separator %>&nbsp;&nbsp;</font></TD>
            <TD><dsp:textarea cols="35" rows="4"
                       bean="<%= descriptionBeanValue %>" wrap="true"></dsp:textarea></TD>
          </TR>
          <TR>
            <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
          </TR>
          <TR VALIGN="top" ALIGN="left" >
            <TD align="right"><font class="small"><%= attachmentLabel %><core:if value="<%= dexpage.getAttachmentRequired() %>"><font class="error"><%= requiredLabel %></font></core:if>&nbsp;&nbsp;</font><BR>
            </TD>
            <TD><dsp:input type="file" bean="DocumentFormHandler.uploadedFile"/></TD>
          </TR>
          <TR>
            <TD><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
            <TD><font class="smaller"><%= maxFileSizeStatement %></font></TD>
          </TR>
          <TR>
            <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
          </TR>
          <TR VALIGN="top" ALIGN="left" >
            <TD align="right"><font class="small"><%= overrideFileNameLabel %>&nbsp;&nbsp;</font><BR>
            </TD>
            <TD><dsp:input type="text" size="35" bean="DocumentFormHandler.overrideFileName"/></TD>
          </TR>
          <TR>
            <TD><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
            <TD><font class="smaller"><%= overrideFileNameInstructions %></font></TD>
          </TR>
           <TR>
            <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
          </TR>
          <TR VALIGN="top" ALIGN="left">
            <TD>&nbsp;</TD>
            <TD>
               <dsp:input type="submit" value="<%= createButton %>" bean="DocumentFormHandler.create"/>
               &nbsp;&nbsp;&nbsp;
               <dsp:input type="submit" value="<%= cancelButton %>" bean="DocumentFormHandler.cancel"/>
            </TD>
          </TR>
          <TR>
            <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=20 width=1 border=0></TD>
          </TR>
        </TABLE>

      </dsp:form>

   </dsp:oparam> <%-- end mayWrite --%>
</dsp:droplet>

</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/createDoc.jsp#2 $$Change: 651448 $--%>

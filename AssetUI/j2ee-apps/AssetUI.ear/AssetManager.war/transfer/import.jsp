<%--
  
  The first page of the import dialog.  This page uploads the file and then moves on to the importValidate.jsp

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/import.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>

  <c:set var="importFormHandlerPath" value="/atg/web/assetmanager/transfer/ImportFormHandler"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="importFormHandler"
                    bean="${importFormHandlerPath}"/>

  <c:url var="thisURL" value="/transfer/import.jsp"/>
  <c:url var="errorURL" value="/transfer/importerror.jsp"/>
  <c:url var="initialUploadURL" value="/transfer/initialupload.jsp"/>
  <c:url var="validateURL" value="/transfer/importValidate.jsp"/>

  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
        <fmt:message key="common.pageTitle"/>
      </title>
      <dspel:include page="/components/head.jsp"/>
    </head>

<script type="text/javascript">

</script>


    <body id="importBody">

      <div id="importContainer">

        <%--  import form. --%>
        <dspel:form id="importForm" enctype="multipart/form-data" name="importForm" action="${validateURL}" method="post">

          <dspel:input type="hidden" bean="${importFormHandlerPath}.importValidateSuccessUrl"
                       value="${validateURL}"/>
          <dspel:input type="hidden" bean="${importFormHandlerPath}.importValidateErrorURL"
                       value="${errorURL}"/>

          <%-- import body --%>
          <div class="importContainer" id="middle">
            
           <fmt:message key='transfer.importDialog.uploadLabel'/><br>
           <label for="uploadedFile"><fmt:message key='transfer.importDialog.fileLabel'/></label> <dspel:input type="file" bean="${importFormHandlerPath}.uploadedFile" id="uploadedFile" name="uploadedFile" /> 

           



          <div class="importActions">

            <dspel:input id="uploadFile" type="submit" style="display:none"
                       bean="${importFormHandlerPath}.uploadFile" value="UploadFile" priority="-10"/>
          
            <dspel:a iclass="buttonSmall" href="javascript:document.importForm.uploadFile.click();dojo.byId('screen').style.display='block'">
              <fmt:message key="transfer.importDialog.uploadFile"/>
            </dspel:a>

            <dspel:a iclass="buttonSmall" href="javascript:parent.hideImportDialog()">
              <fmt:message key="common.cancel"/>
            </dspel:a>


            <%-- If get rid of iframes, use code below.
            <dspel:a iclass="buttonSmall go"
                     href="javascript:submitDojoForm(dojo.byId('uploadFile'),dojo.byId('importIframe'), true)">
              <fmt:message key="common.ok"/>
            </dspel:a>
          
            <dspel:a iclass="buttonSmall" href="javascript:parent.hideImportDialog()">
              <fmt:message key="common.cancel"/>
            </dspel:a>
            --%>

          </div>
          </div>
        </dspel:form>

      </div>
      
      <div id="screen" style="display: none;"></div>

    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/import.jsp#2 $$Change: 651448 $ --%>

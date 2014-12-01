<%--
  
  The first page of the import dialog.  This page uploads the file and then moves on to the importValidate.jsp

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/importerror.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>

  <c:set var="importFormHandlerPath" value="/atg/web/assetmanager/transfer/ImportFormHandler"/>
  <c:set var="importAssetPath" value="/atg/web/assetmanager/transfer/ImportAsset"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="importFormHandler"
                    bean="${importFormHandlerPath}"/>
  <dspel:importbean var="importAsset"
                    bean="${importAssetPath}"/>

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

      <%-- Display errors--%>

 
      <div id="importContainer">
         <div class="errorMessage">
         <fmt:message key="error.import.errorTitle"/>

          <br>

          <fmt:message key="error.import.errorIntro">
            <fmt:param value="${importAsset.fileName}"/>
          </fmt:message>

          <br>

         <c:if test="${not empty importFormHandler.formExceptions}">
           <c:forEach items="${importFormHandler.formExceptions}" var="exc">
            <c:out value="${exc.message}"/>
           </c:forEach>
         </c:if>

         <div class="importBottom">

           <%-- Close button --%>
           <dspel:a iclass="buttonSmall" href="javascript:parent.hideImportDialog()">
             <fmt:message key="common.ok.title"/>
           </dspel:a>
         </div>
       </div>
       </div>





    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/importerror.jsp#2 $$Change: 651448 $ --%>

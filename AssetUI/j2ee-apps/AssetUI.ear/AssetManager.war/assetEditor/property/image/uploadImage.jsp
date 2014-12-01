<%--
  Custom property editor for image data.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <table border="0" cellpadding="0" cellspacing="3">
    <tr>

      <td>
        <c:set var="propView" value="${view.propertyMappings['url']}"/>
        <dspel:getvalueof id="url" bean="${propView.formHandlerProperty}" />
        <dspel:input type="file" bean="${formHandlerPath}.uploadedFile" id="uploadedFile" onpropertychange="formFieldModified()" oninput="markAssetModified()" onchange="markAssetModified()" /> 
      </td>

    </tr>
    <tr>
     <td>
        <c:if test="${!empty url}">
          <web-ui:invoke var="hasValidExtension"
                           componentPath="/atg/web/assetmanager/editor/IsValidMediaName"
                           method="checkValidFileName">
             <web-ui:parameter value="${url}" type="java.lang.String"/>
          </web-ui:invoke>

          <fmt:message var="altMessage" key="imageUploadEditor.altMessage">
            <fmt:param value="${url}"/>
          </fmt:message>
          <c:if test="${! hasValidExtension}">
            <fmt:message var="altMessage" key="imageUploadEditor.extensionErrorMessage"/>
          </c:if>
          <img src='<c:out value="${url}"/>' alt='<c:out value="${altMessage}"/>' />
        </c:if>
     </td>
    </tr>
  </table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/image/uploadImage.jsp#2 $$Change: 651448 $--%>

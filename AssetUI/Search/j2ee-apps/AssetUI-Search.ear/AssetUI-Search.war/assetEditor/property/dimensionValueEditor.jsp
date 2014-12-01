<%--
  Default property editor for enumerated values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/dimensionValueEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"               %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:include page="updateBaseSearchConfigLocaleFilter.jsp"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Get an ID for the property's input element --%>  
  <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  
  <c:set var="onchange" value="${inputId}_onchange"/>  

  <%-- Get the current property value --%>
  <dspel:getvalueof var="propVal" bean="${propertyView.formHandlerProperty}"/>

  <c:choose>
    <c:when test="${not empty requestScope.atgCurrentParentAsset}">
      <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${requestScope.atgCurrentAsset.asset}" parentItem="${requestScope.atgCurrentParentAsset.asset}"/>
    </c:when>
    <c:otherwise>
      <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${requestScope.atgCurrentAsset.asset}"/>
    </c:otherwise>
  </c:choose>

  <c:if test="${dimensionValueChoicesResult != null}">
    <%-- <td> supplied by propertyContainer.jsp --%>
      <%-- Display the parent dimension service name --%>
      <div class="formLabel">
        <c:out value="${dimensionValueChoicesResult.searchDimensionDisplayName}"/>:
      </div>
    </td>
    <td>
      <script type="text/javascript">
        function <c:out value="${onchange}"/>() {
          markAssetModified();
          <c:if test="${'language' == dimensionValueChoicesResult.searchDimensionName}">
          var el = document.getElementById('<c:out value="${inputId}"/>');
          issueRequest('/AssetUI-Search/assetEditor/property/updateBaseSearchConfigLocaleFilter.jsp?language='+el.options[el.selectedIndex].value, null, null, null, true);
          </c:if>
        }
      </script>

      <c:choose>
        <c:when test="${empty dimensionValueChoicesResult.readOnlyValue}">
          <%-- Display the property value in a select box --%>
          <dspel:select id="${inputId}"
                        bean="${propertyView.formHandlerProperty}"
                        onchange="${onchange}()"
                        converter="nullable">

            <%-- add All Others --%>    
             <c:if test="${dimensionValueChoicesResult.showAllOthers}">
               <dspel:option value="null">
                 <fmt:message key="dimensionValueEditor.allOthers"/>
               </dspel:option>
             </c:if>

             <%-- Iterate over each of the possible values --%>
             <c:forEach var="key" items="${dimensionValueChoicesResult.dimensionValueChoiceKeys}" varStatus="i">
 
               <c:set var="selected" value="${propVal == key}"/>
 
              <dspel:option value="${key}" selected="${selected}">
                <c:out value="${dimensionValueChoicesResult.dimensionValueChoiceDisplayNames[i.index]}"/>
              </dspel:option>

            </c:forEach>
          </dspel:select>  
        </c:when>
        <c:otherwise>
          <c:out value="${dimensionValueChoicesResult.readOnlyValue}"/>
          <dspel:input type="hidden" 
                       id="${inputId}" 
                       bean="${propertyView.formHandlerProperty}"
                       value="${dimensionValueChoicesResult.readOnlyKey}"/>
        </c:otherwise>
      </c:choose>
    <%-- </td> supplied by propertyContainer.jsp --%>
  </c:if>            

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/dimensionValueEditor.jsp#2 $$Change: 651448 $--%>


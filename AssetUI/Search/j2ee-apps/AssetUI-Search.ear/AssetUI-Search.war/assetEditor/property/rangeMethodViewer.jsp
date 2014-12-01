<%--
  component property editor for property value ranks

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/rangeMethodViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="valueType" bean="${requestScope.formHandlerPath}.value.availableRankingProperty.valueType"/>
  <dspel:getvalueof var="dataType" bean="${requestScope.formHandlerPath}.value.dataType"/>

  <c:if test="${valueType == 'range'}">
 
    <dspel:getvalueof var="rangeMethod" bean="${requestScope.formHandlerPath}.value.rangeMethod"/>       

    <c:set var="lowHighStyle" value="none"/>
    <c:if test="${rangeMethod == 'low-to-high'}">
      <c:set var="lowHighStyle" value="block"/>
    </c:if>
    <c:set var="highLowStyle" value="none"/>
    <c:if test="${rangeMethod == 'high-to-low'}">
      <c:set var="highLowStyle" value="block"/>
    </c:if>

    <table>
      <tr>
        <td colspan="2">
          <dspel:input type="radio" disabled="true"
                       bean="${requestScope.formHandlerPath}.value.rangeMethod" value="low-to-high"/>
          <c:choose>
            <c:when test="${dataType == 'date'}">
              <fmt:message key='rangeMethodEditor.dateLowToHighRangeMethodText'/>
            </c:when>
            <c:otherwise>
              <fmt:message key='rangeMethodEditor.numericLowToHighRangeMethodText'/>
            </c:otherwise>
          </c:choose>
        </td>     
      </tr>

      <tr>
        <td></td>
        <td>
          <span id="ltoh" style="display:<c:out value='${lowHighStyle}'/>" >
            <c:choose>
              <c:when test="${dataType == 'date'}">
                <fmt:message key='rangeMethodEditor.dateMinLabel'/>
              </c:when>
              <c:otherwise>
                <fmt:message key='rangeMethodEditor.numericMinLabel'/>
              </c:otherwise>
            </c:choose>
            <dspel:input type="text" disabled="true"
              bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.lowToHighMin" size="10"
              onchange="markAssetModified()"/>
            <c:choose>
              <c:when test="${dataType == 'date'}">
                <fmt:message key='rangeMethodEditor.dateMaxLabel'/>
              </c:when>
              <c:otherwise>
                <fmt:message key='rangeMethodEditor.numericMaxLabel'/>
              </c:otherwise>
            </c:choose>
            <dspel:input type="text" disabled="true" 
              bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.lowToHighMax" size="10"
              onchange="markAssetModified()"/>
	   </span>
         </td>
      </tr>

      <tr>
        <td colspan="2">
	  <dspel:input type="radio" disabled="true" 
                       bean="${requestScope.formHandlerPath}.value.rangeMethod" value="high-to-low"/>
          <c:choose>
            <c:when test="${dataType == 'date'}">
              <fmt:message key='rangeMethodEditor.dateHighToLowRangeMethodText'/>
            </c:when>
            <c:otherwise>
              <fmt:message key='rangeMethodEditor.numericHighToLowRangeMethodText'/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>

      <tr>
        <td></td>
        <td>
          <span id="htol"  style="display:<c:out value='${highLowStyle}'/>" >
            <c:choose>
              <c:when test="${dataType == 'date'}">
                <fmt:message key='rangeMethodEditor.dateMinLabel'/>
              </c:when>
              <c:otherwise>
                <fmt:message key='rangeMethodEditor.numericMinLabel'/>
              </c:otherwise>
            </c:choose>
            <dspel:input type="text" disabled="true" 
              bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.highToLowMin" size="10"
              onchange="markAssetModified()"/>
            <c:choose>
              <c:when test="${dataType == 'date'}">
                <fmt:message key='rangeMethodEditor.dateMaxLabel'/>
              </c:when>
              <c:otherwise>
                <fmt:message key='rangeMethodEditor.numericMaxLabel'/>
              </c:otherwise>
            </c:choose>
            <dspel:input type="text" disabled="true" 
              bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.highToLowMax" size="10"
              onchange="markAssetModified()"/>
	  </span>
        </td>
      </tr>

      <tr>
        <td>
	  <dspel:input type="radio" disabled="true" 
			bean="${requestScope.formHandlerPath}.value.rangeMethod"  value="manual"/>
           <c:choose>
             <c:when test="${dataType == 'date'}">
               <fmt:message key='rangeMethodEditor.dateManualRangeMethodText'/>
             </c:when>
             <c:otherwise>
               <fmt:message key='rangeMethodEditor.numericManualRangeMethodText'/>
             </c:otherwise>
           </c:choose>
        </td>
      </tr>
    </table>

  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/rangeMethodViewer.jsp#2 $$Change: 651448 $--%>

<%--
  Default editor for timestamp elements of collection properties.

  The following request-scoped variables are expected to be set:
  
  @param  mpv  A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramInitialize" param="initialize"/>
  <dspel:getvalueof var="paramValueId"    param="valueId"/>
  
  <c:if test="${not paramInitialize}">

    <%-- Create a text input for the property value --%>
    <td class="formValueCell">
      <input type="text" id="<c:out value='${paramValueId}'/>"
             class="formTextField" value=""/>
    </td>
  
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/timestampComponentEditor.jsp#2 $$Change: 651448 $--%>

<%--
  Page fragment that renders the derived property icon for a single property.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/derivedPropertyMenu.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="overridden" param="overridden"/>
  <dspel:getvalueof var="propEditorDivId" param="propEditorDivId"/>
  <dspel:getvalueof var="inheritedValueDivId" param="inheritedValueDivId"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>  
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="propertyView" value="${requestScope.mpv}"/>  
  <c:set var="propertyName" value="${propertyView.propertyName}"/>

  <%-- The icon type depends on the current state of the derived property --%>
  <c:choose>
    <c:when test="${overridden}">
      <c:set var="iconClassSuffix" value="override"/>
      <c:set var="propAction" value="change"/>
    </c:when>
    <c:otherwise>
      <c:set var="iconClassSuffix" value="inherit"/>
      <c:set var="propAction" value="keep"/>
    </c:otherwise>
  </c:choose>

  <%-- Derive IDs for page elements --%>
  <c:set var="id" value="${requestScope.atgPropertyViewId}"/>
  <c:set var="iconId"  value="icon_${id}"/>
  <c:set var="inputId" value="mode_${id}"/>
  <c:set var="menuId"  value="menuElem_${id}"/>
  <c:set var="menuObj" value="menuObj_${id}"/>
    
  <%-- Create a hidden input that specifies the action for this property.  If
       the user selects an item from the derived property menu, the value of
       this input will be changed by DerivedPropertyMenu.changeMode to indicate
       the selected action. --%>
  <dspel:input type="hidden" id="${inputId}"
               bean="${requestScope.formHandlerPath}.derivedPropertyActions.${propertyName}"
               value="${propAction}"/>

  <%-- Create and initialize the menu object --%>
  <script type="text/javascript">

    var <c:out value="${menuObj}"/> = new atg.assetmanager.DerivedPropertyMenu();

    <c:out value="${menuObj}"/>.inputId             = "<c:out value='${inputId}'/>";
    <c:out value="${menuObj}"/>.iconId              = "<c:out value='${iconId}'/>";
    <c:out value="${menuObj}"/>.menuId              = "<c:out value='${menuId}'/>";
    <c:out value="${menuObj}"/>.propEditorDivId     = "<c:out value='${propEditorDivId}'/>";
    <c:out value="${menuObj}"/>.inheritedValueDivId = "<c:out value='${inheritedValueDivId}'/>";
    dojo.addOnLoad(function() {
      <c:out value="${menuObj}"/>.createMenu();
    });

  </script>

  <%-- Render the icon --%>
  <div class="ataDD">
    <fmt:message var="dropDownTitle" key="assetEditor.derivedProperty.dropDownTitle"/>
    <a id="<c:out value='${iconId}'/>" href="#"
       onclick="displayMenuAtEventTarget('<c:out value="${menuId}"/>', event)"
       class="iconATA_<c:out value='${iconClassSuffix}'/>"
       title="<c:out value='${dropDownTitle}'/>"></a>
  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/derivedPropertyMenu.jsp#2 $$Change: 651448 $--%>

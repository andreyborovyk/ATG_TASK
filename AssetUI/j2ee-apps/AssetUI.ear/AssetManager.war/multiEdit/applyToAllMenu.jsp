<%--
  Page fragment that renders the apply-to-all icon for a single property.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/applyToAllMenu.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="propEditorDivId" param="propEditorDivId"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>  
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <c:set var="propertyView" value="${requestScope.mpv}"/>  
  <c:set var="propertyName" value="${propertyView.propertyName}"/>

  <%-- Obtain from the apply-to-all form handler's property action map the
       currently selected action for this property --%>
  <c:set var="propertyActionPath"
         value="/atg/web/assetmanager/multiedit/ApplyToAllFormHandler.applyType.${propertyName}"/>
  <dspel:getvalueof var="propertyAction" bean="${propertyActionPath}"/>

  <%-- The icon type and the message to be displayed to the right of the icon
       depend on the currently selected action.  If the action is "Change", then
       we don't want to display any message, because we will be displaying the
       actual property editor instead of a message. --%>
  <c:choose>
    <c:when test="${propertyAction eq 'Change'}">
      <c:set var="iconClassSuffix" value="change"/>
      <c:set var="actionDescription" value="${null}"/>
    </c:when>
    <c:when test="${propertyAction eq 'MakeNull'}">
      <c:set var="iconClassSuffix" value="null"/>
      <fmt:message var="actionDescription" key="ataMenu.valuesNullMessage"/>
    </c:when>
    <c:otherwise>
      <c:set var="iconClassSuffix" value="leave"/>
      <fmt:message var="actionDescription" key="ataMenu.keepValuesMessage"/>
      
      <%-- The default action is null, but the form handler wants it to be
           "Keep" for no change, se we manually assign it here. --%>
      <c:set var="propertyAction" value="Keep"/>
    </c:otherwise>
  </c:choose>

  <%-- Derive IDs for page elements --%>
  <c:set var="id" value="${requestScope.atgPropertyViewId}"/>
  <c:set var="iconId"           value="icon_${id}"/>
  <c:set var="inputId"          value="mode_${id}"/>
  <c:set var="descriptionDivId" value="description_${id}"/>
  <c:set var="menuId"           value="menuElem_${id}"/>
  <c:set var="menuObj"          value="menuObj_${id}"/>

  <%-- Create a hidden input that specifies the action for this property.  The
       initial value is the current action.  If the user selects an item from
       the apply-to-all menu, the value of this input will be changed by
       ApplyToAllMenu.changeMode to indicate the selected action.
  --%>
  <dspel:input type="hidden" id="${inputId}"
               bean="${propertyActionPath}" value="${propertyAction}"/>

  <%-- Create and initialize the menu object --%>
  <script type="text/javascript">

    var <c:out value="${menuObj}"/> = new atg.assetmanager.ApplyToAllMenu();

    <c:out value="${menuObj}"/>.name             = "<c:out value='${menuObj}'/>";
    <c:out value="${menuObj}"/>.inputId          = "<c:out value='${inputId}'/>";
    <c:out value="${menuObj}"/>.iconId           = "<c:out value='${iconId}'/>";
    <c:out value="${menuObj}"/>.menuId           = "<c:out value='${menuId}'/>";
    <c:out value="${menuObj}"/>.descriptionDivId = "<c:out value='${descriptionDivId}'/>";
    <c:out value="${menuObj}"/>.propEditorDivId  = "<c:out value='${propEditorDivId}'/>";
    <c:out value="${menuObj}"/>.propertyViewId   = "<c:out value='${propertyView.uniqueId}'/>";
    <c:out value="${menuObj}"/>.propertyRequired   = <c:out value='${propertyView.propertyDescriptor.required}'/>;
    dojo.addOnLoad(function() {
      <c:out value="${menuObj}"/>.createMenu();
    });

  </script>

  <%-- Render the icon --%>
  <div class="ataDD">
    <fmt:message var="selectUpdateActionTitle" key="ataMenu.selectUpdateActionTitle"/>
    <a id="<c:out value='${iconId}'/>" href="#"
       onclick="displayMenuAtEventTarget('<c:out value="${menuId}"/>', event)"
       class="iconATA_<c:out value='${iconClassSuffix}'/>"
       title="<c:out value='${selectUpdateActionTitle}'/>"></a>
  </div>

  <%-- Render the message, if there is one.  Note that we render the div even
       if there is no message, so that it can be filled in by ApplyToAllMenu.changeMode
       if the user selects a menu item that would cause a message to appear.
  --%>
  <div id="<c:out value='${descriptionDivId}'/>" class="noInputMessage">
    <c:if test="${not empty actionDescription}">

      <%-- Set the atgPropertyEditorVisibilityStyle request variable so that
           the container which invoked us (normally propertyContainer.jsp) will
           not attempt to render the property editor. --%>
      <c:set scope="request" var="atgPropertyEditorVisibilityStyle"
             value="display:none"/>

      <%-- Note that clicking the message is equivalent to selecting "Change"
           from the menu.  This makes it much easier to begin editing a
           particular property. --%>
      <em>
        <a href="javascript:<c:out value='${menuObj}'/>.onChange()">
          <c:out value="${actionDescription}"/>
        </a>
      </em>

    </c:if>
  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/multiEdit/applyToAllMenu.jsp#2 $$Change: 651448 $--%>

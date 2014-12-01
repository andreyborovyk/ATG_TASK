<%--
  Default page fragment for rendering a categorized list of properties inside
  an editor tab for a repository item asset.

  @param  categories  A list of PropertyTabFilter.PropertyCategoryDescriptor
                      objects representing a categorized list of properties to
                      be displayed in the tab.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/defaultTabLayout.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"               %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="pCategories" param="categories"/>

  <%-- Get the help-text bundle from an itemViewMapping attribute --%>
  <c:set var="bundleName" value="${requestScope.atgItemViewMapping.attributes.resourceBundle}"/>
  <c:choose>
    <c:when test="${not empty bundleName}">
      <fmt:setBundle var="bundle" basename="${bundleName}"/>
    </c:when>
    <c:otherwise>
      <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
      <fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>
    </c:otherwise>
  </c:choose>

  <%-- Display any text that should go at the top of the page --%>
  <c:if test="${not empty requestScope.atgItemViewMapping.attributes.textAbove}">
    <div class="defaultTabHelpText">
      <p><fmt:message bundle="${bundle}" key="${requestScope.atgItemViewMapping.attributes.textAbove}"/><p>
    </div>
  </c:if>

  <%-- Determine the page fragment to be used as a container for each property
       editor.  The default page path can be overridden using an itemViewMapping
       attribute. --%>
  <c:set var="containerPage" value="${requestScope.atgItemViewMapping.attributes.propertyContainerPage}"/>
  <c:set var="containerPageContext" value="${requestScope.atgItemViewMapping.attributes.propertyContainerPageContext}"/>
  <c:if test="${empty containerPage}">
    <c:set var="containerPage" value="propertyContainer.jsp"/>
  </c:if>

  <%-- Determine if a box is to be rendered around all of the properties in each
       category.  We never show the boxes if we are in multi-edit mode.  The
       boxes can also be omitted by specifying an itemViewMapping attribute. --%>
  <c:set var="showCategoryBox"
         value="${not requestScope.atgIsMultiEditView and
                  empty requestScope.atgItemViewMapping.attributes.hideCategoryBorders}"/>

  <%-- Render each category --%>
  <c:forEach var="category" items="${pCategories}" varStatus="loop">

    <%-- Begin the category box --%>
    <c:if test="${showCategoryBox}">
      <c:choose>
        <c:when test="${loop.count % 2 == 0}">
          <fieldset class="altGroup"><legend><span><c:out value="${category.displayName}"/></span></legend>
        </c:when>
        <c:otherwise>
          <fieldset><legend><span><c:out value="${category.displayName}"/></span></legend>
        </c:otherwise>
      </c:choose>
    </c:if>

    <%-- Render help text for this category, if specified, using an attribute
         whose name is "textAbove<name_of_category>".  e.g. For a category
         containing properties whose category-resource is "categoryBasics",
         you would use the attribute "textAbovecategoryBasics" --%>
    <c:set var="dynamicAttributeKey" value="textAbove${category.categoryId}"/>
    <c:set var="textAboveMessageKey" value="${requestScope.atgItemViewMapping.attributes[dynamicAttributeKey]}"/>
    <c:if test="${not empty textAboveMessageKey}">
      <div class="categoryBoxHelpText"><fmt:message bundle="${bundle}" key="${textAboveMessageKey}"/></div>
    </c:if>

    <%-- Render an editor for each property in the current category. If there
         is an additional column fragment defined for the category, pass that
         information into the property container page. Note that additional
         columns do not get rendered in multi edit mode. --%>
    <c:set var="columnFragmentCategoryName" value="${requestScope.atgItemViewMapping.attributes.additionalColumnFragmentCategoryName}"/>
    <c:set var="columnFragment" value="${requestScope.atgItemViewMapping.attributes.additionalColumnFragment}"/>
    <c:set var="columnFragmentContext" value="${requestScope.atgItemViewMapping.attributes.additionalColumnFragmentContext}"/>

    <table class="formTable">
      <c:forEach var="prop" items="${category.properties}" varStatus="categoryPropertiesLoop">
        <dspel:include otherContext="${containerPageContext}" page="${containerPage}">
          <dspel:param name="prop" value="${prop}"/>

          <c:if test="${category.categoryId == columnFragmentCategoryName &&
                        not empty columnFragment &&
                        not requestScope.atgIsMultiEditView}">
            <web-ui:collectionPropertySize collection="${category.properties}" var="numRowsToSpan"/>
            <c:if test="${categoryPropertiesLoop.first}">
              <dspel:param name="columnFragment" value="${columnFragment}"/>
              <dspel:param name="columnFragmentContext" value="${columnFragmentContext}"/>
              <dspel:param name="numRowsToSpan" value="${numRowsToSpan}"/>
            </c:if>
          </c:if>

        </dspel:include>
      </c:forEach>
    </table>

    <%-- An itemViewMapping attribute can be used for specifying an additional
         page fragment that is to be displayed at the bottom of the final
         category box.  Note that this is only supported in single-edit mode. --%>
    <c:if test="${loop.last and
                  not requestScope.atgIsMultiEditView and
                  not empty requestScope.atgItemViewMapping.attributes.additionalFragment}">
      <dspel:include otherContext="${requestScope.atgItemViewMapping.attributes.additionalFragmentContext}"
                     page="${requestScope.atgItemViewMapping.attributes.additionalFragment}"/>
    </c:if>

    <%-- Close the category box --%>
    <c:if test="${showCategoryBox}">
      </fieldset>
    </c:if>

  </c:forEach>

  <%-- An itemViewMapping attribute can be used for specifying an additional
       page fragment that is to be displayed at the bottom of the page.  Note
       that this is only supported in single-edit mode. --%>
  <c:if test="${not requestScope.atgIsMultiEditView and
                not empty requestScope.atgItemViewMapping.attributes.finalFragment}">

    <%-- If requested, surround the fragment with a box with an optional label --%>
    <c:set var="showCategoryBox"
           value="${not empty requestScope.atgItemViewMapping.attributes.finalFragmentBox}"/>
    <c:if test="${showCategoryBox}">
      <fieldset class="altGroup">
      <c:if test="${not empty requestScope.atgItemViewMapping.attributes.finalFragmentLabel}">
        <legend>
          <span><fmt:message bundle="${bundle}" key="${requestScope.atgItemViewMapping.attributes.finalFragmentLabel}"/></span>
        </legend>
      </c:if>
    </c:if>

    <dspel:include otherContext="${requestScope.atgItemViewMapping.attributes.finalFragmentContext}"
                   page="${requestScope.atgItemViewMapping.attributes.finalFragment}"/>

    <%-- Close the box, if present --%>
    <c:if test="${showCategoryBox}">
      </fieldset>
    </c:if>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/defaultTabLayout.jsp#2 $$Change: 651448 $--%>

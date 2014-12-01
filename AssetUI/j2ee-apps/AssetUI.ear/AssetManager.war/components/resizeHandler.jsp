<%--
  This page fragment allows a DOM element that lives inside assetManager.jsp to
  have its height modified when the browser window's height is changed.

  Parameters:
    @param  resizableElementId       The ID of the element to be resized
    @param  extraVerticalElementIds  A comma-separated list of IDs of elements
                                     whose heights are to be deducted from the
                                     calculated height of the resizable element
    @param  heightKey                The key in the SessionInfo.dimensions map
                                     that contains the current height of the
                                     resizable element

    @return  May append to a request-scoped variable named "resizableStyle",
             in order to specify a pre-calculated height for the element given
             by resizableElementId

  Example of use:
    <c:set scope="request" var="resizableStyle" value="border: none;"/>
    <dspel:include page="/components/resizeHandler.jsp">
      <dspel:param name="resizableElementId"      value="scrollContainer"/>
      <dspel:param name="extraVerticalElementIds" value="scrollFooter"/>
      <dspel:param name="heightKey"               value="searchResultsScrollContainer"/>
    </dspel:include>
    <dspel:iframe id="scrollContainer" src="${listURL}" style="${requestScope.resizableStyle}"/>

  The SessionInfo component includes an map of DOM element heights.  Whenever a
  new window height is specified on the client, any visible, resizable elements
  must be resized, and the element height map must be repopulated.  Anytime a
  resizable element is displayed, if its height is stored in the map, it is
  rendered using that height.  Otherwise, the element height must be calculated,
  set, and stored in the map.

  This page fragment does two things:

  1) It registers a JavaScript function that is invoked when the window is
  resized, or when the DOM element is initially displayed.  This function
  sets the DOM element's height according to the current window height.
  The element's height is adjusted so that the element occupies all of the
  remaining vertical window space, starting at its current origin, but deducting
  the heights of any elements that appear below the element (these must be
  specified using the extraVerticalElementIds parameter).

  2) It either stores the previously calculated height of the DOM element in
  ${requestScope.resizableStyle}, so that it can be applied when rendering the
  DOM element, or, if the height is unknown, it sets the window.needInitialSize
  variable, which tells assetManager.jsp's onload function to invoke the resize
  function.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/resizeHandler.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <%-- Unpack all DSP parameters --%>
  <dspel:getvalueof var="paramResizableElementId"      param="resizableElementId"/>
  <dspel:getvalueof var="paramExtraVerticalElementIds" param="extraVerticalElementIds"/>
  <dspel:getvalueof var="paramHeightKey"               param="heightKey"/>

  <dspel:importbean var="sessionInfo" bean="/atg/web/assetmanager/SessionInfo"/>

  <script type="text/javascript">
    window.resizeHandler.addResizableElement("<c:out value='${paramHeightKey}'/>", "<c:out value='${paramResizableElementId}'/>", "<c:out value='${paramExtraVerticalElementIds}'/>");
  </script>

  <c:set var="initialHeight" value="${sessionInfo.dimensions[paramHeightKey]}"/>

  <c:choose>
    <c:when test="${not empty initialHeight}">
      <c:set scope="request" var="resizableStyle" value="${requestScope.resizableStyle} height: ${initialHeight}px;"/>
    </c:when>
    <c:otherwise>
      <script type="text/javascript">
        window.resizeHandler.needInitialSize = true;
      </script>
    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/resizeHandler.jsp#2 $$Change: 651448 $--%>

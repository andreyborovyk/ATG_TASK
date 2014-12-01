<%--
  Viewer for image properties in diff/merge mode
  @param  mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
    
  <%-- Display the image.  Note that the image URL comes from a different
       property.  NB: TBD: Use an attribute to determine the name of the
       other property. --%>
  <dspel:tomap var="item" value="${propertyView.view.itemMapping.item}"/>
  <c:if test="${not empty item.url}">
    <dspel:img src="${item.url}"/>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/imageViewer.jsp#2 $$Change: 651448 $--%>

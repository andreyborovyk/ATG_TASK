<%--
  Default file asset read-only view
  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<!-- Begin BIZUI's /views/item/file/view.jsp -->

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
  <div id="nonTableContent">
    <c:forEach items="${view.propertyMappings}" var="mpv">
      <c:set value="${mpv}" var="mpv" scope="request"/>
      <c:if test="${mpv.value.mapped}">
        <c:set value="${mpv.value}" var="mpv" scope="request"/>
        <c:if test="${ !empty mpv.value.uri}">
          <c:out value="<!-- Begin jsp URI ${mpv.value.uri} -->" escapeXml="false"/>
          <dspel:include otherContext="${mpv.value.contextRoot}" page="${mpv.value.uri}"/>
          <c:out value="<!-- End jsp URI ${mpv.value.uri} -->" escapeXml="false"/>
          <br />
        </c:if>
      </c:if>
    </c:forEach>
  </div>
</dspel:page>

<!-- End BIZUI's /views/item/file/view.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/item/file/view.jsp#2 $$Change: 651448 $--%>

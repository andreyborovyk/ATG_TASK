<%--
  Default repository asset edit view
  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<!-- Begin BIZUI's /views/item/gsa/oneColumnEdit.jsp -->

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<dspel:page>
<fmt:bundle basename="atg.epub.ViewResources">
<div id="nonTableContent" style="margin-top: -10px;">

<div id="formKey" style="margin-top: -10px;">
      <p>
      <c:if test="${view.attributes.title}">
        <b><c:out value="${view.attributes.title}" escapeXml="false"/></b><br />
      </c:if>
      <c:if test="${view.attributes.textAbove ne null}">
      <div class="positionBox">
        <p><img src="<c:url context='/atg' value='/images/icon_help.gif'/>" alt=""/>&nbsp;<a class="toggleDetailsOff" id="toggleDetails" href="#" onclick="displayToggle('box', 'box', 'noBox');return false" onfocus="blur(this)" onmouseover="status='';return true;"><fmt:message key="help-text"/></a></p>
        <div class="noBox" id="box">
          <div class="bi">
            <div class="bt">
              <div>
              </div>
            </div>
            <h1><!-- help text title should go here --></h1>
            <div class="details">
              <p><c:out value="${view.attributes.textAbove}" escapeXml="false"/></p>
            </div>
            <div class="bb">
              <div>
              </div>
            </div>
       </div>
        </div>
      </div>
      </c:if>
      </p>
      <table border="0" cellpadding="0" cellspacing="3" style="float: right; margin-left: 45px;">
        <tr>
          <td class="formLabel formLabelRequired formLabelKey">&nbsp;<fmt:message key="required-field"/></td>
        </tr>
      </table>
</div>
  
<dspel:getvalueof var="expert" bean="/atg/userprofiling/Profile.expert"/>

<%-- Group the properties by category --%>
<pws:categorize itemDescriptor="${view.itemMapping.item.itemDescriptor}" expert="${expert}" var="cattedItems"/>
      
<%-- Iterate over each of the properties and render them, handling column switching as needed --%>
<c:set var="inTable" value="false"/>
<c:forEach items="${cattedItems}" var="propertyDescriptor" varStatus="loopStatus">

  <%-- If the item is a category, we need to close the table a display the category header --%>
  <c:choose>
    <c:when test="${propertyDescriptor.class.name == 'atg.epub.pws.taglib.CategoryPropertyDescriptor'}">
      <%-- Only close the previous table if we're already in a category --%>
      <c:if test="${inTable}">
 </table>
        <c:set var="inTable" value="false"/>
      </c:if>
      <h2 class="seperate"><c:out value="${propertyDescriptor.name}"/></h2>
    </c:when>

    <%-- This must be a real property --%>
    <c:otherwise>
      <c:if test="${debug}">
 <c:out value="<!-- Rendering property ${propertyDescriptor.name} -->" escapeXml="false"/>
      </c:if>
 
      <%-- Should we start a new table tag? --%>
      <c:if test="${!inTable}">
 <table cellpadding="0" cellspacing="0" border="0">
 <c:set var="inTable" value="true"/>
      </c:if>

      <%-- include the correct property editor for rendering  --%>
      <c:set var="propView" value="${view.propertyMappings[propertyDescriptor.name]}"/>
      <c:if test="${ !empty propView.uri}">
 <tr><td>
 <c:set value="${propView}" var="mpv" scope="request"/>
        <c:out value="<!-- Begin JSP, context root ${propView.contextRoot}, URI ${propView.uri} -->" escapeXml="false"/>
 <dspel:include otherContext="${propView.contextRoot}" page="${propView.uri}"/>
        <c:out value="<!-- End jsp URI ${propView.uri} -->" escapeXml="false"/>
 </td></tr>
      </c:if>

    </c:otherwise>
  </c:choose>
</c:forEach>

<%-- Close out the table, if necessary --%>
<c:if test="${inTable}">
  </table>
</c:if>

</div>
</fmt:bundle>
</dspel:page>
<!-- End BIZUI's /views/item/gsa/oneColumnEdit.jsp -->
<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/item/gsa/oneColumnEdit.jsp#2 $$Change: 651448 $--%>

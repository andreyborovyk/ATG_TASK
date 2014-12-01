<%--
  List property editor -- applies to properties of any type
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws"          %>

<dspel:page>
<fmt:setBundle var="viewMapBundle" basename="atg.web.viewmapping.ViewMappingResources"/>

<%@ include file="../includes/fieldAttributes.jspf" %>
<%@ include file="../includes/buttonAttributes.jspf" %>

<%-- Bring in the JS methods for dealing with this data type --%>
<dspel:include page="${mpv.componentUri}"/>

<table border="0" cellpadding="0" cellspacing="3" width="100%">
<tr>
  
  <%--
   Determine the input class of this item..
   --%>
  <c:set var="pvClassAttr" value="class=\"formLabel\""/>
  <c:if test="${mpv.propertyDescriptor.required}">
    <c:set var="pvClassAttr" value="class=\"formLabel formLabelRequired\""/>
  </c:if>

  <c:set var="pvIsDisabled" value="false"/>
  <c:if test="${!mpv.propertyDescriptor.writable}">
    <c:set var="pvIsDisabled" value="true"/>
  </c:if>

  <%--
   Display the property view title...
   Use the property display name by default.  Check 'title' attribute
   for override...
   --%>
  <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
  <c:if test="${!empty title}">
    <c:set var="pvTitle" value="${title}"/>
  </c:if>
  <td <c:out value="${pvClassAttr}" escapeXml="false"/> >
    <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
  </td>


  <td>

  <div>

  <%--
   Display any errors that were caught for this property...
   --%>
  <c:if test="${ ! empty formHandler.propertyExceptions[mpv.propertyName]}">
    <span class="error">
      <c:out value="${formHandler.propertyExceptions[mpv.propertyName].message}"/>
    </span>
    <br />
  </c:if>

  <%-- Open paragraph and show the optional lead-in text --%>
  <c:if test="${!empty textAboveField}">
    <c:out value="${textAboveField}" escapeXml="false"/><br />
  </c:if>

  <%-- Render the Collection form --%>
  <%-- The collectionForm_[collectionName] DIV holds the visible form --%>
  <div class="formPadding" id="collectionForm_<c:out value='${mpv.uniqueId}'/>"></div>

  <%-- Is this a list of items?  or of primitives? --%>
  <c:set var="collValueProperty" value="${mpv.formHandlerProperty}"/>
  <c:if test="${!empty mpv.componentRepositoryItemDescriptor}">
    <c:set var="collValueProperty" value="${mpv.formHandlerProperty}.repositoryIds"/>
  </c:if>

  <%-- We also need a place to null out the field --%>
  <c:set var="collNullProperty" value="${mpv.formHandlerProperty}.values"/>

  <%-- DIV to hold all the hidden fields which write data to the form handler --%>
  <%-- Note that we need to start off with one bogus reference to the Collection
       to get the Nucleus machinery going.  This will get removed during the
       next DIV rewrite --%>
  <div id="collectionHidden_<c:out value='${mpv.uniqueId}'/>">
    <dspel:input type="hidden" value="dummy" bean="${collValueProperty}"/>
    <dspel:input type="hidden" value="null" bean="${collNullProperty}"/>
  </div>
        
  </div>

  <%-- Initialize the Collection data --%>
  <script language="JavaScript">
    // Register the Collection...
    var collData = collectionRegister( "<c:out value="${mpv.uniqueId}"/>", "list", "<c:out value="${collValueProperty}"/>", "<c:out value="${collNullProperty}"/>");
    
    // Set some display properties of the collection from attributes of the view mapping
    <c:if test="${!empty deleteButtonText}">
      collData.deleteButtonText = '<c:out value="${deleteButtonText}"/>';
    </c:if>
    <c:if test="${!empty addButtonText}">
      collData.addButtonText = '<c:out value="${addButtonText}"/>';
    </c:if>
    <c:if test="${!empty editItemButtonText}">
      collData.editItemButtonText = '<c:out value="${editItemButtonText}"/>';
    </c:if>
    <c:if test="${mpv.attributes.hideUpDownButtons != null}">
      collData.hideUpDownButtons = '<c:out value="${mpv.attributes.hideUpDownButtons}"/>';
    </c:if>

  </script>

  <%-- Get the base assetURI for future reference, if needed --%>
  <c:if test="${!empty mpv.componentRepositoryItemDescriptor}">
    <c:set var="itemDescriptor" value="${mpv.componentRepositoryItemDescriptor}"/>
    <pws:createVersionManagerURI var="emptyRelatedAssetURI"
      componentName="${itemDescriptor.repository.repositoryName}"
      itemDescriptorName="${itemDescriptor.itemDescriptorName}"
      itemId=""/>
  </c:if>

  <%-- Add any items already within the Collection --%>
  <dspel:getvalueof var="arrayValues" vartype="Object[]" bean="${mpv.formHandlerProperty}"/>
  <c:forEach items="${arrayValues}" var="arrayValue" varStatus="status">
    <c:choose>
      <c:when test="${!empty mpv.componentRepositoryItemDescriptor}">
        <c:set var="displayNameProperty" value="${mpv.componentRepositoryItemDescriptor.itemDisplayNameProperty}"/>
        <c:choose>
          <c:when test="${not empty displayNameProperty}">
            <dspel:getvalueof var="displayNameValue" bean="${mpv.formHandlerProperty}[${status.index}].${displayNameProperty}"/>
          </c:when>
          <c:otherwise>
            <dspel:getvalueof var="displayNameValue" bean="${mpv.formHandlerProperty}.repositoryIds[${status.index}]"/>
          </c:otherwise>
        </c:choose>
        <script language="JavaScript">
          collectionAddItem( "<c:out value="${mpv.uniqueId}"/>", "",
                               "<dspel:valueof bean="${mpv.formHandlerProperty}.repositoryIds[${status.index}]"/>",
                               '<%=java.net.URLEncoder.encode(""+pageContext.findAttribute("displayNameValue"), "UTF-8")%>',
                               '<c:out value="${emptyRelatedAssetURI}"/>' +
                                      '<dspel:valueof bean="${mpv.formHandlerProperty}.repositoryIds[${status.index}]"/>');
        </script>
      </c:when>
      <c:when test="${arrayValue.class.name == 'java.util.Date' || arrayValue.class.name == 'java.sql.Date'}">
        <dspel:getvalueof var="propertyValue" bean="${mpv.formHandlerProperty}.values[${status.index}]"/>
        <c:set var="dateValue" value=""/>
        <c:if test="${not empty propertyValue}">
          <fmt:message var="fieldDateFormat" bundle="${viewMapBundle}" key="date-format-input"/>
          <fmt:formatDate var="dateValue" value="${propertyValue}" pattern="${fieldDateFormat}"/>
        </c:if>
        <script language="JavaScript">
          collectionAddItem( "<c:out value="${mpv.uniqueId}"/>", "",
                                "<c:out value="${dateValue}"/>",
                               "", "");
        </script>
      </c:when>
      <c:otherwise>
        <script language="JavaScript">
          collectionAddItem( "<c:out value="${mpv.uniqueId}"/>", "",
                               "<dspel:valueof bean="${mpv.formHandlerProperty}.values[${status.index}]"/>",
                               "", "");
        </script>
      </c:otherwise>
    </c:choose>
  </c:forEach>

  <%-- Finally, draw the Collection --%>
  <script language="JavaScript">
    collectionDraw( "<c:out value="${mpv.uniqueId}"/>");
    collectionMakeHidden( "<c:out value="${mpv.uniqueId}"/>");
  </script>

  </td>

</tr>
</table>


<%-- Every property editor should declare 'get' and 'set' functions
     so that they work with the "form saver" that enabled form values
     to be retained without having to hit "Save Changes"  --%>
<script language="JavaScript">

  function get_<c:out value="${mpv.uniqueId}"/> ()
  {
    // Make sure we're look at the latest data
    collectionGrabValues( '<c:out value="${mpv.uniqueId}"/>');

    // Encode the data as a '|' delimited list of value=displayName
    // URLEncode the parts so there's no confusion...
    var tmpStr = '';
    var collData = collectionData['<c:out value="${mpv.uniqueId}"/>'];
    for ( var x in collData.items) {
      tmpStr += encodeURIComponent(collData.items[x].value) + '=' +
                encodeURIComponent(collData.items[x].displayName) + '=' +
                encodeURIComponent(collData.items[x].assetURI);
      if ( x != collData.items.length-1) {
        tmpStr += '|';
      }
    }
    return tmpStr;
  }

  function set_<c:out value="${mpv.uniqueId}"/> ( value)
  {
    var pairs = value.split('|');

    // Clear out the collection before load values from the form saver
    collectionClear( '<c:out value="${mpv.uniqueId}"/>');

    // Decode and load!
    for ( var x in pairs) {
      if ( pairs[x] != '') {
        var vdn = pairs[x].split('=');
        var value = decodeURIComponent(vdn[0]);
        var displayName = decodeURIComponent(vdn[1]);
        var assetURI = decodeURIComponent(vdn[2]);

        collectionAddItem( '<c:out value="${mpv.uniqueId}"/>', '', value, displayName, assetURI);
      }
    }

    // Redraw the collection with the new stuff
    collectionMakeHidden( '<c:out value="${mpv.uniqueId}"/>');
    collectionDraw( '<c:out value="${mpv.uniqueId}"/>');
  }

  // Register this property as saveable...
  fsRegisterProperty( '<c:out value="${mpv.uniqueId}"/>');

</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/list/listEdit.jsp#2 $$Change: 651448 $--%>

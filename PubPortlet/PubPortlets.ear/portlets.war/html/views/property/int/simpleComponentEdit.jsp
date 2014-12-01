<%--
  @param   mpv  A request scoped, MappedPropertyView item for this view
  @param   componentProperty
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ page import="java.io.*,java.util.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<%-- Is the field disabled? --%>
<c:set var="pvIsDisabled" value=""/>
<c:if test="${!mpv.propertyDescriptor.writable}">
  <c:set var="pvIsDisabled" value="disabled=\"true\""/>
</c:if>

<%-- Here are the obligatory JS methods to render the field --%>
<script language="JavaScript">

  function collectionDrawItem_<c:out value="${mpv.uniqueId}"/> (fieldId, fieldValue, fieldDisplayName) {
    var tempHTML = '';

    // Render the form field and return it to the collection manager as a string
    tempHTML += '<input type="text" ';
    tempHTML +=        '<c:out value="${pvIsDisabled}"/> ';
    tempHTML +=        'size="<c:out value="${mpv.componentAttributes.inputFieldSize}"/>" ';
    tempHTML +=        'maxlength="<c:out value="${mpv.componentAttributes.inputFieldMaxlength}"/>" '; 
    tempHTML +=        'id="collectionValue_' + fieldId + '" ';
    tempHTML +=        'value="' + fieldValue + '" ';
    tempHTML += '/>';
    tempHTML += '<input type="hidden" id="collectionDisplayName_' + fieldId + '" value="' + fieldDisplayName + '"/>';

    return tempHTML;
  }

</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/int/simpleComponentEdit.jsp#2 $$Change: 651448 $--%>


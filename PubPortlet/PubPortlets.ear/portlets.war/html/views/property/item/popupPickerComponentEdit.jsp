<%--
  Standard editor for repository item properties...  which pops up an IFRAME asset browser

  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>

<script language="JavaScript">

  function collectionDrawItem_<c:out value="${mpv.uniqueId}"/> (fieldId, fieldValue, fieldDisplayName, assetURI, editButtonText) {
    var tempHTML = '';

    // Render the visible data
    tempHTML += '<table cellpadding="0" cellspacing="2"><tr><td>';

    // Add the button to set the item with
    tempHTML += '<input type="button" value="' + editButtonText + '" ';
    tempHTML +=     "onmousedown=\"collectionSetPickerItem('" + fieldId + "'); ";
    tempHTML +=                  "showAssetPicker( '<c:out value="${mpv.uniqueId}"/>_set', ";

    <pws:getMappedRepository var="mappedRepository"
                repositoryName="${mpv.componentRepositoryItemDescriptor.repository.repositoryName}"/>
    tempHTML +=                          "'<c:out value="${mappedRepository.absoluteName}"/>:<c:out value="${mpv.componentRepositoryItemDescriptor.itemDescriptorName}"/>',";
    tempHTML +=                          "'<c:out value="${mpv.componentRepositoryItemDescriptor.beanDescriptor.displayName}"/>',";
    tempHTML +=                          "'<c:out value="${mpv.componentRepositoryItemDescriptor.repository.repositoryName}"/>',";
    tempHTML +=                          "'repository', ";
<c:choose>
  <c:when test="${ ! empty mpv.componentRepositoryItemDescriptor.itemDisplayNameProperty }" >
    tempHTML +=                          "'<c:out value="${mpv.componentRepositoryItemDescriptor.itemDisplayNameProperty}" />', ";
  </c:when>
  <c:otherwise>
    tempHTML +=                          "'', ";
  </c:otherwise>
</c:choose>
    tempHTML +=                          "'true', ";
    tempHTML +=                          "'pick', ";
    tempHTML +=                          "'false', ";
    tempHTML +=                          "'nested', ";
    tempHTML +=                          "'hide', ";
    tempHTML +=                          "'Search', ";
    tempHTML +=                          "'Asset Picker', ";
    tempHTML +=                          "'<c:out value="${assetInfoPath}"/>'";
    tempHTML +=                          ")\"/>";
    tempHTML += '<td>';
    
    // Render the display name in a link to that item
    tempHTML += "<a id='collectionFormDisplay_" + fieldId + "' ";
    tempHTML +=     "href=\"javascript:<c:out value="${mpv.uniqueId}"/>_pushContext('" + fieldId + "','" + assetURI + "');\"/>";
    tempHTML +=     escapeXMLText(decodeURIComponent(fieldDisplayName));
    tempHTML += "</td></a>";
      
    // Add the hidden fields
    tempHTML += '<input type="hidden" id="collectionValue_' + fieldId + '" value="' + fieldValue + '"/>';
    tempHTML += '<input type="hidden" id="collectionDisplayName_' + fieldId + '" value="' + fieldDisplayName + '"/>';
    tempHTML += '<input type="hidden" id="collectionAssetURI_' + fieldId + '" value="' + assetURI + '"/>';
    tempHTML += '</td></tr></table>';
    
    return tempHTML;
  }

  function <c:out value="${mpv.uniqueId}"/>_pushContext( fieldId, assetURI ) {
    collectionSetPickerItem( fieldId);
    postFsOnSubmit = function() { 
      pushContext( assetURI,
        { onSelect:'<c:out value="${mpv.uniqueId}"/>_set' } );
    }
    fsOnSubmit();           
  }
    
  function <c:out value="${mpv.uniqueId}"/>_set( selected, attributes ) {
     var tempHTML = '';
     var fieldId = collectionGetPickerItem();
     var input = document.getElementById( 'collectionValue_' + fieldId);
     input.value = selected.id;

     // Set the displayName field...  we like this to be URI encoded in case of special characters
     input = document.getElementById( 'collectionDisplayName_' + fieldId);
     input.value = encodeURIComponent(selected.displayName);

     input = document.getElementById( 'collectionAssetURI_' + fieldId);
     input.value = selected.uri;

     // Redraw the collection
     var fieldParams = fieldId.split('_');
     collectionGrabValues( fieldParams[0]);
     collectionDraw( fieldParams[0]);
  }

</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/item/popupPickerComponentEdit.jsp#2 $$Change: 651448 $--%>

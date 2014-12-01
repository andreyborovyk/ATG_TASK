<%--
  Standard editor for repository item properties...  which pops up an IFRAME asset browser

  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/item/popupPickerEdit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws"          %>

<dspel:page>
<%@ include file="../includes/fieldAttributes.jspf" %>
<%@ include file="../includes/buttonAttributes.jspf" %>

<table border="0" cellpadding="0" cellspacing="3">
<tr>
	
	<c:catch var="exc">

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

  <%-- A hidden field which maps to the real bean property... --%>
  <dspel:input type="hidden" 
               nullable="${ ! mpv.propertyDescriptor.required }"
               bean="${mpv.formHandlerProperty}.repositoryId"
               id="${mpv.uniqueId}Value"/>

  <%-- Show optional pre-label --%>
  <c:if test="${!empty labelBeforeField}">
    <c:out value="${labelBeforeField}" escapeXml="false"/>&nbsp;
  </c:if>

  <dspel:getvalueof var="assetId"
    bean="${mpv.formHandlerProperty}.repositoryId"/>

  <c:set var="itemDescriptor" value="${mpv.repositoryItemDescriptor}"/>

  <pws:createVersionManagerURI var="emptyRelatedAssetURI"
    componentName="${itemDescriptor.repository.repositoryName}"
    itemDescriptorName="${itemDescriptor.itemDescriptorName}"
    itemId=""/>

  <script type="text/javascript">

    function <c:out value="${mpv.uniqueId}"/>_pushContext() {
      var input = 
        document.getElementById( '<c:out value="${mpv.uniqueId}"/>' + "Value" );
      postFsOnSubmit = function() { 
        pushContext( '<c:out value="${emptyRelatedAssetURI}"/>' + input.value, 
          { onSelect:'<c:out value="${mpv.uniqueId}"/>_set' } );
      }
      fsOnSubmit();        
    }

    // this function is a callback, called by the asset browser
    // when a related asset is chosen by the user
    function <c:out value="${mpv.uniqueId}"/>_set( selected, attributes ) {
      var input = 
        document.getElementById( '<c:out value="${mpv.uniqueId}"/>' + "Value" );
      input.value = selected.id;
      var link = document.getElementById( 
        '<c:out value="${mpv.uniqueId}"/>' + "DisplayName" );     
      
      /*
       * Bug148419
       * We can't simply replace innerHTML here
       *   link.innerHTML = selected.displayName;
       * due to the following IE bug
       *   http://support.microsoft.com/default.aspx/kb/927917
       * instead we need to update a copy of the node and replace the original with the updated copy.
       */
      var tempHtml = selected.displayName;
      var orginalNode = link;
      var clonedNode = orginalNode.cloneNode(true);
      var parentNode = orginalNode.parentNode;

      clonedNode.innerHTML = tempHtml;
      parentNode.replaceChild(clonedNode, orginalNode);
    }

    function <c:out value="${mpv.uniqueId}"/>_pick() {
      // array to contain only those types to be shown in browser
      var allowableTypes = new Array( 0 );

      // Populate the allowableTypes array with the top-level asset type
      // and any subtypes
      <pws:getItemSubTypes var="subtypes"
        repositoryPath="${mpv.repositoryItemDescriptor.repository.absoluteName}" 
        itemType="${mpv.repositoryItemDescriptor.itemDescriptorName}">
        <c:forEach items="${subtypes}" var="subtype">

          var assetType = new Object();
          assetType.typeCode            = 'repository';
          <c:choose>
           <c:when test="${ ! empty subtype.beanDescriptor.displayName }">
            assetType.displayName         = '<c:out value="${subtype.beanDescriptor.displayName}"/>';
           </c:when>
           <c:otherwise>
            assetType.displayName         = '<c:out value="${subtype.itemDescriptorName}"/>';
           </c:otherwise>
          </c:choose>
          assetType.displayNameProperty = '<c:out value="${subtype.itemDisplayNameProperty}"/>';
          assetType.typeName            = '<c:out value="${subtype.itemDescriptorName}"/>';
          assetType.repositoryName      = '<c:out value="${subtype.repository.repositoryName}"/>';

          <pws:getMappedRepository var="mappedRepository" repositoryName="${subtype.repository.repositoryName}"/>
          assetType.componentPath       = '<c:out value="${mappedRepository.absoluteName}"/>';
          assetType.createable = "true"; 
          
          allowableTypes[ allowableTypes.length ] = assetType;
        </c:forEach>
      </pws:getItemSubTypes>

      var picker = new AssetBrowser( '<c:out value="${assetInfoPath}"/>' );
      picker.browserMode        = "pick";
      picker.isAllowMultiSelect = "false";
      picker.createMode         = "nested";
      picker.onSelect           = '<c:out value="${mpv.uniqueId}"/>_set';
      picker.closeAction        = "hide";
      picker.defaultView        = "Search";
      picker.assetTypes         = allowableTypes;
      picker.assetPickerFrameElement = document.getElementById('iFrame');
      picker.assetPickerDivId        = "browser";
      picker.invoke();
    } 

    <c:if test="${ ! mpv.propertyDescriptor.required }">
      <%-- 
         Inserts the word "null" into the hidden dspel:input tag's value
         for empty refernces that can be null.
      --%>
      function <c:out value="${mpv.uniqueId}"/>_checkNull() {
        var input = 
          document.getElementById( '<c:out value="${mpv.uniqueId}"/>' + "Value" );
        if ( input.value == null || input.value.length==0 )
          input.value="null";
      } 
      registerOnLoad( function() { <c:out value="${mpv.uniqueId}"/>_checkNull(); } );
    </c:if>

  </script>

  <%-- Get the name of the property in this item that is the display name --%>
  <c:set var="displayNameProperty"
     value="${mpv.propertyDescriptor.propertyBeanInfo.itemDisplayNameProperty}"/>

  <%-- Show the picker button --%>
  <c:set var="pvEditItemText" value="edit link"/>
  <c:if test="${!empty editItemButtonText}">
    <c:set var="pvEditItemText" value="${editItemButtonText}"/>
  </c:if>
  <input type="button" value="<c:out value="${pvEditItemText}"/>"  class="formElementCustom"
    onmousedown="javascript:<c:out value="${mpv.uniqueId}"/>_pick();"/>

  <%-- Determine how to clear item references --%>
  <c:choose>
    <c:when test="${mpv.propertyDescriptor.required}">
      <c:set var="id" value="" />
    </c:when>
    <c:otherwise>
      <c:set var="id" value="null" />
    </c:otherwise>
  </c:choose>

  <%-- Show the clear button --%>
  <c:set var="pvClearItemText" value="clear link"/>
  <c:if test="${!empty clearItemButtonText}">
    <c:set var="pvClearItemText" value="${clearItemButtonText}"/>
  </c:if>
  <input type="button" value="<c:out value="${pvClearItemText}"/>" class="formElementCustom"
    onmousedown='javascript:<c:out value="${mpv.uniqueId}"/>_set( { id: "<c:out value='${id}'/>", displayName: ""} );'/>

  <a id='<c:out value="${mpv.uniqueId}"/>DisplayName' onmouseover="status='';return true;"
    href="#" onclick="<c:out value="${mpv.uniqueId}"/>_pushContext();return false;"/>
    <dspel:valueof bean="${mpv.formHandlerProperty}.${displayNameProperty}"/>
  </a>

  <%-- Show optional post-label --%>
  <c:if test="${!empty labelAfterField}">
    &nbsp;<c:out value="${labelAfterField}" escapeXml="false"/>
  </c:if>

  <%-- Show option trailing text --%>
  <c:if test="${!empty textBelowField}">
    <br />
    <c:out value="${textBelowField}" escapeXml="false"/>
  </c:if>

  <%-- Close out field --%>
  </td>

  </c:catch>
  <c:if test="${exc != null &&
        (exc.class.name == 'atg.security.RuntimeSecurityException'
         || exc.class.name =='atg.security.SecurityException')}">
    <c:out value="You do not have permission to access the value of this property."/>
  </c:if>

</tr>
</table>



<%-- Every property editor should declare 'get' and 'set' functions
     so that they work with the "form saver" that enabled form values
     to be retained without having to hit "Save Changes"  --%>
<script type="text/javascript">

  function get_<c:out value="${mpv.uniqueId}"/>()
  {
    var fieldID = '<c:out value="${mpv.uniqueId}"/>' + "Value";
    var fieldDN = '<c:out value="${mpv.uniqueId}"/>' + "DisplayName";
    var idVal = document.getElementById(fieldID).value;
    var dnVal = document.getElementById(fieldDN).innerHTML;
    return encodeURIComponent(idVal) + '=' + encodeURIComponent(dnVal);
  }

  function set_<c:out value="${mpv.uniqueId}"/>( value )
  {
    var pair = value.split('=');
    var selected = new Object();
    selected.id = decodeURIComponent(pair[0]);
    selected.displayName = decodeURIComponent(pair[1]);
    <c:out value="${mpv.uniqueId}"/>_set( selected );
  }

  // Register this property as saveable...
  fsRegisterProperty( '<c:out value="${mpv.uniqueId}"/>' );

</script>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/item/popupPickerEdit.jsp#2 $$Change: 651448 $--%>

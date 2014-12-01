<%--
  Browse Picker initial page

  @param apView
    Determine's which page to include in the Asset Picker iframe.
    Will either be repository/asset type selection page, or the
    plugin container page.

  @param assetInfo
    Client path to nucleus AssetInfo context object. 
    Source: client/container

  @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowsePicker.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<!-- Begin assetBrowsePicker.jsp -->

<dspel:page>

<dspel:importbean var="config"
  bean="/atg/assetui/Configuration"/>


  <%@ include file="assetPickerConstants.jsp" %>

  <%-- get BrowseAssetInfo --%>
  <c:choose>
    <c:when test="${ ! empty param.assetInfo }">
      <c:set var="assetInfoPath" value="${param.assetInfo}"/>
    </c:when>
    <c:otherwise>
      <c:set var="assetInfoPath" 
        value="/atg/epub/servlet/BrowseAssetInfo"/>
    </c:otherwise>
  </c:choose>
  <dspel:importbean bean="${assetInfoPath}" var="assetInfo"/>

  <%-- asset component path --%>
  <c:if test="${ ! empty assetInfo.attributes.componentPath}">
    <c:set var="componentPath" value="${assetInfo.attributes.componentPath}"/>
  </c:if>

  <%-- asset item type --%>
  <c:if test="${ ! empty assetInfo.attributes.itemType }">
    <c:set var="itemType" value="${assetInfo.attributes.itemType}"/>
  </c:if>

  <script language="JavaScript">

    var assetTypes;

    //
    // Populate assetTypes object 
    //
    function loadAssetTypes() {

      var assetTypesList = new Array( 0 );

      <pws:getVersionedAssetTypes var="assetTypes">
        <c:forEach items="${assetTypes}" var="result">
          <c:forEach items="${result.types}" var="type">
            <c:choose>
              <c:when test="${result.repository}">
                <c:set var="typeCode" value="repository"/>
              </c:when>
              <c:when test="${result.fileSystem}">
                <c:set var="typeCode" value="file"/>
              </c:when>
            </c:choose>

            var assetType = new Object();
            assetType.typeCode            = '<c:out value="${typeCode}"/>';
            assetType.displayName         = '<c:out value="${type.displayName}"/>';
            assetType.displayNameProperty = 'displayName';
            assetType.typeName            = '<c:out value="${type.typeName}"/>';
            assetType.repositoryName      = '<c:out value="${result.repositoryName}"/>';
            assetType.componentPath       = '<c:out value="${result.componentPath}"/>';
            assetType.createable          = 'true';

            // DL: this may no longer be necessary
            assetType.encodedType = 
              assetType.componentPath + ":" + assetType.typeName;

            <c:if test="${result.repository}">
              <c:if test="${ !empty type.itemDescriptor.itemDisplayNameProperty}">
                  assetType.displayNameProperty = 
                    '<c:out value="${type.itemDescriptor.itemDisplayNameProperty}"/>';
              </c:if>
            </c:if>
            assetTypesList[ assetTypesList.length ] = assetType;
          </c:forEach>
        </c:forEach>
      </pws:getVersionedAssetTypes>

      assetTypes = assetTypesList;
    }

    loadAssetTypes();

    //
    // Helper method to get item display property
    //
    function getItemDisplayProperty( repository, itemType ) {
      var itemDisplayProperty = null;

      for (var i=0; i<assetTypes.length; i++) {
        if ( assetTypes[i].componentPath == repository && 
             assetTypes[i].typeName == itemType )

          itemDisplayProperty = assetTypes[i].displayNameProperty ;
      }
      return itemDisplayProperty;
    }

    //
    // populate drop down menu with asset types
    //
    function loadAssetPicker() {
      var temp = new Array(0);
      var test = "<c:out value='${param.browserMode}'/>";
      var compPath = "<c:out value='${componentPath}'/>";
      
      // populate select dropdowns if assetTypes is > 1
      if ( assetTypes.length > 1 ) {
        document.abSelectForm.selectedRepository.options.length = 0;
        var j = 1;
	var selectedIndex = 0;
        for (var i=0; i < assetTypes.length; i++) {
          if ( temp[ assetTypes[i].componentPath ] != assetTypes[i].componentPath ) {
            document.abSelectForm.selectedRepository.options.length = j;
            document.abSelectForm.selectedRepository.options[j-1] =
              new Option( assetTypes[i].repositoryName );
            document.abSelectForm.selectedRepository.options[j-1].value =
              assetTypes[i].componentPath;
            temp[ assetTypes[i].componentPath ] = assetTypes[i].componentPath;

	    if (assetTypes[i].componentPath==compPath) { 
		selectedIndex=j-1;
	    }

            j++;
          }
        }
        document.abSelectForm.selectedRepository.options.selectedIndex=selectedIndex;

        populateTypePullDown( document.abSelectForm.selectedRepository.value );

      }
      // otherwise, set the componentPath/itemType and go to the
      // search form 
      else if ( assetTypes.length == 1 ){
        
        document.abSelectForm.componentPath.value =
          assetTypes[0].componentPath;
        document.abSelectForm.itemType.value =
          assetTypes[0].typeName;
        if ( getViewConfiguration() != null )
          document.abSelectForm.viewConfiguration.value = 
            getViewConfigurationAsString();
        document.abSelectForm.submit();
      }
    }
  
    //
    // Populate the itemType drop down
    //
    function populateTypePullDown( componentPath ) {
    
      var j = 1;
      var selectedIndex = 0;
      var iType = "<c:out value='${itemType}'/>";

      document.abSelectForm.selectedItemType.options.length = 0;
      
      for ( var i=0; i<assetTypes.length; i++) {
        if (assetTypes[i].componentPath == componentPath) {
          document.abSelectForm.selectedItemType.options.length = j
          document.abSelectForm.selectedItemType.options[j-1] = 
            new Option( assetTypes[i].displayName );
          document.abSelectForm.selectedItemType.options[j-1].value = 
            assetTypes[i].typeName;

	    if (assetTypes[i].typeName==iType) { 
		selectedIndex=j-1;
	    }
          j++;
        }
      }
      document.abSelectForm.selectedItemType.options.selectedIndex=selectedIndex;

    }

  </script>



  <!-- 
        Determine which view to show.  Querystring vars take priority, then check session
  -->
  <c:set var="apView" value="${ param.apView }"/>
  <c:set var="assetView" value="${assetInfo.attributes.assetview}"/>
  <c:set var="assetURI" value="${assetInfo.attributes.asseturi}"/>

  <c:choose>
    <c:when test="${ apView eq AP_CONTAINER }">
      <dspel:include page="${BROWSE_CONTAINER_PAGE}"/>
    </c:when>
    <c:when test="${ apView eq AP_ITEM_TYPE_SELECT }">
      <dspel:include page="${BROWSE_CONTAINER_PAGE}"/>
    </c:when>
    <c:when test="${ assetView eq SEARCH_INITIAL }">
      <dspel:include page="${BROWSE_CONTAINER_PAGE}"/>
    </c:when>
    <c:when test="${ assetView eq SEARCH_RESULTS }">
      <dspel:include page="${BROWSE_CONTAINER_PAGE}"/>
    </c:when>
    <c:otherwise>
      <dspel:include page="${BROWSE_CONTAINER_PAGE}"/>
    </c:otherwise>
  </c:choose>

</dspel:page>

<!-- End assetBrowsePicker.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowsePicker.jsp#2 $$Change: 651448 $--%>

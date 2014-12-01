<%--
  Thie file is used in conjunction with orgAllMembersGrid.jsp to display org's
  all members collections.

  Parameters used during initialization:
  @param initialize     Indicates we are in initialization mode.
  @param editorObj      The name of a Javascript object that can be used to add
                        grid customizations, such as extra columns and behaviors.
  @param allowInsert    Indicates whether insertion of items is allowed.
  @param showIdColumn   Indicates whether an ID column should be displayed.
  @param showIconColumn Indicates whether an Icon column should be displayed.

  Parameters issued via AJAX requests from the grid:
  @param startIndex       The start index of the items to render.
  @param endIndex         The end index of the items to render.
  @param formHandlerPath  The form handler used for the retrieval of current organization
                          and all its members.

  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/grid/itemOrgMembers.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="json"     uri="http://www.atg.com/taglibs/json"                  %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack DSP and other parameters --%>
  <dspel:getvalueof var="paramInitialize"           param="initialize"/>
  <dspel:getvalueof var="paramEditorObj"            param="editorObj"/>
  <dspel:getvalueof var="paramShowIdColumn"         param="showIdColumn"/>
  <dspel:getvalueof var="paramShowIconColumn"       param="showIconColumn"/>

  <dspel:getvalueof var="paramStartIndex"           param="startIndex"/>
  <dspel:getvalueof var="paramEndIndex"             param="endIndex"/>
  <dspel:getvalueof var="paramFormHandlerPath"      param="formHandlerPath"/>
  
  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="formHandler" bean="${paramFormHandlerPath}"/>

  <c:choose>

    <%-- Javascript initialization --%>
    <c:when test="${not empty paramInitialize}">

      <script type="text/javascript">

        /////////////////////////////////////////////////////////////////////
        //        COLUMN FORMATTERS AND SETUP
        /////////////////////////////////////////////////////////////////////

        // Cell formatter for icon for individual items.
        <c:if test="${paramShowIconColumn == 'true'}">
          <c:out value="${paramEditorObj}"/>.formatIcon =
            function(pData, pRowIndex) {
              // "this" refers to the Dojox grid cell object.
              if (pData && pData != "...") {
                var html = '<img src="' + pData + '" border="0" align="absmiddle"/>';
                return html;
              }
              return pData;
            };
        </c:if>
          

        // Optionally display an Icon column.
        <c:if test="${paramShowIconColumn == 'true'}">
          <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
            { name: 'icon',
              field: 1,
              formatter: <c:out value="${paramEditorObj}"/>.formatIcon,
              styles: 'text-align: center;',
              width: '15px'};
          // Add a column for item display name.
          <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
            { name: '<fmt:message key="collectionEditor.item"/>',
              field: 0,
              styles: 'text-align: left;',
              width: 'auto'};
  
          <c:if test="${paramShowIdColumn == 'true'}">
            <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
              { name: '<fmt:message key="collectionEditor.itemId"/>',
                field: 2,
                styles: 'text-align: center;',
                width: 'auto'};
          </c:if>
        </c:if>

        <c:if test="${paramShowIconColumn != 'true' && paramShowIdColumn == 'true'}">
          <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
            { name: '<fmt:message key="collectionEditor.itemId"/>',
              field: 1,
              styles: 'text-align: center;',
              width: 'auto'};
          // Add a column for item display name.
          <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
            { name: '<fmt:message key="collectionEditor.item"/>',
              field: 0,
              styles: 'text-align: center;',
              width: 'auto'};
  
        </c:if>
      </script>

    </c:when>

    <%-- Render the items between startIndex and endIndex as JSON. Return an array
         of rows, each of which contains an object representing an individual
         item in the collection. --%>
    <c:when test="${not empty paramStartIndex && not empty paramEndIndex}">
      <%-- Get the all members of the current organization --%>
      <c:set var="allmembers" value="${formHandler.allMembers}"/>

      <json:array prettyPrint="true">

        <c:forEach items="${allmembers}" var="orgMember" varStatus="itemLoop"
                   begin="${paramStartIndex}" end="${paramEndIndex}">

          <c:set var="repItem" value="${orgMember.repositoryItem}"/>

          <web-ui:getIcon var="iconURL"
                          container="${formHandler.repositoryPathName}"
                          type="${repItem.itemDescriptor.itemDescriptorName}"
                          assetFamily="RepositoryItem"/>
          <c:url context="/${iconURL.contextRoot}" value="${iconURL.relativeUrl}" var="iconFullUrl"/>

          <json:array>

            <%-- Cell 0: item display name --%>
            <json:property name="displayName">
              <c:out value="${orgMember.name}"/>
            </json:property>

            <%-- Cell 1: url --%>
            <c:if test="${paramShowIconColumn == 'true'}">
              <json:property name="iconUrl">
                 <c:out value="${iconFullUrl}"/>
              </json:property>
            </c:if>

            <%-- Cell 2: url --%>
            <c:if test="${paramShowIdColumn == 'true'}">
              <json:property name="repositoryId">
                <c:out value="${repItem.repositoryId}"/>
              </json:property>
            </c:if>

          </json:array>

        </c:forEach>

      </json:array>

    </c:when>

  </c:choose>
</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/grid/itemOrgMembers.jsp#2 $$Change: 651448 $--%>

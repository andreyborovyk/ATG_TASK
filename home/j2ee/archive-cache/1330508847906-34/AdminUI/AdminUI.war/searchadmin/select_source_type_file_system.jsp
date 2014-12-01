<%--
  JSP, showing "file system" source type fields.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/select_source_type_file_system.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="section" var="section"/>
  <d:importbean var="settings" bean="/atg/searchadmin/adminui/navigation/FileSystemSourceSettings"/>

  <c:choose>
    <c:when test="${section == 'general'}">

<table class="form" cellspacing="0" cellpadding="0">
  <tbody>
    <tr>
      <td class="label">
        <span id="settings.baseFilePathAlert"><span class="required"><fmt:message
            key="project_general.required_field"/></span></span>
        <fmt:message key="select_source_type.file_system.file.path"/>
      </td>
      <td>
        <table cellpadding="0" cellspacing="0" class="browseField"><tr><td width="100%">
          <d:input type="text" iclass="textField" name="settings.baseFilePath" id="baseFilePath"
                   bean="FileSystemSourceSettings.baseFilePath"/>
        </td><td>
          <c:url value="/searchadmin/browse_file_system_popup.jsp" var="contentUrl">
            <c:param name="destFieldId" value="baseFilePath"/>
            <c:param name="title" value="index"/>
          </c:url>
          <input type="button" value="<fmt:message key='select_source_type.file_system.buttons.filepath'/>"
                 title="<fmt:message key='select_source_type.file_system.buttons.filepath.tooltip'/>"
                 onclick="return showPopUp('${contentUrl}');" />
        </td></tr></table>
      </td>
    </tr>
  </tbody>
</table>

    </c:when>
    <c:when test="${section == 'indexing'}">

      <d:getvalueof bean="/atg/multisite/SiteManager" var="siteManager"/>
      <c:if test="${siteManager.multisiteEnabled}">

<table class="form" cellspacing="0" cellpadding="0">
  <tbody>
    <tr>
      <td class="label">
        <fmt:message key="select_source_type.file_system.sites_meta_tag"/>
      </td>
      <td>
        <select id="sitesSource" class="small" onchange="onSitesSourceChange(this)">
          <option value="no">
            <fmt:message key="select_source_type.file_system.sites_meta_tag.no_sites"/>
          </option>
          <%--option value="all" <c:if test="${settings.allSites}">selected="true"</c:if>>
            <fmt:message key="select_source_type.file_system.sites_meta_tag.all_sites"/>
          </option--%>
          <option value="select" <c:if test="${not settings.allSites and not empty settings.siteIds}">selected="true"</c:if>>
            <fmt:message key="select_source_type.file_system.sites_meta_tag.select_sites"/>
          </option>
        </select>
        <span class="ea"><tags:ea key="embedded_assistant.select_source_type.file_system.sites_meta_tag" /></span>
        <d:input type="hidden" bean="FileSystemSourceSettings.allSites" id="allSitesField" />
        <d:importbean var="manageContentFormHandler" bean="/atg/searchadmin/adminui/formhandlers/ManageContentFormHandler"/>
        <admin-beans:getLogicalPartitionById var="partition" id="${manageContentFormHandler.contentSetPartition}" />
        <c:url value="/searchadmin/select_sites_popup.jsp" var="selectSitesUrl">
          <c:param name="partitionId" value="${partition.id}"/>
        </c:url>
        <input type="button" onclick="return showPopUp('${selectSitesUrl}');"
               id="selectSitesButton" style="display:none"
               value="<fmt:message key='select_source_type.file_system.sites_meta_tag.select.title'/>"
               title="<fmt:message key='select_source_type.file_system.sites_meta_tag.select.tooltip'/>"/>
        <div id="sitesArea" class="content_textarea" style="display:none">
          <div id="siteNamesDiv">
            <d:getvalueof bean="/atg/search/multisite/SiteIndexInfoService.allSites" var="allSites"/>
            <c:forEach items="${allSites}" var="currentSite">
              <d:tomap var="siteItem" value="${currentSite}"/>
              <c:forTokens var="site" items="${settings.siteIds}" delims=",">
                <c:if test="${siteItem.id eq site}">
                  ${siteItem.id}&nbsp;-&nbsp;${siteItem.name}<br/>
                </c:if>
              </c:forTokens>
            </c:forEach>
          </div>
          <d:input type="hidden" bean="FileSystemSourceSettings.siteIds" id="siteIdsField" />
        </div>
        <div id="sitesDocsetArea" style="display:none">
          <d:input type="checkbox" bean="FileSystemSourceSettings.sitesDocset" value="true" id="sitesDocsetField" />
          <label for="sitesDocsetField"><fmt:message key="select_source_type.file_system.sites_meta_tag.docset" /></label>
        </div>
        <script>
          function onSitesSourceChange(el) {
            document.getElementById("allSitesField").value = el.value == "all";
            document.getElementById("selectSitesButton").style.display = el.value == "select" ? "" : "none";
            document.getElementById("sitesArea").style.display = el.value == "select" ? "" : "none";
            document.getElementById("sitesDocsetArea").style.display = el.value == "no" ? "none" : "";
            if (el.value == "no") {
              changeSites(new Array(), new Array());
            }
          }
          function changeSites(siteIds, siteNames) {
            document.getElementById("siteIdsField").value = siteIds.join(",");
            document.getElementById("siteNamesDiv").innerHTML = siteNames.join("<br/>");
          }
          function getSelectedSiteIds() {
            return document.getElementById("siteIdsField").value.split(",");
          }
          onSitesSourceChange(document.getElementById("sitesSource"));
        </script>
      </td>
    </tr>
  </tbody>
</table>

      </c:if><%-- End of multisiteEnabled check --%>

<c:set var="indexOnlyMetaTagTitleHTML">
  <span><fmt:message key="select_source_type.file_system.index_only_meta_tags.property"/></span>
</c:set>
<c:set var="indexOnlyMetaTagButtonsHTML">
  <input type="button" class="tableButton indexOnlyMetaTagDelete" onclick="indexOnlyMetaTags.del(this);"
         value="<fmt:message key='select_source_type.file_system.index_only_meta_tags.delete' />"
         title="<fmt:message key='select_source_type.file_system.index_only_meta_tags.delete.tooltip' />" />
  <input type="button" class="tableButton indexOnlyMetaTagAdd" onclick="indexOnlyMetaTags.add();"
         value="<fmt:message key='select_source_type.file_system.index_only_meta_tags.add' />"
         title="<fmt:message key='select_source_type.file_system.index_only_meta_tags.add.tooltip' />" />
</c:set>
<table class="form" cellspacing="0" cellpadding="0" id="indexOnlyMetaTagsTable">
  <tbody>
    <tr>
      <td class="label">
        <strong>
          <fmt:message key="select_source_type.file_system.index_only_meta_tags"/>
        </strong>
      </td>
      <td>
        <span class="ea"><tags:ea key="embedded_assistant.select_source_type.file_system.index_only_meta_tags" /></span>
      </td>
    </tr>
    <c:forEach items="${settings.indexOnlyMetaTagsName}" var="metaTagName" varStatus="metaTagStatus">
      <tr id="indexOnlyMetaTagRow${metaTagStatus.index}">
        <td class="label">${indexOnlyMetaTagTitleHTML}</td>
        <td>
          <nobr>
            <d:select iclass="small" bean="FileSystemSourceSettings.indexOnlyMetaTagsType" name="indexOnlyMetaTagsType">
              <%-- TODO move property types from facet form handler to some common place --%>
              <d:importbean var="facetFormHandler" bean="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"/>
              <c:forEach items="${facetFormHandler.propertyTypes}" var="optionKey">
                <d:option value="${optionKey}" selected="${settings.indexOnlyMetaTagsType[metaTagStatus.index] == optionKey}">
                  <fmt:message key="facet.property_type.option.${optionKey}"/>
                </d:option>
              </c:forEach>
            </d:select>
            <d:input type="text" iclass="textField halved" name="indexOnlyMetaTagsName"
                     bean="FileSystemSourceSettings.indexOnlyMetaTagsName" value="${metaTagName}" />
          </nobr>
          &nbsp;
          <nobr>
            <fmt:message key="select_source_type.file_system.index_only_meta_tags.value"/>
            <d:input type="text" bean="FileSystemSourceSettings.indexOnlyMetaTagsValue"
                     value="${settings.indexOnlyMetaTagsValue[metaTagStatus.index]}"
                     iclass="textField halved" name="indexOnlyMetaTagsValue" />
            ${indexOnlyMetaTagButtonsHTML}
          </nobr>
          <br/>
          <tags:checkbox beanName="/atg/searchadmin/adminui/navigation/FileSystemSourceSettings"
              checked="${settings.indexOnlyMetaTagsDocset[metaTagStatus.index]}" name="indexOnlyMetaTagsDocset" />
          <fmt:message key="select_source_type.file_system.index_only_meta_tags.docset"/>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>
<c:set var="indexOnlyMetaTagNewContentHTML">
  <nobr>
    <d:select iclass="small" bean="FileSystemSourceSettings.indexOnlyMetaTagsType" name="indexOnlyMetaTagsType">
      <%-- TODO move property types from facet form handler to some common place --%>
      <d:importbean var="facetFormHandler" bean="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"/>
      <c:forEach items="${facetFormHandler.propertyTypes}" var="optionKey">
        <d:option value="${optionKey}" selected="${optionKey == 'string'}">
          <fmt:message key="facet.property_type.option.${optionKey}"/>
        </d:option>
      </c:forEach>
    </d:select>
    <d:input type="text" iclass="textField halved" name="indexOnlyMetaTagsName"
             bean="FileSystemSourceSettings.indexOnlyMetaTagsName" value="" />
  </nobr>
  &nbsp;
  <nobr>
    <fmt:message key="select_source_type.file_system.index_only_meta_tags.value"/>
    <d:input type="text" bean="FileSystemSourceSettings.indexOnlyMetaTagsValue"
             value="" iclass="textField halved" name="indexOnlyMetaTagsValue" />
    ${indexOnlyMetaTagButtonsHTML}
  </nobr>
  <br/>
  <tags:checkbox beanName="/atg/searchadmin/adminui/navigation/FileSystemSourceSettings"
      checked="false" name="indexOnlyMetaTagsDocset" />
  <fmt:message key="select_source_type.file_system.index_only_meta_tags.docset"/>
</c:set>
<script>
var indexOnlyMetaTags = {
  table: document.getElementById("indexOnlyMetaTagsTable"),
  titleHTML: "${adminfunctions:escapeJsString(indexOnlyMetaTagTitleHTML)}",
  contentHTML: "${adminfunctions:escapeJsString(indexOnlyMetaTagNewContentHTML)}",
  add: function() {
    var l = this.table.rows.length;
    var newRow = this.table.insertRow(l);
    newRow.id = "indexOnlyMetaTagRow" + (l - 1);
    var titleCell = newRow.insertCell(0);
    titleCell.className = "label";
    titleCell.innerHTML = this.titleHTML;
    var contentCell = newRow.insertCell(1);
    contentCell.innerHTML = this.contentHTML;
    this.updateButtons();
  },
  del: function(el) {
    var row = getParentByChildElement(el, "tr");
    this.table.deleteRow(row.rowIndex);
    this.updateButtons();
  },
  updateButtons: function() {
    dojo.query(".indexOnlyMetaTagAdd", this.table).forEach(function(item, index, items) {
      item.style.display = index < items.length - 1 ? "none" : "";
    });
    dojo.query(".indexOnlyMetaTagDelete", this.table).forEach(function(item, index, items) {
      item.style.display = items.length == 1 ? "none" : "";
    });
  }
};
if (${empty settings.indexOnlyMetaTagsName}) {
  indexOnlyMetaTags.add();
} else {
  indexOnlyMetaTags.updateButtons();
}
</script>

    </c:when>
    <c:when test="${section == 'other'}">

<h3>
  <fmt:message key="file_content.settings.mapping.title"/>
  <span id="searchFileTypes:hideLink" class="headerLink" style="display:none">
    [<a href="#" onclick="return showHideSettings('searchFileTypes', false);"
        title="<fmt:message key='file_content.settings.mapping.hide_link.tooltip'/>">
      <fmt:message key="file_content.settings.mapping.hide_link"/>
    </a>]
  </span>
  <span id="searchFileTypes:showLink" class="headerLink">
    [<a href="#" onclick="return showHideSettings('searchFileTypes', true);"
        title="<fmt:message key='file_content.settings.mapping.show_link.tooltip'/>">
      <fmt:message key="file_content.settings.mapping.show_link"/>
    </a>]
  </span>
</h3>

<div id="searchFileTypes" style="display:none">
<table class="data" cellspacing="0" cellpadding="0">
  <thead>
    <tr>
      <th class="left" width="50%">
        <fmt:message key="file_content.settimgs.mapping.title.type"/>
      </th>
      <th class="left" width="50%">
        <fmt:message key="file_content.settimgs.mapping.title.extension"/>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.mapping.type.plain"/>
      </b><br/>
        <fmt:message key="file_content.settings.mapping.description.plain"/>
      </td>
      <td>
        <d:input type="text" bean="FileSystemSourceSettings.fileExtensionsByType.text" iclass="textField" style="width:95%"/>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.mapping.type.html"/>
      </b><br/>
        <fmt:message key="file_content.settings.mapping.description.html"/>
      </td>
      <td>
        <d:input bean="FileSystemSourceSettings.fileExtensionsByType.html" type="html" iclass="textField" style="width:95%"/>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.mapping.type.rich_content"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.mapping.description.rich_content"/>
      </td>
      <td>
        <d:input type="text" bean="FileSystemSourceSettings.fileExtensionsByType.inso" iclass="textField" style="width:95%"/>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.mapping.type.adobe_pdf"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.mapping.description.adobe_pdf"/>
      </td>
      <td>
        <d:input type="text" bean="FileSystemSourceSettings.fileExtensionsByType.pdf" iclass="textField" style="width:95%"/>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.mapping.type.xml"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.mapping.description.xml"/>
      </td>
      <td>
        <d:input type="text" bean="FileSystemSourceSettings.fileExtensionsByType.xml" iclass="textField" style="width:95%"/>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.mapping.type.xhtml"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.mapping.description.xhtml"/>
      </td>
      <td>
        <d:input type="text" bean="FileSystemSourceSettings.fileExtensionsByType.xhtml" iclass="textField" style="width:95%"/>
      </td>
    </tr>

  </tbody>
</table>

<table class="data" cellspacing="0" cellpadding="0">
  <thead>
    <tr>
      <th class="left" width="50%">
        <fmt:message key="file_content.settings.additional_mapping_feature.title.additional"/>
      </th>
      <th class="left" width="50%">
        <fmt:message key="file_content.settings.additional_mapping_feature.title.default"/>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.additional_mapping_feature.blank"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.additional_mapping_description.blank"/>
      </td>
      <td>
        <d:select iclass="small" bean="FileSystemSourceSettings.blankExtension">
          <c:forEach items="${settings.featureMappings}" var="feature">
            <d:option value="${feature}">
              <fmt:message key="file_content.settings.additional_mapping_feature.type.${feature}"/>
            </d:option>
          </c:forEach>
        </d:select>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.additional_mapping_feature.unspecified"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.additional_mapping_description.unspecified"/>
      </td>
      <td>
        <d:select iclass="small" bean="FileSystemSourceSettings.unspecifiedExtensionsMapping">
          <c:forEach items="${settings.featureMappings}" var="feature">
            <d:option value="${feature}">
              <fmt:message key="file_content.settings.additional_mapping_feature.type.${feature}"/>
            </d:option>
          </c:forEach>
        </d:select>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.additional_mapping_feature.suppression"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.additional_mapping_description.suppression"/>
      </td>
      <td>
        <d:input type="text" iclass="textField" style="width:95%" width="100%"
                 bean="FileSystemSourceSettings.suppressionExtensions"/>
      </td>
    </tr>
    <tr>
      <td><b>
        <fmt:message key="file_content.settings.additional_mapping_feature.general_suppression"/>
      </b>
        <br/>
        <fmt:message key="file_content.settings.additional_mapping_description.general_suppression"/>
      </td>
      <td>
        <d:input type="text" iclass="textField" style="width:95%" width="100%"
                 bean="FileSystemSourceSettings.generalFileSuppression"/>
      </td>
    </tr>
  </tbody>
</table>
</div>

    </c:when>
    <c:when test="${section == 'advanced'}">

<table class="form" cellspacing="0" cellpadding="0">
  <tbody>
    <tr>
      <td class="label">
        <fmt:message key="select_source_type.file_system.external.access.url"/>
      </td>
      <td>
        <d:input type="text" iclass="textField" name="externalAccessURL"
                 bean="FileSystemSourceSettings.externalAccessURL"/>
        <span class="ea"><tags:ea key="embedded_assistant.select_source_type_file_system.external_access_url" /></span>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="select_source_type.file_system.output.directory"/>
      </td>
      <td>
        <table cellpadding="0" cellspacing="0" width="100%"><tr><td class="convertedDir">
          <d:input type="text" iclass="textField convertedDir" name="convertedDocumentOutputDirectory"
                   id="convertedDocumentOutputDirectory"
                   bean="FileSystemSourceSettings.convertedDocumentOutputDirectory"/>
        </td><td>
          <c:url value="/searchadmin/browse_file_system_popup.jsp" var="convertedUrl">
            <c:param name="destFieldId" value="convertedDocumentOutputDirectory"/>
            <c:param name="title" value="convent"/>
          </c:url>
          <input type="button" value="<fmt:message key='select_source_type.file_system.buttons.filepath'/>"
                 title="<fmt:message key='select_source_type.file_system.buttons.filepath.tooltip'/>"
                 onclick="return showPopUp('${convertedUrl}');" />
        </td><td class="ea_convertedDir">
          <span class="ea"><tags:ea key="embedded_assistant.select_source_type_file_system.output_directory" /></span>
        </td></tr></table>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="select_source_type.file_system.document.access.url"/>
      </td>
      <td>
        <d:input type="text" iclass="textField" name="convertedDocumentAccessURL"
                 bean="FileSystemSourceSettings.ConvertedDocumentAccessURL"/>
        <span class="ea"><tags:ea key="embedded_assistant.select_source_type_file_system.document_access_url" /></span>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="select_source_type.file_system.default_encoding"/>
      </td>
      <td>
        <d:input type="text" iclass="textField" name="documentEncoding"
                 bean="FileSystemSourceSettings.defaultEncoding"/>
        <span class="ea"><tags:ea key="embedded_assistant.select_source_type_file_system.default_encoding" /></span>
      </td>
    </tr>
  </tbody>
</table>

    </c:when>
  </c:choose>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/select_source_type_file_system.jsp#2 $$Change: 651448 $--%>

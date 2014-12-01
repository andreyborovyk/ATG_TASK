<%--
  JSP,used to assign content sets to host machine.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/select_sites_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="partitionId" var="partitionId"/>

  <script type="text/javascript">
    function customLoad() {
      var siteIds = getSelectedSiteIds();
      for (var i = 0; i < siteIds.length; i++) {
        var field = document.getElementById("siteId." + siteIds[i]);
        if (field) {
          field.checked = true;
        }
      }
      document.getElementById('checkall_site').checked = getChildCheckboxesState('site', 'siteId');
    }
    function onSave() {
      var siteIds = new Array();
      var siteNames = new Array();
      var fields = document.getElementsByName('siteId');
      for (var i = 0; i < fields.length; i++) {
        if (fields[i].checked) {
          var l = siteIds.length;
          siteIds[l] = fields[i].value;
          siteNames[l] = document.getElementById("siteName." + siteIds[l]).innerHTML;
        }
      }
      changeSites(siteIds, siteNames);
    }
  </script>

  <d:form action="${formActionUrl}">
    <div class="content">
      <p>
        <fmt:message key="select_site_popup.title" />
      <p>
      <admin-beans:getLogicalPartitionById var="partition" id="${partitionId}" />
      
      <d:droplet name="/atg/search/multisite/droplets/FindSiteAssociations">
        <d:param name="contentIds" value="${partition.sharedId}"/>
        <d:param name="contentLabels" value="${partition.contentLabel}"/>
        <d:oparam name="output">
          <d:getvalueof var="sites" param="sites" />
        </d:oparam>
      </d:droplet>
      
      <c:set var="checkAllSitesCheckbox">
        <input id="checkall_site" style="margin-left:2px;" type="checkbox" 
               onclick="setChildCheckboxesState('site', 'siteId', this.checked);"/>
      </c:set>
      <admin-ui:table renderer="/templates/table_data.jsp"
                      modelVar="table" var="site"
                      items="${sites}" tableId="site">
        <d:tomap var="siteItem" value="${site}" />
        <admin-ui:column type="checkbox" headerContent="${checkAllSitesCheckbox}">
          <input type="checkbox" name="siteId" value="${siteItem.id}" id="siteId.${siteItem.id}" 
                onclick="document.getElementById('checkall_site').checked =
                              getChildCheckboxesState('site', 'siteId');"/>
        </admin-ui:column>
        <admin-ui:column title="select_site_popup.column.title" type="static" name="selected_sites">
          <span id="siteName.${siteItem.id}"><c:out value="${siteItem.id} - ${siteItem.name}" /></span>
        </admin-ui:column>
        <admin-ui:column title="select_site_popup.column.content.label" type="static" name="content_label">
          <c:out value="${partition.contentLabel}"/>
        </admin-ui:column>
      </admin-ui:table>
    </div>

    <div class="footer" id="popupFooter">
      <input type="button" onclick="onSave(); return closePopUp();"
             value="<fmt:message key='select_site_popup.button.save'/>"
             title="<fmt:message key='select_site_popup.button.save.tooltip'/>"/>
      <input type="button" value="<fmt:message key='select_site_popup.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='select_site_popup.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/select_sites_popup.jsp#2 $$Change: 651448 $--%>

<%--
  JSP,used to assign content sets to host machine.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_environments_host_restrict_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="environmentId" var="environmentId"/>
  <d:getvalueof param="checkedItems" var="checkedItems"/>
  
  <d:importbean var="handler" bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler"/>
  
  <c:url var="configureHostsURL" value="/searchadmin/search_env_configure_hosts.jsp">
    <c:param name="projectId" value="${projectId}"/>
    <c:param name="environmentId" value="${environmentId}"/>
    <c:param name="showType" value="projectEnv"/>
  </c:url>
  
  <d:form action="${configureHostsURL}" method="POST">
    <div class="content">
    <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
    <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.logicalPartitionComparator" var="comparator"/>
    <admin-ui:sort var="sortedPartitions" items="${project.index.logicalPartitions}"  
                   comparator="${comparator}" sortMode="default"/>
    <p>
    <c:set var="checkAllContentSourcesCheckbox">
      <input id="checkall_contentSet" style="margin-left:2px;" type="checkbox" 
             onclick="setChildCheckboxesState('contentSet', 'contentSetName', this.checked);"/>
    </c:set>
    <admin-ui:table renderer="/templates/table_data.jsp"
                    modelVar="table"
                    var="contentSet"
                    items="${sortedPartitions}"
                    tableId="contentSet">
      <admin-ui:column type="checkbox" headerContent="${checkAllContentSourcesCheckbox}">
        <input type="checkbox" name="contentSetName" value="${contentSet.name}" id="contentSet_${contentSet.id}"
              onclick="document.getElementById('checkall_contentSet').checked =
                              getChildCheckboxesState('contentSet', 'contentSetName');"/>
      </admin-ui:column>
      <admin-ui:column title="search_environments_host_restrict_popup.column.title" type="static" name="content_source_sets">
        ${contentSet.name}
      </admin-ui:column>
    </admin-ui:table>
    </div>

    <div class="footer" id="popupFooter">
      <d:input type="hidden" bean="SearchEnvironmentFormHandler.successURL" value="close" name="successUrl"/>
      <d:input type="hidden" bean="SearchEnvironmentFormHandler.errorURL" value="${configureHostsURL}" name="errorUrl" />
      
      <fmt:message var="saveButton" key='search_environments_host_restrict_popup.button.save'/>
      <fmt:message var="saveButtonTooltip" key='search_environments_host_restrict_popup.button.save.tooltip'/>
      <d:input type="submit" bean="SearchEnvironmentFormHandler.addContentSet" iclass="formsubmitter"
               name="saveButton" value="${saveButton}" title="${saveButtonTooltip}"
               onclick="return checkForm()"/>
      <input type="button" value="<fmt:message key='search_environments_host_restrict_popup.button.cancel'/>"
             onclick="closePopUp()"
             title="<fmt:message key='search_environments_host_restrict_popup.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
  <script type="text/javascript">
    //put checked sets values in a table during loading process.
    function customLoad() {
      var checkedItems = '${checkedItems}';
      var objs = null;
      if (checkedItems != null) {
        objs = checkedItems.split(";");
      }
      if ((objs.length > 0) || (objs.length > 0 && objs[0] != "")) {
        var table = document.getElementById("contentSet");
        var inputElems = table.getElementsByTagName("input");
        for (var i = 1; i < inputElems.length; i++) {
          if (inputElems[i].type == "checkbox") {
            for (var j = 0; j < objs.length; j++) {
              if (inputElems[i].id.split("contentSet_")[1] == objs[j]) {
                inputElems[i].checked = "true";
              }
            }
          }
        }
        document.getElementById('checkall_contentSet').checked = getChildCheckboxesState('contentSet', 'contentSetName');
      }
      
      atg.searchadmin.adminui.formsubmitter.customPopupHandleResponse = function(data) {
        var contentSetIds = "";
        var contentSetNames = "";
        var collection = document.getElementsByName('contentSetName');
        var length = collection.length;
        for (var i = 0;i < length; i++) {
          if(collection[i].checked) {
            if (contentSetIds != "") {
              contentSetIds += ';';
            }
            contentSetIds += collection[i].id.split("contentSet_")[1];
            if (contentSetNames != "") {
              contentSetNames += ';';
            }
            contentSetNames += collection[i].value;
          }
        }
        if (contentSetNames.length > 0) {       
          selectedContentLink.innerHTML = contentSetNames;
        } else {
          <fmt:message var="selectedSets" key="search_env_configure_hosts.table.content.sets.select.empty"/>
          selectedContentLink.innerHTML = "${selectedSets}";
        }
        var href = selectedContentLink.href.split("checkedItems=")[0];
        href = href + "checkedItems=" + contentSetIds;
        selectedContentLink.href = href;
        
        var parentElement = selectedContentLink.parentNode;
        var inputs = parentElement.getElementsByTagName("input");
        for (var i=0; i<inputs.length; i++) {
          if (inputs[i].name == "contentSetNames") {
            inputs[i].value = contentSetNames;
          } else if (inputs[i].name == "contentSetIds") {
            inputs[i].value = contentSetIds;
          }
        }
      };
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_environments_host_restrict_popup.jsp#2 $$Change: 651448 $--%>

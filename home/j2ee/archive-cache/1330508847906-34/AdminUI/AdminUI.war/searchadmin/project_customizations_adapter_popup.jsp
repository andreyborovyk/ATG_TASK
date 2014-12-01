<%--
  JSP popUp,  showing all Dictionary Adapter existing in system association with current language.
  Also used for selections dictionary, for including to current project.

  We have request parameter, called "language", which used for get necessary Dictiopnary Adaptors.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_adapter_popup.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" />
  <d:getvalueof param="language" var="language" />

  <admin-beans:getCustomizationsAdapters var="customizationsAdapters" projectId="${projectId}"
                                         languageId="${language}"/>

  <c:url var="backUrl" value="project_customizations_other.jsp"/>
  
  <form action="${backUrl}" method="POST" target="rightPane" name="dictionaryForm">
    <div class="content">
      <p><fmt:message key="project_customizations.adaptors.title" /></p>

      <c:set var="itemsHeaderContentValue">
        <input style="margin-left:2px;" type="checkbox" id="adapterItemsAll" class="selectAll"
               onclick="setChildCheckboxesState('adapterItemsTable', 'selectDict', this.checked);"/>
      </c:set>
      <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                      var="dictionary" items="${customizationsAdapters}"
                      tableId="adapterItemsTable">
        <admin-ui:column type="checkbox" headerContent="${itemsHeaderContentValue}">
          <input type="checkbox" id="check_${dictionary.adaptor}" name="selectDict" value="${dictionary.adaptor}"
                 onclick="document.getElementById('adapterItemsAll').checked =
                              getChildCheckboxesState('adapterItemsTable', 'selectDict');"/>
        </admin-ui:column>
        <admin-ui:column title="project_customizations.table.dictionary" type="static">
          <label for="check_${dictionary.adaptor}"><c:out value="${dictionary.adaptor}"/></label>
        </admin-ui:column>
      </admin-ui:table>
    </div>

    <div class="footer" id="popupFooter">
      <c:set var="totalAdaptorsCount" value="${fn:length(customizationsAdapters)}"/>
      <input type="button" value="<fmt:message key="project_customizations.language.popup.select"/>"
             onclick="getDictionaryId();" <c:if test="${totalAdaptorsCount == 0}">disabled="true"</c:if>
             title="<fmt:message key="project_customizations.language.popup.select"/>"/>
      <input type="button" value="<fmt:message key="project_footer.buttons.cancel"/>" onclick="closePopUp();"
             title="<fmt:message key="project_footer.buttons.cancel"/>"/>
    </div>

  </form>

  <script type="text/javascript">
    function customLoad() {
      var dictionaries = getLanguageDictionaries('<c:out value="${language}"/>');
      if (dictionaries != "") {
        var dictionaryArray = dictionaries.split(", ");
        for (var i = 0; i < dictionaryArray.length; i++) {
          document.getElementById("check_"+dictionaryArray[i]).checked = true;
        }
      }
      document.getElementById('adapterItemsAll').checked = getChildCheckboxesState('adapterItemsTable', 'selectDict');
    }

    function getDictionaryId() {
      var checkedDictionaryName = "";
      var dictionaryBox = document.dictionaryForm.selectDict;
      if (dictionaryBox.length == null) {
        if (dictionaryBox.checked) {
          checkedDictionaryName = dictionaryBox.value;
        }
      } else {
        for (var counter = 0; counter < dictionaryBox.length; counter++) {
          if (dictionaryBox[counter].checked) {
            if (checkedDictionaryName != "") {
              checkedDictionaryName += ", ";
            }
            checkedDictionaryName += dictionaryBox[counter].value;
          }
        }
      }
      setLanguageDictionaries('<c:out value="${language}"/>', checkedDictionaryName);
      closePopUp();
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_customizations_adapter_popup.jsp#1 $$Change: 651360 $--%>

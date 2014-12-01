<%--
  JSP,used to delete content label or target type in table.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_settings_delete_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="tableId" var="tableId"/>
  <d:getvalueof param="index" var="index"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/DeleteGlobalSettingsFormHandler"/>
  <c:set var="formHandler" value="DeleteGlobalSettingsFormHandler"/>
  
  <script>
    function customLoad(){
      var globalSettingName = getNameByIndexFromPopup('${tableId}', ${index});
      document.getElementById("globalSetting").value=globalSettingName;
      document.getElementById("nameToDelete").appendChild(document.createTextNode(globalSettingName));
      atg.searchadmin.adminui.formsubmitter.customPopupHandleResponse = function(data) {
        deleteRowByIndexFromPopup('${tableId}', ${index});
      };
    }
  </script>

  <c:url var="errorURL" value="/searchadmin/global_settings_delete_popup.jsp">
    <c:param name="tableId" value="${tableId}"/>
    <c:param name="index" value="${index}"/>
  </c:url>
  <d:form action="${errorURL}" method="POST">
    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="global_settings_delete_popup.question">
          <fmt:param>
            <strong>
              <c:if test="${tableId eq 'labelTable'}">
                <fmt:message key="global_settings_delete_popup.content_label"/>
                <c:set var="type" value="contentLabel"/>
              </c:if>
              <c:if test="${tableId eq 'targetTable'}">
                <fmt:message key="global_settings_delete_popup.target_type"/>
                <c:set var="type" value="targetType"/>
              </c:if>
              <span id="nameToDelete"></span>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <d:input type="hidden" bean="${formHandler}.globalSetting" name="globalSetting" id="globalSetting"/>
      <d:input type="hidden" bean="${formHandler}.type" value="${type}" name="type"/>
      <fmt:message key="global_settings_delete_popup.ok.button" var="okButton"/>
      <fmt:message key="global_settings_delete_popup.ok.button.tooltip" var="okButtonToolTip"/>
      <d:input type="submit" value="${okButton}" iclass="formsubmitter"
             bean="${formHandler}.delete" 
             title="${okButtonToolTip}"/>
      <input type="button" value="<fmt:message key='global_settings_delete_popup.cancel.button'/>"
             onclick="closePopUp()"
             title="<fmt:message key='global_settings_delete_popup.cancel.button.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_settings_delete_popup.jsp#2 $$Change: 651448 $--%>

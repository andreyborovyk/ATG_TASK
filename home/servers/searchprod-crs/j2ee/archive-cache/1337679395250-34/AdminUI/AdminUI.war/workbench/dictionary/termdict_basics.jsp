<%--
  JSP provides fields for creation/edition of the dictionary

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_basics.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler"/>

  <d:getvalueof param="dictId" var="dictId"/>

  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="dictId" value="${dictId}"/>
  </admin-ui:initializeFormHandler>

  <h3>
    <fmt:message key="termdict_basics.title"/>
  </h3>
  <d:input type="hidden" bean="TermDictsFormHandler.dictId" name="dictId"/>
  <table class="form" cellspacing="0" cellpadding="0">
    <tr>
      <td class="label">
        <span id="dictionaryNameAlert">
          <span class="required"><fmt:message key="project_general.required_field"/></span>
        </span>
        <label for="dictionaryName">
          <fmt:message key="termdict_basics.name"/>
        </label>
      </td>
      <td>
        <d:input type="text" iclass="textField" bean="TermDictsFormHandler.dictionaryName" name="dictionaryName"/>
      </td>
    </tr>
    <tr>
      <td class="label">
        <label for="description">
          <span id="descriptionAlert"></span><fmt:message key="termdict_basics.description"/>
        </label>
      </td>
      <td>
        <d:input type="text" iclass="textField" bean="TermDictsFormHandler.description" name="description"/>
      </td>
    </tr>
    <tr>
      <td class="label">
        <label for="adapterName">
          <span id="adapterNameAlert"></span><fmt:message key="termdict_basics.adaptorName"/>
        </label>
      </td>
      <td>
        <d:input type="text" iclass="textField" bean="TermDictsFormHandler.adapterName" name="adapterName"/>
        <span class="ea"><tags:ea key="embedded_assistant.termdict_basics.adaptorName" /></span>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="termdict_basics.language"/>
      </td>
      <td>
        <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                      var="allLanguages"/>
        <d:select id="language" iclass="small" bean="TermDictsFormHandler.language" name="language">
          <c:forEach items="${allLanguages}" var="currentLanguage">
            <admin-ui:option value="${currentLanguage}">
              <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentLanguage}"/>
              ${localizedLanguage}
            </admin-ui:option>
          </c:forEach>
        </d:select>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="termdict_basics.load.mode"/>
      </td>
      <td>
        <ul class="simple">
          <li>
            <d:input type="radio" id="modify" name="loadMode"
                     bean="TermDictsFormHandler.loadMode" value="modify" />
            <fmt:message key="termdict_basics.load.mode.modify"/>
            <span class="ea"><tags:ea key="embedded_assistant.termdict_basics.load_mode" /></span>
          </li>
          <li>
            <d:input type="radio" id="override" name="loadMode"
                     bean="TermDictsFormHandler.loadMode" value="override"/>
            <fmt:message key="termdict_basics.load.mode.override"/>
          </li>
          <li>
            <d:input type="radio" id="reject" name="loadMode"
                     bean="TermDictsFormHandler.loadMode" value="reject"/>
            <fmt:message key="termdict_basics.load.mode.reject"/>
          </li>
        </ul>
      </td>
    </tr>
    <tr>
      <td class="label">
        <label for="termExpansion">
          <span id="termExpansionAlert"></span><fmt:message key="termdict_basics.max.term.expansion"/>
        </label>
      </td>
      <td>
        <d:input type="text" iclass="textField" bean="TermDictsFormHandler.termExpansion" name="termExpansion"/>
        <span class="ea"><tags:ea key="embedded_assistant.termdict_basics.max_term_expansion" /></span>
      </td>
    </tr>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_basics.jsp#1 $$Change: 651360 $--%>

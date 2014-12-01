<%--
  JSP, showing drop-down of all licensed languages. Used to be included from topicset_new.jsp and topic.jsp.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_languages.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Topic set languages --%>
  <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                var="allLanguages"/>
  <d:select id="language" iclass="small"
            bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.topicSetLanguage">
    <d:option value="">
      <fmt:message key="new_topic_set.form.no_language"/>
    </d:option>
    <c:forEach items="${allLanguages}" var="currentLanguage">
      <admin-ui:option value="${currentLanguage}">
        <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentLanguage}"/>
        ${localizedLanguage}
      </admin-ui:option>
    </c:forEach>
  </d:select>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_languages.jsp#2 $$Change: 651448 $--%>

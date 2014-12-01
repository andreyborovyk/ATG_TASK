<%--
 * JSP contain term synonyms table, allow add new synonym and edit exist synonyms.
 *
 * @author Alexander Lutarevich
 * @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_synonyms_table.jsp#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$$
 --%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/termdict_synonyms_table.js"></script>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"/>
  <d:getvalueof bean="TermFormHandler.sortValue" var="sortValue"/>
  <d:getvalueof bean="TermFormHandler.enableWeight" var="enableWeight"/>
  <d:getvalueof bean="TermFormHandler.synonymBeans" var="synonyms"/>
  <d:getvalueof bean="TermFormHandler.synonymBeansSize" var="synonymsSize"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.synonymComparator" var="comparator"/>
  <c:set target="${comparator}" property="locale" value="${pageContext.request.locale}" />
  <d:getvalueof bean="/atg/searchadmin/adminui/validator/MessageResources" var="messageResources"/>
  <c:set target="${comparator}" property="messageResources" value="${messageResources}" />
  <admin-ui:sort var="sortedSynonyms" items="${synonyms}" comparator="${comparator}" sortMode="${sortValue}" />

  <h3><fmt:message key="term_basics.title.synonyms"/></h3>

  <%-- Table allow edit and add new Synonym Term--%>
  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="synonym" items="${sortedSynonyms}"
                  sort="${sortValue}" onSort="tableOnSort"
                  tableId="editSynonymTable" emptyRow="true">

    <admin-ui:column type="icon" width="30">
      <fmt:message var="iconTooltip" key="termdict_synonyms_table.relationship.tooltip.${synonym.relationship}"/>
      <a class="icon_syn term_${synonym.relationship}" href="#" title="${iconTooltip}" onclick="return false;">syn</a>
    </admin-ui:column>

    <admin-ui:column title="termdict_synonyms_table.table.synonym" type="sortable" width="65%" name="synonym">
      <d:input bean="TermFormHandler.id" type="hidden" name="id" value="${synonym.id}"/>
      <d:input bean="TermFormHandler.changed" type="hidden" name="TermFormHandler.changed" value="false" iclass="synChanged"/>
      <d:input bean="TermFormHandler.synonymName" name="synonymName" value="${synonym.synonym}"
               type="text" iclass="textField" style="width:90%"
               onkeyup="addEmptySynonymField(this);" onchange="addEmptySynonymField(this); setChanged(this);"/>
    </admin-ui:column>

    <admin-ui:column title="termdict_synonyms_table.table.relationship" type="sortable" name="relation">
      <d:select id="synonymRelations" iclass="small"
                bean="TermFormHandler.relationship" name="relationship"
                onchange="addEmptySynonymField(this); setChanged(this); return changeRelationIcon(this);">
        <c:forTokens items="normal,non_reciprocal,related,equivalent" delims="," var="currentRelationship">
          <option value="${currentRelationship}" <c:if
              test="${currentRelationship == synonym.relationship}">selected="true"</c:if>>
            <fmt:message key="termdict_synonyms_table.relationship.${currentRelationship}"/>
          </option>
        </c:forTokens>
      </d:select>
    </admin-ui:column>

    <admin-ui:column title="termdict_synonyms_table.table.part_of_speech" type="sortable" name="speech">
      <d:input bean="TermFormHandler.partOfSpeech" name="partOfSpeech" type="hidden" value="${synonym.partOfSpeech}"/>
      <select class="small" name="partOfSpeechBean" id="speech"
              onchange="saveSelectBoxValue(this, 'partOfSpeech', true); setChanged(this); addEmptySynonymField(this);">
        <c:forTokens items="noun,verb,adjective,adverb,nounProper" delims="," var="currentPartOfSpeech">
          <option value="${currentPartOfSpeech}" <c:if
              test="${currentPartOfSpeech == synonym.partOfSpeech}">selected="true"</c:if>>
            <fmt:message key='termdict_synonyms_table.path.speech.${currentPartOfSpeech}'/>
          </option>
        </c:forTokens>
      </select>
    </admin-ui:column>
    
    <admin-ui:column title="termdict_synonyms_table.table.phrase_type" type="sortable" name="phrase">
      <d:input bean="TermFormHandler.phraseTypes" name="phraseTypes" type="hidden" value="${synonym.phraseType}"/>
      <select class="small" name="phraseTypesBean" id="phrase"
              onchange="saveSelectBoxValue(this, 'phraseTypes', true); setChanged(this); addEmptySynonymField(this);">
        <c:forTokens items="true,phr,xphr" delims="," var="currentPhraseType">
          <option value="${currentPhraseType}" <c:if
              test="${currentPhraseType == synonym.phraseType}">selected="true"</c:if>>
            <fmt:message key='termdict_synonyms_table.phrase.type.${currentPhraseType}'/>
          </option>
        </c:forTokens>
      </select>
    </admin-ui:column>

    <admin-ui:column type="sortable" title="termdict_synonyms_table.table.language" name="language">
      <d:getvalueof bean="/atg/searchadmin/atgsearchengine/LanguageConfigurationService.enabledLanguages"
                    var="allLanguages"/>

      <d:input bean="TermFormHandler.language" name="language" type="hidden" value="${synonym.language}"/>
      <select class="small" name="languageBean"
              onchange="saveSelectBoxValue(this, 'language', true); setChanged(this); addEmptySynonymField(this);">
        <c:forEach items="${allLanguages}" var="currentLanguage">
          <option value="${fn:escapeXml(currentLanguage)}" <c:if test="${fn:toLowerCase(currentLanguage) == fn:toLowerCase(synonym.language)}">
            selected="true"</c:if>>
            <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${currentLanguage}"/>
            ${localizedLanguage}
          </option>
        </c:forEach>
      </select>
    </admin-ui:column>

    <admin-ui:column type="sortable" title="termdict_synonyms_table.table.weight" name="weight">
      <div class="enableWeight" <c:if test="${!enableWeight}">style="display:none"</c:if>>
        <div class="enableWeightField" <c:if test="${synonym.relationship eq 'equivalent'}">style="display:none"</c:if>>
          <c:set var="synonymWeight" value="${synonym.weight}"/>
          <c:if test="${synonymWeight eq '-1'}">
            <c:set var="synonymWeight" value="0"/>
          </c:if>
          <d:input bean="TermFormHandler.weight" name="weight" value="${synonymWeight}" size="3" maxlength="3"
                   type="text" iclass="textField" onkeyup="addEmptySynonymField(this);"
                   onchange="addEmptySynonymField(this); setChanged(this);"/>
        </div>

        <div class="disableWeightField"
          <c:if test="${synonym.relationship ne 'equivalent'}">style="display:none"</c:if>>
          <input name="disWeight" value="" size="3" maxlength="3" disabled="true" class="textField"/>
        </div>
      </div>

      <div class="disableWeight" <c:if test="${enableWeight}">style="display:none"</c:if>>
        <fmt:message key="termdict_synonyms_table.weight.disable"/>
      </div>
    </admin-ui:column>

    <admin-ui:column type="icon" width="20">
      <a class="icon propertyDelete" href="#" onclick="return deleteSynonymField(this, '${synonym.id}');">del</a>
    </admin-ui:column>

  </admin-ui:table>

  <d:input type="submit" bean="TermFormHandler.sort" style="display:none" id="sortInput" name="sort" iclass="formsubmitter"/>
  <d:input type="hidden" bean="TermFormHandler.sortDirection" id="sortDirection" name="sortDirection"/>
  <d:input type="hidden" bean="TermFormHandler.sortColumn" id="sortColumn" name="sortColumn"/>
  <d:input type="hidden" bean="TermFormHandler.deletedIds" id="deletedIds" name="deletedIds"/>

  <script type="text/javascript">
    var relationIconTitles = {
      normal: '<fmt:message key="termdict_synonyms_table.relationship.tooltip.normal"/>',
      related: '<fmt:message key="termdict_synonyms_table.relationship.tooltip.related"/>',
      equivalent: '<fmt:message key="termdict_synonyms_table.relationship.tooltip.equivalent"/>',
      non_reciprocal: '<fmt:message key="termdict_synonyms_table.relationship.tooltip.non_reciprocal"/>'
    };

    initSynonymTable();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_synonyms_table.jsp#2 $$Change: 651448 $--%>

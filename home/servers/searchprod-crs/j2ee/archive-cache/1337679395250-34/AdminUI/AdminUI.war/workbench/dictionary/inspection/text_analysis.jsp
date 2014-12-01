<%--
"Term Lookup" dictionary inspection tab

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/text_analysis.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTextAnalysisFormHandler"/>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/scrolling.js"></script>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent" class="scrollingAreaContent">
      <br/>
      <d:form action="text_analysis.jsp" method="post">
        <c:url var="successUrl" value="${dictionaryPath}/inspection/text_analysis.jsp"/>
        <d:input type="hidden" bean="TermDictionaryInspectionTextAnalysisFormHandler.successURL" value="${successUrl}" name="successURL"/>

        <table class="form" cellspacing="0" cellpadding="0" id="inputPanel">
          <tbody>
            <tr>
              <td class="label">
                <fmt:message key="dictionary_inspection.text_analysis.sao_set"/></label>
              </td>
              <td>
                <d:select iclass="small unchanged" name="selectedSAOId"
                    onchange="document.getElementById('saoChangedSubmit').click()"
                    bean="TermDictionaryInspectionTextAnalysisFormHandler.selectedSAOId">
                  <admin-beans:getSAOSetsByActiveProject var="saoSets"/>
                  <c:forEach items="${saoSets}" var="SAOSet">
                    <d:option value="${SAOSet.id}">
                      <c:out value="${SAOSet.name}" />
                    </d:option>
                  </c:forEach>
                </d:select>
                <d:input bean="TermDictionaryInspectionTextAnalysisFormHandler.saoChanged" name="saoChanged"
                         id="saoChangedSubmit" type="submit" style="display:none" iclass="formsubmitter"/>
                <span class="ea"><tags:ea key="embedded_assistant.term_analysis_sao"/></span>
              </td>
            </tr>
            <tr>
              <td class="label">
                <fmt:message key="dictionary_inspection.text_analysis.search_language"/>
              </td>
              <td>
                <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${handler.language}"/>
                <c:out value="${localizedLanguage}" />
              </td>
            </tr>
            <tr>
              <td class="label">
                <fmt:message key="dictionary_inspection.text_analysis.result_language"/>
              </td>
              <td>
                <admin-beans:setTPOResourceBundle var="bundle" useSAO="true" />
                <fmt:message key="targetLanguage.values.${handler.resultLanguage}" bundle="${bundle}"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <fmt:message key="dictionary_inspection.text_analysis.dictionary_adaptors"/>
              </td>
              <td>
                <c:set var="adaptors">
                  <tags:join items="${handler.dictionaryAdaptors}" var="dictionaryAdaptor" delimiter=", ">
                    ${dictionaryAdaptor}
                  </tags:join>
                </c:set>
                <c:if test="${empty adaptors}">
                  <fmt:message key="dictionary_inspection.text_analysis.dictionary_adaptors.none"/>
                </c:if>
                <c:if test="${not empty adaptors}">
                  <c:out value="${adaptors}"/>
                </c:if>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="inputPanel">
          <table class="form" cellspacing="0" cellpadding="0">
            <tbody>
              <tr>
                <td class="label centered">
                  <fmt:message key="dictionary_inspection.text_analysis.text"/>
                </td>
                <td>
                  <d:input iclass="textField short unchanged" bean="TermDictionaryInspectionTextAnalysisFormHandler.text"
                    id="textInput" name="text" />
                  <fmt:message key="dictionary_inspection.text_analysis.analyze" var="analysisButtonLabel"/>
                  <d:input type="submit" bean="TermDictionaryInspectionTextAnalysisFormHandler.textAnalysis"
                    value="${analysisButtonLabel}" id="textAnalysisSubmit" name="textAnalysis" iclass="formsubmitter"/>
                  <span class="ea"><tags:ea key="embedded_assistant.text_analysis_text" /></span>
                </td>
              </tr>
            <tbody>
          </table>
        </div>
      </d:form>
      <%-- inspect response section --%>
      <c:if test="${handler.searchResponse != null and not empty handler.searchResponse.queryTerms.terms}">
        <h3><fmt:message key="dictionary_inspection.text_processing.results"/></h3>

        <table class="scrollableContainerTable" cellspacing="0" cellpadding="0">
          <tr><td id="titleColumnCell">
            <div class="scrollableTableContainer" id="scrollableTableContainer">
              <div class="scrollableTableAnywidth" id="scrollableTableAnywidth">
                <table class="data" cellspacing="0" cellpadding="0" id="scrollableTable">
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.text_title"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <c:set var="text" value="${fn:escapeXml(queryTerm.text)}"/>
                      <c:if test="${queryTerm.broken eq 'middle' or queryTerm.broken eq 'last'}">
                        <c:set var="text" value="(${text})"/>
                      </c:if>
                      <td><nobr>${text}</nobr></td>
                    </c:forEach>
                  </tr>
                  <%-- Boolean Restriction Item Row--%>
                  <c:if test="${handler.showOperatorRows[0]}">
                    <tr>
                      <th><nobr>
                        <span title="<fmt:message key='dictionary_inspection.text_analysis.boolean_restriction_note'/>">
                          <fmt:message key="dictionary_inspection.text_analysis.boolean_restriction_item"/>
                        </span>
                      </nobr></th>
                      <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                        <td><nobr>
                          <c:choose>
                            <c:when test="${queryTerm.op eq 'docrequired'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_boolean_restriction_item.required"/>
                            </c:when>
                            <c:when test="${queryTerm.op eq 'docnegative'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_boolean_restriction_item.not"/>
                            </c:when>
                            <c:when test="${queryTerm.op eq 'docreqdisj'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_boolean_restriction_item.or_required"/>
                            </c:when>
                            <c:otherwise>&nbsp;</c:otherwise>
                          </c:choose>
                        </nobr></td>
                      </c:forEach>
                    </tr>
                  </c:if>
                  <%-- Boolean Restriction Statement Row--%>
                  <c:if test="${handler.showOperatorRows[1]}">
                    <tr>
                      <th><nobr><fmt:message key="dictionary_inspection.text_analysis.boolean_restriction_statement"/></nobr></th> 
                      <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                        <td><nobr>
                          <c:choose>
                            <c:when test="${queryTerm.op eq 'required'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_boolean_restriction_statement.required"/>
                            </c:when>
                            <c:when test="${queryTerm.op eq 'negative'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_boolean_restriction_statement.not"/>
                            </c:when>
                            <c:otherwise>&nbsp;</c:otherwise>
                          </c:choose>
                        </nobr></td>
                      </c:forEach>
                    </tr>
                  </c:if>
                  <%-- Phrase Match Row--%>
                  <c:if test="${handler.showOperatorRows[2]}">
                    <tr>
                      <th><nobr><fmt:message key="dictionary_inspection.text_analysis.phrase_match"/></nobr></th>
                      <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                        <td><nobr>
                          <c:choose>
                            <c:when test="${queryTerm.quote eq 'begin'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_phase_match.begin"/>
                            </c:when>
                            <c:when test="${queryTerm.quote eq 'middle'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_phase_match.middle"/>
                            </c:when>
                            <c:when test="${queryTerm.quote eq 'end'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_phase_match.end"/>
                            </c:when>
                            <c:otherwise>&nbsp;</c:otherwise>
                          </c:choose>
                        </nobr></td>
                      </c:forEach>
                    </tr>
                  </c:if>
                  <%-- Literal Match Row--%>
                  <c:if test="${handler.showOperatorRows[3]}">
                    <tr>
                      <th><nobr><fmt:message key="dictionary_inspection.text_analysis.literal_match"/></nobr></th>
                      <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                        <td><nobr>
                          <c:if test="${not empty queryTerm.quote}">
                            <fmt:message key="dictionary_inspection.text_analysis.operator_literal_match.yes"/>
                          </c:if>
                          <c:if test="${empty queryTerm.quote}">&nbsp;</c:if>
                        </nobr></td>
                      </c:forEach>
                    </tr>
                  </c:if>
                  <%-- Case Match Row--%>
                  <c:if test="${handler.showOperatorRows[4]}">
                    <tr>
                      <th><nobr><fmt:message key="dictionary_inspection.text_analysis.case_match"/></nobr></th>
                      <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                        <td><nobr>
                          <c:choose>
                            <c:when test="${queryTerm.case eq 'lower'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_case_match.lower"/>
                            </c:when>
                            <c:when test="${queryTerm.case eq 'upper'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_case_match.upper"/>
                            </c:when>
                            <c:when test="${queryTerm.case eq 'mixed'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_case_match.mixed"/>
                            </c:when>
                            <%-- TODO not sure if I have to show 'none' or empty cell --%>
                            <c:when test="${queryTerm.case eq 'none'}">
                              <fmt:message key="dictionary_inspection.text_analysis.operator_case_match.none"/>
                            </c:when>
                            <c:otherwise>&nbsp;</c:otherwise>
                          </c:choose>
                        </nobr></td>
                      </c:forEach>
                    </tr>
                  </c:if>
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.index_term"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <td><nobr>
                        <c:if test="${queryTerm.wildcard}">
                          <fmt:message key="dictionary_inspection.text_analysis.index_term.wildcard" /><br/>
                        </c:if>
                        <c:forEach items="${queryTerm.stems}" var="stem">
                          ${fn:escapeXml(stem.stem)}
                          <c:if test="${not empty stem.lang && stem.lang != handler.language}">
                            <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${stem.lang}"/>
                            (<c:out value="${localizedLanguage}" />)
                          </c:if>
                          <br/>
                        </c:forEach>
                        <c:if test="${empty queryTerm.stems}">&nbsp;</c:if>
                      </nobr></td>
                    </c:forEach>
                  </tr>
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.term_source"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <td><nobr>
                        <c:forEach items="${queryTerm.stems}" var="stem">
                          <c:forEach items="${stem.source}" var="sourceValue">
                            ${fn:escapeXml(sourceValue)}<br/>
                          </c:forEach>
                        </c:forEach>
                        <c:if test="${empty queryTerm.stems}">&nbsp;</c:if>
                      </nobr></td>
                    </c:forEach>
                  </tr>
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.term_weight"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <td>${fn:escapeXml(queryTerm.weight)}</td>
                    </c:forEach>
                  </tr>
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.number_of_items"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <td>${fn:escapeXml(queryTerm.freq)}</td>
                    </c:forEach>
                  </tr>
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.spelling_suggestions"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <td><nobr>
                        <c:forEach items="${queryTerm.corrections}" var="correction">
                          ${fn:escapeXml(correction)}<br/>
                        </c:forEach>
                        <c:if test="${empty queryTerm.corrections}">&nbsp;</c:if>
                      </nobr></td>
                    </c:forEach>
                  </tr>
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.term_expansions"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <td><nobr>
                        <c:forEach items="${queryTerm.expansions.expansionEntries}" var="expansion">
                          ${fn:escapeXml(expansion.toExpansion)}
                          <c:if test="${not empty expansion.lang && expansion.lang != handler.language}">
                            <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${expansion.lang}"/>
                            (<c:out value="${localizedLanguage}" />)
                          </c:if>
                          <br/>
                        </c:forEach>
                        <c:if test="${empty queryTerm.expansions.expansionEntries}">&nbsp;</c:if>
                      </nobr></td>
                    </c:forEach>
                  </tr>
                  <tr>
                    <th><nobr><fmt:message key="dictionary_inspection.text_analysis.term_expansions_source"/></nobr></th>
                    <c:forEach items="${handler.searchResponse.queryTerms.terms}" var="queryTerm">
                      <td><nobr>${empty queryTerm.expansions.expansionEntries ? '&nbsp;' : fn:escapeXml(queryTerm.context)}</nobr></td>
                    </c:forEach>
                  </tr>
                </table>
              </div>
            </div>
          </td>
          <td id="contentCell">
            <div class="scrollingArea" id="scrollingArea">
              <div class="scrollingAreaWindow" id="scrollingAreaWindow">
                <div class="scrollingAreaContent" id="scrollingAreaContent"></div></div></div></td></tr>
        </table>
        <c:if test="${handler.showOperatorRows[0]}">
          <fmt:message key="dictionary_inspection.text_analysis.boolean_restriction_note"/>
        </c:if>
      </c:if>
    </div>
  </div>
  <script type="text/javascript">
    //refreshes right pane after changes in selection of active search project
    function refreshRightPane(obj) {
      loadRightPanel('${pageContext.request.contextPath}${dictionaryPath}/inspection/introduction.jsp');
    }
    function customLoad() {
      tableScrolling.init();
    }
    function focusFormElement() {
      document.getElementById("textInput").focus();
    }
    document.getElementById("textInput").onkeydown = function(e) {
      e = e || window.event;
      if (e.keyCode == 13) {
          document.getElementById('textAnalysisSubmit').click(); 
          return false;
      }
    };
    disableButtonWhenFieldIsEmpty("textAnalysisSubmit", "textInput");
    
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictInspectNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/text_analysis.jsp#2 $$Change: 651448 $--%>

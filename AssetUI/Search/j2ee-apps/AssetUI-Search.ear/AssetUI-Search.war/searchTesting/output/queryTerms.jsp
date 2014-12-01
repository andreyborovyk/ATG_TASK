<%--
  Query Terms section of the output.

  Included into results.jsp.

  The following parameters are passed into the page:

  @param searchResponse         search response

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/queryTerms.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="assetui-search"
    uri="http://www.atg.com/taglibs/assetui-search"%>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="searchResponse" param="searchResponse"/>

  <c:if test="${not (empty searchResponse.diagnostics.queryTerms.terms)}">

    <c:if test="${altFieldSet}">
      <fieldset class="altGroup">
    </c:if>
    <c:if test="${not altFieldSet}">
      <fieldset>
    </c:if>
    <c:set var="altFieldSet" value="${!altFieldSet}" scope="request" />

      <legend><span><fmt:message key="searchTestingResultQueryTerms.legend"/></span></legend>
      <br/>
      <table class="atg_dataTable atg_smerch_summaryTable" >
        <tr>
          <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultQueryTerms.table.header.queryTerm"/></th>
          <c:choose>
            <c:when test="${searchResponse.autoSpell}">
              <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultQueryTerms.table.header.autocorrection"/></th>
            </c:when>
            <c:otherwise>
              <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultQueryTerms.table.header.suggestions"/></th>
            </c:otherwise>
          </c:choose>
          <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultQueryTerms.table.header.operator"/></th>
          <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultQueryTerms.table.header.search"/></th>
          <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultQueryTerms.table.header.dictionary"/></th>
        </tr>
        <c:forEach items="${searchResponse.diagnostics.queryTerms.terms}" var="queryTerm" varStatus="status">
            <c:choose>
            <c:when test="${0 == (status.index mod 2)}">
              <tr class="atg_altRow">
            </c:when>
            <c:otherwise>
              <tr>
            </c:otherwise>
          </c:choose>
            <td>
              <c:out value="${queryTerm.text}"/>
            </td>
            <td>
              <c:choose>
                <c:when test="${!searchResponse.autoSpell and !empty searchResponse.spellingTerms}">
                  <c:forEach items="${searchResponse.spellingTerms}" var="spellingTerm">
                    <c:if test="${queryTerm.text eq spellingTerm.text}">
                      <c:set var="hasSuggestion" value="${false}" />
                      <c:forEach items="${spellingTerm.suggestions}" var="suggestion">
                        <c:if test="${suggestion.freq gt 0}">
                          <c:set var="hasSuggestion" value="${true}" />
                        </c:if>
                      </c:forEach>
                      <c:choose>
                        <c:when test="${hasSuggestion}">
                          <ul>
                            <c:forEach items="${spellingTerm.suggestions}" var="suggestion">
                              <c:if test="${suggestion.freq gt 0}">
                                <li><c:out value="${suggestion.value}"/></li>
                              </c:if>
                            </c:forEach>
                          </ul>
                        </c:when>
                        <c:otherwise>
                          <fmt:message key="searchTestingResultQueryTerms.emptyCorrection"/>
                        </c:otherwise>
                      </c:choose>

                    </c:if>
                  </c:forEach>
                </c:when>
                <c:when test="${searchResponse.autoSpell and not (empty queryTerm.correction)}">
                  <c:out value="${queryTerm.correction}"/>
                </c:when>
                <c:otherwise>
                  <fmt:message key="searchTestingResultQueryTerms.emptyCorrection"/>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <ul>
                <c:set var="op" value="${queryTerm.op}"/>
                <c:choose>
                  <c:when test="${'required' eq op}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.requiredInStatement"/></li>
                  </c:when>
                  <c:when test="${'docrequired' eq op}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.required"/></li>
                  </c:when>
                  <c:when test="${'docreqdisj' eq op}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.requiredLeastOne"/></li>
                  </c:when>
                  <c:when test="${'negative' eq op}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.excludeFromStatement"/></li>
                  </c:when>
                  <c:when test="${'docnegative' eq op}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.exclude"/></li>
                  </c:when>
                </c:choose>
                <c:set var="case" value="${queryTerm.case}"/>
                <c:choose>
                  <c:when test="${'lower' eq case}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.lowerCaseOnly"/></li>
                  </c:when>
                  <c:when test="${'upper' eq case}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.upperCaseOnly"/></li>
                  </c:when>
                  <c:when test="${'mixed' eq case}">
                    <li><fmt:message key="searchTestingResultQueryTerms.operator.mixedCaseOnly"/></li>
                  </c:when>
                </c:choose>
                <c:if test="${not (queryTerm.morph)}">
                  <li><fmt:message key="searchTestingResultQueryTerms.operator.literal"/></li>
                </c:if>
                <c:if test="${queryTerm.ordered}">
                  <li><fmt:message key="searchTestingResultQueryTerms.operator.ordered"/></li>
                </c:if>
                <c:set var="isAdjacent" value="${0 == queryTerm.adjacency}"/>
                <c:if test="${isAdjacent}">
                  <li><fmt:message key="searchTestingResultQueryTerms.operator.adjacent"/></li>
                </c:if>
              </ul>
            </td>
            <td>

              <assetui-search:getContainerSize var="stemCount"
                  container="${queryTerm.stems}"/>
              <assetui-search:getContainerSize var="entryCount"
                  container="${queryTerm.expansions.expansionEntries}"/>
              <assetui-search:getContainerSize var="fromCount"
                  container="${queryTerm.expansions.expansionEntries[0].fromSourceTerms}"/>
              <c:set var="hasSingleStem"
                  value="${1 == stemCount}"/>
              <c:set var="isSourceStemIdentical"
                  value="${(1 == entryCount)
                      and (1 == fromCount)
                      and ('identity' eq queryTerm.expansions.expansionEntries[0].fromSourceTerms[0].type)}"/>

              <c:set var="hasStems"
                  value="${stemCount != 0}"/>
              <c:set var="hasSpelling"
                  value="${not (empty queryTerm.spelling)}"/>
              <c:set var="isZeroFreq"
                  value="${0 == queryTerm.freq}"/>
                  
                <c:choose>

                    <%--
                        GK: Condition 7 - term not in content, no spelling?
                          This doesn't make sense. This seems to be related to just if the term is known
                          or not and spelling didn't correct it.
                          Many terms are not in the content, but are correctly spelled.
        
                          a. An unknown and uncorrected term will have no <stem> entries
                          b. A unknown and CORRECTED term will have a <stem> and spelling= attribute (as discussed)
                          c. A known term not in the content (including synonyms) will have <term freq="0"
                          d. A known term not in content, but with synonyms in it, will have <entry docfreq="0">
                            for its stem, but other <entry docfreq="x" entries. Its <term freq="x" will be non zero
                    --%>
                    <c:when test="${not searchResponse.autoSpell and ((not hasStems) or (hasStems and hasSpelling) or (isZeroFreq))}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.termNotInContent"/>
                    </c:when>

                    <%--
                        GK: Condition 8 - stop word
                          This will have weight=0, although that value could change.
                          Maybe I should mark a term that is below the indexStopThresh.
                          I see I have op="stopword", but that is used for skipped unknown terms.
                          I added op="excluded" for that, and use op="stopword" for terms less then the threshold
                          (currently no terms).
                    --%>
                    <c:when test="${(0 == queryTerm.weight) or ('excluded' eq op)}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.commonTermIgnored"/>
                    </c:when>

                    <%--
                        GK: Condition 9 - Spelling suggestion
                          We discussed this separately  (spelling="wintertree|internal" plus new <correction>)
                    --%>
                    <c:when test="${searchResponse.autoSpell and ((hasSpelling) and (not (empty queryTerm.correction)))}">
                      <c:if test="${queryTerm.morph}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.formsOfStem"/>
                        <ul>
                          <c:forEach items="${queryTerm.stems}" var="stem">
                            <li><c:out value="${stem.stem}"/></li>
                          </c:forEach>
                        </ul>
                      </c:if>
                      
                      <c:if test="${queryTerm.expand and queryTerm.morph and not empty queryTerm.expansions.expansionEntries}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.formsOfSynonyms"/>
                          <ul>
                            <c:forEach items="${queryTerm.expansions.expansionEntries}" var="expansionEntry">
                              <li><c:out value="${expansionEntry.toExpansion}"/></li>
                            </c:forEach>
                          </ul>
                      </c:if>
                      
                      <c:if test="${not (empty queryTerm.norm)}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.formsOfNormalizedTerm"/>
                        <ul>
                          <li><c:out value="${queryTerm.norm}"/></li>
                        </ul>
                      </c:if>
                    </c:when>

                    <%--
                        GK: Condition 10 - No forms of term
                          bMorph = true and op = negative | docnegative
                    --%>

                    <%--
                        GK: Condtion 11 - No occurrance of term
                          bMorph = false and op = negative | docnegative
                          I would change this to NO occurrance of EXACT term
                    --%>
                    <c:when test="${('negative' eq op) or ('docnegative' eq op)}">
                        <c:choose>
                            <c:when test="queryTerm.morph">
                                <fmt:message key="searchTestingResultQueryTerms.condition.noFormsOfStem"/>
                            </c:when>
                            <c:otherwise>
                                <fmt:message key="searchTestingResultQueryTerms.condition.noOccurrenceOfTerm"/>
                            </c:otherwise>
                        </c:choose>
                        <ul>
                          <c:forEach items="${queryTerm.stems}" var="stem">
                            <li><c:out value="${stem.stem}"/></li>
                          </c:forEach>
                        </ul>
                    </c:when>

                    <%--
                        GK: Condition 12 - Wildcard matches:
                          Use all of <expansion><entry><to> to get the matches
                    --%>
                    <c:when test="${queryTerm.wildcard}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.wildcardMatches"/>
                        <ul>
                          <c:forEach items="${queryTerm.stems}" var="stem">
                            <li><c:out value="${stem.stem}"/></li>
                          </c:forEach>
                        </ul>
                    </c:when>

                    <%--
                        GK: Condition 1 - Exact Term
                          If bExpand AND bMorph are false, (due to double quotes) then it was matched exactly.
                          Should just have one <stem>, one <expansion><entry> holding same stem (type="identity").
                    --%>
        
                    <%--
                        GK: Condition 2 - Exact Term (compound)
                          Should be same as #1, compound term is irrelevant
                    --%>
                    <c:when test="${(not queryTerm.expand) and (not queryTerm.morph) and (hasSingleStem)}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.exactTerm"/>
                        <ul>
                            <li><c:out value="${queryTerm.stems[0].stem}"/></li>
                        </ul>
                    </c:when>

                    <%--
                        GK: Condition 3 - Forms of stem
                          If bMorph is true, then it matches the forms of the <stem>
                    --%>
        
                    <%--
                        GK: Condition 4 - Forms of synonyms
                          If bMorph AND bExpand is true, then it matches forms of expansions (synonyms).
                    --%>
        
                    <%--
                        GK: Condition 5 - equivalent synonym
                          I added a <norm> element for this. The <stem> will hold the stems of the normalized form.
                    --%>
        
                    <%--
                        GK: Condition 6 - Combination
                          generally 3 and 4 and 5 can be combined.
                    --%>
                    <c:otherwise>
                      <c:if test="${queryTerm.morph}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.formsOfStem"/>
                        <ul>
                          <c:forEach items="${queryTerm.stems}" var="stem">
                            <li><c:out value="${stem.stem}"/></li>
                          </c:forEach>
                        </ul>
                      </c:if>
                      
                      <c:if test="${queryTerm.expand and queryTerm.morph and not empty queryTerm.expansions.expansionEntries}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.formsOfSynonyms"/>
                          <ul>
                            <c:forEach items="${queryTerm.expansions.expansionEntries}" var="expansionEntry">
                              <li><c:out value="${expansionEntry.toExpansion}"/></li>
                            </c:forEach>
                          </ul>
                      </c:if>
                      
                      <c:if test="${not (empty queryTerm.norm)}">
                        <fmt:message key="searchTestingResultQueryTerms.condition.formsOfNormalizedTerm"/>
                        <ul>
                          <li><c:out value="${queryTerm.norm}"/></li>
                        </ul>
                      </c:if>
                    </c:otherwise>

                </c:choose>

            </td>
            <td>
              <c:out value="${searchResponse.parserOptions.language}"/>
              <c:if test="${not (empty queryTerm.context)}">
                <fmt:message key="searchTestingResultQueryTerms.language">
                  <fmt:param><c:out value="${queryTerm.context}"/></fmt:param>
                </fmt:message>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
    </fieldset>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/queryTerms.jsp#2 $$Change: 651448 $--%>

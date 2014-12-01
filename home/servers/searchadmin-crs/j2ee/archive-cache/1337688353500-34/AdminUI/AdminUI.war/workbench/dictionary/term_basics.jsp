<%--
  JSP provides fields for creation/edition of the term

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_basics.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler" var="handler"/>
  <h3>
    <fmt:message key="term_basics.title.basics"/>
  </h3>
  <table class="form" cellspacing="0" cellpadding="0">
    <tr>
      <td class="label">
        <span id="termAlert">
          <span class="required"><fmt:message key="project_general.required_field"/></span>
        </span>
        <label for="term">
          <fmt:message key="term_basics.term"/>
        </label>
      </td>
      <td>
        <d:input type="text" iclass="textField" bean="TermFormHandler.term" name="term"/>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="term_basics.term.path"/>
      </td>
      <td>
        <c:out value="${handler.path}"/>
        <c:choose>
          <c:when test="${empty handler.termId}">
            <fmt:message key="term_basics.new.term.path"/>
          </c:when>
          <c:otherwise>
            <c:out value="${handler.term}"/>
          </c:otherwise>
        </c:choose>
        <span class="ea"><tags:ea key="embedded_assistant.term_basics.term_path" /></span>
      </td>
    </tr>
    <c:if test="${!handler.top}">
      <tr>
        <td class="label">
          <fmt:message key="term_basics.new.term.propagate"/>
        </td>
        <td>
          <d:input type="checkbox" bean="TermFormHandler.propagate" id="propagate" name="propagate"/>
          <fmt:message key="term_basics.new.term.propagate.description"/>
        </td>
      </tr>
    </c:if>
    <tr>
      <td class="label">
        <fmt:message key="term_basics.term.path.speech"/>
      </td>
      <td>
        <d:select id="pathSpeech" iclass="small" bean="TermFormHandler.partSpeech" name="partSpeech">
          <c:forTokens items="noun,verb,adjective,adverb,nounProper" delims="," var="pos">
            <d:option value="${pos}"><fmt:message key="term_basics.path.speech.${pos}"/></d:option>
          </c:forTokens>
        </d:select>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="term_basics.term.phrase.type"/>
      </td>
      <td>
        <d:select id="pathSpeech" iclass="small" bean="TermFormHandler.phraseType" name="phraseType">
          <c:forTokens items="true,phr,xphr" delims="," var="pos">
            <d:option value="${pos}"><fmt:message key="term_basics.phrase.type.${pos}"/></d:option>
          </c:forTokens>
        </d:select>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="term_basics.weight"/>
      </td>
      <td>
        <d:input bean="TermFormHandler.enableWeight" type="hidden" name="enableWeight" id="enableWeight"/>
        <d:getvalueof bean="TermFormHandler.enableWeight" var="enableWeight"/>
        <span id="enableSynonymWeight"
            <fmt:message var="disableTooltip" key="term_basics.disable.tooltip"/>
            <c:if test="${!enableWeight}">style="display:none"</c:if>
            >
          <d:input type="text" iclass="textField number" bean="TermFormHandler.termWeight"
                   name="termWeight"/>
          <a href="#" onclick="return enableDisableWeights(false);" title="${disableTooltip}">
            <fmt:message key="term_basics.disable"/>
          </a>
        </span>

        <span id="disableSynonymWeight"
            <fmt:message var="enableTooltip" key="term_basics.enable.tooltip"/>
            <c:if test="${enableWeight}">style="display:none"</c:if>
            >
          <fmt:message key="term_basics.disabled"/>
          <a href="#" onclick="return enableDisableWeights(true);" title="${enableTooltip}">
            <fmt:message key="term_basics.enable"/>
          </a>
        </span>
        <span class="ea"><tags:ea key="embedded_assistant.term_basics.weight" /></span>
      </td>
    </tr>
  </table>
  <script type="text/javascript">
    function enableDisableWeights(enable) {
      setChangeFlag();
      document.getElementById('enableWeight').value = enable;
      var enableDiv = document.getElementById("enableSynonymWeight");
      var disableDiv = document.getElementById("disableSynonymWeight");
      if (enable) {
        enableDiv.style.display = "inline";
        disableDiv.style.display = "none";
      }
      else {
        enableDiv.style.display = "none";
        disableDiv.style.display = "inline";
      }
      // call javascript on termdict_synonyn_table.jsp
      enableDisableTableWeights();
      return false;
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_basics.jsp#2 $$Change: 651448 $--%>

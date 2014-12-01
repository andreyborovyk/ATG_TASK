<%--
Delete macros popup.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/macros_delete_popup.jsp#2 $$Change: 651448 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="id" var="id" />
  <d:getvalueof param="level" var="level" />

  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/macros.js"></script>

  <form>
    <c:if test="${level eq 'global'}">
      <topic:globalMacroFindByPrimaryKey globalMacroId="${id}" var="macro"/>
    </c:if>
    <c:if test="${level eq 'local'}">
      <topic:topicMacroFindByPrimaryKey topicMacroId="${id}" var="macro"/>
    </c:if>
    <c:if test="${level eq 'globalQueryRuleSetMacro'}">
      <queryrule:globalQueryRuleSetMacroFindByPrimaryKey globalQueryRuleSetMacroId="${id}" var="macro"/>
    </c:if>
    <div class="content">
      <p>
        <strong>
          <fmt:message key="macros_delete_popup.question">
            <fmt:param>
              <c:out value="${macro.name}"/>
            </fmt:param>
          </fmt:message>
        </strong>
      </p>
    </div>
    <div class="footer" id="popupFooter">
      <input type="button" value="<fmt:message key='macros_delete_popup.button.delete'/>"
        onclick="deleteMacroFieldById('deleteMacrosLink_${macro.id}', ${macro.id});closePopUp();"
        title="<fmt:message key='macros_delete_popup.button.delete.tooltip'/>"/>
      <input type="button" value="<fmt:message key='macros_delete_popup.button.cancel'/>"
        onclick="closePopUp()" title="<fmt:message key='macros_delete_popup.button.cancel.tooltip'/>"/>
    </div>
  </form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/macros_delete_popup.jsp#2 $$Change: 651448 $--%>

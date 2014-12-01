<%--
Delete topic rule popup.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_rule_delete_popup.jsp#2 $$Change: 651448 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="id" var="id" />

  <form>
    <topic:topicPatternFindByPrimaryKey topicPatternId="${id}" var="pattern"/>
    <div class="content">
      <p>
        <strong>
          <fmt:message key="topic_rule_delete_popup.question">
            <fmt:param>
              <c:out value="${pattern.pattern}"/>
            </fmt:param>
          </fmt:message>
        </strong>
      </p>
    </div>
    <div class="footer" id="popupFooter">
      <input type="button" value="<fmt:message key='topic_rule_delete_popup.button.delete'/>"
        onclick="deleteFieldById('deleteRuleLink_${id}');closePopUp();" title="<fmt:message key='topic_rule_delete_popup.button.delete.tooltip'/>"/>
      <input type="button" value="<fmt:message key='topic_rule_delete_popup.button.cancel'/>"
        onclick="closePopUp()" title="<fmt:message key='topic_rule_delete_popup.button.cancel.tooltip'/>"/>
    </div>
  </form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_rule_delete_popup.jsp#2 $$Change: 651448 $--%>

<%--
  JSP,  Patterns Macros tab

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/macros_edit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/macros.js"></script>

  <%-- Name FormHandler for storing macros --%>
  <d:getvalueof param="formHandlerName" var="formHandlerName" />
  <%-- The resource bundle key for showing macros count message --%>
  <d:getvalueof param="macroMessage" var="macroMessage" />
  <d:getvalueof param="level" var="level" />
  <d:getvalueof bean="${formHandlerName}.sortValue" var="sortValue"/>
  <d:importbean bean="${formHandlerName}" var="macrosFormHandler" />

  <p>
    <strong>
      <d:getvalueof var="macrosSize" bean="${formHandlerName}.macrosSize"/>
      <fmt:message key="${macroMessage}">
        <fmt:param value="${macrosSize}"/>
      </fmt:message>
    </strong>
  </p>

  <div id="macrosEditTable">

    <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="macro" items="${macrosFormHandler.macroBeans}"
                  sort="${sortValue}" onSort="tableOnSort"
                  tableId="editMacroTable" emptyRow="true">

      <admin-ui:column title="topic_sets.table.name" type="sortable" width="50%" name="macro">
        <d:input bean="${formHandlerName}.macroName" name="macroName" value="${macro.name}" type="sortable"
                 iclass="textField" onkeyup="addEmptyField(this); setMacroUpdeted(this);" onchange="addEmptyField(this); setMacroUpdeted(this);" style="width:95%" size="20"/>
        <d:input bean="${formHandlerName}.macroId" name="macroId" value="${macro.id}" type="hidden" />
        <d:input type="hidden" bean="${formHandlerName}.macroUpdated" value="${macro.updated}"
                 name="macroUpdated" iclass="macroChanged"/>
      </admin-ui:column>

      <admin-ui:column title="topic_sets.table.definition" type="sortable" width="50%" name="definition">
        <d:input bean="${formHandlerName}.macroDefinition" name="macroDefinition" value="${macro.definition}" type="text"
                 iclass="textField" onkeyup="addEmptyField(this); setMacroUpdeted(this);" onchange="addEmptyField(this); setMacroUpdeted(this);" style="width:95%" size="20"/>
      </admin-ui:column>

      <admin-ui:column type="icon" width="20">
        <fmt:message key="macros_edit.tooltip.delete" var="deleteTitle"/>
        <c:if test="${!empty macro.id}">
          <c:url value="/workbench/macros_delete_popup.jsp" var="popUrl">
            <c:param name="id" value="${macro.id}"/>
            <c:param name="level" value="${level}"/>
          </c:url>
          <a name="deleteMacrosLink_${macro.id}" id="deleteMacrosLink_${macro.id}" class="icon propertyDelete" 
             title="${deleteTitle}" href="${popUrl}" onclick="return showPopUp(this.href);">del</a>
        </c:if>
        <c:if test="${empty macro.id}">
          <a class="icon propertyDelete" title="${deleteTitle}" href="#"
             onclick="return deleteMacroField(this, '${macro.id}');">del</a>
        </c:if>
      </admin-ui:column>
    </admin-ui:table>
  </div>

  <d:input type="submit" bean="${formHandlerName}.sort" style="display:none" id="sortInput" name="sort" iclass="formsubmitter"/>
  <d:input type="hidden" bean="${formHandlerName}.sortDirection" id="sortDirection" name="sortDirection"/>
  <d:input type="hidden" bean="${formHandlerName}.sortColumn" id="sortColumn" name="sortColumn"/>
  <d:input type="hidden" bean="${formHandlerName}.deletedIds" name="deletedIds" id="deletedIds" />
  <d:input type="hidden" bean="${formHandlerName}.needInitialization" name="needInitialization" id="needInitialization"/>

  <script type="text/javascript">
    initTable(document.getElementById("editMacroTable"));
  </script>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/macros_edit.jsp#2 $$Change: 651448 $--%>

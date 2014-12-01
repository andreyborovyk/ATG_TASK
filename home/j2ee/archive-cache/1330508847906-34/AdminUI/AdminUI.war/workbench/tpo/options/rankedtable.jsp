<%--
JSP, used to show ranked table TPO type. Used to display dictionary adaptors SAO option.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/rankedtable.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="handlerName" param="handlerName"/>
  <d:getvalueof var="handler" param="handler"/>
  <d:getvalueof var="optionDef" param="optionDef"/>
  <d:getvalueof var="valuesProp" param="valuesProp"/>

  <%-- TPO localization --%>
  <d:getvalueof var="bundle" param="bundle"/>

  <c:set var="optionName" value="options.${optionDef.name}.${valuesProp}"/>
  <c:set var="optionValue" value="${handler.options[optionDef.name][valuesProp][0]}" />

  <c:if test="${not empty optionDef.values}">
    <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
            var="value" tableId="${optionName}.view" items="${optionDef.values}">
      <admin-ui:column type="checkbox" title="rankedtable.table.enable">
        <input type="checkbox" id="${value.text}_checkbox" name="checkbox" 
          onchange="enableInput('${value.text}');"/>
      </admin-ui:column>
      <admin-ui:column type="static" title="rankedtable.table.rank">
        <input type="text" class="textField" id="${value.text}_input" 
          disabled="true" name="input"/>
      </admin-ui:column>
      <admin-ui:column type="static" title="rankedtable.table.adaptor_name">
        <input type="hidden" value="${value.text}" id="${value.text}_hidden" name="hidden"/>
        <fmt:message key="${optionDef.name}.values.${value.text}" bundle="${bundle}"/>
      </admin-ui:column>
    </admin-ui:table>
    
    <div id="${optionName}.options" style="display:none;visibility:hidden;">
      <c:forEach items="${handler.options[optionDef.name][valuesProp]}" var="value">
        <d:input type="hidden"
                 bean="${handlerName}.${optionName}"
                 name="${optionName}"
                 value="${value}" />
      </c:forEach>
    </div>
    
    <input type="button"
          value="<fmt:message key='rankedtable.button.reorder'/>"
          title="<fmt:message key='rankedtable.button.reorder.tooltip'/>"
          onclick="reorderTable();"/>
    
    <script type="text/javascript">
      registerTPOView('${optionName}.view', '${optionName}');
    </script>  
  </c:if>
  <c:if test="${empty optionDef.values}">
    <fmt:message key="sao_set.details.not_available"/>
  </c:if>
    
  <script type="text/javascript">
    function initializeRankedTableRow(option, index){
      var checkbox;
      var rank;
      checkbox = document.getElementById(option + "_checkbox");
      if (checkbox != null){
        checkbox.checked = true;
      }
      rank = document.getElementById(option + "_input");
      if (rank != null){
        rank.disabled = false;
        rank.value = index;
      }
    }
    
    function reorderTable(){
      var checkboxes = document.getElementsByName("checkbox");
      var inputs = document.getElementsByName("input");
      for (var i = 0; i < inputs.length; i++){
        if (checkboxes[i].checked){
          var input = inputs[i];
          if ((isNaN(parseInt(input.value)))){
            checkboxes[i].checked = false;
            input.disabled = true;
          } else {
            input.value = parseInt(input.value);
          }
        } 
      }
      for (var i = 0; i < inputs.length; i++){
        for (var j = inputs.length - 1; j > i; j--){
          if (compareRanks(checkboxes[j-1].checked, inputs[j-1].value, 
              checkboxes[j].checked, inputs[j].value)){
              moveField(inputs[j], true);
          }
        }
      }
      for (var i = 0; i < inputs.length; i++){
        if (checkboxes[i].checked){
          inputs[i].value = i + 1;
        } else {
          break;
        }
      }
    }
    
    function compareRanks(firstEnable, firstRank, secondEnable, secondRank){
      if (!secondEnable){
        return false;
      }
      else{
        if (!firstEnable){
          return true;
        }
        else{
          if (parseInt(firstRank) > parseInt(secondRank)){
            return true;
          }
          else{
            return false;
          }
        }
      }
    }
    
    function enableInput(option){
      var checkbox = document.getElementById(option + "_checkbox");
      var input = document.getElementById(option + "_input");
      if (checkbox.checked){
        input.disabled = false;
      } else {
        input.disabled = true;
      }
    }
  </script>
  
  <c:forEach items="${handler.options[optionDef.name][valuesProp]}" var="value" varStatus="valueIndex">
    <c:if test="${not empty value}">
      <script type="text/javascript">
        initializeRankedTableRow("${value}", "${valueIndex.index + 1}");
      </script>
    </c:if>
  </c:forEach>
  <c:if test="${not empty optionDef.values}">
    <script type="text/javascript">
      reorderTable();
    </script>
  </c:if>
  
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/rankedtable.jsp#1 $$Change: 651360 $--%>

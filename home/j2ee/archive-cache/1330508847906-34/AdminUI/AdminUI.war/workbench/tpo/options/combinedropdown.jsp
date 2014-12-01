<%--
JSP, used to show textarea TPO type.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/combinedropdown.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="handlerName" param="handlerName"/>
  <d:getvalueof var="handler" param="handler"/>
  <d:getvalueof var="optionDef" param="optionDef"/>
  <d:getvalueof var="valuesProp" param="valuesProp"/>
  
  <c:set var="optionName" value="options.${optionDef.name}.${valuesProp}"/>
  
  <c:forEach items="${handler.options[optionDef.name][valuesProp]}" var="value" varStatus="status">
    <c:if test="${status.first}">
      <c:set var="optionValue" value="${value}"/>
    </c:if>
    <c:if test="${!status.first}">
      <c:set var="optionValue" value="${optionValue}; ${value}"/>
    </c:if>
  </c:forEach>
  <div id="<c:out value='${optionName}'/>.inputs">
    <c:forEach items="${handler.options[optionDef.name][valuesProp]}" var="value">
      <c:set var="value">
        <c:out value="${value}"/>
      </c:set>
      <d:input bean="${handlerName}.${optionName}"
               type="hidden" name="${optionName}"
               value="${value}"/>
    </c:forEach>
  </div>
  <select id="${optionName}.combined" class="small" 
    onclick="setChangeFlag();tpoCombineDropdownChange('${optionName}');">
    <c:forEach items="${optionDef.values}" varStatus="cursor">
      <c:set var="length" value="${fn:length(optionDef.values) - cursor.index - 1}"/>
      <c:forEach items="${optionDef.values}" var="value" varStatus="status" begin="0" end="${length}">
        <c:if test="${status.first}">
          <c:set var="currentValue" value="${value.text}"/>
        </c:if>
        <c:if test="${!status.first}">
          <c:set var="currentValue" value="${currentValue}; ${value.text}"/>
        </c:if>
      </c:forEach>
      <option value="${currentValue}" 
        <c:if test="${fn:toLowerCase(optionValue) == fn:toLowerCase(currentValue)}">selected="true"</c:if> >
        <c:out value="${currentValue}"/>
      </option>
    </c:forEach>
  </select>
  
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/combinedropdown.jsp#2 $$Change: 651448 $--%>

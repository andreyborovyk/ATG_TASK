<%--
JSP, used to show checkbox TPO type.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/checkbox.jsp#1 $$Change: 651360 $
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

  <c:forEach items="${optionDef.values}" var="value" varStatus="status">
    <c:set var="inputId" value="${optionName}[${status.index}]"/>
    <input class="selector" type="radio"
           id="${inputId}" name="${optionName}.checkbox"
           value="${value.text}"
           <c:if test="${optionValue == value.text}">checked="true"</c:if>
           onclick="setChangeFlag();"/>
    <label for="${inputId}">
      <fmt:message key="${optionDef.name}.values.${value.text}" bundle="${bundle}"/>
    </label>
    <script type="text/javascript">
      registerTPOView('${inputId}', '${optionName}');
    </script>
  </c:forEach>
  <d:input bean="${handlerName}.${optionName}" value="${optionValue}" type="hidden"
           name="${optionName}" id="${optionName}.hidden" />

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/checkbox.jsp#1 $$Change: 651360 $--%>

<%--
JSP, used to show input TPO type.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/input.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="handlerName" param="handlerName"/>
  <d:getvalueof var="handler" param="handler"/>
  <d:getvalueof var="optionDef" param="optionDef"/>
  <d:getvalueof var="valuesProp" param="valuesProp"/>
  
  <c:set var="optionName" value="options.${optionDef.name}.${valuesProp}"/>
  <c:set var="optionValue" value="${handler.options[optionDef.name][valuesProp][0]}"/>

  <input class="textField" type="text"
         value="${optionValue}"
         id="${optionName}.view"
         onclick="setChangeFlag();"/>
  <script type="text/javascript">
    registerTPOView('${optionName}.view', '${optionName}');
  </script>
  <d:input bean="${handlerName}.${optionName}" value="${optionValue}" type="hidden"
           name="${optionName}" id="${optionName}.hidden" />

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/input.jsp#2 $$Change: 651448 $--%>

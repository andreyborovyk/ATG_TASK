<%--
JSP, used to show msdropdown TPO type.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/msdropdown.jsp#1 $$Change: 651360 $
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

  <jsp:useBean id="selection" class="java.util.HashMap"/>
  <c:forEach items="${handler.options[optionDef.name][valuesProp]}" var="cursor">
    <c:if test="${cursor != null}">
      <c:set target="${selection}" property="${fn:toLowerCase(cursor)}" value="${true}"/>
    </c:if>
  </c:forEach>

  <div id="${optionName}.options" style="display:none;visibility:hidden;">
    <c:forEach items="${selection}" var="value">
      <d:input type="hidden"
               bean="${handlerName}.${optionName}"
               name="${optionName}"
               value="${value.key}" />
    </c:forEach>
  </div>
  <select class="small"
          multiple="true"
          id="${optionName}.view"
          onclick="setChangeFlag();">
    <c:forEach items="${optionDef.values}" var="value" varStatus="status">
      <fmt:message key="${optionDef.name}.values.${value.text}" 
                   var="optionValue" bundle="${bundle}"/>
      <option value="${value.text}" <c:if test="${!empty selection[fn:toLowerCase(value.text)]}">selected="selected"</c:if>>
        <c:out value="${optionValue}"/>
      </option>
    </c:forEach>
  </select>
  <script type="text/javascript">
    registerTPOView('${optionName}.view', '${optionName}');
  </script>
  
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/msdropdown.jsp#1 $$Change: 651360 $--%>

<%--
JSP, used to show textarea TPO type.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/textarea.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="handlerName" param="handlerName"/>
  <d:getvalueof var="handler" param="handler"/>
  <d:getvalueof var="optionDef" param="optionDef"/>
  <d:getvalueof var="valuesProp" param="valuesProp"/>
  
  <c:set var="optionName" value="options.${optionDef.name}.${valuesProp}"/>

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
  <textarea class="textAreaField" rows="3" id="<c:out value='${optionName}'/>.textarea"></textarea>
  <script type="text/javascript">
    tpoTextareaLoad("<c:out value='${optionName}'/>");
    addEvent(document.getElementById("${optionName}.textarea"), 'keypress', setChangeFlag);
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/textarea.jsp#2 $$Change: 651448 $--%>

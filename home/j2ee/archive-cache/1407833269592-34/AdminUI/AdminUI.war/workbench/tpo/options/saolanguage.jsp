<%--
JSP, used to show dropdown TPO type.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/saolanguage.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
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
    <c:if test="${fn:length(optionDef.values) > 1}">
      <select id="${optionName}.view" class="small" onclick="setChangeFlag();" onchange="changeAdaptorLanguage();">
        <c:forEach items="${optionDef.values}" var="value">         
          <option value="${value.text}"
              <c:if test="${fn:toLowerCase(optionValue) == fn:toLowerCase(value.text)}">selected="true"</c:if> >
            <fmt:message key="${optionDef.name}.values.${value.text}" bundle="${bundle}"/>
          </option>
        </c:forEach>
      </select>
      <script type="text/javascript">
        registerTPOView('${optionName}.view', '${optionName}');
        function changeAdaptorLanguage(){
          syncTPOViews();
          document.getElementById('changeAdaptorsButton').click();
        }
      </script>
      <d:input bean="${handlerName}.${optionName}" value="${optionValue}" type="hidden"
           name="${optionName}" id="${optionName}.hidden" />
      <d:input style="display: none;" type="submit" name="changeAdaptors" id="changeAdaptorsButton"
                    bean="${handlerName}.changeAdaptors" iclass="formsubmitter"/>
    </c:if>
    <c:if test="${fn:length(optionDef.values) == 1}">
      <fmt:message key="${optionDef.name}.values.${optionValue}" bundle="${bundle}"/>
      <d:input bean="${handlerName}.${optionName}" value="${optionValue}" type="hidden"
           name="${optionName}" id="${optionName}.hidden" />
    </c:if>
  </c:if>
  <c:if test="${empty optionDef.values}">
    <fmt:message key="sao_set.details.not_available"/>
  </c:if>
  
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/saolanguage.jsp#2 $$Change: 651448 $--%>

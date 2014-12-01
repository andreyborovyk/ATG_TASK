<%--
JSP, TPO index template.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/TPO_index.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <%--Parameters--%>
  <d:getvalueof var="handlerName" param="handlerName" />
  <d:getvalueof var="handler" param="handler" />
  <d:getvalueof var="tpoSetId" param="tpoSetId" />
  <d:getvalueof var="optionDef" param="optionDef" />
  <d:getvalueof var="id" param="id"/>
  <%--Set TPO bundle--%>
  <admin-beans:setTPOResourceBundle var="bundle" useSAO="false"/>

  <tr id="${id}" style="display:none" class="linepadded">
    <td class="label">
      <span id="options.${optionDef.name}.indexValuesAlert"></span>
      <fmt:message key="${optionDef.name}.label" bundle="${bundle}" />
    </td>
    <td>
      <d:include page="${optionDef.display}.jsp">
        <d:param name="handlerName" value="${handlerName}" />
        <d:param name="handler" value="${handler}" />
        <d:param name="optionDef" value="${optionDef}" />
        <d:param name="valuesProp" value="indexValues" />
        <d:param name="bundle" value="${bundle}" />
      </d:include>
      <span class="ea"><tags:ea key="${optionDef.name}.description" bundle="${bundle}"/></span>
    </td>
  </tr>
  <script type="text/javascript">
    registerTPO('${id}');
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/TPO_index.jsp#2 $$Change: 651448 $--%>

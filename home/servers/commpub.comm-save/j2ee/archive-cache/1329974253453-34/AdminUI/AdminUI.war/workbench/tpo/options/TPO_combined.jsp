<%--
JSP, TPO content template.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/TPO_combined.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%--Common parameters--%>
  <d:getvalueof var="handlerName" param="handlerName"/>
  <d:getvalueof var="handler" param="handler"/>
  <d:getvalueof var="indexTPOSetName" param="indexTPOSetName"/>
  <d:getvalueof var="tpoSetId" param="tpoSetId"/>
  <d:getvalueof var="optionDef" param="optionDef"/>
  <d:getvalueof var="id" param="id"/>
  
  <%--Set TPO bundle--%>
  <admin-beans:setTPOResourceBundle var="bundle" useSAO="false"/>

    <tr id="${id}" style="display:none" class="linepadded">
      <td class="label">
        <span id="options.${optionDef.name}.contentValuesAlert"></span>
        <span id="options.${optionDef.name}.indexValuesAlert"></span>
        <fmt:message key="${optionDef.name}.label" bundle="${bundle}"/>
      </td>
      <td>
        <c:choose>
          <c:when test="${optionDef.contentLevel}">

            <d:getvalueof var="isIndexLevel" bean="${handlerName}.options.${optionDef.name}.indexLevel"/>

            <input class="selector" type="radio"
                   id="options.${optionDef.name}.indexLevel_index" name="options.${optionDef.name}.indexLevel_radio"
                   value="true"
                   onclick="setChangeFlag(); return switchTPODivs('options.${optionDef.name}', 'index');"
                   <c:if test="${isIndexLevel}">checked="checked"</c:if> />
            <label for="options.${optionDef.name}.indexLevel_index"
                   id="options.${optionDef.name}_index_label"
                   <%-- todo: use value from edit field --%>
                   title="<fmt:message key="tpo_set.edit.content.radio.label.index"/>:<c:forEach var="indexValueItem" items="${handler.options[optionDef.name].indexValues}"><c:out value='${indexValueItem}'/></c:forEach>">
              <fmt:message key="tpo_set.edit.combined.radio.label.index"/>
            </label>

            &nbsp;

            <input class="selector" type="radio"
                   id="options.${optionDef.name}.indexLevel_content" name="options.${optionDef.name}.indexLevel_radio"
                   value="false"
                   onclick="setChangeFlag(); return switchTPODivs('options.${optionDef.name}', 'content');"
                   <c:if test="${!isIndexLevel}">checked="checked"</c:if> />
            <label for="options.${optionDef.name}.indexLevel_content"
                   id="options.${optionDef.name}_content_label">
              <fmt:message key="tpo_set.edit.combined.radio.label.content"/>
            </label>
            
            <script type="text/javascript">
              registerTPOView('options.${optionDef.name}.indexLevel_index', 'options.${optionDef.name}.indexLevel');
              registerTPOView('options.${optionDef.name}.indexLevel_content', 'options.${optionDef.name}.indexLevel');
            </script>

          </c:when>
          <c:otherwise>
            <fmt:message key="tpo_set.edit.combined.text.index_only"/>
            <c:out value="${indexTPOSetName}"/>
          </c:otherwise>
        </c:choose>
        <span class="ea"><tags:ea key="${optionDef.name}.description" bundle="${bundle}"/></span>
        <div id="options.${optionDef.name}_content" class="tpoContentOptionDiv"
             <c:if test="${handler.options[optionDef.name].indexLevel}">style="display:none"</c:if> >
          <d:include page="${optionDef.display}.jsp">
            <d:param name="handlerName" value="${handlerName}"/>
            <d:param name="handler" value="${handler}"/>
            <d:param name="optionDef" value="${optionDef}"/>
            <d:param name="valuesProp" value="contentValues"/>
            <d:param name="bundle" value="${bundle}"/>
          </d:include>
        </div>
        <div id="options.${optionDef.name}_index" class="tpoContentOptionDiv"
             <c:if test="${not handler.options[optionDef.name].indexLevel}">style="display:none"</c:if> >
          <d:include page="${optionDef.display}.jsp">
            <d:param name="handlerName" value="${handlerName}"/>
            <d:param name="handler" value="${handler}"/>
            <d:param name="optionDef" value="${optionDef}"/>
            <d:param name="valuesProp" value="indexValues"/>
            <d:param name="bundle" value="${bundle}"/>
          </d:include>
        </div>
        <%--Hidden field for selector--%>
        <c:if test="${optionDef.contentLevel}">
          <d:input type="hidden"
                   id="options.${optionDef.name}.indexLevel.hidden"
                   name="options.${optionDef.name}.indexLevel"
                   bean="${handlerName}.options.${optionDef.name}.indexLevel"
          />
        </c:if>

      </td>
    </tr>
    
    <script type="text/javascript">
      registerTPO('${id}');
    </script>  
      
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/options/TPO_combined.jsp#2 $$Change: 651448 $--%>

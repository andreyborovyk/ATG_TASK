<%--
  Page, showing "basic" information in general tab on create/edit facet page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_basics.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="facetFormHandler" bean="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"/>
  <h3>
    <fmt:message key="facet.basics"/>
  </h3>
  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <span id="facetNameAlert">
            <span class="required"><fmt:message key="facet.required.field"/></span>
          </span>
          <label for="facetName">
            <fmt:message key="facet.name"/>
          </label>
        </td>
        <td>
          <d:input bean="FacetFormHandler.facetName" type="text" id="facetName" iclass="textField" name="facetName"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <span id="facetPropertyAlert">
            <span class="required"><fmt:message key="facet.required.field"/></span>
          </span>
          <label for="facetProperty">
            <fmt:message key="facet.property"/>
          </label>
        </td>
        <td>
          <d:input bean="FacetFormHandler.facetProperty" type="text" id="facetProperty" iclass="textField"
                   name="facetProperty"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <span class="required">
            <fmt:message key="facet.required.field"/>
          </span>
          <label for="facetPropertyType">
            <fmt:message key="facet.property_type"/>
          </label>
        </td>
        <td>
          <d:select iclass="small" id="facetPropertyType" bean="FacetFormHandler.facetPropertyType"
                    name="facetPropertyType" onchange="facetSelections.onPropertyTypeChange();">
            <c:forEach items="${facetFormHandler.propertyTypes}" var="optionKey">
              <d:option value="${optionKey}">
                <fmt:message key="facet.property_type.option.${optionKey}"/>
              </d:option>
            </c:forEach>
          </d:select>
        </td>
      </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_basics.jsp#2 $$Change: 651448 $--%>

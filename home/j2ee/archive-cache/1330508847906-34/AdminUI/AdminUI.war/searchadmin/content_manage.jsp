<%--
  JSP, used to create new content source set.
  This page is not used by itself, it is included into project_create_content_set.jsp page and
  index_create_content_set.jsp page. Page contain all general information, but such elements as error and
  success url's are created on pages, that include this page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_manage.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="logicalPartitionId" var="logicalPartitionId"/>
  <d:getvalueof param="setId" var="setId"/>
  <d:getvalueof param="action" var="action"/>

  <d:importbean var="manageContentFormHandler" bean="/atg/searchadmin/adminui/formhandlers/ManageContentFormHandler"/>

  <admin-ui:initializeFormHandler handler="${manageContentFormHandler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="logicalPartitionId" value="${logicalPartitionId}"/>
    <admin-ui:param name="setId" value="${setId}"/>
    <admin-ui:param name="action" value="${action}"/>
  </admin-ui:initializeFormHandler>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST">
      <div id="paneContent">
        <p>
          <fmt:message key="content_create.message"/>
        </p>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <span id="contentNameAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
                <label for="contentName"><fmt:message key="content.create.content_name"/></label>
              </td>
              <td>
                <%-- Content set name --%>
                <d:input type="text" bean="ManageContentFormHandler.contentName"
                         id="contentName" iclass="textField" name="contentName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <span id="contentSourceTypeAlert"><span class="required"><fmt:message key="project_general.required_field"/></span></span>
                <fmt:message key="select_source_type.type"/>
              </td>
              <td>
                <d:getvalueof var="sourceTypes" bean="/atg/searchadmin/adminui/navigation/SourceTypesComponent" />
                <d:select iclass="small" name="contentSourceType" bean="ManageContentFormHandler.contentSourceType"
                          id="sourceTypeSelect" onchange="document.getElementById('changeContentTypeButton').click();">
                  <d:option value="">
                    <fmt:message key="select_source_type.choose"/>
                  </d:option>
                  <c:forEach items="${sourceTypes.enumerateSourceTypes}" var="type" varStatus="typeStatus">
                    <d:option value="${type.id}">
                      <c:out value="${type.name}"/>
                    </d:option>
                    <c:if test="${manageContentFormHandler.contentSourceType == type.id}">
                      <c:set var="currentSourceType" value="${type}" />
                    </c:if>
                  </c:forEach>
                </d:select>
              </td>
            </tr>
          </tbody>
        </table>

        <c:if test="${not empty currentSourceType}">

          <d:include page="${currentSourceType.settingsPage}" otherContext="${currentSourceType.settingsPageContext}">
            <d:param name="section" value="general"/>
          </d:include>

          <h3>
            <fmt:message key="content.create.indexing_options.message"/>
            <span id="advIndexingOptions:hideLink" class="headerLink" style="display:none">
              [<a href="#" onclick="return showHideSettings('advIndexingOptions', false);"
                  title="<fmt:message key='content.create.indexing_options.message.hide.tooltip'/>">
                <fmt:message key="advanced_setting.hide_link"/>
              </a>]
            </span>
            <span id="advIndexingOptions:showLink" class="headerLink">
              [<a href="#" onclick="return showHideSettings('advIndexingOptions', true);"
                  title="<fmt:message key='content.create.indexing_options.message.show.tooltip'/>">
                <fmt:message key="advanced_setting.show_link"/>
              </a>]
            </span>
          </h3>
          <span id="advIndexingOptions" style="display:none">
            <table class="form" cellspacing="0" cellpadding="0">
              <tbody>
                <tr>
                  <td class="label">
                    <fmt:message key="content.create.indexing_options.tpo"/>
                  </td>
                  <td>
                    <fmt:message key="content.create.indexing_options.tpo.default.dropdown" var="defaultTPOName" />
                    <admin-beans:getCustomizationsByType varItems="tpos" customizationType="Content Set Text Processing Options"
                                                         defaultItemName="${defaultTPOName}" />
                    <d:select id="tpo" iclass="small" bean="ManageContentFormHandler.tpo" name="tpo">
                      <c:forEach items="${tpos}" var="currentTpo">
                        <d:option value="${currentTpo.id}">
                          <c:out value="${currentTpo.nameValue}"/>
                        </d:option>
                      </c:forEach>
                    </d:select>
                  </td>
                </tr>
                <tr>
                  <td class="label">
                    <label for="documentSetName">
                      <span id="documentSetNameAlert"></span>
                      <fmt:message key="content.create.indexing_options.doc_set_name"/>
                    </label>
                  </td>
                  <td>
                    <d:input iclass="textField" name="documentSetName"
                             id="documentSetName"
                             bean="ManageContentFormHandler.documentSetName"/>
                  </td>
                </tr>
                <tr>
                  <td class="label">
                    <fmt:message key="content.create.indexing_options.item_type"/>
                  </td>
                  <td>
                    <d:select id="contentItemType" iclass="small" bean="ManageContentFormHandler.contentItemType"
                              name="contentItemType">
                      <d:option value="default_unstruct">
                        <fmt:message key="content.create.indexing_options.item_type.def_un"/>
                      </d:option>
                      <d:option value="default_struct">
                        <fmt:message key="content.create.indexing_options.item_type.def_st"/>
                      </d:option>
                    </d:select>
                  </td>
                </tr>
              </tbody>
            </table>
            <d:include page="${currentSourceType.settingsPage}" otherContext="${currentSourceType.settingsPageContext}">
              <d:param name="section" value="indexing"/>
            </d:include>
          </span>

          <d:include page="${currentSourceType.settingsPage}" otherContext="${currentSourceType.settingsPageContext}">
            <d:param name="section" value="other"/>
          </d:include>

          <h3>
            <fmt:message key="advanced_settings.head"/>
            <span id="advSettings:hideLink" class="headerLink" style="display:none">
              [<a href="#" onclick="return showHideSettings('advSettings', false);"
                  title="<fmt:message key='advanced_setting.hide_link.tooltip'/>">
                <fmt:message key="advanced_setting.hide_link"/>
              </a>]
            </span>
            <span id="advSettings:showLink" class="headerLink">
              [<a href="#" onclick="return showHideSettings('advSettings', true);"
                  title="<fmt:message key='advanced_setting.show_link.tooltip'/>">
                <fmt:message key="advanced_setting.show_link"/>
              </a>]
            </span>
          </h3>
          <span id="advSettings" style="display:none">
            <d:include page="${currentSourceType.settingsPage}" otherContext="${currentSourceType.settingsPageContext}">
              <d:param name="section" value="advanced"/>
            </d:include>
            <table class="form" cellspacing="0" cellpadding="0">
              <tbody>
                <tr>
                  <td class="label">
                    <fmt:message key="select_source_type.file_system.additional_settings"/>
                  </td>
                  <td>
                    <d:textarea type="area" iclass="textAreaField" rows="10" name="advancedSettings"
                                bean="ManageContentFormHandler.advancedSettings"/>
                    <span class="ea"><tags:ea key="embedded_assistant.select_source_type_file_system.additional_settings" /></span>
                  </td>
                </tr>
              </tbody>
            </table>
          </span>

        </c:if>

      </div>

      <div id="paneFooter">
        <d:input bean="ManageContentFormHandler.projectId" type="hidden" name="projectId"/>
        <d:input bean="ManageContentFormHandler.contentSetPartition" type="hidden" name="contentSetPartition"/>
        <d:input bean="ManageContentFormHandler.contentSetId" type="hidden" name="contentSetId"/>
        <d:input bean="ManageContentFormHandler.action" type="hidden" name="contentSetId"/>
        <d:input bean="ManageContentFormHandler.needInitialization" value="false" type="hidden" name="needInitialization"/>

        <c:if test="${action ne 'edit'}">
          <%-- Create content button --%>
          <fmt:message key="content_create.buttons.add_content" var="buttonOk"/>
          <fmt:message key="content_create.buttons.add_content.tooltip" var="buttonOkTooltip"/>
          <d:input type="submit" iclass="formsubmitter" bean="ManageContentFormHandler.create"
                   name="create" onclick="return checkForm()"
                   value="${buttonOk}" title="${buttonOkTooltip}"/>
        </c:if>
        <c:if test="${action eq 'edit'}">
          <%-- Edit content button --%>
          <fmt:message key="content_create.buttons.save_content" var="buttonOk"/>
          <fmt:message key="content_create.buttons.save_content.tooltip" var="buttonOkTooltip"/>
          <d:input type="submit" iclass="formsubmitter" bean="ManageContentFormHandler.update"
                   name="update" onclick="return checkForm()"
                   value="${buttonOk}" title="${buttonOkTooltip}"/>
        </c:if>
        <%-- Cancel button --%>
        <fmt:message key="content_create.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="content_create.buttons.cancel.tooltip" var="cancelButtonTooltip"/>
        <d:input type="submit" bean="ManageContentFormHandler.cancel" iclass="formsubmitter"
                 name="cancelButton" value="${cancelButtonTitle}" title="${cancelButtonTooltip}"/>
        <%-- Change content type invisible button --%>
        <d:input type="submit" iclass="formsubmitter" style="display:none;"
                 bean="ManageContentFormHandler.changeContentType" id="changeContentTypeButton" />
        <%-- Validation --%>
        <admin-validator:validate beanName="ManageContentFormHandler"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_manage.jsp#2 $$Change: 651448 $--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%@ page import="java.io.*,java.util.*,java.util.ResourceBundle" %>
<%@ page import="atg.naming.*" %>
<%@ page import="atg.servlet.*" %>
<%@ page import="atg.nucleus.*" %>

<%--
   Document Exchange Gear
   gearmode = content
   displaymode = full

   This fragment is included by DocExchFull when the value
   of request parameter dexmode=oneItem.  It presents the
   details of the documents metadata and gives options to
   update document, update status, discuss, or delete the
   document as permissions allow.  
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= gearEnv %>">
<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
<dsp:importbean bean="/atg/portal/gear/docexch/PermissionsDroplet"/>
<dsp:importbean bean="/atg/portal/gear/docexch/DocumentFormHandler"/>
<dsp:importbean bean="/atg/portal/gear/docexch/DocumentDownloadParams"/>

<%--
    Normally, the current document comes in on a request parameter.
    However, f we came directly from createDoc.jsp, then we need to
    get the documentid from the DocumentFormHandler, or from the DocumentDownloadParams.
--%>
  <dsp:getvalueof id="docid_from_download_params" bean="DocumentDownloadParams.docexchDocumentId">
  <dsp:getvalueof id="docid_from_form_handler" bean="DocumentFormHandler.repositoryId">
<%
  String docid = atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("documentid");
  if (docid == null || docid.trim().length() == 0) 
	docid = (String)docid_from_form_handler;
  if (docid == null || docid.trim().length() == 0) 
	docid = (String)docid_from_download_params;
%>
   

  <i18n:message id="titleColumnHeader" key="titleColumnHeader"/>
  <i18n:message id="descriptionColumnHeader" key="descriptionColumnHeader"/>
  <i18n:message id="authorColumnHeader" key="authorColumnHeader"/>
  <i18n:message id="statusColumnHeader" key="statusColumnHeader"/>
  <i18n:message id="createDateColumnHeader" key="createDateColumnHeader"/>
  <i18n:message id="discTitle" key="discTitle"/>
  <i18n:message id="downloadDocument" key="downloadDocument"/>
  <i18n:message id="untitled" key="untitled"/>
  <i18n:message id="separator" key="labelFieldSeparator"/>
  <i18n:message id="updateStatusLinkText" key="updateStatusLinkText"/>
  <i18n:message id="updateDocLinkText" key="updateDocLinkText"/>
  <i18n:message id="deleteDocLinkText" key="deleteDocLinkText"/>
  <i18n:message id="docDetailsTitle" key="docDetailsTitle"/>
  <i18n:message id="actionsTitle" key="actionsTitle"/>

  <% String lineColor = "#666666"; %>

    <% String rqlquery = new StringBuffer("ID IN { \"").append(docid).append("\"}").toString(); %>
    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
      <dsp:param name="repository" value="<%= dexpage.getRepositoryPath() %>"/>
      <dsp:param name="itemDescriptor" value="<%= dexpage.getItemDescriptorName() %>"/>
      <dsp:param name="queryRQL" value="<%= rqlquery %>"/>
      <dsp:param name="howMany" value="1"/>

      <dsp:oparam name="empty">
        no doc
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:getvalueof id="docElement" param="element" idtype="atg.repository.RepositoryItem">
        <dex:isDocInGear gearEnv="<%= gearEnv %>" document="<%= docElement %>">

        <%-- Put the communityid, pageid, gearid and docid in a session-scoped bean.
             These values will be used by the document download servlet
             to get the document.  --%> 
	<dsp:setvalue bean="DocumentDownloadParams.docexchCommunityId"
	              value="<%= gearEnv.getCommunity().getId() %>"/>
	<dsp:setvalue bean="DocumentDownloadParams.docexchPageId"
	              value="<%= gearEnv.getPage().getId() %>"/>
 	<dsp:setvalue bean="DocumentDownloadParams.docexchGearId"
	              value="<%= gearEnv.getGear().getId() %>"/>
 	<dsp:setvalue bean="DocumentDownloadParams.docexchDocumentId"
	              value="<%= docid %>"/>


          <% String doclink = gearEnv.getGear().getServletContext() + "/download/" + dexpage.getFilename(docElement);%>
          <table cellspacing="0" cellpadding="2" border="0">
            <core:If value="<%= dexpage.getDisplayTitle() %>">
              <tr>
                <td colspan=2 align="left"><font class="large_bold">
                  <b><%= dexpage.getTitle(docElement) %></b>
                </font></td>
              </tr>
            </core:If>
            <core:If value="<%= dexpage.getDisplayDescription() %>">
              <tr>
                <td align="right"><font class="small">
                  <%= descriptionColumnHeader %><%= separator %>
                </font></td>
                <td><font class="small">
                  <%= dexpage.getDescription(docElement) %>
                </font></td>
              </tr>
            </core:If>
            <core:If value="<%= dexpage.getDisplayAuthor() %>">
              <tr>
                <td align="right"><font class="small">
                  <%= authorColumnHeader %><%= separator %>
                </font></td>
                <td><font class="small">
                  <%= dexpage.getAuthorFirstName(docElement) %>
                  <%= dexpage.getAuthorLastName(docElement) %>
                </font></td>
              </tr>
            </core:If>

            <core:IfNotNull value="<%= dexpage.getFilename(docElement) %>">
              <tr>
                <td align="right"><font class="small">
                  <nobr><%= downloadDocument %><%= separator %>
                </font></td>
                <td><font class="small">
                  <%-- view doc link --%>
                  <core:CreateUrl id="viewDocUrl" url="<%= atg.servlet.ServletUtil.escapeURLString(doclink) %>">
                  <a href="<%= viewDocUrl.getNewUrl() %>" class="gear_content"><%= dexpage.getFilename(docElement) %></a>
                  </core:CreateUrl>
                </font></td>
              </tr>
            </core:IfNotNull>

            <core:If value="<%= dexpage.getDisplayStatus() %>">
              <tr>
                <td align="right"><font class="small">
                  <%= statusColumnHeader %><%= separator %>
                </font></td>
                <td><font class="small">
                  <%= dexpage.getStatus(docElement) %>
                   <%-- edit status link --%>
                  <dsp:droplet name="/atg/portal/gear/docexch/PermissionsDroplet">
                    <dsp:param name="pafEnv" value="<%= gearEnv %>"/>
                    <dsp:oparam name="mayUpdateStatus">
                      <core:CreateUrl id="editStatusUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
                        <core:UrlParam param="paf_dm" value="full"/>
                        <core:UrlParam param="paf_gm" value="content"/>
                        <core:UrlParam param="dexmode" value="editStatus"/>
                        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
                        <core:urlParamValue id="prevpaf_dm" param="prevpaf_dm">
                          <core:UrlParam param="prevpaf_dm" value="<%= prevpaf_dm %>"/>
                        </core:urlParamValue>
                        <core:UrlParam param="documentid" value="<%= docid %>"/>
                        <paf:encodeUrlParam param="doctitle" value="<%= dexpage.getTitle(docElement) %>"/>
                        <a href="<%= editStatusUrl.getNewUrl()
                        %>" class="gear_nav"><%= updateStatusLinkText %></a>
                      </core:CreateUrl>
                    </dsp:oparam>
                  </dsp:droplet>
                </font></td>
              </tr>
            </core:If>
            <core:If value="<%= dexpage.getDisplayCreateDate() %>">
              <tr>
                <td align="right"><font class="small">
                  <%= createDateColumnHeader %><%= separator %>
                </font></td>
                <td><font class="small">
                  <i18n:formatDate value="<%= dexpage.getCreateDate(docElement) %>"
                  style="<%= dexpage.getDateStyle() %>"/>
                </font></td>
              </tr>
            </core:If>
          </table>

          <%-- make edit/delete links if current user has permission to edit or delete this file --%>
          <dex:mayEdit document="<%= docElement %>">
            <br>
            <%-- edit doc link --%>
            <core:CreateUrl id="editDocUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
               <core:UrlParam param="paf_dm" value="full"/>
               <core:UrlParam param="paf_gm" value="content"/>
               <core:UrlParam param="dexmode" value="edit"/>
               <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
               <core:UrlParam param="prevpaf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
               <core:UrlParam param="documentid" value="<%= docid %>"/>
               <paf:encodeUrlParam param="doctitle" value="<%= dexpage.getTitle(docElement) %>"/>
               &nbsp;&nbsp;<a href="<%= editDocUrl.getNewUrl()
               %>" class="gear_nav"><font class="small"><%= updateDocLinkText %></font></a>
            </core:CreateUrl>

            <%-- separator --%>
            |

            <%-- delete doc link --%>
            <core:CreateUrl id="delDocUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
               <core:UrlParam param="paf_dm" value="full"/>
               <core:UrlParam param="paf_gm" value="content"/>
               <core:UrlParam param="dexmode" value="delete"/>
               <core:UrlParam param="prevpaf_dm" value='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("prevpaf_dm") %>'/>
               <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
               <core:UrlParam param="documentid" value="<%= docid %>"/>
               <paf:encodeUrlParam param="doctitle" value="<%= dexpage.getTitle(docElement) %>"/>
               <a href="<%= delDocUrl.getNewUrl()
               %>" class="gear_nav"><font class="small"><%= deleteDocLinkText %></font></a>
            </core:CreateUrl>
            <br><br>

         </dex:mayEdit>


          <%-- DISCUSSION --%>
          <dsp:droplet name="/atg/portal/gear/docexch/PermissionsDroplet">
            <dsp:param name="pafEnv" value="<%= gearEnv %>"/>
            <dsp:oparam name="mayDiscuss">
              <table width="100%" cellspacing="0" cellpadding="0" border="0">
                <tr>
                  <td colspan="3" bgcolor="<%= lineColor %>" ><img src='<%= 
                  dexpage.getRelativeUrl("/images/clear.gif") %>' 
                  height="1" width="1" border="0"></td>
                </tr>
                <tr>
                  <td colspan="3" ><img src='<%= 
                  dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></td>
                </tr>
                <tr>
                  <td colspan="3"><font class="medium_bold">&nbsp;<%=discTitle%></font></td>
                </tr>
                <tr>
                  <td colspan="3"><img src='<%= 
                  dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></td>
                </tr>
              </table>

              <%-- If a discussion board already exists, show it --%>
              <% String discussionid = dexpage.getDiscussionId(docElement); %>

              <dsp:setvalue param="forumID" value="<%= discussionid %>" />
              <dsp:setvalue param="trFlag" value="T"/>

              <core:IfNotNull value="<%= discussionid %>">
                <%-- NOTE: using include directive here to keep page size small.
                 Large pages do not compile. --%>
                <dsp:include page="/discussionList.jsp" flush="false"/>
              </core:IfNotNull>

              <%-- show the form to contribute to the discussion (this will create a
              discussion board if it does not already exist --%>
              <%-- NOTE the use of the include directive instead of
              jsp:include because the form processing seems not
              to work properly with jsp include --%>
              <%@ include file="discussionPostForm.jspf" %>
            </dsp:oparam>
          </dsp:droplet>
          <%-- END DISCUSSION --%>

          </dex:isDocInGear>	
        </dsp:getvalueof>
      </dsp:oparam>
    </dsp:droplet> <%-- end RQLQueryRange --%>

  </dsp:getvalueof>
  </dsp:getvalueof>


</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/displayDoc.jsp#2 $$Change: 651448 $--%>

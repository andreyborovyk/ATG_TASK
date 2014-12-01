<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Document Exchange Gear
   gearmode = content
   displaymode = full

   This fragment is included by DocExchFull when the value
   of request parameter dexmode=list.  It displays a pagable
   sortable list of documents from the repository. All column
   headers, property names, and the length of the list are
   determined by gear parameters.
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= gearEnv %>">
<dsp:importbean bean="/atg/portal/gear/docexch/PermissionsDroplet"/>
<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />

      <table border="0" cellpadding="1" cellspacing="1" width="100%">
      <tr>
        <td width="90%" align=center><font class="large_bold"><i18n:message key="allItemsLinkText"/></font></td>
      </tr>
      <tr>
      <td nowrap><font class="small">
      <dsp:droplet name="/atg/portal/gear/docexch/RQLQueryBuilder">
        <dsp:param name="repository" value="<%= dexpage.getRepositoryPath() %>"/>
        <dsp:param name="itemDescriptor" value="<%= dexpage.getItemDescriptorName() %>"/>
        <dsp:param name="gearInstanceRef" value="<%= gearEnv.getGear().getId() %>"/>
        <dsp:param name="gearIdPropertyName" value="<%= dexpage.getGearIdPropertyName() %>"/>
        <dsp:oparam name="query">
          <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
            <dsp:param name="repository" value="<%= dexpage.getRepositoryPath() %>"/>
            <dsp:param name="itemDescriptor" value="<%= dexpage.getItemDescriptorName() %>"/>
            <dsp:param name="gearInstanceRef" value="<%= gearEnv.getGear().getId() %>"/>
            <dsp:param name="queryRQL" param="queryRQL"/>
            <dsp:param name="sortProperties" param="sortProperty"/>
            <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
            <dsp:param name="howMany" value="<%= dexpage.getFullListPageSize() %>"/>

            <dsp:oparam name="outputStart">
              <!-- outputStart begin -->
              <%-- This is for the previous|next --%>
              <%@ include file="prevNext.jspf" %>
              <%-- End of the prev | next nav --%>
              </font>

              <%-- Begin table header --%>
              <table border="0" width="100%" cellpadding="4" cellspacing="2">
              <core:If value="<%= dexpage.getDisplayColumnHeaders() %>">
                <tr>
                <core:If value="<%= dexpage.getDisplayTitle() %>">
                  <td bgcolor="#<%= dexpage.getHighlightBGColor() %>"><font class="small">
                    <core:CreateUrl id="sort1Url" url="<%= gearEnv.getOriginalRequestURI() %>">
                      <core:UrlParam param="paf_dm" value="full"/>
                      <core:UrlParam param="paf_gm" value="content"/>
                      <core:UrlParam param="dexmode" value="list"/>
                      <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
                      <core:UrlParam param="sortProperty" value="<%= dexpage.getTitlePropertyName() %>"/>
                      <a href="<%= sort1Url.getNewUrl() %>" class="gear_content"><i18n:message key="titleColumnHeader"/></a>
                    </core:CreateUrl>
                 </font></td>
                </core:If>
                <core:If value="<%= dexpage.getDisplayAuthor() %>">
                  <td bgcolor="#<%= dexpage.getHighlightBGColor() %>"><font class="small">
                    <core:CreateUrl id="sort2Url" url="<%= gearEnv.getOriginalRequestURI() %>">
                      <core:UrlParam param="paf_dm" value="full"/>
                      <core:UrlParam param="paf_gm" value="content"/>
                      <core:UrlParam param="dexmode" value="list"/>
                      <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
                      <core:UrlParam param="sortProperty" value="<%= dexpage.getAuthorLastNameCombinedProp() %>"/>
                      <a href="<%= sort2Url.getNewUrl() %>" class="gear_content"><i18n:message key="authorColumnHeader"/></a>
                    </core:CreateUrl>
                  </font></td>
                </core:If>
                <core:If value="<%= dexpage.getDisplayCreateDate() %>">
                  <td bgcolor="#<%= dexpage.getHighlightBGColor() %>"><font class="small">
                    <core:CreateUrl id="sort3Url" url="<%= gearEnv.getOriginalRequestURI() %>">
                      <core:UrlParam param="paf_dm" value="full"/>
                      <core:UrlParam param="paf_gm" value="content"/>
                      <core:UrlParam param="dexmode" value="list"/>
                      <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
	              <%-- a - in front of the property name requests a reverse sort --%>
                      <paf:encodeUrlParam param="sortProperty" value='<%= "-" + dexpage.getCreateDatePropertyName() %>'/>
                      <a href="<%= sort3Url.getNewUrl() %>" class="gear_content"><i18n:message key="createDateColumnHeader"/></a>
                    </core:CreateUrl>
                  </font></td>
                </core:If>
                <core:If value="<%= dexpage.getDisplayStatus() %>">
                  <td bgcolor="#<%= dexpage.getHighlightBGColor() %>"><font class="small">
                    <core:CreateUrl id="sort4Url" url="<%= gearEnv.getOriginalRequestURI() %>">
                      <core:UrlParam param="paf_dm" value="full"/>
                      <core:UrlParam param="paf_gm" value="content"/>
                      <core:UrlParam param="dexmode" value="list"/>
                      <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
                      <core:UrlParam param="sortProperty" value="<%= dexpage.getStatusPropertyName() %>"/>
                      <a href="<%= sort4Url.getNewUrl() %>" class="gear_content"><i18n:message key="statusColumnHeader"/></a>
                    </core:CreateUrl>
                  </font></td>
                </core:If>
                </tr>
              </core:If>
              <!-- outputStart end -->
            </dsp:oparam>

            <dsp:oparam name="output">
              <dsp:getvalueof id="docElement" param="element">
              <!-- output start -->
              <tr>
                <core:If value="<%= dexpage.getDisplayTitle() %>">
                  <td><font class="small">
                    <%-- view details link --%>
                    <core:CreateUrl id="editDocUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
                      <core:UrlParam param="prevpaf_dm" value="full"/>
                      <core:UrlParam param="paf_dm" value="full"/>
                      <core:UrlParam param="paf_gm" value="content"/>
                      <core:UrlParam param="dexmode" value="oneItem"/>
                      <core:UrlParam param="paf_gear_id"
                      value="<%= gearEnv.getGear().getId() %>"/>
                      <core:UrlParam param="documentid" value="<%= dexpage.getDocumentId(docElement) %>"/>
                      <a href="<%= editDocUrl.getNewUrl()
                      %>" class="gear_content"><%= dexpage.getTitle(docElement) %></a>
                    </core:CreateUrl>
                  </font></td>
                </core:If>
                <core:If value="<%= dexpage.getDisplayAuthor() %>">
                  <td>
                    <font class="small"><%= dexpage.getAuthorFirstName(docElement) %>
                    <%= dexpage.getAuthorLastName(docElement) %></font>
                  </td>
                </core:If>
                <core:If value="<%= dexpage.getDisplayCreateDate() %>">
                 <td>
                   <font class="small">
                     <core:IfNot value='<%= dexpage.getDateStyle().equals("notShown") %>'>
                       <i18n:formatDate value="<%= dexpage.getCreateDate(docElement) %>"
                                        style="<%= dexpage.getDateStyle() %>"/>
                     </core:IfNot>
                     <core:IfNot value='<%= dexpage.getTimeStyle().equals("notShown") %>'>
                        <i18n:formatTime value="<%= dexpage.getCreateDate(docElement) %>"
                                         style="<%= dexpage.getTimeStyle() %>"/>
                     </core:IfNot>
                   </font>
                 </td>
                </core:If>
                <core:If value="<%= dexpage.getDisplayStatus() %>">
                  <td>
                    <font class="small"><%= dexpage.getStatus(docElement) %></font>
                  </td>
                </core:If>
              </tr>
              <%--
              <core:If value="<%= dexpage.getDisplayDescription() %>">
                <tr>
                  <td colspan=4>
                    <font class="smaller" style="marginleft:15px;marginright:10px;marginbottom:1px"><%= dexpage.getDescription(docElement) %></font>
                  </td>
                </tr>
              </core:If>
              --%>
              </dsp:getvalueof>
              <!-- output end -->
            </dsp:oparam>

            <dsp:oparam name="empty">
              <!-- empty start -->
              <font class="medium"><i18n:message key="emptyText"/></font>
              </td>
              </tr>
              <!-- empty end -->
            </dsp:oparam>

            <dsp:oparam name="error">
              <font class="medium"><i18n:message key="genericError"/></font>
              </td>
              </tr>
            </dsp:oparam>

            <dsp:oparam name="outputEnd">
              <!-- outputEnd start -->
              </table>
              <table border="0" width="100%" cellpadding="0" cellspacing="2">
                <tr>
                  <td width="90%">&nbsp;</td>
                  <td nowrap>
                      <%-- This is for the previous|next --%>
                      <%@ include file="prevNext.jspf" %>
                      <%-- End of the prev | next nav --%>
                  </td>
                </tr>
              </table>
              <!-- outputEnd end -->
            </dsp:oparam>
          </dsp:droplet> <%-- end RQLQueryRange --%>
        </dsp:oparam>
      </dsp:droplet> <%-- end RQLQueryBuilder --%>


      <dsp:droplet name="/atg/portal/gear/docexch/PermissionsDroplet">
        <dsp:param name="pafEnv" value="<%= gearEnv %>"/>
        <dsp:oparam name="mayWrite">
          <font class="small">
          <!-- mayWrite begin -->
          <core:CreateUrl id="uploadGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gm" value="content"/>
            <core:UrlParam param="dexmode" value="upload"/>
            <core:UrlParam param="prevpaf_dm" value="full"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
              &nbsp;<a class="gear_nav" href="<%= uploadGearUrl.getNewUrl() %>"
              class="gear_nav"><font class="small"><i18n:message key="newItemLinkText"/></font></a>
          </core:CreateUrl>
          <!-- mayWrite end -->
           </font>
        </dsp:oparam>
      </dsp:droplet>

      <br>
</td>
</tr>
</table>
</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/fullList.jsp#2 $$Change: 651448 $--%>

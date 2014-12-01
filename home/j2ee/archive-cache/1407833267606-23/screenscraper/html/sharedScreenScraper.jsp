  <%@ taglib uri="/paf-taglib" prefix="paf" %>
  <%@ taglib uri="/core-taglib" prefix="core" %>
  <%@ taglib uri="/dsp-taglib" prefix="dsp" %>
  <%@ taglib uri="/screenscraper-taglib" prefix="ss" %>
  <%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

  <dsp:page>
    <paf:InitializeGearEnvironment id="pafEnv">
      <%-- including the resource bundle  --%>
      <%@ include file="includeBundle.jspf" %>  
      
      <%-- populate variable to check if full mode url is not missconfigured i.e not null and not all blank --%>
       <% boolean missConfiguredFull= false; %>
      <core:IfNull value='<%=pafEnv.getGearInstanceParameter("instanceFullURL")%>'>
        <% missConfiguredFull = true; %>
      </core:IfNull>
      
      <core:IfNotNull value='<%=pafEnv.getGearInstanceParameter("instanceFullURL")%>'>
        <core:If value='<%="".equals(pafEnv.getGearInstanceParameter("instanceFullURL").trim())%>'>
           <% missConfiguredFull= true; %>
        </core:If>
      </core:IfNotNull>
      <p>
      <%-- populate the variable to check if Full mode link text is configured --%>
       <% boolean fullTextNotGiven= false; %>
      <core:IfNull value='<%=pafEnv.getGearInstanceParameter("fullModeLinkText")%>'>
        <% fullTextNotGiven = true; %>
      </core:IfNull>
      
      <core:IfNotNull value='<%=pafEnv.getGearInstanceParameter("fullModeLinkText")%>'>
        <core:If value='<%="".equals(pafEnv.getGearInstanceParameter("fullModeLinkText").trim())%>'>
           <% fullTextNotGiven= true; %>
        </core:If>
      </core:IfNotNull>
<%--  --%>
      
      <core:IfNot value="<%= missConfiguredFull %>">
        <%
           atg.portal.framework.ColorPalette palette = pafEnv.getPage().getColorPalette();
           String titleBGColor = palette.getGearTitleBackgroundColor();
           String titleTextColor = palette.getGearTitleTextColor();
        %>
 
        <table COLS=1 WIDTH="100%" >
        <tr>
        <td BGCOLOR="#<%=titleTextColor%>" align="right"><font color="#<%=titleBGColor%>">
          <core:CreateUrl id="goFullUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="paf_dm" value="full"/>
              <core:UrlParam param="paf_gm" value="content"/>
              <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
              <core:UrlParam param="paf_success_url" value="<%= pafEnv.getOriginalRequestURI() %>"/>
              <a href="<%= goFullUrl.getNewUrl() %>" class="gear_nav">
               <core:IfNot value="<%= fullTextNotGiven %>">
                  <%=pafEnv.getGearInstanceParameter("fullModeLinkText")%>
               </core:IfNot>
               <core:If value="<%= fullTextNotGiven %>">
                  <%=pafEnv.getGearInstanceParameter("instanceFullURL")%>
               </core:If>
            </a>
        </core:CreateUrl>
      </font></td>
      </tr>
      </table>
    </core:IfNot>
    
    <%-- Check to see if shared mode url is not null or blank --%>
     <% boolean missConfiguredShared= false; %>
    <core:IfNull value='<%=pafEnv.getGearInstanceParameter("instanceSharedURL")%>'>
      <% missConfiguredShared = true; %>
    </core:IfNull>
    
    <core:IfNotNull value='<%=pafEnv.getGearInstanceParameter("instanceSharedURL")%>'>
      <core:If value='<%="".equals(pafEnv.getGearInstanceParameter("instanceSharedURL").trim())%>'>
         <% missConfiguredShared= true; %>
      </core:If>
    </core:IfNotNull>
    
    
    
    <core:ExclusiveIf>
    
      <core:If value="<%= missConfiguredShared %>">
       <%--  Place Holder for error messgage. We don't consider this an error for now.--%>
      </core:If>
      
      <core:If value="true">
          <ss:renderhtml id="renderInstance"  gearId="<%= pafEnv.getGear().getId() %>" 
          URL='<%= pafEnv.getGearInstanceParameter("instanceSharedURL").trim() %>'>
              <%--Check if there was an error during retreival/parsing of the web page and show error if any --%>
              <core:IfNot value="<%= renderInstance.getSuccess() %>" >
                 <%= renderInstance.getErrorMessage()%>
                 <br>
                 <hr SIZE="0">
                 <i18n:message key="screenscraper-config-error"/>
                  <hr SIZE="0">
              </core:IfNot>
              </ss:renderhtml>
      </core:If>
    </core:ExclusiveIf>
    
  </paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/screenscraper/screenscraper.war/html/sharedScreenScraper.jsp#2 $$Change: 651448 $--%>

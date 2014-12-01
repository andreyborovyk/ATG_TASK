<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp-taglib" prefix="dsp" %>
<%@ taglib uri="/screenscraper-taglib" prefix="ss" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<dsp:page>
  <paf:InitializeGearEnvironment id="pafEnv">
    <%@ include file="includeBundle.jspf" %>
    
    <%-- End user can come to this page by manually creating a URL we should check for miss configuration --%>
     <% boolean missConfiguredFull= false; %>
    <core:IfNull value='<%=pafEnv.getGearInstanceParameter("instanceFullURL")%>'>
      <% missConfiguredFull = true; %>
    </core:IfNull>
    
    <core:IfNotNull value='<%=pafEnv.getGearInstanceParameter("instanceFullURL")%>'>
      <core:If value='<%="".equals(pafEnv.getGearInstanceParameter("instanceFullURL").trim())%>'>
         <% missConfiguredFull= true; %>
      </core:If>
    </core:IfNotNull>
    
    <core:ExclusiveIf>
    
      <core:If value="<%= missConfiguredFull%>">
        <%--Place holder for error message.  --%>
      </core:If>
      
      <core:If value="true">
          <ss:renderhtml id="renderInstance"  gearId="<%= pafEnv.getGear().getId() %>" URL='<%= pafEnv.getGearInstanceParameter("instanceFullURL").trim() %>'>
              <core:IfNot value="<%= renderInstance.getSuccess() %>" >
                 <hr SIZE="0">
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
<%-- @version $Id: //app/portal/version/10.0.3/screenscraper/screenscraper.war/html/fullScreenScraper.jsp#2 $$Change: 651448 $--%>

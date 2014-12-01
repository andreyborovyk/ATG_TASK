<!-- ******** Begin Slot Gear display ******** -->
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<%
   String origURI=pafEnv.getOriginalRequestURI();
   String contextPath = request.getContextPath() ;
   String clearGIF = contextPath+"/images/clear.gif";
   String infoGIF = contextPath+"/images/info.gif";

   String communityId=pafEnv.getCommunity().getId();

   String slotParam="/atg/registry/Slots/" + pafEnv.getGearInstanceParameter("slotComponent");
   String filterParam=pafEnv.getGearInstanceParameter("filter");
     if (filterParam!=null) { if (filterParam.length()<1) { filterParam=null; } }
   String sourceMapParam=pafEnv.getGearInstanceParameter("sourceMap");
     if (sourceMapParam!=null) { if (sourceMapParam.length()<1) { sourceMapParam=null; } }
   String howManyParam=pafEnv.getGearInstanceParameter("howMany");
     if (howManyParam!=null) { if (howManyParam.length()<1) { howManyParam=null; } }
   String sortPropertiesParam=pafEnv.getGearInstanceParameter("sortProperties");
     if (sortPropertiesParam!=null) { if (sortPropertiesParam.length()<1) { sortPropertiesParam=null; } }
   String contentEventParam=pafEnv.getGearInstanceParameter("fireContentEvent");
     if (contentEventParam!=null) { if (contentEventParam.length()<1) { contentEventParam=null; } }
   String contentTypeEventParam=pafEnv.getGearInstanceParameter("fireContentTypeEvent");
     if (contentTypeEventParam!=null) { if (contentTypeEventParam.length()<1) { contentTypeEventParam=null; } }
   String defaultImageParam=pafEnv.getGearInstanceParameter("defaultImage");
     if (defaultImageParam!=null) { if (defaultImageParam.length()<1) { defaultImageParam=null; } }
   String targetingBean=pafEnv.getGearInstanceParameter("targetingBean");
     if (targetingBean==null) { 
       targetingBean="/atg/targeting/TargetingFirst";
     }
   String urlProperty=pafEnv.getGearInstanceParameter("urlPropertyName");

   String mediaContextParam=pafEnv.getGearInstanceParameter("mediaContextRoot");
     if (mediaContextParam!=null) { if (mediaContextParam.length()<1) { mediaContextParam=null; } }
%>

    <dsp:droplet name="<%=targetingBean%>">
	<dsp:param bean="<%= slotParam %>" name="targeter"/>
	<core:ifNotNull value="<%=filterParam%>">
	   <dsp:param name="filter" value="<%=filterParam%>"/>
	</core:ifNotNull>
	<core:ifNotNull value="<%=sourceMapParam%>">
	   <dsp:param name="sourceMap" value="<%=sourceMapParam%>"/>
	</core:ifNotNull>
	<core:ifNotNull value="<%=howManyParam%>">
	   <dsp:param name="howMany" value="<%=howManyParam%>"/>
	</core:ifNotNull>
	<core:ifNotNull value="<%=sortPropertiesParam%>">
	   <dsp:param name="sortProperties" value="<%=sortPropertiesParam%>"/>
	</core:ifNotNull>
	<core:ifNotNull value="<%=contentEventParam%>">
	   <dsp:param name="fireContentEventParam" value="<%=contentEventParam%>"/>
	</core:ifNotNull>
	<core:ifNotNull value="<%=contentTypeEventParam%>">
	   <dsp:param name="fireContentTypeEvent" value="<%=contentTypeEventParam%>"/>
	</core:ifNotNull>
	<dsp:oparam name="output">
	   <dsp:getvalueof id="elUrl" idtype="java.lang.String" param='<%= "element." + urlProperty%>' >


	   <%  try {
	          ServletConfig conf = getServletConfig();
	          ServletContext mediaContext = conf.getServletContext();

	          if ( mediaContextParam!=null) {
	             mediaContext =    mediaContext.getContext(mediaContextParam);
	          }
		  // Add community ID since it's so darn useful to have...
		  String urlWithCommunity=elUrl+"?paf_community_id="+communityId;
	          RequestDispatcher disp=mediaContext.getRequestDispatcher(urlWithCommunity);
	          disp.include(request, response);
	       } 
	       catch ( Exception ex) {

	       } 
	   %>
	   </dsp:getvalueof>
	</dsp:oparam>
	<dsp:oparam name="empty">
	   <core:ifNotNull value="<%=defaultImageParam%>">
	       <img src="<%= defaultImageParam %>" border="0">
	   </core:ifNotNull>
	</dsp:oparam>
    </dsp:droplet>

</paf:InitializeGearEnvironment>
</dsp:page>
<!-- ******** End of Slot Gear display ******** -->
<%-- @version $Id: //app/portal/version/10.0.3/slotgear/slotgear.war/externalMedia.jsp#2 $$Change: 651448 $--%>

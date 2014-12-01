<!-- ******** Begin Slot Gear display ******** -->
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<%
   String origURI=pafEnv.getOriginalRequestURI();
   String contextPath = request.getContextPath() ;
   String clearGIF = contextPath+"/images/clear.gif";
   String infoGIF = contextPath+"/images/info.gif";

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
   String catalogUrl=pafEnv.getGearInstanceParameter("catalogUrl");
   // default to URL for Ecovida demo
   if (catalogUrl==null) { 
        catalogUrl="/EcoVida/b2b/catalog/product.jsp"; 
   } else if (catalogUrl.length()<1) { 
        catalogUrl="/EcoVida/b2b/catalog/product.jsp"; 
   }
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
     <table border=0 cellpadding=0 cellspacing=0>
       <tr><td><dsp:img src="<%=clearGIF%>"/></td></tr>
       <tr>
         <td> 
         <table border=0 cellpadding=4 cellspacing=0>
          <tr>
          <td valign="top">
            
          <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
          <dsp:getvalueof id="imageURL" idtype="java.lang.String" param="element.thumbnailImage.url">
          
            <core:switch value="<%=imageURL%>">
              <core:case value="<%=null%>">
                <core:ifNotNull value="<%=defaultImageParam%>">
	          <dsp:a href="<%=catalogUrl%>">
                      <dsp:param name="id" param="element.repositoryId"/>
                      <dsp:param name="Item" param="element"/>
                  <img border="0" hspace="10" src="<%= defaultImageParam %>"></dsp:a>
                </core:ifNotNull>
              </core:case>
              <core:defaultCase>
                <table bgcolor="#FFFFFF" border=0 cellpadding=0 cellspacing=0>
                  <tr>
                    <td><dsp:img src="<%=clearGIF%>" hspace="10" /></td>
                    <td>
		    <dsp:a href="<%=catalogUrl%>">
                      <dsp:param name="id" param="element.repositoryId"/>
                      <dsp:param name="Item" param="element"/>
			<img border="0" src="<%=imageURL%>"></dsp:a></td>
                    <td><dsp:img src="<%=clearGIF%>" hspace="28" /></td>
                  </tr>
                </table>
               </core:defaultCase>
             </core:switch>
          
          </dsp:getvalueof>
          </dsp:getvalueof>
          
          <p class="spacedText">
          <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
          <dsp:a href="<%=catalogUrl%>">
            <dsp:param name="id" param="element.repositoryId"/>
            <dsp:param name="Item" param="element"/>
            <b><dsp:valueof param="element.displayName">No name</dsp:valueof></b></dsp:a>
          </dsp:getvalueof>
              
            <br>
            <dsp:valueof param="element.longDescription"></dsp:valueof>
            </span>
	    <%--
            <dsp:droplet name="Switch">
              <dsp:param bean="Profile.transient" name="value"/>
              <dsp:oparam name="false">
                <dsp:img src="<%=clearGIF%>" vspace="2"/><br>
                Price:
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="element.childSKUs"/>
                  <dsp:param name="elementName" value="SKU"/>
                  <dsp:oparam name="output">
                    <dsp:droplet name="/atg/commerce/pricing/PriceItem">
                      <dsp:param name="product" param="element"/>
                      <dsp:param name="item" param="SKU"/>
                      <dsp:param name="elementName" value="PricingCommerceItem"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof id="pval0" param="PricingCommerceItem.priceInfo.amount"><dsp:include page="DisplayCurrencyType.jsp" flush="false"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>                
                      </dsp:oparam>
                    </dsp:droplet>            
                  </dsp:oparam>
                </dsp:droplet><br><dsp:img src="<%=clearGIF%>" vspace="2"/><br>
              </dsp:oparam>

            </dsp:droplet>
	    --%>
	    </td>
          </tr>
        </table></td>
    </table>
    </dsp:oparam>
  </dsp:droplet>

</paf:InitializeGearEnvironment>
</dsp:page>
<!-- ******** End of Slot Gear display ******** -->
<%-- @version $Id: //app/portal/version/10.0.3/slotgear/slotgear.war/productSlot.jsp#2 $$Change: 651448 $--%>

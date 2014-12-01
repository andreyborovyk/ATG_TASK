<%--
  This property viewer is for viewing the property type of a rankingProperty or an 
  availableRankingProperty as a resourced string. 
  
  @param  underlyingValue  The value of the prop that needs to be resourced.

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/propertyTypeDisplay.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <dspel:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dspel:importbean var="config"
                    bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle var="resourceBundle" basename="${config.resourceBundle}"/>

  <%-- Get the form properties --%>
  <dspel:getvalueof var="underlyingValue" param="underlyingValue"/>
        
  <dspel:droplet name="Switch">
    <dspel:param name="value" value="${underlyingValue}"/>
    <dspel:oparam name="string">
      <fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.string"/>
    </dspel:oparam>
    <dspel:oparam name="float">
      <fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.number"/>
    </dspel:oparam>
    <dspel:oparam name="integer">
      <fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.number"/>
    </dspel:oparam>
    <dspel:oparam name="boolean">
      <fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.boolean"/>
    </dspel:oparam>
    <dspel:oparam name="bool">
      <fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.boolean"/>
    </dspel:oparam>
    <dspel:oparam name="date">
      <fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.date"/>
    </dspel:oparam>
    <dspel:oparam name="enum">
      <fmt:message bundle="${resourceBundle}" key="indexedPropertySelector.enum"/>
    </dspel:oparam>
  </dspel:droplet>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/propertyTypeDisplay.jsp#2 $$Change: 651448 $--%>

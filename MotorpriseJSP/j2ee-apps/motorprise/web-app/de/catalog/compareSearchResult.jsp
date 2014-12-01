<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/RepositoryValues"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
<dsp:importbean bean="/atg/projects/b2bstore/catalog/SearchCompare"/>

<dsp:form action="compare.jsp" method="POST">
<dsp:droplet name="ForEach">
  <dsp:param bean="SearchCompare.searchResults" name="array"/>
  <dsp:param name="sortProperties" value="+displayName,+description"/>
  <dsp:oparam name="outputStart">
    <b>Suchergebnisse</b>
    <blockquote>
    <table border=0 cellpadding=0 cellspacing=0>
  </dsp:oparam>
  <dsp:oparam name="output">
    <tr>
    <td>
      <dsp:input bean="ProductListHandler.productIdList" paramvalue="element.repositoryId" type="checkbox"/>
      <dsp:valueof param="element.displayName"/> - <dsp:valueof param="element.description"/>
    </td>
    </tr>
  </dsp:oparam>
  <dsp:oparam name="outputEnd">
    </table></br>
    <dsp:input bean="ProductListHandler.addProductList" type="submit" value="Zur Liste hinzufügen"/>
    </blockquote>
  </dsp:oparam>
  <dsp:oparam name="empty">
    <b>Suchergebnisse</b>
    <blockquote>Keine entsprechenden Produkte gefunden</blockquote>
  </dsp:oparam>
</dsp:droplet>
</dsp:form>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/catalog/compareSearchResult.jsp#2 $$Change: 651448 $--%>

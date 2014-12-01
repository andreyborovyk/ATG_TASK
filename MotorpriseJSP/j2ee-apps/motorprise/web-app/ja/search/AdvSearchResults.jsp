<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/catalog/AdvProductSearch"/>

<DECLAREPARAM NAME="Skus" 
              CLASS="java.util.List" 
              DESCRIPTION="List of Skus">

<DECLAREPARAM NAME="ResultList" 
              CLASS="java.util.List" 
              DESCRIPTION="List of Products">

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="ResultList"/>
    <dsp:oparam name="outputStart">
      <b>検索条件に適合する製品が見つかりました</b>
      <br><br>
    </dsp:oparam>
    <dsp:oparam name="output"> 
      <%-- Display a link to the element: --%>
      <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
      <dsp:a page="<%=urlStr%>">
        <dsp:param name="id" param="element.repositoryId"/>
        <dsp:param name="navAction" value="jump"/>
        <dsp:param name="Item" param="element"/>   
        <dsp:valueof param="element.displayName">名前なし</dsp:valueof>&nbsp;-&nbsp;<dsp:valueof param="element.description"/>
      </dsp:a>
      </dsp:getvalueof>
      <br>
    </dsp:oparam>
    <dsp:oparam name="empty">
      <dsp:droplet name="Switch">
        <dsp:param bean="AdvProductSearch.PreviouslySubmitted" name="value"/>
        <dsp:oparam name="true">
          <b>検索条件に合う製品は見つかりませんでした</b>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/search/AdvSearchResults.jsp#2 $$Change: 651448 $--%>

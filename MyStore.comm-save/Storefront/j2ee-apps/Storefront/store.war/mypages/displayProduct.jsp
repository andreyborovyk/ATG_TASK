<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" prefix="dspel" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>

<dsp:droplet name='ProductLookup'>  
  <dsp:oparam name="output">
    <dsp:param name='product' param='element'/>
<html>
<head>
  <title><dsp:valueof param="product.displayName"/></title>
</head>

<body bgcolor="#ffffff">
  <dsp:include page='header.jsp'>
<dsp:param name='pagetitle' param='product.displayName'/>
</dsp:include>


<b>Description:</b> <dsp:valueof param='product.description'/><BR>

<img src="<dsp:valueof param='product.smallImage.url'/>"/>


<h4>Those who bought this also bought</h4>

<dsp:droplet name='ForEach'>
<dsp:param name='array' param='product.relatedProducts'/>
<dsp:oparam name="output">
<dsp:a href='displayProduct.jsp'>
<dsp:param name='id' param='element.repositoryId'/>
<dsp:valueof param="element.displayName"/>
<img src="<dsp:valueof param='element.thumbnailImage.url'/>"/>
</dsp:a>
</dsp:oparam>

</dsp:droplet>











</body>
</html>

  </dsp:oparam>


</dsp:droplet>



</dsp:page>

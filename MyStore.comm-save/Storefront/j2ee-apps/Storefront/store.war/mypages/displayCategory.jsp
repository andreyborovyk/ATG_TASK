<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" prefix="dspel" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>

<dsp:droplet name='CategoryLookup'>  
  <dsp:oparam name="output">
   <dsp:param name='category' param='element'/> 
 
<html>
<head>
  <title><dsp:valueof param="category.displayName"/></title>
</head>

<body bgcolor="#ffffff">

<dsp:include page='header.jsp'>
<dsp:param name='pagetitle' param='category.displayName'/>
</dsp:include>

<h4>Child Categories</h4>


<dsp:droplet name='ForEach'>
  <dsp:param param="category.childCategories" name="array"/>
<dsp:oparam name="output">
  <dsp:param name='childCategory' param='element'/>

<dsp:a href='displayCategory.jsp'>
<dsp:param name='id' param='childCategory.repositoryId'/>
  <dsp:valueof param="childCategory.displayName"/>
</dsp:a>
<br>



</dsp:oparam>


</dsp:droplet>


<h4>Child Products</h4>


<dsp:droplet name='ForEach'>
  <dsp:param param="category.childProducts" name="array"/>
<dsp:oparam name="output">
  <dsp:param name='childProduct' param='element'/>

<dsp:a href='displayProduct.jsp'>
<dsp:param name='id' param='childProduct.repositoryId'/>
  <dsp:valueof param="childProduct.displayName"/>

<img src="<dsp:valueof param='childProduct.thumbnailImage.url'/>">


</dsp:a>
<br>



</dsp:oparam>


</dsp:droplet>




</body>
</html>

 </dsp:oparam>

</dsp:droplet>



</dsp:page>

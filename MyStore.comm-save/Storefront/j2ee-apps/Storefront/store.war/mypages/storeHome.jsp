<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" prefix="dspel" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<html>
<head>
  <title></title>
</head>

<body bgcolor="#ffffff">
<dsp:include page='header.jsp'>
<dsp:param name='pagetitle' value='Browse Catalog'/>
</dsp:include>


<dsp:droplet name='ForEach'>
  <dsp:param bean="Profile.catalog.rootNavigationCategory.childCategories" name="array"/>
<dsp:oparam name="output">
<dsp:a href='displayCategory.jsp'>
<dsp:param name='id' param='element.repositoryId'/>
<dsp:valueof param="element.displayName"/>
</dsp:a>
<BR>
</dsp:oparam>

</dsp:droplet>




</body>
</html>


</dsp:page>

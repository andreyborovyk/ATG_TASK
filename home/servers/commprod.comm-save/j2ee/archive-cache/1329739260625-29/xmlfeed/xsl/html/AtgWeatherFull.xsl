<?xml version="1.0" encoding="ISO-8859-1" ?> 

<!-- set up non breaking spaces to be typed w/o number -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>





<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- make it so we can use break elements -->
<xsl:output method="html"/>

<xsl:param name="high" />
<xsl:param name="low" />
<xsl:param name="currentTemp" />
<xsl:param name="currentCond" />

<xsl:template match="/">
   <xsl:apply-templates select="//AtgWeather"/>
</xsl:template>

<xsl:template match="AtgWeather">
   <xsl:apply-templates select="Forecast"/>
</xsl:template>

<xsl:template match="Forecast">
  
   <xsl:apply-templates select="current_weather"/>
<table><tr>
 <td>&nbsp;&nbsp;</td>
   <xsl:for-each select="day">
   <td align="center">
         <xsl:value-of select="day_name" /> <br/>
        <font size="-2" class="smaller">
        <xsl:value-of select="day_forecast" /> <br/>
        <xsl:value-of select="$high" />&nbsp;<b><xsl:value-of select="day_high" /></b><br/>
        <xsl:value-of select="low" />&nbsp;&nbsp;<b><xsl:value-of select="day_low" /></b></font><br/>
        <img><xsl:attribute name="src"><xsl:value-of 
        select="day_image_url"/></xsl:attribute></img>
     </td>
     <td>&nbsp;&nbsp;</td>
   </xsl:for-each>    
 </tr>
</table><br/><br/>
</xsl:template>

<xsl:template match="current_weather">
<center><table><tr><td>
   <img><xsl:attribute name="src"><xsl:value-of     
     select="current_image_url"/></xsl:attribute>
 	<xsl:attribute name="align">left</xsl:attribute></img><br/> 
      <font size="-1" class="small">Weather&nbsp;for&nbsp;<xsl:value-of select="//cityname"/></font><br/><font size="-2" class="smaller">
    <xsl:value-of select="$currentCond" />&nbsp;<xsl:value-of select="current_condition"/><br/>
    <xsl:value-of select="$currentTemp" />&nbsp;<xsl:value-of select="current_temp"/></font><br />
</td></tr></table></center>
</xsl:template>




</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/html/AtgWeatherFull.xsl#2 $$Change: 651448 $-->

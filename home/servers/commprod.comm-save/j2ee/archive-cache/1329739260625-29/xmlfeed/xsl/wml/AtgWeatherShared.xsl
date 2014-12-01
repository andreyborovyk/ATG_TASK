<?xml version="1.0" encoding="ISO-8859-1" ?> 

<!-- set up non breaking spaces to be typed w/o number -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- make it so we can use break elements -->
<xsl:output method="xml" omit-xml-declaration="yes"/>

<xsl:param name="currentTemp" />

<xsl:template match="/">
   <xsl:apply-templates select="//AtgWeather"/>
</xsl:template>

<xsl:template match="AtgWeather">
   <xsl:apply-templates select="Forecast"/>
</xsl:template>

<xsl:template match="Forecast">
   <p align="center"><xsl:value-of select="cityname"/></p>
   <p align="center">
       <xsl:apply-templates select="current_weather"/>
       <xsl:value-of select="timestamp"/>
   </p>
</xsl:template>

<xsl:template match="current_weather">
    <xsl:value-of select="current_condition"/>
    <table columns="2">    
      <tr><td><xsl:value-of select="$currentTemp" /></td>
      <td><xsl:value-of select="current_temp"/>&#xB0;</td></tr>
    </table>
</xsl:template>

</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/wml/AtgWeatherShared.xsl#2 $$Change: 651448 $-->

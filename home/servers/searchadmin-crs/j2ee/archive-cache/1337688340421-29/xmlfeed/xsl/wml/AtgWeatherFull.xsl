<?xml version="1.0" encoding="ISO-8859-1" ?> 

<!-- set up non breaking spaces to be typed w/o number -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- make it so we can use break elements -->
<xsl:output method="xml" omit-xml-declaration="yes"/>

<xsl:param name="currentTemp" />
<xsl:param name="windDirection" />
<xsl:param name="windSpeed" />
<xsl:param name="humidity" />
<xsl:param name="pressure" />
<xsl:param name="visibility" />
<xsl:param name="high" />
<xsl:param name="low" />

<xsl:template match="/">
   <xsl:apply-templates select="//AtgWeather"/>
</xsl:template>

<xsl:template match="AtgWeather">

   <xsl:apply-templates select="Forecast"/>

</xsl:template>

<xsl:template match="Forecast">
  
     <xsl:apply-templates select="current_weather"/>

     <xsl:for-each select="day">
     <xsl:variable name="day_name"><xsl:value-of select="day_name" /></xsl:variable>
     <xsl:variable name="id"><xsl:number value="position()" /></xsl:variable>
     <card id="day{$id}" title="{$day_name}">
       <p align="center">
        <xsl:value-of select="day_forecast" />
        <table columns="2">
          <tr><td><xsl:value-of select="$high" /></td><td><xsl:value-of select="day_high" />&#xB0;</td></tr>
          <tr><td><xsl:value-of select="$low" /></td><td><xsl:value-of select="day_low" />&#xB0;</td></tr>
        </table>
       </p>
       <do type="accept" label="Next">
         <go href="#day{$id+1}"/>
       </do>
     </card>
     </xsl:for-each>    

</xsl:template>

<xsl:template match="current_weather">
  <card id="current" title="Current">
    <p align="center" mode="wrap">
    <xsl:value-of select="current_condition"/>
    <table columns="2">
     <tr><td><xsl:value-of select="$currentTemp" /></td><td><xsl:value-of select="current_temp"/>&#xB0;</td></tr>
     <tr><td><xsl:value-of select="$windDirection" /></td><td><xsl:value-of select="wind_direction"/></td></tr>
     <tr><td><xsl:value-of select="$windSpeed" /></td><td><xsl:value-of select="wind_speed"/></td></tr>
     <tr><td><xsl:value-of select="$humidity" /></td><td><xsl:value-of select="humidity"/></td></tr>
     <tr><td><xsl:value-of select="$pressure" /></td><td><xsl:value-of select="pressure"/></td></tr>
     <tr><td><xsl:value-of select="$visibility" /></td><td><xsl:value-of select="visibility"/></td></tr>
    </table>
    <xsl:value-of select="timestamp"/>
    </p>
    <do type="accept" label="Next">
      <go href="#day1"/>
    </do>
  </card>
</xsl:template>




</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/wml/AtgWeatherFull.xsl#2 $$Change: 651448 $-->

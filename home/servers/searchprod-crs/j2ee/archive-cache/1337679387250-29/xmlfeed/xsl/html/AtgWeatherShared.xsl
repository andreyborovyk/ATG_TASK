<?xml version="1.0" encoding="ISO-8859-1" ?> 

<!-- set up non breaking spaces to be typed w/o number -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- make it so we can use break elements -->
<xsl:output method="html"/>

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
   <table><tr bgcolor="#cccccc"><td>
   <font size="-1" class="small">5-Day Forecast | <xsl:value-of select="cityname"/></font><br/>
   </td>
   </tr>
   
   <tr>
   <td>
   
   <table cellpadding="5"><tr>
    <xsl:for-each select="day">
   <td align="center">
         <xsl:value-of select="day_name" /> <br/>
        <font size="-2" class="smaller">
                <img><xsl:attribute name="src"><xsl:value-of 
        select="day_image_url"/></xsl:attribute></img> <br/>
        <xsl:value-of select="day_forecast" /> <br/><br/>
        <xsl:value-of select="$high" />: <xsl:value-of select="day_high" /><br/>
        <xsl:value-of select="$low" />: <xsl:value-of select="day_low" /><br/></font>

     </td>

   </xsl:for-each> 
   </tr></table> 
   
   </td></tr>
   </table>
   <xsl:apply-templates select="current_weather"/>
</xsl:template>



<xsl:template match="current_weather">
    <center>
    <!--
    The CURRENT WEATHER items have been removed. 
    Uncomment if you would like to include them in your gear.
    <table>
    
    <font size="-1" class="small">
    <tr><td> 
    <img><xsl:attribute name="src"><xsl:value-of 
    select="current_image_url"/></xsl:attribute></img></td>
    <td><xsl:value-of select="current_condition"/></td></tr>
    <tr><td><xsl:value-of select="$currentTemp" /></td>
    <td><xsl:value-of select="current_temp"/></td></tr>
    
    <tr><td><xsl:value-of select="$windDirection" /></td><td><xsl:value-of select="wind_direction"/></td></tr>
    <tr><td><xsl:value-of select="$windSpeed" /></td><td><xsl:value-of select="wind_speed"/></td></tr>
    <tr><td><xsl:value-of select="$humidity" /></td><td><xsl:value-of select="humidity"/></td></tr>
    <tr><td><xsl:value-of select="$pressure" /></td><td><xsl:value-of select="pressure"/></td></tr>
    <tr><td><xsl:value-of select="$visibility" /></td><td><xsl:value-of select="visibility"/></td></tr>
    
    
    </font></table>
    -->
    </center>
</xsl:template>


</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/html/AtgWeatherShared.xsl#2 $$Change: 651448 $-->

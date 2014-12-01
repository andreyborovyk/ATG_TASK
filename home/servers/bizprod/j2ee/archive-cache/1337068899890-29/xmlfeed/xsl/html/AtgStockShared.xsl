<?xml version="1.0" encoding="ISO-8859-1" ?> 

<!-- set up non breaking spaces to be typed w/o number -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- make it so we can use break elements -->
<xsl:output method="html"/>

<xsl:param name="ticker" />
<xsl:param name="last" />
<xsl:param name="change" />
<xsl:param name="percentage" />
<xsl:param name="open" />
<xsl:param name="high" />
<xsl:param name="low" />


<xsl:template match="/">
   <xsl:apply-templates select="//AtgStock"/>
</xsl:template>

<xsl:template match="AtgStock">


<table width="100%" cellpadding="0" cellspacing="2" border="0">
     
   <tr bgcolor="#cccccc"><td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="$ticker" /></font></td>
       <td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="$last" /></font></td>
       <td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="$change" /></font></td>
       <td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="$percentage" /></font></td>
       <td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="$open" /></font></td>
       <td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="$high" /></font></td>
       <td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="$low" /></font></td></tr>
 <xsl:for-each select="Stock">


    
     <tr>
     <td><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="ticker" /> </font></td>

     
     <td align="right"><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="last" /> </font></td>

     
     <td align="right"><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="substring-before(change,'(')" /> </font></td>

     <td align="right"><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="substring-before(substring-after(change,'('),')')" /> </font></td>
     
     <td align="right"><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="open" /> </font></td>

     
     <td align="right"><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="high" /> </font></td>

     
     <td align="right"><font size="-1" face="verdana,arial,geneva,sans"><xsl:value-of select="low" /> </font></td>
     </tr>

     
  </xsl:for-each>
  </table>

    
</xsl:template>



</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/html/AtgStockShared.xsl#2 $$Change: 651448 $-->

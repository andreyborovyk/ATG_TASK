<?xml version="1.0" encoding="ISO-8859-1" ?> 

<!-- set up non breaking spaces to be typed w/o number -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- make it so we can use break elements -->
<xsl:output method="xml" omit-xml-declaration="yes"/>

<xsl:param name="ticker" />
<xsl:param name="last" />
<xsl:param name="change" />
<xsl:param name="percentage" />
<xsl:param name="open" />
<xsl:param name="high" />
<xsl:param name="low" />
<xsl:param name="volume" />

<xsl:template match="/">
   <xsl:apply-templates select="//AtgStock"/>
</xsl:template>

<xsl:template match="AtgStock">


 <xsl:for-each select="Stock"> 
   <xsl:variable name="ticker"><xsl:value-of select="ticker" /></xsl:variable>
   <xsl:variable name="id"><xsl:number value="position()" /></xsl:variable>
   <card id="stock{$id}" title="{$ticker}">
    <p align="center">
    <table columns="2">
     <tr><td><xsl:value-of select="$last" /></td><td><xsl:value-of select="last" /></td></tr>
     <tr><td><xsl:value-of select="$change" /></td><td><xsl:value-of select="substring-before(change,'(')" /></td></tr>
     <tr><td><xsl:value-of select="$percentage" /></td><td><xsl:value-of select="substring-before(substring-after(change,'('),')')" /></td></tr>
     <tr><td><xsl:value-of select="$open" /></td><td><xsl:value-of select="open" /></td></tr>
     <tr><td><xsl:value-of select="$high" /></td><td><xsl:value-of select="high" /></td></tr>	
     <tr><td><xsl:value-of select="$low" /></td><td><xsl:value-of select="low" /></td></tr>	
     <tr><td><xsl:value-of select="$volume" /></td><td><xsl:value-of select="volume" /></td></tr>	
    </table>
    </p>
    <do type="accept" label="Next">
     <go href="#stock{$id+1}"/>
    </do>
    </card>

  </xsl:for-each>
 

</xsl:template>



</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/wml/AtgStockFull.xsl#2 $$Change: 651448 $-->

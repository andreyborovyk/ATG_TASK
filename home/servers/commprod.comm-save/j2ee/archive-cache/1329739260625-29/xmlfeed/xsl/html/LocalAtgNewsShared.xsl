<?xml version='1.0' encoding='utf-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="4.0" encoding="utf-8" />
	<xsl:param name="articleGearUrl"/>


<xsl:template match="/">
   <xsl:apply-templates select="//AtgNews"/>
</xsl:template>

<xsl:template match="AtgNews">
   <xsl:for-each select="category">
   <font size="-1" class="small"><b><xsl:value-of select="@name" /></b></font>
   <ul>   
   <xsl:for-each select="story">
   <li>
   
   <xsl:text disable-output-escaping="yes"><![CDATA[<a href="]]></xsl:text><xsl:value-of select="$articleGearUrl" /><xsl:text disable-output-escaping="yes"><![CDATA[&]]></xsl:text>articleID=<xsl:value-of select="id" /><xsl:text disable-output-escaping="yes"><![CDATA[">]]></xsl:text><xsl:value-of select="headline" /><xsl:text disable-output-escaping="yes"><![CDATA[</a>]]></xsl:text>

   </li>
   </xsl:for-each>

   </ul>
   </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/html/LocalAtgNewsShared.xsl#2 $$Change: 651448 $-->

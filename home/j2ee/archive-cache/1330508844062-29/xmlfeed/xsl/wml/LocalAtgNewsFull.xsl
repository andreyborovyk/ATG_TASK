<?xml version='1.0' encoding='utf-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml"  omit-xml-declaration="yes"/>
<xsl:param name="articleGearUrl"/>


<xsl:template match="/">
   <xsl:apply-templates select="//AtgNews"/>
</xsl:template>

<xsl:template match="AtgNews">

  <card id="new" title="news">   
   <xsl:apply-templates select="story" />
  </card>

</xsl:template>

<xsl:template match="story">
   
  <p align="center" mode="wrap"><b><xsl:value-of select="headline" /></b></p>
  <p mode="wrap"><xsl:value-of select="summary" /></p>

</xsl:template>

</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/wml/LocalAtgNewsFull.xsl#2 $$Change: 651448 $-->

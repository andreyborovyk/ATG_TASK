<?xml version='1.0' encoding='utf-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" omit-xml-declaration="yes"/>
<xsl:param name="articleGearUrl"/>


<xsl:template match="/">
   <xsl:apply-templates select="//AtgNews"/>
</xsl:template>

<xsl:template match="AtgNews">

   <xsl:for-each select="category">
     
       <p align="center" mode="nowrap">
        <b><xsl:value-of select="@name" /></b>
       </p>

       <p mode="wrap">
        <xsl:for-each select="story">
          <xsl:if test="position() = 1">
           <xsl:value-of select="headline" /><br/>
          </xsl:if>
        </xsl:for-each>
       </p>

   </xsl:for-each>

</xsl:template>

</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/wml/LocalAtgNewsShared.xsl#2 $$Change: 651448 $-->

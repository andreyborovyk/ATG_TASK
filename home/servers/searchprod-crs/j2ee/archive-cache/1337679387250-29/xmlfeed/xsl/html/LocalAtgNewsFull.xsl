<?xml version='1.0' encoding='utf-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="4.0" encoding="utf-8" />
	<xsl:param name="articleGearUrl"/>


<xsl:template match="/">
   <xsl:apply-templates select="//AtgNews"/>
</xsl:template>

<xsl:template match="AtgNews">
   
  <xsl:apply-templates select="story" />
</xsl:template>

<xsl:template match="story">
   

  <h3><xsl:value-of select="headline" /></h3>
  <p><h4> <xsl:value-of select="summary" /> </h4></p>
  <xsl:call-template name="substitute">
            <xsl:with-param name="string">
              <xsl:value-of select="content" />
            </xsl:with-param>
           
  </xsl:call-template>
      
</xsl:template>


<xsl:template name="substitute">
   <xsl:param name="string" />
   <xsl:param name="from" select="'&#xA;'" />
   <xsl:param name="to">
      <br />
   </xsl:param>
   <xsl:choose>
      <xsl:when test="contains($string, $from)">
         <xsl:value-of select="substring-before($string, $from)" />
         <xsl:copy-of select="$to" />
         <xsl:call-template name="substitute">
            <xsl:with-param name="string"
                            select="substring-after($string, $from)" />
            <xsl:with-param name="from" select="$from" />
            <xsl:with-param name="to" select="$to" />
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$string" />
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>                



</xsl:stylesheet>
<!-- @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeed.war/xsl/html/LocalAtgNewsFull.xsl#2 $$Change: 651448 $-->

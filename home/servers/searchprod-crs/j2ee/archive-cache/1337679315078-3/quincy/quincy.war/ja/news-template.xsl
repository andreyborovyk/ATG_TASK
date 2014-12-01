<?xml version="1.0"?> <xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
> <xsl:output method="html"/> <xsl:template match="/newsarticle"> <p><b><xsl:value-of select="headline"/></b></p>

	<p>著者 <xsl:value-of select="author"/></p>

	<xsl:value-of select="content/text()" disable-output-escaping="yes"/> </xsl:template> <xsl:template match="p"> <p><xsl:apply-templates/></p>
</xsl:template> <xsl:template match="b"> <b><xsl:apply-templates/></b> </xsl:template> <xsl:template match="break"> <br/>
</xsl:template> <xsl:template match="dollar"> $ </xsl:template> </xsl:stylesheet>
<!-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/news-template.xsl#2 $$Change: 651448 $-->

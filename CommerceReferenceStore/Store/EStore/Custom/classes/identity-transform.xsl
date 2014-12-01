<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> 
 <xsl:strip-space elements="*"/>
    <xsl:output method="xml" indent='yes' encoding='UTF-8' version='1.0' omit-xml-declaration="yes"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="*">
        <xsl:element name="{name(.)}">
            <xsl:for-each select="@*">
                <xsl:attribute name="{name(.)}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="text()">
	<xsl:value-of select="."/>
    </xsl:template>
</xsl:stylesheet>

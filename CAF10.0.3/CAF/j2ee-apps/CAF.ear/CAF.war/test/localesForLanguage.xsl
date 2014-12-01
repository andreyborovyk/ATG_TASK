<?xml version='1.0'?>
<xsl:stylesheet version = "1.0"
           xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html"
          omit-xml-declaration="yes"/>

  <xsl:template match="localeHeading">
    <tr>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>

  <xsl:template match="column">
    <xsl:call-template name="column">
      <xsl:with-param name="name">
        <xsl:value-of select="@name"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="column">
    <xsl:param name="name"/>
    <th class="headingCell">
      <xsl:value-of select="$name"/>
    </th>
  </xsl:template>

  <xsl:template match="locale">
    <xsl:call-template name="locale">
      <xsl:with-param name="displayName">
        <xsl:value-of select="@displayName"/>
      </xsl:with-param>
      <xsl:with-param name="language">
        <xsl:value-of select="@language"/>
      </xsl:with-param>
      <xsl:with-param name="displayCountry">
        <xsl:value-of select="@displayCountry"/>
      </xsl:with-param>
      <xsl:with-param name="country">
        <xsl:value-of select="@country"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="locale">
    <xsl:param name="displayName"/>
    <xsl:param name="language"/>
    <xsl:param name="displayCountry"/>
    <xsl:param name="country"/>
    <xsl:if test="string-length($country) > 0">
      <tr>
        <td class="cell">
          <xsl:value-of select="$displayName"/>
        </td>
        <td class="cell">
          <xsl:value-of select="$language"/>_<xsl:value-of select="$country"/>
        </td>
        <td class="cell">
          <xsl:value-of select="$displayCountry"/>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template match="localesForLanguage">
    <h3>
      <xsl:value-of select="@title"/>
    </h3>
    <xsl:if test='count(//child::locale) > 0'>
      <table border="0" 
           cellpadding="0" 
           cellspacing="0">
        <xsl:apply-templates/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
<!-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/localesForLanguage.xsl#2 $$Change: 651448 $-->

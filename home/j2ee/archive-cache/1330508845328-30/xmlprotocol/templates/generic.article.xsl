<?xml version='1.0' encoding='utf-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
<ATGCOPYRIGHT>

 * Copyright (C) 2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

A template for displaying an article using sample data.

Author: J Marino 

-->
	<xsl:output method="html" version="4.0" encoding="utf-8"/>
	<xsl:param name="articleGearUrl"/>
	<xsl:param name="headlinesUrl"/>
	<xsl:template match="/">
		<xsl:apply-templates select="//article"/>
		<br/>
		<br/>
	</xsl:template>
	<xsl:template match="article">
		<font size="+1">
			<b>
				<xsl:value-of select="article.title"/>
			</b>
		</font>
		<br/>
		<br/>
		<font size="-1">
			<i>
				<xsl:value-of select="article.source"/>  <xsl:variable name="month">
					<xsl:value-of select="substring(article.date,5,2)">
					</xsl:value-of></xsl:variable>
				<xsl:choose>
					<xsl:when test="$month='01'">January</xsl:when>
					<xsl:when test="$month='02'">February</xsl:when>
					<xsl:when test="$month='03'">March</xsl:when>
					<xsl:when test="$month='04'">April</xsl:when>
					<xsl:when test="$month='05'">May</xsl:when>
					<xsl:when test="$month='06'">June</xsl:when>
					<xsl:when test="$month='07'">July</xsl:when>
					<xsl:when test="$month='08'">August</xsl:when>
					<xsl:when test="$month='09'">September</xsl:when>
					<xsl:when test="$month='10'">October</xsl:when>
					<xsl:when test="$month='11'">November</xsl:when>
					<xsl:when test="$month='12'">December</xsl:when>
				</xsl:choose> <xsl:value-of select="substring(article.date,7,2)"></xsl:value-of>, <xsl:value-of select="substring(article.date,1,4)"></xsl:value-of>
			</i>
		</font>
		<br/>
		<xsl:variable name="theAuthor">
			<xsl:value-of select="article.byline">
			</xsl:value-of>
		</xsl:variable>
		<xsl:if test="string-length($theAuthor)&gt;1">
			<br/>
			<br/>
			<font size="-1">
				<b>By <xsl:value-of select="$theAuthor"/></b>
			</font>
		</xsl:if>
		<br/>
		<xsl:apply-templates select="article.body//para"/>
	</xsl:template>
	<xsl:template match="para">
		<xsl:variable name="thePara">
			<xsl:value-of select=".">
			</xsl:value-of>
		</xsl:variable>
		<xsl:if test="string-length($thePara)&gt;1">
			<font size="+1">
				<br/>
				<xsl:value-of select="$thePara"/>
				<br/>
			</font>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c)1998-2001 eXcelon Corp.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" url="file://c:\workspace\app\xmlprotocol\main\xmlprotocol.war\web\data\generic.articles\article1.xml" htmlbaseurl="" processortype="internal" commandline="" additionalpath="" additionalclasspath=""/></scenarios><MapperInfo  srcSchemaPath="" srcSchemaRoot="" destSchemaPath="" destSchemaRoot="" />
</metaInformation>
-->
<!-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/templates/generic.article.xsl#2 $$Change: 651448 $-->

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

A template for displaying a list of avalable categories from the JSP
that generates sample data.

Author: J Marino 

The following paramaters are passed in by the JSP that calls this template:

headlinesGearUrl is the base URL that points back to the Gear to display headlines in 
                 a particular folder  The template calculates the full URL which includes the base 
			     URL and a folder id parameter which is used by the gear to retrieve headlines in a  
			     folder from the service provider.
			     

-->
	<xsl:output method="html" version="4.0" encoding="utf-8"/>
	<xsl:param name="headlinesGearUrl"/>
	<xsl:template match="/">
		<xsl:apply-templates select="//category"/>
	</xsl:template>
	<xsl:template match="category">
		<img src="/gear/xmlprotocol/templates/images/folder_closed16.gif"/>
		<xsl:text disable-output-escaping="yes"><![CDATA[<a href="]]></xsl:text>
		<xsl:value-of select="$headlinesGearUrl"/>
		<xsl:text disable-output-escaping="yes"><![CDATA[&]]></xsl:text>xmlprotocol_categories=<xsl:value-of select="category.info/@id"/><xsl:text disable-output-escaping="yes"><![CDATA[">]]></xsl:text>
		<xsl:value-of select="category.name"/>
		<xsl:text disable-output-escaping="yes"><![CDATA[</a>]]></xsl:text>
		<br/>
		<br/>
	</xsl:template>
</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c)1998-2001 eXcelon Corp.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" url="file://c:\workspace\app\xmlprotocol\main\xmlprotocol.war\web\data\generic.categories.xml" htmlbaseurl="" processortype="internal" commandline="" additionalpath="" additionalclasspath=""/></scenarios><MapperInfo  srcSchemaPath="" srcSchemaRoot="" destSchemaPath="" destSchemaRoot="" />
</metaInformation>
-->
<!-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/templates/generic.categories.xsl#2 $$Change: 651448 $-->

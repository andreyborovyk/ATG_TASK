<?xml version='1.0' encoding='utf-8' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
<ATGCOPYRIGHT>

 * Copyright (C) 2011 Art Technology Group, Inc.
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

xpath.adaptor.generator.xsl

Generates XMLProtocol gear adaptors extending the GernericXPathAdaptor class.
Takes an adaptor manifest as imput.

Author: J Marino  

-->
	<xsl:output method="text" encoding="utf-8"/>
	<xsl:template match="/">
		<xsl:apply-templates select="adaptor.manifest"/>
	</xsl:template>

	<xsl:template match="adaptor.manifest">
		<xsl:call-template name="copyright">
		</xsl:call-template>
		
		package <xsl:value-of select="adaptor.definition/@package"/>;
		
		/**
		<xsl:value-of select="adaptor.definition/comment"/>
		**/
		
		import atg.portal.gear.xmlprotocol.*;

		public class <xsl:value-of select="adaptor.definition/@class.name"/> extends XPathBaseAdaptor implements ConversationAdaptor{

		  public static String ADAPTOR_DESCRIPTION     = "<xsl:value-of select="adaptor.definition/description"/>";
		  public static String ADAPTOR_VERSION         = "<xsl:value-of select="adaptor.definition/version"/>";

		  //initialize static values
		  {
		    XPATH_RESPONSE_ERROR_MSG         = "<xsl:value-of select="adaptor.definition/response.config/xpath.response.error.msg"/>";

		    XPATH_AUTHENTICATION_ERROR_MSG   = "<xsl:value-of select="adaptor.definition/response.config/xpath.authentication.error.msg"/>";
		    XPATH_SESSION_ID                 = "<xsl:value-of select="adaptor.definition/response.config/xpath.session.id"/>";
		    XPATH_CATEGORY_NAMES             = "<xsl:value-of select="adaptor.definition/response.config/xpath.category.names"/>";
		    XPATH_CATEGORY_IDS               = "<xsl:value-of select="adaptor.definition/response.config/xpath.category.ids"/>";
		    XPATH_BOOKMARKS                  = "<xsl:value-of select="adaptor.definition/response.config/xpath.bookmarks"/>";

		    XPATH_AUTHENTICATION_STATUS      = "<xsl:value-of select="adaptor.definition/response.config/xpath.authentication.status"/>";
		    AUTHENTICATION_STATUS_OK         = <xsl:value-of select="adaptor.definition/response.config/authentication.status.ok"/>;

		    //URL param names
		    USER_ID                          = "<xsl:value-of select="adaptor.definition/param.config/user.id"/>";
		    PASSWORD                         = "<xsl:value-of select="adaptor.definition/param.config/password"/>";
		    CATEGORIES_BEGIN                 = "<xsl:value-of select="adaptor.definition/param.config/categories.begin"/>";
		    CATEGORIES_END                   = "<xsl:value-of select="adaptor.definition/param.config/categories.end"/>";
		    NUM_HEADLINES                    = "<xsl:value-of select="adaptor.definition/param.config/num.headlines"/>";
		    BOOKMARKS                        = "<xsl:value-of select="adaptor.definition/param.config/bookmarks"/>";
		    BOOKMARKS_SEPARATOR              = "<xsl:value-of select="adaptor.definition/param.config/bookmarks.separator"/>";
		    ARTICLE_ID                       = "<xsl:value-of select="adaptor.definition/param.config/article.id"/>";
		    SESSION_ID                       = "<xsl:value-of select="adaptor.definition/param.config/session.id"/>";
                    HEADLINES_PARAMS                 = "<xsl:value-of select="adaptor.definition/param.config/headline.params"/>";
                    CATEGORY_PARAMS                  = "<xsl:value-of select="adaptor.definition/param.config/category.params"/>";
                    ARTICLE_PARAMS                   = "<xsl:value-of select="adaptor.definition/param.config/article.params"/>";
                    AUTHENTICATE_PARAMS              = "<xsl:value-of select="adaptor.definition/param.config/authentication.params"/>";
            AUTHENTICATION_TYPE              = <xsl:value-of select="adaptor.definition/provider.config/authentication.type"/>; 
		  }

          
		  public boolean supportsMultipleCategories(){
			return <xsl:value-of select="adaptor.definition/provider.config/supports.multiple.categories"/>;
 		   }


		}
	</xsl:template>


	<xsl:template name="copyright">
	/*
	 * Copyright (C) 2011 Art Technology Group, Inc.
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
	 */

	 /**
	  * Auto-generated by XMLProtocol Gear adaptor generator stylesheet
	  * Please do not edit this file
	  */
  
  </xsl:template>

</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c)1998-2001 eXcelon Corp.
<metaInformation>
<scenarios/><MapperInfo  srcSchemaPath="" srcSchemaRoot="" destSchemaPath="" destSchemaRoot="" />
</metaInformation>
-->
<!-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/etc/adaptor.templates/xpath.adaptor.generator.xsl#2 $$Change: 651448 $-->

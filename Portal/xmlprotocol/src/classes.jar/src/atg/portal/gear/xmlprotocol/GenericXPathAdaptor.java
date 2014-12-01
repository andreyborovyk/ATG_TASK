
	/*
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
	 */

	 /**
	  * Auto-generated by XMLProtocol Gear adaptor generator stylesheet
	  * Please do not edit this file
	  */
  
  
		
		package atg.portal.gear.xmlprotocol;
		
		/**
		This adaptor works with local data in the web application
		**/
		
		import atg.portal.gear.xmlprotocol.*;

		public class GenericXPathAdaptor extends XPathBaseAdaptor implements ConversationAdaptor{

		  public static String ADAPTOR_DESCRIPTION     = "Generic Sample Demo Adaptor";
		  public static String ADAPTOR_VERSION         = "1.0";

		  //initialize static values
		  {
		    XPATH_RESPONSE_ERROR_MSG         = "//error.message";

		    XPATH_AUTHENTICATION_ERROR_MSG   = "//error.message";
		    XPATH_SESSION_ID                 = "/login.response/session.id";
		    XPATH_CATEGORY_NAMES             = "/categories.response//category.name";
		    XPATH_CATEGORY_IDS               = "/categories.response//category.info/@id";
		    XPATH_BOOKMARKS                  = "/categories.response//category.info/@bookmark";

		    XPATH_AUTHENTICATION_STATUS      = "/login.response/@status";
		    AUTHENTICATION_STATUS_OK         = 0;

		    //URL param names
		    USER_ID                          = "USER_ID";
		    PASSWORD                         = "PASSWORD";
		    CATEGORIES_BEGIN                 = "CATEGORIES";
		    CATEGORIES_END                   = "";
		    NUM_HEADLINES                    = "NUM_HEADLINES";
		    BOOKMARKS                        = "BOOKMARKS";
		    BOOKMARKS_SEPARATOR              = ",";
		    ARTICLE_ID                       = "ARTICLE_ID";
		    SESSION_ID                       = "SESSION_ID";
                    HEADLINES_PARAMS                 = "";
                    CATEGORY_PARAMS                  = "";
                    ARTICLE_PARAMS                   = "";
                    AUTHENTICATE_PARAMS              = "";
            AUTHENTICATION_TYPE              = AUTH_STATELESS; 
		  }

          
		  public boolean supportsMultipleCategories(){
			return true;
 		   }


		}
	
/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.projects.store.droplet;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;


/**
 * This droplet performs a look up in the repository to obtain localized resource text message templates either
 * by matching on a specific key to obtain a single resource text message template or by matching on a tag to
 * obtain zero or more resource text messages 'tagged' with this value. It is not possible to find a match on
 * both key and tag values, if a key is supplied that is the value used to find a match.
 * The ‘key’ property is a unique value in the repository so only a single message template will match on any
 * particular key value. The ‘tag’ property is not a unique value across message templates but there can only 
 * be one tag per text resource. So a text message template can be tagged as ‘news’ but not as both ‘news’ and
 * ‘latest-news’.
 *
 * When performing a key match we firstly try to find a site specific resource text message template by matching
 * on a site version of the supplied key value or if no match is found we try to find a match on the supplied
 * key value. If no match is found in the repository for either key versions then:
 *   if the default text input parameter is not empty then this is used as the message text
 *   if the default text input parameter is empty and the default text property is not empty then
 *     the default text property is used as the message text
 *   otherwise the message text is set as an empty string
 *
 * No default text is returned when performing a tag match. In this case an empty string array is returned.
 *
 * The message templates are then parsed to substitute any format pattern specifiers with the corresponding
 * entry value obtained from either the 'args' input parameter map or from the request input parameters.
 *
 * The message template can consist of static text elements and zero or more format pattern specifiers. A format
 * pattern specifier is defined as a region of text enclosed by '{' and '}' characters. This allows the page
 * coder to create dynamic message content based on the message template and substituting values for the format
 * pattern specifiers at runtime.
 * For example,
 *   The quick {color} fox {action} over the {object}.
 *
 * with matching format pattern values as follows
 *   color=brown
 *   action=jumps
 *   object=lazy dog
 *
 * resolves as
 *  The quick brown fox jumps over the lazy dog.
 *
 * The message template can contain '\\', '\{', '\}' escape sequences. This allows '\', '{', and '}' characters
 * to appear in the message and not be treated as format specifier pattern delimiters.
 * If the message template was defined as
 *   The \{ quick {color} fox \} {action} over the {object}.
 *
 * this resolves as
 *   The { quick brown fox } jumps over the lazy dog.
 *
 * Unknown escape sequences ignore the '\' character, so
 *   The \q quick {color} fox {action} over the {object}.
 *
 * will resolve as
 *   The q quick brown fox jumps over the lazy dog.
 *
 * Text within the format pattern delimiters is taken literally and will ignore escape sequences, so
 *   The quick {c\qolor\} fox {action} over the {object}.
 *
 * will pick up 'c\qolor\' as a format pattern specifier.
 *
 * If multiple parameters matching the same format pattern specifier are passed to the droplet then it is the
 * last parameter which will be used. So passing in a 'color' parameter twice with values 'brown' & 'black' will
 * insert 'black' into the example above.
 *
 * Any parameter name/values or map entries passed into the droplet but which do not match on a format pattern
 * specifier in the message template are ignored.
 *
 * Any format pattern specifiers present in the message template but which do not have a corresponding value passed
 * into the droplet remain untouched in the resulting message.
 *
 * The droplet does not perform any strong verification on the message template form so
 *   The quick {color fox jumps over the lazy dog.
 *
 * is a valid template, there is no malformed exception for the unclosed format pattern.
 * As there is no format pattern specifier sequence recognized in the template, the message resolves as
 *   The quick {color fox jumps over the lazy dog.
 *
 *
 * The droplet takes the following input parameters:
 *   key (optional, required if ‘tag’ attribute not supplied)
 *   The key code to use when looking up the resource message text in the repository.
 *
 *   tag (optional, required if ‘key’ attribute not supplied)
 *   The tag to use when looking up the resource message text in the repository..
 *
 *   args (optional)
 *   A map object consisting of a number of entries to use when populating the format pattern specifiers embedded
 *   in the message template text. The format pattern specifiers in the message template are used to match on
 *   the map key values and the corresponding map entry value is substituted for the format pattern in the resulting
 *   message text.
 *
 *   arg name (optional)
 *   An arbitrary number of parameters to use when populating the format pattern specifiers embedded in the message
 *   template text. The actual 'arg' name is not defined; the format pattern specifiers in the message template are
 *   used as possible parameter names and the parameter value is substituted for the format pattern in the resulting
 *   message text.
 *
 *   defaultText (optional)
 *   The default text to output when a message for the given key could not be found.
 *
 * The droplet renders the following open parameters:
 *   output
 *   message - the localized resource message text
 *
 *   error
 *   message - error message if a problem occurred processing the request
 *
 * <p>
 * Example:
 * <pre>
 *  <dsp:importbean bean="/atg/store/droplet/StoreText"/>
 *
 *  <c:set var="key" value="company_aboutUs.aboutUs"/>
 *  <c:set var="storeName" value="ATG Store"/>
 *
 *  <dsp:droplet name="StoreText">
 *    <dsp:param name="key" value="${key}"/>
 *    <dsp:param name="storeName" value="${storeName}"/>

 *    <dsp:oparam name="output">
 *      <dsp:getvalueof var="message" param="message"/>
 *      <c:out value="[${key}: ${message}]" escapeXml="false"/>
 *    </dsp:oparam>
 *    <dsp:oparam name="error">
 *      <dsp:getvalueof var="message" param="message"/>
 *      <c:out value="[Error: ${key}: ${message}]" escapeXml="false"/>
 *    </dsp:oparam>
 *  </dsp:droplet>
 *
 *
 *  <c:set var="tag" value="news"/>
 *
 *  <dsp:droplet name="StoreText">
 *    <dsp:param name="tag" value="${tag}"/>
 *
 *    <dsp:oparam name="output">
 *      <dsp:getvalueof var="messages" param="messages"/>
 *      <c:out value="[${tag}:"/>
 *      <c:forEach items="${messages}"
 *                 var="message">
 *        <c:out value="  ${message}" escapeXml="false"/>
 *      </c:forEach>
 *      <c:out value="]"/>
 *    </dsp:oparam>
 *    <dsp:oparam name="error">
 *      <dsp:getvalueof var="message" param="message"/>
 *      <c:out value="[Error: ${key}: ${message}]" escapeXml="false"/>
 *    </dsp:oparam>
 *  </dsp:droplet>
 * </pre>
 * </p>
 */
public class StoreTextDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/StoreTextDroplet.java#3 $$Change: 635816 $";


  /*
   * Input parameter names
   */
  private final static String KEY_ATTRIBUTE_NAME         = "key";
  private final static String TAG_ATTRIBUTE_NAME         = "tag";
  private final static String ARGS_ATTRIBUTE_NAME        = "args";
  private final static String DEFAULTTEXT_ATTRIBUTE_NAME = "defaultText";

  /*
   * Message oparam output values
   */
  private final static String  MESSAGE  = "message";
  private final static String  MESSAGES = "messages";
  private static ParameterName OUTPUT  = ParameterName.getParameterName( "output" );

  /*
   * Error message oparam output values
   */
  private final static String  ERROR_MESSAGE = "message";
  private static ParameterName ERROR         = ParameterName.getParameterName( "error" );

  /*
   * Format characters relevant within the context of the resource text message template.
   */
  private final char ESCAPE_CHAR = '\\';
  private final char START_FORMAT_PATTERN_CHAR = '{';
  private final char END_FORMAT_PATTERN_CHAR   = '}';

  /*
   * Global constants
   */
  private static String SITE_ID = "siteId";
  private static String ERROR_MESSAGE_TXT = "";

  /*
   * Modes for matching repository resource text templates
   */
  private enum Mode { BY_KEY, BY_TAG, UNKNOWN };


  /**
   * The default text string to use if the repository does not contain a text resource message
   * with a matching key value. The default text template can contain '{key}' format pattern
   * specifiers with the '{key}' pattern being populated in the resulting text with the 'key' value.
   */
  private String mDefaultTextTemplate = "";

  public void setDefaultTextTemplate( String pDefaultTextTemplate ) {
    if ( pDefaultTextTemplate == null ) {
      pDefaultTextTemplate = "";
    }

    mDefaultTextTemplate = pDefaultTextTemplate;
  }

  public String getDefaultTextTemplate() {
    return( mDefaultTextTemplate );
  }

  public String getDefaultText( String pKey ) {
    Map args = new HashMap();

    String defaultText = "";


    args.put( KEY_ATTRIBUTE_NAME,
              pKey );

    defaultText = parseTemplate( getDefaultTextTemplate(),
                                 args );

    return( defaultText );
  }


  /**
   * The template string defining the site specific key format. This can contain '{siteId}' and '{key}'
   * format pattern specifiers with the '{siteId}' pattern being populated by the current site id
   * if any, and the '{key}' pattern being populated in the with the 'key' value.
   * Defaults to {siteId}.{key}
   */
  private String mSiteKeyTemplate = "{" + SITE_ID + "}.{" + KEY_ATTRIBUTE_NAME + "}";

  public void setSiteKeyTemplate( String pSiteKeyTemplate ) {
    if ( pSiteKeyTemplate == null ) {
      pSiteKeyTemplate = "";
    }

    mSiteKeyTemplate = pSiteKeyTemplate;
  }

  public String getSiteKeyTemplate() {
    return( mSiteKeyTemplate );
  }

  public String getSiteKey( String pKey ) {
    Map args = new HashMap();

    String siteKey = "";

    String siteId = SiteContextManager.getCurrentSiteId();


    if ( !StringUtils.isEmpty(siteId) ) {
      args.put( SITE_ID,
                siteId );

      args.put( KEY_ATTRIBUTE_NAME,
                pKey );

      siteKey = parseTemplate( getSiteKeyTemplate(),
                               args );
    }
    else {
      siteKey = "";
    }

    return( siteKey );
  }


  /**
   * The template string defining the site specific tag format. This can contain '{siteId}' and '{tag}'
   * format pattern specifiers with the '{siteId}' pattern being populated by the current site id
   * if any, and the '{tag}' pattern being populated in the with the 'tag' value.
   * Defaults to {siteId}.{tag}
   */
  private String mSiteTagTemplate = "{" + SITE_ID + "}.{" + TAG_ATTRIBUTE_NAME + "}";

  public void setSiteTagTemplate( String pSiteTagTemplate ) {
    if ( pSiteTagTemplate == null ) {
      pSiteTagTemplate = "";
    }

    mSiteTagTemplate = pSiteTagTemplate;
  }

  public String getSiteTagTemplate() {
    return( mSiteTagTemplate );
  }

  public String getSiteTag( String pTag ) {
    Map args = new HashMap();

    String siteTag = "";

    String siteId = SiteContextManager.getCurrentSiteId();


    if ( !StringUtils.isEmpty(siteId) ) {
      args.put( SITE_ID,
                siteId );

      args.put( TAG_ATTRIBUTE_NAME,
                pTag );

      siteTag = parseTemplate( getSiteTagTemplate(),
                               args );
    }
    else {
      siteTag = "";
    }

    return( siteTag );
  }


  /**
   * The repository containing the text resource message strings.
   */
  private Repository mRepository = null;

  public void setRepository( Repository pRepository ) {
    mRepository = pRepository;
  }


  public Repository getRepository() {
    return( mRepository );
  }


  /**
   * The item descriptor to use when retrieving the text resource 
   * message strings from the repository.
   */
  private String mItemDescriptorName = null;

  public void setItemDescriptorName( String pItemDescriptorName ) {
    mItemDescriptorName = pItemDescriptorName;
  }


  public String getItemDescriptorName() {
    return( mItemDescriptorName );
  }


  /*
   * Returns either a site specific resource text message template string by matching on the site version
   * of the supplied key value, or if no site specific resource text template is found then returns the
   * resource text template matching on the supplied key value.
   * An empty string is returned if no match for either is found.
   *
   * @param pKey The key value to match the resource text on.
   *
   * @return The text resource template string matched on the key value or an
   *          empty string if no match is found.
   *
   * @throws RepositoryException If there was an error accessing the repository.
   */
  private String getMessageTemplateByKey( String pKey )
    throws RepositoryException {

    String itemDescriptorName = getItemDescriptorName();

    Repository repository = getRepository();

    RepositoryView repositoryView = repository.getView( itemDescriptorName );

    String key = pKey;
    String siteKey = getSiteKey( pKey );

    String messageTemplate = "";


    if ( !StringUtils.isEmpty(siteKey) ) {
      /*
       * Look up the repository for a match on either the site specific version
       * of the supplied key or on the supplied key itself
       */
      RqlStatement rqlStatement = RqlStatement.parseRqlStatement( "key = ?0 OR key = ?1" );

      Object[] params = new Object[] { siteKey, key };

      RepositoryItem[] items = rqlStatement.executeQuery( repositoryView,
                                                          params );

      messageTemplate = "";

      if ( items != null
           && items.length > 0 ) {
        if ( items.length > 1
             && ((String)items[0].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() >= ((String)items[1].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() ) {
          messageTemplate = (String)items[0].getPropertyValue( "text" );
        }
        else if ( items.length > 1
                  && ((String)items[1].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() > ((String)items[0].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() ) {
          messageTemplate = (String)items[1].getPropertyValue( "text" );
        }
        else {
          messageTemplate = (String)items[0].getPropertyValue( "text" );
        }
      }
    }
    else {
      /*
       * Look up the repository for a match on the supplied key
       */
      RqlStatement rqlStatement = RqlStatement.parseRqlStatement( "key = ?0" );

      Object[] params = new Object[] { key };

      RepositoryItem[] items = rqlStatement.executeQuery( repositoryView,
                                                          params );

      messageTemplate = "";

      if ( items != null
           && items.length > 0 ) {
         messageTemplate = (String)items[0].getPropertyValue( "text" );

         if ( messageTemplate == null ) {
           messageTemplate = "";
         }
      }
    }

    return( messageTemplate );
  }


  /*
   * Returns all resource text message template strings matching on the site version of the supplied tag
   * value and also the supplied tag value.
   * An empty string array of length 0 is returned if no match is found.
   *
   * @param pTag The tag value to match the resource text on.
   *
   * @return Array of text resource template strings matched on the tag value or an
   *          empty array if no match is found.
   *
   * @throws RepositoryException If there was an error accessing the repository.
   */
  private String[] getMessageTemplatesByTag( String pTag )
    throws RepositoryException {

    String itemDescriptorName = getItemDescriptorName();

    Repository repository = getRepository();

    RqlStatement rqlStatement = null;

    Object[] params = null;

    String tag = pTag;
    String siteTag = getSiteTag( pTag );

    String[] messageTemplates = new String[0];


    /*
     * Look up the repository for a match on the supplied tag
     */
    RepositoryView repositoryView = repository.getView( itemDescriptorName );


    if ( !StringUtils.isEmpty(siteTag)
         && !StringUtils.isEmpty(tag) ) {
      rqlStatement = RqlStatement.parseRqlStatement( "tag = ?0 OR tag = ?1" );

      params = new Object[] { siteTag, tag };
    }

    if ( StringUtils.isEmpty(siteTag)
         && !StringUtils.isEmpty(tag) ) {
      rqlStatement = RqlStatement.parseRqlStatement( "tag = ?0" );

      params = new Object[] { tag };
    }

    if ( StringUtils.isEmpty(siteTag)
         && StringUtils.isEmpty(tag) ) {
      messageTemplates = new String[0];
    }
    else {
      RepositoryItem[] items = rqlStatement.executeQuery( repositoryView, params );

      if ( items != null ) {
        messageTemplates = new String[items.length];

        for ( int i = 0;
              i < items.length;
              ++i ) {
          messageTemplates[i] = (String)items[i].getPropertyValue( "text" );

          if ( messageTemplates[i] == null ) {
            messageTemplates[i] = "";
          }
        }
      }
      else {
        messageTemplates = new String[0];
      }
    }

    return( messageTemplates );
  }


  /**
   * Formats the template string; parses the template and substitutes any recognized format patterns
   * with matched values from the 'pArgs' map or 'pRequest' parameters.
   *
   * @param pTemplate The template to parse.
   *
   * @param pArgs Map containing key/entry values for the template format pattern name/values.
   *
   * @param pRequest HTTP request containing parameters for the template format pattern name/values.
   *
   * @return The template with format patterns populated.
   */
  private String parseTemplate( String pTemplate,
                                Map pArgs,
                                DynamoHttpServletRequest pRequest ) {
    char ch = '\0';

    StringBuffer formatPattern = new StringBuffer();

    StringBuffer result = new StringBuffer();

    Set keySet = null;

    Enumeration requestParameterNames = null;

    String key = "";


    if ( pArgs != null ) {
      keySet = pArgs.keySet();
    }

    /*
     * Iterate over the template characters populating any format pattern specifiers encountered.
     */
    for ( int i = 0;
          pTemplate != null && i < pTemplate.length();
          ++i ) {
      ch = pTemplate.charAt( i );

      if ( ch == ESCAPE_CHAR ) {
        /*
         * We've encountered the escape character. 
         * If the next character is either the start or end format pattern character, or the
         * escape character then treat this as a normal character and add to result text.
         * Otherwise ignore the escape character for unrecognized escape sequences.
         */
        if ( i + 1 < pTemplate.length() ) {
          switch ( pTemplate.charAt(i + 1) ) {
            case START_FORMAT_PATTERN_CHAR:
              result.append( START_FORMAT_PATTERN_CHAR );
              ++i;
              break;

            case END_FORMAT_PATTERN_CHAR:
              result.append( END_FORMAT_PATTERN_CHAR );
              ++i;
              break;

            case ESCAPE_CHAR:
              result.append( ESCAPE_CHAR );
              ++i;
              break;

            default:
              /*
               * Ignore the escape character for unrecognized escape sequences.
               */
              break;
          }
        }
      }
      else if ( ch == START_FORMAT_PATTERN_CHAR ) {
        /*
         * We've possibly encountered a format pattern string.
         * Firstly try to find a match in the map of arguments, if no match is found then try to
         * find a match in the request parameters.
         */
        boolean formatPatternFound = false;

        /*
         * Iterate over the map entries trying to match the format pattern with a map key.
         * If a match is found substitute the corresponding map entry value for the format pattern.
         */
        if ( keySet != null ) {
          for ( Iterator iterator = keySet.iterator();
                !formatPatternFound && iterator.hasNext(); ) {

            key = (String)iterator.next();

            formatPattern = new StringBuffer();
            formatPattern.append( START_FORMAT_PATTERN_CHAR ).append( key ).append( END_FORMAT_PATTERN_CHAR );

            if ( pTemplate.startsWith( new String(formatPattern), i) ) {
              result.append( pArgs.get(key) );

              i = pTemplate.indexOf( END_FORMAT_PATTERN_CHAR, i );

              formatPatternFound = true;
            }
          }
        }

        /*
         * If the format pattern has no match in the map of arguments try to find a match in the request parameters.
         * Iterate over the request parameters trying to match the format pattern with a parameter name.
         * If a match is found substitute the corresponding parameter value for the format pattern.
         */
        if ( !formatPatternFound
             && pRequest != null ) {
          requestParameterNames = pRequest.getParameterNames();
            
          while ( !formatPatternFound
                  && requestParameterNames != null
                  && requestParameterNames.hasMoreElements() ) {

            key = (String)requestParameterNames.nextElement();

            formatPattern = new StringBuffer();
            formatPattern.append( START_FORMAT_PATTERN_CHAR ).append( key ).append( END_FORMAT_PATTERN_CHAR );

            if ( pTemplate.startsWith(new String(formatPattern), i) ) {
              result.append( pRequest.getParameter(key) );

              i = pTemplate.indexOf( END_FORMAT_PATTERN_CHAR, i );

              formatPatternFound = true;
            }
          }
        }

        /*
         * There was no format pattern match, simply add the format pattern start character
         * to the result text and continue searching for other matches.
         */
        if ( !formatPatternFound ) {
          result.append( ch );
        }

      }
      else {
        /*
         * We've encountered a normal character, so add this to the result text
         * and continue searching for format pattern matches.
         */
        result.append( ch );
      }
    }

    return( new String(result) );
  }


  /**
   * @see parseTemplate(String, Map, DynamoHttpServletRequest)
   */
  private String parseTemplate( String pTemplate,
                                Map pArgs ) {
    return( parseTemplate(pTemplate,
                          pArgs,
                          null) );
  }


  /**
   * @see parseTemplate(String, Map, DynamoHttpServletRequest)
   */
  private String parseTemplate( String pTemplate,
                                DynamoHttpServletRequest pRequest ) {
    return( parseTemplate(pTemplate,
                          null,
                          pRequest) );
  }

  
  /**
   * Receives and validates the request input parameters.
   * Performs a look up in the repository based on the supplied 'key' parameter value to obtain localized
   * resource text message template. If a match is found the message template is parsed to substitute any
   * format pattern specifiers with the corresponding entry value obtained from either the 'args' input
   * parameter map or the request input parameters.
   * If no match is found for the 'key' in the repository then:
   *   if the default text input parameter is not empty then this is used as the message text
   *   if the default text input parameter is empty and the default text property is not empty then
   *     the default text property is used as the message text
   *   otherwise the message text is set as an empty string
   *
   * @param pRequest - HTTP request
   * @param pResponse - HTTP response
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service( DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse )
    throws ServletException,
           IOException {

    try {
      String keyParamValue = pRequest.getParameter( KEY_ATTRIBUTE_NAME );
      String tagParamValue = pRequest.getParameter( TAG_ATTRIBUTE_NAME );
      Map argsParamValue = (Map)pRequest.getObjectParameter( ARGS_ATTRIBUTE_NAME );
      String defaultTextParamValue = pRequest.getParameter( DEFAULTTEXT_ATTRIBUTE_NAME );

      String messageTemplate = "";
      String[] messageTemplates = null;

      String message = "";
      String[] messages = null;

      Mode mode = Mode.UNKNOWN;
      

      /*
       * Obtain the resource text message template.
       */
      if ( !StringUtils.isEmpty(keyParamValue) ) {
        if ( isLoggingDebug() ) {
          logDebug( "Searching for resource text template matching key value: " + keyParamValue );
        }

        mode = Mode.BY_KEY;
      }
      else if ( !StringUtils.isEmpty(tagParamValue) ) {
        if ( isLoggingDebug() ) {
          logDebug( "Searching for resource text templates matching tag value: " + tagParamValue );
        }

        mode = Mode.BY_TAG;
      }
      else {
        if ( isLoggingError() ) {
          logDebug( "No supplied key or tag value" );
        }

        mode = Mode.UNKNOWN;
        
        messageTemplate = "";
        messageTemplates = new String[0];
      }


      /*
       * Obtain the resource text message matching the key.
       */
      if ( mode == Mode.BY_KEY ) {
        messageTemplate = getMessageTemplateByKey( keyParamValue );

        if ( !StringUtils.isEmpty(messageTemplate) ) {
          /*
           * Construct the message by populating any format patterns specified in the message template.
           */
          if ( isLoggingDebug() ) {
            logDebug( "Found matching resource text template: " + messageTemplate );
          }

          message = parseTemplate( messageTemplate,
                                   argsParamValue,
                                   pRequest );

          if ( isLoggingDebug() ) {
            logDebug( "Formatted resource text message: " + message );
          }
        }
        else if ( !StringUtils.isEmpty(defaultTextParamValue) ) {
          if ( isLoggingDebug() ) {
            logDebug( "No matching resource text template found, using supplied default text: " + defaultTextParamValue );
          }

          message = defaultTextParamValue;
        }
        else if ( !StringUtils.isEmpty(getDefaultTextTemplate()) ) {
          if ( isLoggingDebug() ) {
            logDebug( "No matching resource text template found, using default text property: " + defaultTextParamValue );
          }

          message = getDefaultText( keyParamValue );

          if ( isLoggingDebug() ) {
            logDebug( "Formatted default text property: " + message );
          }
        }
        else {
          if ( isLoggingDebug() ) {
            logDebug( "No matching resource text template or default text found, using empty string" );
          }

          message = "";
        }
      }
      
      /*
       * Obtain all resource text messages matching the tag.
       */
      if ( mode == Mode.BY_TAG ) {
       messageTemplates = getMessageTemplatesByTag( tagParamValue );

       messages = new String[messageTemplates.length];

       for ( int i = 0;
             messageTemplates != null && i < messageTemplates.length;
             ++i ) {
          /*
           * Construct the message by populating any format patterns specified in the message template.
           */
          if ( isLoggingDebug() ) {
            logDebug( "Found matching resource text template: " + messageTemplates[i] );
          }

          if ( !StringUtils.isEmpty(messageTemplates[i]) ) {
            message = parseTemplate( messageTemplates[i],
                                     argsParamValue,
                                     pRequest );
            messages[i] = message;
          }
          else {
            messages[i] = "";
          }

          if ( isLoggingDebug() ) {
            logDebug( "Formatted resource text message: " + message );
          }
        }
      }

      /*
       * Render the message oparam output
       */
      if ( mode == Mode.BY_KEY ) {
        pRequest.setParameter( MESSAGE,
                               message );
      }

      if ( mode == Mode.BY_TAG ) {
        pRequest.setParameter( MESSAGES,
                               messages );
      }

      pRequest.serviceLocalParameter( OUTPUT,
                                      pRequest,
                                      pResponse );
    }
    catch( Exception exception ) {
      if ( isLoggingError() ) {
        logError( exception );
      }

      /*
       * Render the error message oparam output
       */
      pRequest.setParameter( ERROR_MESSAGE,
                             ERROR_MESSAGE_TXT );

      pRequest.serviceLocalParameter( ERROR,
                                      pRequest,
                                      pResponse );
    }
  }
}



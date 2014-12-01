/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.droplet.xml;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.xml.transform.ErrorListener;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import atg.core.net.URLUtils;
import atg.nucleus.logging.ApplicationLogging;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.PageContextServletOutputStream;
import atg.servlet.ServletUtil;
import atg.xml.service.DocumentCache;
import atg.xml.service.DocumentCacheAdapter;
import atg.xml.service.DocumentCacheKey;
import atg.xml.service.XMLTemplateMap;
import atg.xml.tools.DefaultErrorHandler;
import atg.xml.tools.XMLToDOMParser;
import atg.xml.tools.XMLToolsFactory;
import atg.xml.tools.XSLProcessor;

/**
 * <p>
 * Transform XML documents using a template. <br>
 * The template can be a JHTML page or an XSLT stylesheet.
 *
 * <p>
 * The <i>input</i> parameter specifies the XML document to
 * be transformed. <br>The template for the transformation
 * is located according to the following order of precedence:
 *
 * <ul>
 *   <li> A DOM document bound to the <i>template</i> parameter. </li>
 *   <li> The document referenced by the URI string
 *        bound to the <i>template</i> parameter. </li>
 *   <li> Then when the <i>template</i> parameter is unset:
 *        <ul>
 *          <li>A match derived from the <code>XMLTemplateMap</code> component:
 *            <ul>
 *              <li>A specific match on the input document URI.</li>
 *              <li>A specific match on the type suffix
 *                  of the input document URI.</li>
 *              <li>A default template defined in the
 *                  <code>XMLTemplateMap</code> component.</li>
              </ul>
 *          </li>
 *          <li> The template defined by the XML document itself,
 *               using an embedded <br>
 *               <code>&lt;?xml-stylesheet?&gt;</code> processing instruction.
 *          </li>
 *        </ul>
 *   </li>
 * </ul>
 *
 * <p>
 * In the case of a JHTML template, the <code>XMLTransform</code> droplet
 * will pass a DOM document as a local parameter to the execution of the
 * JHTML page.
 * The name of this parameter can be controlled by the optional
 * <i>documentParameterName</i> parameter.
 * The default is <code>inputDocument</code>.
 *
 * <p>
 * The DOM object that is the result of the transformation will
 * be bound to the <i>documentOutputName</i> output parameter,
 * for further processing or rendering in the <i>output</i> oparam.
 *
 * <p>
 * If the <i>output</i> parameter is not specified,
 * the transformed document will be serialized directly into the page.
 * If the transform is XSL, the result will conform
 * to the <code>&lt;xsl:output&gt;</code> top-level element.
 *
 * <p>
 * These are the parameters for the <code>XMLTransform</code> droplet:
 *
 * <dl>
 *
 * <dt><i>Input Paramaters</i></dt>
 * <dd>&#160;</dd>
 *
 * <dt><b>input</b> (required)</dt>
 * <dd>The XML document to be transformed.
 *     This can be a DOM <code>Document</code> object,
 *     a SAX <code>InputSource</code>,
 *     a text <code>InputStream</code>,
 *     or a URL for an XML document.
 *     This parameter is required.
 *     If it is not supplied, the <i>unset</i> open parameter will be rendered.
 *     <br><br>
 * </dd>
 *
 * <dt><b>template</b> (optional)</dt>
 * <dd>The template to be used to transform the XML document.
 *     This can be either a URL for a template, or a DOM document object.
 *     This parameter is optional. If it is not supplied,
 *     a match will be sought in the <code>XMLTemplateMap</code>,
 *     and finally from an <code>&lt;?xml-stylesheet?&gt;</code>
 *     directive embedded in the XML document itself.
 *     If no template is found, it is an error.
 *     If logging is enabled, the reason will be logged,
 *     then the <i>failure</i> open parameter will be rendered.
 *     <br><br>
 * </dd>

 * <dt><b>templateContext</b> (optional)</dt>
 * <dd>If the template is a JSP template, AND is in a separate
 *     web-application, specify templateContext to the context
 *     root of the target web-application. Note this option
 *     does not apply to JHTML pages.
 * </dd>
 *
 * <dt><b>validate</b> (optional)</dt>
 * <dd>Determine how the XML document specified by the <i>input</i> parameter
 *     should be validated against a DTD specified in its <code>DOCTYPE</code>
 *     declaration.
 *     Validation will be slower, but may help prevent problems later when
 *     accessing parts of the XML document.
 *     The legal values are: <br><br>
 *     <ul>
 *       <li><code>true</code><br>
 *       Read the DTD, validate the document,
 *       and apply all information from the DTD.
 *       </li><br><br>
 *       <li><code>false</code> (default)<br>
 *       Read the DTD, do not validate the document, but substitute
 *       default attributes, and apply other information from the DTD at the
 *       parser's discretion (Apache Xerces does support this mode).
 *       </li> <br><br>
 *       <li><code>nodtd</code><br>
 *       Do not read the DTD, do not validate the document,
 *       do not apply any defaults or other information.
 *       Use this mode when you do not want the parser to try and resolve
 *       the DTD location, perhaps because the remote URL location is
 *       temporarily unreachable.
 *       Note that the resulting document may be incomplete, have incorrect
 *       attibute values, and will not have subsitution of external entities.
 *       (Apache Xerces does support this mode,
 *        but it may not be available for every parser).
 *       </li>
 *     </ul>
 *     <br>
 *     This parameter is optional, the default value is <code>false</code>.
 *     If there is no <code>DOCTYPE</code> declaration,
 *     this parameter has no effect.
 *     <br><br>
 * </dd>
 *
 * <dt><b>passParams</b> (optional)</dt>
 * <dd>If the template is an XSL stylesheet:
 *     a flag to control the passing of parameters
 *     from the request and droplet environment, to top-level
 *     <code>&lt;xsl:param&gt;</code> elements in the XSL stylesheet.
 *     Legal values are: <br><br>
 *     <ul>
 *       <li><code>none</code>: no params are passed (default).</li>
 *       <li><code>query</code>: URL query params from a GET request.</li>
 *       <li><code>post</code>: form params from a POST request.</li>
 *       <li><code>local</code>: only params in this droplet scope.</li>
 *       <li><code>all</code>: params from all droplet and request scopes.</li>
 *     </ul>
 *     <br>
 *     If more than one parameter exists for a given name,
 *     only the first is passed. The order of parameter scopes
 *     is from local (within the <code>XMLTransform</code> droplet),
 *     up through other enclosing scopes, and eventually to the
 *     request query or post parameters.
 *     See <code>DynamoHttpServletRequest</code> for more details
 *     on the query, post, and local parameter scope definitions.
 *     <br><br>
 * </dd>
 *
 * <dt><b>documentParameterName</b> (optional)</dt>
 * <dd>If the template is JHTML: the name of the parameter to
 *     bind the input DOM document object.
 *     The default name is <code>inputDocument</code>.
 *     <br><br>
 * </dd>
 *
 * <dt><i>Outut Paramaters</i></dt>
 * <dd>&#160;</dd>
 *
 * <dt><b>documentOutputName</b> (optional)</dt>
 * <dd>This parameter will be bound to the DOM document which
 *     results from the transformation. This object can be further
 *     processed or rendered in the <i>output</i> oparam.
 *     The default name is <code>document</code>.
 *     <br><br>
 *
 * <dt><b>errors</b> (optional)</dt>
 * <dd>This parameter will be bound to an Enumeration of
 *     Exceptions if there were failures when transforming the XML document.
 *     <br><br>
 * </dd>
 *
 * <dt><i>Open Paramaters</i></dt>
 * <dd>&#160;</dd>
 *
 * <dt><b>unset</b> (optional) </dt>
 * <dd>The oparam to render if the <i>input</i> parameter is not set.
 *     <br><br>
 * </dd>
 *
 * <dt><b>output</b> (optional) </dt>
 * <dd>This oparam is used when the XML document was successfully
 *     transformed. The result of the transformation is available
 *     as a DOM document in the parameter specified by the
 *     <code>documentOutputName</code>.
 *     If the <i>output</i> oparam is not specified,
 *     then the document is automatically serialized to the output page.
 *     <br><br>
 * </dd>
 *
 * <dt><b>failure</b> (optional)</dt>
 * <dd>This oparam will be used  to format output when there was a failure to
 *     transform the XML document. If error logging is enabled,
 *     then the reason for the failure will be logged.
 *     Exceptions are also bound to the <i>errors</i> output parameter.
 * </dd>
 *
 * </dl>
 *
 * <p>
 * For more details about XSLT, see
 * <a href="http://www.w3.org/TR/xslt">XSL Transformations</a>
 * (W3C Recommendation 1999-11-19).
 * In particular, see this section on
 * <a href="http://www.w3.org/TR/xslt#top-level-variables">Top-level
 * Variables and Parameters</a>, which mentions passing parameters
 * to stylesheets using <code>&lt;xsl:param&gt;</code> elements.
 * For more details about embedded references to XSL stylesheets, see
 * <a href="http://www.w3.org/TR/xml-stylesheet">Associating Stylesheets
 * with XML Documents</a> (W3C Recommendation 1999-06-26).
 *
 * <p>
 * When embedded XSL directives are used, the stylesheets from
 * multiple processing instructions are concatenated together,
 * as if they were imported into a common stylesheet.
 * There is no iteration over multiple stylesheets in the source
 * document, and no recursion over directives in the output of the
 * first transform.
 * The <code>type</code> pseudo-attribute must be present,
 * with one of these values: <code>text/xml</code> (official XSLT 1.0 MIME type),
 * <code>text/xsl</code> (incorrect, but frequently used by analogy with
 * <code>text/css</code>),
 * or <code>application/xslt&#x2b;xml</code> (newly suggested for XSLT by
 * RFC 3023 <i>XML Media Types</i>, but due to a typo in Xalan, you may need
 * to use <code>application/xml&#x2b;xslt</code> instead).
 *
 * <p>
 * If the <i>output</i> open parameter is not specified for an XSL transform,
 * the serialized output will generally conform to the attributes of the
 * <code>&lt;xsl:output&gt;</code> top-level element.
 *
 * <p>
 * The MIME type is written to the HTTP response <code>Content-type</code> header.
 * The MIME type is determined by an explicit <i>media-type</i> attribute
 * on the <code>&lt;xsl:output&gt;</code> element.
 * If this is not present, then the MIME type is taken from the
 * Dynamo servlet pipeline, typically derived with the <code>MIMETyper</code>
 * from the filetype suffix of the page.
 * The default <code>media-type</code> usually depends on the output
 * <i>method</i> attribute: <code>text/xml</code> for method <code>xml</code>,
 * and <code>text/html</code> for the <code>html</code>,
 * but in Dynamo this will always get overwritten with the
 * value from the pipeline.
 *
 * <p>
 * The default <i>method</i> for Dynamo is <code>html</code>,
 * not the standard XSL default value of <code>xml</code>.
 * If XML output is required, it must be explicitly requested in the
 * <code>&lt;xsl:output&gt;</code> element.
 * Since the MIME type can be derived from the Dynamo servlet pipeline,
 * it is possible to get an inconsistent <code>method</code> in XSL
 * (e.g. a WML type from the servlet pipeline,
 *  but a default <code>html</code> output method).
 * Care should be taken to ensure that the MIME type and the
 * output method are consistent.
 *
 * <p>
 * An encoding is used to serialize the result to the response stream,
 * and the encoding name is set in the <code>Content-type</code> HTTP header
 * using the <code>charset</code> flag.
 * The encoding is determined by an explicit <i>encoding</i> attribute
 * on the <code>&lt;xsl:output&gt;</code> element.
 * If this is not present, then the encoding is taken from the
 * Dynamo servlet pipeline, typically derived with the <code>EncodingTyper</code>
 * from the path of the compiled page.
 * Note that the encoding does not default to the standard XSL value of <code>UTF-8</code>,
 * and it is completely independent of the actual encoding of the XSL stylesheet itself,
 * as specified in the <code>&lt;?xml?&gt;</code> declaration.
 * If an output encoding of <code>UTF-8</code> is required,
 * it must be explicitly requested in the <code>&lt;xsl:output&gt;</code> element.
 *
 * @see atg.xml.service.XMLTemplateMap
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/XMLTransform.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $

 */
public class XMLTransform extends DynamoServlet
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/XMLTransform.java#2 $$Change: 651448 $";

  XMLToolsFactory mXMLToolsFactory;
  XMLTemplateMap  mXMLTemplateMap;
  DocumentCache   mDocumentCache;
  DocumentCache   mStylesheetCache;

  //-------------------------------------

  /** The name of the input paramameter */
  protected static final String INPUT_PARAM = "input";

  /** The name of the default document parameter */
  protected static final String OUT_DOC_NAME_PARAM = "documentOutputName";

  /** The name of the parameter used to configure the document */
  protected static final String IN_DOC_NAME_PARAM  = "documentParameterName";

  /** The name of the output paramameter */
  protected static final String OUTPUT_PARAM = "output";

  /** The name of the unset paramameter */
  protected static final String UNSET_PARAM = "unset";

  /** The name of the validate paramameter */
  protected static final String VALIDATE_PARAM = "validate";

  /** The name of the args paramameter */
  protected static final String PASS_PARAM  = "passParams";

  /** The name of the template paramameter */
  protected static final String TEMPLATE_PARAM = "template";

  /** The context path of the target template page (only applies to JSP).  */
  protected static final String TEMPLATE_CONTEXT_PARAM = "templateContext";
  
  /** The name of the errors paramameter */
  protected static final String ERRORS_PARAM = "errors";

  /** The name of the failure paramameter */
  protected static final String FAILURE_PARAM = "failure";

  //-------------------------------------

  /** The default input document parameter name for JHTML */
  protected static final String DEFAULT_IN_DOC_NAME  = "inputDocument";

  /** The default output document parameter name */
  protected static final String DEFAULT_OUT_DOC_NAME = "document";

  //-------------------------------------
  // Enumerated values for the passparams paramameter

  protected static final String PASS_NONE       = "none";
  protected static final String PASS_LOCAL      = "local";
  protected static final String PASS_QUERY      = "query";
  protected static final String PASS_POST       = "post";
  protected static final String PASS_ALL        = "all";

  protected static final int    ENUM_PASS_NONE  = 1;
  protected static final int    ENUM_PASS_LOCAL = 2;
  protected static final int    ENUM_PASS_QUERY = 3;
  protected static final int    ENUM_PASS_POST  = 4;
  protected static final int    ENUM_PASS_ALL   = 5;

  protected static final int    DEFAULT_PASS    = ENUM_PASS_NONE;

  //-------------------------------------

  /**
   * The DocumentCache used by this component
   */
  public void setDocumentCache( DocumentCache pDocumentCache )
  {
    mDocumentCache = pDocumentCache;
  }

  /**
   * The DocumentCache used by this component.
   */
  public DocumentCache getDocumentCache()
  {
    return mDocumentCache;
  }

  //-------------------------------------

  /**
   * The StylesheetCache used by this component
   */
  public void setStylesheetCache( DocumentCache pStylesheetCache )
  {
    mStylesheetCache = pStylesheetCache;
  }

  /**
   * The StylesheetCache used by this component.
   */
  public DocumentCache getStylesheetCache()
  {
    return mStylesheetCache;
  }

  //-------------------------------------
  /**
   * The XMLToolsFactory used by this component.
   */
  public void setXmlToolsFactory( XMLToolsFactory pXMLToolsFactory )
  {
    mXMLToolsFactory = pXMLToolsFactory;
  }

  /**
   * The XMLToolsFactory used by this component.
   */
  public XMLToolsFactory getXmlToolsFactory()
  {
    return mXMLToolsFactory;
  }

  //-------------------------------------
  /**
   * The XMLTemplateMap used by this component
   */
  public void setXmlTemplateMap( XMLTemplateMap pXMLTemplateMap )
  {
    mXMLTemplateMap = pXMLTemplateMap;
  }

  /**
   * The XMLTemplateMap used by this component.
   */
  public XMLTemplateMap getXmlTemplateMap()
  {
    return mXMLTemplateMap;
  }

  //-------------------------------------

  /**
   * The service method for this droplet.
   */
  public void service( DynamoHttpServletRequest  pReq,
                       DynamoHttpServletResponse pRes )
    throws ServletException, IOException
  {
    Object      value;
    Object      input = null;
    String      inputString = null;
    String      inputDocParamName  = DEFAULT_IN_DOC_NAME;
    String      outputDocParamName = DEFAULT_OUT_DOC_NAME;
    Map         params = null;
    Document    dom = null;
    // unused InputStream templateInput = null;
    String      templateString = null;
    Document    templateDom = null;
    boolean     validate = false;
    boolean     loadDTD = true;
    boolean     failure = false;
    boolean     output = false;
    boolean     jhtml = false;
    Document    result = null;
    Vector      errors = null;

    DefaultErrorHandler errorHandler = new DefaultErrorHandler( this, true, false );
    errorHandler.setDefaultSystemId( this.getName() );

    try
    {
      if ((input = pReq.getObjectParameter(INPUT_PARAM)) == null)  {
        pReq.serviceLocalParameter( UNSET_PARAM, pReq, pRes );
        return;
      }

      if ((value = pReq.getParameter(VALIDATE_PARAM)) != null) {
        String validateString = value.toString();
        if (validateString.equalsIgnoreCase("true")) {
          validate = true;
          loadDTD = true;
        }
        else if (validateString.equalsIgnoreCase("false")) {
          validate = false;
          loadDTD = true;
        }
        else if (validateString.equalsIgnoreCase("nodtd")) {
          validate = false;
          loadDTD = false;
        }
        else {
          throw new IllegalArgumentException(
                        "Illegal argument for validate input param. " +
                        "Value is '" + validateString +
                        "', expecting 'true', 'false' or 'nodtd'." );
        }
      }

      if (isLoggingDebug()) {
        logDebug( "Validation: validate=" + validate + ", load DTD=" + loadDTD );
      }

      if ((value = pReq.getObjectParameter(TEMPLATE_PARAM)) != null) {
        if (value instanceof Document) {
	    templateDom  = (Document) value;
          if (isLoggingDebug()) logDebug( "Input template DOM Document" );
        }
        else {
          templateString = value.toString();
        }
      }

      if ((value = pReq.getParameter(IN_DOC_NAME_PARAM)) != null) {
        inputDocParamName = value.toString();
      }

      if ((value = pReq.getParameter(OUT_DOC_NAME_PARAM)) != null) {
        outputDocParamName = value.toString();
      }

      if (pReq.getLocalParameter(OUTPUT_PARAM) != null) {
        output = true;
      }

      if ((value = pReq.getParameter(PASS_PARAM)) != null)
      {
        String passParams = value.toString();
        int    pass       = DEFAULT_PASS;

        if (isLoggingDebug()) logDebug( "Pass params: " + passParams );

        if      (passParams.equalsIgnoreCase(PASS_NONE))  pass = ENUM_PASS_NONE;
        else if (passParams.equalsIgnoreCase(PASS_LOCAL)) pass = ENUM_PASS_LOCAL;
        else if (passParams.equalsIgnoreCase(PASS_QUERY)) pass = ENUM_PASS_QUERY;
        else if (passParams.equalsIgnoreCase(PASS_POST))  pass = ENUM_PASS_POST;
        else if (passParams.equalsIgnoreCase(PASS_ALL))   pass = ENUM_PASS_ALL;

        if (pass != ENUM_PASS_NONE) params = getParams( pReq, pass, this );
      }

      long startTime = 0;

      if (isLoggingDebug())
      {
        startTime = System.currentTimeMillis();
      }

      // allow extra types here - see bug 29575

      if (input instanceof Document) {
        if (isLoggingDebug()) logDebug( "Input XML DOM Document" );
        dom = (Document) input;
      }
      else if (input instanceof InputStream) {
        if (isLoggingDebug()) logDebug( "Input XML InputStream" );
        XMLToDOMParser parser = mXMLToolsFactory.createXMLToDOMParser();
        DocumentCacheAdapter.setLoadDTD( parser, loadDTD, this );
        dom = parser.parse( (InputStream)input, validate, errorHandler );
      }
      else if (input instanceof InputSource) {
        if (isLoggingDebug()) logDebug( "Input XML InputSource" );
        XMLToDOMParser parser = mXMLToolsFactory.createXMLToDOMParser();
        DocumentCacheAdapter.setLoadDTD( parser, loadDTD, this );
        dom = parser.parse( (InputSource)input, validate, errorHandler );
      }
      else {
        inputString = input.toString();
        dom = getDocumentFromCache( pReq, inputString, validate, loadDTD, errorHandler );
	}

      if (errorHandler.getHasErrors()) {
        throw new RuntimeException( "Parser error" );
      }

	// Case where a DOM for the XSL template was specified.
	if (templateDom != null)
      {
	  result = processXSLTemplate( pReq, pRes, dom, templateDom,
                                     output, params, errorHandler );
	}
	else
      {
	  if (templateString  == null &&
            inputString     != null &&
            mXMLTemplateMap != null  )
        {
	    if (URLUtils.isRelative(inputString))
          {
	      String dir = URLUtils.URIDirectory(pReq.getRequestURI());
	      inputString = URLUtils.resolvePureRelativePath(inputString, dir);
	    }

	    // Case where the template was not defined by the droplet
	    // - check the template map.
	    templateString = mXMLTemplateMap.findTemplate( dom, inputString );
	  }

	  if (templateString != null)
        {
          // Is this a JHTML/JWML/JSP template or an XSL template?
          // ??Implement as property list
          if (templateString.endsWith("jhtml") ||
              templateString.endsWith("jwml")  ||
              templateString.endsWith("jsp")    )
            {
	      processJHTMLTemplate( pReq, pRes, dom, templateString, inputDocParamName );
              jhtml = true;
	    }
	    else
          {
            Object stylesheet = getStylesheetFromCache( pReq, templateString );
            if (stylesheet == null)
            {
              Document styleDoc = getDocumentFromCache( pReq, templateString, validate,
                                                        loadDTD, errorHandler );
	        result = processXSLTemplate( pReq, pRes, dom, styleDoc,
                                           output, params, errorHandler );
            }
            else
            {
	        result = processXSLTemplate( pReq, pRes, dom, stylesheet,
                                           output, params, errorHandler );
            }
	    }
	  }
        else // templateString == null)
        {
          // try finding an <?xml-stylesheet?> directive in the file.
          result = processXSLTemplate( pReq, pRes, dom, output, params, errorHandler );
        }
      } // templateDom

      if (isLoggingDebug())
      {
        long totalTime = System.currentTimeMillis() - startTime;
        logDebug( "Elapsed time: " + totalTime + " ms" );
      }
    }
    catch (Exception e) {
      if (isLoggingError()) {
        logError(e);
      }
      errors = new Vector( 1 );
      errors.addElement( e );
      failure = true;
    }

    if (errorHandler.getHasErrors()) {
      pReq.setParameter( ERRORS_PARAM, errorHandler.getErrors() );
      failure = true;
    }
    else if (errors != null) {
      pReq.setParameter( ERRORS_PARAM, errors.elements() );
      failure = true;
    }

    if (failure) {
      pReq.serviceLocalParameter( FAILURE_PARAM, pReq, pRes );
    }
    else if (output) {
      // bind the resulting DOM into the request context
      if (isLoggingDebug()) {
        logDebug( "Binding DOM to param:" + outputDocParamName );
      }
      if (isLoggingWarning() && !jhtml && result == null) {
        logWarning( "Result of XSL transform is null." );
      }
      pReq.setParameter( outputDocParamName, result );
      pReq.serviceLocalParameter( OUTPUT_PARAM, pReq, pRes );
    }
  }

  /**
   * Process an XSL template from a DOM document.
   */
  protected Document processXSLTemplate( DynamoHttpServletRequest  pReq,
                                         DynamoHttpServletResponse pRes,
                                         Document                  pInput,
                                         Document                  pStylesheet,
                                         boolean                   output,
                                         Map                       pParams,
                                         ErrorListener             pErr )
    throws Exception
  {
    if (pStylesheet == null) return null;

    Document     result    = null;
    XSLProcessor processor = mXMLToolsFactory.createXSLProcessor();

    if (output) // DOM-DOM
    {
      if (isLoggingDebug()) {
        logDebug( "Process XML DOM with XSL DOM and return a DOM." );
      }
	result = processor.process( pInput, pStylesheet, pReq.getPathTranslated(),
                                  pParams, pErr );
    }
    else // DOM-stream
    {
      if (isLoggingDebug()) {
        logDebug( "Process XML DOM with XSL DOM and serialize to response." );
      }

      Object pXform = processor.compileStylesheet( pStylesheet, pReq.getPathTranslated() );

      doStreamTransform( processor, pInput, pXform, pParams, pRes, pErr );
    }
    return result;
  }

  /**
   * Process an XSL template with a pre-compiled template.
   */
  protected Document processXSLTemplate( DynamoHttpServletRequest  pReq,
                                         DynamoHttpServletResponse pRes,
                                         Document                  pInput,
                                         Object                    pXform,
                                         boolean                   output,
                                         Map                       pParams,
                                         ErrorListener             pErr )
    throws Exception
  {
    if (pXform == null) return null;

    Document     result    = null;
    XSLProcessor processor = mXMLToolsFactory.createXSLProcessor();

    if (output) // DOM-DOM
    {
      if (isLoggingDebug()) {
        logDebug( "Process XML DOM with precompiled XSL and return a DOM." );
      }
      result = processor.process( pInput, pXform, pParams, pErr );
    }
    else        // DOM-stream
    {
      if (isLoggingDebug()) {
        logDebug( "Process XML DOM with precompiled XSL and serialize to response." );
      }

      doStreamTransform( processor, pInput, pXform, pParams, pRes, pErr );
    }
    return result;
  }

  /**
   * Process with any embedded XSL template.
   */
  protected Document processXSLTemplate( DynamoHttpServletRequest  pReq,
                                         DynamoHttpServletResponse pRes,
                                         Document                  pInput,
                                         boolean                   output,
                                         Map                       pParams,
                                         ErrorListener             pErr )
    throws Exception
  {
    Document     result    = null;
    XSLProcessor processor = mXMLToolsFactory.createXSLProcessor();
    Object       pXform;

    try
    {
      pXform = processor.compileAssociatedStylesheet( pInput,
                                                      ServletUtil.getCurrentPathTranslated( pReq ) );
    }
    catch( Exception e )
    {
      if (isLoggingError()) {
        logError( "Error finding or reading a stylesheet reference in the XML source document. " +
                  "Could also be caused by missing or misnamed 'template' droplet input parameter, " +
                  "or misconfigured xmlTemplateMap component." );
      }
      throw e;
    }

    if (output) // DOM-DOM
    {
      if (isLoggingDebug()) {
        logDebug( "Process XML DOM with embedded XSL directive and return a DOM." );
      }
	result = processor.process( pInput, pXform, pParams, pErr );
    }
    else       // DOM-stream
    {
      if (isLoggingDebug()) {
        logDebug( "Process XML DOM with embedded XSL directive and serialize to response." );
      }

      doStreamTransform( processor, pInput, pXform, pParams, pRes, pErr );
    }
    return result;
  }

  /**
   * Process a JHTML template.
   */
  protected void processJHTMLTemplate( DynamoHttpServletRequest  pReq,
                                       DynamoHttpServletResponse pRes,
                                       Document                  pInput,
                                       String                    pTemplateURI,
                                       String                    pInputDocParamName )
    throws Exception
  {
    // Bind the request document parameter name to the input document.
    if (isLoggingDebug()) {
      logDebug( "Pass XML DOM as parameter " + pInputDocParamName +
                "to JHTML/JWML/JSP template " + pTemplateURI +
                " and include result in current page." );
    }
    pReq.setParameter( pInputDocParamName, pInput );
    if (!pTemplateURI.endsWith("jsp")) {    
      ServletUtil.embed( pTemplateURI, pReq, pRes );
    }
    else {
      String contextPath =
        (String)pReq.getLocalParameter(TEMPLATE_CONTEXT_PARAM);
      if (contextPath == null) {
        String currentContextRoot = ServletUtil.getCurrentContextPath(pReq);
        if ((currentContextRoot != null) &&
            pTemplateURI.startsWith(currentContextRoot + "/")) {
          pTemplateURI = pTemplateURI.substring(currentContextRoot.length());
        }
      }
      javax.servlet.RequestDispatcher rd = ServletUtil.getRequestDispatcher(
        pReq, contextPath, pTemplateURI);
      if (rd != null) {
        ServletUtil.flushBeforeInclude(pRes);
        if (ServletUtil.isDynamoJ2EEServer()) {
          rd.include(pReq, pRes);
        }
        else {
          rd.include(pReq.getRequest(), pRes.getResponse());
        }
      }
      else {
        if (contextPath == null)
          throw new ServletException("Could not find template '" +
                                     pTemplateURI + "'");
        else
          throw new ServletException("Could not find template '" +
                                     pTemplateURI + "' in context root '" +
                                     contextPath + "'");
      }
    }
  }

  //-------------------------------------------------------------
  // utils

  Document getDocumentFromCache( DynamoHttpServletRequest pReq,
                                 String                   pUrl,
                                 boolean                  validate,
                                 boolean                  loadDTD,
                                 DefaultErrorHandler      pErrorHandler )
    throws Exception
  {
    // get a cache entry for the file

    pErrorHandler.setDefaultPublicId( pUrl );
    String translated = translate( pUrl, pReq );

    if (isLoggingDebug()) {
      logDebug( "Input XML source parameter: " + pUrl );
      if (translated != null) {
        logDebug( "Input XML  path translated: " + translated );
      }
    }

    DocumentCacheKey key = new DocumentCacheKey( translated == null ? pUrl : null,
                                                 translated, validate, loadDTD );
    key.setErrorHandler( pErrorHandler );

    return (Document) mDocumentCache.get( key );
  }

  Object getStylesheetFromCache( DynamoHttpServletRequest pReq, String pUrl )
    throws Exception
  {
    if (mStylesheetCache == null)
      return null;

    String translated = translate(pUrl, pReq);

    if (isLoggingDebug()) {
      logDebug( "Input XSL source parameter: " + pUrl );
      if (translated != null) {
        logDebug( "Input XSL  path translated: " + translated );
      }
    }

    DocumentCacheKey key = new DocumentCacheKey(translated == null ? pUrl : null,
                                                translated, false, false );
    return mStylesheetCache.get(key);
  }

  void doStreamTransform( XSLProcessor              pProcessor,
                          Document                  pInput,
                          Object                    pXform,
                          Map                       pParams,
                          DynamoHttpServletResponse pRes,
                          ErrorListener             pErr )
    throws Exception
  {
    // Always use the explicit mime type and encoding from the xsl stylesheet,
    // but override defaults using value from servlet pipeline,
    // then reset defaults afterwards.

    synchronized( pXform )
    {
      // ignore any charset on the content type
      String contentType = pRes.getContentType();
      int pos = -1;
      if ( contentType != null )
        pos = contentType.indexOf(';');

      if (pos != -1) {
        contentType = contentType.substring(0,pos);
      }

      boolean override = pProcessor.setDefaultContentType(
                                          pXform,
                                          contentType,
                                          pRes.getCharacterEncoding() );

      if (isLoggingDebug()) {
        logDebug( "Default content type: '" + contentType +
                               "; charset=" + pRes.getCharacterEncoding() + "'" );
        logDebug( "Output  content type: '" + pProcessor.getContentType( pXform ) + "'" );
      }

      pRes.setContentType( pProcessor.getContentType( pXform ) );


      ServletOutputStream outputStream = pRes.getOutputStream();
      if (outputStream instanceof PageContextServletOutputStream) {
        PageContextServletOutputStream pxos = (PageContextServletOutputStream) outputStream;
        Writer writer = pxos.getOut();
        pProcessor.process( pInput, pXform, pParams, writer, pErr );
      }
      else {
        Writer writer = new java.io.OutputStreamWriter(new NonFlushingOutputStream(outputStream),pRes.getCharacterEncoding());
        pProcessor.process( pInput, pXform, pParams, writer, pErr );

        // here, we flush the writer, so that it writes any content to the
        // output stream, which is fine, because the output stream is
        // a NonFlushingOutputStream, so we won't flush early
        writer.flush();
      }
      if (override) pProcessor.resetOutputProperties( pXform );
    }
  }

  /**
   * This class is a workaround required by the fact that we are not allowed
   * to call flush on an output stream inside of a tag library on websphere.
   *
   * Unfortunately, the XSL processor feels as though it needs to call flush
   * so we just hide that here.
   */
  class NonFlushingOutputStream extends FilterOutputStream {
     NonFlushingOutputStream(OutputStream pOS) {
       super(pOS);
     }

     public void flush() throws IOException {}
  }

  //-------------------------------------------------------------
  // class utils

  static String translate( String url, DynamoHttpServletRequest pReq )
  {
    if (url == null || !URLUtils.isRelative(url)) {
      return null;
    }

    return ServletUtil.resolveTranslatedPathFromURI( url, pReq );
  }

  static Map getParams( DynamoHttpServletRequest pReq,
                        int                      pass,
                        ApplicationLogging       pLog )
  {
     // loads various params as objects
     // but they almost always get converted to strings
     // doesn't do anything about duplicated names

     Map         params = new HashMap();
     Enumeration names;
     String      name;
     Object      value;

     switch( pass )
     {
       case ENUM_PASS_QUERY:
         names = pReq.getQueryParameterNames();
         while( names.hasMoreElements() )
         {
           name  = (String) names.nextElement();
           value = pReq.getQueryParameter(name);
           if (value != null) params.put( name, value );
         }
         break;

       case ENUM_PASS_POST:
         names = pReq.getPostParameterNames();
         while( names.hasMoreElements() )
         {
           name  = (String) names.nextElement();
           value = pReq.getPostParameter(name);
           if (value != null) params.put( name, value );
         }
         break;

       case ENUM_PASS_LOCAL:
         names = pReq.getParameterNamesInStack();
         while( names.hasMoreElements() )
         {
           name  = (String) names.nextElement();
           // filter oparam compiled pages
           if (!name.equalsIgnoreCase(OUTPUT_PARAM) &&
               !name.equalsIgnoreCase(FAILURE_PARAM) )
           {
              value = pReq.getLocalParameter( name );
              if (value != null) params.put( name, value );
           }
         }
         break;

       case ENUM_PASS_ALL:
         names = pReq.getParameterNamesInStack();
         while( names.hasMoreElements() )
         {
           name  = (String) names.nextElement();
           // filter oparam compiled pages
           if (!name.equalsIgnoreCase(OUTPUT_PARAM) &&
               !name.equalsIgnoreCase(FAILURE_PARAM) )
           {
              // changed from getParameter to getObjectParameter
              // to allow for DOM objects to be passed in as 
              // parameters
              value = pReq.getObjectParameter( name );
              if (value != null) params.put( name, value );
           }
         }
         break;

       case ENUM_PASS_NONE:
       default:
         // should not reach here
         break;
     }

     if (pLog.isLoggingDebug())
     {
        java.util.Iterator itr = params.entrySet().iterator();

        while( itr.hasNext() )
        {
          Map.Entry entry = (Map.Entry) itr.next();
          pLog.logDebug( "  param: " + entry.getKey() + " = " + entry.getValue() );
        }
     }

     return params;
  }
}


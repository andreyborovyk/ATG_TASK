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

import javax.servlet.ServletException;

import org.xml.sax.ErrorHandler;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Vector;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;

import atg.service.cache.CacheAdapter;
import atg.core.net.URLUtils;

import atg.xml.tools.*;
import atg.xml.service.*;

/**
 * This droplet parses an XML document which is specified by
 * the <i>input</i> parameter.  The result of the parse will 
 * be bound to the <i>document</i> parameter, 
 * which can then be manipulated inside the <i>output</i> oparam.  
 *
 * <p>
 * These are the parameters for the <code>XMLToDOM</code> droplet:
 *
 * <dl>
 * 
 * <dt><i>Input Paramaters</i></dt>
 * <dd>&#160;</dd>
 * 
 * <dt><b>input</b> (required)</dt>
 * <dd>Specifies the XML document to be parsed. This can be either an
 *     absolute or relative URL for an XML document. 
 *     <br><br>
 * </dd>
 *    
 * <dt>
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
 *     If there is no <code>DOCTYPE</code> declaration, this 
 *     parameter has no effect.
 *     <br><br>
 * </dd>
 *
 * <dt><i>Output Paramaters</i></dt>
 * <dd>&#160;</dd>
 * 
 * <dt><b>document</b> (optional)</dt>
 * <dd>This parameter will be bound to a DOM document if the XML document was
 *     successfully retrieved and parsed. This parameter name is optional. 
 *     If a name is not provided, the output will be bound to the
 *     <code>document</code> parameter.
 *     <br><br>
 * </dd>
 * 
 * <dt><b>errors</b> (optional)</dt>
 * <dd>This parameter will be bound to an Enumeration of Exceptions 
 *     if there were failures when parsing or retrieving the XML document.
 *     <br><br>
 * </dd>
 *
 * <dt><i>Open Paramaters</i></dt>
 * <dd>&#160;</dd>
 * 
 * <dt><b>unset</b></dt>
 * <dd>This oparam will be used when there is no <i>input</i>
 *     document URL parameter.
 *     <br><br>
 * </dd>
 *
 * <dt><b>failure</b></dt>
 * <dd>This oparam will be used  to format output when there was a failure to 
 *     retrieve, parse, or serialize the XML document. It will also be invoked
 *     if the <code>documentCache</code> property is <code>null</code>.
 *     <br><br>
 * </dd>
 *
 * <dt><b>output</b></dt>
 * <dd>This oparam can be used to process or render the XML document 
 *     when it has been successfully retrieved and parsed. 
 *     If there is no <i>output</i> parameter, 
 *     the DOM is serialized directly to the response.
 *
 * </dl>
 *
 * <p>
 *     Static files should almost always be served directly.
 *     When no <i>output</i> oparam is present, the process is not just a 
 *     transparent conversion of the XML, since it can be validated or indented, 
 *     and it will loose any internal DTD subset. 
 *     This cycle of parsing, validation, and serialization 
 *     will be much slower than simply passing the file to the response,
 *     but it can be useful for debugging during development.
 *
 *     When the DOM is serialized ot the page, it uses the
 *     encoding and MIME type from the Dynamo servlet pipeline.
 *     These are typically determined by the <code>EncodingTyper</code> 
 *     and <code>MimeTyper</code> components, and the default values
 *     will be <code>ISO-8859-1</code> and <code>text/html</code>.
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/XMLToDOM.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 */
public class XMLToDOM extends DynamoServlet 
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/XMLToDOM.java#2 $$Change: 651448 $";

  /** The name of the <i>input</i> input parameter */
  protected static String INPUT_PARAM    = "input";
  
  /** The name of the <i>validate</i> input parameter */
  protected static String VALIDATE_PARAM = "validate";
  
  /** The name of the <i>document</i> output parameter */
  protected static String DOCUMENT_PARAM = "document";

  /** The name of the <i>errors</i> outpu parameter */
  protected static String ERRORS_PARAM   = "errors";

  /** The name of the <i>unset</i> open parameter  */
  protected static String UNSET_PARAM    = "unset";
  
  /** The name of the <i>failure</i> open parameter */
  protected static String FAILURE_PARAM  = "failure";
  
  /** The name of the <i>output</i> open parameter */
  protected static String OUTPUT_PARAM   = "output";

  //-------------------------------------
  // Properties
  //-------------------------------------

  DocumentCache mDocumentCache;

  /** 
   * The DocumentCache used by this component
   */
  public void setDocumentCache(DocumentCache pDocumentCache)
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
   * Parse an XML document
   */
  public void service(DynamoHttpServletRequest  pReq, 
                      DynamoHttpServletResponse pRes) 
    throws ServletException, IOException 
  {
    boolean  failure  = false;
    boolean  validate = false;
    boolean  loadDTD  = true;
    boolean  output   = true;

    String   inputString = null;
    Document dom = null;
    Object   value;

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(this, true, false);
    errorHandler.setDefaultSystemId( this.getName() );

    try 
    {
      // flag the case when there's no output oparam
      output = (pReq.getLocalParameter(OUTPUT_PARAM) != null);

      // test required inputs
      if ((value = pReq.getParameter(INPUT_PARAM)) == null)  
      {
        pReq.serviceLocalParameter( UNSET_PARAM, pReq, pRes );  
        return;
      }

      inputString = value.toString();
      errorHandler.setDefaultPublicId( inputString );
      
      if ((value = pReq.getParameter(VALIDATE_PARAM)) != null) 
      {
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
  
      // parsing or retrieving from the DocumentCache

      DocumentCacheKey key;

	if (URLUtils.isRelative(inputString))
      {
	  // Needs some conversion here to find out the
	  // absolute file path of relative and absolute URIs.
	  String translated = ServletUtil.resolveTranslatedPathFromURI(inputString, pReq);

          System.out.println("translated = " + translated);

	  key = new DocumentCacheKey( null, translated, validate, loadDTD );
	}
      else 
      {
	  key = new DocumentCacheKey( inputString, null, validate, loadDTD );
	}

      key.setErrorHandler( errorHandler );

      // NB. Need to add a SAX ErrorHandler here to report
	// back errors to the user

	dom = (Document) mDocumentCache.get( key );

	if (errorHandler.getHasErrors()) 
      {
	  failure = true;
	  
	  // If we have an error make sure that we attempt
	  // to remove the key from the cache
	  if ( key != null ) mDocumentCache.remove( key );
	  
	  // Bind errors into the request context.
	  pReq.setParameter( ERRORS_PARAM, errorHandler.getErrors() );
	} 
      else if (!output)
      {
        // serialize the DOM to the response
        // use default encoding with pretty printing

        CacheAdapter         ca  = getDocumentCache().getCacheAdapter();
        DocumentCacheAdapter dca = (DocumentCacheAdapter) ca;
        XMLToolsFactory      xtf = dca.getXmlToolsFactory();
        DocumentFormatter    fmt = xtf.createDocumentFormatter();
  
        fmt.formatToXML( dom, pRes.getWriter(), null, false, true, 2 );
      }
    }
    catch (Exception e) 
    {
	if (isLoggingError()) {
	  logError(e);
      }
      if (!errorHandler.getHasErrors()) {
        Vector errors = new Vector( 1 );
        errors.addElement( e );
	  pReq.setParameter( ERRORS_PARAM, errors.elements() );
      }
	failure = true;
    }

    // render oparams
 
    if (failure) 
    {
	pReq.serviceLocalParameter( FAILURE_PARAM, pReq, pRes );
    }
    else if (output)
    {
	// Bind the resulting DOM into the request context
	pReq.setParameter( DOCUMENT_PARAM, dom );
      pReq.serviceLocalParameter( OUTPUT_PARAM, pReq, pRes );
    }
  }
}



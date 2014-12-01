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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Enumeration;

import atg.droplet.ForEach;
import atg.servlet.*;

import atg.xml.tools.*;

/**
 * <p>
 * Select a set of DOM nodes using a pattern and iterate over them.     
 *
 * <p>
 * These are the parameters for the <code>NodeForEach</code> droplet:
 *
 * <dl>
 * 
 * <dt><i>Input Paramaters</i></dt>
 * <dd>&#160;</dd>
 * 
 * <dt><b>node</b> (required)</dt>
 * <dd>This parameter specifies the DOM node to be passed to this droplet.
 *     If it is a <code>Document</code> node, then the 
 *     selection is made relative to the root element of the document.<br><br>
 *
 * <dt><b>select</b> (optional)</dt>
 * <dd>The pattern used to select the nodes to be iterated over. 
  *    This pattern can be any XPath expression, see
 *     <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a>
 *     (1999-11-16).
 *     If no pattern is provided then select all the element 
 *     children of the target <i>node</i>.<br><br>
 * </dd>
 *
 * <dt><i>Other Parameters</i></dt>
 * <dd>&#160;</dd>
 *
 * <dt><b>output</b></dt>
 * <dt><b>startOutput</b></dt>
 * <dt><b>endOutput</b></dt>
 * <dt><i>etc.</i>
 *
 * <dd> All standard output and open parameter definitions 
 *      are inherited from the <code>ForEach</code> droplet. 
 * </dd>
 *
 *</dl>
 *
 * <p>
 * There are no <i>failure</i> or <i>unset</i> oparams for the 
 * <code>NodeForEach</code> droplet, but the <i>node</i> is required,
 * and there can be errors in the execution of the XML query. 
 * For example, there will be an error if the  
 * <i>select</i> parameter is an illegal XPath expression.
 * If an error occurs during node selection,
 * the resulting array will be null, 
 * and the <i>empty</i> oparam will be rendered.
 * If error logging is enabled, 
 * the details of the exception will be written to the log.
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeForEach.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 */
public class NodeForEach extends ForEach
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeForEach.java#2 $$Change: 651448 $";

  /** The name of the <i>node</i> input parameter */
  public static String NODE_PARAM = "node";

  /** The name of the <i>select</i> input parameter */
  public static String SELECT_PARAM = "select";

  //-------------------------------------

  /** 
   *  The default XPath expression to use 
   *  when the <i>select</i> parameter is unset. 
   */
  public static String DEFAULT_XPATH_EXPRESSION = "child::*";

  //-------------------------------------
  // Properties
  //-------------------------------------

  /** 
   * The XMLToolsFactory used by this component. 
   */
  XMLToolsFactory mXMLToolsFactory;

  /** 
   * Set the XMLToolsFactory to be used by this component. 
   */
  public void setXmlToolsFactory(XMLToolsFactory pXMLToolsFactory)
  {
    mXMLToolsFactory = pXMLToolsFactory;
  }

  /** 
   * Get the XMLToolsFactory used by this component. 
   */
  public XMLToolsFactory getXmlToolsFactory()
  {
    return mXMLToolsFactory;
  }

  //-------------------------------------
  
  /**
   * Gets the array-like value (e.g., array, Vector, Enumeration) 
   * to be used by this droplet from the request.  
   * This implementation uses an XPath query.
   */
  public Object getArray( DynamoHttpServletRequest pReq ) 
  {
    Node        node   = null;
    String      select = DEFAULT_XPATH_EXPRESSION;
    Enumeration result = null;
    Object      value;
    
    value = pReq.getObjectParameter( NODE_PARAM );
    
    if (value != null && value instanceof Node) 
    {
  	 node = (Node) value;
       if (node instanceof Document) 
       {
	    node = ((Document)node).getDocumentElement();
       }
    }

    value = pReq.getParameter( SELECT_PARAM );

    if (value != null)
    {
      select = value.toString();
    }

    if (node != null && mXMLToolsFactory != null) 
    {
      try 
      {
        XSLQueryEngine engine = mXMLToolsFactory.createXSLQueryEngine();

        Element element = (node instanceof Element ? (Element) node : null);

	  result = engine.select( select, node, element );
      }
      catch (Exception e) 
      {	
	  if (isLoggingError())
	    logError(e);
      }
    }

    return result;
  }
}


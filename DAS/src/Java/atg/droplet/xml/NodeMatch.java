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

import atg.servlet.*;

import atg.xml.tools.*;

/**
 * <p>
 * Select the next DOM node that matches a pattern.     
 *
 * <p>
 * These are the parameters for the <code>NodeMatch</code> droplet:
 *
 * <dl>
 * 
 * <dt><i>Input Paramaters</i></dt>
 * <dd>&#160;</dd>
 *
 * <dt><b>node</b> (required)</dt>
 * <dd>The DOM node from which to start the search.
 *     The match will only find nodes appearing after this node in the document.
 *     If this is a <code>Document</code> node, 
 *     then the whole document will be searched.
 *     <br><br>
 * </dd>
 * 
 * <dt><b>match</b> (required)</dt>
 * <dd>The pattern that is used to select the next node. 
 *     This pattern can be any XPath expression, see
 *     <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a>
 *     (1999-11-16). Examples:    
 * <blockquote>
 * <table>
 *  <tr>
 *    <td><code>comment()</code></td>	     
 *    <td>
 *      select a comment child of the current node
 *    </td>
 *  </tr>
 *  <tr>
 *    <td><code>widget-order</code></td>
 *    <td>select the next widget-order element</td>
 *  </tr>
 *  <tr>
 *    <td><code>widget-order[@id=1111]</code></td> 
 *    <td>select the widget-order element with the id attribute 1111</td>
 *  </tr>
 *  <tr>
 *    <td><code>widget-order/@id</code></td>
 *    <td>
 *    select the value of the id attribute for the next widget-order element
 *    </td>
 *  </tr> 
 *  </table>
 * </blockquote>
 * </dd>
 *
 * <dt><i>Output Paramaters</i></dt>
 * <dd>&#160;</dd>
 *
 * <dt><b>matched</b></dt>
 * <dd>The next DOM node which matches the pattern.
 *     This parameter is available for formatting in the <i>output</i> oparam.
 *     <br><br>
 * </dd>
 *
 * <dt><i>Open Paramaters</i></dt>
 * <dd>&#160;</dd>
 *
 * <dt><b>unset</b></dt>
 * <dd>The oparam to use when the required <i>node</i> and <i>match</i> 
 *     parameters were not set.<br><br>
 * </dd>
 * 
 * <dt><b>failure</b></dt>
 * <dd>The oparam used when there was a failure 
 *     during the XML query. For example, the <i>match</i>
 *     parameter was an illegal XPath expression.
 *     If error logging is enabled, then the details of the exception 
 *     will be written to the log.<br><br>
 * </dd>
 *
 * <dt><b>empty</b></dt>
 * <dd>The oparam used when no node was selected.<br><br>
 * </dd>
 * 
 * <dt><b>output</b></dt>
 * <dd>The oparam used when a node was selected.
 *     The result is bound to the <i>matched</i> output parameter.
 *     <br><br>
 * </dd>
 * 
 * </dl>
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeMatch.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class NodeMatch extends DynamoServlet 
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeMatch.java#2 $$Change: 651448 $";

  XMLToolsFactory mXMLToolsFactory;

  /** The name of the <i>node</i> input parameter */
  protected static String NODE_PARAM = "node";

  /** The name of the <i>match</i> input parameter */
  protected static String MATCH_PARAM = "match";

  /** The name of the <i>unset</i> open parameter */
  protected static String UNSET_PARAM = "unset";
  
  /** The name of the <i>failure</i> open parameter */
  protected static String FAILURE_PARAM = "failure";
  
  /** The name of the <i>empty</i> open parameter */
  protected static String EMPTY_PARAM = "empty";
  
  /** The name of the <i>output</i> open parameter */
  protected static String OUTPUT_PARAM = "output";

  /** The name of the <i>matched</i> output parameter */
  protected static String MATCHED_PARAM = "matched";

  //-------------------------------------
  // Properties
  //-------------------------------------

  /** 
   * The XMLToolsFactory used by this component. 
   */
  public void setXmlToolsFactory(XMLToolsFactory pXMLToolsFactory)
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
   * The service() method for this droplet.
   */
  public void service(DynamoHttpServletRequest  pReq, 
                      DynamoHttpServletResponse pRes) 
  		throws ServletException, IOException 
  {
    Node    node  = null;
    String  match = null;
    Object  value;

    boolean output  = false;
    boolean failure = false;
    boolean empty   = false;
    boolean unset   = true;

    value = pReq.getObjectParameter( NODE_PARAM );
    
    if (value != null && value instanceof Node) 
    {
  	 node = (Node) value;
       if (node instanceof Document) 
       {
	    node = ((Document)node).getDocumentElement();
       }
    }

    value = pReq.getParameter( MATCH_PARAM );

    if (value != null) 
    {
       match = value.toString();
    }

    if (node != null  && match != null && mXMLToolsFactory != null) 
    {
      unset = false;

      try 
      {
        XSLQueryEngine engine  = mXMLToolsFactory.createXSLQueryEngine();

        // find the first element ancestor - see applix case 54591

        Node nsnode = node;

        while (nsnode != null && !(nsnode instanceof Element))
        {
          nsnode = nsnode.getParentNode();
        }

        Node   matched = engine.match( match, node, (Element)nsnode );

        if (matched != null)
        {
           // bind the resulting Node into the request context
           pReq.setParameter( MATCHED_PARAM, matched );
           output = true;
        }
        else
        {
           empty  = true;
        }
      }
      catch (Exception e) 
      {
	  if (isLoggingError())
	    logError(e);
        failure = true;
      }
    }

    if (output) 
    {
       // some nodes were successfully matched 
       pReq.serviceLocalParameter( OUTPUT_PARAM, pReq, pRes );
    }   
    else if (empty)
    {
       // no nodes were matched 
       pReq.serviceLocalParameter( EMPTY_PARAM, pReq, pRes );  
    }
    else if (unset)
    {
       // the node or match parameters were not set
       pReq.serviceLocalParameter( UNSET_PARAM, pReq, pRes );  
    }
    else if (failure)
    {
       // error in the XSL processor
       // probably a bad XPath expression
       pReq.serviceLocalParameter( FAILURE_PARAM, pReq, pRes );  
    }
  }
}



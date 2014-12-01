/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.taglibs.xtags.xpath;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import org.apache.taglibs.xtags.util.JspVariableContext;

/** The add tag parses it's body (as an XML fragment) and appends the contents to the
  * current node. The current node must be an Element.
  *
  * @author James Elson
  */
public class AddTag extends AbstractBodyTag {
    
    private XPath beforeXPath;
    private XPath afterXPath;
    
    public AddTag() {
    }

    public int doEndTag() throws JspException  {
        Object element = TagHelper.getInputNodes(pageContext, this, false );
        if (element == null) {
            throw new JspException( "No current node to add content to" );
        }
        if (! (element instanceof Element) ) {
            throw new JspException( "Current node is not an Element" );
        }
        if (bodyContent != null) {
            try {
                StringReader sreader = new StringReader("<dummy>"+bodyContent.getString()+"</dummy>");
                SAXReader reader = new SAXReader();
                Document doc = reader.read(sreader);
                Element root = doc.getRootElement();
                List nodes = root.content();
                while (! nodes.isEmpty() ) {
                    Node node = (Node)nodes.remove(0);
                    node.detach();
                    ((Element)element).add( node );
                }
            } 
            catch (DocumentException e) { 
                handleException(e);
            }
        }
        
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        beforeXPath = null;
        afterXPath = null;
    }

    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /** Sets an XPath expression used to determine a child element of the current element.
     *  The body contents will be inserted just before the first node that matches this
     *  XPath.
     */
    public void setAfter(String after) {
        this.afterXPath = createXPath( after );
    }

    /** Sets an XPath expression used to determine a child element of the current element.
     *  The body contents will be inserted just after the first node that matches this
     *  XPath.
     */
    public void setBefore(String before) {
        this.beforeXPath = createXPath( before );
    }
    
    /** A factory method to create new XPath instances */
    protected XPath createXPath(String xpathExpression) {
        XPath xpath = getDocumentFactory().createXPath( xpathExpression ); 
        xpath.setVariableContext( JspVariableContext.getInstance( pageContext ) );
        return xpath;
    }

    /** @return the factory used to create XPath instances */
    protected DocumentFactory getDocumentFactory() {
        return DocumentFactory.getInstance();
    }
}

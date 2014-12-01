/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * information on the Apache Software Foundation, please see * <http://www.apache.org/>.
 *
 */

package org.apache.taglibs.xtags.xpath;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.XMLWriter;

/** A tag which performs a copy operation like the XSLT tag - a shallow copy
  *
  * @author James Strachan
  */
public class CopyTag extends AbstractTag {

    /** Holds the node selected for this tag */
    private Node node;
    
    /** Holds the current XMLWriter used in this tag */
    private XMLWriter writer;
    
    /** Holds value of property xpath. */
    private XPath xpath;
    

    public CopyTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException {
        node = getNode();
        if ( node != null ) {
            try {
                writer = TagHelper.getXMLWriter( pageContext, this );
                if ( node instanceof Element ) {
                    writer.writeOpen( (Element) node );
                }
                else {
                    writer.write( node );
                }
            }
            catch (IOException e) {
                handleException(e);
            }
        }
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() throws JspException {
        if ( node instanceof Element && writer != null ) {
            try {
                writer.writeClose( (Element) node );
            }
            catch (IOException e) {
                handleException(e);
            }
            finally {
                node = null;
                writer = null;
            }
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        node = null;
        writer = null;
        xpath = null;
    }

    // Properties
    //-------------------------------------------------------------------------                
    
    /** Setter for property select.
     * @param select New value of property select.
     */
    public void setSelect(String select) {
        if ( select != null && ! select.equals( "." ) ) {
            xpath = createXPath( select );
        }
        else {
            xpath = null;
        }
    }
    
    /** Returns the node selected for this tag
     */
    protected Node getNode() {
        Object input = getInputNodes();
        if ( xpath != null ) {
            input = xpath.selectNodes( input );
        }
        return getNode( input );
    }
    
    protected Node getNode(Object input) {
        if ( input instanceof Element ) {
            return (Element) input;
        }
        else if ( input instanceof Node ) {
            // don't output documents
            if ( !( input instanceof Document ) ) {
                return (Node) input;
            }
        }
        else if ( input instanceof List ) {
            List list = (List) input;
            if ( list.size() > 0 ) {
                return getNode( list.get(0) );
            }
        }
        return null;
    }

}

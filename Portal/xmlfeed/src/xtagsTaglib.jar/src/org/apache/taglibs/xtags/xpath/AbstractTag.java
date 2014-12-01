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
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.taglibs.xtags.xpath;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.NodeFilter;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.rule.Stylesheet;

import org.jaxen.VariableContext;

import org.apache.taglibs.xtags.util.JspNestedException;
import org.apache.taglibs.xtags.util.JspVariableContext;



/** A tag which performs an XPath expression on the current context Node
  *
  * @author James Strachan
  */
public abstract class AbstractTag extends TagSupport {

    protected static final Document EMPTY_DOCUMENT = DocumentHelper.createDocument();
    
    protected static final boolean ALLOW_FLUSH = false;
    
    protected Object context;
    
    
    public AbstractTag() {
    }

    public void release() {
        context = null;
    }

    public void flush() throws JspException {
        if ( ALLOW_FLUSH ) {
            try {
                pageContext.getOut().flush();
            }
            catch (IOException e) {
                handleException(e);
            }
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    public void setContext(Object context) {
        this.context = context;
    }

    
    // Helper methods
    //-------------------------------------------------------------------------                
    /** @return true if the given filter matches a node in the 
      * input nodes
      */
    public boolean matches(NodeFilter filter) {
        Object input = getInputNodes( false );
        if ( input == null ) {
            // use an empty document to support
            // filters that just use XPath variables
            // such as "$foo='bar'"
            input = EMPTY_DOCUMENT;
        }
        if ( input instanceof List ) {
            List list = (List) input;
            for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
                Object object = iter.next();
                if ( object instanceof Node ) {
                    Node node = (Node) object;
                    if ( filter.matches( node ) ) {
                        return true;
                    }
                }
            }
        }
        else if ( input instanceof Node ) {
            Node node = (Node) input;
            if ( filter.matches( node ) ) {
                return true;
            }
        }
        return false;
    }

    /** @return the input node on which to make a selction
      */
    public Object getInputNodes() {
        return getInputNodes( true );
    }
    
    public Object getInputNodes( boolean warn ) {
        if ( context == null ) {
            return TagHelper.getInputNodes( pageContext, this, warn );
        }
        return context;
    }
    
    public void setInputNodes( Object inputNodes ) {
        TagHelper.setInputNodes( pageContext, inputNodes );
    }
    
    public Stylesheet getStylesheet() {
        StylesheetTag tag  = (StylesheetTag) findAncestorWithClass( 
            this, StylesheetTag.class 
        );
        if ( tag != null ) {
            return tag.getStylesheet();
        }
        else {
            return TagHelper.getStylesheet( pageContext );
        }
    }

    
    // Implementation methods
    //-------------------------------------------------------------------------                

    /** A factory method to create new XPath instances */
    protected XPath createXPath(String xpathExpression) {
        VariableContext variableContext = JspVariableContext.getInstance( pageContext );
        return getDocumentFactory().createXPath( xpathExpression, variableContext ); 
    }

    /** A factory method to create new XPath filter */
    protected NodeFilter createXPathFilter(String xpathExpression) {
        VariableContext variableContext = JspVariableContext.getInstance( pageContext );
        return getDocumentFactory().createXPathFilter( xpathExpression, variableContext ); 
    }

    /** @return the factory used to create XPath instances */
    protected DocumentFactory getDocumentFactory() {
        return DocumentFactory.getInstance();
    }
    
    /** Handles non-JspExceptions thrown in this instance
      */
    protected void handleException( Exception e ) throws JspException {
        if ( e instanceof JspException ) {
            throw (JspException) e;
        }
        else {
            pageContext.getServletContext().log( e.getMessage(), e );
            throw new JspNestedException( e );
        }
    }    
}

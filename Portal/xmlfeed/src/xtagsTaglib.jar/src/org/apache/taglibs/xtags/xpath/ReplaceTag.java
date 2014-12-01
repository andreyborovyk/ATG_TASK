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

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.apache.taglibs.xtags.util.JspVariableContext;

/** The replace tag parses it's body (as an XML fragment) and replaces the contents to the
  * current node with this new XML fragment. The current node must be an Element.
  *
  * @author James Elson
  */
public class ReplaceTag extends AbstractBodyTag {
    
    public ReplaceTag() {
    }

    public int doEndTag() throws JspException  {
        Object context = TagHelper.getInputNodes(pageContext, this, false );
        if (context == null) {
            logInfo( "No current node to replace" );
            return EVAL_PAGE;
        }
        
        try {
            String xmlFragment = null;
            if (bodyContent != null) {
                xmlFragment = bodyContent.getString();
            }
            if (context instanceof List) {
                List els = (List )context;
                if (els.size() > 1) {
                    throw new JspException( "Current context contains more than one node");
                }
                if (els.size() == 1) {
                    context = els.get(0);
                }
            }
            if (context instanceof Document) {
                if (xmlFragment == null) {
                    throw new JspException( "Cannot replace document with empty body");
                }
                Document sourceDoc = (Document) context;
                Document newDoc = DocumentHelper.parseText( xmlFragment );

                // clear source doc contents
                sourceDoc.clearContent();
                for ( int i = 0, size = newDoc.nodeCount(); i < size; i++ ) {
                    Node node = newDoc.node(i);
                    // detach from new doc
                    node.detach();
                    // add to source
                    sourceDoc.add( node );
                }
            } else {
                if (! (context instanceof Element) ) {
                    throw new JspException( "Current node is not an Element: "+context.getClass().getName() );
                }
                Element element = (Element)context;

                SAXReader reader = new SAXReader();

                if (element.isRootElement()) {
                    if (xmlFragment == null) {
                        throw new JspException( "Cannot replace root element with empty body");
                    }
                    Document newDoc = DocumentHelper.parseText( xmlFragment );
                    Document sourceDoc = element.getDocument();
                    Element newRoot = newDoc.getRootElement();
                    newRoot.detach();
                    sourceDoc.setRootElement(newRoot);
                } else {
                    Element parent = element.getParent();
                    List parentContent = parent.content();
                    int index = parentContent.indexOf(element);
                    parentContent.remove(index);
                    if (xmlFragment != null) {
                        Document newDoc = DocumentHelper.parseText( "<dummy>"+xmlFragment+"</dummy>" );
                        parentContent.addAll(index, newDoc.getRootElement().content() );
                    }
                }
            }
        } catch (DocumentException e) { 
            handleException(e);
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
    }
}

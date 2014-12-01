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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.dom4j.DocumentFactory;
import org.dom4j.Node;
import org.dom4j.XPath;

import org.apache.taglibs.xtags.util.JspVariableContext;


/** A tag which performs an iteration over the results of an XPath expression on an XML document
  *
  * @author James Strachan
  */
public class ForEachTag extends AbstractBodyTag implements ContextNodeTag {

    /** Holds the XPath selection instance. */
    private XPath xpath;
    
    /** Holds value of property id. */
    private String id;
    
    /** Holds value of property type. */
    private String type;    
    
    /** Holds the current iterator */
    private Iterator iterator;
    
    /** Holds value of property contextNode. */
    private Object contextNode;
    
    /** Holds the sorting XPath expression */
    private XPath sortXPath;
    /** Holds value of property distinct. */
    private boolean distinct;
    
    /** The input nodes */
    private Object context;
    
    /** Cache of original context which is restored after the loop finishes */
    private Object originalContext;
    
    /** Holds value of property ascending. */
    private boolean ascending = true;    
    
    public ForEachTag() {
    }

    /** Causes the iteration to be stopped like the Java 'break' statement.
      */
    public void breakLoop() throws JspException {
        finishLoop();
    }
    
    // ContextNodeTag interface
    //------------------------------------------------------------------------- 
    public Object getContext() {
        return contextNode;
    }
    
    // BodyTag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {
        originalContext = TagHelper.getInputNodes( pageContext );
        iterator = null;
        if ( xpath != null ) {    
            List list = selectNodes();
            iterator = list.iterator();
            if ( hasNext() ) {
                return EVAL_BODY_TAG;
            }
        }
        reset();
        return SKIP_BODY;
    }

    public int doAfterBody() throws JspException {
        if ( iterator != null ) {
            if ( hasNext() ) {
                return EVAL_BODY_TAG;
            }
            else {
                finishLoop();
            }
        }
        // reset the original context just in case a <jsp:include>
        // was used in the body or whatnot
        TagHelper.setInputNodes( pageContext, originalContext );
        reset();
        return SKIP_BODY;
    }
    

    public void release() {
        reset();
        xpath = null;
        sortXPath = null;
        distinct = false;
        ascending = true;
    }

    
    // Properties
    //-------------------------------------------------------------------------                    
    public void setContext(Object context) {
        this.context = context;
    }
    
    /** Sets the select XPath expression
      */
    public void setSelect(String select) {
        this.xpath = createXPath( select );
    }

    /** Sets the XPath selection expression
      */
    public void setSelectXPath(XPath xpath) {
        this.xpath = xpath;
    }
    
    /** Setter for property sort.
     * @param sort New value of property sort.
     */
    public void setSort(String sort) {
        if ( sort == null ) {
            this.sortXPath = null;
        }
        else {
            this.sortXPath = createXPath( sort );
        }
    }

    public void setSortXPath(XPath sortXPath) {
        this.sortXPath = sortXPath;        
    }
    
    /** Getter for property distinct.
     * @return Value of property distinct.
     */
    public boolean isDistinct() {
        return distinct;
    }
    /** Setter for property distinct.
     * @param distinct New value of property distinct.
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
    
    /** Getter for property id.
     * @return Value of property id.
     */
    public String getId() {
        return id;
    }
    
    /** Setter for property id.
     * @param id New value of property id.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /** Getter for property type.
     * @return Value of property type.
     */
    public String getType() {
        return type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     */
    public void setType(String type) {
        this.type = type;
    }
    
    
    /** Getter for property ascending.
     * @return Value of property ascending.
     */
    public boolean isAscending() {
        return ascending;
    }
    
    /** Setter for property ascending.
     * @param ascending New value of property ascending.
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    protected List selectNodes() {
        if ( xpath != null ) {    
            Object input = getInputNodes();
            if ( sortXPath != null ) {
                List answer = xpath.selectNodes( input, sortXPath, distinct );
                if ( ! ascending ) {
                    Collections.reverse( answer );
                }
                return answer;
            }
            else {
                return xpath.selectNodes( input );
            }
        }
        return Collections.EMPTY_LIST;
    }
    
    /** @return the input node on which to make a selction
      */
    public Object getInputNodes() {
        if ( context == null ) {
            return TagHelper.getInputNodes( pageContext, this, true );
        }
        return context;
    }
    
    
    /** Finishes the loop and outputs the current body */
    protected void finishLoop() throws JspException {
        iterator = null;
        contextNode = null;
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        }
        catch (IOException e) {
            handleException(e);
        }
        bodyContent.clearBody();
    }
    
    /** Performs an iteration and defines a variable of the current object */
    protected boolean hasNext() {
        if ( iterator.hasNext() ) {
            contextNode = iterator.next();
            TagHelper.defineVariable( pageContext, getId(), contextNode );
            if ( contextNode != null ) {
                return true;
            }
        }
        return false;
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
    
    private void reset() {
        iterator = null;
        contextNode = null;
    }
    
}

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

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Node;
import org.dom4j.XPath;

/** A tag which defines a variable from an XPath expression 
  *
  * @author James Strachan
  */
public class VariableTag extends AbstractTag {

    /** Holds the XPath selection instance. */
    private XPath xpath;    
    /** Holds value of property id. */
    private String id;
    /** Holds value of property type. */
    private String type;
    
    
    //-------------------------------------------------------------------------                
    public VariableTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {        
        if ( xpath != null ) {    
            Object value = null;
            Object inputNodes = getInputNodes(false);
            if ( type == null ) {
                // default to string value
                value = xpath.valueOf( inputNodes );
            }
            else if ( type.equalsIgnoreCase( "string" ) || type.equals( "java.lang.String" ) ) {
                value = xpath.valueOf( inputNodes );
            }
            else if (type.equals( "node" ) || type.equals( "org.dom4j.Node") ) {
                value = xpath.selectSingleNode( inputNodes );
            }
            else if (type.equals( "list" ) || type.equals( "java.util.List") ) {
                value = xpath.selectNodes( inputNodes );
            }
            else if ( type.equalsIgnoreCase( "number" ) || type.equals( "java.lang.Number" ) || type.equals( "java.lang.Double" ) ) {
                Number n = xpath.numberValueOf( inputNodes );
                value = n;
                if ( type.equals( "java.lang.Double" ) && ! (value instanceof Double) ) {
                    value = new Double( n.doubleValue() );
                }
            }
            else {
                value = xpath.selectObject( inputNodes );
            }
            if ( value == null ) {
                pageContext.removeAttribute( getId() );
            }
            else {
                pageContext.setAttribute( getId(), value );
            }
        }
        return SKIP_BODY;
    }

    public void release() {
        super.release();
        xpath = null;
        id = null;
        type = null;
    }

    // Properties
    //-------------------------------------------------------------------------                
    
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
}

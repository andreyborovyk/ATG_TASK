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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;


/** A tag to produce an XML element which can contain other attributes 
  * or elements like the <code>&lt;xsl:element&gt;</code> tag.
  *
  * @author James Strachan
 * @version $Id: //app/portal/version/10.0.3/xmlfeed/xtagsTaglib.jar/src/org/apache/taglibs/xtags/xpath/ElementTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  */
public class ElementTag extends AbstractBodyTag  {

    /** Should attribute values be trimmed of whitespace? */
    protected static final boolean TRIM_VALUES = true;
    
    /** Holds value of property name. */
    private String name;
    private List attributeNames;
    private Map attributeValues;
    
    //-------------------------------------------------------------------------
    public ElementTag() {
    }

    public void addAttribute( String name ) {
        if ( attributeNames == null ) {
            attributeNames = new ArrayList();
        }
        attributeNames.add( name );
    }

    public void setAttributeValue( String name, String value ) {
        if ( attributeValues == null ) {
            attributeValues = new HashMap();
        }
        attributeValues.put( name, value );
    }

    // BodyTag interface
    //-------------------------------------------------------------------------
    public void release() {
        super.release();
        name = null;
        attributeNames = null;
        attributeValues = null;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_TAG;
    }
    
    public int doAfterBody() throws JspException {
        JspWriter out = bodyContent.getEnclosingWriter();
        //JspWriter out = pageContext.getOut();
        try {
            out.print( "<" + getName() );
            printAttributes( out );
            
            String content = bodyContent.getString();
            if ( content == null || content.length() <= 0 ) {
                out.print( "/>" );
            }
            else {
                out.print( ">" );
                out.print( content );
                out.print( "</" + getName() + ">" );
            }        
            bodyContent.clearBody();
        }
        catch ( IOException e ) {
            handleException( e );
        }
        return SKIP_BODY;
    }
    
    // Properties
    //-------------------------------------------------------------------------
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------    
    protected void printAttributes( JspWriter out ) throws IOException {
        if ( attributeNames != null ) {
            int size = attributeNames.size();
            for ( int i = 0; i < size; i++ ) {
                String attributeName = (String) attributeNames.get( i );
                printAttribute( out, attributeName );
            }
        }
    }
    
    protected void printAttribute( JspWriter out, String attributeName ) throws IOException {
        Object value = null;
        if ( attributeValues != null ) {
            value = attributeValues.get( attributeName );
        }
        String text = attributeName;
        if ( value != null ) {
            if ( TRIM_VALUES ) {
                text = value.toString().trim();
            }
            else {
                text = value.toString();
            }
        }
        out.print( " " + attributeName + "=\"" + text + "\"" );
    }
}

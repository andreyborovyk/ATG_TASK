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
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.XPath;

/** A tag which performs an XPath expression on the current context Node
  *
  * @author James Strachan
  */
public class ValueOfTag extends AbstractTag {

    /** Holds value of property xpath. */
    private XPath xpath;    
    

    public ValueOfTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException  {        
        if ( xpath != null ) {
            try {
                String text = xpath.valueOf( getInputNodes() );
                text = encode( text );
                pageContext.getOut().print( text );
            }
            catch (IOException e) {
                handleException(e);
            }
        }
        return SKIP_BODY;
    }

    public void release() {
        super.release();
        xpath = null;
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

    
    // Helper methods
    //-------------------------------------------------------------------------                    

    /** Encodes the standard entities in the given string
     */
    public static String encode(String text) {
        StringBuffer buffer = new StringBuffer();
        char[] block = text.toCharArray();
        int size = block.length;
        int last = 0;
        for ( int i = 0; i < size; i++) {
            char ch = block[i];
            
            String entity = null;            
            switch ( ch ) {
                case '<' :
                    entity = "&lt;";
                    break;
                case '>' :
                    entity = "&gt;";
                    break;
                case '&' :
                    entity = "&amp;";
                    break;
                default :
                    /* no-op */ ;
            }
            if (entity != null) {
                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
            }
        }
        if ( last < size ) {
            buffer.append(block, last, size - last);
        }
        return buffer.toString();
    }    
}

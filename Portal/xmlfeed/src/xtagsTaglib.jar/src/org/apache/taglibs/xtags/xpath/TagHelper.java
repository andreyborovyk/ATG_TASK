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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.rule.Stylesheet;

/** A number of helper methods
  *
  * @author James Strachan
  */
public class TagHelper {

    /** Request scope attribute name used to pass the context between
      * JSP files 
      */
    public static final String REQUEST_KEY_CONTEXT = "org.apache.taglibs.xtags.taglib.Context";
    
    /** Request scope attribute name used to pass the stylesheet between
      * JSP files */
    public static final String REQUEST_KEY_STYLESHEET = "org.apache.taglibs.xtags.taglib.Stylesheet";

    protected static final OutputFormat outputFormat;
    
    /** Request scope attribute name used to pass the XMLWriter between
      * JSP files 
      */
    public static final String REQUEST_KEY_XML_WRITER = "org.apache.taglibs.xtags.XMLWriter";
    
    static {
        outputFormat = new OutputFormat();
        outputFormat.setSuppressDeclaration(true);
    }
    
    
    public static OutputFormat getOutputFormat( PageContext pageContext ) {
        return outputFormat;
    }
   
    public static XMLWriter getXMLWriter(PageContext pageContext, Tag thisTag) {
        OutputTag tag = (OutputTag) TagSupport.findAncestorWithClass( 
            thisTag, OutputTag.class 
        );
        if ( tag != null ) {
            return tag.getXMLWriter();
        }
        return new XMLWriter( pageContext.getOut(), getOutputFormat( pageContext ) );
    }

    public static XMLWriter createXMLWriter(PageContext pageContext) {
        return new XMLWriter( pageContext.getOut(), getOutputFormat( pageContext ) );
    }

   
    
    /** @return the input node on which to make a selction
      */
    public static Object getInputNodes(PageContext pageContext) {
        Object nodes = pageContext.getAttribute( 
            REQUEST_KEY_CONTEXT, PageContext.PAGE_SCOPE 
        );
        if (nodes == null) {
            nodes = pageContext.getAttribute( 
                REQUEST_KEY_CONTEXT, PageContext.REQUEST_SCOPE 
            );
        }
        return nodes;
    }
    
    /** @return the input node on which to make a selction
      */
    public static Object getInputNodes(PageContext pageContext, Tag thisTag, boolean warn) {
        Object context = null;
        ContextNodeTag tag = (ContextNodeTag) TagSupport.findAncestorWithClass( 
            thisTag, ContextNodeTag.class 
        );
        if ( tag != null ) {
            context = tag.getContext();
        }
        if ( context == null ) {
            context = getInputNodes( pageContext );
        }
/*        
        if ( context == null && warn ) {
            pageContext.getServletContext().log( "WARNING: No Input Node found!" );
            Exception e = new Exception();
            e.printStackTrace();
        }
*/
        return context;
    }
    
    public static void setInputNodes( PageContext pageContext, Object inputNodes ) {
        if ( inputNodes == null ) {
            pageContext.removeAttribute( 
                REQUEST_KEY_CONTEXT,  
                PageContext.PAGE_SCOPE 
            );
            pageContext.removeAttribute( 
                REQUEST_KEY_CONTEXT,  
                PageContext.REQUEST_SCOPE 
            );
        }
        else {
            pageContext.setAttribute( 
                REQUEST_KEY_CONTEXT,  
                inputNodes, 
                PageContext.PAGE_SCOPE 
            );
            pageContext.setAttribute( 
                REQUEST_KEY_CONTEXT,  
                inputNodes, 
                PageContext.REQUEST_SCOPE 
            );
        }
    }
    
    public static Stylesheet getStylesheet(PageContext pageContext) {
        return (Stylesheet) pageContext.getAttribute( 
            REQUEST_KEY_STYLESHEET, 
            PageContext.REQUEST_SCOPE 
        );
    }
    
    public static void setStylesheet(PageContext pageContext, Stylesheet stylesheet) {
        pageContext.setAttribute( 
            REQUEST_KEY_STYLESHEET,  
            stylesheet, 
            PageContext.REQUEST_SCOPE 
        );
    }
    
    public static void defineVariable(PageContext pageContext, String id, Object value ) {
        if ( id != null ) {
            pageContext.setAttribute( id, value );
        }
        setInputNodes( pageContext, value );
    }
}

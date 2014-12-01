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

package org.apache.taglibs.xtags.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.jaxen.VariableContext;

/** A Servlet to display the result of an XPath expression as XML
  * 
  * @author James Strachan
  */

public class JspVariableContext implements VariableContext {

    public static final String KEY_REQUEST_VARIABLE_CONTEXT = "org.apache.taglibs.xtags.jsp.VariableContext";
    
    /** Stores the page context */
    private PageContext pageContext;


    /** A static helper method to return the DOM4J {@link VariableContext}
      * for the current request, lazily creating an instance if one does not
      * currently exist.
      */
    public static JspVariableContext getInstance(PageContext pageContext) {
        JspVariableContext answer = (JspVariableContext) JspHelper.findAttribute( 
                pageContext,  
                KEY_REQUEST_VARIABLE_CONTEXT 
        );
        if ( answer == null ) {
            answer = new JspVariableContext( pageContext );
            pageContext.setAttribute( 
                KEY_REQUEST_VARIABLE_CONTEXT, 
                answer,
                PageContext.PAGE_SCOPE 
            );
        }
        return answer;
    }
    
    public JspVariableContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    public Object getVariableValue(String name) {
        Object answer = JspHelper.findAttribute( pageContext, name );
        if ( answer == null ) {
            answer = pageContext.getRequest().getParameter( name );
            if ( answer == null ) {
                answer = pageContext.getServletContext().getInitParameter( name );
            }
        }
        return answer;
    }
    
    public Object getVariableValue(String prefix, String name) {
        if ( prefix != null && prefix.length() > 0 ) {
            if ( "app".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.APPLICATION_SCOPE );
            }
            else if ( "session".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.SESSION_SCOPE );
            }
            else if ( "request".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.REQUEST_SCOPE );
            }
            else if ( "page".equals( prefix ) ) {
                return pageContext.getAttribute( name, PageContext.PAGE_SCOPE );
            }
            else if ( "param".equals( prefix ) ) {
                return pageContext.getRequest().getParameter( name );
            }
            else if ( "initParam".equals( prefix ) ) {
                return pageContext.getServletContext().getInitParameter( name );
            }
            else if ( "header".equals( prefix ) ) {
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                return request.getHeader( name );
            }
            else if ( "cookie".equals( prefix ) ) {
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                Cookie[] cookies = request.getCookies();
                if ( cookies != null ) {
                    for ( int i = 0, size = cookies.length; i < size; i++ ) {
                        Cookie cookie = cookies[i];
                        if ( name.equals( cookie.getName() ) ) {
                            return cookie.getValue();  
                        }
                    }
                }
                return null;
            }
        }
        Object answer = JspHelper.findAttribute( pageContext, name );
        if ( answer == null ) {
            answer = pageContext.getRequest().getParameter( name );
            if ( answer == null ) {
                answer = pageContext.getServletContext().getInitParameter( name );
            }
        }
        return answer;
    }
    
    public Object getVariableValue(String namespaceURI, String prefix, String name) {
        return getVariableValue(prefix, name);
    }
}

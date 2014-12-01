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

import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.DocumentHelper;
import org.dom4j.rule.Action;
import org.dom4j.rule.Pattern;
import org.dom4j.rule.Rule;
import org.dom4j.rule.RuleManager;
import org.dom4j.rule.pattern.DefaultPattern;

/** An Action which includes a piece of JSP
  *
  * @author James Strachan
  */
public class JspAction implements Action {

    /** Holds value of property jsp. */
    private String jsp;
    
    /** Holds value of property pageContext. */
    private PageContext pageContext;
    

    public JspAction() {
    }

    public JspAction(PageContext pageContext, String jsp) {
        this.pageContext = pageContext;
        this.jsp = jsp;
    }

    
    /** Getter for property jsp.
     * @return Value of property jsp.
     */
    public String getJsp() {
        return jsp;
    }
    
    /** Setter for property jsp.
     * @param jsp New value of property jsp.
     */
    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    /** Getter for property pageContext.
     * @return Value of property pageContext.
     */
    public PageContext getPageContext() {
        return pageContext;
    }
    
    /** Setter for property pageContext.
     * @param pageContext New value of property pageContext.
     */
    public void setPageContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }
    
    
    
    // Action interface
    //-------------------------------------------------------------------------     
    public void run( Node node ) throws Exception {
        if ( pageContext == null ) {
            throw new JspException( "No PageContext. Cannot process JSP: " + jsp );
        }
        else
        if ( jsp == null ) {
            throw new JspException( "No JSP! Cannot execute Action" );
        }
        else {
            Object oldContext = TagHelper.getInputNodes( pageContext );
            TagHelper.setInputNodes( pageContext, node );
            
            pageContext.include( jsp );            
            pageContext.getOut().flush();
            
            TagHelper.setInputNodes( pageContext, oldContext );
        }
    }
}

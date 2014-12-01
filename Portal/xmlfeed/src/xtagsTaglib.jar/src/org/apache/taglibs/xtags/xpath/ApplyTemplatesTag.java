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
import org.dom4j.DocumentHelper;
import org.dom4j.rule.Stylesheet;


/** The body of this tag defines a stylesheet which is implemented via calling
  * a JSP include.
  *
  * @author James Strachan
  */
public class ApplyTemplatesTag extends AbstractTag {

    /** Holds value of property mode. */
    private String mode;    

    /** Holds the XPath object */
    private XPath xpath;
    

    public ApplyTemplatesTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspException {
        flush();
        Stylesheet stylesheet = getStylesheet();
        if ( stylesheet == null ) {
            throw new JspException( 
                "applytemplates tag requires a Stylesheet to be in scope" 
            );
        }
        else {
            try {
                // Tell the current TemplateTag to put its output in the Stylesheet's output list,
                // otherwise the order of the output will be incorrect.
                TemplateTag templateTag = (TemplateTag) findAncestorWithClass( 
                    this, TemplateTag.class 
                );
                templateTag.preApplyTemplates();
                
                Object context = getInputNodes();
                if ( xpath != null ) {
                    stylesheet.applyTemplates( context, xpath );
                }
                else {
                    stylesheet.applyTemplates( context );
                }
                // restore the context back to what it was
                setInputNodes( context );
            }
            catch (JspException e) {
                throw e;
            }
            catch (Exception e) {
                handleException(e);
            }
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        xpath = null;
        mode = null;
    }

    // Properties
    //-------------------------------------------------------------------------                

    public void setSelect( String select ) {
        xpath = createXPath( select );
    }
    
    /** Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }    
}

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

package org.apache.taglibs.xtags.xslt;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource; 

import org.dom4j.Node;
import org.dom4j.Document;
import org.dom4j.io.DocumentSource;

import org.apache.taglibs.xtags.util.JspNestedException;
import org.apache.taglibs.xtags.util.URLHelper;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/** A tag which performs an XSLT transformation on a given XML document
  *
  * @author James Strachan
  */
public class StyleTag extends BodyTagSupport implements ParameterAcceptingTag {
    
    /** The XML data resource. */
    private Object xml;

    /** The XSL stylesheet resource. */
    private Object xsl;

    /** The destination result of the XSLT transformation */
    private Result result;

    /** The XSLT output method, "xml", "html", "text" or whatever */
    private String outputMethod;

    /** XSLT parameters */
    private Map parameters;
    
    /** Indirection buffer, used to avoid flush problems with BodyContent */
    private StringWriter stringWriter;
    
    
    // ParameterAcceptingTag interface
    //-------------------------------------------------------------------------                
    public void setParameter(String name, Object value) {
        if ( parameters == null ) {
            parameters = new HashMap();
        }
        parameters.put( name, value );
    }
    

    // BodyTag interface
    //-------------------------------------------------------------------------                

    /** Evaluate the body content of this tag if 
      * we need to for either the XML or XSLT data; otherwise we skip it.
     *
     * @exception JspException if a JSP error occurs
     */
    public int doStartTag() throws JspException {
        parameters = null;
        return EVAL_BODY_TAG;
    }


    /** Extract the body if required for either the XML or XSLT
      *
      * @exception JspException if a JSP error has occurred
      */
    public int doAfterBody() throws JspException {
        if ( bodyContent != null ) {
            String text = bodyContent.getString().trim();
            if ( text.length() > 0 ) {
                if ( xsl == null ) {
                    xsl = new StringReader( text );
                }
                else if ( xml == null ) { 
                    xml = new StringReader( text );
                }
            }
        }        
	return SKIP_BODY;
    }


    /** Perform the transformation and render the output.
      *
      * @exception JspException if a JSP exception occurs
      */
    public int doEndTag() throws JspException {
        if ( xml == null || xsl == null ) {
            throw new JspException( "Must specify both XML and an XSLT to style" );
        }
        
        // Prepare an input source for the data
        Source data = getSource(xml);

        // Prepare an input source for the stylesheet
        Source style = getSource(xsl);

        // Prepare an output source for the outputs
        Result result = getResult();

        // Use JAXP to perform the stylesheet
        try {
          
          //TransformerFactory factory = TransformerFactory.newInstance();
          TransformerFactory factory = new atg.apache.xalan.processor.TransformerFactoryImpl();
          
            factory.setURIResolver( createURIResolver() );
            Transformer transformer = factory.newTransformer(style);
            configure(transformer);
            transformer.transform( data, result );
            if ( stringWriter != null ) {
                pageContext.getOut().write( stringWriter.toString() );
            }
        } 
        catch (TransformerException e) {
            handleException(e);
        }
        catch (IOException e) {
            handleException(e);
        }
        finally {
            stringWriter = null;
        }
	return EVAL_PAGE;
    }

    
    /**
     * Release any allocated resources.
     */
    public void release() {
	xml = null;
	xsl = null;
	result = null;
        parameters = null;
    }



    // Properties
    //-------------------------------------------------------------------------                
    
    public void setOutputMethod(String outputMethod) {
        this.outputMethod = outputMethod;
    }
    
    /** Sets the dom4j document to be styled */
    public void setDocument(Document document) {
        this.xml = document;
    }

    /** Sets the XSLT transformer used to transform the document */
    public void setTransformer(Transformer transformer) {
        this.xsl = transformer;
    }

    /** Sets the JAXP Result instance to 
      */
    public void setResult(Result result) {
        this.result = result;
    }

    /** Sets where the output is written to
      */
    public void setWriter(Writer writer) {
        setResult( new StreamResult( writer ) );
    }

    /** Sets the SAX ContentHandler that the output is written to */
    public void setResultHandler(ContentHandler handler) {
        setResult( new SAXResult( handler ) );
    }

    /** Sets the XML URL that the input document is read from */
    public void setXml(String xml) {
	this.xml = xml;
    }

    /** Sets the source of the XML document */
    public void setXmlSource(Source source) {
        this.xml = source;
    }

    public void setXmlReader(Reader reader) {
        this.xml = reader;
    }

    /** Sets the XSL URL that the stylesheet is read from */
    public void setXsl(String xsl) {
	this.xsl = xsl;
    }

    public void setXslReader(Reader reader) {
	this.xsl = reader;
    }

    /** Sets the source of the XML document */
    public void setXslSource(Source source) {
        this.xsl = source;
    }




    // Implementation methods
    //-------------------------------------------------------------------------                    

    /** Configures the Transformer before use 
      */
    protected void configure(Transformer transformer) {
        if ( outputMethod != null ) {
            transformer.setOutputProperty( OutputKeys.METHOD, outputMethod );
        }
        if ( parameters != null ) {
            for ( Iterator iter = parameters.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = entry.getKey().toString();
                Object value = entry.getValue();
                transformer.setParameter( name, value );
            }
        }
    }

    /** Construct and return a JAXP Source to read the XML data to be styled.
      *
      * @param source is the object to be converted into a JAXP Source
      * @exception JspException if a JSP error occurs
      */
    protected Source getSource(Object source) throws JspException {
        if (source instanceof Source) {
	    return (Source) source;
        }
        else if (source instanceof Reader) {
	    return new StreamSource((Reader) source);
        }
        else if (source instanceof String) {
            try {
                URL url = URLHelper.createURL( (String) source, pageContext );
                if ( url == null ) {
                    throw new JspException("Bad URL: " + source + ".");
                }
                return new StreamSource(url.openStream(), url.toExternalForm());
            }
            catch (IOException e) {
                throw new JspException( 
                    "Bad URL: " + source + ". Exception: " + e.getMessage() 
                );
            }
        }
        else if (source instanceof Node) {
	    return new DocumentSource((Node) source);
        }
        else if (source instanceof org.w3c.dom.Node) {
	    return new DOMSource((org.w3c.dom.Node) source);
        }
        else if (source instanceof InputSource) {
	    return new SAXSource((InputSource) source);
        }
        else if ( source == null ) {
	    throw new JspException( "No XSLT source specified" );
        }
        else {
	    throw new JspException(
                "Invalid input source type '" + source.getClass().getName() + "'"
            );
        }
    }
 
    /** Construct and return a JAXP Result
      *
      * @exception JspException if a JSP error occurs
      */
    protected Result getResult() throws JspException {
        if ( result == null ) {
            // use StringWriter to avoid flushing problems with BodyContent
            stringWriter = new StringWriter();
            return new StreamResult( stringWriter );
        }
        else {
            stringWriter = null;
        }
        return result;
    }
    
    /** Creates a URI resolver capable of resolving URIs when used in XSLT includes or imports */
    protected URIResolver createURIResolver() {
        return new URIResolver() {
            public Source resolve(String href, String base)
               throws TransformerException {
                try {
                    return getSource(href);
                }
                catch(javax.servlet.jsp.JspException e) {
                    return null;
                }
            }

        };
    }

    /** Handles non-JspExceptions thrown in this instance
      */
    protected void handleException( Exception e ) throws JspException {
        if ( e instanceof JspException ) {
            throw (JspException) e;
        }
        else {
            pageContext.getServletContext().log( e.getMessage(), e );
            e.printStackTrace();
            throw new JspNestedException( e );
        }
    }
}

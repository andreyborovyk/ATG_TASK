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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/** Some helper methods for creating URLs that can handle relative or absolute
  * URIs or full URLs.
  * 
  * @author <a href="mailto:james.strachan@metastuff.com">James Strachan</a>
 * @version $Id: //app/portal/version/10.0.3/xmlfeed/xtagsTaglib.jar/src/org/apache/taglibs/xtags/util/URLHelper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  */
public class URLHelper {

    /** @return the URL for the given URI. If the uri contains a ':' then
      * it is assumed to be a URL, otherwise a local URI resource is used.
      */
    public static URL createURL(String uri, PageContext pageContext) throws MalformedURLException {
        if ( uri.indexOf( ":" ) >= 0 ) {
            return new URL( uri );
        }
        else {
            return getResourceURL( uri, pageContext );
        }
    }
    
    /** @return the URL object for the given resource URI using the
      * ServletContext.getResource(String) method. 
      * If the path does not start with a '/' character then a relative
      * URI is calculated.
      */
    public static URL getResourceURL(String uri, PageContext pageContext) throws MalformedURLException {
        if ( uri.charAt(0) != '/' ) {
            // calculate a URI relative to the current JSP page
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            String path = request.getServletPath();
            if ( path.length() > 0 ) {
                int index = path.lastIndexOf( '/' );
                if ( index >= 0 ) {
                    String prefix = path.substring(0, index + 1);
                    uri = prefix + uri;
                }
            }
        }
        return pageContext.getServletContext().getResource( uri );
    }
}

/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.core.jhtml2jsp;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import atg.nucleus.DynamoEnv;

/**
 *
 * <p>Does its best to convert a JHTML page to a JSP page using the
 * DSP tag library.  In doing so, it does the following:
 *
 * <ul>
 *
 * <li>&lt;%@ taglib uri="http://www.atg.com/dsp.tld" prefix="dsp" %&gt;
 * is added to the top of the page
 *
 * <li>&lt;dsp:page&gt; is added after the taglib declaration
 *
 * <li>&lt;/dsp:page&gt; is added to the end of the page
 *
 * <li>importbean turns into dsp:importbean
 *
 * <li>oparam turns into dsp:oparam
 *
 * <li>/oparam turns into /dsp:oparam
 *
 * <li>valueof turns into dsp:valueof, value="param:..." turns into
 * param="...", value="bean:..." turns into bean="..."
 *
 * <li>If valueof ... /valueof is empty, then the dsp:valueof turns
 * into empty tag.  Otherwise, /valueof turns into /dsp:valueof
 *
 * <li>For any attribute of any other tag, param:... turns into
 * &lt;valueof param="..."/&gt; and bean:... turns into &lt;valueof
 * bean="..."/&gt;
 *
 * <li>For any attribute of any tag, "...jhtml" turns into "...jsp",
 * or "...jhtml?..." turns into "...jsp?..."
 *
 * <li>java ... /java turns into &lt% ... &gt; and results in warning
 *
 * <li>For any attribute of any tag, `...` turns into &lt;%=...%&gt;
 * and results in a warning
 *
 * <li>a.../a turns into dsp:a.../dsp:a, value="param:..." turns into
 * paramvalue="...", value="bean:..." turns into beanvalue="..."
 *
 * <li>for droplet src=, if it has no enclosed param tags and the src=
 * is not a param: or bean:, then it translates into a &lt;%@ include
 * file=... %&gt;.  Otherwise it turns into a &lt;dsp:include ... &gt;
 * tag, and the enclosed parameters turn into &lt;jsp:param .../&gt;
 * tags.  If the src= or any parameter values have param: or bean:,
 * then getvalueof tags are used to pass values into those params.
 * <b>Note - this is no longer true</b> - I started getting strange
 * JIT errors when using too many levels of @include, so now all
 * droplet src= tags get translated into dsp:include tags.
 *
 * <li>form.../form turns into dsp:form.../dsp:form.  If the form's
 * action is "`request.getRequestURI()`" or
 * "bean:/OriginatingRequest.pathInfo" then it is turned into
 * "&lt;%=request.getServletPath()%&gt;".
 *
 * <li>input turns into dsp:input.../ - "checked" becomes
 * checked="&lt;%=true%&gt;", "unchecked" becomes "&lt;%=false%&gt;",
 * "required" becomes required="&lt;%=true%&gt;".  value="param:..."
 * turns into paramvalue="...", value="bean:..." turns into
 * beanvalue="...".  Note that this is only done if the input tag has
 * a bean= attribute in it - otherwise, it is left alone.
 *
 * <li>textarea.../textarea turns into dsp:textarea.../dsp:textarea -
 * "required" becomes required="&lt;%=true%&gt;".
 *
 * <li>select turns into dsp:select.../ - "required" becomes
 * required="&lt;%=true%&gt;", "multiple" becomes
 * multiple=&lt;%=true%&gt;
 *
 * <li>option turns into dsp:option.../ - "selected" becomes
 * selected="&lt;%=true%&gt;", "unselected" becomes
 * selected="&lt;%=false%&gt;"
 *
 * <li>setvalue turns into dsp:setvalue.../, value="param:..." turns
 * into paramvalue="...", value="bean:..." turns into beanvalue="..."
 *
 * </ul>
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/Jhtml2Jsp.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class Jhtml2Jsp
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/Jhtml2Jsp.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  static int MAX_NESTINGL_LEVELS = 1000;

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Member variables
  //-------------------------------------

  ParsedElement[] mElements;
  String mPrefix;
  Parser mParser;
  static String mCommentsPrefix = " JSP CONVERTER: ";

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public Jhtml2Jsp()
  {
    mParser = new Parser();
  }

  //-------------------------------------
  /**
   * Converts the given jhtml file to a corresponding jsp file.
   */
  public void convertFile(String jhtmlFilename, String jspFilename)
    throws IOException
  {
    String prefix = DynamoEnv.getProperty("atg.jhtml2jsp.dspPrefix");
    if ( prefix == null ) {
      prefix = "dsp";
    }
    prefix = prefix + ":";
    convertFile(jhtmlFilename, jspFilename, prefix);
  }

  public void convertFile(String jhtmlFilename, String jspFilename, String prefix)
    throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(jhtmlFilename));
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(jspFilename)));

    mElements = mParser.parse(in);
    mPrefix = prefix;
    in.close();

    String prefixSansColon;
    int i = mPrefix.indexOf(':');
    prefixSansColon = (i > 0 ? mPrefix.substring(0, i) : mPrefix);

    String uri = DynamoEnv.getProperty("atg.jhtml2jsp.dspUri");
    if (uri == null) {
      uri = "/dsp";
    }
    
    out.println("<%@ taglib uri=\"" + uri + "\" prefix=\"" + prefixSansColon + "\" %>");
    out.println("<" + mPrefix + "page>");
    out.println();

    convert(out);

    out.println ();
    out.println ("</" + mPrefix + "page>");
    out.close ();
  }


  public String convertString(String jhtmlText, String prefix)
    throws IOException
  {
    StringReader in = new StringReader(jhtmlText);
    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);

    mElements = mParser.parse(in);
    mPrefix = prefix;
    convert(out);

    return writer.getBuffer().toString();
  }


  /**
   * Converts the list of parsed jhtml elements in mElements and
   * writes the corresponding jsp to the given PrintWriter, using
   * mPrefix as the dsp taglib prefix.
   */
  private void convert(PrintWriter pOut)
  {
    boolean [] wrapOparamWithGetvalueof = new boolean[MAX_NESTINGL_LEVELS];
    int oparamNestLevel = 0;
    String closingString = "";

    // Walk through each element
    for (int i = 0; i < mElements.length; i++) {
      ParsedElement pelem = mElements [i];

      // Make sure this hasn't been marked for no rendering
      if (pelem.getShouldRender ()) {

        // Handle text
        if (pelem instanceof TextParsedElement) {
          TextParsedElement elem = (TextParsedElement) pelem;
          pOut.print (elem.getText ());
        }

        // Handle tags
        else if (pelem instanceof TagParsedElement) {
          TagParsedElement elem = (TagParsedElement) pelem;
          String tagName = elem.getTagName ();
          boolean endTag = elem.getEndTag ();
          Map attrs = elem.getAttributes ();

          // First replace all .jhtml with .jsp
          boolean jhtmlSubstituted = substituteJhtml (attrs);

          // Then replace all backquotes
          boolean backquoteSubstituted = substituteBackquote (attrs);

          // replace known attributes with no value with 'true'
          substituteAttributes (attrs);

          // Handle java ... /java case
          if ("java".equals (tagName)) {
            if (!endTag) {
               String type = (String) attrs.get ("type");
               if (type != null && type.equalsIgnoreCase("print")) 
                 pOut.print ("<%=");
               else if (type != null && type.equalsIgnoreCase("import")) {
                 pOut.print ("<%@ page import=\"");
                 closingString = "\"";
               }
               else {
                 printComments(pOut, "ERROR: can not convert java code that follows");
                 pOut.print ("<%");
               }
            }
            else {
              pOut.print (closingString+"%>");
              closingString = "";
              System.out.println ("Warning - contains java code: " +
                                  mElements [i - 1]);
            }
          }

          // Handle oparam
          else if ("oparam".equals (tagName)) {
            if (oparamNestLevel < 0 ||  
                oparamNestLevel > MAX_NESTINGL_LEVELS-2) {
              printComments(pOut, "ERROR: incorrect number of open and closing <oparam> tags");
              System.out.println ("Warning: incorrect number of open and closing <oparam> tags");
              oparamNestLevel = 0;
            }
            else if (endTag) {
              if (--oparamNestLevel > -1 &&
                  wrapOparamWithGetvalueof[oparamNestLevel]) {
                pOut.print ("</" + mPrefix + "oparam>\n");
                pOut.print ("</" + mPrefix + "getvalueof>");
              }
              else if (oparamNestLevel < 0) {
                printComments(pOut, "ERROR: incorrect number of open and closing <oparam> tags");
                System.out.println ("Warning: incorrect number of open and closing <oparam> tags");
                oparamNestLevel = 0;
              }
              else
                pOut.print ("</" + mPrefix + "oparam>");
            }
            else {
              String nameString = (String) attrs.get ("name");

              // See if the name has a param: or bean:
              // If so, put the tag inside <dsp:getvalueof>
              if (nameString != null &&
                  (nameString.startsWith ("param:") ||
                   nameString.startsWith ("bean:"))) {

                attrs.remove("name");
                String bp;
                if (nameString.startsWith ("param:"))
                  bp = "param";
                else
                  bp = "bean";

                String nameVal = nameString.substring (bp.length ()+1);
                pOut.print ("<" + mPrefix + "getvalueof id=\"nameval"+oparamNestLevel+"\" "+bp+"=\"" +
                            nameVal +
                            "\" idtype=\"java.lang.String\">\n");
                // Output the oparam tag
                pOut.print ("<" + mPrefix + "oparam name=\"" +
                            "<%=nameval"+oparamNestLevel+"%>" +
                            "\""+elem.getAttributesString ()+">");
                if (elem.isSelfClosing()) {
                  pOut.print ("</" + mPrefix + "oparam>\n");
                  pOut.print ("</" + mPrefix + "getvalueof>");
                }
                else {
                    wrapOparamWithGetvalueof[oparamNestLevel++] = true;
                }
              }
              else {
                pOut.print ("<" + mPrefix + "oparam" +
                            elem.getAttributesString () +
                            (elem.isSelfClosing() ? "/" : "") +
                            ">");
                wrapOparamWithGetvalueof[oparamNestLevel++] = false;
              }
            }
          }
          // Handle param
          else if ("param".equals (tagName)) {
            substituteParamBeanValue (attrs, false);
            pOut.print ("<" + mPrefix + "param" +
                        elem.getAttributesString () +
                        "/>");
          }

          // Handle importbean
          else if ("importbean".equals (tagName)) {
            pOut.print ("<" + mPrefix + "importbean" +
                        elem.getAttributesString () +
                        "/>");
          }

          // Handle droplet
          else if ("droplet".equals (tagName)) {
            if (endTag) {
              pOut.print ("</" + mPrefix + "droplet>");
            }
            else {
              // See if it's src or bean or name
              String beanString = (String) attrs.get ("bean");
              String nameString = (String) attrs.get ("name");
              if (beanString == null &&
                  nameString != null) {
                beanString = nameString;
              }
              String srcString = (String) attrs.get ("src");
              if (srcString != null) {
                TagParsedElement [] paramTags = new TagParsedElement [0];
                if (!elem.isSelfClosing())
                  paramTags = getParamTags (i + 1, pOut);

                // Note - this used to convert droplet src= into a
                // @include, but this was causing some JIT problems,
                // so now all droplet src= get converted to jsp:includ
                if (false &&  // added to always go with dsp:include
                    paramTags.length == 0 &&
                    !srcString.startsWith ("param:") &&
                    !srcString.startsWith ("bean:")) {
                  pOut.print ("<%@ include file=\"" +
                              srcString +
                              "\" %>");
                }
                else {
                  int numGetvalueofs = 0;

                  // See if the src has a param: or bean:
                  boolean srcStringReplaced = false;
                  if (srcString.startsWith ("param:")) {
                    String srcVal = srcString.substring ("param:".length ());
                    pOut.print ("<" + mPrefix + "getvalueof id=\"srcval\" param=\"" +
                                srcVal +
                                "\">");
                    numGetvalueofs++;
                    srcStringReplaced = true;
                  }
                  else if (srcString.startsWith ("bean:")) {
                    String srcVal = srcString.substring ("bean:".length ());
                    pOut.print ("<" + mPrefix + "getvalueof id=\"srcval\" bean=\"" +
                                srcVal +
                                "\">");
                    numGetvalueofs++;
                    srcStringReplaced = true;
                  }

                  // See if any params have param: or bean:
                  for (int j = 0; j < paramTags.length; j++) {
                    TagParsedElement paramTag = paramTags [j];
                    Map paramAttrs = paramTag.getAttributes ();
                    String paramVal = (String) paramAttrs.get ("value");
                    if (paramVal != null) {
                      if (paramVal.startsWith ("param:")) {
                        String pval = paramVal.substring ("param:".length ());
                        String pvalname = "pval" + numGetvalueofs;
                        pOut.print ("<" + mPrefix + "getvalueof id=\"" +
                                    pvalname +
                                    "\" param=\"" +
                                    pval +
                                    "\">");
                        paramAttrs.put ("value", "<%=" + pvalname + "%>");
                        numGetvalueofs++;
                      }
                      else if (paramVal.startsWith ("bean:")) {
                        String pval = paramVal.substring ("bean:".length ());
                        String pvalname = "pval" + numGetvalueofs;
                        pOut.print ("<" + mPrefix + "getvalueof id=\"" +
                                    pvalname +
                                    "\" bean=\"" +
                                    pval +
                                    "\">");
                        paramAttrs.put ("value", "<%=" + pvalname + "%>");
                        numGetvalueofs++;
                      }
                    }
                  }

                  // Output the include tag
                  pOut.print ("<" + mPrefix + "include page=\"" +
                              (srcStringReplaced ? "<%=(String)srcval%>" : srcString) +
                              "\">");

                  // Output the params
                  for (int j = 0; j < paramTags.length; j++) {
                    TagParsedElement paramTag = paramTags [j];
                    Map paramAttrs = paramTag.getAttributes ();

                    substituteBackquote (paramAttrs);

                    String name = (String) paramAttrs.get ("name");
                    String value = (String) paramAttrs.get ("value");

                    StringBuffer pbuf = new StringBuffer ();
                    pbuf.append ("<" + mPrefix + "param");
                    if (name != null) {
                      pbuf.append (" name=\"" + name + "\"");
                    }
                    if (value != null) {
                      if (value.indexOf('\"') != -1)
                        pbuf.append (" value='" + value + "'");
                      else
                        pbuf.append (" value=\"" + value + "\"");
                    }
                    pbuf.append ("/>");
                    pOut.print (pbuf);
                  }

                  pOut.print ("</" + mPrefix + "include>");

                  for (int j = 0; j < numGetvalueofs; j++) {
                    pOut.print ("</" + mPrefix + "getvalueof>");
                  }
                }
              }
              else if (beanString != null) {
                attrs.remove ("bean");
                attrs.put ("name", beanString);
                pOut.print ("<" + mPrefix + "droplet" +
                            elem.getAttributesString () +
                            ">");
              }
            }
          }

          // Handle valueof
          else if ("valueof".equals (tagName)) {
            if (endTag) {
              pOut.print ("</" + mPrefix + "valueof>");
            }
            else {
              substituteParamBeanValue (attrs, false);

              // See if the valueof contains only whitespace
              boolean containsOnlyWhitespace = false;
              if (valueofContainsOnlyWhitespace (i + 1)) {
                containsOnlyWhitespace = true;
              }

              pOut.print ("<" + mPrefix + "valueof" +
                          elem.getAttributesString () +
                          (containsOnlyWhitespace || elem.isSelfClosing()? 
                           "/" : "") + ">");
            }
          }

          // Handle a
          else if ("a".equals (tagName)) {
            if (endTag) {
              pOut.print ("</" + mPrefix + "a>");
              if (elem.getWrapped())
                pOut.print ("</" + mPrefix + "getvalueof>");
            }
            else {
              substituteParamBeanValue (attrs, true);
              if (!wrapGetValueOf (i, pOut)) {
                pOut.print ("<" + mPrefix + "a" +
                            elem.getAttributesString () +
                            ">");
              }
            }
          }

          // Handle form
          else if ("form".equals (tagName)) {
            if (endTag) {
              pOut.print ("</" + mPrefix + "form>");
              if (elem.getWrapped())
                pOut.print ("</" + mPrefix + "getvalueof>");
            }
            else {
              // get getRequestURI into getServletPath
              String actionStr = (String) attrs.get ("action");
              if ("<%=request.getRequestURI()%>".equals (actionStr) ||
                  "bean:/OriginatingRequest.pathInfo".equals (actionStr)) {
                attrs.put ("action", "<%=request.getServletPath()%>");
              }
              if (!wrapGetValueOf (i, pOut)) {
                pOut.print ("<" + mPrefix + "form" +
                            elem.getAttributesString () +
                            ">");
              }
            }
          }

          // Handle input
          else if ("input".equals (tagName)) {
            substituteParamBeanValue (attrs, true);
            substituteCheckedRequiredMultiple (attrs);
            if (!wrapGetValueOf (i, pOut)) {
              pOut.print ("<" + mPrefix + "input" +
                          elem.getAttributesString () +
                          "/>");
            }
          }

          // Handle textarea
          else if ("textarea".equals (tagName)) {
            if (endTag) {
              pOut.print ("</" + mPrefix + "textarea>");
            }
            else {
              substituteCheckedRequiredMultiple (attrs);
              pOut.print ("<" + mPrefix + "textarea" +
                          elem.getAttributesString () +
                          ">");
            }
          }

          // Handle select
          else if ("select".equals (tagName)) {
            if (endTag) {
              pOut.print ("</" + mPrefix + "select>");
              if (elem.getWrapped())
                pOut.print ("</" + mPrefix + "getvalueof>");
            }
            else {
              substituteCheckedRequiredMultiple (attrs);
              if (!wrapGetValueOf (i, pOut)) {
                pOut.print ("<" + mPrefix + "select" +
                            elem.getAttributesString () +
                            ">");
              }
            }
          }

          // Handle option
          else if ("option".equals (tagName)) {
            if (endTag) {
                //pOut.print ("</" + mPrefix + "option>");
              if (elem.getWrapped())
                pOut.print ("</" + mPrefix + "getvalueof>");
            }
            else {
              substituteParamBeanValue (attrs, true);
              substituteCheckedRequiredMultiple (attrs);
              if (!wrapGetValueOf (i, pOut)) {
                pOut.print ("<" + mPrefix + "option" +
                            elem.getAttributesString () + "/>");
                // (elem.isSelfClosing() ? "/>" : ">"));
              }
            }
          }

          // Handle setvalue
          else if ("setvalue".equals (tagName)) {
            substituteParamBeanValue (attrs, true);
            if (!attrs.containsKey("value") &&
                !attrs.containsKey("beanvalue") &&
                !attrs.containsKey("paramvalue"))  // no value
                attrs.put("value", "<%=null%>");

            pOut.print ("<" + mPrefix + "setvalue" +
                        elem.getAttributesString () +
                        "/>");
          }
          // Handle frame
          else if ("frame".equals (tagName)) {
            if (!wrapGetValueOf (i, pOut)) {
              pOut.print ("<" + mPrefix + "frame" +
                          elem.getAttributesString () +
                          "/>");
            }
          }
          // Handle iframe
          else if ("iframe".equals (tagName)) {
            if (endTag) {
              pOut.print ("</" + mPrefix + "iframe>");
            }
            else {
              pOut.print ("<" + mPrefix + "iframe" +
                          elem.getAttributesString () +
                          ">");
            }
          }
          // Handle img
          else if ("img".equals (tagName)) {
            if (!wrapGetValueOf (i, pOut)) {
                pOut.print ("<" + mPrefix + "img" +
                            elem.getAttributesString () +
                            "/>");
            }
          }

          // Handle link
          else if ("link".equals (tagName)) {
            if (!wrapGetValueOf (i, pOut)) {
              pOut.print ("<" + mPrefix + "link" +
                          elem.getAttributesString () +
                          "/>");
            }
          }
          // Handle declareparam: do nothing
          else if ("declareparam".equals (tagName)) {
            pOut.print("<" + mPrefix + "declareparam" + 
                       elem.getAttributesString() +
                       "/>");
          }
          // Handle any other tag
          else {
            boolean converted = convertParamBeanToValueof (attrs);

            if (jhtmlSubstituted ||
                backquoteSubstituted ||
                converted) {
              pOut.print ("<" +
                          tagName +
                          elem.getAttributesString () +
                          ">");
            }
            else {
              pOut.print ("<");
              pOut.print (elem.getText ());
              if (elem.isSelfClosing())
                pOut.print ("/");
              pOut.print (">");
            }
          }
        }
      }
    }

  }

  //-------------------------------------
  /**
   *
   * Looks in every value and converts any ending with .jhtml to
   * ending with .jsp.  If any were changed, returns true.
   **/
  boolean substituteJhtml (Map pAttributes)
  {
    boolean ret = false;

    Set keySet = pAttributes.keySet ();
    Iterator iter = keySet.iterator ();
    while (iter.hasNext ()) {
      String key = (String) iter.next ();
      String value = (String) pAttributes.get (key);
      if (value != null) {
        int ix = value.indexOf (".jhtml");
        if (value.endsWith (".jhtml")) {
          value =
            value.substring (0, value.length () - ".jhtml".length ()) + ".jsp";
          pAttributes.put (key, value);
          ret = true;
        }
        else if (ix >= 0 &&
                 value.charAt (ix + ".jhtml".length ()) == '?') {
          value =
            value.substring (0, ix) +
            ".jsp" +
            value.substring (ix + ".jhtml".length ());
          pAttributes.put (key, value);
          ret = true;
        }
      }
    }

    return ret;
  }

  //-------------------------------------
  /**
   *
   * Looks in every value and converts any of the form `...` to
   * &lt;%=...%&gt; If any were changed, returns true.
   **/
  boolean substituteBackquote (Map pAttributes)
  {
    boolean ret = false;

    Set keySet = pAttributes.keySet ();
    Iterator iter = keySet.iterator ();
    while (iter.hasNext ()) {
      String key = (String) iter.next ();
      String value = (String) pAttributes.get (key);

      if (value != null &&
          value.length () >= 2) {
        int startBackquote = value.indexOf ('`');
        int endBackquote   = value.lastIndexOf ('`');
        if (startBackquote != -1 && endBackquote != -1
            && startBackquote != endBackquote) {
          System.out.println ("Warning - contains java backquote code: " +
                            value);
          if (startBackquote == 0 && endBackquote == value.length()-1) {
            // exclude enclosing backquotes
            value = "<%=" +
                value.substring (startBackquote+1, endBackquote) +
                "%>";
          }
          else {
            // split string at backquotes
            value = "<%=" +
                "\"" +
                value.substring(0,startBackquote) +
                "\" + " +
                value.substring (startBackquote+1, endBackquote) +
                ((endBackquote == value.length () - 1) ?
                 "" : ("+ \"" + value.substring (endBackquote+1, value.length ()) +
                 "\"")) +
                 "%>";
          }
          pAttributes.put (key, value);
          ret = true;
        }
      }
    }

    return ret;
  }

  //-------------------------------------
  /**
   *
   * Looks in the value attribute - if it starts with param: or bean:,
   * it becomes a bean or param attribute.  Returns true if it did
   * such a substitution.  If pFullValue is true, then it turns into
   * "beanvalue" or "paramvalue".
   **/
  boolean substituteParamBeanValue (Map pAttributes,
                                    boolean pFullValue)
  {
    String valueString = (String) pAttributes.get ("value");
    if (valueString != null) {
      if (valueString.startsWith ("param:")) {
        pAttributes.remove ("value");
        pAttributes.put
          ((pFullValue ? "paramvalue" : "param"),
           valueString.substring ("param:".length ()));
        return true;
      }
      else if (valueString.startsWith ("bean:")) {
        pAttributes.remove ("value");
        pAttributes.put
          ((pFullValue ? "beanvalue" : "bean"),
           valueString.substring ("bean:".length ()));
        return true;
      }
      else if (valueString.startsWith ("property:")) {
        pAttributes.remove ("value");
        pAttributes.put
          ((pFullValue ? "beanvalue" : "bean"),
           valueString.substring ("property:".length ()));
        return true;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }

  //-------------------------------------
  /**
   *
   * Substitutes &lt;%=true|false%&gt; for checked, unchecked,
   * selected, unselected, required, multiple.
   **/
  void substituteCheckedRequiredMultiple (Map pAttributes)
  {
    if (pAttributes.containsKey ("checked")) {
      pAttributes.remove ("checked");
      pAttributes.put ("checked", "<%=true%>");
    }
    if (pAttributes.containsKey ("unchecked")) {
      pAttributes.remove ("unchecked");
      pAttributes.put ("checked", "<%=false%>");
    }
    if (pAttributes.containsKey ("selected")) {
      pAttributes.remove ("selected");
      pAttributes.put ("selected", "<%=true%>");
    }
    if (pAttributes.containsKey ("unselected")) {
      pAttributes.remove ("unselected");
      pAttributes.put ("selected", "<%=false%>");
    }
    if (pAttributes.containsKey ("required")) {
      pAttributes.remove ("required");
      pAttributes.put ("required", "<%=true%>");
    }
    if (pAttributes.containsKey ("multiple")) {
      pAttributes.remove ("multiple");
      pAttributes.put ("multiple", "<%=true%>");
    }
  }

  //-------------------------------------
  /**
   *
   * Checks to see if the specified index contains a whitespace-only
   * text element, and the next index contains a /valueof
   **/
  boolean valueofContainsOnlyWhitespace (int pIndex)
  {
    if (isEndValueofTag (pIndex)) {
      mElements [pIndex].setShouldRender (false);
      return true;
    }
    else if (isWhitespaceOnlyTag (pIndex) &&
             isEndValueofTag (pIndex + 1)) {
      mElements [pIndex].setShouldRender (false);
      mElements [pIndex + 1].setShouldRender (false);
      return true;
    }
    else {
      return false;
    }
  }

  //-------------------------------------
  /**
   *
   * Returns true if the tag at the specified index is a text tag
   * containing only whitespace.
   **/
  boolean isWhitespaceOnlyTag (int pIndex)
  {
    if (pIndex >= mElements.length) return false;
    ParsedElement elem = mElements [pIndex];
    if (elem instanceof TextParsedElement) {
      TextParsedElement e = (TextParsedElement) elem;
      String text = e.getText ();
      for (int i = 0; i < text.length (); i++) {
        if (!Character.isWhitespace (text.charAt (i))) return false;
      }
      return true;
    }
    else {
      return false;
    }
  }

  //-------------------------------------
  /**
   *
   * Returns true if the tag at the specified index is an ending
   * valueof tag
   **/
  boolean isEndValueofTag (int pIndex)
  {
    if (pIndex >= mElements.length) return false;
    ParsedElement elem = mElements [pIndex];
    if (elem instanceof TagParsedElement) {
      TagParsedElement e = (TagParsedElement) elem;
      return
        "valueof".equals (e.getTagName ()) &&
        e.getEndTag ();
    }
    else {
      return false;
    }
  }

  //-------------------------------------
  /**
   *
   * Walks through all the attributes.  Any of the form "param:..." or
   * "bean:..." are converted to the equivalent valueof tag.  Returns
   * true if any were converted.
   **/
  boolean convertParamBeanToValueof (Map pAttributes)
  {
    boolean ret = false;

    Set keySet = pAttributes.keySet ();
    Iterator iter = keySet.iterator ();
    while (iter.hasNext ()) {
      String key = (String) iter.next ();
      String value = (String) pAttributes.get (key);

      if (value != null) {
        if (value.startsWith ("param:")) {
          value = value.substring ("param:".length ());
          String [] vals = splitValueAtWhitespace (value);
          if (vals.length == 1) {
            value =
              "<" + mPrefix + "valueof param=\"" +
              vals [0] +
              "\"/>";
            ret = true;
            pAttributes.put (key, value);
          }
          else if (vals.length >= 2) {
            value =
              "<" + mPrefix + "valueof param=\"" +
              vals [0] +
              "\">" +
              vals [1] +
              "</" + mPrefix + "valueof>";
            ret = true;
            pAttributes.put (key, value);
          }
        }

        else if (value.startsWith ("bean:")) {
          value = value.substring ("bean:".length ());
          String [] vals = splitValueAtWhitespace (value);
          if (vals.length == 1) {
            value =
              "<" + mPrefix + "valueof bean=\"" +
              vals [0] +
              "\"/>";
            ret = true;
            pAttributes.put (key, value);
          }
          else if (vals.length >= 2) {
            value =
              "<" + mPrefix + "valueof bean=\"" +
              vals [0] +
              "\">" +
              vals [1] +
              "</" + mPrefix + "valueof>";
            ret = true;
            pAttributes.put (key, value);
          }
        }
      }
    }

    return ret;
  }

  //-------------------------------------
  /**
   *
   * Splits the specified value up by whitespace
   **/
  String [] splitValueAtWhitespace (String pValue)
  {
    List ret = new ArrayList ();

    StringBuffer buf = new StringBuffer ();
    int state = 0;
    for (int i = 0; i < pValue.length (); i++) {
      char ch = pValue.charAt (i);

      switch (state) {

      case 0: // in chars, looking for whitespace
        if (Character.isWhitespace (ch)) {
          ret.add (buf.toString ());
          buf = new StringBuffer ();
          state = 1;
        }
        else {
          buf.append (ch);
        }
        break;

      case 1: // in whitespace, looking for chars
        if (!Character.isWhitespace (ch)) {
          buf.append (ch);
          state = 0;
        }
        break;
      }
    }

    if (state == 0) {
      ret.add (buf.toString ());
    }

    return (String []) ret.toArray (new String [ret.size ()]);
  }

  //-------------------------------------
  /**
   *
   * Searches for all the param tags starting with the given index and
   * ending with the next /droplet tag.  Marks all of those tags as
   * "should not render".
   **/
  TagParsedElement [] getParamTags (int pIndex, PrintWriter pOut)
  {
    List ret = new ArrayList ();

    for (; pIndex < mElements.length; pIndex++) {
      ParsedElement elem = mElements [pIndex];
      elem.setShouldRender (false);

      if (elem instanceof TagParsedElement) {
        TagParsedElement e = (TagParsedElement) elem;

        if ("droplet".equals (e.getTagName ())) {
          if (e.getEndTag ()) {
            break;
          }
          else {
            printComments(pOut, "ERROR: cannot convert nested includes (drople src)");
            System.out.println ("Warning droplet src contains nested droplet");
          }
        }
        else if ("oparam".equals (e.getTagName ())) {
          printComments(pOut, "WARNING: check droplet src containing nested oparam");
          System.out.println ("Warning droplet src contains nested oparam");
        }
        else if ("param".equals (e.getTagName ()) &&
                 !e.getEndTag ()) {
          ret.add (e);
        }
      }
    }

    return (TagParsedElement [])
      ret.toArray (new TagParsedElement [ret.size ()]);
  }

  //-------------------------------------
  /**
   *
   * add ="true" for nosave, animateonselect, noresize
   **/
  void substituteAttributes (Map pAttributes)
  {
    if (pAttributes.containsKey ("nosave")) {
      pAttributes.remove ("nosave");
      pAttributes.put ("nosave", "true");
    }
    if (pAttributes.containsKey ("animateonselect")) {
      pAttributes.remove ("animateonselect");
      pAttributes.put ("animateonselect", "true");
    }
    if (pAttributes.containsKey ("noresize")) {
      pAttributes.remove ("noresize");
      pAttributes.put ("noresize", "true");
    }
    if (pAttributes.containsKey ("valueishtml")) {
      pAttributes.remove ("valueishtml");
      pAttributes.put ("valueishtml", "<%=true%>");
    }
    if (pAttributes.containsKey ("required")) {
      pAttributes.remove ("required");
      pAttributes.put ("required", "<%=true%>");
    }
    if (pAttributes.containsKey ("nullable")) {
      pAttributes.remove ("nullable");
      pAttributes.put ("nullable", "<%=true%>");
    }
    if (pAttributes.containsKey ("reverse")) {
      pAttributes.remove ("reverse");
      pAttributes.put ("reverse", "<%=true%>");
    }
    if (pAttributes.containsKey ("currencyConversion")) {
      pAttributes.remove ("currencyConversion");
      pAttributes.put ("converter", "currencyConversion");
    }
    if (pAttributes.containsKey ("currencyconversion")) {
      pAttributes.remove ("currencyconversion");
      pAttributes.put ("converter", "currencyConversion");
    }
    if (pAttributes.containsKey ("currency")) {
      pAttributes.remove ("currency");
      pAttributes.put ("converter", "currency");
    }
    if (pAttributes.containsKey ("creditcard")) {
      pAttributes.remove ("creditcard");
      pAttributes.put ("converter", "creditcard");
    }
    if (pAttributes.containsKey ("euro")) {
      pAttributes.remove ("euro");
      pAttributes.put ("converter", "euro");
      pAttributes.put ("symbol", "&euro");
    }
    if (pAttributes.containsKey ("class")) {
      pAttributes.put ("iclass", pAttributes.get ("class"));
      pAttributes.remove ("class");
    }
    if (pAttributes.containsKey ("priority")) {
      String s = "<%=" +pAttributes.get ("priority") + "%>";
      pAttributes.remove ("priority");
      pAttributes.put ("priority", s);
    }
  }

  //-------------------------------------
  /**
   * Searches the closing tag of the given tag
   * retunrs index of closing tag or 0 if not found
   **/
  int getEndTag (int pIndex, String pTag, boolean pAllowNested)
  {
    int ret = 0;
    int nestLevel = 0;
    for (; pIndex < mElements.length; pIndex++) {
      ParsedElement elem = mElements [pIndex];
      if (elem instanceof TagParsedElement) {
        TagParsedElement e = (TagParsedElement) elem;

        if (pTag.equals (e.getTagName ())) {
          if (e.getEndTag ()) {
            if (nestLevel == 0) {
              ret = pIndex;
              break;
            }
            nestLevel--;
          }
          else {
            if (!pAllowNested) {
              ret = 0;
              break;
            }
            else if (!elem.isSelfClosing())
              nestLevel++;
          }
        }
      }
    }
    return (ret);
  }

  //-------------------------------------
  /**
   *
   * Walks through all the attributes.  Any of the form "param:..." or
   * "bean:..." are converted to the equivalent valueof tag.
   * If those found wrap tag with <dsp:getvalueof> tag
   **/
  //boolean wrapGetValueOf (int pElemIndex, PrintStream pOut)
  boolean wrapGetValueOf (int pElemIndex, PrintWriter pOut)
  {
    ParsedElement pelem = mElements [pElemIndex];
    TagParsedElement elem = null;
    if (pelem instanceof TagParsedElement)
       elem = (TagParsedElement) pelem;
    else
        return false;

    boolean wrap    = false;
    boolean wrapEnd = false;
    String tagName = elem.getTagName ();
    Map attrs      = elem.getAttributes ();
    String bp = null;
    String nameVal = null;

    Set keySet = attrs.keySet ();
    Iterator iter = keySet.iterator ();
    while (iter.hasNext ()) {
      String key = (String) iter.next ();
      String value = (String) attrs.get (key);

      // See if the value has a param: or bean:
      // If so, put the tag inside <dsp:getvalueof>
      if (value != null &&
          (value.startsWith ("param:") ||
           value.startsWith ("bean:"))) {

          if (value.startsWith ("param:"))
              bp = "param";
          else
              bp = "bean";

          nameVal = value.substring (bp.length ()+1);

          attrs.remove(key);
          attrs.put(key, "<%="+tagName+pElemIndex+"%>");

          wrap = true;

          int endIndex = getEndTag (pElemIndex+1, tagName, false);
          if (endIndex != 0) {
            ParsedElement pendElem = mElements [endIndex];
            TagParsedElement endElem  = (TagParsedElement) pendElem;
            endElem.setWrapped(true);
            wrapEnd = true;
          }
          break;
      }
    }
    if (wrap) {
      // Output the wrap
      pOut.print ("<" + mPrefix + "getvalueof id=\""+tagName+pElemIndex+"\" "+bp+"=\"" +
                  nameVal +
                  "\" idtype=\"java.lang.String\">\n");
      // Output the tag
      pOut.print ("<" + mPrefix + "" + tagName +
                  elem.getAttributesString());
      if (wrapEnd)
        pOut.print (">");
      else {
        pOut.print ("/>\n");
        pOut.print ("</" + mPrefix + "getvalueof>");
      }
    }
    return wrap;
  }

  //-------------------------------------

  private void printComments(PrintWriter pOut, String pComments)
  {  
     pOut.print ("<%--" + mCommentsPrefix + pComments + "--%>\n");
  }


  //-------------------------------------
  
  /** Convert the JHTML pages in pFromDir to jsp pages in
   * pToDir.
   *
   * @param pFromDir The source directory.
   * @param pToDir The target directory.
   * @param boolean pCopyOtherFiles whether other files should be
   * copied.
   */
  static void convertDir(File pFromDir, File pToDir,
                         boolean pCopyOtherFiles) throws IOException {
    
    if (!pToDir.isDirectory()) {
      System.err.println("Target directory '" +
                         pToDir.getAbsolutePath() + "' does not exist.");
    }
    else {
      String strFromPrefix = pFromDir.getAbsolutePath();
      if (!strFromPrefix.endsWith(File.separator)) {
        strFromPrefix = strFromPrefix + File.separator;
      }
      int lenFromPrefix = strFromPrefix.length();        
        
      // both source and target exist
      File[] files = pFromDir.listFiles(new JhtmlFileFilter());
      for (int i = 0; i < files.length; i++) {
        File fileFromCur = files[i];
        String strFromPath = fileFromCur.getAbsolutePath();
        if (!strFromPath.startsWith(strFromPrefix)) {
          System.err.println("--Error: source path does not begin with '"
                             + strFromPrefix + "': '" + strFromPath + "'");
        }
        else {
          String strSubpath = strFromPath.substring(lenFromPrefix);
          if (fileFromCur.isDirectory()) {
            File fileDirNew = new File(pToDir, strSubpath);
              
            if (!fileDirNew.exists()) {
              if (fileDirNew.mkdir()) {
                System.out.println("--Created directory '" + fileDirNew + "'--");
              }
              else {
                System.err.println("--Failed to create directory '" + fileDirNew + "'--"); 
              }
            }

            convertDir(fileFromCur, fileDirNew, pCopyOtherFiles);
          }
          else if (strSubpath.endsWith(".jhtml")) {
            System.out.println("--Converting '" + fileFromCur + "'--"); 
            // chop off .jhtml
            String strTo = strSubpath.substring(0,
                                                strSubpath.length() - 6);
            File fileToCur = new File(pToDir, strTo + ".jsp");
              
            Jhtml2Jsp jhtml2jsp = new Jhtml2Jsp();
            jhtml2jsp.convertFile(fileFromCur.getAbsolutePath(),
                                  fileToCur.getAbsolutePath());
          }
          else {
            if (pCopyOtherFiles) {
              File fileToCur = new File(pToDir, strSubpath);
              System.out.println("--Copying '" + fileFromCur + "'--");
              atg.core.io.FileUtils.copyFile(fileToCur.getAbsolutePath(),
                                             fileFromCur.getAbsolutePath());
            }
          }
        }
      }
    }
  }

  //-------------------------------------
  // Main method
  //-------------------------------------
  public static void main (String [] pArgs)
    throws IOException
  {
    if (pArgs.length != 2) {
      usage ();
      System.exit (1);
    }


    if (new File(pArgs[0]).isDirectory()) {
      File fileFromDir = new File(pArgs[0]);
      File fileToDir = new File(pArgs[1]);
      boolean copyOtherFiles = true;

      try {
        fileFromDir = new File(fileFromDir.getCanonicalPath());
      }
      catch (IOException e) {
        System.err.println("Error: could not get canonical path for '" +
          fileFromDir + "' skipping copying of other files.");
        copyOtherFiles = false;
      }
        
      try {
        fileToDir = new File(fileToDir.getCanonicalPath());
      }
      catch (IOException e) {
        System.err.println("Error: could not get canonical path for '" +
                           fileToDir + "' skipping copying of other files.");
        copyOtherFiles = false;
      }

      if (!fileToDir.isDirectory()) {
        System.err.println("Error: target directory '" +
                           fileToDir +
                           "' does not exist, or is not a directory.");
        System.exit(1);
      }

      // converting in the same directory.
      if (fileToDir.equals(fileFromDir))
        copyOtherFiles = false;
      
      convertDir(fileFromDir, fileToDir, copyOtherFiles);
    }
    else {
      Jhtml2Jsp jhtml2jsp = new Jhtml2Jsp();
      jhtml2jsp.convertFile(pArgs[0], pArgs[1]);
    }
  }
  

  //-------------------------------------
  static void usage ()
  {
    System.err.println ("");    
    System.err.println ("Usage:");
    System.err.println ("");    
    System.err.println ("  atg.core.jhtml2jsp.Jhtml2Jsp {input JHTML file} {output JSP file}");
    System.err.println ("    Single file conversion.");
    System.err.println ("");
    
    System.err.println ("  atg.core.jhtml2jsp.Jhtml2Jsp {input JHTML dir} {output JSP dir}");
    System.err.println ("    Directory batch conversion (only converts files whose names end with");
    System.err.println ("    '.jhtml' and does not remove source .jhtml files)");
    System.err.println ("");
  }

  //-------------------------------------

  static class JhtmlFileFilter implements FileFilter {
    public boolean accept(File pFile) {
      if (pFile.isDirectory())
        return true;
      if (pFile.getName().endsWith(".jhtml"))
        return true;
      return false;
    }
  }

}

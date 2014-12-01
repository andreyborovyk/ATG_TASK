/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.service.email;

import java.io.*;
import java.text.MessageFormat;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.activation.*;

import atg.core.util.ResourceUtils;

/** 
 * An implementation of <code>DataContentHandler</code> that 
 * handles content of type "text/html."  The Object representing
 * the content is expected to be either String or InputStream.
 * 
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/HtmlDataContentHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

// Note: the only method in this class that appears to be 
// called by the JavaMail SMTP implementation is writeTo.

public class HtmlDataContentHandler implements DataContentHandler {
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/HtmlDataContentHandler.java#2 $$Change: 651448 $";

  /** Resource bundle **/
  static final String MY_RESOURCE_NAME = "atg.service.ServiceResources";

  private static java.util.ResourceBundle sResourceBundle = 
  java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /** "text/html" content type **/
  static String TEXT_HTML = "text/html";

  /** "charset=" string **/
  static String CHARSET_STRING = "charset=";

  /** DataFlavor objects associated with this DataContentHandler **/
  static DataFlavor[] sDataFlavors;

  static {
    sDataFlavors = new DataFlavor[2];
    // String representation class
    sDataFlavors[0] = 
      new ActivationDataFlavor(String.class, TEXT_HTML, "HTML String");
    // InputStream representation class
    sDataFlavors[1] = new DataFlavor(TEXT_HTML, "HTML InputStream");
  }

  //-------------------------------------
  // DataContentHandler implementation
  //-------------------------------------

  //-------------------------------------
  /**
   * Returns an array of DataFlavor objects indicating the flavors 
   * the data can be provided in.  The array is ordered according to 
   * preference for providing the data (from most richly descriptive 
   * to least descriptive).
   **/
  public DataFlavor[] getTransferDataFlavors() {
    return sDataFlavors;
  }

  //-------------------------------------
  /**
   * Returns an object which represents the data to be transferred. 
   * The class of the object returned is defined by the representation 
   * class of the flavor.
   * @param pDataFlavor DataFlavor representing the requested type
   * @param pDataSource DataSource representing data to be converted
   * @return the constructed Object
   * @exception UnsupportedFlavorException if the DataFlavor passed
   * in is not one of the supported data flavors
   **/
  public Object getTransferData(DataFlavor pDataFlavor, 
				DataSource pDataSource)
       throws UnsupportedFlavorException, IOException 
  {
    if (!pDataFlavor.getMimeType().equals(TEXT_HTML))
      throw new UnsupportedFlavorException(pDataFlavor);

    Class flavorClass = pDataFlavor.getRepresentationClass();
    if (flavorClass.equals(String.class))
      return getContent(pDataSource);
    else if (flavorClass.equals(InputStream.class))
      return pDataSource.getInputStream();
    else 
      throw new UnsupportedFlavorException(pDataFlavor);
  }

  //-------------------------------------
  /**
   * Return an object representing the data in its most preferred 
   * form - i.e., as a String.
   **/
  public Object getContent(DataSource pDataSource) throws IOException {
    StringBuffer buf = new StringBuffer();
    BufferedReader in = new BufferedReader
      (new InputStreamReader(pDataSource.getInputStream()));
    String line;
    while ((line = in.readLine()) != null) {
      buf.append(line);
      buf.append('\n');
    }
    return buf.toString();      
  } 
    
  //-------------------------------------
  /**
   * Convert the object to a byte stream of the specified MIME type 
   * and write it to the output stream.
   * 
   * @exception IllegalArgumentException if pMimeType is not 
   * "text/html," or if pObject is not either a String or an 
   * InputStream
   **/
  public void writeTo(Object pObject, 
		      String pMimeType, 
		      OutputStream pOutputStream) 
       throws IOException 
  {
    String mimeType = pMimeType.toLowerCase();
    
    if (!mimeType.startsWith(TEXT_HTML)) {
      Object[] args = { pMimeType };
      throw new IllegalArgumentException
	(ResourceUtils.getMsgResource
	 ("emailHtmlDataContentHandlerIllegalMimeType", 
	  MY_RESOURCE_NAME, sResourceBundle, args));
    }

    String encoding = null;
    int charsetIndex = mimeType.indexOf(CHARSET_STRING);
    if (charsetIndex != -1) {
      /* Make sure to preserve the case of the encoding */
      encoding = pMimeType.substring(charsetIndex + CHARSET_STRING.length());
      int sepIndex = encoding.indexOf(';');
      if (sepIndex != -1)
	encoding = encoding.substring(0, sepIndex);
      sepIndex = encoding.indexOf(' ');
      if (sepIndex != -1)
	encoding = encoding.substring(0, sepIndex);

      /* Peel of any quotation marks around the name */
      if (encoding.startsWith("\""))
       encoding = encoding.substring(1,encoding.length()-1);
      
      if (encoding.equals("us-ascii"))
	encoding = "ASCII";
    }

    if (pObject instanceof String) {
      OutputStreamWriter osw = (encoding == null) ? 
	new OutputStreamWriter(pOutputStream) : 
	new OutputStreamWriter(pOutputStream, encoding);
      Writer out = new BufferedWriter(osw);

      out.write((String) pObject);
      out.flush();
    } 

    else if (pObject instanceof InputStream) {
      InputStreamReader isr = (encoding == null) ?
	new InputStreamReader((InputStream) pObject) :
	new InputStreamReader((InputStream) pObject, encoding);
      BufferedReader in = new BufferedReader(isr);

      OutputStreamWriter osw = (encoding == null) ? 
	new OutputStreamWriter(pOutputStream) : 
	new OutputStreamWriter(pOutputStream, encoding);
      PrintWriter out = new PrintWriter(new BufferedWriter(osw));
      
      String line;
      while ((line = in.readLine()) != null)
	out.println(line);
      out.flush();
    }
    
    else if (pObject instanceof byte[]) {
      pOutputStream.write((byte[]) pObject);
      pOutputStream.flush();
    }

    else {
      Object[] args = { pObject.getClass().getName(), pObject.toString() };
      throw new IllegalArgumentException
	(ResourceUtils.getMsgResource
	 ("emailHtmlDataContentHandlerIllegalObjectType",
	  MY_RESOURCE_NAME, sResourceBundle, args));
    }
  }

  //-------------------------------------
}

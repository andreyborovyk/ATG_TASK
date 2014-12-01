/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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
package atg.servlet.pagecompile;

import java.io.*;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.servlet.ServletUtil;

/**
 *
 * Converts Japanese encoded text to UNICODE.  Useful when the
 * encoding method of the text is not known.  Should only be used if
 * you are really looking for Japanese encoding.  If you know you have
 * ASCII, then don't waste your time trying to convert.
 * <P>
 *
 * The encoding method of the text is determined using Ken Lunde's
 * algorithm.  For more information, see "Understanding Japanese
 * Information Processing" by Ken Lunde from O'Reilly & Associates,
 * ISBN 1-56592-043-0.
 * <P>
 *
 * @author pagan lord craig
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pagecompile/JapaneseConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class JapaneseConverter extends GenericService implements Converter
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pagecompile/JapaneseConverter.java#2 $$Change: 651448 $";

  /*--------------------------------------------------------*/
  // Constants

  /** ASCII encoding method. */
  static final byte   ASCII			= 1;
  static final String ASCII_ENCODING		= "ASCII";

  /** New JIS encoding method (also called JIS X 0208-1983). */
  static final byte   NEW			= 2;

  /** Old JIS encoding method (also called JIS X 0208-1978). */
  static final byte   OLD			= 3;

  /** NEC Kanji encoding method. */
  static final byte   NEC			= 4;

  /** Shift-JIS encoding method. */
  static final byte   SJIS			= 5;

  /** EUC encoding method. */
  static final byte   EUC			= 6;

  /** Either EUC or Shift-JIS encoding method. */
  static final byte   EUCORSJIS			= 7;

  /** End of file. Used in detection algorithm. */
  static final byte EOF		= (byte) (-1);


  /*--------------------------------------------------------*/
  // Member variables


  /*--------------------------------------------------------*/
  // Constructor

  /** 
   *
   * Empty constructor.
   **/
  public JapaneseConverter ()
    {
      // Nothing to do!
    }


  /*--------------------------------------------------------*/
  // Properties
  //
  //   fallbackEncoding
  //

  String mFallbackEncoding;
  /**
   *
   * The encoding type to use when the actual encoding type of data to
   * convert could not be discerned.  This value should be a String
   * such as, "JIS", "SJIS", or "EUC_JP".
   **/
  public void setFallbackEncoding (String pValue)
  {
    mFallbackEncoding = pValue;
  }
  /**
   *
   * The encoding type to use when the actual encoding type of data to
   * convert could not be discerned.  This value should be a String
   * such as, "JIS", "SJIS", or "EUC_JP".
   **/
  public String getFallbackEncoding ()
  {
    return mFallbackEncoding;
  }


  /*--------------------------------------------------------*/
  // Public Methods
  //
  //   convert (byte[])
  //   convert (String)
  //   convert (Dictionary)
  //

  /**
   *
   * Converts the byte array into a Java UNICODE String. <P>
   * 
   * The passed in encoding is ignored.  This class is designed to
   * look at the data to convert and attempt to discern the encoding
   * of the data.
   **/
  public String convert (byte[] pValue, String pEncoding)
       throws IOException
  {
    // Umm... we want to always try and discern the encoding from the
    // data.  If someone wants to actually use a specific encoding,
    // let them make a new class.
    String encoding = null;

    // If a null encoding was passed in, make a stab at detecting the
    // encoding.  We might have already tried this, but then again, we
    // might not have.
    if (encoding == null)
      {
        byte codeType = detectCodingType(pValue);
        encoding = typeToEncoding(codeType);
        if (encoding == null)
          {
            // If we could not figure out the encoding type, see if we are
            // to use some default encoding.
            encoding = getFallbackEncoding();
            if (encoding == null)
              throw new IOException("unable to discern data encoding type : no default/fallback to use");
            if (isLoggingWarning())
              logWarning("unable to discern data encoding type : defaulting to "+encoding);
          }
      }

    // No conversion necessary for ASCII;
    if (encoding == ASCII_ENCODING ||
        ServletUtil.checkIsSingleByteEncoding(encoding))
      return new String(pValue);

    // Turn the bytes into an InputStream.
    ByteArrayInputStream bais = new ByteArrayInputStream(pValue);
    // Put that into an InputStreamReader which we can give an
    // encoding type to.
    InputStreamReader isr = new InputStreamReader(bais, encoding);
    // Now pull each converted character out of the stream and put
    // into the buffer.
    StringBuffer sb = new StringBuffer();
    for (int c = isr.read(); c != (-1); c = isr.read())
      sb.append((char) c);
    // Return the converted string.
    return sb.toString();
  }

  /**
   *
   * Converts the byte array into a Java UNICODE String.  This method
   * assumes that the native text only occupies the low bytes of the
   * String, i.e. the high bytes in the supplied String will be
   * dropped. <P>
   * 
   * The passed in encoding is ignored.  This class is designed to
   * look at the data to convert and attempt to discern the encoding
   * of the data.
   **/
  public String convert (String pValue, String pEncoding)
       throws IOException
  {
    // The question here is how are the encoded bytes stored in the
    // string?  Are all the high bytes of the string 0 meaning high
    // and low bytes of the encoded text are stored only in the low
    // bytes of the string?  Or are some of the high bytes occupied
    // indicating that even though it isn't UNICODE the encoded bytes
    // stored in both high and low bytes of the string?
    byte[] bytes = getBytes(pValue);
    return convert(bytes, pEncoding);
  }

  /**
   *
   * Converts all of the keys and values in the supplied dictionary to
   * UNICODE Strings.  The method assumes that the keys and values are
   * both Strings to begin with. <P>
   * 
   * The passed in encoding is ignored.  This class is designed to
   * look at the data to convert and attempt to discern the encoding
   * of the data.
   **/
  public Dictionary convert (Dictionary pValue, String pEncoding)
       throws IOException
  {
    if (pValue == null) return null;
    // Okay, this sucks.  Originally i thought, all of the items in
    // this Dictionary will be of the same encoding type, so we'll
    // stop detecting the type once we figure out what it is.  Fine.
    // However, if you tell the StreamReader that you are decoding JIS
    // and you pass it raw ASCII, you get back all kinds of garbage.
    // *sigh* So now we will detect each line so we won't decode the
    // ASCII lines by mistake, even though this should be legal as
    // ASCII is just a sub-set of all the other encoding methods.
    
    // Do we convert keys and values?  If so we'll need to construct a
    // new Dictionary.
    Dictionary returnTable = new Hashtable();

    // Run through the dictionary.
    Enumeration keys = pValue.keys();
    Enumeration values = pValue.elements();
    while (keys.hasMoreElements())
      {
        String key = (String) keys.nextElement();
        Object value = values.nextElement();

        key = convert(key, pEncoding);

        // The value might be a String[] or a String, so we will
        // have to handle both cases here.
        if (value instanceof java.lang.String)
          {
            value = convert((String) value, pEncoding);
          }
        else if (value instanceof java.lang.String[])
          {
            String[] newValue = new String[((String[]) value).length];
            for (int i = 0; i < ((String[]) value).length; i++)
              {
                newValue[i] = convert(((String[]) value)[i], pEncoding);
              }
            value = newValue;
          }
        
        // Put the converted key/value pair into the new dictionary. 
        returnTable.put(key, value);
      }

    return returnTable;
  }


  /*--------------------------------------------------------*/
  // Protected Methods
  //
  //   getBytes
  //   typeToEncoding
  //   detectCodingType
  //

  /**
   *
   * Because java.lang.String.getBytes() is deprecated, we will have
   * our own version here.  Sometimes strings get into Java String
   * objects where the high and low bytes of the encoded string are
   * all stored in the low bytes of the Java String.  We will want to
   * be able to pull those high and low bytes out cleanly.
   **/
  byte[] getBytes (String pValue)
  {
    byte[] bytes = new byte[pValue.length()];
    // Hmmm... do we do a ton of String.getChars or do we get the
    // String's char[]?
    for (int i = 0; i < bytes.length; i++)
      bytes[i] = (byte) pValue.charAt(i);
    return bytes;
  }

  /**
   *
   * Returns the Java encoding tag equivalent to the internal encoding
   * type representation.  If there is no mapping or the encoding type
   * is indeterminate, null is returned.
   **/
  String typeToEncoding (byte pValue)
       throws IOException
  {
    switch (pValue)
      {
      case ASCII:
        return ASCII_ENCODING;
      case NEW:
        // BUG: which one of these is the encoding Java means when it
        // says JIS (previously it said JIS0208, but that doesn't help
        // much either.

        // throw new IOException("no mapping for JIS X 0208-1983 to Java encoding type");
        return "JIS";
      case OLD:
        // BUG: which one of these is the encoding Java means when it
        // says JIS (previously it said JIS0208, but that doesn't help
        // much either.

        // throw new IOException("no mapping for JIS X 0208-1978 to Java encoding type");
        return "JIS";
      case NEC:
        // BUG: what does this map to in Java charset?
        throw new IOException("no mapping for NEC to Java encoding type");
      case SJIS:
        return "SJIS";
      case EUC:
        return "EUC_JP";
      case EUCORSJIS:
        // BUG: if we hit this, we really don't know what it might be.
        // We could default to some encoding type, but if we are
        // wrong, then we will have problems.
      default:
        return null;
      }
  }

  /**
   *
   * See detectCodingType(byte[]) for functional description.
   *
   * @param pValue String
   * @return byte indicating encoding type
   **/
  byte detectCodingType (String pValue)
  {
    // The question here is how are the encoded bytes stored in the
    // string?  Are all the high bytes of the string 0 meaning high
    // and low bytes of the encoded text are stored only in the low
    // bytes of the string?  Or are some of the high bytes occupied
    // indicating that even though it isn't UNICODE the encoded bytes
    // stored in both high and low bytes of the string?
    byte[] bytes = getBytes(pValue);
    return detectCodingType(bytes);
  }

  /**
   *
   * Discerns the encoding type of the given array. <P>
   *
   * This code has been ported (with slight modification) to Java from
   * Ken Lunde's C function "detectCodingType".  For more information,
   * see "Understanding Japanese Information Processing" by Ken Lunde
   * from O'Reilly & Associates, ISBN 1-56592-043-0.
   *
   * @param pArray array to detect encoding type of
   * @return byte indicating encoding type
   **/
  byte detectCodingType (byte[] pValue)
  {
    int index = -1;
    byte whatCode = ASCII;
    int c = 0;


    while ((whatCode == EUCORSJIS || whatCode == ASCII) && c != EOF)
      if ((c = ((index+=1) < pValue.length) ? (pValue[index] & 0xFF) : EOF) != EOF)
	{
	  if (c == 27) 
	    {
	      c = ((index+=1) < pValue.length) ? (pValue[index] & 0xFF) : EOF;
	      if (c == '$') 
		{
		  c = ((index+=1) < pValue.length) ? (pValue[index] & 0xFF) : EOF;
		  if (c == 'B')
		    whatCode = NEW;
		  else if (c == '@')
		    whatCode = OLD;
		}
	      else if (c == 'K')
		whatCode = NEC;
	    }
	  else if ((c >= 129 && c <= 141) || (c >= 143 && c <= 159))
	    whatCode = SJIS;
	  else if (c == 142) 
	    {
	      c = ((index+=1) < pValue.length) ? (pValue[index] & 0xFF) : EOF;
	      if ((c >= 64  && c <= 126) || (c >= 128 && c <= 160) || (c >= 224 && c <= 252))
		whatCode = SJIS;
	      else if (c >= 161 && c <= 223)
		whatCode = EUCORSJIS;
	    }
	  else if (c >= 161 && c <= 223) 
	    {
	      c = ((index+=1) < pValue.length) ? (pValue[index] & 0xFF) : EOF;
	      if (c >= 240 && c <= 254)
		whatCode = EUC;
	      else if (c >= 161 && c <= 223)
		whatCode = EUCORSJIS;
	      else if (c >= 224 && c <= 239)
		{
		  whatCode = EUCORSJIS;
		  while (c >= 64 && c != EOF && whatCode == EUCORSJIS)
		    {
		      if (c >= 129) 
			{
			  if (c <= 141 || (c >= 143 && c <= 159))
			    whatCode = SJIS;
			  else if (c >= 253 && c <= 254)
			    whatCode = EUC;
			}
		      c = ((index+=1) < pValue.length) ? (pValue[index] & 0xFF) : EOF;
		    }
		}
	      else if (c <= 159)
		whatCode = SJIS;
	    }
	  else if (c >= 240 && c <= 254)
	    whatCode = EUC;
	  else if (c >= 224 && c <= 239)
	    {
	      c = ((index+=1) < pValue.length) ? (pValue[index] & 0xFF) : EOF;
	      if ((c >= 64  && c <= 126) || (c >= 128 && c <= 160))
		whatCode = SJIS;
	      else if (c >= 253 && c <= 254)
		whatCode = EUC;
	      else if (c >= 161 && c <= 252)
		whatCode = EUCORSJIS;
	    }
	}

    // Return the code type.
    return whatCode;
  }

} // End of class JapaneseConverter

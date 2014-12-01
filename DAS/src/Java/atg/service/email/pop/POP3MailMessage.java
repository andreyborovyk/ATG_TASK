/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.service.email.pop;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Dictionary;
import java.util.Vector;


/**
 * The <code>POP3MailMessage</code> class encapsulates the definition of
 * an email message header and body. 
 *
 * @author  R. DeMillo
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/pop/POP3MailMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class POP3MailMessage {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/pop/POP3MailMessage.java#2 $$Change: 651448 $";


  protected Dictionary mHeader = null;
  protected MessageBody mBody[] = null;
  protected String mAttachmentDir = null;
  protected POP3 mPOP3Connection  = null;
  protected POP3Reader mIn = null;
  protected int mMsgSize = -1;
  protected int mMsgTotal = 0;
  protected POP3MessageInfo mMsgmInfo = null;

  /**
   * Create a mail message identified by <code>msgmInfo</code>
   * ready to read from the the specified POP3 connection.
   *
   * @param  connection   The POP3 connection to read the message from.
   * @param  msgmInfo     Information about the message to read.
   */
  public  POP3MailMessage (POP3 connection, 
                           POP3MessageInfo msgmInfo) {
    mPOP3Connection = connection;
    mAttachmentDir  = connection.getAttachmentDir();
    mIn = connection.getInputStream();
    mMsgmInfo = msgmInfo;
    mMsgSize = msgmInfo.getMessageSize();		
  }

  /**
   * Create a mail message ready to read from the the
   * specified POP3 connection.
   *
   * @param  connection   The POP3 connection to read the message from.
   */
  public  POP3MailMessage (POP3 connection) {
    mPOP3Connection = connection;
    mAttachmentDir  = connection.getAttachmentDir();
    mIn = connection.getInputStream();
  }


  /**
   * Create a mail message ready to read from the the specified stream.
   *
   * @param  in  The stream to read the message from.
   */
  public POP3MailMessage (POP3Reader in) {
    mAttachmentDir = POP3.getDefaultAttachmentDir();
    mIn = mPOP3Connection.getInputStream();
    mIn = in;
  }


  /**
   * Create a mail message ready to read from the the specified stream.
   * Attachments will be saved in the specified directory.
   *
   * @param  in  The stream to read the message from.
   * @param  attachment_dir  The directyory to save attachments into.
   */
  public POP3MailMessage (POP3Reader in, 
                          String attachment_dir)  {

    mAttachmentDir = attachment_dir;
    mIn = in;
  }

  /**
   * Set the computed size of the message.
   *
   * @param  msg_size  the computed size of the message.
   */
  public void setMessageSize (int msg_size) {
    mMsgSize = msg_size;
  }


  /**
   * Return the name of the directory which attachments are saved in.
   */
  public String getAttachmentDir () {
    return (mAttachmentDir);
  }


  /**
   * Set the directory for saving attachemnts.
   *
   * @param  dir  the new attachment directory.
   */
  public void setAttachmentDir (String dir) {
    mAttachmentDir = dir;
  }


  /**
   * Gets a hashtable that contains the name/value pairs from the
   * message header.
   */
  public Dictionary getHeader () {
    return (mHeader);
  }


  /**
   * Get the value associated with a header field name. Assumes that
   * the header has been read with <code>readHeader</code>.
   *
   * @param  name  the name of the header field to retrieve the value for
   */
  public String getHeaderValue (String name) {

    if (mHeader == null) {
      return (null);
    }
    
    /**
     * No need, since we're using a case insensitive hashtable
     **/
    //name = name.toLowerCase();

    try {
      String value = (String)mHeader.get(name);
      return (value);
    } catch (Exception ignore) {
      return (null);
    }
  }


  /**
   * Read the mail message.
   *
   * @exception  IOException If an I/O error occurs 
   * @exception  POP3Exception  when a POP3 protocol error occurs.
   * @exception  MalformedEmailException if the email being read is 
   * malformed
   */
  public POP3MailMessage read () 
    throws IOException, POP3Exception, MalformedEmailException 
  {

    if (mIn == null) {
      throw new IOException("message stream is null");
    }

    int start = mIn.getBytesRead();
    mHeader = mIn.readNameValuePairs();
    int total = mIn.getBytesRead() - start;
    if (mPOP3Connection != null) {
      mPOP3Connection.fireMailProgressEvent(total, total, mMsgSize);
    }
    incrMessageTotal(total);

    mBody   = readBody();
    return (this);
  }


  /**
   * Read the body of the message. The body may contain both
   * inline content and attachments.  We assume that the header
   * has been read prior to calling this method.
   *
   * @exception  IOException  If an I/O error occurs 
   * @exception  POP3Exception  when a POP3 protocol error occurs.
   * @exception  MalformedEmailException if the email being read is 
   * malformed
   */
  protected synchronized MessageBody [] readBody ()
    throws IOException, POP3Exception, MalformedEmailException  {

    if (mBody != null) {
      return (mBody);
    }

    if (mIn == null) {
      throw new IOException("message stream is null");
    }

    if (mHeader == null) {
      throw new POP3Exception("header not read yet");
    }

    /*
     * Is this a multipart message?
     */
    String content_type = getHeaderValue("content-type");
    if (content_type == null) {
      content_type = "text/plain";
    }

    String tmp = content_type.toLowerCase();
    if (tmp.indexOf("multipart") >= 0) {
      return (parseMultipart(content_type, false));
    }

    return getPlainTextContent(content_type);
  }

  /**
   * Get the body of the message. The body may contain both
   * inline content and attachments.
   */
  public synchronized MessageBody [] getBody () {
    return (mBody);
  }


  /**
   * Read in a multipart message and save the body and files contained
   * in the boundaries.
   *
   * @exception  IOException If an I/O error occurs 
   * @exception  MalformedEmailException if the email being read is 
   * malformed
   */
  protected MessageBody [] parseMultipart (String content_type,
					   boolean pIsNestedMultipart)
    throws IOException, MalformedEmailException  {

    /*
     * Parse the boundary string out of the content type
     */
    String tmp = content_type.toLowerCase();
    int index = tmp.indexOf("boundary=");
    if (index == -1) {
      handleMalformedEmail
        ("no boundary found in multipart message");
    }

    String boundary = content_type.substring(index + 9).trim();
    if ((boundary = Utilities.removeQuotes(boundary)) == null) {	
      handleMalformedEmail
        ("malformed boundary in multipart message");
    }
    /*
     * Get the length of the boundary string, we'll need it later
     */
    int boundary_len = boundary.length();

    /*
     * Now read each boundary and create a MessageBody object
     * to hold the text or file info
     */
    Vector parts = new Vector();
    MessageBody currentPart = null;
    String base64_extra = null;
    boolean attachment = false;
    boolean is_base64 = false;
    boolean isNestedMultipart = pIsNestedMultipart;
    int last_total = mIn.getBytesRead();
    byte line_separator[] = Utilities.getLineSeparator().getBytes();
    while (true) {
      
      String line = mIn.readLine();
      if (line == null) {
	break;
      }
      
      int bytes = mIn.getBytesRead() - last_total;
      int msg_total = incrMessageTotal(bytes);
      last_total += bytes;
      if (mPOP3Connection != null) {
	mPOP3Connection.fireMailProgressEvent(bytes, msg_total, mMsgSize);
      }
      
      /*
       * Are we on a new boundary?
       */
      if (line.indexOf(boundary) == 2 && line.substring(0, 2).equals("--"))
	{
	  // If we have a message part to finish up, do it
	  if (currentPart != null) {
	    if (currentPart.getType() == MessageBody.FILE) {
	      currentPart.completeFile();
	    }
	    parts.addElement(currentPart);
	    currentPart = null;
	    base64_extra = null;
	    attachment = false;
	    is_base64 = false;
	  }
	  
	  /** See if this is the terminating boundary,
	      if so we are all done here **/
	  if (line.indexOf("--", boundary_len + 2) == boundary_len + 2) {
	    if(isNestedMultipart)
	      break;
	      else
		continue;
	  }
	  /** Parse the message part header **/
	  currentPart = parseParts();
	  if (currentPart == null) {
	    continue;
	  }
	  
	  bytes = mIn.getBytesRead() - last_total;
	  msg_total = incrMessageTotal(bytes);
	  last_total += bytes;
	  if (mPOP3Connection != null) {
	    mPOP3Connection.fireMailProgressEvent
	      (bytes, msg_total, mMsgSize);
	  }
	  
	  /** Multipart part? **/
	  if(currentPart.getType() == MessageBody.MULTIPART) {
	    MessageBody [] moreParts =
		parseMultipart(currentPart.getUnparsedContentType(), true);
	    for(int i = 0; i < moreParts.length; i++) {
	      MessageBody morePart = moreParts[i];
	      parts.addElement(morePart);
	    }
	    currentPart = null;
	    continue;
	  }// if multipart
	  
	  /** File? **/
	  else if (currentPart.getType() == MessageBody.FILE) {
	    String encoding = currentPart.getEncoding();
	    if (encoding != null &&
		encoding.equals("base64")) {
	      is_base64 = true;
	    } else {
	      is_base64 = false;
	    } // if base64
	    
	    attachment = true;
	    base64_extra = null;
	  } // if this is a file
	  else {
	    attachment = false;
	  } // otherwise, it's text
	  
	} // not a boundary
      else if (currentPart == null) {
	continue;
      }// null part
      else if (is_base64 && attachment) {
	if (base64_extra != null) {
	  line = base64_extra + line;
	}
	
	/**
	 * The base 64 Codec only works with
	 * data lengths divisible by 4
	 **/
	int len = line.length();
	int left_over = len % 4;
	if (left_over != 0) {
	  base64_extra = line.substring(len - left_over);
	  line = line.substring(0, len - left_over);	
	}// If the length is not divisible by 4
	else {
	  base64_extra = null;
	}
	
	byte buf[] = Base64Codec.decode(line.getBytes());
	if (buf != null) {
	  currentPart.appendBuffer(buf);
	}
	
      } // if base64 attachment
      else if (attachment) {
	byte buf[] = line.getBytes();
	if (buf != null) {
	  currentPart.appendBuffer(buf);
	  currentPart.appendBuffer(line_separator);
	}
	
      }// if just an attachment
      else {
	currentPart.append(line);
      }// if just text (not attachment)
    }

    /*
     * Convert the Vector of MessageBody to MessageBody[]
     */
    int num_parts = parts.size();
    if (num_parts < 1) {
      return (null);
    }

    MessageBody body[] = new MessageBody[num_parts];
    parts.copyInto(body);
    return (body);	
  }


  /**
   * Reads and parses the information for 1 part of a multipart message.
   *
   * @exception  IOException If an I/O error occurs 
   */
  protected MessageBody parseParts () throws IOException {

    Dictionary parts;
    try {
      if ((parts = mIn.readNameValuePairs()) == null) {
        return (null);
      }
    } catch (Exception e) {
      return (null);
    }


    String content_type = (String)parts.get("content-type");
    String content_disp = (String)parts.get("content-disposition");
    String enc = (String)parts.get("content-transfer-encoding");
    
    if(content_type == null)
      content_type = "text/plain";
    /** See if this is another multipart **/
    String tmp = content_type.toLowerCase();
    if(tmp.indexOf("multipart") >= 0) {
      return (new MessageBody(true, content_type));
    }

    /*
     * If part has no content-disposition item then it's
     * an inline message
     */
    if (content_disp == null) {
      return (new MessageBody("", content_type));
    }

    /*
     * See if this content-disp has more than 1 part
     */
    int index = content_disp.indexOf(";");
    if (index < 0) {
      return new MessageBody("", content_type);
    }

    String part2 = content_disp.substring(index + 1);
    if (part2 != null) {
      part2 = Utilities.removeQuotes(part2.trim());
    }

    /*
     * See if there is a filename in the content-disposition
     */
    if (part2 != null && part2.length() > 0) {
      index = part2.indexOf("filename");
      if (index >= 0) {
        String filename = part2.substring(index + 9);
        if (filename != null) {
          filename = Utilities.removeQuotes(filename.trim());

          return new MessageBody(filename, content_type, enc);
        }
      }
    }
    return new MessageBody("", content_type);
  }


  /**
   * Utility function which simply writes the message header
   * to System.out.
   *
   * @exception  IOException If an I/O error occurs 
   */
  public void dumpHeader () throws IOException {

    if (mHeader == null) {
      return;
    }

    Enumeration keys = mHeader.keys();
    while (keys.hasMoreElements()) {
      String key = (String)keys.nextElement();
      String value = (String)mHeader.get(key);
      System.out.println(key + ": " + value);	
    }
  }

  /**
   * See if you can auto determine this is a bounced message.
   * Note: I believe the only consistancy in determining
   * returned email is in the body of the email. If the
   * RCPT code is 550, the message was returned to sender.
   * (UNIX boxes defaul to a return from the MAILER_DAEMON, but
   * I'm not convinced that this is a guarentee.) - rjd
   *
   * @see atg.service.email.pop.POP3MailMessage
   * @param none
   * @return attempted email address if bounced, null otherwise
   */
   public String isReturnToSender() throws Exception {

     //unused boolean result = false;
    MessageBody msgs[] = getBody();
    String badAddress = null;
    
    // Need to loop through multi-part messages to find this
    for (int i = 0; i < msgs.length; i++) {
      // The 550 code is in the text portion
      if (msgs[i].getType() == MessageBody.TEXT) {
        // Search the text of message for the RCPT code
        int rcptCodeIndex = (msgs[i].getText()).indexOf("<<< 550");
        if ( rcptCodeIndex >= 0) {
          // Found the code, assume the worst
          // get the bad email address
          for (int j = rcptCodeIndex+7; j< (msgs[i].getText()).length(); j++) {
            if ((msgs[i].getText()).charAt(j) == '<') {
              // Found the email address
              badAddress =  (msgs[i].getText()).substring(j+1, 
                                       (msgs[i].getText()).length() - 1);
              // strip off crap at the end
              int lastChar = badAddress.indexOf(">");
              badAddress = badAddress.substring(0, lastChar);
              //unused result = true; 
              return badAddress;
            }
            
          }
        }
      }
      
    }
    
    return badAddress;
  }

/**
  * Dumps the current message and all message attachments 
  * in an organized output. This does not dump the header.
  *
  * This method takes no action if the message is ill formed.
  *
  * @see #dumpHeader()
  * @param none
  * @retun void
  */
  public void dump () throws Exception {

    MessageBody msgs[] = getBody();
    dumpHeader();
    
    for (int i = 0; i < msgs.length; i++) {
      System.out.println("");
      System.out.println("content-type: " +
                         msgs[i].getContentType());

      if (msgs[i].getType() == MessageBody.TEXT) {
        System.out.println("charset: " +
                           msgs[i].getCharset());
        System.out.println(msgs[i].getText());
      } else {
        System.out.println("name: " +
                           msgs[i].getName());
        System.out.println("filename: " +
                           msgs[i].getFilename());
        System.out.println("encoding: " +
                           msgs[i].getEncoding());
      }
    }
  }
 
 /**
   * Increment the total count of bytes read for this message.
   *
   * @param  by_bytes  The number of bytes to increment the total by.
   */
  protected synchronized int incrMessageTotal (int by_bytes) {
    mMsgTotal += by_bytes;
    return (mMsgTotal);
  }


  /**
   * Get the computed size of this message.
   */
  public synchronized int getMessageSize () {
    return (mMsgSize);
  }


  /**
   * Get the total count of bytes processed for this message to date.
   */
  public synchronized int getMessageTotal () {
    return (mMsgTotal);
  }
  
  /**
   * Gets plain text content for this message
   * @param pContentType the content type
   * @return a message body of plain text content
   **/
  private MessageBody[] getPlainTextContent(String pContentType) 
    throws IOException
  {
    /*
     * If we got here the message is a normal non-attachement
     * type of message. Read the body of the message.
     */
    StringBuffer buf = new StringBuffer();
    boolean first_line = true;
    int last_total = mIn.getBytesRead();
    while (true) {
			
      String line = mIn.readLine();
      if (line == null) {
        break;
      }

      if (first_line) {
        first_line = false;
        buf.append(line);
      } else {
        buf.append("\n" + line);
      }


      int bytes = mIn.getBytesRead() - last_total;
      int msg_total = incrMessageTotal(bytes);
      last_total += bytes;
      if (mPOP3Connection != null) {
        mPOP3Connection.fireMailProgressEvent(
                                               bytes, msg_total, mMsgSize);
      }
    }
	
    MessageBody body[] = new MessageBody[1];
    body[0] = new MessageBody(buf.toString(), pContentType);
    return (body);	
  }
  
  /**
   * Creates a MalformedEmailException that may or may not include
   * the number of the message that we're processing
   * @param pMessage the message of the exception, if we are going
   * to throw one
   * @return a message body, if we are to process the email as normal
   * @exception MalformedEmailException if we shouldn't process the
   * email
   **/
  private void handleMalformedEmail(String pMessage)
    throws MalformedEmailException, IOException
  {
    // We need to read to the end of the message so the 
    // input stream is cleared for any further commands
    // NOTE - if an IOException occurs here, we've got 
    // bigger problems
    mBody = getPlainTextContent("text/plain");
    throw new MalformedEmailException
      (pMessage, this);
  }
    
}



/*<ATGCOPYRIGHT>

 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.xmlprotocol;

import java.io.PrintStream;
import java.io.PrintWriter;


 /**
  * This class enables nesting of exceptions, e.g.
  *  try {
  *        ...
  *  } catch (Exception e) {
  *       throw new ConversationException(e);
  *  }
  *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/ConversationException.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/

public class ConversationException extends Exception {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/ConversationException.java#2 $$Change: 651448 $";


    private Throwable mOriginalException;

    public ConversationException (String pMessage){
      super (pMessage);
    }

    public ConversationException (){
      super ();
    }
    public ConversationException( Throwable pOrignalException ) {
      super(pOrignalException.getMessage());
      mOriginalException = pOrignalException;
  }

    public ConversationException( String pMessage, Throwable pOrignalException ) {
      super( pMessage );
      mOriginalException = pOrignalException;
    }

    public void printStackTrace( PrintStream ps ) {
      if (mOriginalException != null){
        mOriginalException.printStackTrace(ps);
      }else{
        super.printStackTrace(ps);
      }
    }

    public void printStackTrace( PrintWriter pw ) {
      if (mOriginalException != null){
        mOriginalException.printStackTrace(pw);
      }else{
        super.printStackTrace(pw);
      }
    }

    public void printStackTrace() {
     if (mOriginalException != null){
        mOriginalException.printStackTrace();
      }else{
        super.printStackTrace();
      }
    }

    public Throwable fillInStackTrace() {
        if ( mOriginalException == null ) {
            return super.fillInStackTrace();
        } else {
            return mOriginalException.fillInStackTrace();
        }
    }

    //returns original exception or null
     public Throwable getOriginalException() {
        return mOriginalException;
    }



}
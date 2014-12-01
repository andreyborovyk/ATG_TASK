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

package atg.integrations.taxware;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * <p>VeraZipCaller provides an higher level interface to TaxWare's zipcode
 *    verification software. 
 *
 * @see ZipRequest
 * @see ZipResultItem
 * @see ZipResult
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipCaller.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */


public class VeraZipCaller {
    
    // Class version string
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipCaller.java#2 $$Change: 651448 $";
    
    static int INPUT_GEN_RECORD_SIZE = 0;
    static int INPUT_IND_RECORD_SIZE = 68;
    static int OUTPUT_GEN_RECORD_SIZE = 208;
    static int OUTPUT_IND_RECORD_SIZE = 70;
        
    /** StringBuffer re-used for each request */
    protected static StringBuffer m_strbufInput; // input buffer
    
    /** Input header re-used for each request */
    protected static InputHeader m_inputHeader; // input header
    
    /** whether TaxWare's files are open or not */
    protected static boolean m_bOpen = false;
    
    /** method for calculating tax */
    protected static Method m_methodCalculateTax;
    
    /** argument array */
    protected static Object[] m_rgobjArgs;

    /** this is empty for the time being */
    static class InputHeaderRecordDef extends RecordDef {
        InputHeaderRecordDef() {
            super(1);
        }
        protected void initializeFieldDefs() {
            // General input record layout
        }
    }
    
    /** Defines the input header which starts each
     *  taxware request.
     */
    static class InputHeader extends FieldSet {
        protected static RecordDef m_recordDef;
        
        /** Create the record definition shared by all instances
         * of this class. */
        protected synchronized void createRecordDef() {
            if (null != m_recordDef)
                return;
            m_recordDef = new InputHeaderRecordDef();
        }

        /** get the RecordDef shared by all instances of this class. */
        RecordDef getRecordDef() {
            if (null == m_recordDef) {
                createRecordDef();
            }
            return(m_recordDef);
        }
    }
    
    
  /** Class that defines the output header. */
    static class OutputHeaderRecordDef extends RecordDef {
        OutputHeaderRecordDef() {
            super(3);
        }
        
        protected void initializeFieldDefs() {
            // General output record layout
            addFieldDef("NUMREC", ZipOutRecDef.NUMRECSIZE, FieldDefinition.INT_FTYPE);
            addFieldDef("CMPLCODE", ZipOutRecDef.CCSIZE, FieldDefinition.INT_FTYPE);
            addFieldDef("ERRMESSAGE", ZipOutRecDef.NEWMESSAGESIZE);
        }
    }
    
    /** Class the represents the output header. */
    static class OutputHeader extends FieldSet {
        protected static RecordDef m_recordDef;
        
        protected synchronized void createRecordDef() {
            if (null != m_recordDef)
                return;
            m_recordDef = new OutputHeaderRecordDef();
        }
        
        RecordDef getRecordDef() {
            if (null == m_recordDef) {
                createRecordDef();
            }
            return(m_recordDef);
        }
        
        public int getNumberOfRecords() {
            return((int)getLongFieldValue("NUMREC"));
        }
        public int getCompletionCode() {
            return((int)getLongFieldValue("CMPLCODE"));
        }
        public String getErrorMessage() {
            return(getStringFieldValue("ERRMESSAGE"));
        }
    }
    
    
    /** Create our static input string buffer */
    protected synchronized static void createInputStringBuffer() {
        
        m_strbufInput = createBuffer(1);
        
        if (null == m_inputHeader)
            createInputHeader();
        
        m_inputHeader.writeFields(m_strbufInput, 0);
    }

    /* Create our static InputHeader. */
    protected synchronized static void createInputHeader() {
        if (null != m_inputHeader)
            return;
        
        m_inputHeader = new InputHeader();
        // should we set something for the header?
    }
    
    /** Create a string buffer already pre-filled to the
     * appropriate size. */
    protected static StringBuffer createBuffer(int cRequests) {
        int cBufSize = INPUT_GEN_RECORD_SIZE + (cRequests *
                                                INPUT_IND_RECORD_SIZE);
        
        StringBuffer strbuf = new StringBuffer(cBufSize);
        
        // now, let's pad the whole thing with spaces.
        // Surely there is a more efficient way to do this in Java???
        
        for (int i=0; i < cBufSize; i++) {
            strbuf.append(' ');
        }
        
        return(strbuf);
    }
    
    /** Calls the verify zipcode function.
     *
     * @exception TaxwareCriticalException Thrown if installation problem.
     * @exception TaxwareMinorException Thrown if error returned from VeraZip.
     */
    protected static synchronized OutputHeader callCalculate(String stringRequest, 
                                                             StringBuffer strbuf)
        throws TaxwareMinorException, TaxwareCriticalException {
        
        StringBuffer intobj = new StringBuffer();

        // call to the UTL to pass the verazip request
        SalesTaxCaller.callCalculate(stringRequest, strbuf, intobj, true);
        
        Integer intobjResult = new Integer(intobj.toString());
        int iResult = intobjResult.intValue();
        
        OutputHeader outhead = new OutputHeader();
        outhead.parseFields(strbuf.toString(), 0);
        
        //System.out.println("VeraZip RESULT: " + iResult);
        //System.out.println("VeraZip COMPLCODE: " + outhead.getCompletionCode());
        //System.out.println("VeraZip COMPLCODEDCS: " + outhead.getErrorMessage());

        // probably there is a better way to track a failure
        if (iResult != 1) {
            String strReason = SalesTaxService.msg.format("VERAZIP_FAILED_WITH_CODE", 
                                                          intobjResult.toString());
            
            if (outhead.getErrorMessage() != null)
                strReason = outhead.getErrorMessage();
            
            if (((iResult > 0) && (iResult < 17)) || (iResult == 80))
                throw new TaxwareMinorException(strReason);      
            
        }
        
        return(outhead);
    
    }
    
    /** Does all the stuff for verifying a zip that needs to be synchronized.
     * @exception TaxwareCriticalException Thrown if installation problem.
     * @exception TaxwareMinorException Thrown if error returned from VeraZip.
     */
    protected static synchronized OutputHeader calculateInternal(ZipRequest request,
                                                                 StringBuffer strbuf)
        throws TaxwareMinorException, TaxwareCriticalException {
        
        createInputStringBuffer(); // creates and clears
        request.writeFields(m_strbufInput, INPUT_GEN_RECORD_SIZE);
               
        OutputHeader outhead = callCalculate(m_strbufInput.toString(),
                                             strbuf);
        
        return(outhead);
        
    }
    
    /** Verify a city/state/zipcode triplet when given a ZipRequest object.
     *
     * @param request a ZipRequest object
     * @return the ZipResult object representing the result
     * @exception TaxwareCriticalException Thrown if installation problem.
     * @exception TaxwareMinorException Thrown if error returned from VeraZip.
     */
    public static ZipResult calculate(ZipRequest request)
        throws TaxwareMinorException, TaxwareCriticalException {
        
        StringBuffer strbuf = new StringBuffer();
        
        // do all the synhronized, shared resource kind of stuff
        OutputHeader outhead = calculateInternal(request, strbuf);
                
        int numRecs = outhead.getNumberOfRecords();
        ZipResultItem [] rgZipResultItem = null;        
        
        int iResult = outhead.getCompletionCode();
        String strReason = null;
        
        if (iResult != 0) {
            strReason = SalesTaxService.msg.getString("VERAZIP_" + iResult);
            
            if (((iResult > 0) && (iResult <= 10)) || (iResult == 80))
                throw new TaxwareCriticalException(strReason);
            
        }
        
        if (numRecs > 0) {
            rgZipResultItem = new ZipResultItem[numRecs];
            
            for (int i = 0; i < numRecs; i++) {
                rgZipResultItem[i] = new ZipResultItem();
                rgZipResultItem[i].parseFields(strbuf.toString(), 
                                               OUTPUT_GEN_RECORD_SIZE + 
                                               (OUTPUT_IND_RECORD_SIZE * i));
            }
            
        }
        
        ZipResult zipresult = new ZipResult(request, iResult, strReason,
                                            rgZipResultItem);
        return(zipresult);
    
    }
    

    /** A wrapper used for testing. */
    public static final ZipResult QuickVerifyZip(String strCity, String strState,
                                                 String strZip, boolean bDumpResults) 
    {
                                               
        ZipRequest request = new ZipRequest(strState, strZip, strCity, null, null, null);
 
        ZipResult zipresult = null;
        try {
            zipresult = calculate(request);
        } catch (TaxwareMinorException except) {
            System.out.println(SalesTaxService.msg.format("VERAZIPMinorQuickVerify", except.toString()));
        } catch (TaxwareCriticalException except) {
            System.out.println(SalesTaxService.msg.format("VERAZIPCriticalQuickVerify", except.toString()));
        }
        
        if (bDumpResults) {
            if ((null != zipresult) && (zipresult.getResultItemCount() > 0)) {
                int numRecs = zipresult.getResultItemCount();
                for (int j=0; j < numRecs; j++) {
                    System.out.println(zipresult.getDescriptionAt(j));
                }
            }
        }
        
        return(zipresult);
    }
    
    /** Perform some basic tests. */
    public static void main(String rgArgs[]) {
   
        if (rgArgs.length == 3) {
            QuickVerifyZip(rgArgs[0], rgArgs[1], rgArgs[2], true);
            return;
        }
        
        int cRequests = 1;
        
        for (int i = 0; i < cRequests; i++) {
            //QuickVerifyZip("Denver", "CO", "80202", true);
            QuickVerifyZip("Boston", "MA", "02215", true);
        }
        
    }

}


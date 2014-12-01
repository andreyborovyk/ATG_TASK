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
import java.lang.*;

/**
 * SalesTaxCaller provides a higher level interface to TaxWare's sales
 * calculation software. Since the shared library may not be re-entrant,
 * synchronize a number of the methods here just to be safe. <p>
 *
 * Each TaxWare request is made up of an InputHeader and some number of 
 * individual records. This interface sends one request at a time.<p>
 * Each TaxWare response is made up of an OutputHeader and some number
 * of individual records (should be equal to the number of individual input
 * records).
 *
 * The only function one should need to call directly is <i>CalculateTax</i>.
 *
 * @see TaxRequest
 * @see TaxResult
 * @author Michael Traskunov, taken and modified from Charles Morehead
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/SalesTaxCaller.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */


public class SalesTaxCaller {
  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/SalesTaxCaller.java#2 $$Change: 651448 $";

  /** length of a general (input prefix) record */
  static int INPUT_GEN_RECORD_SIZE = 60;

  /** length of a individual input record */
  static int INPUT_IND_RECORD_SIZE = 2592;

  /** Length of a general (output prefix) record. */
  static int OUTPUT_GEN_RECORD_SIZE = 284;
    
  /** Length of an individual output record. */
  static int OUTPUT_IND_RECORD_SIZE = 1704;
  
  /** StringBuffer re-used for each request. */
  protected static StringBuffer m_strbufInput; // input buffer

  /** Input header re-used for each request. */
  protected static InputHeader m_inputHeader; // input header

  /** Whether TaxWare's files are open or not. */
  protected static boolean m_bOpen = false;

  /** Method for calculating tax. */
  protected static Method m_methodCalculateTax;

  /** Argument array. */
  protected static Object[] m_rgobjArgs;

  /** TaxWare Open process indicator value */
  public static String OPEN_PROCESS_IND = "2";
  
  /** TaxWare Close process indicator value */
  public static String CLOSE_PROCESS_IND = "3";
   
  /* Input header */
  static protected class InputHeaderRecordDef extends RecordDef {
    InputHeaderRecordDef() {
      super(10);
    }
    protected void initializeFieldDefs() {
      // General input record layout
      addFieldDef("NUMBERINRECORDS", NUMLINEITEMSIZE,
                                    FieldDefinition.INT_FTYPE);
      addFieldDef("COMPRESSIONIND", CHARSIZE);
      addFieldDef("COMPRESSIONCHAR1", CHARSIZE);
      addFieldDef("COMPRESSIONCHAR2", CHARSIZE);
      addFieldDef("OPENCLOSEPROCESSIND", CHARSIZE);
      addFieldDef("RESERVED1", 50);
    }
  }
  
  static class InputHeader extends FieldSet {
    protected static RecordDef m_recordDef;

    protected synchronized void createRecordDef() {
      if (null != m_recordDef)
        return;
      m_recordDef = new InputHeaderRecordDef();
    }

    RecordDef getRecordDef() {
      if (null == m_recordDef) {
        createRecordDef();
      }
      return(m_recordDef);
    }
  }
    
    
  /** Output header */
  static protected class OutputHeader extends FieldSet {
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
          return((int)getLongFieldValue("NUMBEROUTRECORDS"));
      }
      // taxware 3.1 returns null if nothing went wrong (a bug?)
      // taxware 3.2 returns zero if nothing went wrong
      // this is a reason why this value is parsed as string
      public String getCommonCompletionCode() {
          return(getStringFieldValue("COMCOMPLCODE"));
      }
      public String getCompletionCodeDsc() {
          return(getStringFieldValue("COMPLCODEDSC"));
      }
      
  }
  
  static class OutputHeaderRecordDef extends RecordDef {
    OutputHeaderRecordDef() {
      super(15);
    }
    
    protected void initializeFieldDefs() {
      // General output record layout
      addFieldDef("NUMBEROUTRECORDS", NUMLINEITEMSIZE,
                  FieldDefinition.INT_FTYPE);
      addFieldDef("COMCOMPLCODE", COMPCODESIZE,
                  FieldDefinition.INT_FTYPE);
      addFieldDef("COMPLCODEDSC", NEWMESSAGESIZE);
      addFieldDef("MERCHANTNAME", COMPIDSIZE);
      addFieldDef("COMPRESSIND", CHARSIZE);
      addFieldDef("COMPRESSCHAR1", CHARSIZE);
      addFieldDef("COMPRESSCHAR2", CHARSIZE);
      addFieldDef("OPENCLOSEPROCIND", CHARSIZE);
      addFieldDef("RESERVED1OUT", 50);
    }
  }

  /** Create our static input string buffer to hold the input header +
   * individual records. This is created every time it is used.
   * The input header is created only once, but some of the fields get set 
   * every time we call to Taxware, for example the number of records.
   */
  protected synchronized static void createInputStringBuffer(int cRequests) {
    
    // create the input string buffer every time, since the cRequests number
    // changes from 0 (TaxWare Open or Close) to N (calculating taxes)
    m_strbufInput = createBuffer(INPUT_GEN_RECORD_SIZE, 
                                 INPUT_IND_RECORD_SIZE, cRequests);
 
    // allocate input header only once,
    // fields can be reset at a later stage
    if (null == m_inputHeader)
        createInputHeader();

    m_inputHeader.setFieldValue("NUMBERINRECORDS", cRequests);
    m_inputHeader.writeFields(m_strbufInput, 0);
  }
    

  /** Create the InputString buffer */
  protected static StringBuffer createBuffer(int header, int record, int cRequests) {
    int cBufSize = header + (record * cRequests);
                             
    StringBuffer strbuf = new StringBuffer(cBufSize);

    // now, let's pad the whole thing with spaces.
    for (int i = 0; i < cBufSize; i++) {
        strbuf.append(' ');
    }
      
    return(strbuf);
  }
    

  /** Create our input header. The inpute header is re-used for each call. */
  protected synchronized static void createInputHeader() {
    if (null != m_inputHeader)
      return;
    
    m_inputHeader = new InputHeader();
    m_inputHeader.setFieldValue("COMPRESSIONIND", "0");
    m_inputHeader.setFieldValue("COMPRESSIONCHAR1", "0");
    m_inputHeader.setFieldValue("COMPRESSIONCHAR2", "0");
    m_inputHeader.setFieldValue("OPENCLOSEPROCESSIND", "1");
    m_inputHeader.setFieldValue(
      "RESERVED1", "RESERVED FOR FUTURE USE 50 CHARS LONG*************");
  }


  /** Called to open or close taxware. Taxware has the option
   * to load data files and keep them in memory. Open loads
   * data into memory, and close flushes it properly. Called by
   * SalesTaxService.
   *
   * @see SalesTaxService
   * @exception TaxwareCriticalException Thrown if installation problem.
   * @exception TaxwareMinorException Thrown if error returned from TaxWare.
   */
  protected synchronized static void openOrCloseTaxWare(boolean bOpen) throws
  TaxwareCriticalException, TaxwareMinorException {
    if (bOpen == m_bOpen) {
      return;
    }
    
    // makes sure out input header exists (no records)
    createInputStringBuffer(0);

    // set the open/close process indicator to "open" or "close"
    m_inputHeader.setFieldValue("OPENCLOSEPROCESSIND", bOpen ? OPEN_PROCESS_IND : CLOSE_PROCESS_IND);
    m_inputHeader.writeFields(m_strbufInput, 0);

    StringBuffer m_strbufOutput = new StringBuffer();
   
    // following will return an exception if anything goes wrong
    OutputHeader outhead = callCalculate(m_strbufInput.toString(), m_strbufOutput, null, false);
      
    // remember whether it was open or close
    m_bOpen = bOpen;

    // set the open/close process indicator to "process only"
    m_inputHeader.setFieldValue("OPENCLOSEPROCESSIND", " ");
  }


  /** Close down taxware. Called by SalesTaxService.
   * @exception TaxwareCriticalException Thrown if installation problem.
   * @exception TaxwareMinorException Thrown if error returned from TaxWare.
   */
  public static synchronized void closeTaxWare()
    throws TaxwareCriticalException, TaxwareMinorException {
      openOrCloseTaxWare(false);
  }


  /** Open taxware. Called by SalesTaxService.
   *
   * @see SalesTaxService
   * @exception TaxwareCriticalException Thrown if installation problem.
   * @exception TaxwareMinorException Thrown if error returned from TaxWare.
   */
  public static synchronized void openTaxWare()
    throws TaxwareCriticalException, TaxwareMinorException {
    openOrCloseTaxWare(true);
  }


  /** Do the dynamic call into the taxcommon class.
   * @exception TaxwareMinorException Thrown if installation problem.
   * @exception TaxwareCriticalException Thrown if error returned from TaxWare.
   */
  protected static synchronized OutputHeader callCalculate(
    String stringRequest, StringBuffer strbuf, StringBuffer intobj, boolean vzip) throws
    TaxwareCriticalException, TaxwareMinorException {
      
      
    if (m_methodCalculateTax == null) {
      
      try {
        Class classTaxCommon = Class.forName("taxcommon");
        Class rgclassArgs[] = { Class.forName("java.lang.String"),
                                Class.forName("java.lang.StringBuffer") };
        m_methodCalculateTax = classTaxCommon.getDeclaredMethod(
          "CalculateTax", rgclassArgs);
      } catch (ClassNotFoundException excpt) {
          throw new TaxwareCriticalException(excpt.toString());
      } catch (NoSuchMethodException excpt) {
          throw new TaxwareCriticalException(excpt.toString());
      } catch (UnsatisfiedLinkError excpt) {
          throw new TaxwareCriticalException(excpt.toString());
      }
      m_rgobjArgs = new Object[2];
    
    }
    
    m_rgobjArgs[0] = stringRequest;
    m_rgobjArgs[1] = strbuf;
    
    Integer intobjResult = null;

    try {
        intobjResult = (Integer)m_methodCalculateTax.invoke(null, m_rgobjArgs);
    } catch (InvocationTargetException excpt) {
        throw new TaxwareCriticalException(excpt.toString());
    } catch (IllegalAccessException excpt) {
        throw new TaxwareCriticalException(excpt.toString());
    }
    
    if (vzip) {
        intobj.append(intobjResult.toString());
        return null;
    }

    OutputHeader outhead = new OutputHeader();
    outhead.parseFields(strbuf.toString(), 0);
    
    //System.out.println("Sales RESULT: " + intobjResult.intValue());
    //System.out.println("Sales COMCOMPLCODE: " + outhead.getCommonCompletionCode());
    //System.out.println("Sales COMCOMLCODEDSC: " + outhead.getCompletionCodeDsc());
    
    if (intobjResult.intValue() != 1) {
        String error = null;
        
        if (outhead.getCommonCompletionCode() != null)
            error = SalesTaxService.msg.format("TAXWARE_CALC_FAILED_WITH_CODE", 
                                               outhead.getCommonCompletionCode());
        else
            error = SalesTaxService.msg.format("TAXWARE_CALC_FAILED_WITH_CODE", 
                                               intobjResult.toString());
        if (outhead.getCompletionCodeDsc() != null)
            error = outhead.getCompletionCodeDsc();
        
        throw new TaxwareMinorException(error);
        
    }
    
    return(outhead);
  }


  /** Does all the stuff for calculating tax that needs to be synchronized.
   *
   * @exception TaxwareCriticalException Thrown if installation problem.
   * @exception TaxwareMinorException Thrown if error returned from TaxWare.
   */
  protected static synchronized OutputHeader calculateTaxInternal(TaxRequest[] request,
                                                                  StringBuffer strbuf)
      throws TaxwareMinorException, TaxwareCriticalException {
      
      int size = request.length;
      // create the input buffer
      createInputStringBuffer(size);
      
      // write all request objects into the input buffer
      for (int i = 0; i < size; i++) {
          request[i].writeFields(m_strbufInput, INPUT_GEN_RECORD_SIZE + 
                                 (INPUT_IND_RECORD_SIZE * i));
      }

      // send the input buffer to taxware and return the response
      // in the output buffer
      OutputHeader outhead = callCalculate(m_strbufInput.toString(), strbuf, null, false);  
      return(outhead);
      
  }

  /** Calculate the SalesTax (and a myriad of additiona tax information when
   * given a TaxRequest object).
   *
   * @param request a TaxRequest object
   * @return the TaxResult object representing the result
   * @exception TaxwareCriticalException Thrown if installation problem.
   * @exception TaxwareMinorException Thrown if error returned from TaxWare.
   */
  public static TaxResult[] calculateTax(TaxRequest[] request) 
      throws TaxwareMinorException, TaxwareCriticalException {

      HashSet s = new HashSet(16);
      s.add(Long.valueOf(0L));
      return (calculateTax (request, s));
  }

  public static TaxResult[] calculateTax(TaxRequest[] request,
					 Set pNonFatalCompCodes) 
      throws TaxwareMinorException, TaxwareCriticalException {
       
      // output buffer to hold the response
      StringBuffer m_strbufOutput = new StringBuffer();

      // do all the synhronized, shared resource kind of stuff
      // output buffer will contain the response from taxware
      OutputHeader outhead = calculateTaxInternal(request, m_strbufOutput);
      
      // the number of input records must be equal to the number
      // of output records, but get the number from taxware
      int size = outhead.getNumberOfRecords();
      
      // int size = request.length;
      TaxResult[] taxresult = new TaxResult[size];
      // currently we parse ALL the fields, which probably not
      // really necessary in many cases
      for (int i = 0; i < size; i++) {
          taxresult[i] = new TaxResult();
          taxresult[i].parseFields(m_strbufOutput.toString(), 
                                   OUTPUT_GEN_RECORD_SIZE  + (OUTPUT_IND_RECORD_SIZE * i));
          // check for any taxware returned error messages 
          if (!pNonFatalCompCodes.contains(Long.valueOf(taxresult[i].getGeneralCompletionCode()))) {
              //System.out.println("General COMPLCODE: " + taxresult[i].getGeneralCompletionCode());
              throw new TaxwareMinorException(taxresult[i].getGeneralCompletionCodeDescription());
          }
      }
      
      return(taxresult);
  }
    
  /* for testing routines */
  protected static double getTotalTaxAmount(TaxResult taxresult) {
      double d = taxresult.getCountryTaxAmount();
      double d1 = taxresult.getTerritoryTaxAmount();
      double d2 = taxresult.getProvinceTaxAmount() + taxresult.getSecProvinceTaxAmount();
      double d3 = taxresult.getCountyTaxAmount() + taxresult.getSecCountyTaxAmount();
      double d4 = taxresult.getCityTaxAmount() + taxresult.getSecCityTaxAmount();
      double d5 = d + d1 + d2 + d3 + d4;
      return d5;
  }
    
    
  /** Run some simple tests */
  public static void main(String args[]) {
    
    Date date = new Date(System.currentTimeMillis());

    int cRequests = Integer.parseInt(args[0]);

    // first, try without leaving open
    long startTime = System.currentTimeMillis();

    TaxRequest[] request = new TaxRequest[cRequests];
    for (int i = 0; i < cRequests; i++) {
        
        if (i % 2 == 0)
            request[i] = new TaxRequest("US", "Boston", "MA", "02199",
                                        "US", "Boston", "MA", "02199",
                                        "US", "Boston", "MA", "02199",
                                        "US", "Boston", "MA", "02199",
                                        10000, 10000, 0, date,
                                        "WEEWIDGETS");
        else
            request[i] = new TaxRequest("US", "Boston", "MA", "02199",
                                        "US", "Center", "CO", "81125",
                                        "US", "Boston", "MA", "02199",
                                        "US", "Center", "CO", "81125",
                                        10000, 0, 0, date,
                                        "WEEWIDGETS");
        
        //request[i].setDstGeoCode("00");
        //request[i].setPOAGeoCode("00");
        //System.out.println("Setting the TaxWare object[" + i + "]");
        
    }
    
    TaxResult taxresult[] = null;
    try {
        taxresult = calculateTax(request);
    } catch (TaxwareMinorException except) {
        System.out.println(except.toString());
    } catch (TaxwareCriticalException except) {
        System.out.println(except.toString());
    }
   
    double total = 0;
    if (taxresult != null)
        for (int i = 0; i < cRequests; i++) {
            double ind_tax = getTotalTaxAmount(taxresult[i]);
            System.out.println("The sales/use tax[" + i + "]: " + ind_tax);
            total += ind_tax;
        }
    System.out.println("The sales/use tax: " + total);
    if (total != 0)
        return;
    
    long endTime = System.currentTimeMillis();
    Long tDiff = Long.valueOf (endTime - startTime);
    Long tDiffReg = Long.valueOf ( (endTime - startTime) /cRequests);
    
    Object[] tArgs = {tDiff, tDiffReg};
    //System.out.println(SalesTaxService.msg.format("TXWRSalesTaxCallerTime", tArgs));
    
    try {
        openTaxWare();
    } catch (TaxwareMinorException except) {
        System.out.println(except.toString());
    } catch (TaxwareCriticalException except) {
        System.out.println(except.toString());
    }
    
    startTime = System.currentTimeMillis();
    
    for (int i = 0; i < cRequests; i++) {

      request[i] = new TaxRequest("US", "Boston", "MA", "02199",
                                  "US", "Center", "CO", "81125",
                                  "US", "Boston", "MA", "02199",
                                  "US", "Center", "CO", "81125",
                                  20000, 20000, 1000, date,
                                  "WEEWIDGETS");
      
      request[i].setDstGeoCode("01");
      request[i].setPOAGeoCode("01");
    }
    
    taxresult = null;
    try {
        taxresult = calculateTax(request);
    } catch (TaxwareMinorException except) {
        System.out.println(except.toString());
    } catch (TaxwareCriticalException except) {
        System.out.println(except.toString());
    }
    
    total = 0;
    if (taxresult != null)
        for (int i = 0; i < cRequests; i++) {
            double ind_tax = getTotalTaxAmount(taxresult[i]);
            System.out.println("The sales/use tax[" + i + "]: " + ind_tax);
            total += ind_tax;
        }
    System.out.println("The sales/use tax: " + total);
    
    endTime = System.currentTimeMillis();
    
    try {
        closeTaxWare();
    } catch (TaxwareMinorException except) {
        System.out.println(except.toString());
    } catch (TaxwareCriticalException except) {
        System.out.println(except.toString());
    }      
    
    tDiff = Long.valueOf (endTime - startTime);
    tDiffReg = Long.valueOf ( (endTime - startTime)/cRequests);
    
    Object[] fArgs = {tDiff, tDiffReg};
    //System.out.println(SalesTaxService.msg.format("TXWRSalesTaxCallerTime", fArgs));
    
  }
}


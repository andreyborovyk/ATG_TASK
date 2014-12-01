/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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
/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.droplet;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;

import java.io.*;
import java.lang.*;
import java.lang.reflect.*;


/**
 * The Invoke servlet invokes a named method for a particular 
 * object and arguements. Any results are returned through the
 * return parameter. See the documentation on java.lang.reflect.Method 
 * for more details.
 * <p>
 * It takes as parameters:
 *
 * INPUT PARAMS
 *
 * <dl>
 * <dt>object
 * <dd>The instance object to invoke the <i>method</i>.
 *
 * <dt>method 
 * <dd>The method to invoke. Must be either an instance of 
 * java.lang.reflect.Method or the name of a public method 
 * declared in the value <i>object</i> as a String. The 
 * java.lang.Class.getMethod() method is used to find the named
 * method.
 *
 * <dt>args
 * <dd>An array of arguments to <i>method</i>. If null then the method
 * assumes it takes no arguments. If the value is of type Object the method
 * assumes it takes a single argument. If the value if an array of Objects 
 * the method assumes it takes these Object as arguments.
 * </dl>
 *
 * OUTPUT PARAMS
 *
 * <dl>
 * <dt>return
 * <dd>The object returned when invoking the <i>method</i> or null 
 * if the method is of return type void.
 *
 * <dt>throwable
 * <dd>The exception that was thrown when the method was executed or
 * null if no exception was thrown.
 * </dl>
 *
 * OPARAMS
 *
 * <dl>
 * <dt>output
 * <dd> Serviced if there were no errors.

 * <dt>catch
 * <dd> Serviced if errors occurred during the method call. The throwable
 * parameter is set to the exception that was thrown.
 *
 * <dt>finally
 * <dd> Always serviced after the method call. The throwable
 * parameter is set to the exception thrown.
 * </dl>
 *
 * @author Andrew Rickard
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Invoke.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class Invoke extends DynamoServlet {
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
	"$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Invoke.java#2 $$Change: 651448 $";
    
    //-------------------------------------
    // Constants
    //-------------------------------------
    //Input Params
    public final static String OBJECT           = "object"; //instance of java.lang.Object(Duh!)
    public final static String METHOD           = "method"; //instance of java.lang.reflect.Method     
    public final static String ARGS             = "args";   //

    //Return Params
    public final static String RETURN           = "return";
    public final static String THROWABLE        = "throwable"; //Set if Error occurs when invoked
        
    //oparams
    public final static String OUTPUT           = "output"; 
    public final static String CATCH            = "catch";   //Serviced if Error occurs
    public final static String FINALLY          = "finally"; //Always Serviced

    //-------------------------------------
    // Member Variables
    //-------------------------------------

    //-------------------------------------
    // Properties
    //-------------------------------------
    
    //-------------------------------------
    // Constructors
    //-------------------------------------
    
    /**
     * Constructs an instanceof Invoke
     */
    public Invoke() {
    }

    /**
     * Service thre request
     */    
    public void service(DynamoHttpServletRequest request, 
			DynamoHttpServletResponse response) 
	throws ServletException, IOException {
	Method method = null;
	Object obj = null;
	Object [] args = null;
	Object value;
	
	//Get Object
	obj = request.getLocalParameter(OBJECT);
	
	//Get Args
	if((value = request.getLocalParameter(ARGS)) != null) {
	    if(value.getClass().isArray()) {
		args = (Object [])value;
	    } else {
		//Be nice and stick it in an array for them
		args = new Object[1]; 
		args[0] = value;
	    }
	} else {
	    args = new Object[0]; //no args
	}

	//Get Method
	if((value = request.getLocalParameter(METHOD)) != null) {
	    if(value instanceof Method) { 
		method = (Method)value;
	    }
	    if(value instanceof String) {
		String methodName = (String)value;
				      
		//Be nice and try looking it up in obj's class
		if(obj != null) {
		    Class cls = obj.getClass();
		    if(cls != null) {
			Class [] argTypes = null;
			
			//Build argTypes
			if(args != null) {
			    argTypes = new Class[args.length];
			    for(int i = 0 ; i < args.length ; i++) {
				argTypes[i] = (args[i]).getClass();
			    }
			}
			
			try {
			    if(isLoggingDebug()) {
				StringBuffer buff = new StringBuffer();
				
				buff.append("Invoke: Getting Method: ");
				if(obj != null)
				    buff.append(obj);
				buff.append(".");
				buff.append(methodName);
				buff.append("(");
				if(argTypes != null) {
				    for(int i = 0 ; i < argTypes.length ; i++) {
					buff.append(argTypes[i].getName());
					if(i != (argTypes.length - 1))
					    buff.append(",");
				    }
				}
				buff.append(")");
				logDebug(buff.toString());
			    }
			    
			    //Find the method
			    method = cls.getMethod(methodName,argTypes); // Note: finds and exact match
			} catch (NoSuchMethodException nsme) {
			    if(isLoggingError())
				logError(nsme);				    
			} catch (SecurityException se) {
			    if(isLoggingError())
				logError(se);			    
			}	
		    }
		}
	    }
	}
		
	//Service It!
	serviceInvoke(request,response,obj,method,args);

	//Service FINALLY
	if(request.getLocalParameter(THROWABLE) != null)
	    serviceFinally(request,response);
    }
    
    /**
     * Services the CATCH oparam if there's an exception
     */
    protected void serviceCatch(DynamoHttpServletRequest request, 
				DynamoHttpServletResponse response,
				Throwable exception) throws ServletException, IOException {
	
	//Set THROWABLE Param
	request.setParameter(THROWABLE,exception);
	
	//Service CATCH
	request.serviceLocalParameter(CATCH,request,response);
    }

    /**
     * Services the FINALLY oparam if there's an exception
     */
    protected void serviceFinally(DynamoHttpServletRequest request, 
				  DynamoHttpServletResponse response) throws ServletException, IOException {			
	//Service FINALLY
	request.serviceLocalParameter(FINALLY,request,response);
    }

    /**
     * Handles the Invoke call
     */
    protected void serviceInvoke(DynamoHttpServletRequest request, 
				 DynamoHttpServletResponse response,				 
				 Object obj,
				 Method method,
				 Object [] args) throws ServletException, IOException {
	
	Object ret = null;
		
	try {	    
	    if(isLoggingDebug()) {
		StringBuffer buff = new StringBuffer();
		
		buff.append("Invoke: Invoking: ");
		if(obj != null)
		    buff.append(obj);
		buff.append(".");
		if(method != null)
		    buff.append(method.getName());
		buff.append("(");
		if(args != null) {
		    for(int i = 0 ; i < args.length ; i++) {
			buff.append(args[i]);
			if(i != (args.length - 1))
			    buff.append(",");
		    }
		}
		buff.append(")");
		logDebug(buff.toString());
	    }
	   	    
	    // Invoke!
	    ret = method.invoke(obj,args);

	    if(isLoggingDebug()) {
		StringBuffer buff = new StringBuffer();
		
		buff.append("Invoke: Invoked: ");
		if(obj != null)
		    buff.append(obj);
		buff.append(".");
		if(method != null)
		    buff.append(method.getName());
		buff.append("(");
		if(args != null) {
		    for(int i = 0 ; i < args.length ; i++) {
			buff.append(args[i]);
			if(i != (args.length - 1))
			    buff.append(",");
		    }
		}
		buff.append(")");
		logDebug(buff.toString());
	    }
		

	    //Set RETURN param
	    request.setParameter(RETURN,ret);
	    
	    //Service OUTPUT 
	    request.serviceLocalParameter(OUTPUT,request,response);

	} catch (NullPointerException npe) {
	    if(isLoggingError())
		logError(npe);
	   
	} catch (IllegalArgumentException iae) {
	    if(isLoggingError())
		logError(iae);
	   
	} catch (IllegalAccessException iae) {
	    if(isLoggingError())
		logError(iae);	
	   
	} catch (InvocationTargetException ite) {
	    //Method threw and Exception
	    if(isLoggingError())
		logError(ite);
	    serviceCatch(request,response,ite.getTargetException());
	}
	
    }
	       
} // end of class

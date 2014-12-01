/*
** Copyright (c) 1997 by Tim Endres
** 
** This program is free software.
** 
** You may redistribute it and/or modify it under the terms of the GNU
** General Public License as published by the Free Software Foundation.
** Version 2 of the license should be included with this distribution in
** the file LICENSE, as well as License.html. If the license is not
** included	with this distribution, you may find a copy at the FSF web
** site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
** Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
**
** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
** REDISTRIBUTION OF THIS SOFTWARE. 
** 
*/

package com.ice.util;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.net.*;


/**
 * The UserProperties class.
 *
 * @version $Change: 651448 $
 * @author Tim Endres,
 *    <a href="mailto:time@ice.com">time@ice.com</a>.
 */

public abstract class
UserProperties
	{
	private static final String		RCS_ID = "$Change: 651448 $$DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $";
	private static final String		RCS_NAME = "$Name:  $";
	private static final String		RCS_REV = "$Change: 651448 $";

	private static final String		PREFIX_PROPERTY = "propertyPrefix";
	private static final String		DEFAULTS_RSRC_NAME = ".com.ice.global.defaultsResource.";

	private static final String		GLOBAL_RSRCLIST_NAME = ".com.ice.global.propertyResourceList";
	private static final String		GLOBAL_RSRC_PREFIX = ".com.ice.global.propertyResource.";

	private static final String		APP_RSRCLIST_NAME = ".com.ice.local.propertyResourceList";
	private static final String		APP_RSRC_PREFIX = ".com.ice.local.propertyResource.";

	private static final String		LOCAL_PROPERTY = "global.localPropertyFile";
	private static final String		LOCAL_DEFAULT = "properties.txt";


	private static boolean		debug;
	private static boolean		verbose;

	private static String		osname;
	private static String		userName;
	private static String		userHome;

	private static String		prefix;

	private static String		osSuffix;
	private static String		userSuffix;

	private static String		defaultsResource;
	private static String		localPropertyFile;

	static
		{
		UserProperties.debug = false;
		UserProperties.verbose = false;

		UserProperties.prefix = null;

		UserProperties.defaultsResource = null;
		UserProperties.localPropertyFile = null;
		
		UserProperties.osname = System.getProperty( "os.name" );
		UserProperties.userName = System.getProperty( "user.name" );
		UserProperties.userHome = System.getProperty( "user.home" );

		UserProperties.osSuffix = UserProperties.osname.replace( ' ', '_' );
		UserProperties.userSuffix = UserProperties.userName.replace( ' ', '_' );
		}

	static public String
	getOSName()
		{
		return UserProperties.osname;
		}

	static public String
	getUserHome()
		{
		return UserProperties.userHome;
		}

	static public String
	getUserName()
		{
		return UserProperties.userName;
		}

	static public void
	setDebug( boolean debug )
		{
		UserProperties.debug = debug;
		}

	static public void
	setVerbose( boolean verbose )
		{
		UserProperties.verbose = verbose;
		}

	static public void
	setLocalPropertyFile( String fileName )
		{
		UserProperties.localPropertyFile = fileName;
		}

	static public void
	setDefaultsResource( String rsrcName )
		{
		UserProperties.defaultsResource = rsrcName;
		}

	static public void
	setOSSuffix( String suffix )
		{
		UserProperties.osSuffix = suffix;
		}

	static public void
	setUserSuffix( String suffix )
		{
		UserProperties.userSuffix = suffix;
		}

	static public void
	setPropertyPrefix( String prefix )
		{
		if ( prefix.endsWith( "." ) )
			UserProperties.prefix = prefix;
		else
			UserProperties.prefix = prefix + ".";
		}

	static public String
	getPropertyPrefix()
		{
		return UserProperties.prefix;
		}

	static public Font
	getFont( String name, Font defaultFont )
		{
		return
			Font.getFont
				( UserProperties.fullPropertyName( name ),
					defaultFont );
		}

	static public Color
	getColor( String name, Color defaultColor )
		{
		return
			Color.getColor
				( UserProperties.fullPropertyName( name ),
					defaultColor );
		}

	static public String
	fullPropertyName( String name )
		{
		return UserProperties.prefix + name;
		}

	/**
	 * Retrieve a system string property.
	 * Returns a provided default value if the property
	 * is not defined.
	 *
	 * @param name The name of the property to retrieve.
	 * @param defval A default string value.
	 * @return The string value of the named property.
	 */

	static private String
	getOverridableProperty( String name, String defval )
		{
		String		value = null;
		String		overName = null;
		String		fullName = null;

		if ( name.startsWith( "." ) )
			fullName = name.substring(1);
		else
			fullName = UserProperties.fullPropertyName( name );

		if ( fullName.endsWith( "." ) )
			{
			fullName = fullName.substring( 0, fullName.length() );
			value = System.getProperty( fullName, defval );
			if ( UserProperties.debug )
				System.err.println
					( "UserProperties.getOverridableProperty: "
						+ fullName + " = '" + value + "'" );
			return value;
			}

		if ( UserProperties.osSuffix != null
				&& UserProperties.userSuffix != null )
			{
			overName =
				fullName + "." + UserProperties.osSuffix
				 + "." + UserProperties.userSuffix;
			value = System.getProperty( overName, null );
			if ( UserProperties.debug )
				System.err.println
					( "UserProperties.getOverridableProperty: "
						+ overName + " = '" + value + "'" );
			if ( value != null )
				return value;
			}

		if ( UserProperties.userSuffix != null )
			{
			overName = fullName + "." + UserProperties.userSuffix;
			value = System.getProperty( overName, null );
			if ( UserProperties.debug )
				System.err.println
					( "UserProperties.getOverridableProperty: "
						+ overName + " = '" + value + "'" );
			if ( value != null )
				return value;
			}

		if ( UserProperties.osSuffix != null )
			{
			overName = fullName + "." + UserProperties.osSuffix;
			value = System.getProperty( overName, null );
			if ( UserProperties.debug )
				System.err.println
					( "UserProperties.getOverridableProperty: "
						+ overName + " = '" + value + "'" );
			if ( value != null )
				return value;
			}

		if ( value == null )
			{
			value = System.getProperty( fullName, null );
			if ( UserProperties.debug )
				System.err.println
					( "UserProperties.getOverridableProperty: "
						+ fullName + " = '" + value + "'" );
			}

		if ( value == null )
			{
			value = defval;
			if ( UserProperties.debug )
				System.err.println
					( "UserProperties.getOverridableProperty: "
						+ name + " defaulted to '" + value + "'" );
			}

		return value;
		}

	/**
	 * Retrieve a system string property.
	 * Returns a provided default value if the property
	 * is not defined.
	 *
	 * @param name The name of the property to retrieve.
	 * @param defval A default string value.
	 * @return The string value of the named property.
	 */

	static public String
	getProperty( String name, String defval )
		{
		String result =
			UserProperties.getOverridableProperty
				( name, defval );
		return result;
		}

	/**
	 * Retrieve a system integer property.
	 * Returns a provided default value if the property
	 * is not defined.
	 *
	 * @param name The name of the property to retrieve.
	 * @param defval A default integer value.
	 * @return The integer value of the named property.
	 */

	static public int
	getProperty( String name, int defval )
		{
		int result = defval;

		String val = UserProperties.getProperty( name, null );

		if ( val != null )
			{
			try { result = Integer.parseInt( val ); }
				catch ( NumberFormatException ex )
					{ result = defval; }
			}

		return result;
		}

	/**
	 * Retrieve a system double property.
	 * Returns a provided default value if the property
	 * is not defined.
	 *
	 * @param name The name of the property to retrieve.
	 * @param defval A default double value.
	 * @return The double value of the named property.
	 */

	static public double
	getProperty( String name, double defval )
		{
		double result = defval;

		String val = UserProperties.getProperty( name, null );

		if ( val != null )
			{
			try { result = Double.valueOf( val ).doubleValue(); }
				catch ( NumberFormatException ex )
					{ result = defval; }
			}

		return result;
		}

	/**
	 * Retrieve a system boolean property.
	 * Returns a provided default value if the property
	 * is not defined.
	 *
	 * @param name The name of the property to retrieve.
	 * @param defval A default boolean value.
	 * @return The boolean value of the named property.
	 */

	static public boolean
	getProperty( String name, boolean defval )
		{
		boolean result = defval;

		String val = UserProperties.getProperty( name, null );

		if ( val != null )
			{
			if ( val.equalsIgnoreCase( "TRUE" ) )
				result = true;
			else if ( val.equalsIgnoreCase( "FALSE" ) )
				result = false;
			}

		return result;
		}

	/**
	 * Establishes critical default properties.
	 *
	 * @param props The system properties to add properties into.
	 */

	static public void
	defaultProperties( Properties props )
		{
		props.put( "com.ice.util.UserProperties.revision", "$Change: 651448 $" );
		props.put( "copyright", "Copyright (c) by Tim Endres" );

		//
		// Define the following to create a global
		// enterprise-wide defaults resource...
		// e.g.
		//
		// props.put
		//	( UserProperties.DEFAULTS_RSRC_NAME,
		//		"http://www.ice.com/properties/defaults.txt" );
		//
		}

	static public void
	addDefaultProperties( Properties props, Properties defaultProps )
		{
		Enumeration enm = defaultProps.keys();

		for ( ; enm.hasMoreElements() ; )
			{
			String key = null;

			try { key = (String)enm.nextElement(); }
				catch ( NoSuchElementException ex )
					{ key = null; }
			
			if ( key != null )
				{
				String value = (String) defaultProps.get( key );

				if ( value == null )
					{
					System.err.println
						( "UserProperties.addDefaultProperties: "
							+ "key '" + key + "' has null value!" );
					}
				else
					{
					props.put( key, value );
					}
				}
			}
		}

	/**
	 * Loads a properties stream into the System properties table.
	 *
	 * @param path The properties data's input stream.
	 * @param props The system properties to add properties into.
	 */

	static private boolean
	loadPropertiesStream( InputStream in, Properties props )
		throws IOException
		{
		props.load( in );
		return true;
		}

	/**
	 * Loads a named properties file into the System properties table.
	 *
	 * @param path The properties file's pathname.
	 * @param props The system properties to add properties into.
	 */

	static private boolean
	loadPropertiesFile( String path, Properties props )
		{
		FileInputStream	in;
		boolean			result = true;

		try { in = new FileInputStream( path ); }
		catch ( IOException ex )
			{
			System.err.println
				( "ERROR opening property file '" + path
					+ "' - " + ex.getMessage() );
			result = false;
			in = null;
			}

		if ( result )
			{
			try {
				UserProperties.loadPropertiesStream( in, props );
				}
			catch ( IOException ex )
				{
				System.err.println
					( "ERROR loading property file '" + path
						+ "' - " + ex.getMessage() );
				result = false;
				}
			}

		if ( in != null )
			{
			try { in.close(); }
			catch ( IOException ex )
				{
				System.err.println
					( "ERROR closing property file '" + path
						+ "' - " + ex.getMessage() );
				result = false;
				}
			}

		if ( result )
			System.err.println
				( "Loaded property file '" + path + "'." );

		return result;
		}

	/**
	 * Loads a named resource into the System properties table.
	 *
	 * @param path The properties resource's name.
	 * @param props The system properties to add properties into.
	 */

	static private InputStream
	openNamedResource( String name )
		throws java.io.IOException
		{
		InputStream	in = null;
		boolean		result = false;
		boolean		httpURL = true;
		URL			propsURL = null;

		//
		// UNDONE REVIEW
		// I really should be getting the URL's protocol, when it
		// is a "full" URL, and checking for the different possible
		// error returns for http, ftp, et.al.
		//
		try { propsURL = new URL( name ); }
			catch ( MalformedURLException ex )
				{
				httpURL = false;
				propsURL = null;
				}

		if ( propsURL == null )
			{
			propsURL = UserProperties.class.getResource( name );
			}

		if ( propsURL != null )
			{
			URLConnection urlConn =
				propsURL.openConnection();

			if ( httpURL )
				{
				String hdrVal = urlConn.getHeaderField(0);
				if ( hdrVal != null )
					{
					String code =
						HTTPUtilities.getResultCode( hdrVal );

					if ( code != null )
						{
						if ( ! code.equals( "200" ) )
							{
							throw new java.io.IOException
								( "status code = " + code );
							}
						}
					}
				}

			in = urlConn.getInputStream();
			}

		return in;
		}

	static private boolean
	loadPropertiesResource( String name, Properties props )
		{
		InputStream	in;
		boolean		result = false;

		try {
			in = UserProperties.openNamedResource( name );
			if ( in != null )
				{
				UserProperties.loadPropertiesStream( in, props );
				in.close();
				result = true;
				}
			}
		catch ( java.io.IOException ex )
			{
			System.err.println
				( "ERROR loading properties resource '"
					+ name + "' - " + ex.getMessage() );
			}

		return result;
		}

	private static void
	loadPropertyResourceList(
			String listPropName, String rsrcPrefix, Properties props )
		{
		String rsrcListStr = 
			UserProperties.getProperty( listPropName, null );

		if ( rsrcListStr != null )
			{
			String[] rsrcList =
				StringUtilities.splitString( rsrcListStr, ":" );
			
			for ( int rIdx = 0
					; rsrcList != null && rIdx < rsrcList.length
						; ++rIdx )
				{
				String rsrcTag = rsrcPrefix + rsrcList[rIdx];

				String rsrcName =
					UserProperties.getProperty( rsrcTag, null );

				if ( rsrcName != null )
					{
					boolean result =
						UserProperties.loadPropertiesResource
							( rsrcName, props );

					if ( ! result )
						{
						System.err.println
							( "ERROR loading property resource '"
								+ rsrcName + "'" );
						}
					}
				}
			}
		}

	// UNDONE
	// This routine need to use JNDI (?) to get a 'global' property
	// file name (typically on a network mounted volume) to read,
	// which should in turn set the name of the local property file.
	// JNDI should also set some 'critical' properties, such as
	// the important OTA hostnames, service ports, etc.
	
	// REVIEW
	// UNDONE
	// This routine should have a 'filter' that filters out all
	// global properties that do not start with prefix?
	
	/**
	 * Load all related properties for this application.
	 * This class method will look for a global properties
	 * file, loading it if found, then looks for a local
	 * properties file and loads that.
	 */

	static public void
	loadProperties( String packageName, Properties appProps )
		{
		boolean			result;
		File			propFile;
		String			propPath;
		String			propName;
		String			rsrcName;

		if ( UserProperties.debug )
			{
			UserProperties.printContext( System.err );
			}

		Properties sysProps =
			System.getProperties();

		if ( sysProps == null )
			return;

		UserProperties.defaultProperties( sysProps );

		//
		// ---- PROCESS THE DEFAULT PROPERTIES RESOURCE
		//
		rsrcName = UserProperties.defaultsResource;
		if ( rsrcName == null )
			{
			rsrcName =
				UserProperties.getProperty
					( UserProperties.DEFAULTS_RSRC_NAME, null );
			}

		if ( rsrcName != null )
			{
			result = 
				UserProperties.loadPropertiesResource
					( rsrcName, sysProps );

			System.err.println
				( "Loaded "
					+ ( result ? "the " : "no " )
					+ "default properties." );
			}

		//
		// ---- PROCESS THE APPLICATION DEFAULT PROPERTIES
		//
		if ( appProps != null )
			{
			UserProperties.addDefaultProperties
				( sysProps, appProps );
			}

		//
		// ---- PROCESS THE PREFIX PROPERTY
		//
		String newPrefix = UserProperties.prefix;
		if ( newPrefix == null )
			{
			UserProperties.getProperty
				( packageName + "."
					+ UserProperties.PREFIX_PROPERTY, null );

			if ( newPrefix != null )
				{
				UserProperties.setPropertyPrefix( newPrefix );
				if ( UserProperties.verbose )
					System.err.println
						( "Property prefix set to '" + newPrefix + "'" );
				}
			}

		//
		// ---- PROCESS THE GLOBAL PROPERTIES RESOURCES
		//
		UserProperties.loadPropertyResourceList
			( UserProperties.GLOBAL_RSRCLIST_NAME,
				UserProperties.GLOBAL_RSRC_PREFIX, sysProps );

		//
		// ---- PROCESS THE LOCAL PROPERTIES FILE
		//
		propPath = UserProperties.localPropertyFile;
		if ( propPath == null )
			{
			propPath =
				UserProperties.getProperty
					( UserProperties.LOCAL_PROPERTY,
						UserProperties.LOCAL_DEFAULT );
			}

		if ( propPath != null )
			{
			propFile = new File( propPath );
			if ( propFile.exists() )
				{
				result =
					UserProperties.loadPropertiesFile
						( propPath, sysProps );

				if ( ! result )
					{
					System.err.println
						( "ERROR loading local property file '"
							+ propPath + "'" );
					}
				}
			}

		//
		// ---- PROCESS THE GLOBAL PROPERTIES RESOURCES
		//
		UserProperties.loadPropertyResourceList
			( UserProperties.APP_RSRCLIST_NAME,
				UserProperties.APP_RSRC_PREFIX, sysProps );
		}

	public static void
	printContext( PrintStream out )
		{
		out.println
			( "os.name    = '" + UserProperties.osname + "'" );
		out.println
			( "user.name  = '" + UserProperties.userName + "'" );
		out.println
			( "user.home  = '" + UserProperties.userHome + "'" );

		out.println( "" );

		out.println
			( "prefix     = '" + UserProperties.prefix + "'" );
		out.println
			( "osSuffix   = '" + UserProperties.osSuffix + "'" );
		out.println
			( "userSuffix = '" + UserProperties.userSuffix + "'" );

		out.println( "" );
		}

	public static void
	printUsage( PrintStream out )
		{
		out.println
			( "Properties options:" );

		out.println
			( "   -propDebug             -- "
				+ "turns on debugging of property loading" );
		out.println
			( "   -propVerbose           -- "
				+ "turns on verbose messages during loading" );

		out.println
			( "   -propDefaults rsrcName -- "
				+ "sets default properties resource name" );
		out.println
			( "   -propFile path         -- "
				+ "sets application property file path" );

		out.println
			( "   -propOS suffix         -- "
				+ "sets the os suffix" );
		out.println
			( "   -propUser suffix       -- "
				+ "sets the user suffix" );
		out.println
			( "   -propPrefix prefix     -- "
				+ "sets application property prefix" );
		}

	static public String []
	processOptions( String [] args )
		{
		Vector newArgs = new Vector( args.length );

		for ( int iArg = 0 ; iArg < args.length ; ++iArg )
			{
			if ( args[iArg].equals( "-propPrefix" )
						&& (iArg + 1) < args.length )
				{
				UserProperties.setPropertyPrefix( args[++iArg] );
				}
			else if ( args[iArg].equals( "-propFile" )
						&& (iArg + 1) < args.length )
				{
				UserProperties.setLocalPropertyFile( args[++iArg] );
				}
			else if ( args[iArg].equals( "-propDefaults" )
						&& (iArg + 1) < args.length )
				{
				UserProperties.setDefaultsResource( args[++iArg] );
				}
			else if ( args[iArg].equals( "-propDebug" ) )
				{
				UserProperties.setDebug( true );
				}
			else if ( args[iArg].equals( "-propVerbose" ) )
				{
				UserProperties.setVerbose( true );
				}
			else if ( args[iArg].equals( "-propOS" )
						&& (iArg + 1) < args.length )
				{
				UserProperties.setOSSuffix( args[++iArg] );
				}
			else if ( args[iArg].equals( "-propUser" )
						&& (iArg + 1) < args.length )
				{
				UserProperties.setUserSuffix( args[++iArg] );
				}
			else
				{
				newArgs.addElement( args[iArg] );
				}
			}

		String[] result = new String[ newArgs.size() ];
		for ( int i = 0 ; i < newArgs.size() ; ++i )
			result[i] = (String) newArgs.elementAt(i);

		return result;
		}

	}



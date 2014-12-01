/*
** Tim Endres' utilities package.
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

import java.util.*;


public class
StringUtilities
	{

	static public String[]
	splitString( String splitStr, String delim )
		{
		int				i, count;
		String[]		result;
		StringTokenizer toker;

		toker = new StringTokenizer( splitStr, delim );

		count = toker.countTokens();

		result = new String[ count ];

		for ( i = 0 ; i < count ; ++i )
			{
			try { result[i] = toker.nextToken(); }
			catch ( NoSuchElementException ex )
				{
				result = null;
				break;
				}
			}

		return result;
		}

	public static String[]
	argumentSubstitution( String[] args, Hashtable vars )
		{
		StringBuffer argBuf = new StringBuffer();

		String[] result = new String[ args.length ];

		for ( int aIdx = 0 ; aIdx < args.length ; ++aIdx )
			{
			String argStr = args[ aIdx ];

			int index = argStr.indexOf( '$' );

			if ( index < 0 )
				{
				result[ aIdx ] = argStr;
				continue;
				}
			else
				{
				result[ aIdx ] =
					StringUtilities.stringSubstitution( argStr, vars );
				}
			}

		return result;
		}

	public static String
	stringSubstitution( String argStr, Hashtable vars )
		{
		StringBuffer argBuf = new StringBuffer();

		for ( int cIdx = 0 ; cIdx < argStr.length() ; )
			{
			char ch = argStr.charAt( cIdx );

			switch ( ch )
				{
				case '$':
					StringBuffer nameBuf = new StringBuffer();
					for ( ++cIdx ; cIdx < argStr.length() ; ++cIdx )
						{
						ch = argStr.charAt( cIdx );
						if ( ch == '_' || Character.isLetterOrDigit( ch ) )
							nameBuf.append( ch );
						else
							break;
						}

					if ( nameBuf.length() > 0 )
						{
						String value = (String)
							vars.get( nameBuf.toString() );

						if ( value != null )
							{
							argBuf.append( value );
							}
						}
					break;
				
				default:
					argBuf.append( ch );
					++cIdx;
					break;
				}
			}

		return argBuf.toString();
		}

	public static String[]
	parseArgumentString( String argStr )
		{
		String[] result = null;

		Vector vector =
			StringUtilities.parseArgumentVector( argStr );

		if ( vector != null && vector.size() > 0 )
			{
			result = new String[ vector.size() ];

			for ( int i = 0 ; i < result.length ; ++i )
				{
				result[i] = (String)vector.elementAt(i);
				}
			}

		return result;
		}

	public static Vector
	parseArgumentVector( String argStr )
		{
		Vector			result = new Vector();
		StringBuffer	argBuf = new StringBuffer();

		boolean backSlash = false;
		boolean matchSglQuote = false;
		boolean matchDblQuote = false;

		for ( int cIdx = 0 ; cIdx < argStr.length() ; ++cIdx )
			{
			char ch = argStr.charAt( cIdx );

			switch ( ch )
				{
				//
				// W H I T E S P A C E
				//
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					if ( backSlash )
						{
						argBuf.append( ch );
						backSlash = false; 
						}
					else if ( matchSglQuote || matchDblQuote )
						{
						argBuf.append( ch );
						}
					else if ( argBuf.length() > 0 )
						{
						result.addElement( argBuf.toString() );
						argBuf.setLength( 0 );
						}
					break;

				case '\\':
					if ( backSlash )
						{
						argBuf.append( "\\" );
						}
					backSlash = ! backSlash;
					break;

				case '\'':
					if ( backSlash )
						{
						argBuf.append( "'" );
						backSlash = false; 
						}
					else if ( matchSglQuote )
						{
						result.addElement( argBuf.toString() );
						argBuf.setLength( 0 );
						matchSglQuote = false;
						}
					else if ( ! matchDblQuote )
						{
						matchSglQuote = true;
						}
					break;

				case '"':
					if ( backSlash )
						{
						argBuf.append( "\"" );
						backSlash = false; 
						}
					else if ( matchDblQuote )
						{
						result.addElement( argBuf.toString() );
						argBuf.setLength( 0 );
						matchDblQuote = false;
						}
					else if ( ! matchSglQuote )
						{
						matchDblQuote = true;
						}
					break;

				default:
					if ( backSlash )
						{
						switch ( ch )
							{
							case 'b': argBuf.append( '\b' ); break;
							case 'f': argBuf.append( '\f' ); break;
							case 'n': argBuf.append( '\n' ); break;
							case 'r': argBuf.append( '\r' ); break;
							case 't': argBuf.append( '\t' ); break;

							default:
								char ch2 = argStr.charAt( cIdx+1 );
								char ch3 = argStr.charAt( cIdx+2 );
								if ( (ch >= '0' && ch <= '7')
										&& (ch2 >= '0' && ch2 <= '7')
										&& (ch3 >= '0' && ch3 <= '7') )
									{
									int octal =
										( ( (ch - '0') * 64 )
											+ ( (ch2 - '0') * 8 )
												+ (ch3 - '0') );
									argBuf.append( (char) octal );
									cIdx += 2;
									}
								else if ( ch == '0' )
									{
									argBuf.append( '\0' );
									}
								else
									{
									argBuf.append( ch );
									}
								break;
							}
						}
					else
						{
						argBuf.append( ch );
						}

					backSlash = false;
					break;
				}
			}

		if ( argBuf.length() > 0 )
			{
			result.addElement( argBuf.toString() );
			}

		return result;
		}
	
	}


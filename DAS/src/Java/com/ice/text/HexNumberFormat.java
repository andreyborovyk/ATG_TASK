/*
** Copyright (c) 1997 by Timothy Gerard Endres
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

package com.ice.text;

import java.lang.*;
import java.text.*;
import java.util.*;

/**
 * The HexNumberFormat class implements the code necessary
 * to format and parse Hexidecimal integer numbers.
 *
 * @version $Change: 651448 $
 * @author Timothy Gerard Endres, <a href="mailto:time@ice.com">time@ice.com</a>.
 * @see java.text.NumberFormat
 */

public class
HexNumberFormat	extends Format
	{																
	static public final String		RCS_ID = "$Change: 651448 $$DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $";
	static public final String		RCS_REV = "$Change: 651448 $";

	private static char[]		lowChars;
	private static char[]		uprChars;

	private int					count;
	private String				pattern;
	private static char[]		hexChars;

	static
		{
		HexNumberFormat.lowChars = new char[20];
		HexNumberFormat.uprChars = new char[20];
		
		HexNumberFormat.uprChars[0] = HexNumberFormat.lowChars[0] = '0';
		HexNumberFormat.uprChars[1] = HexNumberFormat.lowChars[1] = '1';
		HexNumberFormat.uprChars[2] = HexNumberFormat.lowChars[2] = '2';
		HexNumberFormat.uprChars[3] = HexNumberFormat.lowChars[3] = '3';
		HexNumberFormat.uprChars[4] = HexNumberFormat.lowChars[4] = '4';
		HexNumberFormat.uprChars[5] = HexNumberFormat.lowChars[5] = '5';
		HexNumberFormat.uprChars[6] = HexNumberFormat.lowChars[6] = '6';
		HexNumberFormat.uprChars[7] = HexNumberFormat.lowChars[7] = '7';
		HexNumberFormat.uprChars[8] = HexNumberFormat.lowChars[8] = '8';
		HexNumberFormat.uprChars[9] = HexNumberFormat.lowChars[9] = '9';
		HexNumberFormat.uprChars[10] = 'A'; HexNumberFormat.lowChars[10] = 'a';
		HexNumberFormat.uprChars[11] = 'B'; HexNumberFormat.lowChars[11] = 'b';
		HexNumberFormat.uprChars[12] = 'C'; HexNumberFormat.lowChars[12] = 'c';
		HexNumberFormat.uprChars[13] = 'D'; HexNumberFormat.lowChars[13] = 'd';
		HexNumberFormat.uprChars[14] = 'E'; HexNumberFormat.lowChars[14] = 'e';
		HexNumberFormat.uprChars[15] = 'F'; HexNumberFormat.lowChars[15] = 'f';
		}

    static public final HexNumberFormat
	getInstance()
		{
		return new HexNumberFormat( "XXXXXXXX" );
		}

	public
	HexNumberFormat( String pattern )
		{
		super();
		this.pattern = pattern;
		this.count = pattern.length();
		this.hexChars =
			( pattern.charAt(0) == 'X'
				? HexNumberFormat.uprChars
				: HexNumberFormat.lowChars );
		}

	public String
	format( int hexNum )
		throws IllegalArgumentException
		{
		FieldPosition pos = new FieldPosition(0);
		StringBuffer hexBuf = new StringBuffer(8);

		this.format( new Integer( hexNum ), hexBuf, pos );

		return hexBuf.toString();
		}

	public StringBuffer
	format( Object hexInt, StringBuffer appendTo, FieldPosition fieldPos )
		throws IllegalArgumentException
		{
		char[] hexBuf = new char[16];
		
		int end = fieldPos.getEndIndex();
		int beg = fieldPos.getBeginIndex();

		int hexNum = ((Integer) hexInt).intValue();

		for ( int i = 7 ; i >= 0 ; --i )
			{
			hexBuf[i] = this.hexChars[ (hexNum & 0x0F) ];
			hexNum = hexNum >> 4;
			}

		for ( int i = (8 - this.count) ; i < 8 ; ++i )
			{
			appendTo.append( hexBuf[i] );
			}

		return appendTo;
		}

	public int
	parse( String source )
		throws ParseException
		{
		throw new ParseException( "unimplemented!", 0 );
		}

	public Object
	parseObject( String source, ParsePosition pos )
		{
		return null;
		}

	}


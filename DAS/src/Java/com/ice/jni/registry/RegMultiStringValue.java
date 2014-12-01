/*
** Java native interface to the Windows Registry API.
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

package com.ice.jni.registry;

import java.io.PrintWriter;


/**
 * The RegMultiStringValue class represents a multiple
 * string, or string array, value in the registry
 * (REG_MULTI_SZ).
 *
 * @see com.ice.jni.registry.Registry
 * @see com.ice.jni.registry.RegistryKey
 */

public class
RegMultiStringValue extends RegistryValue
	{
	String[]	data;
	int			dataLen;


	public
	RegMultiStringValue( RegistryKey key, String name )
		{
		super( key, name, RegistryValue.REG_MULTI_SZ );
		this.data = null;
		this.dataLen = 0;
		}

	public
	RegMultiStringValue( RegistryKey key, String name, int type )
		{
		super( key, name, type );
		this.data = null;
		this.dataLen = 0;
		}

	public
	RegMultiStringValue( RegistryKey key, String name, String[] data )
		{
		super( key, name, RegistryValue.REG_MULTI_SZ );
		this.setData( data );
		}

	public String[]
	getData()
		{
		return this.data;
		}

	public int
	getLength()
		{
		return this.dataLen;
		}

	public void
	setData( String[] data )
		{
		this.data = data;
		this.dataLen = data.length;
		}

	public byte[]
	getByteData()
		{
		int len = this.getByteLength();

		int ri = 0;
		byte[] result = new byte[len];
		for ( int i = 0 ; i < this.dataLen ; ++i )
			{
			byte[] strBytes = this.data[i].getBytes();

			for ( int j = 0 ; j < strBytes.length ; ++j )
				result[ri++] = strBytes[j];

			result[ri++] = 0;
			}

		return result;
		}

	public int
	getByteLength()
		{
		int len = 0;
		for ( int i = 0 ; i < this.dataLen ; ++i )
			len += this.data[i].length() + 1;

		return len;
		}

	public void
	setByteData( byte[] data )
		{
		int start;
		int count = 0;

		for ( int i = 0 ; i < data.length ; ++i )
			{
			if ( data[i] == 0 )
				count++;
			}

		int si = 0;
		String[] newData = new String[ count ];
		for ( int i = start = 0 ; i < data.length ; ++i )
			{
			if ( data[i] == 0 )
				{
				newData[si] = new String( data, start, (i - start) );
				start = si;
				}
			}

		this.setData( newData );
		}

	public void
	export( PrintWriter out )
		{
		byte[]	hexData;
		int		dataLen = 0;

		out.println( "\"" + this.getName() + "\"=hex(7):\\" );

		for ( int i = 0 ; i < this.data.length ; ++i )
			{
			dataLen += this.data[i].length() + 1;
			}

		++dataLen;

		int idx = 0;
		hexData = new byte[ dataLen ];

		for ( int i = 0 ; i < this.data.length ; ++i )
			{
			int strLen = this.data[i].length();
			byte[] strBytes = this.data[i].getBytes();

			System.arraycopy
				( strBytes, 0, hexData, idx, strLen );
			
			idx += strLen;

			hexData[ idx++ ] = '\0';
			}

		hexData[ idx++ ] = '\0';

		RegistryValue.exportHexData( out, hexData );
		}

	}





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
import com.ice.text.HexNumberFormat;


/**
 * The RegDWordValue class represents a double word, or
 * integer, value in the registry (REG_DWORD).
 *
 * @see com.ice.jni.registry.Registry
 * @see com.ice.jni.registry.RegistryKey
 */

public class
RegDWordValue extends RegistryValue
	{
	int		data;
	int		dataLen;

	public
	RegDWordValue( RegistryKey key, String name )
		{
		super( key, name, RegistryValue.REG_DWORD );
		this.data = 0;
		this.dataLen = 0;
		}

	public
	RegDWordValue( RegistryKey key, String name, int type )
		{
		super( key, name, type );
		this.data = 0;
		this.dataLen = 0;
		}

	public
	RegDWordValue( RegistryKey key, String name, int type, int data )
		{
		super( key, name, RegistryValue.REG_DWORD );
		this.setData( data );
		}

	public int
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
	setData( int data )
		{
		this.data = data;
		this.dataLen = 1;
		}

	public byte[]
	getByteData()
		{
		byte[] result = new byte[4];

		result[0] = (byte) ( (this.data >> 24) & 255 );
		result[1] = (byte) ( (this.data >> 16) & 255 );
		result[2] = (byte) ( (this.data >> 8)  & 255 );
		result[3] = (byte) (  this.data        & 255 );

		return result;
		}

	public int
	getByteLength()
		{
		return 4;
		}

	public void
	setByteData( byte[] data )
		{
		int newValue =
			  ( (((int) data[0]) << 24) & 0xFF000000 )
			| ( (((int) data[1]) << 16) & 0x00FF0000 )
			| ( (((int) data[2]) << 8)  & 0x0000FF00 )
			| (  ((int) data[3])        & 0x000000FF );

		this.setData( newValue );
		}

	public void
	export( PrintWriter out )
		{
		out.print( "\"" + this.getName() + "\"=" );

		HexNumberFormat nFmt =
			new HexNumberFormat( "xxxxxxxx" );

		out.println( "dword:" + nFmt.format( this.getData() ) );
		}

	}





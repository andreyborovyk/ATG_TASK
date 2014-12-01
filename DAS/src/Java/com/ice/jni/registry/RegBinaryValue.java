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
 * The RegBinaryValue class represents a binary value in the
 * registry (REG_BINARY).
 *
 * @see com.ice.jni.registry.Registry
 * @see com.ice.jni.registry.RegistryKey
 */

public class
RegBinaryValue extends RegistryValue
	{
	byte[]		data;
	int			dataLen;


	public
	RegBinaryValue( RegistryKey key, String name )
		{
		super( key, name, RegistryValue.REG_BINARY );
		this.data = null;
		this.dataLen = 0;
		}

	public
	RegBinaryValue( RegistryKey key, String name, int type )
		{
		super( key, name, type );
		this.data = null;
		this.dataLen = 0;
		}

	public
	RegBinaryValue( RegistryKey key, String name, byte[] data )
		{
		super( key, name, RegistryValue.REG_BINARY );
		this.setData( data );
		}

	public byte[]
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
	setData( byte[] data )
		{
		this.data = data;
		this.dataLen = data.length;
		}

	public byte[]
	getByteData()
		{
		return this.data;
		}

	public int
	getByteLength()
		{
		return this.dataLen;
		}

	public void
	setByteData( byte[] data )
		{
		this.data = data;
		this.dataLen = data.length;
		}

	public void
	export( PrintWriter out )
		{
		out.println( "\"" + this.getName() + "\"=hex:\\" );
		RegistryValue.exportHexData( out, this.data );
		}

	}





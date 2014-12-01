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

/**
 * This exception is used to indicate that no such key exists in the registry.
 *
 * @version $Change: 651448 $
 * @author Timothy Gerard Endres,
 *    <a href="mailto:time@ice.com">time@ice.com</a>.
 */

public class
RegistryException extends Exception
	{
	static public final String	RCS_ID = "$Change: 651448 $$DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $";
	static public final String	RCS_REV = "$Change: 651448 $";
	static public final String	RCS_NAME = "$Name:  $";

	private int		errorCode;


	public
	RegistryException()
		{
		super();
		this.errorCode = -1;
		}

	public
	RegistryException( String msg )
		{
		super( msg );
		this.errorCode = -1;
		}

	public
	RegistryException( String msg, int regErr )
		{
		super( msg );
		this.errorCode = regErr;
		}

	public int
	getErrorCode()
		{
		return this.errorCode;
		}

	public void
	setErrorCode( int errorCode )
		{
		this.errorCode = errorCode;
		}

	}



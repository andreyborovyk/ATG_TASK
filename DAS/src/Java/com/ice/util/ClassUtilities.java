
package com.ice.util;

public class
ClassUtilities
	{
	public static boolean
	implementsInterface( Class aClass, String interfaceName )
		{
		int ii;

		Class[] interfaces = aClass.getInterfaces();

		for ( ii = 0 ; ii < interfaces.length ; ++ii )
			{
			if ( interfaceName.equals( interfaces[ ii ].getName() ) )
				break;
			}

		return ( ii < interfaces.length );
		}

	}


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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;
import java.io.IOException;
import java.util.*;


public class
AWTUtilities
	{
	static public Point
	computeDialogLocation( Dialog dialog, int w, int h )
		{
		Dimension scrnSz =
			dialog.getToolkit().getScreenSize();

		int x = (scrnSz.width - w) / 2;
		int y = (scrnSz.height - h) / 3;
		
		return new Point( x, y );
		}

	static public void
	constrain(
			Container container, Component component,  
			int fill, int anchor,
			int gx, int gy, int gw, int gh, double wx, double wy )
		{
		GridBagConstraints	c =
			new GridBagConstraints();

		c.fill = fill;
		c.anchor = anchor;
		c.gridx = gx;
		c.gridy = gy;
		c.gridwidth = gw;
		c.gridheight = gh;
		c.weightx = wx;
		c.weighty = wy;

		( (GridBagLayout)container.getLayout() ).
			setConstraints( component, c );

		container.add( component );
		}

	static public void
	constrain(
			Container container, Component component,  
			int fill, int anchor,
			int gx, int gy, int gw, int gh,
			double wx, double wy,
			int ipadx, int ipady )
		{
		GridBagConstraints	c =
			new GridBagConstraints();

		c.fill = fill;
		c.anchor = anchor;
		c.gridx = gx;
		c.gridy = gy;
		c.gridwidth = gw;
		c.gridheight = gh;
		c.weightx = wx;
		c.weighty = wy;
		c.ipadx = ipadx;
		c.ipady = ipady;

		( (GridBagLayout)container.getLayout() ).
			setConstraints( component, c );

		container.add( component );
		}

	static public Font
	getFont( String fontName )
		{
		StringTokenizer toker =
			new StringTokenizer( fontName, "-", false );

		String sName = "Helvetica";
		String sStyle = "plain";
		String sSize = "12";

		int numTokes = toker.countTokens();
		boolean isok = true;

		try {
			if ( numTokes > 0 )
				{
				sName = toker.nextToken();

				if ( numTokes == 2 )
					{
					sSize = toker.nextToken();
					}
				else if ( numTokes == 3 )
					{
					sStyle = toker.nextToken();
					sSize = toker.nextToken();
					}
				}
			}
		catch ( Exception ex )
			{
			System.err.println
				( "Bad font specification '" + fontName + "' - "
					+ ex.getMessage() );
			return null;
			}

		int style =
				  (sStyle.compareTo( "plain" ) == 0)
					? Font.PLAIN :
				( (sStyle.compareTo( "bold" ) == 0)
					? Font.BOLD :
				( (sStyle.compareTo( "italic" ) == 0)
					? Font.ITALIC : (Font.BOLD + Font.ITALIC) ) );

		int size = Integer.parseInt( sSize );

		return new Font( sName, style, size );
		}

	public static Image
	getImageResource( String name )
		throws java.io.IOException
		{
		return
			com.ice.util.AWTUtilities.getImageResource
				( com.ice.util.AWTUtilities.class, name );
		}

	public static Image
	getImageResource( Class base, String name )
		throws java.io.IOException
		{
		Image	result = null;

		Toolkit	tk = Toolkit.getDefaultToolkit();

		URL imageURL = base.getResource( name );

		if ( imageURL != null )
			{
			result = tk.createImage
				( (ImageProducer) imageURL.getContent() );
			}
		
		return result;
		}

	}


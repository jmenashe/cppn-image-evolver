/**
 * Copyright (C) 2010 Brian Woolley
 * 
 * This file is part of the octopusArm simulator.
 * 
 * The octopusArm simulator is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Brian Woolley on Jul 12, 2010
 */
package edu.ucf.eplex.imageEvolver.gui;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.JFrame;


import edu.ucf.eplex.imageEvolver.ImagePanel;
import edu.ucf.eplex.picbreeder.ParameterTableInstance;

/**
 * @author Brian Woolley on Jul 12, 2010
 *
 */
public class NodeViewer extends JFrame {

	private static final long serialVersionUID = 7240162288068308374L;
	
	private int maxX = ParameterTableInstance.get().getInteger("display", "width");
	private int maxY = ParameterTableInstance.get().getInteger("display", "height") + 22;

	/**
	 * 
	 * @param evolution
	 * @param props 
	 */
	public NodeViewer(String title, Image image) {
		super(title);
		
		ImagePanel panel = new ImagePanel(image);
		
		getContentPane().add(panel, BorderLayout.EAST);

		setSize(maxX, maxY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}	
}

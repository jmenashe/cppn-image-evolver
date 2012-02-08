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

import edu.ucf.eplex.imageEvolver.ImageEvolver;
import edu.ucf.eplex.imageEvolver.ImagePanel;

/**
 * @author Brian Woolley on Jul 12, 2010
 *
 */
public class EvolutionViewer extends JFrame {

	private static final long serialVersionUID = 7240162288068308374L;
	
	private int maxX = 512;

	private int maxY = 278;
	/**
	 * 
	 * @param evolution
	 * @param props 
	 */
	public EvolutionViewer(ImageEvolver evolution) {
		super("Evolution Viewer");
		
		ImagePanel targetPanel = new ImagePanel(evolution.getTargetImage());
		ImagePanel champPanel = new ImagePanel();
		evolution.registerChampObserver(champPanel);
		
		maxX = 2 * evolution.getTargetImage().getWidth(this);
		maxY = 22 + evolution.getTargetImage().getHeight(this);
		
		getContentPane().add(champPanel,  BorderLayout.WEST);
		getContentPane().add(targetPanel, BorderLayout.EAST);

		setSize(maxX, maxY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public EvolutionViewer(String title, Image subject, Image target) {
		super(title);

		ImagePanel targetPanel = new ImagePanel(target);
		ImagePanel subjectPanel = new ImagePanel(subject);
		
		getContentPane().add(targetPanel, BorderLayout.EAST);
		getContentPane().add(subjectPanel,  BorderLayout.WEST);

		setSize(maxX, maxY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}	
}

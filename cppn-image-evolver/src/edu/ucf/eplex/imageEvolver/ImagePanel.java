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
package edu.ucf.eplex.imageEvolver;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * @author Brian Woolley on Jul 12, 2010
 *
 */
public class ImagePanel extends JPanel implements EvolutionObserver {

	private static final long serialVersionUID = -1110688523689208955L;

	private Image currentImage;

	public ImagePanel() {}
	
    public ImagePanel(Image image) {
		assert (image != null);
		currentImage = image;
		
		Dimension size = new Dimension(image.getWidth(this), image.getHeight(this));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);       
 	}
	
	public void paintComponent(Graphics g) {
        g.drawImage(currentImage, 0, 0, null);
    }
	/**
	 * @see edu.ucf.eplex.imageEvolver.EvolutionObserver#updateChampImage()
	 */
	public void updateChampImage(Image anImage) {
		currentImage = anImage;
		setSize(new Dimension(currentImage.getWidth(this), currentImage.getHeight(this)));
		repaint();
	}
}

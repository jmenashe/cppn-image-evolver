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
 * created by Brian Woolley on Jul 22, 2010
 */
package edu.ucf.eplex.imageEvolver;

/**
 * @author Brian Woolley on Jul 22, 2010
 *
 */
public class Region {

	protected final int f_area;
	private final int f_x, f_y, f_width, f_height;

	public Region(int x, int y, int width, int height) {
		assert (x >= 0);
		assert (y >= 0);
		assert (width > 0);
		assert (height > 0);
		
		f_x = x;
		f_y = y;
		f_width = width;
		f_height = height;
		f_area = width * height;
	}

	public boolean contains(int x, int y) {
		if (x >= f_x && y >= f_y && x < (f_x+f_width) && y < (f_y+f_height))
			return true;
		else
			return false;
	}

	public int getValue(int[] pixelSet, int scanWidth) {
		// TODO Calculate the average pixel value in this region of the pixelSet...
		int total = 0;
		for (int i=0; i<f_width; i++) {
			for (int j=0; j<f_height; j++) {
				total += pixelSet[(f_y+j)*scanWidth + (f_x+i)];
			}
		}
		return Math.round( (long) total/f_area);
	}

}

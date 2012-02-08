/*
 * Unlicensed intellectual property of the University of Central Florida for
 * internal usage only. You may not distribute this code to anyone. You may
 * not use this code (as source or compiled) or information obtained from
 * this code without permission.
 *
 * Picbreeder Project
 * Evolutionary Complexity Research Group
 * School of Electrical Engineering and Computer Science
 * 2006-2007
 */

package edu.ucf.eplex.picbreeder.renderers;

import edu.ucf.eplex.picbreeder.Individual;

/**
 * A RenderingAlgorithm contains the code nescessary to render an image
 * from the CPPN and Phenotype.  This implementation class should contain
 * the information such as pixel scheduling (interlaced, raster lines,
 * etc.) and swing notifications.
 * 
 * @author Nick
 */

public interface RenderingAlgorithm {
	/**
	 * Gets the quality level of this rendering algorithm.
	 * Higher quality is considered better.
	 * 
	 * @return The quality
	 */
	public int quality();
	
	/**
	 * Renders the image given the specified network.
	 * 
	 * @param image The result image
	 */
	public void render(Individual image);
}

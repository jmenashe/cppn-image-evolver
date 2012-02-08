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


package edu.ucf.eplex.picbreeder;

/**
 * The Phenotype is the basic interface of all phenotypes.  The client
 * assumes that the phenotype is always a 2-dimension rectangular buffer
 * of double arrays.  This means a network may write color or greyscale
 * images.
 * 
 * @author Nick Beato 
 */

public interface Phenotype {
	/**
	 * Computes the normalized <code>x</code> value associated with the 
	 * input coordinate <code>x</code>.
	 * 
	 * @param x The x value accepted by <code>setValue</code> 
	 * @return The normalized x coordinate
	 */
	
	public double computeInputX(int x);
	
	/**
	 * Computes the normalized <code>y</code> value associated with the 
	 * input coordinate <code>y</code>.
	 * 
	 * @param y The y value accepted by <code>setValue</code> 
	 * @return The normalized y coordinate
	 */
	
	public double computeInputY(int y);
	/**
	 * Retrieves the upper-bound of the y values acceptable by the 
	 * <code>setValue</code> method.
	 * 
	 * @return The non-inclusive height of the phenotype
	 */
	
	public int getHeight();
	
	/**
	 * 
	 * @return
	 */
	
	public int[] getPixelArray();

	/**
	 * Retrieves the upper-bound of the x values acceptable by the 
	 * <code>setValue</code> method.
	 * 
	 * @return The non-inclusive width of the phenotype
	 */
	
	public int getWidth();
	
	/**
	 * Updates the value of a particular coordinite with the values provided.
	 * <p>
	 * The <code>x</code> coordinate is in the range of
	 * <code>0 <= x < getWidth()</code>.  Likewise, the <code>y</code>
	 * coordinate is in the range of <code>0 <= y < getHeight()</code>.
	 * 
	 * @param x The x coordinate, in the range of <code>[0, getWidth())</code>
	 * @param y The y coordinate, in the range of <code>[0, getHeight())</code>
	 * @param values The correct values of the phenotype at (x,y)
	 */
	
	public void setValue(int x, int y, double[] values);
	
}



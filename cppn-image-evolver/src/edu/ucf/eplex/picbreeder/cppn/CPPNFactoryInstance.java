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

package edu.ucf.eplex.picbreeder.cppn;

/**
 * Singleton object for CPPNFactory class.
 * <p>
 * All singleton objects are implemented as independant classes to
 * allow objects to implement multiple singletons without the multiple
 * class inheritence issue.
 * 
 * @author Nick Beato
 */

public class CPPNFactoryInstance extends Singleton {
	private static CPPNFactory singleton = null;

	static {
		new CPPNFactoryInstance();
	}
	
	/**
	 * Gets the CPPNFactory singleton.
	 * 
	 * @return The CPPNFactory singleton
	 */
	public static CPPNFactory get() {
		return singleton;
	}
	
	/**
	 * Sets the CPPNFactory singleton.
	 * 
	 * @param instance The CPPNFactory singleton
	 */
	public static void set(CPPNFactory instance) {
		singleton = instance;
	}
	
	public void beginSession() {
		singleton = new edu.ucf.eplex.picbreeder.cppn.DefaultCPPNFactory();
	}

	public void endSession() {
		singleton = null;
	}
}

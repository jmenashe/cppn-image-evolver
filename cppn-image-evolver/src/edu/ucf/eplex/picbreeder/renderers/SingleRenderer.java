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
import edu.ucf.eplex.picbreeder.TaskListener;

/**
 * A SingleRenderer will render all individuals on a single thread in
 * the background. It will notify the caller via a TaskListener when
 * rendering has completed.
 * 
 * @author Nick
 */
public class SingleRenderer extends AbstractRenderer {
	/**
	 * Creates a renderer that notifies the owner before
	 * rendering is started and after rendering has completed.
	 * 
	 * @param owner The owning object
	 */
	public SingleRenderer(TaskListener owner) {
		super(owner);
	}

	public String getDescription() {
		return "Renders one image at a time until all images are rendered.";
	}
	
	protected void renderImplementation(Individual individual) {
		try {
			RenderingAlgorithmInstance.get().render(individual);
			individual.setRendered(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void runOnce() {
		for(Individual ind : individuals)
			if(consider(ind))
				renderImplementation(ind);
	}
	
	public String toString() {
		return "Sequential Renderer";
	}
}

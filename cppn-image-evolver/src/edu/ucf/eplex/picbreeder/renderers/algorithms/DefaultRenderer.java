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

package edu.ucf.eplex.picbreeder.renderers.algorithms;

import edu.ucf.eplex.picbreeder.Individual;
import edu.ucf.eplex.picbreeder.Phenotype;
import edu.ucf.eplex.picbreeder.TaskListener;
import edu.ucf.eplex.picbreeder.cppn.CPPNFactoryInstance;
import edu.ucf.eplex.picbreeder.cppn.Network;

/**
 * RenderingAlgorithm that renders the entire image in the background using multiple threads.
 * This is useful for large images.
 * 
 * @author Brian
 */

public final class DefaultRenderer extends AbstractRenderingAlgorithm implements TaskListener {
	
	public DefaultRenderer() {
		super(1.0);
	}
	
	public void notifyTaskFinished() {}
	
	public void notifyTaskStarting() {}
	
	public void render(Individual ind) {
		int h = ind.getPhenotype(0).getHeight();
		int w = ind.getPhenotype(0).getWidth();
		
		runOnce(ind, 0, 0, w, h);
	}
	private void runOnce(Individual individual, int xMin, int yMin, int xMax, int yMax) {
		Phenotype [] ps = new Phenotype[individual.countPhenotypes()];
		
		for(int i = 0; i < ps.length; i++)
			ps[i] = individual.getPhenotype(i);
		
		final Network net = CPPNFactoryInstance.get().createNetwork(individual.getGenome());
		
		double fx, fy;
		double [] copy = new double[3];
		double [] outputs;
		
		for(int y = yMin; y < yMax; y++) {
			fy = ps[0].computeInputY(y);
			
			for(int x = xMin; x < xMax; x++) {
				fx = ps[0].computeInputX(x);
				net.evaluateAt(fx, fy);
				
				for(int i = 0; i < ps.length; i++) {
					outputs = net.readOutput(i);
					System.arraycopy(outputs, 0, copy, 0, 3);
					ps[i].setValue(x, y, copy);
				}
			}
		}
	}

}

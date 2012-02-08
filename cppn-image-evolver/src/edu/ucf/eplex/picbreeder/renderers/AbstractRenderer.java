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

import java.util.Collection;

import edu.ucf.eplex.picbreeder.Individual;
import edu.ucf.eplex.picbreeder.TaskListener;

abstract class AbstractRenderer implements Renderer {
	private class RenderThread extends Thread {
		RenderThread() {
			super();
			listener.notifyTaskStarting();
		}
		
		public void run() {
			try {
				// sleep so swing can update everything nicely
				// without stealing the system from it
				Thread.sleep(50);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			runOnce();
			listener.notifyTaskFinished();
		}
	}
	protected Collection <Individual> individuals;
	
	private final TaskListener listener;
	
	protected AbstractRenderer(TaskListener owner) {
		listener = owner;
	}
	
	protected final boolean consider(Individual individual) {
		// TODO fix phenotype quality
		return !individual.isRendered() || RenderingAlgorithmInstance.get().quality() > individual.getQuality();
	}
	
	public void render(Collection <Individual> individuals) {
		this.individuals = individuals;
		
		spawnThread();
	}
	
	protected abstract void runOnce();
	
	private void spawnThread() {
		new RenderThread().start();
	}
}

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
 * created by Brian Woolley on Jul 9, 2010
 */
package edu.ucf.eplex.imageEvolver.util;

import java.io.BufferedReader;
import java.io.FileReader;

import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.Copyright;
import com.anji.integration.Generation;
import com.anji.persistence.Persistence;
import com.anji.run.Run;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import edu.ucf.eplex.imageEvolver.ImageEvolver;

/**
 * @author Brian Woolley on Jul 9, 2010
 *
 */
public class RunConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println( Copyright.STRING );
//		if(args.length == 0) {
//			System.out.println("Usage: java com.anji.imageEvolver.ImageCompare [options]");
//			System.out.println("Options:");
//			System.out.println("    -t targetGenomeFile");
//			System.out.println("    -p propertiesFile");
//			System.out.println("    -v verbose");
//			System.out.println("    -n numThreads");
//			System.exit(-1);
//		}
//
//		ArgumentParser parser = new ArgumentParser(args);
//		client.ParameterTableInstance.set(new test.TestParameters());
//		
//		int width = Integer.parseInt(client.ParameterTableInstance.get().getParameter("display", "width"));
//		int height = Integer.parseInt(client.ParameterTableInstance.get().getParameter("display", "height"));
//		String targetGenomeFile = parser.findArgument("-t");
//		String propFile = parser.findArgument("-p");
//		boolean verbose = parser.hasOption("-v");
//		
		if ( args.length != 2 ) {
			System.err.println( "usage: <cmd> <properties-file> <logFile-file>" );
			System.exit( -1 );
		}

		// load properties file
		Properties props = new Properties();
		props.loadFromResource( args[ 0 ] );

		// load runLog from logFile
		BufferedReader runLog = new BufferedReader(new FileReader(args[1]));
		
		new RunConverter(props, runLog);		
	}
	
	private final Configuration config;
	
	
	private final Persistence db;
	
	private final ImageEvolver fitnessFunc;
	public RunConverter(Properties props, BufferedReader runLog) throws Exception {
		fitnessFunc = new ImageEvolver();
		fitnessFunc.init(props);

		db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
		config = new DummyConfiguration();

		Run run = buildRunFromLogFile(runLog);
		run.init(props);
		db.store(run);		
	}
	private Run buildRunFromLogFile(BufferedReader runLog) {
		
	/* ...
	 * INFO  Generation 0: start
	 * INFO  connection->neuron id map size == 4
	 * INFO  neurons->connection id map size == 40
	 * INFO  species count: 6
	 * INFO  # chromosomes with max fitness: 0
	 * INFO  champ: id=40 score=0.6473 nodes=6 connections=5
	 * INFO  Generation 0: end [22:03:04 - 22:03:09] [5522]
	 * ...
	 */
		int generation = 0;
		int speciesCount;
		String champId = null;
		Chromosome champ = null;
		
		Run run = new Run();
		
		try {
			String line = runLog.readLine();
			EOF:
			while(line != null && generation < 30000) {
				while (!line.contains("INFO  Generation "+generation+": start")) {
					line = runLog.readLine();
					if (line == null) break EOF;
				}
				while (!line.contains("INFO  species count: ")) {
					line = runLog.readLine();
					if (line == null) break EOF;
				}
				speciesCount = Integer.parseInt(line.substring("INFO  species count: ".length()));
				while (!line.contains("INFO  champ: id=")) {
					line = runLog.readLine();
					if (line == null) break EOF;
				}
				String currChamp = line.substring("INFO  champ: id=".length(), line.indexOf(" score="));
				generation++;
				
				// load the champ chromosome (if new)
				if (!currChamp.equals(champId)) {
					champId = currChamp;
					System.out.print("["+generation+"] loading champId: "+champId+"...");
					champ = db.loadChromosome(champId, config);
					db.store(champ);
					System.out.println("done.");
				}
				
				// create an equivalent generation
				run.addGeneration(new Generation(generation, champ, speciesCount));				
			}
		} catch (Exception e) {}

		return run;
	}

}

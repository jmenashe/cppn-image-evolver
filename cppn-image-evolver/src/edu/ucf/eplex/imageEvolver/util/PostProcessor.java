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

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.anji.Copyright;
import com.anji.persistence.Persistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import edu.ucf.eplex.imageEvolver.ImageEvolver;

/**
 * @author Brian Woolley on Jul 9, 2010
 *
 */
public class PostProcessor {

	/**
	 * @author Brian Woolley on Nov 9, 2010
	 *
	 */
	class Generation {
		protected  long champId = 332;
		protected  int generationId, fitness = 2, nodes = 4, connections = 3;

		protected Generation(Node gen) {
			generationId = Integer.parseInt(gen.getAttributes().getNamedItem("id").getNodeValue());
			
			Node champ = gen.getFirstChild();
			while (true) {
				if (champ.getNodeName().equalsIgnoreCase("champ")) {
					champId = Long.parseLong(champ.getAttributes().getNamedItem("id").getNodeValue());
					Node feature = champ.getFirstChild();
					while (true) {
						if (feature.getNodeName().equalsIgnoreCase("fitness"))
							fitness = Integer.parseInt(feature.getTextContent());
						if (feature.getNodeName().equalsIgnoreCase("connections"))
							connections = Integer.parseInt(feature.getTextContent());
						if (feature.getNodeName().equalsIgnoreCase("nodes"))
							nodes = Integer.parseInt(feature.getTextContent());
						feature = feature.getNextSibling();
						if (feature == null) break;
					}
				}
				champ = champ.getNextSibling();
				if (champ == null) break;			
			}
//				Node feature = champ.getFirstChild();
//				System.out.println(feature.getNodeName() + ": " + feature.getNodeValue());
//				feature = feature.getNextSibling();
//				System.out.println(feature.getNodeName() + ": " + feature.getNodeValue());
//				feature = feature.getNextSibling();
//				System.out.println(feature.getNodeName() + ": " + feature.getNodeValue());
//				feature = feature.getNextSibling();

//				champId = Long.parseLong(champ.getAttributes().getNamedItem("id").getNodeValue());
//				System.out.println("ChampId: "+ champId);
//				NodeList champFeatures = champ.getChildNodes();
//				fitness = Integer.parseInt(champFeatures.item(0).getNodeValue());
//				nodes = Integer.parseInt(champFeatures.item(1).getNodeValue());
//				connections = Integer.parseInt(champFeatures.item(2).getNodeValue());
		}
	}
	
	private class Run {
		private List<Generation> generations = new ArrayList<Generation>();
		
		private Run(String runFileXml) throws Exception {
			InputStream in = new FileInputStream( runFileXml );
			Node run = null;
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse( in );
				run = doc.getFirstChild();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (run == null) throw new Exception("no run element found in " + runFileXml);
			
			Node gen = run.getFirstChild();
			while (true) {
				if (gen.getNodeName().equalsIgnoreCase("generation"))
					generations.add(new Generation(gen));
				gen = gen.getNextSibling();
				if (gen == null) break;
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println( Copyright.STRING );
		if(args.length == 0) {
			System.out.println("Usage: java com.anji.imageEvolver.PostProcessor [options]");
			System.out.println("Options:  <propertiesFile> <runFiles [run1.xml run2.xml ...]>");
			System.exit(-1);
		}

		// load fitness function from properties
		Properties props = new Properties();
		props.loadFromResource( args[ 0 ] );

//		ImageEvolver fitnessFunc = new ImageEvolver();
//		fitnessFunc.init(props);

		List<String> inFiles = new ArrayList<String>();
		for (int i=1; i<args.length; i++)
			inFiles.add(args[i]);
		
		long hack = System.currentTimeMillis();
		// load each runFile from xml into the experiment 
		PostProcessor exp = new PostProcessor(inFiles);
		hack = System.currentTimeMillis() - hack;
		System.out.println("Imported " + inFiles.size() + " runs in " + hack + " mS...");
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream( "./output/champs.csv" );
			out.write(exp.getChampsOverGenerations().getBytes() );
			out.close();
			out = new FileOutputStream( "./output/fitness.csv" );
			out.write(exp.getFitnessOverGenerations().getBytes() );
			out.close();
			out = new FileOutputStream( "./output/connections.csv" );
			out.write(exp.getConnectionsOverGenerations().getBytes() );
			out.close();
			out = new FileOutputStream( "./output/nodes.csv" );
			out.write(exp.getNodesOverGenerations().getBytes() );
			out.close();
		}
		finally {
			if ( out != null )
				out.close();
		}
		// Write out each all champs at 1024x1024

//		Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
//		Configuration config = new DummyConfiguration();
//		ArrayList<Chromosome> chroms = new ArrayList<Chromosome>();
//
//		for (int i=1; i<args.length; i++) {
//			Chromosome chrom = db.loadChromosome( args[ i ], config );
//			if ( chrom == null ) throw new IllegalArgumentException( "no chromosome found: " + args[ i ] );
//			chroms.add( chrom );
//		}

		// evaluate
//		fitnessFunc.evaluate( chroms );

//		for (Chromosome c : chroms) {
////			Image target = fitnessFunc.getTargetImage();			
//			Image subject = fitnessFunc.loadGreyscaleImage(c);
////			new EvolutionViewer("Subject " +c.getId()+ " vs. Image " +c.getId(), subject, target);	
////			System.out.println( c.getId() + "\t" + fitnessFunc.getAvgPixelErr( c ) );
//			ImageIO.write((RenderedImage) subject, "png", new File("./imageTargets/target" +c.getId()+".png"));
//		}
	}
	
	private List<Run> runs = new ArrayList<Run>();
	
	public PostProcessor(List<String> runFiles) {
		for (String runFile : runFiles)
			try {
				runs.add(new Run(runFile));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private long getChampInGeneration(Run r, int generation) {
		if (generation < r.generations.size())
			return r.generations.get(generation).champId;
		else
			return r.generations.get((r.generations.size()-1)).champId;
	}

	private String getChampsOverGenerations() {
		StringBuilder generation = new StringBuilder(getHeader());
		for (int i=0; i<30000; i+=10) {
			generation.append(i);
			for (Run r : runs) {
				generation.append(", " + getChampInGeneration(r, i));
			}
			generation.append("\n");
		}
		
		// add the last generation
		generation.append(30000);
		for (Run r : runs) 
			generation.append(", " + getChampInGeneration(r, 29999));
		generation.append("\n");
		
		return generation.toString();
	}

	private int getConnectionsInGeneration(Run r, int generation) {
		if (generation < r.generations.size())
			return r.generations.get(generation).connections;
		else
			return r.generations.get((r.generations.size()-1)).connections;
	}

	private String getConnectionsOverGenerations() {
		StringBuilder generation = new StringBuilder(getHeader());
		for (int i=0; i<30000; i+=10) {
			generation.append(i);
			for (Run r : runs) {
				generation.append(", " + getConnectionsInGeneration(r, i));
			}
			generation.append("\n");
		}
		
		// add the last generation
		generation.append(30000);
		for (Run r : runs) {
			generation.append(", " + getConnectionsInGeneration(r, 29999));
		}
		generation.append("\n");
		return generation.toString();
	}

	private int getFitnessInGeneration(Run r, int generation) {
		if (generation < r.generations.size())
			return r.generations.get(generation).fitness;
		else
			return r.generations.get((r.generations.size()-1)).fitness;
	}

	private String getFitnessOverGenerations() {
		StringBuilder generation = new StringBuilder(getHeader());
		for (int i=0; i<30000; i+=10) {
			generation.append(i);
			for (Run r : runs) {
				generation.append(", " + getFitnessInGeneration(r, i));
			}
			generation.append("\n");
		}
		// add the last generation
		generation.append(30000);
		for (Run r : runs) {
			generation.append(", " + getFitnessInGeneration(r, 29999));
		}
		generation.append("\n");
		return generation.toString();
	}
	
	private String getHeader() {
		StringBuilder header = new StringBuilder("Generation");
		for (Run r : runs)
			header.append(", Run" + (runs.indexOf(r)+1));
		return header.append("\n").toString();
	}

	private int getNodesInGeneration(Run r, int generation) {
		if (generation < r.generations.size())
			return r.generations.get(generation).nodes;
		else
			return r.generations.get((r.generations.size()-1)).nodes;
	}
	private String getNodesOverGenerations() {
		StringBuilder generation = new StringBuilder(getHeader());
		for (int i=0; i<30000; i+=10) {
			generation.append(i);
			for (Run r : runs) {
				generation.append(", " + getNodesInGeneration(r, i));
			}
			generation.append("\n");
		}
		// add the last generation
		generation.append(30000);
		for (Run r : runs) {
			generation.append(", " + getNodesInGeneration(r, 29999) );
		}
		generation.append("\n");
		return generation.toString();
	}

}
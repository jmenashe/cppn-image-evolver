/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Philip Tucker
 */
package com.anji.integration;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jgap.Chromosome;
import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;

import com.anji.neat.NeatConfiguration;
import com.anji.persistence.Persistence;
import com.anji.run.Run;
import com.anji.util.Properties;

/**
 * Writes genetic algorithm data, including chromosomes and run, to persistent storage.
 * 
 * @author Philip Tucker
 */
public class PersistenceEventListener implements GeneticEventListener {
	
	private static Logger logger = Logger.getLogger( PersistenceEventListener.class );
	
	/**
	 * properties key, whether or not to persist all chromosomes
	 */
	public final static String PERSIST_ALL_CHROMOSOMES_KEY = "persist.all";
	
	/**
	 * properties key, whether or not to persist generational champs
	 */
	public final static String PERSIST_CHAMPIONS_KEY = "persist.champions";
	
	/**
	 * properties key, whether or not to persist all of final generation
	 */
	public final static String PERSIST_LAST_GEN_KEY = "persist.last";
	
	private Collection<Chromosome> champs = new ArrayList<Chromosome>();
	
	private NeatConfiguration config;
	
	private Persistence db = null;
	
	private boolean persistAllChroms = false;
	
	private boolean persistChamps = false;
	
	private boolean persistLastGen = false;
	
	private Collection<Chromosome> previousGeneration = new ArrayList<Chromosome>();
	
	private Run run;
	
	/**
	 * ctor
	 * @param aConfig - need this to persist config
	 * @param aRun
	 */
	public PersistenceEventListener( NeatConfiguration aConfig, Run aRun ) {
		config = aConfig;
		run = aRun;
	}
	
	/**
	 * @param event <code>GeneticEvent.GENOTYPE_EVALUATED_EVENT</code> writes chromosomes and
	 * updates run; <code>GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT</code> loads config;
	 * <code>GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVEN</code> stores config
	 */
	public void geneticEventFired( GeneticEvent event ) {
		Genotype genotype = (Genotype) event.getSource();
		if ( GeneticEvent.GENOTYPE_START_GENETIC_OPERATORS_EVENT.equals( event.getEventName() ) ) {
			genotypeStartGeneticOperatorsEvent();
		}
		else if ( GeneticEvent.GENOTYPE_FINISH_GENETIC_OPERATORS_EVENT.equals( event.getEventName() ) ) {
			genotypeFinishGeneticOperatorsEvent();
		}
		else if ( GeneticEvent.GENOTYPE_EVALUATED_EVENT.equals( event.getEventName() ) ) {
			genotypeEvaluatedEvent( genotype );
		}
		else if ( GeneticEvent.RUN_COMPLETED_EVENT.equals( event.getEventName() ) ) {
			runCompletedEvent();
		}
	}
	
	private void genotypeEvaluatedEvent( Genotype genotype ) {
		Collection<Chromosome> currentGeneration = genotype.getChromosomes();
	
		Chromosome champ = genotype.getFittestChromosome();
		if (!champs.contains(champ)) {
			// record champ
			champs.add( champ );

			// persits champ
			storeChromosome( champ );

			// persist run
			storeRun( run );
		}
		
		previousGeneration.clear();
		previousGeneration.addAll( currentGeneration );
	}
	
	@Deprecated
	private void genotypeEvaluatedEventOriginal( Genotype genotype ) {
		Collection<Chromosome> currentGeneration = genotype.getChromosomes();
	
		// persist generation
		if ( persistAllChroms || persistLastGen ) {
			for (Chromosome c : currentGeneration) {
				storeChromosome( c );
			}
		}
	
		// persist champ
		Chromosome champ = genotype.getFittestChromosome();
		champs.add( champ );
		if ( persistChamps ) {
			try {
				db.store( champ );
			}
			catch ( Exception e ) {
				String msg = "PersistenceEventListener: error storing chromosome " + champ.getId();
				logger.error( msg, e );
				throw new IllegalStateException( msg + ": " +  e );
			}
		}
	
		// persist run
		storeRun( run );
		
		// delete chromosomes we don't want to persist
		if ( !persistAllChroms ) {
			previousGeneration.removeAll( currentGeneration );
			if ( persistChamps )
				previousGeneration.removeAll( champs );
			for (Chromosome c : previousGeneration) {
				try {
					db.deleteChromosome( c.getId().toString() );
				}
				catch ( Exception e ) {
					logger.error( "error storing chromosome " + c, e );
				}
			}
		}
	
		previousGeneration.clear();
		previousGeneration.addAll( currentGeneration );
	}
	
	private void genotypeFinishGeneticOperatorsEvent() {
		config.logIdMaps( logger, Priority.INFO );
		try {
			config.store();
		}
		catch ( InvalidConfigurationException e ) {
			logger.error( "could not store configuration", e );
		}
	}
	
	private void genotypeStartGeneticOperatorsEvent() {
		try {
			config.load();
		}
		catch ( InvalidConfigurationException e ) {
			logger.error( "could not load configuration", e );
		}
	}
	
	/**
	 * See <a href=" {@docRoot}/params.htm" target="anji_params">Parameter Details </a> for
	 * specific property settings.
	 * 
	 * @param props configuration data
	 */
	public void init( Properties props ) {
		db = (Persistence) props.singletonObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
		persistAllChroms = props.getBooleanProperty( PERSIST_ALL_CHROMOSOMES_KEY );
		persistChamps = props.getBooleanProperty( PERSIST_CHAMPIONS_KEY );
		persistLastGen = props.getBooleanProperty( PERSIST_LAST_GEN_KEY );
	}
	
	private void runCompletedEvent() {
		
		// persist champs
		if ( persistChamps ) {
			for (Chromosome champ : champs) {
				storeChromosome( champ );
			}
		}
	
		// persist run
		storeRun( run );
	}
	
	/**
	 * stores chromosome <code>chrom<code>
	 * @param chrom
	 */
	private void storeChromosome( Chromosome chrom ) {
		try {
			db.store( chrom );
		}
		catch ( Exception e ) {
			String msg = "PersistenceEventListener: error storing chromosome " + chrom.getId();
			logger.error( msg, e );
			throw new IllegalStateException( msg + ": " +  e );
		}
	}
		
	private void storeRun( Run run ) {
		try {
			db.store( run );
		}
		catch ( Exception e ) {
			logger.error( "PersistenceEventListener: error storing run", e );
		}
	}

}

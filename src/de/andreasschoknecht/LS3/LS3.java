/**
 * Part of the LS3 Similarity-based process model search package.
 * 
 * Licensed under the GNU General Public License v3.
 *
 * Copyright 2012 by Andreas Schoknecht <andreas_schoknecht@web.de>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Andreas Schoknecht
 */

package de.andreasschoknecht.LS3;

import java.util.Arrays;

/**
 * <p>
 * The LS3 contains the methods for querying a document collection. Three different overloaded methods are provided:
 * <ul>
 * <li>QueryKAll for obtaining query result using all models in the collection as query with a specified dimensionality parameter.
 * <li>QueryAll for obtaining query result using all models in the collection as query without specifying a dimensionality parameter.
 * <li> Query for obtaining query results for a model collection using a specific query model.
 * </ul>
 * 
 * <p>
 * These three types of querying use either a document collection defined by a path to a directory containing PNML files. Or the use a DocumentCollection
 * object, which has been prepared before. Besides, all methods need a threshold parameter theta, which specifies the acceptable similarity values for result
 * calculation. All model pairs having a similarity value equal to or greater than theta will be included in the results of a query.
 * 
 * <p>
 * It is also possible to print query data to the console. Therefore all query types have a corresponding PrintData variant, which prints query data to the console.
 * These data contain the terms of a model collection, the Term-Document Matrix, the SVD, the LSSSM similarity matrix, and the results.
 */
public class LS3 {
	
	
	/**
	 * QueryKAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Therefore, the reduced k-dimensional SVD is used. The models having
	 * a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 *
	 * @param pnmlPath The path to the PNML files (the model collection)
	 * @param k The dimensionality parameter k for the reduced SVD matrices
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryKAll(String pnmlPath, int k, float theta) {
		DocumentCollection documentCollection = new DocumentCollection(pnmlPath);
		documentCollection.createDocuments();		
		documentCollection.generateTDMatrix();
			
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		lssmCalculator.calculateLSSMMatrix();
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
		
		return result;
	}
	
	/**
	 * QueryKAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Therefore, the reduced k-dimensional SVD is used. The models having
	 * a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 * 
	 * Additionally, all data necessary for the calculation of results is printed to the console.
	 *
	 * @param pnmlPath The path to the PNML files (the model collection)
	 * @param k The dimensionality parameter k for the reduced SVD matrices
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryKAllPrintData(String pnmlPath, int k, float theta) {
		DocumentCollection documentCollection = new DocumentCollection(pnmlPath);
		documentCollection.createDocuments();		
		documentCollection.generateTDMatrix();
			
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		lssmCalculator.calculateLSSMMatrix();
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
		
		printTerms(documentCollection);
		printTDMatrix(documentCollection);
		printSVD(lssmCalculator);
		printReducedSVD(lssmCalculator);
		printLSSMMatrix(lssmCalculator);
		printResults(result);
		
		return result;	
	}
	
	/**
	 * QueryKAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Therefore, the reduced k-dimensional SVD is used. The models having
	 * a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 * 
	 * This method needs a DocumentCollection object, which has been set up before. I.e., the PNML files this DocumentCollection objects represents
	 * have been parsed and the corresponding Term-Document Matrix has been created.
	 *
	 * @param documentCollection The document collection created from a set of PNML files.
	 * @param k The dimensionality parameter k for the reduced SVD matrices
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryKAll(DocumentCollection documentCollection, int k, float theta) {		
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		lssmCalculator.calculateLSSMMatrix();
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
		
		return result;
	}
	
	/**
	 * QueryKAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Therefore, the reduced k-dimensional SVD is used. The models having
	 * a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 * 
	 * This method needs a DocumentCollection object, which has been set up before. I.e., the PNML files this DocumentCollection objects represents
	 * have been parsed and the corresponding Term-Document Matrix has been created.
	 * 
	 * Additionally, all data necessary for the calculation of results is printed to the console.
	 *
	 * @param documentCollection The document collection created from a set of PNML files.
	 * @param k The dimensionality parameter k for the reduced SVD matrices
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryKAllPrintData(DocumentCollection documentCollection, int k, float theta) {		
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		lssmCalculator.calculateLSSMMatrix();
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
		
		printTerms(documentCollection);
		printTDMatrix(documentCollection);
		printSVD(lssmCalculator);
		printReducedSVD(lssmCalculator);
		printLSSMMatrix(lssmCalculator);
		printResults(result);
		
		return result;
	}
	
	/**
	 * QueryAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Besides, the queries are executed for all k &lt;= rank of SVD. That means
	 * the results for all queries are calculated for each k being smaller or equal to the rank of the SVD.
	 * The models having a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 *
	 * @param pnmlPath The path to the PNML files (the model collection)
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryAll(String pnmlPath, float theta) {
		DocumentCollection documentCollection = new DocumentCollection(pnmlPath);
		documentCollection.createDocuments();		
		documentCollection.generateTDMatrix();
			
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		
		QueryAllResult result = new QueryAllResult();
		int rank = lssmCalculator.getRank();
		for(int i = 1; i <= rank; i++) {
			lssmCalculator.reduceDimensionality(i);
			lssmCalculator.calculateLSSMMatrix();
					
			result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
		}
		
		return result;
	}
	
	
	/**
	 * QueryAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Besides, the queries are executed for all k &lt;= rank of SVD. That means
	 * the results for all queries are calculated for each k being smaller or equal to the rank of the SVD.
	 * The models having a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 * 
	 * Additionally, all data necessary for the calculation of results is printed to the console.
	 *
	 * @param pnmlPath The path to the PNML files (the model collection)
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryAllPrintData(String pnmlPath, float theta) {
		DocumentCollection documentCollection = new DocumentCollection(pnmlPath);
		documentCollection.createDocuments();		
		documentCollection.generateTDMatrix();
			
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		
		QueryAllResult result = new QueryAllResult();
		int rank = lssmCalculator.getRank();
		for(int i = 1; i <= rank; i++) {
			lssmCalculator.reduceDimensionality(i);
			lssmCalculator.calculateLSSMMatrix();
					
			result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
			
			System.out.println("---------------------");
			System.out.println("Print data for k = " + i);
			System.out.println("---------------------");
			//printTerms(documentCollection);
			//printTDMatrix(documentCollection);
			//printSVD(lssmCalculator);
			//printReducedSVD(lssmCalculator);
			printLSSMMatrix(lssmCalculator);
			//printResults(result);
		}
		
		return result;
	}
	
	/**
	 * QueryAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Besides, the queries are executed for all k &lt;= rank of SVD. That means
	 * the results for all queries are calculated for each k being smaller or equal to the rank of the SVD.
	 * The models having a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 * 
	 * This method needs a DocumentCollection object, which has been set up before. I.e., the PNML files this DocumentCollection objects represents
	 * have been parsed and the corresponding Term-Document Matrix has been created.
	 *
	 * @param documentCollection The document collection created from a set of PNML files
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryAll(DocumentCollection documentCollection, float theta) {
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		
		QueryAllResult result = new QueryAllResult();
		int rank = lssmCalculator.getRank();
		for(int i = 1; i <= rank; i++) {
			lssmCalculator.reduceDimensionality(i);
			lssmCalculator.calculateLSSMMatrix();
					
			result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
		}
		
		return result;
	}
	
	/**
	 * QueryAll executes all queries for a model collection. I.e., all models in the collection are used as input and LSSM 
	 * similarity values between all models are calculated. Besides, the queries are executed for all k &lt;= rank of SVD. That means
	 * the results for all queries are calculated for each k being smaller or equal to the rank of the SVD.
	 * The models having a higher similarity value than theta are stored in an QueryAllResult object and returned by this method.
	 * 
	 * This method needs a DocumentCollection object, which has been set up before. I.e., the PNML files this DocumentCollection objects represents
	 * have been parsed and the corresponding Term-Document Matrix has been created.
	 * 
	 * Additionally, all data necessary for the calculation of results is printed to the console.
	 *
	 * @param documentCollection The document collection created from a set of PNML files
	 * @param theta The threshold parameter theta for determining similar models
	 * @return The query results which contain all similar models of each model in the collection
	 */
	public QueryAllResult queryAllPrintData(DocumentCollection documentCollection, float theta) {
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		
		QueryAllResult result = new QueryAllResult();
		int rank = lssmCalculator.getRank();
		for(int i = 1; i <= rank; i++) {
			lssmCalculator.reduceDimensionality(i);
			lssmCalculator.calculateLSSMMatrix();
					
			result = documentCollection.calculateResults(lssmCalculator.getLSSMMatrix(), theta);
			
			System.out.println("---------------------");
			System.out.println("Print data for rank " + rank);
			System.out.println("---------------------");
			printTerms(documentCollection);
			printTDMatrix(documentCollection);
			printSVD(lssmCalculator);
			printReducedSVD(lssmCalculator);
			printLSSMMatrix(lssmCalculator);
			printResults(result);
		}
		
		return result;
	}
	
	
	
	/**
	 * Query calculates the result of a query with a query model returning all models which have a similarity value equal to or higher
	 * than a specified threshold theta. The parameter k specifies which dimensionality of the SVD should be used for similarity 
	 * calculation.
	 *
	 * @param pnmlPath The path to the model collection containing PNML files
	 * @param queryFile The path to the query model, i.e. PNML file used for querying
	 * @param k The dimensionality k used for SVD similarity calculation
	 * @param theta The threshold value theta
	 * @return The query results which contain all similar models to the query model
	 */
	public QueryAllResult query(String pnmlPath, String queryFile, int k, float theta) {
		// Create the document collection for which the similar models should be calculated and calculate SVD
		DocumentCollection documentCollection = new DocumentCollection(pnmlPath);
		documentCollection.createDocuments();		
		documentCollection.generateTDMatrix();
		
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		
		// Create a Query object and extract the terms from it
		Query query = new Query(queryFile);
		query.extractTerms();
		query.calculateTermFrequencies(documentCollection.getTermCollection());
		query.calculateWeightedFrequencies(documentCollection.getTDMatrix());
		query.calculatePseudoDocument(lssmCalculator.getUk(), lssmCalculator.getSk());
		query.calculateLSSMValues(lssmCalculator.getSk(), lssmCalculator.getVtk());
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(query, theta);
		
		return result;
	}
	
	/**
	 * QueryPrintData calculates the result of a query with a query model returning all models which have a similarity value equal to or higher
	 * than a specified threshold theta. The parameter k specifies which dimensionality of the SVD should be used for similarity 
	 * calculation.
	 * 
	 * Additionally, all data necessary for the calculation of results is printed to the console.
	 *
	 * @param pnmlPath The path to the model collection containing PNML files
	 * @param queryFile The path to the query model, i.e. PNML file used for querying
	 * @param k The dimensionality k used for SVD similarity calculation
	 * @param theta The threshold value theta
	 * @return The query results which contain all similar models to the query model
	 */
	public QueryAllResult queryPrintData(String pnmlPath, String queryFile, int k, float theta) {
		// Create the document collection for which the similar models should be calculated and calculate SVD
		DocumentCollection documentCollection = new DocumentCollection(pnmlPath);
		documentCollection.createDocuments();		
		documentCollection.generateTDMatrix();
		
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		
		// Create a Query object and extract the terms from it
		Query query = new Query(queryFile);
		query.extractTerms();
		query.calculateTermFrequencies(documentCollection.getTermCollection());
		query.calculateWeightedFrequencies(documentCollection.getTDMatrix());
		query.calculatePseudoDocument(lssmCalculator.getUk(), lssmCalculator.getSk());
		query.calculateLSSMValues(lssmCalculator.getSk(), lssmCalculator.getVtk());
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(query, theta);
		
		printTerms(documentCollection);
		printTerms(query);
		printTDMatrix(documentCollection);
		printSVD(lssmCalculator);
		printReducedSVD(lssmCalculator);
		printResults(result);
		
		return result;
	}
	
	/**
	 * QueryPrintData calculates the result of a query with a query model returning all models which have a similarity value equal to or higher
	 * than a specified threshold theta. The parameter k specifies which dimensionality of the SVD should be used for similarity 
	 * calculation.
	 * 
	 * This method needs a DocumentCollection object, which has been set up before. I.e., the PNML files this DocumentCollection objects represents
	 * have been parsed and the corresponding Term-Document Matrix has been created.
	 *
	 * @param documentCollection The document collection created from a set of PNML files
	 * @param queryFile The path to the query model, i.e. PNML file used for querying
	 * @param k The dimensionality k used for SVD similarity calculation
	 * @param theta The threshold value theta
	 * @return The query results which contain all similar models to the query model
	 */
	public QueryAllResult query(DocumentCollection documentCollection , String queryFile, int k, float theta) {
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		
		// Create a Query object and extract the terms from it
		Query query = new Query(queryFile);
		query.extractTerms();
		query.calculateTermFrequencies(documentCollection.getTermCollection());
		query.calculateWeightedFrequencies(documentCollection.getTDMatrix());
		query.calculatePseudoDocument(lssmCalculator.getUk(), lssmCalculator.getSk());
		query.calculateLSSMValues(lssmCalculator.getSk(), lssmCalculator.getVtk());
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(query, theta);
		
		return result;
	}
	
	/**
	 * QueryPrintData calculates the result of a query with a query model returning all models which have a similarity value equal to or higher
	 * than a specified threshold theta. The parameter k specifies which dimensionality of the SVD should be used for similarity 
	 * calculation.
	 * 
	 * This method needs a DocumentCollection object, which has been set up before. I.e., the PNML files this DocumentCollection objects represents
	 * have been parsed and the corresponding Term-Document Matrix has been created.
	 * 
	 * Additionally, all data necessary for the calculation of results is printed to the console.
	 *
	 * @param documentCollection The document collection created from a set of PNML files
	 * @param queryFile The path to the query model, i.e. PNML file used for querying
	 * @param k The dimensionality k used for SVD similarity calculation
	 * @param theta The threshold value theta
	 * @return The query results which contain all similar models to the query model
	 */
	public QueryAllResult queryPrintData(DocumentCollection documentCollection , String queryFile, int k, float theta) {
		LSSMCalculator lssmCalculator = new LSSMCalculator();
		lssmCalculator.calculateSVD( documentCollection.getTDMatrix().getWeightedMatrix() );
		lssmCalculator.reduceDimensionality(k);
		
		// Create a Query object and extract the terms from it
		Query query = new Query(queryFile);
		query.extractTerms();
		query.calculateTermFrequencies(documentCollection.getTermCollection());
		query.calculateWeightedFrequencies(documentCollection.getTDMatrix());
		query.calculatePseudoDocument(lssmCalculator.getUk(), lssmCalculator.getSk());
		query.calculateLSSMValues(lssmCalculator.getSk(), lssmCalculator.getVtk());
		
		QueryAllResult result = new QueryAllResult();
		result = documentCollection.calculateResults(query, theta);
		
		printTerms(documentCollection);
		printTerms(query);
		printTDMatrix(documentCollection);
		printSVD(lssmCalculator);
		printReducedSVD(lssmCalculator);
		printResults(result);
		
		return result;
	}
	

	private void printTerms(Query query) {
		System.out.println("---------------------");
		System.out.println("Printing query terms");
		System.out.println("---------------------");
		System.out.println("Query terms: "+query.getTermCollection().toString());
		System.out.println("Query amount of terms: "+query.getTermCollection().size());
		System.out.println("Query term frequencies: "+Arrays.toString( query.getTermFrequencies()) );
		System.out.println("Query weighted term frequencies: "+Arrays.toString( query.getWeightedTermFrequencies()) );
		System.out.println("Query pseudo document: "+Arrays.toString( query.getPseudoDocument() ) );
		System.out.println("Query LSSM values: "+Arrays.toString( query.getLSSMValues() ) );
		System.out.println("---------------------\r\n\r\n");
		
	}

	private void printResults(QueryAllResult result) {
		System.out.println("---------------------");
		System.out.println("Printing query results");
		System.out.println("---------------------");
		for(int i = 0, k = result.getResults().size(); i < k; i++) {
			System.out.println("Modell: " + result.getResults().get(i).getQuery().getPNMLPath() );
			System.out.println("Results:");
			for( LS3Document doc: result.getResults().get(i).getResults()) {
				System.out.println( doc.getPNMLPath() );
			}
			System.out.println("---------------------");	
		}
		System.out.println("---------------------\r\n\r\n");
		
	}

	private void printLSSMMatrix(LSSMCalculator lssmCalculator) {
		System.out.println("---------------------");
		System.out.println("Printing LSSM matrix");
		System.out.println("---------------------");
		double[][] lssmData = lssmCalculator.getLSSMMatrix().getData();
		for(int i = 0, k = lssmCalculator.getLSSMMatrix().getRowDimension(); i < k; i++) {
			for(int j = 0, l = lssmCalculator.getLSSMMatrix().getColumnDimension(); j < l; j++) {
				System.out.print(" "+lssmData[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------\r\n\r\n");
		
	}

	private void printReducedSVD(LSSMCalculator lssmCalculator) {
		System.out.println("---------------------");
		System.out.println("Printing reduced SVD matrices");
		System.out.println("---------------------");
		System.out.println("Print matrix Uk");
		System.out.println("---------------------");
		double[][] ukData = lssmCalculator.getUk().getData();
		for(int i = 0, k = lssmCalculator.getUk().getRowDimension(); i < k; i++) {
			for(int j = 0, l = lssmCalculator.getUk().getColumnDimension(); j < l; j++) {
				System.out.print(" "+ukData[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------");
		System.out.println("Print reduced singular value matrix Sk");
		System.out.println("---------------------");
		double[][] skData = lssmCalculator.getSk().getData();
		for(int i = 0, k = lssmCalculator.getSk().getRowDimension(); i < k; i++) {
			for(int j = 0, l = lssmCalculator.getSk().getColumnDimension(); j < l; j++) {
				System.out.print(" "+skData[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------");
		System.out.println("Print reduced matrix Vtk");
		System.out.println("---------------------");
		double[][] vtkData = lssmCalculator.getVtk().getData();
		for(int i = 0, k = lssmCalculator.getVtk().getRowDimension(); i < k; i++) {
			for(int j = 0, l = lssmCalculator.getVtk().getColumnDimension(); j < l; j++) {
				System.out.print(" "+vtkData[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------\r\n\r\n");
		
	}

	private void printSVD(LSSMCalculator lssmCalculator) {
		System.out.println("---------------------");
		System.out.println("Printing SVD matrices");
		System.out.println("---------------------");
		System.out.println("Print matrix U");
		System.out.println("---------------------");
		double[][] uData = lssmCalculator.getU().getData();
		for(int i = 0, k = lssmCalculator.getU().getRowDimension(); i < k; i++) {
			for(int j = 0, l = lssmCalculator.getU().getColumnDimension(); j < l; j++) {
				System.out.print(" "+uData[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------");
		System.out.println("Print singular value matrix S");
		System.out.println("---------------------");
		double[][] sData = lssmCalculator.getS().getData();
		for(int i = 0, k = lssmCalculator.getS().getRowDimension(); i < k; i++) {
			for(int j = 0, l = lssmCalculator.getS().getColumnDimension(); j < l; j++) {
				System.out.print(" "+sData[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------");
		System.out.println("Print matrix Vt");
		System.out.println("---------------------");
		double[][] vtData = lssmCalculator.getVt().getData();
		for(int i = 0, k = lssmCalculator.getVt().getRowDimension(); i < k; i++) {
			for(int j = 0, l = lssmCalculator.getVt().getColumnDimension(); j < l; j++) {
				System.out.print(" "+vtData[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------");
		System.out.println("Print rank: "+lssmCalculator.getRank());
		System.out.println("---------------------\r\n\r\n");
	}

	private void printTDMatrix(DocumentCollection documentCollection) {
		System.out.println("---------------------");
		System.out.println("Printing Term-Document Matrix");
		System.out.println("---------------------");
		for(int i = 0, k = documentCollection.getTDMatrix().getRowNumber(); i < k; i++) {
			System.out.print(documentCollection.getTDMatrix().getTermArray()[i]+" ");
			for(int j = 0, l = documentCollection.getTDMatrix().getColumnNumber(); j < l; j++) {
				System.out.print(" "+documentCollection.getTDMatrix().getMatrix()[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------\r\n\r\n");
		
		
		System.out.println("---------------------");
		System.out.println("Printing Weighted Term-Document Matrix");
		System.out.println("---------------------");
		for(int i = 0, k = documentCollection.getTDMatrix().getRowNumber(); i < k; i++) {		
			for(int j = 0, l = documentCollection.getTDMatrix().getColumnNumber(); j < l; j++) {
				System.out.print(" "+documentCollection.getTDMatrix().getWeightedMatrix()[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------\r\n\r\n");
		
	}

	private void printTerms(DocumentCollection documentCollection) {
		System.out.println("---------------------");
		System.out.println("Printing Terms");
		System.out.println("---------------------");
		System.out.println("Collection terms: "+documentCollection.getTermCollection().toString());
		System.out.println("Amount of terms in collection: "+documentCollection.getTermCollection().size());
		for(int i = 0, k = documentCollection.getDocumentNumber(); i < k; i++) {
			System.out.println("Document "+i+" terms: "+documentCollection.getDocuments().get(i).getTermCollection().toString());
			System.out.println("Document "+i+" amount of terms: "+documentCollection.getDocuments().get(i).getTermCollection().size());
		}
		System.out.println("---------------------\r\n\r\n");
		
	}
	

}

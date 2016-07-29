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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.jdom2.JDOMException;

import com.google.common.collect.Multiset;

/**
 * <p>
 * The DocumentCollection represents a collection of process models. When building a collection the files are filtered according to
 * the file extension .pnml. Process models in other formats are not supported.
 * <p>
 * After creation of a DocumentCollection the function createDocuments() allows the parsing of PNML files. With the function 
 * generateTDMatrix() the corresponding Term-Document Matrix can be created afterwards.
 */
public class DocumentCollection {
	
	/** The path to the directory containing PNML files. */
	private String pnmlPath;
	
	/** The file list of PNML files. */
	private String[] fileList;
	
	/** The amount of LS3Documents in this document collection.
	 *  The amount of distinct terms in the whole document collection */
	private int documentNumber, amountTerms;
	
	/** The LS3Documents contained in this document collection. */
	private ArrayList<LS3Document> ls3Documents;
	
	/** The term collection of this document collection. */
	private LinkedHashSet<String> termCollection;
	
	/** The Term-LS3Document Matrix for this document collection. */
	private TDMatrix tdMatrix;
	
	
	public DocumentCollection(String pnmlPath) {		
		File dir = new File(pnmlPath);
		String absolutePath = dir.getAbsolutePath();
		setPnmlPath(absolutePath);
		setFileList( filterPNMLFiles(dir) );
		
		setDocumentNumber(fileList.length);
		
		this.ls3Documents = new ArrayList<LS3Document>();
		this.termCollection = new LinkedHashSet<String>();
	}
	
	/**
	 * Filters the PNML files in a directory, which possibly contains other files as well.
	 *
	 * @param dir The directory containing PNML files and possibly other files
	 * @return the PNML files in the directory
	 */
	private static String[] filterPNMLFiles(File dir) {
		// Check if directory can be accessed
		if (!dir.isDirectory()) {
			System.out.println("Error: could not open directory "+dir.getAbsolutePath());
			System.exit(-2);
		}
		// Filter PNML files
		String[] fileList = dir.list(new FilenameFilter() {
			public boolean accept(File d, String name) {
				return name.endsWith(".pnml");
			}
		});
		return fileList;
	}
	
	/**
	 * Generate a Term-Document Matrix from this document collection's documents.
	 */
	public void generateTDMatrix() {
		tdMatrix = new TDMatrix(amountTerms, documentNumber);
		tdMatrix.fillMatrix(termCollection, ls3Documents);
		tdMatrix.fillWeightedMatrix();
	}
	
	/**
	 * Creates the LS3Documents of this document collection. Each document contains the relevant information of a PNML file for the LS3.
	 */
	public void createDocuments() {
		for(int i = 0; i < fileList.length; i++) {
			ls3Documents.add( new LS3Document(pnmlPath+File.separatorChar+fileList[i]) );
		}
		PNMLReader pnmlReader = new PNMLReader();
		try {
			pnmlReader.processDocuments(this);
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate query results for a whole document collection using a Latent Semantic Analysis-based Similarity Measure (LSSM) matrix.
	 * All documents having a similarity value equal or higher than theta with respect to another document are included in the results.
	 *
	 * @param lssmMatrix The LSSM matrix containing similarity values between documents
	 * @param theta The parameter theta as a threshold value
	 * @return all the results for each document as query
	 */
	QueryAllResult calculateResults(RealMatrix lssmMatrix, float theta) {
		QueryAllResult result = new QueryAllResult();
		ArrayList<LS3Document> documents = this.getDocuments();
		int docNumber = documents.size();
		double[][] matrixData = lssmMatrix.getData();
		
		for(int i = 0; i < docNumber; i++) {
			QueryResult queryResult = new QueryResult( documents.get(i) );
			for(int j = 0; j < docNumber; j++) {
				if(matrixData[i][j] >= theta)
					queryResult.addResult( documents.get(j) );
			}
			result.addResult(queryResult);
		}
		
		return result;
	}
	
	/**
	 * Calculate query results for a query and a document collection. All documents having a similarity value equal or higher
	 * than theta with respect to the query are included in the results.
	 *
	 * @param query The query document
	 * @param theta The parameter theta as a threshold value
	 * @return the query result
	 */
	QueryAllResult calculateResults(Query query, float theta) {
		QueryAllResult result = new QueryAllResult();
		ArrayList<LS3Document> documents = this.getDocuments();
		int docNumber = documents.size();
		double[] lssmValues = query.getLSSMValues();
		
		QueryResult queryResult = new QueryResult( query );
		
		for(int i = 0; i < docNumber; i++) {		
			if(lssmValues[i] >= theta)
				queryResult.addResult( documents.get(i) );
		}
		result.addResult(queryResult);
		
		return result;
	}
	
	/**
	 * Stores the Term-Document Matrix of this document collection as a text file containing file paths, term list and TD Maritx.
	 *
	 * @param filePath The file path for the resulting text file
	 */
	public void storeTDMatrix(String filePath) {
		File file = new File(filePath);
		String text = "";
		
		text = text + "--------------------COLLECTION--------------------\r\n";
		for(int i = 0, k = fileList.length; i < k; i++) {
			text = text + fileList[i] + "\r\n";
		}
		text = text + "----------------------------------------\r\n";
		
		text = text + "--------------------TERM LIST--------------------\r\n";
		Iterator<String> itr = termCollection.iterator();
		while(itr.hasNext()) {
			text = text + itr.next() + "\r\n";
		}
		text = text + "----------------------------------------\r\n";
		
		text = text + "--------------------TD MATRIX--------------------\r\n";
		text = text + "--------------------ROW NUMBER--------------------\r\n";
		text = text + tdMatrix.getRowNumber() + "\r\n";
		text = text + "----------------------------------------\r\n";
		text = text + "--------------------COLUMN NUMBER--------------------\r\n";
		text = text + tdMatrix.getColumnNumber() + "\r\n";
		text = text + "----------------------------------------\r\n";
		text = text + "--------------------MATRIX DATA--------------------\r\n";
		for (int i = 0, k = tdMatrix.getRowNumber(); i < k; i++) {		
			for (int j = 0, l = tdMatrix.getColumnNumber(); j < l; j++) {
				if(j != 0)
					text = text + " "+tdMatrix.getMatrix()[i][j];
				else
					text = text + tdMatrix.getMatrix()[i][j];
					
			}
			text = text + "\r\n";
		}
		
		try {
			FileUtils.writeStringToFile(file, text, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("File stored");
	}
	
	
	/**
	 * Load Term-Document Matrix data stored in a text file.
	 *
	 * @param filePath The file path to the file containing the Term-Document Matrix data.
	 */
	public void loadTDMatrix(String filePath) {
		
		try (Stream<String> lines = Files.lines (Paths.get(filePath), StandardCharsets.UTF_8))
		{			
			ArrayList<String> files = new ArrayList<String>();
			TDMatrix tdMatrix;
			int rowNumber = 0;
			int columnNumber = 0;
			
			Iterator<String> itr = lines.iterator();
			while (itr.hasNext()) {
				String line = itr.next();
				
				switch (line) {
	            case "--------------------COLLECTION--------------------":
	            	System.out.println(line);
	            	String line2 = itr.next();
	            	while ( !line2.equals("----------------------------------------") ) {
	            		files.add(line2);
	            		line2 = itr.next();
	            	}
	                break;
	            case "--------------------TERM LIST--------------------":
	            	System.out.println(line);
	            	termCollection.clear();
	            	line2 = itr.next();
	            	while ( !line2.equals("----------------------------------------") ) {
	            		termCollection.add(line2);
	            		line2 = itr.next();
	            	}
	                break;
	            case "--------------------ROW NUMBER--------------------":
	            	System.out.println(line);
	            	rowNumber = Integer.parseInt( itr.next() );
	                break;
	            case "--------------------COLUMN NUMBER--------------------":
	            	System.out.println(line);
	            	columnNumber = Integer.parseInt( itr.next() );
	                break;
	            case "--------------------MATRIX DATA--------------------":
	            	System.out.println(line);
	            	tdMatrix = new TDMatrix(rowNumber, columnNumber);
	            	tdMatrix.setRowNumber(rowNumber);
	            	tdMatrix.setColumnNumber(columnNumber);
	            	tdMatrix.setTermArray(termCollection.toArray(new String[0]));
	            	
	            	int rowCounter = 0;
	            	while ( itr.hasNext() ) {
	            		line2 = itr.next();
	            		String[] tokens = line2.split(" ");
	            		double[] values = new double[tokens.length];
	            		for (int i = 0; i < tokens.length; i++)
	            			values[i] = Double.parseDouble(tokens[i]);
	            		
	            		tdMatrix.fillRow(values, rowCounter);
	            		
	            		rowCounter++;
	            	}
	            	
	            	this.tdMatrix = tdMatrix;
	                break;
		    	}
				
			}
			
			setFileList( files.toArray(new String[0]) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("File loaded");
	}

	
	/**
	 * Insert a model to a model collection. This means that the underlying Term-Document Matrix has to be updated.
	 *
	 * @param modelPath the path to the model to be inserted.
	 */
	public void insertModel(String modelPath) {
		// Make sure file name is correct
		if (!modelPath.endsWith(".pnml"))
			modelPath = modelPath + ".pnml";
		
		// Create new LS3Document object and add it to the document collection list of documents
		System.out.println("------------------------");
		System.out.println("Model to insert:");
		System.out.println("------------------------");
		System.out.println(modelPath.substring(modelPath.lastIndexOf(File.separator) + 1));
		System.out.println("------------------------");
		System.out.println("Models in list:");
		System.out.println("------------------------");
		
		String[] updatedFileList =  new String[fileList.length + 1];
		for (int i = 0; i <= fileList.length; i++) {
			if (i != fileList.length)
				updatedFileList[i] = fileList[i];
			else
				updatedFileList[i] = modelPath.substring(modelPath.lastIndexOf(File.separator) + 1);
			
			System.out.println(updatedFileList[i]);
			
		}
		
		documentNumber++;
		
		LS3Document newDocument = new LS3Document(modelPath);
		PNMLReader pnmlReader = new PNMLReader();
		try {
			pnmlReader.processDocument(newDocument);
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("------------------------");
		System.out.println("New LS3Document data:");
		System.out.println("------------------------");
		System.out.println(newDocument.getPNMLPath());
		System.out.println("Amount of terms = "+newDocument.getAmountTerms());
		for (String term: newDocument.getTermCollection()) {
			System.out.println(term);
		}
		
		
		// Add new column to the Term-Document Matrix
		int t = tdMatrix.getRowNumber();
		double[] termFrequencies = new double[t];
		String[] termCollectionArray = new String[termCollection.size()];
		termCollection.toArray(termCollectionArray);
		
		Multiset<String> termsM = newDocument.getTermCollection();
		for (int i = 0; i < t; i++) {
			termFrequencies[i] = termsM.count(termCollectionArray[i]);
			termsM.remove(termCollectionArray[i]);
		}
		System.out.println("------------------------");
		System.out.println("Term frequencies:");
		System.out.println("------------------------");
		System.out.println( Arrays.toString(termFrequencies) );
		
		System.out.println("------------------------");
		System.out.println("Old TD Matrix:");
		System.out.println("------------------------");
		for(int i = 0, k = tdMatrix.getRowNumber(); i < k; i++) {
			System.out.print(tdMatrix.getTermArray()[i]+" ");
			for(int j = 0, l = tdMatrix.getColumnNumber(); j < l; j++) {
				System.out.print(" "+tdMatrix.getMatrix()[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------\r\n\r\n");
		
		
		tdMatrix.addColumn(termFrequencies);
		
		System.out.println("------------------------");
		System.out.println("New TD Matrix:");
		System.out.println("------------------------");
		for(int i = 0, k = tdMatrix.getRowNumber(); i < k; i++) {
			System.out.print(tdMatrix.getTermArray()[i]+" ");
			for(int j = 0, l = tdMatrix.getColumnNumber(); j < l; j++) {
				System.out.print(" "+tdMatrix.getMatrix()[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------\r\n\r\n");
		
		// Add new terms of the new model to the term list of the document collection
		System.out.println("------------------------");
		System.out.println("Old term collection:");
		System.out.println("------------------------");
		for (String term: termCollection) {
			System.out.println(term);
		}
		
		System.out.println("------------------------");
		System.out.println("Terms remaining in insertion model:");
		System.out.println("------------------------");
		System.out.println( Arrays.toString(termsM.toArray(new String[termsM.size()])) );
		
		Set<String> termSet = termsM.elementSet();
		String[] newTerms = termSet.toArray( new String[termSet.size()] );
		for (String term: newTerms) {
			termCollection.add(term);
		}
		
		System.out.println("------------------------");
		System.out.println("New term collection:");
		System.out.println("------------------------");
		for (String term: termCollection) {
			System.out.println(term);
		}
		
		System.out.println("------------------------");
		System.out.println("New term collection TD Matrix:");
		System.out.println("------------------------");
		for (String term: tdMatrix.getTermArray()) {
			System.out.println(term);
		}
		
		//  Add one row for each new term and add the corresponding Term-Document Matrix entries
		double[] newTermsFrequencies = new double[newTerms.length];
		for (int i = 0; i < newTerms.length; i++) {
			newTermsFrequencies[i] = termsM.count(newTerms[i]);
		}
		
		System.out.println("------------------------");
		System.out.println("New term frequencies:");
		System.out.println("------------------------");
		System.out.println( Arrays.toString(newTermsFrequencies) );
		
		int n = tdMatrix.getColumnNumber();
		for (int i = 0; i < newTermsFrequencies.length; i++) {
			double[] newRow = new double[n];
			for (int j = 0; j < n - 2; j++)
				newRow[j] = 0;
			
			newRow[n-1] = newTermsFrequencies[i];
			tdMatrix.addRow(newRow);
		}
		
		// Update term list of TDMatrix object
		tdMatrix.setTermArray(termCollection.toArray(new String[0]));
		
		System.out.println("------------------------");
		System.out.println("Final TD Matrix:");
		System.out.println("------------------------");
		for(int i = 0, k = tdMatrix.getRowNumber(); i < k; i++) {
			System.out.print(tdMatrix.getTermArray()[i]+" ");
			for(int j = 0, l = tdMatrix.getColumnNumber(); j < l; j++) {
				System.out.print(" "+tdMatrix.getMatrix()[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("---------------------\r\n\r\n");
		
	}
	
	/**
	 * Delete a model from the Term-Document Matrix search structure.
	 *
	 * @param modelName The model name of the model to be removed.
	 */
	public void deleteModel(String modelName) {		
		// Make sure file name is correct
		if (!modelName.endsWith(".pnml"))
			modelName = modelName + ".pnml";
		
		// Delete column from TD Matrix and set correct number of columns
		int deletionIndex = 0;	
		for (int i = 0, l = fileList.length; i < l; i++) {
			if (fileList[i].equals(modelName)) {
				tdMatrix.deleteColumn(i);
				deletionIndex = i;
			}		
		}
		
		// Delete model name from fileList (update to new file list).
		String[] newFileList = new String[fileList.length - 1];
		int counter = 0;
		for (int i = 0, l = fileList.length; i < l; i++) {
			if (i != deletionIndex) {
				newFileList[counter] = fileList[i];
				counter++;
			}		
		}
		setFileList(newFileList);
		
		// Delete LS3Document representation of file "modelName" (update to new ArrayList of LS3Documents).
		for (int i = 0, l = ls3Documents.size(); i < l; i++) {
			if (ls3Documents.get(i).getPNMLPath().endsWith(modelName)) {
				ls3Documents.remove(i);
				i = l;
			}	
		}
		
		// Delete term rows that only contain values 0.0. I.e. delete unnecessary terms.
		ArrayList<Integer> termDeletionIndices = new ArrayList<Integer>();
		boolean delete = true;
		
		double[][] matrix = tdMatrix.getMatrix();
		for (int i = 0, k = tdMatrix.getRowNumber(); i < k; i++) {
			for (int j = 0, l = tdMatrix.getColumnNumber(); j < l; j++) {
				if (matrix[i][j] != 0.0) {
					delete = false;
					j = l;
				}
			}
			if (delete == true)
				termDeletionIndices.add(i);
			else
				delete = true;
		}

		int deletionCounter = 0;
		for (int index: termDeletionIndices) {
			tdMatrix.deleteRow(index - deletionCounter);
			deletionCounter++;
		}
			
		
		// Update term list of document collection.
		deletionCounter = 0;
		LinkedHashSet<String> newTermList = new LinkedHashSet<String>();
		for (String term: termCollection) {
			if ( !termDeletionIndices.contains(deletionCounter) )
				newTermList.add(term);
			
			deletionCounter++;
		}
		
		setTermCollection(newTermList);
		
		// Update term list of TDMatrix object
		tdMatrix.setTermArray(termCollection.toArray(new String[0]));
		
	}
	

	public String getPnmlPath() {
		return pnmlPath;
	}

	void setPnmlPath(String pnmlPath) {
		this.pnmlPath = pnmlPath;
	}

	public String[] getFileList() {
		return fileList;
	}

	void setFileList(String[] fileList) {
		this.fileList = fileList;
	}

	public int getDocumentNumber() {
		return documentNumber;
	}

	void setDocumentNumber(int documentNumber) {
		this.documentNumber = documentNumber;
	}

	public ArrayList<LS3Document> getDocuments() {
		return ls3Documents;
	}

	void addDocument(LS3Document ls3Document) {
		this.ls3Documents.add(ls3Document);
	}

	public LinkedHashSet<String> getTermCollection() {
		return termCollection;
	}

	public void setTermCollection(LinkedHashSet<String> termCollection) {
		this.termCollection = termCollection;
	}

	void addTerm(String term) {
		this.termCollection.add(term);
		amountTerms = termCollection.size();
	}

	public int getAmountTerms() {
		return amountTerms;
	}

	public TDMatrix getTDMatrix() {
		return tdMatrix;
	}

}

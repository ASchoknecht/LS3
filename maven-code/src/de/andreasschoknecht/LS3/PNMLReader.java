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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.tartarus.martin.Stemmer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

/**
 * A PNMLReader is used for parsing PNML files. Thereby the labels of the process models are extracted and preprocessed. This preprocessing
 * includes lower-case conversion, stop word removal and stemming using Porter's stemming algorithm.
 */
public class PNMLReader {
	
	/** The PNML file list. */
	private String[] fileList;
	
	/** The term collection. */
	private HashSet<String> termCollection;
	
	/** The stop word list for filtering non relevant terms. */
	private ArrayList<String> stopwords;
	
	/**
	 * Process documents extracts the terms in place and transition labels of the models in the model collection and creates
	 * corresponding term lists.
	 *
	 * @param documentCollection The document collection which should be processed
	 * @throws JDOMException if no valid XML document is parsed
	 * @throws IOException if input/output errors occur when parsing a PNML file
	 */
	void processDocuments(DocumentCollection documentCollection) throws JDOMException, IOException {
		for( LS3Document ls3Document: documentCollection.getDocuments() ) {
			List<Object> labels = getPNMLLabelTerms( ls3Document.getPNMLPath() );
			createTermLists(labels, ls3Document, documentCollection);
		}	
	}
	
	/**
	 * Process document extracts the terms in place and transition labels of a model and creates a corresponding term list. Can be used
	 * for parsing a query model.
	 *
	 * @param ls3Document The document (model) for parsing
	 * @throws JDOMException if no valid XML document is parsed
	 * @throws IOException if input/output errors occur when parsing a PNML file
	 */
	void processDocument(LS3Document ls3Document) throws JDOMException, IOException {			
		List<Object> labels = getPNMLLabelTerms( ls3Document.getPNMLPath() );
		createTermLists(labels, ls3Document);		
	}
	
	/**
	 * Extract the terms of all transition and place labels in a PNML file.
	 *
	 * @param pnmlPath The path to a PNML file for parsing
	 * @return labels A list of labels from transition and places contained in a PNML file
	 * @throws JDOMException if no valid XML document is parsed
	 * @throws IOException if input/output errors occur when parsing a PNML file
	 */
	private List<Object> getPNMLLabelTerms(String pnmlPath) throws JDOMException, IOException{
		// Create internal document with SAXBuilder from PNML file
		Document doc = new SAXBuilder().build( new File(pnmlPath) );

		// Create xpathFactory for querying doc
		XPathFactory xpathFactory = XPathFactory.instance();

		// Define XPath expression for filtering the labels of transition and place labels
		XPathExpression<Object> expr = xpathFactory.compile("//name/text");
		List<Object> labels = expr.evaluate(doc);

		return labels;
	}
		
	/**
	 * Creates the term lists for a process model (LS3Document) in a model collection. Adds the terms to the document itself as Bag-of-Words and adds the terms to
	 * the HashSet of terms of the document collection. This method is used when parsing a document collection.
	 *
	 * @param labels The labels contained in the PNML file
	 * @param ls3Document The LS3Document representation of the PNML file for updating the term list of the document
	 * @param documentCollection The DocumentCollection for updating the term list of the whole collection
	 * @throws IOException if stop word file could not be read
	 */
	private void createTermLists(List<Object> labels, LS3Document ls3Document, DocumentCollection documentCollection) throws IOException {
		initializeWordList();
		
		ArrayList<String> tokens = new ArrayList<String>();
		String label = "";		
		for(Object temp: labels){
			Element value = (Element) temp;
			label = label + value.getText() + " ";
		}	
			
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(label), new CoreLabelTokenFactory(), "untokenizable=allKeep");
		while (ptbt.hasNext()) {
			tokens.add(ptbt.next().value());
		}

		for (int i = 0, j = tokens.size(); i < j; i++){
			String bereinigt = tokens.get(i).toLowerCase();

			// Clear tokens of empty tokens, stop words, and automatic tool labels
			if(!bereinigt.matches("(p|t)*([0-9]+)") && !stopwords.contains(bereinigt) && !bereinigt.equals("")){
				String term = bereinigt.replaceAll("[0-9]+", "");
				ls3Document.addTerm( stemString(term) );
				documentCollection.addTerm( stemString(term) );
			}
		}
	}
	
	/**
	 * Creates the term list for a process model (LS3Document). It only adds the terms to the document itself as Bag-of-Words.
	 * This method is used when parsing a query model.
	 *
	 * @param labels The labels contained in the PNML file
	 * @param ls3Document The LS3Document representation of the PNML file for updating the term list of the document
	 * @throws IOException if stop word file could not be read
	 */
	private void createTermLists(List<Object> labels, LS3Document ls3Document) throws IOException {
		initializeWordList();
		
		ArrayList<String> tokens = new ArrayList<String>();
		String label = "";		
		for(Object temp: labels){
			Element value = (Element) temp;
			label = label + value.getText() + " ";
		}	
			
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(label), new CoreLabelTokenFactory(), "untokenizable=allKeep");
		while (ptbt.hasNext()) {
			tokens.add(ptbt.next().value());
		}

		for (int i = 0, j = tokens.size(); i < j; i++){
			String bereinigt = tokens.get(i).toLowerCase();

			// Clear tokens of empty tokens, stop words, and automatic tool labels
			if(!bereinigt.matches("(p|t)*([0-9]+)") && !stopwords.contains(bereinigt) && !bereinigt.equals("")){
				String term = bereinigt.replaceAll("[0-9]+", "");
				ls3Document.addTerm( stemString(term) );
			}
		}
	}
	
	
	/**
	 * Method for stemming a term according to the Porter Stemmer algorithm.
	 *
	 * @param toStem The term to stem
	 * @return The term stemmed by the Porter Stemmer
	 */
	private static String stemString(String toStem){
		Stemmer porter = new Stemmer();
		char[] charArray = toStem.toCharArray();
		
		for (int i = 0; i< charArray.length;i++) {
			porter.add(charArray[i]);
		}
		porter.stem();
		
		return porter.toString();
	}
	
	/**
	 * Copies the stop words from a file into a list to be processed.
	 * 
	 * @throws IOException if stop word file could not be read
	 */
	private void initializeWordList() throws IOException {
		stopwords = new ArrayList<String>();
 
        BufferedReader br = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream("/stop-words.txt") ) );
				
		String word = "";
		do {
			word = br.readLine();
			if (word != null)
				stopwords.add(word);
		} while (word != null);
		br.close();
	}
	
	
	public String[] getFileList() {
		return fileList;
	}
	
	public void setFileList(String[] fileList) {
		this.fileList = fileList;
	}

	public HashSet<String> getTermCollection() {
		return termCollection;
	}

	public void setTermCollection(HashSet<String> termCollection) {
		this.termCollection = termCollection;
	}

}

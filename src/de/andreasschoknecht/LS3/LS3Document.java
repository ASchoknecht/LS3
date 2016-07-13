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

import java.io.IOException;

import org.jdom2.JDOMException;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * A LS3Document represents a process model as a document for querying.
 */
public class LS3Document {
	
	/** The path to the PNML file this document represents. */
	private String pnmlPath;
	
	/** The amount of distinct terms in this document. */
	private int amountTerms;
	
	/** The term collection of a model as Bag-of-Words. */
	private Multiset<String> bagOfWords;
	
	public LS3Document(String pnmlPath) {
		setPNMLPath(pnmlPath);		
		this.bagOfWords = HashMultiset.create();
	}
	
	public void createTermList() throws JDOMException, IOException {
		PNMLReader pnmlReader = new PNMLReader();
		pnmlReader.processDocument(this);
	}

	public String getPNMLPath() {
		return pnmlPath;
	}

	public void setPNMLPath(String pnmlPath) {
		this.pnmlPath = pnmlPath;
	}

	public Multiset<String> getTermCollection() {
		return bagOfWords;
	}

	public void addTerm(String term) {
		this.bagOfWords.add(term);
		amountTerms = bagOfWords.size();
	}

	public int getAmountTerms() {
		return amountTerms;
	}

	public void setAmountTerms(int amountTerms) {
		this.amountTerms = amountTerms;
	}

}

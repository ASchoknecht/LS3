package de.andreasschoknecht.LS3;

import java.util.ArrayList;

import de.andreasschoknecht.LS3.DocumentCollection;
import de.andreasschoknecht.LS3.LS3;
import de.andreasschoknecht.LS3.LS3Document;
import de.andreasschoknecht.LS3.QueryAllResult;
import de.andreasschoknecht.LS3.QueryResult;

public class Main {

	public static void main(String[] args) {
		
		// Testing storage of TD Matrix
		/*DocumentCollection documentCollection = new DocumentCollection("C:\\Testmodels");
		
		documentCollection.createDocuments();
		documentCollection.generateTDMatrix();
		//documentCollection.storeTDMatrix("C:\\Ausgabe\\TDMatrix.txt");
		documentCollection.deleteModel("test3");
		//documentCollection.storeTDMatrix("C:\\Ausgabe\\TDMatrix3.txt");
		documentCollection.insertModel("C:\\Testmodels\\test3.pnml");
		System.out.println("------------------------");
		System.out.println("Done");*/
		
		// Testing of LS3 querying
		/*LS3 ls3 = new LS3();
		String model = "C:\\Wopedmodels\\uzCologne.pnml";
		QueryAllResult result = ls3.query("C:\\Wopedmodels", model, 25, 0.79f);
		ArrayList<QueryResult> results = result.getResults();
		
		for (QueryResult queryResult: results) {
			System.out.println("-----------------------");
			System.out.println("Result");
			System.out.println("-----------------------");
			ArrayList<LS3Document> resultDocs = queryResult.getResults();
			for (LS3Document doc: resultDocs)
				System.out.println(doc.getPNMLPath());
			System.out.println("-----------------------");
		}*/
		
		// Testing of LS3 querying
		LS3 ls3 = new LS3();
		QueryAllResult result = ls3.queryKAllPrintData("C:\\Wopedmodels", 14, 0.79f);
		
		System.out.println("Testing of similarity value inclusion into QueryResult.");
		
		ArrayList<QueryResult> qar = new ArrayList<QueryResult>();
		qar = result.getResults();
		/*for (QueryResult qr: qar) {
			System.out.println( "Query: "+qr.getQuery().getPNMLPath() );
			System.out.println("------------- Similar Models -------------");
			ArrayList<LS3Document> simModels = qr.getResults();
			ArrayList<Double> simValues = qr.getSimilarityValues();
			
			for (int i = 0; i < simModels.size();i++) {
				System.out.println( "Model: "+simModels.get(i).getPNMLPath()+", Similarity Value = "+simValues.get(i) );
			}
			System.out.println("--------------------------");
		}*/
		
	}

} 
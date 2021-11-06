package ie.tcd.storeydy;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BM25Similarity;


public class Main {

        public static void main(String[] args) throws Exception {
                //Make an Indexer
                Indexer indexCreator = new Indexer();

                //Create and store the index
                indexCreator.makeIndex();
		//Implements search over IndexReader
		//Directory directory = FSDirectory.open(Paths.get("index"));
		//DirectoryReader ireader = DirectoryReader.open(directory);
		//IndexSearcher isearcher = new IndexSearcher(ireader);
		//isearcher.setSimilarity(new BM25Similarity());
		//System.out.println("Similarity used: BM25 \n"); 

		//Utils utilities = new Utils();
		
		//File queries = new File("corpus/cran.qry");
		
		//ArrayList<ArrayList<String>> parsedQueries = utilities.parseQuesrys(queries);

		//File resultsFile = new File("src/main/resources/results/SimpleQueryBM25Scoring.txt");
		//FileWriter writer = new FileWriter("src/main/resources/results/SimpleQueryBM25Scoring.txt");
		
		//System.out.println("Tokenising Query with stop words using a Standard Analyser. Conducting query on Index and storing results in SimpleQueryBM25Scoring.txt \n");
		//for(int queryIndex = 0; queryIndex < parsedQueries.get(1).size(); queryIndex++){
		//		String text = parsedQueries.get(1).get(queryIndex);
		//		ArrayList<String> tokenisedQuery = utilities.tokeniseQuery(text);
				//System.out.println(tokenisedQuery);
		//		float[] docScoreCounter = utilities.makeQuery(tokenisedQuery, isearcher);
				//System.out.println(docScoreCounter);
		//		ArrayList<DocumentScore> docScores = utilities.rankDocuments(docScoreCounter);
				//System.out.println(docScores.size());
		//		for(int score = 0; score < docScores.size(); score++)
		//		{
		//		String result = parsedQueries.get(0).get(queryIndex) + " Q0 " + (docScores.get(score).documentNumber+1) + " 0 " + String.valueOf(docScores.get(score).score) + " SimpleQueryBM25Scoring" + "\n"; //rank ignored by trec_eval
		//		writer.write(result); 
				//String message = "DocumentNumber: " + docScores.get(score).documentNumber + ", score: " + String.valueOf(docScores.get(score).score) + "\n";
				//System.out.print(message);
		//		}
		//}
		//System.out.println("Query results acquired \n");
		//writer.close();
        }
}

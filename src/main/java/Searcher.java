import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.PrintWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
//import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Searcher {

    private static String RESULTS_PATH="results.txt";
    private static String INDEX="Index";
    private static String QUERY_PATH ="query/topics";

    public void main() throws Exception {
        org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(new File(QUERY_PATH), "UTF-8", "");

        String queryString = "";
        String topic_tag ="top";

        String topic_num ="num";
        String topic_title ="title";
        String topic_description ="desc";
        String topic_narrative ="narr";

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX)));
        IndexSearcher searcher = new IndexSearcher(reader);

        //Analyzer analyzer = new SimpleAnalyzer();
        //Analyzer analyzer = new WhitespaceAnalyzer();;
        Analyzer analyzer = new EnglishAnalyzer();

        PrintWriter writer = new PrintWriter(RESULTS_PATH, "UTF-8");

        //BM25 Similarity
        searcher.setSimilarity(new BM25Similarity());

        //Classic Similarity
        //searcher.setSimilarity(new ClassicSimilarity());


        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "DOCNO", "text"}, analyzer);

        System.out.println("Reading the queries and creating results");

        Elements topics = jsoupDoc.select(topic_tag);

        for (Element topicElement : topics) {
            String num = topicElement.getElementsByTag(topic_num).text();
            String title = topicElement.getElementsByTag(topic_title).text();
            String descStr = topicElement.getElementsByTag(topic_description).text();
            String narrativeStr = topicElement.getElementsByTag(topic_narrative).text();
            Pattern numberPattern = Pattern.compile("(\\d+)");
            Matcher numberMatcher = numberPattern.matcher(num);
            String number = "";
            if(numberMatcher.find()) {
                number = numberMatcher.group().trim();
            }

            descStr = descStr.replace("\n"," ");
            Pattern descPattern = Pattern.compile("Description: (.*)Narrative");
            Matcher descMatcher = descPattern.matcher(descStr);
            String desc = "";
            if(descMatcher.find()) {
                desc = descMatcher.group(1).trim();
            }

            String narrative = narrativeStr.replace("\n"," ").replace("Narrative: ","").trim();
            queryString += title;
            queryString += narrative;
            queryString = queryString.trim();
            Query query = parser.parse(QueryParser.escape(queryString));
            queryString = "";
            search(searcher,writer,number,query);

        }

        System.out.println("Results have been written to the file.");
        writer.close();
        reader.close();
    }

    public static void search(IndexSearcher searcher, PrintWriter writer,String num, Query query) throws IOException {

        TopDocs results = searcher.search(query, 1000);
        ScoreDoc[] hits = results.scoreDocs;

        for (int i = 0; i < hits.length; i++) {
            ScoreDoc hit = hits[i];
            writer.println(num + " 0 " + searcher.doc(hit.doc).get("DOCNO") + " " + i + " " + hits[i].score);
        }
    }
}
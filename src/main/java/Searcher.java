import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.PrintWriter;

import org.apache.lucene.analysis.core.StopAnalyzer;
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
import org.junit.Test;


public class Searcher {

    private static String RESULTS_PATH="results.txt";
    private static String INDEX="Index";
    private static String QUERY_PATH ="query/topics";

    @Test
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
        //Analyzer analyzer = new EnglishAnalyzer();

        // Define the stop words set and apply them into customer analyzer
        String[] StopWords =  {"a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", "and", "any", "are", "aren", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can", "couldn", "couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn", "doesn't", "doing", "don", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have", "haven", "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just", "ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn", "needn't", "no", "nor", "not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "re", "s", "same", "shan", "shan't", "she", "she's", "should", "should've", "shouldn", "shouldn't", "so", "some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn", "wasn't", "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "won", "won't", "wouldn", "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "could", "he'd", "he'll", "he's", "here's", "how's", "i'd", "i'll", "i'm", "i've", "let's", "ought", "she'd", "she'll", "that's", "there's", "they'd", "they'll", "they're", "they've", "we'd", "we'll", "we're", "we've", "what's", "when's", "where's", "who's", "why's", "would"};
        Analyzer analyzer = new MyCustomerAnalyzer.MyStopAnalyzer(StopWords);

        PrintWriter writer = new PrintWriter(RESULTS_PATH, "UTF-8");

        // BM25 Similarity
        // After testing many times, when k1 = 1.8, b = 0.85, it performs well
        searcher.setSimilarity(new BM25Similarity((float) 1.8, (float) 0.85));

        // Add weight to the key fields: 'title' and 'text'
        Map<String, Float> boosts = new HashMap<>();
        boosts.put("title", 0.1F);
        boosts.put("text", 0.9F);
        QueryParser parser = new MultiFieldQueryParser(new String[]{"DOCNO","title", "text"}, analyzer,boosts);

        System.out.println("Reading the queries and creating results");

        // Use jsoup to parse the document
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

        TopDocs results = searcher.search(query, 2000);
        ScoreDoc[] hits = results.scoreDocs;

        for (int i = 0; i < hits.length; i++) {
            ScoreDoc hit = hits[i];
            writer.println(num + " Q0 " + searcher.doc(hit.doc).get("DOCNO") + " " + i + " " + hits[i].score + " STANDARD");
        }
    }
}
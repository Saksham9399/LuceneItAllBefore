import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.similarities.BM25Similarity;

public class Indexer {

    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "index";
    private static String FINANCIAL_TIMES_DIRECTORY = "corpus/ft";
    private static String FEDERAL_REGISTER_DIRECTORY = "corpus/fr94";
    private static String FOREIGN_BROADCAST_INFORMATION_SERVICE_DIRECTORY = "corpus/fbis";
    private static String LA_TIMES_DIRECTORY = "corpus/latimes";
    private static String[] directories = {FINANCIAL_TIMES_DIRECTORY, FEDERAL_REGISTER_DIRECTORY, FOREIGN_BROADCAST_INFORMATION_SERVICE_DIRECTORY, LA_TIMES_DIRECTORY};

    public int docnum = 0;

    public void makeIndex() throws Exception {

        // Analyzer analyzer = new EnglishAnalyzer();
        String[] StopWords =  {"a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", "and", "any", "are", "aren", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can", "couldn", "couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn", "doesn't", "doing", "don", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have", "haven", "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just", "ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn", "needn't", "no", "nor", "not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "re", "s", "same", "shan", "shan't", "she", "she's", "should", "should've", "shouldn", "shouldn't", "so", "some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn", "wasn't", "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "won", "won't", "wouldn", "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "could", "he'd", "he'll", "he's", "here's", "how's", "i'd", "i'll", "i'm", "i've", "let's", "ought", "she'd", "she'll", "that's", "there's", "they'd", "they'll", "they're", "they've", "we'd", "we'll", "we're", "we've", "what's", "when's", "where's", "who's", "why's", "would"};
        Analyzer analyzer = new MyCustomerAnalyzer.MyStopAnalyzer(StopWords);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // BM25 Similarity
        // After testing many times, when k1 = 1.8, b = 0.85, it performs well
        config.setSimilarity(new BM25Similarity((float) 1.8, (float) 0.85));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        Directory indexDirectory = FSDirectory.open(Paths.get("./Index"));
        IndexWriter indexWriter = new IndexWriter(indexDirectory, config);

        for (String directory : directories) {
            File dir = new File(directory);
            File[] files = dir.listFiles();
            for (File file : files) {
                System.out.println(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/") + 3));
                if (file.isDirectory()) {
                    File[] filesInsideDirectory = file.listFiles();
                    for (File innerFile : filesInsideDirectory) {
                        // According to the table to find the key content|
                        // DOCNO: the document id; HEADLINE/DOCTITLE/H3: the title of the document; TEXT: the content of the document
                        if (innerFile.getAbsolutePath().substring(innerFile.getAbsolutePath().lastIndexOf("/") + 1, innerFile.getAbsolutePath().lastIndexOf("/") + 3).equals("ft")) {
                            // CALL FUNCTION TO PARSE FINANCIAL TIMES DOCUMENTS
                            String content = new String(Files.readAllBytes(Paths.get(innerFile.getAbsolutePath())));
                            org.jsoup.nodes.Document doc = Jsoup.parse(content, "", Parser.xmlParser());
                            for (Element e : doc.select("DOC")) {
                                String DOCNO = e.select("DOCNO").toString();
                                DOCNO = DOCNO.replace("<DOCNO>", "");
                                DOCNO = DOCNO.replace("</DOCNO>", "");
                                DOCNO = DOCNO.replace(",", " ");

                                String HEADLINE = e.select("HEADLINE").toString();
                                HEADLINE = HEADLINE.replace("<HEADLINE>", "");
                                HEADLINE = HEADLINE.replace("</HEADLINE>", "");
                                HEADLINE = HEADLINE.replace(",", " ");
                                HEADLINE = HEADLINE.replace("\n", " ");

                                String TEXT = e.select("TEXT").toString();
                                TEXT = TEXT.replace("<TEXT>", "");
                                TEXT = TEXT.replace("</TEXT>", "");
                                TEXT = TEXT.replace(",", " ");
                                TEXT = TEXT.replace("\n", " ");
                                add(indexWriter, DOCNO, HEADLINE, TEXT);
                            }
                        } else if (innerFile.getAbsolutePath().substring(innerFile.getAbsolutePath().lastIndexOf("/") + 1, innerFile.getAbsolutePath().lastIndexOf("/") + 3).equals("fr")) {
                            // CALL FUNCTION TO PARSE FEDERAL REGISTER DOCUMENTS
                            String content = new String(Files.readAllBytes(Paths.get(innerFile.getAbsolutePath())));
                            org.jsoup.nodes.Document doc = Jsoup.parse(content, "", Parser.xmlParser());
                            for (Element e : doc.select("DOC")) {
                                String DOCNO = e.select("DOCNO").toString();
                                DOCNO = DOCNO.replace("<DOCNO>", "");
                                DOCNO = DOCNO.replace("</DOCNO>", "");
                                DOCNO = DOCNO.replace(",", " ");

                                String TITLE = e.select("DOCTITLE").toString();
                                TITLE = TITLE.replace("<DOCTITLE>", "");
                                TITLE = TITLE.replace("</DOCTITLE>", "");
                                TITLE = TITLE.replace(",", " ");
                                TITLE = TITLE.replace("\n", " ");

                                String TEXT = e.select("TEXT").toString();
                                TEXT = TEXT.replace("<TEXT>", "");
                                TEXT = TEXT.replace("</TEXT>", "");
                                TEXT = TEXT.replace(",", " ");
                                TEXT = TEXT.replace("&blank;", " ");
                                TEXT = TEXT.replace("/&blank;", " ");
                                TEXT = TEXT.replace("\n", " ");
                                add(indexWriter, DOCNO, TITLE, TEXT);
                            }
                        }
                    }
                } else if ((file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("fb")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("ft")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("fr")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("la"))) {
                    if (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/") + 3).equals("fb")) {
                        // CALL FUNCTION TO PARSE FOREIGN BROADCAST INFORMATION SERVICE
                        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                        org.jsoup.nodes.Document doc = Jsoup.parse(content, "", Parser.xmlParser());
                        for (Element e : doc.select("DOC")) {
                            String DOCNO = e.select("DOCNO").toString();
                            DOCNO = DOCNO.replace("<DOCNO>", "");
                            DOCNO = DOCNO.replace("</DOCNO>", "");
                            DOCNO = DOCNO.replace(",", " ");

                            String title = e.select("H3").toString();
                            title = title.replace("<H3> <TI>", "");
                            title = title.replace("</TI></H3>", "");
                            title = title.replace(",", " ");
                            title = title.replace("\n", " ");

                            String text = e.select("TEXT").toString();
                            text = text.replace("<TEXT>", "");
                            text = text.replace("</TEXT>", "");
                            text = text.replace(",", " ");
                            add(indexWriter, DOCNO, title, text);
                        }
                    }
                    else if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/") + 3).equals("la")){
                        // CALL FUNCTION TO PARSE LOS ANGELES TIMES
                        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                        org.jsoup.nodes.Document doc = Jsoup.parse(content, "", Parser.xmlParser());
                        for (Element e : doc.select("DOC")) {
                            String DOCNO = e.select("DOCNO").toString();
                            DOCNO = DOCNO.replace("<DOCNO>", "");
                            DOCNO = DOCNO.replace("</DOCNO>", "");
                            DOCNO = DOCNO.replace(",", "");

                            String HEADLINE = e.select("HEADLINE").text();
                            String BYLINE =e.select("BYLINE").text();

                            String title = String.format("%s%s",HEADLINE,BYLINE);
                            title = title.replace(",","");
                            if (title.isEmpty()){
                                title = e.select("GRAPHIC").text();
                                title = title.replace(",","");
                            }
                            String text = e.select("TEXT").text();
                            text = text.replace(",", " ");
                            add(indexWriter, DOCNO, title, text);
                        }
                    }
                }
            }
        }
        indexWriter.close();
    }

    private void add(IndexWriter indexWriter, String DOCNO, String title, String text) throws Exception{
        // Create the TextField according to the label: DOCNO, title, text
        Document document = new Document();
        document.add(new TextField("DOCNO", DOCNO, Field.Store.YES));
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("text", text, Field.Store.YES));
        indexWriter.addDocument(document);
        docnum++;
        System.out.println(docnum);
    }
}
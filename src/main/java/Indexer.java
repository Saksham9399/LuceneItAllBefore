import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "index";
    private static String FINANCIAL_TIMES_DIRECTORY = "corpus/ft";
    private static String FEDERAL_REGISTER_DIRECTORY = "corpus/fr94";
    private static String FOREIGN_BROADCAST_INFORMATION_SERVICE_DIRECTORY = "corpus/fbis";
    private static String LA_TIMES_DIRECTORY = "corpus/latimes";
//    private static String csv = ""

    private static String[] directories = {FINANCIAL_TIMES_DIRECTORY, FEDERAL_REGISTER_DIRECTORY, FOREIGN_BROADCAST_INFORMATION_SERVICE_DIRECTORY, LA_TIMES_DIRECTORY};


    //private Utils utils = new Utils();
    public void makeIndex() throws Exception {

        //String currentDirectory = System.getProperty("user.dir");
        //System.out.println("The current working directory is " + currentDirectory);
        // Analyzer that is used to process TextField
        //StopList stopList = new StopList();
        //CharArraySet set = stopList.stopList;

        //StandardAnalyzer standardAnalyzer = new StandardAnalyzer(set);
        //standardAnalyzer.setVersion(Version.LUCENE_8_10_0);

        //Organise and parse the documents
        // File file = new File(CORPUS_FILE);
        // if (!file.exists()){
        //         System.out.print("File Not Found: 404\n");
        // }
        //ArrayList<ArrayList<String>> fields = utils.parseDocuments(file);

        //Path innerDirectory = Paths.get(FINANCIAL_TIMES_DIRECTORY);
        String filePath = "";
        String file_fbis = filePath + "fbis.csv";
        String file_fr = filePath + "fr.csv";
        String file_ft = filePath + "ft.csv";
        String file_la = filePath + "la.csv";

        BufferedWriter fw = null;
        fw = new BufferedWriter(new FileWriter(file_ft, true));
        String header = "DOCNO,HEADLINE,TEXT";
        fw.write(header);
        fw.newLine();

        BufferedWriter fb = null;
        fb = new BufferedWriter(new FileWriter(file_fb, true));
        fb.write(header);
        fb.newLine();
        for (String directory : directories) {
            File dir = new File(directory);
            File[] files = dir.listFiles();

            for (File file : files) {
                System.out.println(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/") + 3));
                if (file.isDirectory()) {
                    File[] filesInsideDirectory = file.listFiles();
                    for (File innerFile : filesInsideDirectory) {
                        if (innerFile.getAbsolutePath().substring(innerFile.getAbsolutePath().lastIndexOf("/") + 1, innerFile.getAbsolutePath().lastIndexOf("/") + 3).equals("ft")) {
                            String content = new String(Files.readAllBytes(Paths.get(innerFile.getAbsolutePath())));
                            Document doc = Jsoup.parse(content, "", Parser.xmlParser());
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
                                System.out.println(DOCNO);
                                System.out.println(HEADLINE);
                                System.out.println(TEXT);
                                fw.write(DOCNO);
                                fw.write(',');
                                fw.write(HEADLINE);
                                fw.write(',');
                                fw.write(TEXT);
                                fw.newLine();
                            }
//                            Elements tests = doc.getElementsByTag("DOCNO");
//                            for (Element testElement : tests) {
//                                System.out.println(testElement.getElementsByTag("DOCNO"));
//                            }
                            // CALL FUNCTION TO PARSE FINANCIAL TIMES DOCUMENTS
                        } else if (innerFile.getAbsolutePath().substring(innerFile.getAbsolutePath().lastIndexOf("/") + 1, innerFile.getAbsolutePath().lastIndexOf("/") + 3).equals("fr")) {
                            // CALL FUNCTION TO PARSE FEDERAL REGISTER DOCUMENTS
                        }
                    }
                } else if ((file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("fb")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("ft")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("fr")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("la"))) {
                    if (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/") + 3).equals("fb")) {
                        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                        Document doc = Jsoup.parse(content, "", Parser.xmlParser());
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
                            //text = text.replace("\n", " ");
                            //System.out.println(DOCNO);
                            //System.out.println(title);
                            //System.out.println(text);
                            fb.write(DOCNO);
                            fb.write(',');
                            fb.write(title);
                            fb.write(',');
                            fb.write(text);
                            fb.newLine();
                        }
                    else
                        if (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/") + 3).equals("la")) {
                            // CALL FUNCTION TO PARSE LA TIMES DOCUMENTS
                        }

                    }

                }


            }
            fw.close();

        }
        fb.close();
    }

}

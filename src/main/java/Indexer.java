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
    public void makeIndex() throws Exception
    {

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

        BufferedWriter fw=null;
        fw = new BufferedWriter(new FileWriter(file_ft,true));
        String header = "DOCNO,HEADLINE,TEXT";
        fw.write(header);
        fw.newLine();
        for(String directory : directories)
        {
            File dir = new File(directory);
            File[] files = dir.listFiles();

            for(File file : files)
            {
                System.out.println(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1, file.getAbsolutePath().lastIndexOf("/")+3));
                if(file.isDirectory())
                {
                    File[] filesInsideDirectory = file.listFiles();
                    for(File innerFile : filesInsideDirectory)
                    {
                        if(innerFile.getAbsolutePath().substring(innerFile.getAbsolutePath().lastIndexOf("/") + 1, innerFile.getAbsolutePath().lastIndexOf("/")+3).equals("ft"))
                        {
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
                        }
                        else if(innerFile.getAbsolutePath().substring(innerFile.getAbsolutePath().lastIndexOf("/") + 1, innerFile.getAbsolutePath().lastIndexOf("/")+3).equals("fr"))
                        {
                            // CALL FUNCTION TO PARSE FEDERAL REGISTER DOCUMENTS
                        }
                    }
                }
                else if( (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("fb")) ||  (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("ft")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("fr")) || (file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1, (file.getAbsolutePath().lastIndexOf("/") + 3)).equals("la")))
                {
                    if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/")+3).equals("fb"))
                    {
                        // CALL FUNCTION TO PARSE FOREIGN BROADCAST DOCUMENTS
                    }
                    else if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("/")+3).equals("la"))
                    {
                        // CALL FUNCTION TO PARSE LA TIMES DOCUMENTS
                    }

                }

            }


        }
        fw.close();










        //for(File file : files)
        //	{
        //		if(file.isDirectory()){
        // System.out.println("Directory: " + file.getAbsolutePath());
        //		File[] filesInsideDirectory = file.listFiles();
        //		for(File innerFile : filesInsideDirectory){
        //			System.out.println("File: " + innerFile.getAbsolutePath());
        //			//ArrayList<ArrayList<String>> fields = this.utils.parseDocuments(innerFile);
        //			ParsedDocResults parsed = this.utils.parseFinancialTimesDocuments(innerFile);
        //			ArrayList<ArrayList<String>> fields = parsed.fields;
        //			int docCount = parsed.docCount;

        // 			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //			Document doc = new Document();
        //			FieldType type = new FieldType();
        //			type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        //			type.setStored(true);
        //			type.setTokenized(true);
        //			type.setStoreTermVectors(true);
        //			for(int i = 0; i < docCount; i++){
        //				for(int j = 0; j < fields.size(); j++){
        //				switch(j){
        //					case 0:
        //doc.add((IndexableField)new TextField("Document_number", ((ArrayList<String>)fields.get(j)).get(i), Field.Store.YES));
        //					break;

        //					case 1:
        //doc.add((IndexableField)new Field("Title", ((ArrayList<CharSequence>)fields.get(j)).get(i), (IndexableFieldType)type));
        //					break;

        //					case 2:
        //doc.add((IndexableField)new Field("Text", ((ArrayList<CharSequence>)fields.get(j)).get(i), (IndexableFieldType)type));
        //					break;
        //				}
        //				}
        //			documents.add(doc);
        //			doc = new Document();
        //			}
        //		}

        //		}
        //	}
        //	iwriter.addDocuments(documents);
        //	iwriter.close();
        //	fsDirectory.close();
        //	System.out.println("Indexing done \n \n");

        //Files.walk(dir).forEach(path -> {
        //    if(path.toFile().isDirectory())
        //    {
        //        System.out.println("Directory: " + path.toFile.getAbsolutePath());
        //    }
        // });







        // To store an index in memory
        // Directory directory = new RAMDirectory();
        // To store an index on disk
        //Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Set up an index writer to add process and save documents to the index
        //IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //IndexWriter iwriter = new IndexWriter(directory, config);

        //ArrayList<Document> documnets = new ArrayList<Document>();

        // Create a new document
        //Document doc = new Document();

        //Create a custom field type
        //FieldType type = new FieldType();
        //type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        //type.setStored(true);
        //type.setTokenized(true);
        //type.setStoreTermVectors(true);

        // for (int i = 0; i < 1400; i++) {
        //         for (int j = 0; j < fields.size(); j++) {
        //                 //Use switch to get the parsed data under the right heading
        //                 switch(j) {
        //                 case 0:
        //                         doc.add(new TextField("Document_number", fields.get(j).get(i), Field.Store.YES));
        //                         System.out.print("Indexing Document: ");
        //                         System.out.print(fields.get(j).get(i));
        //                         System.out.print("\n");
        //                         break;
        //                 case 1:
        //                         doc.add(new Field("Title", fields.get(j).get(i), type));
        //                         break;
        //                 case 2:
        //                         doc.add(new Field("Author", fields.get(j).get(i), type));
        //                         break;
        //                 case 3:
        //                         doc.add(new Field("Journal", fields.get(j).get(i), type));
        //                         break;
        //                 case 4:
        //                         doc.add(new Field("Text", fields.get(j).get(i), type));
        //                         break;
        //                 }
        //         }
        //         // Save the document to the index
        //         documnets.add(doc);
        //         doc = new Document();
        // }

        // iwriter.addDocuments(documnets);

        // // Commit changes and close everything
        // iwriter.close();
        // directory.close();
        // System.out.print("Indexing done\n\n");
    }

}

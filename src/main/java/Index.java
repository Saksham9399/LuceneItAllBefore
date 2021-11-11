import java.io.*;
import java.lang.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.*;

public class Index {
    // Create the index
    public void createIndex(Analyzer s) throws Exception{
        // Specified analyzer and initialize the IndexWriter
        Analyzer analyzer = s;
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        Directory directory = FSDirectory.open(Paths.get("./Index"));
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // The path to the original document
        File file = new File("fbis.csv");
        File[] fileList = file.listFiles();
        for(File file2 : fileList){
            if(file2.isFile()){
                // Create a document object
                Document doc = new Document();
                // Create a field object and add the field to the document object
                BufferedReader csvReader = new BufferedReader(new FileReader(file2));
                String line = csvReader.readLine();
                line = csvReader.readLine();
                String[] item =line.split(",");
                String id = item[0];
                String tittle = item[1];
                String author = item[2];
                String bibliography = item[3];
                Field idField = new TextField("id", id, Field.Store.YES);
                doc.add(idField);
                Field tittleField = new TextField("tittle", tittle, Field.Store.YES);
                doc.add(tittleField);
                Field authorField = new TextField("author", author, Field.Store.YES);
                doc.add(authorField);
                Field bibliographyField = new TextField("bibliography", bibliography, Field.Store.YES);
                doc.add(bibliographyField);
                if(item.length == 5){
                    String content = item[4];
                    Field contentField = new TextField("content", content, Field.Store.YES);
                    doc.add(contentField);
                }
                // Print the doc id and the tittle to check
                System.out.println(item[0]);
                System.out.println(item[1]);
                // Write the document object to the index library using the indexWriter object.
                // This process creates the index and writes the index and the Document object to the index library
                indexWriter.addDocument(doc);
            }
        }
        // Close the indexWriter object
        indexWriter.close();
    }
}

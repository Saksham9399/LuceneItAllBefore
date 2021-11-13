// main function to run the code
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;


public class driver_program {
    public static void main(String[] args) throws Exception {
        //CorpusParser CorpusParser = new CorpusParser();
        //CorpusParser.parseIndex();

        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        standardAnalyzer.setVersion(Version.LUCENE_8_10_0);

        Index indexer = new Index();
        indexer.createIndex(standardAnalyzer);

    }
}

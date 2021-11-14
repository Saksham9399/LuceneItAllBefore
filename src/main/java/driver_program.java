// main function to run the code
public class driver_program {
    public static void main(String[] args) throws Exception {
        Indexer Indexer= new Indexer();
        Indexer.makeIndex();
        Searcher Searcher = new Searcher();
        Searcher.main();

    }
}

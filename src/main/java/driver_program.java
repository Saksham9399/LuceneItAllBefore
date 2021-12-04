// main function to run the code
public class driver_program {
    public static void main(String[] args) throws Exception {
        // Create index files and save them
        Indexer Indexer= new Indexer();
        Indexer.makeIndex();
        // Search the 'topics' in the index files
        Searcher Searcher = new Searcher();
        Searcher.main();

    }
}

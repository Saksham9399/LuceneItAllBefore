import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MyCustomerAnalyzer {
    public static class MyStopAnalyzer extends Analyzer {

        // Define the set of stop words
        private CharArraySet stopWordsSet;

        public  MyStopAnalyzer(String[] stopWords) {
            stopWordsSet  = StopFilter.makeStopSet(stopWords, true);  //CharArraySet
        }

        @Override
        // Rewrite the Token Stream
        protected TokenStreamComponents createComponents(String arg0) {
            // Define the standard tokenizer
            Tokenizer tokenizer = new StandardTokenizer();
            // Define the token steam as lowercase filter
            TokenStream tokenStream = new LowerCaseFilter(tokenizer);
            // Add other filters
            tokenStream = new TrimFilter(tokenStream);
            // Add SynonymGraphFilter in the FlattenGraphFilter
            // Apply two texts of synonym and the detail code in the method: getSynonymMap()
            tokenStream = new FlattenGraphFilter(new SynonymGraphFilter(tokenStream, getSynonymMap(), true));
            tokenStream = new StopFilter(tokenStream, stopWordsSet);
            tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());
            tokenStream = new KStemFilter(tokenStream);
            return new TokenStreamComponents(tokenizer, tokenStream);
        }

        // Apply two texts of synonym
        private SynonymMap getSynonymMap(){
            SynonymMap smap = new SynonymMap(null, null, 0);
            try{
                SynonymMap.Builder sb = new SynonymMap.Builder(true);
                // The first synonym dataset, make 'country' and 'countries' become synonymous with the names of countries
                BufferedReader SynonymsFirst = new BufferedReader(new FileReader("country.txt"));
                String FirstSynonym = SynonymsFirst.readLine();
                String SecondSynonym = SynonymsFirst.readLine();
                String Synonym = SynonymsFirst.readLine();
                while(Synonym != null){
                    System.out.println(Synonym);
                    sb.add(new CharsRef(FirstSynonym), new CharsRef(Synonym), true);
                    sb.add(new CharsRef(SecondSynonym), new CharsRef(Synonym), true);
                    Synonym = SynonymsFirst.readLine();
                }

                // The second synonym dataset, mainly contains the English synonyms
//                BufferedReader SynonymSecond = new BufferedReader(new FileReader("sny.txt"));
//                String Synonym = SynonymSecond.readLine();
//                while(Synonym!=null){
//                    String item[] = Synonym.split(", ");
//                    for (int i = 1; i < item.length; i++) {
//                        sb.add(new CharsRef(item[0]), new CharsRef(item[i]), true);
//                    }
//                    Synonym = SynonymSecond.readLine();
//                }

                smap = sb.build();
            } catch (Exception e) {
                System.out.println("SynonymMap occurs wrong!");
            }
            return smap;
        }
    }
}

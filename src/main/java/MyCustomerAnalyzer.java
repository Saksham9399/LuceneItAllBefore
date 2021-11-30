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

        private CharArraySet stopWordsSet;

        public  MyStopAnalyzer(String[] stopWords) {
            stopWordsSet  = StopFilter.makeStopSet(stopWords, true);  //CharArraySet
        }

        @Override
        protected TokenStreamComponents createComponents(String arg0) {
            Tokenizer tokenizer = new StandardTokenizer();

//            LowerCaseFilter lowerCaseFilter = new LowerCaseFilter(letterTokenizer);
//            TokenStream tokenStream = lowerCaseFilter;
//            TokenFilter synonymFilter = new SynonymFilter(tokenStream, getSynonymMap(), true);
//            StopFilter stopFilter = new StopFilter(synonymFilter, stopWordsSet);
//            TokenFilter stemFilter = new PorterStemFilter(stopFilter);
            // create new token stream
            TokenStream tokenStream = new LowerCaseFilter(tokenizer);
            // add filters
            tokenStream = new TrimFilter(tokenStream);
            tokenStream = new FlattenGraphFilter(new SynonymGraphFilter(tokenStream, getSynonymMap(), true));
            tokenStream = new StopFilter(tokenStream, stopWordsSet);
            tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());
            tokenStream = new KStemFilter(tokenStream);
            return new TokenStreamComponents(tokenizer, tokenStream);
        }

        // Synonym
        private SynonymMap getSynonymMap(){
            SynonymMap smap = new SynonymMap(null, null, 0);
            try{
                BufferedReader countries = new BufferedReader(new FileReader("country.txt"));
                String country = countries.readLine();
                System.out.println(country);
                SynonymMap.Builder sb = new SynonymMap.Builder(true);
                while(country!=null){
                    System.out.println(country);
                    sb.add(new CharsRef("country"), new CharsRef(country), true);
                    sb.add(new CharsRef("countries"), new CharsRef(country), true);
                    country = countries.readLine();
                }
//                BufferedReader Synonyms = new BufferedReader(new FileReader("sny.txt"));
//                String Synonym = Synonyms.readLine();
//                while(Synonym!=null){
//                    String item[] = Synonym.split(", ");
//                    for (int i = 1; i < item.length; i++) {
//                        sb.add(new CharsRef(item[0]), new CharsRef(item[i]), true);
//                    }
//                    Synonym = Synonyms.readLine();
//                }
                smap = sb.build();
            } catch (Exception e) {
                System.out.println("SynonymMap occurs wrong!");
            }
            return smap;
        }
    }
}

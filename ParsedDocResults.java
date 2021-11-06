package ie.tcd.storeydy;

import java.util.ArrayList;

public class ParsedDocResults {
        public ArrayList<ArrayList<String>> fields;
        public int docCount;

        ParsedDocResults(ArrayList<ArrayList<String>> f, int c){
                fields = f;
                docCount = c;
        }

        //public int compareTo(DocumentScore doc) {
        //        return Float.compare(doc.score, this.score);
       // }
}


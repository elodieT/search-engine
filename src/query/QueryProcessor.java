package query;

import com.mongodb.*;

import javax.management.Query;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by sony on 09/12/2015.
 */
public class QueryProcessor {

    ArrayList<String> wordsQuery;

    public QueryProcessor(String query){
        this.wordsQuery=new ArrayList<>();
        try {
            File stopList = new File("./stopwords.txt");
            ArrayList <String> stopWords = new ArrayList<String>();
            Scanner scStopWords = null;
            scStopWords = new Scanner(stopList);
            while(scStopWords.hasNext()){
                stopWords.add(scStopWords.next());
            }

            for (String sw : stopWords) {
                query=query.replaceAll(" "+sw+" ", " ");
            }
            query = query.toLowerCase();
            //textePropre = textePropre.replaceAll("é", "e");

            //parsage du texte
            Scanner scanner = new Scanner(query);
            scanner.useDelimiter(" |,|\\.|;|!|\\?|\\n|'");
            String newWord="";

            while(scanner.hasNext()) {
                newWord = scanner.next();
                if(newWord.length()>7){
                    newWord = newWord.substring(0, 7);
                }
                if (!(wordsQuery.contains(newWord))){
                    wordsQuery.add(newWord);
                }
            }
            scStopWords.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,Integer> processQuery(DBCollection table, String modePoids, String modePerti){

        //TODO : changer l'intérieur de la fonction pour moduler selon les modes
        /* poids : tfidf TFIDF -tf TF
        perti : 1 somme - 2 cos
         */

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (String str : this.wordsQuery){
            BasicDBObject query = new BasicDBObject("word", str);
            DBCursor cursor = table.find(query);
            BasicDBList list;
            ArrayList<String> docsForWord= new ArrayList<>();
            try {
                while (cursor.hasNext()) {
                    DBObject object =cursor.next();
                    list =(BasicDBList)(object.get("documents"));
                    for (int i=0;i<list.size();i++){
                        String docName=list.get(i).toString();
                        docsForWord.add(docName);
                        int tfidfdoc = (Integer)((DBObject) (object.get(docsForWord.get(i)))).get(modePoids);
                        if(map.get(docName)==null){
                            map.put(docName,tfidfdoc ) ;
                        }
                        else{
                           map.put(docName,map.get(docName)+tfidfdoc);
                        }
                    }

                }
            } finally {
                cursor.close();
            }
        }



        Object[] a = map.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
                        ((Map.Entry<String, Integer>) o1).getValue());
            }
        });
        for (Object e : a) {
            System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
                    + ((Map.Entry<String, Integer>) e).getValue());
        }
        return map;
    }
}

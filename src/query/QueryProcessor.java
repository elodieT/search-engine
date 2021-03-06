package query;

import java.util.HashMap;
import com.mongodb.*;
import sparqlclient.Reformulator;

import javax.management.Query;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.*;

public class QueryProcessor {
    ArrayList<String> wordsQuery;

    public QueryProcessor(String query, boolean reform, Reformulator ref) {
        this.wordsQuery = new ArrayList<>();
        try {
            if(reform){
                query = ref.reformulate(query);
            }
            String delimiter = " |,|\\.|;|\\?|\\n|'|\\-|:|\\!|_|\\(|\\)|%|�|\\+|&|\"|\\#|\\{|\\}|=|\\[|\\]";
            File stopList = new File("./stopwords.txt");
            ArrayList<String> stopWords = new ArrayList<String>();
            Scanner scStopWords = null;
            scStopWords = new Scanner(stopList);
            while (scStopWords.hasNext()) {
                stopWords.add(scStopWords.next());
            }
            scStopWords.close();
            query = query.toLowerCase();
            query = query.replaceAll("�", "e");
            query = query.replaceAll("�", "e");
            query = query.replaceAll("�", "e");
            query = query.replaceAll("�", "e");
            query = query.replaceAll("�", "a");
            query = query.replaceAll("�", "a");
            query = query.replaceAll("�", "a");
            query = query.replaceAll("�", "i");
            query = query.replaceAll("�", "i");
            query = query.replaceAll("�", "i");
            query = query.replaceAll("�", "o");
            query = query.replaceAll("�", "o");

            // parsage du texte
            Scanner scanner = new Scanner(query);
            scanner.useDelimiter(delimiter);
            String newWord = "";
            while (scanner.hasNext()) {
                newWord = scanner.next();
                if (!newWord.isEmpty() && !stopWords.contains(newWord)) {

                    if (newWord.length() > 7) {
                        newWord = newWord.substring(0, 7);
                    }
                    if (!(wordsQuery.contains(newWord))) {
                        wordsQuery.add(newWord);
                    }
                }
            }

            scStopWords.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public HashMap<String, Integer> processQuerySomme(DBCollection table, String modePoids) {
		/*
		 * poids : tfidf TFIDF -tf TF - locationweight
		 */
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (String str : this.wordsQuery) {
            BasicDBObject query = new BasicDBObject("word", str);
            DBCursor cursor = table.find(query);
            BasicDBList list;
            ArrayList<String> docsForWord = new ArrayList<>();
            try {
                if (cursor != null) {
                    while (cursor.hasNext()) {
                        DBObject object = cursor.next();
                        list = (BasicDBList) (object.get("documents"));
                        for (int i = 0; i < list.size(); i++) {
                            String docName = list.get(i).toString();
                            docsForWord.add(docName);
                            int poids = (Integer) ((DBObject) (object.get(docsForWord.get(i)))).get(modePoids);
                            if (map.get(docName) == null) {
                                map.put(docName, poids);
                            } else {
                                map.put(docName, map.get(docName) + poids);
                            }
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
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });
		/*for (Object e : a) {
			
			 System.out.println(((Map.Entry<String, Integer>) e).getKey() +
			 " : " + ((Map.Entry<String, Integer>) e).getValue());
			
		}
		*/

        return map;
    }

    public HashMap<String, Integer> processQueryCos(DB db, String modePoids) {
		/*
		 * poids : tfidf TFIDF -tf TF
		 */
        HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
        DBCollection table = db.getCollection("mot");
        DBCollection tableDoc = db.getCollection("documents");
        HashMap<String, Integer> mapPropre = new HashMap<>();
        for (int numMot = 0; numMot < wordsQuery.size(); numMot++) {
            String str = wordsQuery.get(numMot);
            BasicDBObject query = new BasicDBObject("word", str);
            DBCursor cursor = table.find(query);
            BasicDBList list;
            ArrayList<String> docsForWord = new ArrayList<>();
            try {
                if (cursor != null) {
                    while (cursor.hasNext()) {
                        DBObject object = cursor.next();
                        list = (BasicDBList) (object.get("documents"));
                        for (int i = 0; i < list.size(); i++) {
                            String docName = list.get(i).toString();
                            docsForWord.add(docName);
                            int poids = (Integer) ((DBObject) (object.get(docsForWord.get(i)))).get(modePoids);
                            if (map.get(docName) == null) {
                                map.put(docName, new ArrayList<>());
                                for (int j = 0; j < wordsQuery.size(); j++) {
                                    map.get(docName).add(j, 0);
                                }
                                map.get(docName).add(numMot, poids);
                            } else {
                                map.get(docName).add(numMot, poids);
                            }
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }

        for (String docName : map.keySet()) {
            mapPropre.put(docName, 0);

            for (int i = 0; i < wordsQuery.size(); i++) {
                mapPropre.put(docName, mapPropre.get(docName) + map.get(docName).get(i));

            }
            BasicDBObject query = new BasicDBObject("document", docName);
            DBCursor cursor = tableDoc.find(query);
            int norme = 1;
            try {
                if (cursor != null) {
                    while (cursor.hasNext()) {
                        DBObject object = cursor.next();
                        norme = (int) (object.get(modePoids + "Norme"));
                        // System.out.println(norme);
                    }
                }
            } finally {
                cursor.close();
            }
            mapPropre.put(docName, (int) (100 * (double) (mapPropre.get(docName))
                    / (Math.sqrt((double) (wordsQuery.size())) * (double) (norme))));

            //System.out.println(mapPropre.get(docName));
        }
        Object[] a = mapPropre.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });
		/*for (Object e : a) {
			
			 System.out.println(((Map.Entry<String, Integer>) e).getKey() +
			 " : " + ((Map.Entry<String, Integer>) e).getValue());
			 
		}*/
        return mapPropre;
    }


    public HashMap<String, Integer> processQuerySommePonde(DBCollection table, String modePoids) {
		/*
		 * poids : tfidf TFIDF -tf TF - locationweight
		 */
        HashMap<String, ArrayList<Integer>> map = new HashMap<>();
        HashMap<String,Integer> mapret = new HashMap<>();
        /**
         * une entr�e de map : Array[0] = somme des poids
         *                     Array[1] = nombre de mots de la query qui sont dans le doc
         */
        for (String str : this.wordsQuery) {
            BasicDBObject query = new BasicDBObject("word", str);
            DBCursor cursor = table.find(query);
            BasicDBList list;
            ArrayList<String> docsForWord = new ArrayList<>();
            try {
                if (cursor != null) {
                    while (cursor.hasNext()) {
                        DBObject object = cursor.next();
                        list = (BasicDBList) (object.get("documents"));
                        for (int i = 0; i < list.size(); i++) {
                            String docName = list.get(i).toString();
                            docsForWord.add(docName);
                            int poids = (Integer) ((DBObject) (object.get(docsForWord.get(i)))).get(modePoids);
                            if (map.get(docName) == null) {
                                map.put(docName, new ArrayList<>());
                                map.get(docName).add(poids);
                                map.get(docName).add(1);
                            } else {
                                map.get(docName).set(0,map.get(docName).get(0)+poids);
                                map.get(docName).set(1,map.get(docName).get(1)+1);
                            }
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }

        for(String key : map.keySet()){
            //mapret.put(key,(int)((float)(map.get(key).get(0))/((float)(map.get(key).get(1))/(float)(wordsQuery.size()))));
            mapret.put(key,map.get(key).get(0)*map.get(key).get(1));
        }
       /* Object[] a = mapret.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });
		for (Object e : a) {

			 System.out.println(((Map.Entry<String, Integer>) e).getKey() +
			 " : " + ((Map.Entry<String, Integer>) e).getValue());

		}*/

        return mapret;
    }

}

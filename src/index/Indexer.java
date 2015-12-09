package index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.mongodb.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Indexer {
	String corpusPath;

	public Indexer (String corpusPath){
		this.corpusPath = corpusPath;
	}
	
	public void buildIndex(){
		//avec Jsoup, doc.text() = doc.body().text() + doc.title()
		try{
			int nbTotDoc=0;
			File folder = new File(corpusPath);
			File[] listOfFiles = folder.listFiles();
			File stopList = new File("./stopwords.txt");
			ArrayList <String> stopWords = new ArrayList<String>();
			HashMap <String,Words> mapMots = new HashMap <String,Words>();
			Scanner scStopWords = new Scanner(stopList);
			while(scStopWords.hasNext()){
				stopWords.add(scStopWords.next());
			}
			scStopWords.close();
		
			for (File file : listOfFiles) {
			    if (file.isFile()) {
					nbTotDoc++;
					Document doc = Jsoup.parse(file, "UTF-8");
					String filename = file.getAbsolutePath().toString().replace(folder.getAbsolutePath().toString(), "");
					filename = filename.replaceAll("\\.|\\\\|/|html","");
					//nettoyage du texte
					String textePropre=doc.text();
					for (String sw : stopWords) {
						textePropre=textePropre.replaceAll(" "+sw+" ", " ");
					}
					textePropre = textePropre.toLowerCase();
					//textePropre = textePropre.replaceAll("é", "e");
					
					//parsage du texte
					 Scanner scanner = new Scanner(textePropre);
					 scanner.useDelimiter(" |,|\\.|;|!|\\?|\\n|'");
					 String newWord="";
					
					 while(scanner.hasNext()){
						 newWord=scanner.next();
						 if(newWord.length()>7){
							 newWord = newWord.substring(0, 7);
						 }
						 if(mapMots.get(newWord)!=null){

							 if(mapMots.get(newWord).lastDoc().compareTo(filename)==0){
								 mapMots.get(newWord).incrementTFforLastDoc();
							 }
							 else{
								 mapMots.get(newWord).addDoc(filename);
							 }
						}
						 else{
							 mapMots.put(newWord, new Words(filename));
						 }
					 }		
			    }
			}
			for(String m: mapMots.keySet()){
				mapMots.get(m).calculateTFIDF(nbTotDoc);
				System.out.println(m + " " + mapMots.get(m).print());
				//TFIDF=1/(1+log(N/ni))
			}
			System.out.println(mapMots.size());
			//getTable
			/*
			 * pour chaque mot : nouveau document contenant une entrée par "document":[TF,TFIDF,score alpha]
			 * ex : 
			 * {brutus:{D1:[1,1,0]}}
			 * 
			 * Pour les lib: bson + mongo
			 * 
			 */
			MongoClient mongoClient = new MongoClient("localhost",27017);

			DB db = mongoClient.getDB("inverseIndexDB");
			DBCollection table = db.getCollection("mot");
			for(String m: mapMots.keySet()) {
				BasicDBObject doc = new BasicDBObject("word", m);
				BasicDBList list = new BasicDBList();
				for(int i=0; i<mapMots.get(m).numberOfDoc();i++){
					doc.append(mapMots.get(m).documents.get(i),new BasicDBObject("tf",mapMots.get(m).tf.get(i))
							.append("tfidf",mapMots.get(m).tfidf.get(i)));
					list.add(mapMots.get(m).documents.get(i));
				}
				doc.append("documents",list);
				table.insert(doc);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
					Document doc = Jsoup.parse(file, "UTF-8");
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
							 if(mapMots.get(newWord).lastDoc().compareTo(file.getPath())==0){
								 mapMots.get(newWord).incrementTFforLastDoc();
							 }
							 else{
								 mapMots.get(newWord).addDoc(file.getPath());
							 }
						}
						 else{
							 mapMots.put(newWord, new Words(file.getPath()));
						 }
					 }		
			    }
			}
			for(String m: mapMots.keySet()){
				mapMots.get(m).calculateTFIDF();
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
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

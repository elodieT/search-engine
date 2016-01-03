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

	public Indexer(String corpusPath) {
		this.corpusPath = corpusPath;
	}

	public void buildIndex() {
		// avec Jsoup, doc.text() = doc.body().text() + doc.title()
		try {
			String delimiter = " |,|\\.|;|\\?|\\n|'|\\-|:|\\!|_|\\(|\\)|%|°|\\+|&|\"|\\#|\\{|\\}|=|\\[|\\]";
			int nbTotDoc = 0;
			File folder = new File(corpusPath);
			File[] listOfFiles = folder.listFiles();
			File stopList = new File("./stopwords.txt");
			ArrayList<String> stopWords = new ArrayList<String>();
			HashMap<String, Words> mapMots = new HashMap<String, Words>();
			HashMap<String, DocumentText> mapDocs = new HashMap<>();
			Scanner scStopWords = new Scanner(stopList);
			while (scStopWords.hasNext()) {
				stopWords.add(scStopWords.next());
			}
			scStopWords.close();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					nbTotDoc++;
					Document doc = Jsoup.parse(file, "UTF-8");
					String filename = file.getAbsolutePath().toString().replace(folder.getAbsolutePath().toString(),"");
					filename = filename.replaceAll("\\.|\\\\|/|html", "");
					int nbMotTemp = 0;

					// nettoyage du texte
					String textePropre = doc.text();
					String textePropreBody = doc.body().text();
					String textePropreHead = doc.head().text();
					textePropre = textePropre.toLowerCase();
					textePropreHead = textePropreHead.toLowerCase();
					textePropreBody = textePropreBody.toLowerCase();
					textePropre = enleverAccents(textePropre);
					textePropreHead = enleverAccents(textePropreHead);
					textePropreBody = enleverAccents(textePropreBody);

					// parsage du texte
					Scanner scanner = new Scanner(textePropre);
					Scanner scannerHead = new Scanner(textePropreHead);
					Scanner scannerBody = new Scanner(textePropreBody);
					scanner.useDelimiter(delimiter);
					scannerHead.useDelimiter(delimiter);
					scannerBody.useDelimiter(delimiter);
					String newWord = "";

					while(scanner.hasNext()){
						newWord=scanner.next();
						if(!newWord.isEmpty() && !stopWords.contains(newWord)){
							if(newWord.length()>7){
								newWord = newWord.substring(0, 7);
							}
							if(mapMots.get(newWord)!=null){
								nbMotTemp ++;
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

					while (scannerHead.hasNext()){
						newWord=scannerHead.next();
						if(!newWord.isEmpty() && !stopWords.contains(newWord)){
							if(newWord.length()>7){
								newWord = newWord.substring(0, 7);
							}
							if(mapMots.get(newWord)==null){
								System.out.println(newWord);
							}
							mapMots.get(newWord).incrementLocationWeight(10);
						}
					}

					while (scannerBody.hasNext()){
						newWord=scannerBody.next();
						if(!newWord.isEmpty() && !stopWords.contains(newWord)){
							if(newWord.length()>7){
								newWord = newWord.substring(0, 7);
							}
							mapMots.get(newWord).incrementLocationWeight(1);
						}
					}

					mapDocs.put(filename,new DocumentText(nbMotTemp));
				}
			}
			for (String m : mapMots.keySet()) {
				mapMots.get(m).calculateTFIDF(nbTotDoc);
				mapMots.get(m).calculateLocWeightIDF(nbTotDoc);
				// System.out.println(m + " " + mapMots.get(m).print());
			}
			System.out.println(mapMots.size());
			// getTable
			/*
			 * pour chaque mot : nouveau document contenant une entrée par
			 * "document":[TF,TFIDF,score alpha] ex : {brutus:{D1:[1,1,0]}}
			 *
			 * Pour les lib: bson + mongo
			 *
			 */
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("inverseIndexDB");
			DBCollection table = db.getCollection("mot");
			for (String m : mapMots.keySet()) {
				BasicDBObject doc = new BasicDBObject("word", m);
				BasicDBList list = new BasicDBList();
				for (int i = 0; i < mapMots.get(m).numberOfDoc(); i++) {
					doc.append(mapMots.get(m).documents.get(i), new BasicDBObject("tf", mapMots.get(m).tf.get(i))
							.append("tfidf", mapMots.get(m).tfidf.get(i))
							.append("locationweight", mapMots.get(m).locationweight.get(i)));
					list.add(mapMots.get(m).documents.get(i));
					for (String documentKey : mapDocs.keySet()) {
						if (mapMots.get(m).documents.get(i).compareTo(documentKey) == 0) {
							mapDocs.get(documentKey).ajoutPourNormes(mapMots.get(m).tf.get(i),
									mapMots.get(m).tfidf.get(i),mapMots.get(m).locationweight.get(i));
						}
					}
				}
				doc.append("documents", list);
				table.insert(doc);
			}
			DBCollection docColl = db.getCollection("documents");
			for (String doc : mapDocs.keySet()) {
				mapDocs.get(doc).sqrtPourNormes();
				BasicDBObject newdoc = new BasicDBObject("document", doc);
				newdoc.append("nbMots", mapDocs.get(doc).getNbMots());
				newdoc.append("tfNorme", mapDocs.get(doc).getTFNorme());
				newdoc.append("tfidfNorme", mapDocs.get(doc).getTFIDFNorme());
				newdoc.append("locationweightNorme", mapDocs.get(doc).getLOCATIONWEIGHTNorme());
				docColl.insert(newdoc);
			}
			mongoClient.close();

		}catch (IOException e) {
			e.printStackTrace();
		}
	}


	String enleverAccents(String entry){
		String query = entry;
		query = query.replaceAll("é", "e");
		query = query.replaceAll("è", "e");
		query = query.replaceAll("ê", "e");
		query = query.replaceAll("ë", "e");
		query = query.replaceAll("ä", "a");
		query = query.replaceAll("â", "a");
		query = query.replaceAll("à", "a");
		query = query.replaceAll("î", "i");
		query = query.replaceAll("ï", "i");
		query = query.replaceAll("î", "i");
		query = query.replaceAll("ô", "o");
		query = query.replaceAll("ö", "o");

		return query;
	}
}
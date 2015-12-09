package main;

import com.mongodb.*;
import index.Indexer;
import index.Words;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import query.QueryProcessor;

public class Main {

	public static void main(String [] args){

        Indexer ind = new Indexer("./CORPUS/test");
		//ind.buildIndex();
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("inverseIndexDB");
        DBCollection table = db.getCollection("mot");
        Scanner sc = new Scanner(System.in);
        String entry = "";
        String modePoids="";
        while(!entry.contentEquals("X")){
            System.out.println("Choix de la méthode de pondération :");
            System.out.println("    Pour TFIDF : tapez '1'");
            System.out.println("    Pour TF : tapez '2'");
            modePoids=sc.nextLine();
            switch (modePoids){
                case "1":
                    System.out.println("C'est parti pour du TFIDF");
                    modePoids="tfidf";
                    break;
                case "2":
                    System.out.println("C'est parti pour du TF");
                    modePoids="tf";
                    break;
                default:
                    System.err.println("Apprends à taper. Mode par défaut activé (tfidf)");
                    modePoids="tfidf";
            }

            System.out.println("Choix de la méthode de tri de pertinence :");
            System.out.println("    Pour somme : tapez '1'");
            System.out.println("    Pour cosinus : tapez '2'");
            String modePerti=sc.nextLine();

            switch (modePerti){
                case "1":
                    System.out.println("C'est parti pour une SOMME");
                    break;
                case "2":
                    System.out.println("C'est parti pour un COSINUS");
                    break;
                default:
                    System.err.println("Apprends à taper. Mode par défaut activé (somme)");
                    modePoids="1";
            }
            System.out.println("Entrez votre requête : ");
            entry= sc.nextLine();
            QueryProcessor query = new QueryProcessor(entry);
            query.processQuery(table,modePoids,modePerti);

        }
        sc.close();

	}
}

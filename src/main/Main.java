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

        Indexer ind = new Indexer("./CORPUS");
		ind.buildIndex();
        /*MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("inverseIndexDB");
        DBCollection table = db.getCollection("mot");
        Scanner sc = new Scanner(System.in);
        String entry = "";
        String modePoids="";
        Boolean stop = false;
        while(!stop){
            System.out.println("Choix de la m�thode de pond�ration :");
            System.out.println("    Pour TFIDF : tapez '1'");
            System.out.println("    Pour TF : tapez '2'");
            System.out.println("    Pour quitter : tapez 'X'");
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
                case "X":
                    System.out.println("Fin de la recherche ! A bient�t ! ");
                    stop= true;
                    break;
                default:
                    System.err.println("Apprends � taper. Mode par d�faut activ� (tfidf)");
                    modePoids="tfidf";
            }

            System.out.println("Choix de la m�thode de tri de pertinence :");
            System.out.println("    Pour somme : tapez '1'");
            System.out.println("    Pour cosinus : tapez '2'");
            System.out.println("    Pour quitter : tapez 'X'");
            String modePerti=sc.nextLine();

            switch (modePerti){
                case "1":
                    System.out.println("C'est parti pour une SOMME");
                    break;
                case "2":
                    System.out.println("C'est parti pour un COSINUS");
                    break;
                case "X":
                    System.out.println("Fin de la recherche ! A bient�t ! ");
                    stop= true;
                    break;
                default:
                    System.err.println("Apprends � taper. Mode par d�faut activ� (somme)");
                    modePoids="1";
            }
            System.out.println("Entrez votre requ�te : ");
            entry= sc.nextLine();
            QueryProcessor query = new QueryProcessor(entry);
            query.processQuery(table,modePoids,modePerti);
            mongoClient.close();

        }
        sc.close();*/

	}
}

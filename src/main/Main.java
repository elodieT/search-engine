package main;

import com.mongodb.*;

import index.Indexer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import query.Evaluation;
import query.QueryProcessor;
import sparqlclient.Reformulator;
import sparqlclient.SparqlClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;


public class Main {
    public static void main(String[] args) throws IOException {
     /*   Indexer ind = new Indexer("./CORPUS");
        ind.buildIndex();*/
       // modeNormal();
	try {
			Evaluation eval = new Evaluation("./qrels","./resultsSparql.txt");
            eval.modeEval(true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /*SparqlClient sc = new SparqlClient("localhost:8080/space");
        Reformulator ref = new Reformulator(sc);
        ref.reformulate("Omar Sy, prix");*/

    }



    public static void modeNormal(Reformulator ref) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("inverseIndexDB");
        DBCollection table = db.getCollection("mot");
        Scanner sc = new Scanner(System.in);
        String entry = "";
        String modePoids = "";
        Boolean stop = false;
        while (!stop) {
            System.out.println("Choix de la méthode de pondération :");
            System.out.println(" Pour TFIDF : tapez '1'");
            System.out.println(" Pour TF : tapez '2'");
            System.out.println(" Pour LOCATIONWEIGHT : tapez '3'");
            System.out.println(" Pour quitter : tapez 'X'");
            modePoids = sc.nextLine();
            switch (modePoids) {
                case "1":
                    System.out.println("C'est parti pour du TFIDF");
                    modePoids = "tfidf";
                    break;
                case "2":
                    System.out.println("C'est parti pour du TF");
                    modePoids = "tf";
                    break;
                case"3":
                    System.out.println("C'est parti pour du LOCATIONWEIGHT");
                    modePoids = "locationweight";
                    break;
                case "X":
                    System.out.println("Fin de la recherche ! A bientôt ! ");
                    stop = true;
                    break;
                default:
                    System.err.println("Apprends à taper. Mode par défaut activé (tfidf)");
                    modePoids = "tfidf";
            }
            if (stop != true) {
                System.out.println("Choix de la méthode de tri de pertinence :");
                System.out.println(" Pour somme : tapez '1'");
                System.out.println(" Pour cosinus : tapez '2'");
                System.out.println(" Pour somme ponderee : tapez '3'");
                String modePerti = sc.nextLine();
                switch (modePerti) {
                    case "1":
                        System.out.println("C'est parti pour une SOMME");
                        break;
                    case "2":
                        System.out.println("C'est parti pour un COSINUS");
                        break;
                    case "3":
                        System.out.println("C'est parti pour une somme ponderee");
                        break;
                    default:
                        System.err.println("Apprends à taper. Mode par défaut activé (somme)");
                        modePoids = "1";
                }
                System.out.println("Entrez votre requête : ");
                entry = sc.nextLine();
                QueryProcessor query = new QueryProcessor(entry,true, ref);
                switch (modePerti) {
                    case "1":
                        query.processQuerySomme(table, modePoids);
                        break;
                    case "2":
                        query.processQueryCos(db, modePoids);
                        break;
                    case "3":
                        query.processQuerySommePonde(table, modePoids);
                        break;
                }
            }
        }
        mongoClient.close();
        sc.close();
    }
}
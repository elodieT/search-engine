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

        /*Indexer ind = new Indexer("./CORPUS");
		ind.buildIndex();
		*/
        //modeNormal();
        modeEval();

	}


    public static void modeEval(){
        HashMap<String,String> queries = new HashMap<>();
        queries.put("qrelQ1.txt","personnes, Intouchables");
        /*queries.put("qrelQ2.txt","lieu, naissance, Omar Sy");
        queries.put("qrelQ3.txt","personnes, récompensées, Intouchables");
        queries.put("qrelQ4.txt","palmarès, Globes de Cristal, 2012");
        queries.put("qrelQ5.txt","membre, jury, Globes de Cristal");
        queries.put("qrel6.txt","prix, Omar Sy, Globes de Cristal, 2012");
        queries.put("qrelQ7.txt","lieu, Globes Cristal, 2012");
        queries.put("qrelQ8.txt","prix, Omar Sy");
        queries.put("qrelQ9.txt","acteurs, joué avec, Omar Sy");*/

        File folder = new File("./qrels/test");
        File[] listOfFiles = folder.listFiles();
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("inverseIndexDB");
        DBCollection table = db.getCollection("mot");

        File fresults = new File("./results");
        try {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    QueryProcessor query = new QueryProcessor(queries.get(file.getName()));

                    //lecture fichier
                    HashMap<String, Integer> gold = new HashMap<>();
                    Scanner sc = new Scanner(file);
                    while(sc.hasNext()){
                        String docName = sc.next().replace(".html", "");
                        int docScore = (int)(10*Float.parseFloat(sc.next().replace(",",".")));
                        gold.put(docName,docScore);
                        System.out.println(docScore);
                    }
                    //tf Sum
                    HashMap<String, Integer> tfsum = query.processQuerySomme(table, "tf");

                    //tfidf Sum
                    HashMap<String, Integer> tfidfsum = query.processQuerySomme(table, "tfidf");

                    //tf cos
                    HashMap<String, Integer> tfcos = query.processQueryCos(db, "tf");

                    //tfidf cos
                    HashMap<String, Integer> tfidfcos = query.processQueryCos(db, "tfidf");

                    /*TODO :Calcul de Précision, Rappel, FMesure + les écrire dans le fichier résults
                        sous la forme suivante pour chaque qrel (Fmesure pas forcément obligatoire)
                        ------------------qrel1-------------
                        tf-sum P:0.89975 R:0.36807 F:0.7525
                        tfidf-sum P:0.89975 R:0.36807 F:0.7525
                        tf-cos P:0.89975 R:0.36807 F:0.7525
                        tfidf-cos P:0.89975 R:0.36807 F:0.7525
                      */

                }
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();

        }
        mongoClient.close();
    }

    public static void modeNormal(){
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("inverseIndexDB");
        DBCollection table = db.getCollection("mot");
        Scanner sc = new Scanner(System.in);
        String entry = "";
        String modePoids="";
        Boolean stop = false;
        while(!stop){
            System.out.println("Choix de la méthode de pondération :");
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
                    System.out.println("Fin de la recherche ! A bientôt ! ");
                    stop= true;
                    break;
                default:
                    System.err.println("Apprends à taper. Mode par défaut activé (tfidf)");
                    modePoids="tfidf";
            }

            if(stop!=true) {
                System.out.println("Choix de la méthode de tri de pertinence :");
                System.out.println("    Pour somme : tapez '1'");
                System.out.println("    Pour cosinus : tapez '2'");
                String modePerti = sc.nextLine();

                switch (modePerti) {
                    case "1":
                        System.out.println("C'est parti pour une SOMME");
                        break;
                    case "2":
                        System.out.println("C'est parti pour un COSINUS");
                        break;
                    default:
                        System.err.println("Apprends à taper. Mode par défaut activé (somme)");
                        modePoids = "1";
                }
                System.out.println("Entrez votre requête : ");
                entry = sc.nextLine();
                QueryProcessor query = new QueryProcessor(entry);
                switch (modePerti) {
                    case "1":
                        query.processQuerySomme(table, modePoids);
                        break;
                    case "2":
                        query.processQueryCos(db, modePoids);
                        break;
                }
            }

        }
        mongoClient.close();
        sc.close();

    }
}

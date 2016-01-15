package query;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.io.*;
import java.util.*;

/**
 * Created by sony on 02/01/2016.
 */
public class Evaluation {
    File queryFolder;
    File resultFile;
    public Evaluation (String queryFolder, String resultFile){
        this.queryFolder = new File(queryFolder);
        this.resultFile = new File(resultFile);
    }

    public void modeEval() throws IOException {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("qrelQ1.txt", "personnes, Intouchables");
        queries.put("qrelQ2.txt","lieu naissance, Omar Sy");
        queries.put("qrelQ3.txt","personnes, récompensées, Intouchables");
        queries.put("qrelQ4.txt","palmarès, Globes de Cristal 2012");
        queries.put("qrelQ5.txt","membre, jury, Globes de Cristal 2012");
        queries.put("qrelQ6.txt","prix, Omar Sy, Globes de Cristal 2012");
        queries.put("qrelQ7.txt","lieu, Globes de Cristal 2012");
        queries.put("qrelQ8.txt","prix, Omar Sy");
        queries.put("qrelQ9.txt","acteur, joué avec, Omar Sy");

        File[] listOfFiles = queryFolder.listFiles();

        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("inverseIndexDB");
        DBCollection table = db.getCollection("mot");
        ArrayList<String> docsPertForQrel = new ArrayList<String>();
        String docName = "";
        try {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                    docsPertForQrel.clear();
                    System.out.println(file.toString());
                    System.out.println(queries.get(file.getName()));
                    QueryProcessor query = new QueryProcessor(queries.get(file.getName()));
                    // lecture fichier
                    HashMap<String, Integer> gold = new HashMap<>();
                    Scanner sc = new Scanner(file);
                    while (sc.hasNext()) {
                        docName = sc.next().replace(".html", "");
                        int docScore = (int) (10 * Float.parseFloat(sc.next().replace(",", ".")));
                        gold.put(docName, docScore);
                    }
                    Object[] a = gold.entrySet().toArray();
                    // Constitution de la liste des documents pertinents pour la Qrel considérée
                    for (Object e : a) {
                        if (((Map.Entry<String, Integer>) e).getValue() > 0) {
                            docsPertForQrel.add(((Map.Entry<String, Integer>) e).getKey());
                        }
                    }

                    BufferedWriter fichier = new BufferedWriter(new FileWriter(resultFile,true));
                    fichier.write("--------" + file.getName() + "--------");
                    fichier.newLine();
                    int [] tabPrec = {5,10,25,50,100};
                    for (int i : tabPrec){
                        fichier.write("-------- Série sur les "+i+" premiers éléments --------");
                        fichier.newLine();
                        fichier.newLine();
                        // tf Sum
                        HashMap<String, Integer> tfsum = query.processQuerySomme(table, "tf");
                        ArrayList<Float> pAndR1 = calcPandR(i, docsPertForQrel, tfsum);
                        fichier.write("tf-sum : " + "P : " + pAndR1.get(0) + " R : " + pAndR1.get(1)+ " Fm : "+pAndR1.get(2));
                        fichier.newLine();
                        // tfidf Sum
                        HashMap<String, Integer> tfidfsum = query.processQuerySomme(table, "tfidf");
                        ArrayList<Float> pAndR2 = calcPandR(i, docsPertForQrel, tfidfsum);
                        fichier.write("tfidf-sum : " + "P :" + pAndR2.get(0) + " R : " + pAndR2.get(1)+ " Fm : "+pAndR2.get(2));
                        fichier.newLine();

                        // location Sum
                        HashMap<String, Integer> locationsum = query.processQuerySomme(table, "locationweight");
                        ArrayList<Float> pAndR22 = calcPandR(i, docsPertForQrel, locationsum);
                        fichier.write("locationweight sum : " + "P :" + pAndR22.get(0) + " R : " + pAndR22.get(1)+ " Fm : "+pAndR22.get(2));
                        fichier.newLine();
                        fichier.newLine();

                        //tf cos
                        HashMap<String, Integer> tfcos = query.processQueryCos(db, "tf");
                        ArrayList<Float> pAndR3 = calcPandR(i, docsPertForQrel, tfcos);
                        fichier.write("tf-cos " + "P : " + pAndR3.get(0) + " R : " + pAndR3.get(1)+ " Fm : "+pAndR3.get(2));
                        fichier.newLine();
                        // tfidf cos
                        HashMap<String, Integer> tfidfcos = query.processQueryCos(db, "tfidf");
                        ArrayList<Float> pAndR4 = calcPandR(i, docsPertForQrel, tfidfcos);
                        fichier.write("tfidf-cos" + "P : " + pAndR4.get(0) + " R : " + pAndR4.get(1)+" Fm : "+pAndR4.get(2));
                        fichier.newLine();
                        // locationweight cos
                        HashMap<String, Integer> locationcos = query.processQueryCos(db, "locationweight");
                        ArrayList<Float> pAndR44 = calcPandR(i, docsPertForQrel, locationcos);
                        fichier.write("locationweight-cos : " + "P : " + pAndR44.get(0) + " R : " + pAndR44.get(1)+" Fm : "+pAndR44.get(2));

                        fichier.newLine();
                        fichier.newLine();

                        //tf sum ponde
                        HashMap<String, Integer> tfsumponde = query.processQuerySommePonde(table, "tf");
                        ArrayList<Float> pAndR5 = calcPandR(i, docsPertForQrel, tfsumponde);
                        fichier.write("tf-sumPonde " + "P : " + pAndR5.get(0) + " R : " + pAndR5.get(1)+ " Fm : "+pAndR5.get(2));
                        fichier.newLine();
                        // tfidf cos
                        HashMap<String, Integer> tfidfsumponde = query.processQuerySommePonde(table, "tfidf");
                        ArrayList<Float> pAndR6 = calcPandR(i, docsPertForQrel, tfidfsumponde);
                        fichier.write("tfidf-sumPonde" + "P : " + pAndR6.get(0) + " R : " + pAndR6.get(1)+" Fm : "+pAndR6.get(2));
                        fichier.newLine();
                        // locationweight cos
                        HashMap<String, Integer> locationsumponde = query.processQuerySommePonde(table, "locationweight");
                        ArrayList<Float> pAndR66 = calcPandR(i, docsPertForQrel, locationsumponde);
                        fichier.write("locationweight-sumPonde : " + "P : " + pAndR66.get(0) + " R : " + pAndR66.get(1)+" Fm : "+pAndR66.get(2));
                        fichier.newLine();
                        fichier.newLine();
                        fichier.newLine();

                    }

                    fichier.close();

                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mongoClient.close();

    }

    public ArrayList<Float> calcPandR(int rang, ArrayList<String> docsPForQrel,
                                             HashMap<String, Integer> resultMeth) {
        ArrayList<Float> result = new ArrayList();
        ArrayList<String> resultListForMeth = new ArrayList();
        int nbDocPert = 0;
        float prec = 0;
        float rappel = 0;
        float fMesure = 0;
        int i = 0;
        int k = 0;
        // Ajout des entrées de la map à une liste
        final List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(resultMeth.entrySet());
        // Tri de la liste sur la valeur de l'entrée
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(final Map.Entry<String, Integer> e2, final Map.Entry<String, Integer> e1) {
                return e1.getValue().compareTo(e2.getValue());
            }
        });
        // Récupération des documents de pertinence non nulle pour la recherche
        for (final Map.Entry<String, Integer> entry : entries) {
            if (entry.getValue()>0) {
                resultListForMeth.add(entry.getKey());
                i++;
            }
        }
        //System.out.println("Nombre de documents sélectionnés : "+i);
        // Recherche de docs correspondants entre résultats de requete et liste
        // de docs pertinents
        while (k < rang && k < resultListForMeth.size()) {
            if (docsPForQrel.contains(resultListForMeth.get(k))) {
                // System.out.println("La liste des docs pour la qrel contient :
                // "+(resultListForMeth.get(k)));
                nbDocPert++;
            }
            k++;
        }
        rappel = (float)(nbDocPert)/(float) (docsPForQrel.size());
        prec = (float) (nbDocPert)/(float) (rang);
        fMesure = (2*(prec*rappel))/(prec+rappel);

        /*System.out.println("Pour les "+rang+" premiers éléments");
        System.out.println("P :" + prec+" "+" RDQrel:"+rappel+"+fmesure  :"+fMesure);*/
        result.add(prec);
        result.add(rappel);
        result.add(fMesure);

        return result;
    }
}

package sparqlclient;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by sony on 15/01/2016.
 */
public class Reformulator {
    SparqlClient sc;
    String prefix = "PREFIX : <http://ontologies.alwaysdata.net/space#>\n" +
            "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX film: <http://www.irit.fr/recherches/MELODI/ontologies/FilmographieV1.owl#>\n" ;
    public Reformulator(SparqlClient sc){
        this.sc=sc;
    }

    public String reformulate(String entry){
        String out =entry;
        Scanner scanner = new Scanner(entry);
        scanner.useDelimiter(", ");
        String query ;
        String lab;
        ArrayList<String> tabURIEntry = new ArrayList<>();
        while(scanner.hasNext()){
            lab = scanner.next();
           query =  "SELECT ?lab ?x WHERE{" +
                    "{?x rdfs:label \""+lab+"\"}" +
                   "UNION{?x rdfs:label \""+lab+"\"@fr}" +
                   "?x rdfs:label ?lab." +
                   "FILTER(?lab !=\""+lab+"\")" +
                   "FILTER(?lab !=\""+lab+"\"@fr)" +
                   "FILTER(langMatches(lang(?lab),\"fr\"))}";
            Iterable<Map<String, String>> results = sc.select(prefix +query);
            for (Map<String, String> result : results) {
                //System.out.println(result.get("lab"));
                out += ", "+result.get("lab");
                if (!tabURIEntry.contains(result.get("x"))){
                    tabURIEntry.add(result.get("x"));
                }
            }
        }
        if(tabURIEntry.size()==2){
            String A=tabURIEntry.get(0);
            String B = tabURIEntry.get(1);
            query = " SELECT DISTINCT ?lx WHERE{" +
                    "{<"+A+"> <"+ B+ "> ?x}" +
                    "UNION{<"+B+"> <"+ A+"> ?x}" +
                    "UNION{<"+A+"> ?x <"+ B+">}" +
                    "UNION{<"+B+"> ?x <"+ A+">}" +
                    "UNION{ ?x <"+A+"> <"+B+">}" +
                    "UNION{ ?x <"+B+"> <"+A+">}" +
                    "UNION{<"+A +"> ?p ?x." +
                    "?x a <"+B+">}" +
                    "UNION{<"+B+"> ?p ?x." +
                    "?x a <"+A+">}" +
                    "?x rdfs:label ?lx." +
                    "}";
            Iterable<Map<String, String>> results = sc.select(prefix +query);
            for (Map<String, String> result : results) {
                //System.out.println(result.get("lab"));
                out += ", "+result.get("lx");
            }
        }



       /* */
        System.out.println(out);
        return out;
    }

}

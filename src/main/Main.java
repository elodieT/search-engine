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

public class Main {

	public static void main(String [] args){

        Indexer ind = new Indexer("./CORPUS/test");
		//ind.buildIndex();
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("inverseIndexDB");
        DBCollection table = db.getCollection("mot");
        table.find(new DBObject("{'word':'chef'}"));
			





		
	}
}

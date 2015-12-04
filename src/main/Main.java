package main;

import index.Indexer;
import index.Words;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

	public static void main(String [] args){

			Indexer ind = new Indexer("/home/thieblin/Documents/RechercheInfo/CORPUS");
			ind.buildIndex();
			


		
	}
}

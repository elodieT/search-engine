package index;

import java.util.ArrayList;


public class Words {
	public ArrayList <String> documents;
	public ArrayList <Integer> tf; //termfrequencies
	public ArrayList <Integer> tfidf; //tfidf*100
	
	public Words(String doc){
		this.documents = new ArrayList <String> ();
		this.tf = new ArrayList <Integer> ();
		this.documents.add(doc);
		this.tf.add(1);
	}
	
	public String lastDoc(){
		return this.documents.get(this.documents.size()-1);
	}
	
	public void incrementTFforLastDoc(){
		this.tf.set(this.tf.size()-1, this.tf.get(this.tf.size()-1)+1); 
	}
	
	public void addDoc(String doc){
		this.documents.add(doc);
		this.tf.add(1);
	}
	
	public void calculateTFIDF(){
		this.tfidf= new ArrayList <Integer>();
		int len = this.tf.size();
		for(Integer tfi: this.tf)
		this.tfidf.add((int)(tfi*100/len));
	}
	
	public String print(){
		String result="Documents \n";
		for(String d : this.documents){
			result+=d+" ";
		}
		result+="\nTF\n";
		for(Integer i : this.tf){
			result+=i+" ";
		}
		result+="\nTFIDF\n";
		for(Integer i : this.tfidf){
			result+=i+" ";
		}
		
		return result;
	}
}

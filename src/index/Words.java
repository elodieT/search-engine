package index;

import java.util.ArrayList;

public class Words {
	public ArrayList<String> documents;
	public ArrayList<Integer> tf;
	public ArrayList<Integer> tfidf;
	public ArrayList<Integer> locationweight;

	public Words(String doc) {
		this.documents = new ArrayList<String>();
		this.tf = new ArrayList<Integer>();
		this.documents.add(doc);
		this.tf.add(1);
		this.locationweight = new ArrayList();
		this.locationweight.add(0);
	}

	public String lastDoc() {
		return this.documents.get(this.documents.size() - 1);
	}

	public void incrementTFforLastDoc() {
		this.tf.set(this.tf.size() - 1, this.tf.get(this.tf.size() - 1) + 1);
	}

	public void incrementLocationWeight(int poids){
		this.locationweight.set(this.locationweight.size() - 1, this.locationweight.get(this.locationweight.size() - 1) + poids);
	}

	public void addDoc(String doc) {
		this.documents.add(doc);
		this.tf.add(1);
		this.locationweight.add(0);}

	public void calculateTFIDF(int totalNumberOfDocs) {
		// TFIDF=1/(1+log(N/ni))
		this.tfidf = new ArrayList<Integer>();
		int len = this.tf.size();
		for (Integer tfi : this.tf) {
			this.tfidf.add((int) ((tfi) * (Math.log((double) (totalNumberOfDocs) / (double) (len)))));
		}
	}

	public void calculateLocWeightIDF(int totalNumberOfDocs) {
		// LocationWeight=1/(1+log(N/ni))
		int len = this.locationweight.size();
		for (int i=0;i<len;i++) {
			this.locationweight.set(i,((int) ((this.locationweight.get(i)) * (Math.log((double) (totalNumberOfDocs) / (double) (len))))));
		}
	}

	public String print() {
		String result = "Documents \n";
		for (String d : this.documents) {
			result += d + " ";
		}
		result += "\nTF\n";
		for (Integer i : this.tf) {
			result += i + " ";
		}
		result += "\nTFIDF\n";
		for (Integer i : this.tfidf) {
			result += i + " ";
		}
		return result;
	}

	public int numberOfDoc() {
		return this.documents.size();
	}
}

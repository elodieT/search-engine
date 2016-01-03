package index;

public class DocumentText {
    private int nbMots;
    private int sumTFCarre;
    private int sumTFIDFCarre;
    private int sumLOCATIONWEIGHTCarre;
    private int TFNorme;
    private int TFIDFNorme;
    private int LOCATIONWEIGHTNorme;

    /*public index.DocumentText(){
        nbMots=0;
        sumTFCarre=0;
        sumTFIDFCarre=0;
    }*/

    public DocumentText(int nbMots){
        this.setNbMots(nbMots);
        sumTFCarre=0;
        sumTFIDFCarre=0;
    }

    public void ajoutPourNormes(int tf, int tfidf, int locationweight){
        this.sumTFCarre+=tf*tf;
        this.sumTFIDFCarre+=tfidf*tfidf;
        this.sumLOCATIONWEIGHTCarre+=locationweight*locationweight;

    }

    public void sqrtPourNormes(){
        this.setTFNorme((int)(Math.sqrt((double)(sumTFCarre))));
        this.setTFIDFNorme((int)(Math.sqrt((double)(sumTFIDFCarre))));
        this.setLOCATIONWEIGHTNorme((int)(Math.sqrt((double)(sumLOCATIONWEIGHTCarre))));
    }

    public int getNbMots() {
        return nbMots;
    }

    public void setNbMots(int nbMots) {
        this.nbMots = nbMots;
    }

    public int getTFNorme() {
        return TFNorme;
    }

    public void setTFNorme(int TFNorme) {
        this.TFNorme = TFNorme;
    }

    public int getTFIDFNorme() {
        return TFIDFNorme;
    }

    public void setTFIDFNorme(int TFIDFNorme) {
        this.TFIDFNorme = TFIDFNorme;
    }
    public int getLOCATIONWEIGHTNorme() {
        return LOCATIONWEIGHTNorme;
    }

    public void setLOCATIONWEIGHTNorme(int LOCATIONWEIGHTNorme) {
        this.LOCATIONWEIGHTNorme = LOCATIONWEIGHTNorme;
    }
}

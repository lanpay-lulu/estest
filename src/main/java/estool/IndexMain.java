package estool;

/**
 * Created by lanpay on 2017/7/26.
 */
public class IndexMain {
    public static void main(String[] args) {
        String clusterName = "distribution_run";
        //String clusterName = "elasticsearch";
        String IP = "127.0.0.1";
        int PORT = 9300;
        EsWorker es = new EsWorker(clusterName, IP, PORT);
        String indexName = "ppc";
        String typeName = "ttc";
        long lastCnt = 1000*1000*300; // 3e
        int num = 1000*100*300; // 3e
        //MyGenerator.indexPerson(es);
        MyGenerator.indexPerson(es, indexName, typeName, lastCnt, num);
        //indexPersonSingleThread(es);
    }
}

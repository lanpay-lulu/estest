package estool;

import item.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanpay on 2017/7/20.
 */
public class MyGenerator {

    public static void main(String[] args) {
        String clusterName = "distribution_run";
        String IP = "127.0.0.1";
        int PORT = 9300;
        EsWorker es = new EsWorker(clusterName, IP, PORT);
        indexPerson(es);
    }

    public static void indexPerson(EsWorker es) {
        String indexName = "ppc";
        String typeName = "ttc";
        if(!es.indexExists(indexName)){
            System.out.println("[info] create index name="+indexName+", type="+typeName);
            es.createIndex(indexName, typeName, Person.getSetting(), Person.getMapping());
        }

        long lastId = 0;
        int num = 1000*1000*300; // 3e
        long cnt = 0;
        final int pack = 2000;
        System.out.println("begin generate. lastId = "+lastId);
        while(cnt < num){
            List<XCBuildable> docs = genPersons(lastId, pack);
            lastId += pack;
            cnt += pack;
            es.bulkPutData(indexName, typeName, docs);
            if(cnt % (pack*5) == 0) {
                System.out.println("emit cnt=" + cnt);
            }
        }
    }

    public static List<XCBuildable> genPersons(long lastId, long cnt) {
        List<XCBuildable> list = new ArrayList<XCBuildable>();
        for(long i=0; i<cnt; i++){
            Person p = new Person(i+lastId);
            list.add(p);
        }
        return list;
    }
}

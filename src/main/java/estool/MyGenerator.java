package estool;

import item.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by lanpay on 2017/7/20.
 */
public class MyGenerator implements Genable{

    public static final int ThreadNum = 4;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(ThreadNum);

    public static void main(String[] args) {
        //String clusterName = "distribution_run";
        String clusterName = "elasticsearch";
        String IP = "127.0.0.1";
        int PORT = 9300;
        EsWorker es = new EsWorker(clusterName, IP, PORT);
        //indexPerson(es);
        //indexPersonSingleThread(es);
    }

    public static void indexPerson(EsWorker es) {
        String indexName = "ppc";
        String typeName = "ttc";
        long lastCnt = 0;
        int num = 1000*100; // 3e
        indexPerson(es, indexName, typeName, lastCnt, num);
    }

    public static void indexPerson(EsWorker es, String indexName, String typeName, long lastCnt, int num) {
        if(!es.indexExists(indexName)){
            System.out.println("[info] create index name="+indexName+", type="+typeName);
            es.createIndex(indexName, typeName, Person.getSetting(), Person.getMapping());
        }

        final int packSize = 2000;
        int packNum = num / packSize;
        System.out.println("begin generate. lastCnt = "+lastCnt +", packSize="+packSize);
        List<Future> flist = new ArrayList<Future> (packNum);
        long t1 = System.currentTimeMillis();
        for(int i=0; i<packNum; i++){
            long firstId = i*packSize + lastCnt;
            IndexTask task = new IndexTask(es, indexName, typeName, new MyGenerator(), firstId, packSize);
            Future f = threadPool.submit(task);
            flist.add(f);
        }
        for(int i=0; i<flist.size(); i++){
            try{
                flist.get(i).get();
                if(i%10 == 0){
                    System.out.println(i+"/"+packNum+" finished!");
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            } catch(ExecutionException e) {
                e.printStackTrace();
            }
        }
        long t2 = System.currentTimeMillis() - t1;
        System.out.println("total time is "+t2);
    }

    public static void indexPersonSingleThread(EsWorker es) {
        String indexName = "ppc";
        String typeName = "ttc";
        if(!es.indexExists(indexName)){
            System.out.println("[info] create index name="+indexName+", type="+typeName);
            es.createIndex(indexName, typeName, Person.getSetting(), Person.getMapping());
        }

        long lastCnt = 0;
        int num = 1000*100; // 3e

        final int packSize = 2000;
        int packNum = num / packSize;
        System.out.println("begin generate. lastCnt = "+lastCnt +", packSize="+packSize);
        long t1 = System.currentTimeMillis();

        Genable gen = new MyGenerator();
        for(int i=0; i<packNum; i++) {
            long firstId = i * packSize + lastCnt;
            List<XCBuildable> docs = gen.generate(firstId, packSize);
            es.bulkPutData(indexName, typeName, docs);
            if(i%10 == 0){
                System.out.println(i+"/"+packNum+" finished!");
            }
        }
        long t2 = System.currentTimeMillis() - t1;
        System.out.println("total time is "+t2);
    }


    public List<XCBuildable> generate(long firstId, long cnt) {
        List<XCBuildable> list = new ArrayList<XCBuildable>();
        for(long i=0; i<cnt; i++){
            Person p = new Person(i+firstId);
            list.add(p);
        }
        return list;
    }
}

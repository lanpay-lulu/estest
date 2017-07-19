package es_test;

import es_test.model.Accident;
import es_test.model.Item;
import es_test.model.Person;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lanpay on 2017/6/24.
 */
public class TestMain {

    public static void main(String[] args) {
        //makeParentIndex();
        makeIndex();
        //makeQuery();
    }

    public static void makeQuery() {
        String indexName = "javatest";
        String documentType = "testtype";
        EsQuery esQuery = new EsQuery(indexName, documentType);
        esQuery.termQuery();
    }


    public static void makeParentIndex() {
        String indexName = "person";
        String documentType = "tt";
        EsIndex esIndex = new EsIndex(indexName, documentType);
        if(esIndex.exists()){
            System.out.println("index already exists! index name="+indexName);
            return;
        }
        SampleDataGenerator sdg = new SampleDataGenerator();
        List<Person> plist = sdg.generatePerson(100);
        System.out.println("begin making person index ...");
        esIndex.indexAllPerson(plist);

        indexName = "accident";
        esIndex.setIdx(indexName, documentType);
        if(esIndex.exists()){
            System.out.println("index already exists! index name="+indexName);
            return;
        }
        List<Accident> acclist = sdg.generateAccident(100, plist);

        System.out.println("begin making accident index ...");
        esIndex.indexAllAccident(acclist);

    }

    public static void makeIndex(){
        //EsClient es = new EsClient();

        //String indexName = "javatest";
        //String documentType = "testtype";

        String indexName = "ww";
        String documentType = "tww";

        EsIndex esIndex = new EsIndex(indexName, documentType);


        SampleDataGenerator sdg = new SampleDataGenerator();
        int num = (1<<20)*30; // 1kw
        int cnt = 0;
        int base = 52430000;
        int pack = 2000;
        System.out.println("begin generate. base = "+base

        );
        while(cnt < num){
            List<Item> docs = sdg.generateSampleData(base, pack);
            base += pack;
            cnt += pack;
            esIndex.indexAllProducts(docs);
            System.out.println("emit cnt="+cnt);
        }
        System.out.println("begin making index ...");

    }

}

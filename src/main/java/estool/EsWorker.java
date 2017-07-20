package estool;

import item.Person;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lanpay on 2017/7/19.
 */
public class EsWorker {

    private TransportClient client = null;
    private String clusterName = "elasticsearch";
    private String IP = "127.0.0.1";
    private int PORT = 9300;
    private static final Logger logger = Logger.getLogger(EsWorker.class.getName());


    public EsWorker() {
        init();
    }

    public EsWorker(String clusterName, String IP, int PORT) {
        this.clusterName = clusterName;
        this.IP = IP;
        this.PORT = PORT;
        init();
    }

    /**
     * @param name name of index
     * @param mappings json format string of mapping
     * */
    /*
        @param settings:
        {
            "number_of_shards" : 1,
            "number_of_replicas" : 1
        }
        @param mappings:
        {
            "properties" : {
                "name" : { "type" : "text" },
                "description" : { "type" : "text" },
                "age" : { "type" : "long" }
            }
        }
    */
    public boolean createIndex(String name, String type, String settings, String mappings) {
        if(indexExists(name)){
            System.out.println("[error] createIndex already exists! indexName="+name);
            return false;
        }
        return client.admin().indices().prepareCreate(name)
                .addMapping(type, mappings, XContentType.JSON)
                .setSettings(settings, XContentType.JSON)
                .get().isAcknowledged();
    }

    public boolean createIndex(String name, String type, String settings, XContentBuilder mappings) {
        if(indexExists(name)){
            System.out.println("[error] createIndex already exists! indexName="+name);
            return false;
        }
        return client.admin().indices().prepareCreate(name)
                .addMapping(type, mappings)
                .setSettings(settings, XContentType.JSON)
                .get().isAcknowledged();
    }

    /**
     * note: Do not exceed 5000 nor 50M at a time.
     * */
    public void bulkPutData(String indexName, String documentType, List<XCBuildable> list) {
        List<IndexRequestBuilder> requests = new ArrayList<IndexRequestBuilder>();
        int cnt = 0;
        final int Limit = 5000;
        for(XCBuildable xcb: list){
            XContentBuilder item = xcb.toXCB();
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, documentType, String.valueOf(xcb.getId()));
            indexRequestBuilder.setSource(item);
            if(xcb.getParent() != null){
                indexRequestBuilder.setParent(xcb.getParent());
            }
            requests.add(indexRequestBuilder);
            if(requests.size() == Limit){
                cnt += Limit;
                //System.out.println("emit cnt="+cnt);
                processBulkRequests(requests);
                requests.clear();
            }
        }
        processBulkRequests(requests);
    }

    public boolean indexExists(String indexName) {
        IndicesAdminClient adminClient = client.admin().indices();
        IndicesExistsRequest request = new IndicesExistsRequest(indexName);
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        return response.isExists();
    }

    private void init(){
        System.out.println("[info] es cluster name: "+clusterName);
        System.out.println("[info] es url: "+IP+":"+PORT);
        System.out.println("[info] EsWorker begin connecting ...");
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", clusterName).build();
            client = new PreBuiltTransportClient(settings);
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IP), PORT));
            System.out.println("[info] connected node size = " + client.connectedNodes().size());
            if(client.connectedNodes().size() == 0)
            {
                System.out.println("[info] There are no active nodes available for the transport, it will be automatically added once nodes are live!");
            }
        } catch (UnknownHostException e){
            e.printStackTrace();
            System.exit(-1); // todo: This is violate.
        }
    }

    protected BulkResponse processBulkRequests(List<IndexRequestBuilder> requests)
    {
        if (requests.size() > 0)
        {
            BulkRequestBuilder bulkRequest = client.prepareBulk();

            for (IndexRequestBuilder indexRequestBuilder : requests)
            {
                bulkRequest.add(indexRequestBuilder);
            }

            logger.info("Executing bulk index request for size:" + requests.size());
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();

            logger.info("Bulk operation data index response total items is:" + bulkResponse.getItems().length);
            if (bulkResponse.hasFailures())
            {
                // process failures by iterating through each bulk response item
                logger.info("bulk operation indexing has failures:" + bulkResponse.buildFailureMessage());
            }
            return bulkResponse;
        }
        else
        {
            logger.info("Executing bulk index request for size: 0");
            return null;
        }
    }


    public static void main(String[] args) {
        EsWorker es = new EsWorker();
        String mappings = "{\n" +
                "                \"properties\" : {\n" +
                "                    \"name\" : { \"type\" : \"text\" },\n" +
                "                    \"description\" : { \"type\" : \"text\" },\n" +
                "                    \"age\" : { \"type\" : \"long\" }\n" +
                "                }\n" +
                "            }";
        String settings = "{\n" +
                "            \"number_of_shards\" : 1,\n" +
                "            \"number_of_replicas\" : 1\n" +
                "        }";

        String indexName = "ppc";
        String typeName = "ttc";
        if(!es.indexExists(indexName)){
            System.out.println("[info] create index name="+indexName+", type="+typeName);
            es.createIndex(indexName, typeName, Person.getSetting(), Person.getMapping());
        }

        List<XCBuildable> list = new ArrayList<XCBuildable>();
        long lastId = 0;
        for(long i=0; i<1000; i++){
            Person p = new Person(i+lastId);
            list.add(p);
        }
        es.bulkPutData(indexName, typeName, list);
        //boolean ret = es.createIndex("sxsxsx", "mytype", settings, mappings);
        //System.out.println("ret="+ret);
    }
}

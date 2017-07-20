package es_test;

import es_test.model.Accident;
import es_test.model.Item;
import es_test.model.Person;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by lanpay on 2017/6/24.
 */
public class EsIndex {
    private String indexName;
    private String documentType;

    private EsClient esClient;
    private static final Logger logger = Logger.getLogger(EsIndex.class.getName());

    public EsIndex(String indexName, String documentType) {
        this.indexName = indexName;
        this.documentType = documentType;
        esClient = new EsClient();
        //esClient.getClient()
    }

    public void makeChildIndex(String name, String parentName) {
        IndicesAdminClient adminClient = esClient.getClient().admin().indices();

        //adminClient.prepareCreate();
    }

    public void setIdx(String name, String type) {
        this.indexName = name;
        this.documentType = type;
    }

    public boolean exists() {
        IndicesExistsRequest request = new IndicesExistsRequest(this.indexName);
        IndicesAdminClient adminClient = esClient.getClient().admin().indices();
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        if (response.isExists()) {
            return true;
        }
        return false;
    }

    void indexAllPerson(List<Person> list) {
        List<IndexRequestBuilder> requests = new ArrayList<IndexRequestBuilder>();
        try {
            for (Person p : list) {
                requests.add(getIndexRequestBuilderForAnPerson(p));
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        processBulkRequests(requests);
    }

    void indexAllAccident(List<Accident> list) {
        List<IndexRequestBuilder> requests = new ArrayList<IndexRequestBuilder>();
        try {
            for (Accident p : list) {
                requests.add(getIndexRequestBuilderForAnAccident(p));
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        processBulkRequests(requests);
    }

    void indexAllProducts(List<Item> list) {
        List<IndexRequestBuilder> requests = new ArrayList<IndexRequestBuilder>();
        try {
            int cnt = 0;
            for (Item item : list) {
                requests.add(getIndexRequestBuilderForAnItem(item));
                if(requests.size() == 10000){
                    cnt += 10000;
                    System.out.println("emit cnt="+cnt);
                    processBulkRequests(requests);
                    requests.clear();
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        processBulkRequests(requests);
    }

    // --- accident ---
    private IndexRequestBuilder getIndexRequestBuilderForAnAccident(Accident acc) throws IOException
    {
        XContentBuilder contentBuilder = null;
        try {
            contentBuilder = jsonBuilder();
            contentBuilder.startObject()
                    .field("name", acc.name)
                    .field("lat", acc.lat)
                    .field("lng", acc.lng)
                    .field("ts", acc.ts)
                    .endObject();
        } catch (IOException ex) {
            logger.info(ex.getMessage());
            throw new RuntimeException("Error occured while creating product gift json document!", ex);
        }
        IndexRequestBuilder indexRequestBuilder = esClient.getClient().prepareIndex(indexName, documentType, acc.ID);
        indexRequestBuilder.setSource(contentBuilder).setParent(acc.pID); // set everything you need.
        return indexRequestBuilder;
    }

    // --- person ---
    private IndexRequestBuilder getIndexRequestBuilderForAnPerson(Person person) throws IOException
    {
        XContentBuilder contentBuilder = null;
        try {
            contentBuilder = jsonBuilder();
            contentBuilder.startObject()
                    .field("name", person.name)
                    .field("age", person.age)
                    //.field("age", person.)
                    .endObject();
        } catch (IOException ex) {
            logger.info(ex.getMessage());
            throw new RuntimeException("Error occured while creating product gift json document!", ex);
        }
        IndexRequestBuilder indexRequestBuilder = esClient.getClient().prepareIndex(indexName, documentType, person.ID);
        indexRequestBuilder.setSource(contentBuilder); // set everything you need.
        return indexRequestBuilder;
    }



    private IndexRequestBuilder getIndexRequestBuilderForAnItem(Item item) throws IOException
    {
        XContentBuilder contentBuilder = getXContentBuilderForAnItem(item);

        IndexRequestBuilder indexRequestBuilder = esClient.getClient().prepareIndex(indexName, documentType, String.valueOf(item.getId()));

        indexRequestBuilder.setSource(contentBuilder); // set everything you need.

        return indexRequestBuilder;
    }

    private XContentBuilder getXContentBuilderForAnItem(Item item) throws IOException {
        XContentBuilder contentBuilder = null;
        try {
            contentBuilder = jsonBuilder();
            int cat = 0;
            if(item.getAge() % 3 == 1){
                cat = 1;
            }
            else if(item.getAge() % 3 == 2){
                cat = 2;
            }

            contentBuilder.startObject()
                    .field("name", item.getName())
                    //.field("postDate", new Date())
                    .field("description", item.getDescription())
                    .field("age", item.getAge())
                    .field("cat", cat)
                    .endObject();

        } catch (IOException ex)
        {
            logger.info(ex.getMessage());
            throw new RuntimeException("Error occured while creating product gift json document!", ex);
        }
        return contentBuilder;
    }

    protected BulkResponse processBulkRequests(List<IndexRequestBuilder> requests)
    {
        if (requests.size() > 0)
        {
            BulkRequestBuilder bulkRequest = esClient.getClient().prepareBulk();

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
}

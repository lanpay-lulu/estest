package es_test;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * Created by lanpay on 2017/6/26.
 */
public class EsQuery {
    private EsClient esClient;
    private String index;
    private String type;

    public EsQuery(String index, String type) {
        esClient = new EsClient();
        this.index = index;
        this.type = type;
    }

    public void termQuery() {
        QueryBuilder qb = QueryBuilders.termQuery("name", "item_1");
        SearchResponse resp = esClient.getClient().prepareSearch(index)
                .setTypes(type)
                .setQuery(qb)
                .get();
        for (SearchHit hit : resp.getHits().getHits()){
            System.out.println(hit.getSource());
        }
    }

    public void multiGet() {
        MultiGetResponse multiGetItemResponses = esClient.getClient().prepareMultiGet()
                .add(index, type, "1")
                .add(index, type, "2","3")
                .get();

        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                String json = response.getSourceAsString();
                System.out.println("multiGet result: "+json);
            }
        }

    }
}

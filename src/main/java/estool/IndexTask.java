package estool;

import java.util.List;

/**
 * Created by lanpay on 2017/7/24.
 */
public class IndexTask implements Runnable{
    private EsWorker esWorker;
    private Genable gen;
    long lastId;
    long cnt;
    String indexName;
    String typeName;

    public IndexTask(EsWorker esWorker, String indexName, String typeName,
                     Genable gen, long lastId, long cnt) {
        this.esWorker = esWorker;
        this.indexName = indexName;
        this.typeName = typeName;
        this.gen = gen;
        this.lastId = lastId;
        this.cnt = cnt;
    }

    public void run() {
        List<XCBuildable> docs = gen.generate(lastId, cnt);
        esWorker.bulkPutData(indexName, typeName, docs);
    }
}

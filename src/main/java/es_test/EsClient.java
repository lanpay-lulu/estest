package es_test;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.inject.Singleton;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;



/**
 * Created by lanpay on 2017/6/24.
 */

@Singleton
public class EsClient {

    private TransportClient client = null;
    private String clusterName = "elasticsearch";
    private final String IP = "127.0.0.1";
    private final int PORT = 9300;

    private static final Logger logger = Logger.getLogger(EsClient.class.getName());

    synchronized public TransportClient getClient(){
        if(client == null) {
            initClient();
        }
        return client;
    }

    private void initClient() {
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", clusterName).build();
            client = new PreBuiltTransportClient(settings);
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IP), PORT));
            logger.info("connected node size = " + client.connectedNodes().size());
            if(client.connectedNodes().size() == 0)
            {
                logger.info("There are no active nodes available for the transport, it will be automatically added once nodes are live!");
            }
        } catch (UnknownHostException e){

            e.printStackTrace();
        }
    }

    public void addNewNode(String name) throws UnknownHostException
    {
        TransportClient transportClient = client;
        transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(name), 9300));
    }

    public void removeNode(String name) throws UnknownHostException
    {
        TransportClient transportClient = client;
        transportClient.removeTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(name), 9300));
    }

    public static void main(String[] argv){
        EsClient es = new EsClient();
        TransportClient client = es.getClient();
        //System.out.println(client.);
    }
}

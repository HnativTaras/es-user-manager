package com.taras.hnativ.usermanager.utils;

import com.taras.hnativ.usermanager.model.User;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class TestElasticSearchContainer {

    private static final Logger logger = LoggerFactory.getLogger(TestElasticSearchContainer.class);

    private static final String DOCKER_IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:7.8.1";
    private static final String INDEX_NAME = "users";
    private static final String DATA = "test_data.json";


    private static ElasticsearchContainer elasticsearchContainer;
    private static RestHighLevelClient client;

    private static HttpHost host;


    static {
        logger.info("STARTING ELASTICSEARCH CONTAINER");

        elasticsearchContainer = new ElasticsearchContainer(DOCKER_IMAGE_NAME);
        elasticsearchContainer.start();
        host = HttpHost.create(elasticsearchContainer.getHttpHostAddress());

        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(host.toHostString())
                .build();

        client = RestClients.create(clientConfiguration).rest();

        try {
            MainResponse response = client.info(RequestOptions.DEFAULT);
            logger.info("Elasticsearch cluster started");
            logger.info("Cluster name: " + response.getClusterName());
            logger.info("Cluster UUID: " + response.getClusterUuid());
            logger.info("Cluster node name: " + response.getNodeName());
            logger.info("Elasticsearch version: " + response.getVersion().toString());

            createIndex(INDEX_NAME);
            insertTestDataToES(DATA, INDEX_NAME);
        } catch (IOException e) {
            logger.error("Error on elasticsearch initialization: ", e);
        }
        Runtime.getRuntime().addShutdownHook(
                new Thread(TestElasticSearchContainer::stopElasticSearchContainer));
    }


    private static void createIndex(String index) throws IOException {
        logger.info("Starting news index creation");

        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        logger.info("Index created: {}. Index name: {}",
                createIndexResponse.isAcknowledged(), createIndexResponse.index());
    }

    private static void insertTestDataToES(String dataFile, String index) throws IOException {
        logger.info("Inserting test data to Elasticsearch");
        Path newsFile = FileUtils.getResourceFile(dataFile).toPath();

        AtomicInteger totalDocuments = new AtomicInteger();
        AtomicInteger indexedDocuments = new AtomicInteger();
        Files.lines(newsFile).forEach(json -> {
            try {
                totalDocuments.incrementAndGet();
                User user = Utils.getJsonMapper().readValue(json, User.class);
                IndexRequest indexRequest = new IndexRequest(index)
                        .source(json, XContentType.JSON)
                        .id(user.getId());
                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                DocWriteResponse.Result result = indexResponse.getResult();
                if (result == DocWriteResponse.Result.CREATED) {
                    indexedDocuments.incrementAndGet();
                }
            } catch (IOException e) {
                logger.error("Error: ", e);
                throw new RuntimeException(e);
            }
        });
        logger.debug("Total documents from '{}': {}", dataFile, totalDocuments.get());
        logger.debug("Indexed documents: {}", indexedDocuments.get());
        logger.info("Test news indexing completed");
    }

    private static void stopElasticSearchContainer() {
        logger.info("Stopping elasticsearch container");
        try {
            client.close();
            elasticsearchContainer.stop();
            logger.info("Elasticsearch container stopped");
        } catch (IOException e) {
            logger.error("Cannot stop elasticsearch container properly: ", e);
        }
    }

    public static RestHighLevelClient getClient() {
        return client;
    }

    public static HttpHost getHost() {
        return host;
    }


    @Bean
    public RestHighLevelClient client() {
        return client;
    }

    public static void main(String[] args) {

    }

}
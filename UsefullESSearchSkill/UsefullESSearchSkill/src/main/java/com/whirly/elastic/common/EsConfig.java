package com.whirly.elastic.common;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.elasticsearch.common.transport.TransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @program: elastic
 * @description: 配置Elasticsearch客户端信息，获取客户端
 * @author: 赖键锋
 * @create: 2018-08-21 21:42
 **/
@Configuration
public class EsConfig {

    @Value("${elasticsearch.cluster-nodes}")
    private String clusterNodes;

    @Value("${elasticsearch.cluster-name}")
    private String clusterName;

    @Bean
    public Client client() {
        Settings settings = Settings.builder().put("cluster.name", clusterName)
                .put("client.transport.sniff", true).build();

        TransportClient client = new PreBuiltTransportClient(settings);
        try {
            if (clusterNodes != null && !"".equals(clusterNodes)) {
                for (String node : clusterNodes.split(",")) {
                    String[] nodeInfo = node.split(":");
                    client.addTransportAddress(new TransportAddress(InetAddress.getByName(nodeInfo[0]), Integer.parseInt(nodeInfo[1])));
                }
            }
        } catch (UnknownHostException e) {
        }

        return client;
    }
}

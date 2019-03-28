package com.roncoo.es.score.first;

import java.net.InetAddress;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * 员工增删改查的应用程序
 * @author Administrator
 *
 */
public class EmployeeCRUDApp {

	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws Exception {
		// 先构建client
		Settings settings = Settings.builder()
				.put("cluster.name", "elasticsearch")
				.build();
		
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
//		createEmployee(client); 
//		getEmployee(client);
//		updateEmployee(client); 
//		deleteEmployee(client); 
		
		client.close();
	}
	
	/**
	 * 创建员工信息（创建一个document）
	 * @param client
	 */
	private static void createEmployee(TransportClient client) throws Exception {
		IndexResponse response = client.prepareIndex("company", "employee", "1")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
							.field("name", "jack")
							.field("age", 27)
							.field("position", "technique")
							.field("country", "china")
							.field("join_date", "2017-01-01")
							.field("salary", 10000)
						.endObject())
				.get();
		System.out.println(response.getResult()); 
	}
	
	/**
	 * 获取员工信息
	 * @param client
	 * @throws Exception
	 */
	private static void getEmployee(TransportClient client) throws Exception {
		GetResponse response = client.prepareGet("company", "employee", "1").get();
		System.out.println(response.getSourceAsString()); 
	}
	
	/**
	 * 修改员工信息
	 * @param client
	 * @throws Exception
	 */
	private static void updateEmployee(TransportClient client) throws Exception {
		UpdateResponse response = client.prepareUpdate("company", "employee", "1") 
				.setDoc(XContentFactory.jsonBuilder()
							.startObject()
								.field("position", "technique manager")
							.endObject())
				.get();
		System.out.println(response.getResult());  
 	}
	
	/**
	 * 删除 员工信息
	 * @param client
	 * @throws Exception
	 */
	private static void deleteEmployee(TransportClient client) throws Exception {
		DeleteResponse response = client.prepareDelete("company", "employee", "1").get();
		System.out.println(response.getResult());  
	}
	
}

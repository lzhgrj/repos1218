package com.sibd.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibd.pojo.Article;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class Demo {

    private TransportClient client;

    @Before
    public void init() throws UnknownHostException {
        // 创建 Client 连接对象
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    }

    // 创建索引
    @Test
    public void test1(){
        // 创建名称为 blog 的索引
        client.admin().indices().prepareCreate("blog").get();
        // 释放资源
        client.close();
    }

    // 创建映射
    @Test
    public void test2() throws Exception {
        // 添加映射
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("article")
                .startObject("properties")
                .startObject("id")
                .field("type","string").field("store", "yes")
                .endObject()
                .startObject("content")
                .field("type","string").field("store", "yes").field("analyzer", "ik_max_word")
                .endObject()
                .startObject("title")
                .field("type","string").field("store", "yes").field("analyzer", "ik_max_word")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        // 创建映射
        PutMappingRequest mapping = Requests.putMappingRequest("blog").type("article").source(builder);
        client.admin().indices().putMapping(mapping).get();
        // 释放资源
        client.close();
    }

    // 创建文档（通过XContentBuilder）
    @Test
    public void test3() throws Exception {
        // 创建文档信息
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", "1")
                .field("title", "ElasticSearch是一个基于Lucene的搜索服务器")
                .field("content", "它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。")
                .endObject();
        // 建立文档对象
        client.prepareIndex("blog","article","1").setSource(builder).get();
        client.close();
    }

    // 创建文档（通过实体转json）
    @Test
    public void test4() throws Exception {
        Article article = new Article();
        article.setId(2);
        article.setTitle("ElasticSearch是一个基于Lucene的搜索服务器");
        article.setContent("它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(article);
        client.prepareIndex("blog","article","3").setSource(json).get();
        client.close();
    }

    // 关键词查询
    @Test
    public void test5() throws Exception {
        // 设置搜索条件
        SearchResponse searchResponse = client.prepareSearch("blog").setTypes("article")
                .setQuery(QueryBuilders.termQuery("content","搜索")).get();
        // 遍历搜索结果数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询结果有："+ hits.getTotalHits() + "条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            System.out.println("title:" + searchHit.getSource().get("title"));
        }
        client.close();
    }

    // 字符串查询
    @Test
    public void test6() throws Exception {
        // 设置搜索条件
        SearchResponse searchResponse = client.prepareSearch("blog").setTypes("article")
                .setQuery(QueryBuilders.queryStringQuery("搜索")).get();
        // 遍历搜索结果数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询结果有："+ hits.getTotalHits() + "条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            System.out.println("title:" + searchHit.getSource().get("title"));
        }
        client.close();
    }

    // 使用文档ID查询文档
    @Test
    public void test7() throws Exception {
        // 设置搜索条件
        SearchResponse searchResponse = client.prepareSearch("blog").setTypes("article")
                .setQuery(QueryBuilders.idsQuery().addIds("2")).get();
        // 遍历搜索结果数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询结果有："+ hits.getTotalHits() + "条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
        }
        client.close();
    }

    // 批量插入数据
    @Test
    public void test8() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < 50; i++) {
            Article article = new Article();
            article.setId(i);
            article.setTitle(i+"搜索工作其实很快乐");
            article.setContent(i+"我们希望我们的搜索解决方案要快，我们希望有一个零配置和一个完全免费的搜索模式，我们希望能够简单地使用JSON通过HTTP的索引数据，我们希望我们的搜索服务器始终可用，我们希望能够一台开始并扩展到数百，我们要实时搜索，我们要简单的多租户，我们希望建立一个云的解决方案。Elasticsearch旨在解决所有这些问题和更多的问题。");
            client.prepareIndex("blog","article",article.getId().toString()).setSource(mapper.writeValueAsString(article)).get();
        }
        client.close();
    }

    // 分页查询
    @Test
    public void test9() throws Exception{
        // 搜索数据
        SearchRequestBuilder requestBuilder = client.prepareSearch("blog").setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery()); // 默认每页10条记录
        // 分页（每页5条记录）
        requestBuilder.setFrom(0).setSize(5);
        // 遍历搜索结果数据
        SearchResponse searchResponse = requestBuilder.get();
        SearchHits hits = searchResponse.getHits(); // 获取命中次数，查询结果有多少对象
        System.out.println("查询结果有：" + hits.getTotalHits() + "条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next(); // 每个查询对象
            System.out.println(searchHit.getSourceAsString()); // 获取字符串格式打印
            System.out.println("id:" + searchHit.getSource().get("id"));
            System.out.println("title:" + searchHit.getSource().get("title"));
            System.out.println("content:" + searchHit.getSource().get("content"));
            System.out.println("‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐‐");
        }
        client.close();
    }

    // 高亮查询
    @Test
    public void test10() throws Exception {
        // 设置搜索条件
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("blog").setTypes("article")
                .setQuery(QueryBuilders.termQuery("title","工作"));
        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font style='color:red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("title");
        searchRequestBuilder.highlighter(highlightBuilder);
        // 遍历搜索结果数据
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询结果有："+ hits.getTotalHits() + "条");
        for(SearchHit hit:hits){
            System.out.println("String方式打印文档搜索内容:");
            System.out.println(hit.getSourceAsString());
            System.out.println("Map方式打印高亮内容");
            System.out.println(hit.getHighlightFields());
            System.out.println("遍历高亮集合，打印高亮片段:");
            Text[] texts = hit.getHighlightFields().get("title").getFragments();
            for (Text str : texts) {
                System.out.println(str);
            }
        }
        client.close();
    }
}

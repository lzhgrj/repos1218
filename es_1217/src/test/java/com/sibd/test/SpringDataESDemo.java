package com.sibd.test;

import com.sibd.pojo.Article;
import com.sibd.service.ArticleService;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:applicationContext.xml")
public class SpringDataESDemo {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private TransportClient client;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Test
    public void createIndex(){
        elasticsearchTemplate.createIndex(Article.class);
        elasticsearchTemplate.putMapping(Article.class);
    }

    @Test
    public void testSave(){
        Article article = new Article();
        article.setId(100);
        article.setTitle("测试SpringData ElasticSearch");
        article.setContent("Spring Data ElasticSearch 基于 spring data API 简化 elasticSearch操 作，将原始操作elasticSearch的客户端API 进行封装 Spring Data为Elasticsearch Elasticsearch项目提供集成搜索引擎");
        articleService.save(article);
    }

    @Test public void testDelete(){
        Article article = new Article();
        article.setId(100);
        articleService.delete(article);
    }

    @Test
    public void testSave100(){
        for(int i=1;i<=100;i++){
            Article article = new Article();
            article.setId(i);
            article.setTitle(i+"elasticSearch 3.0版本发布..，更新");
            article.setContent(i+"ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口");
            articleService.save(article);
        }
    }

    @Test
    public void testUpdate(){
        Article article = new Article();
        article.setId(40);
        article.setTitle("测试");
        article.setContent("你好");
        articleService.save(article);
    }

    @Test
    public void testFindAllPage(){
        Pageable pageable = PageRequest.of(1,5);
        Page<Article> page = articleService.findAll(pageable);
        for(Article article:page.getContent()){
            System.out.println(article);
        }
    }

    @Test
    public void testFindAll(){
        Iterable<Article> articles = articleService.findAll();
        for (Article article:articles
             ) {
            System.out.println(article);
        }
    }

    @Test
    public void testFindByTitle(){
        String condition = "测试";
        List<Article> articles = articleService.findByTitle(condition);
        for (Article article:articles
             ) {
            System.out.println(article);
        }
    }
}

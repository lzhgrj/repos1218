package com.sibd.dao;

import com.sibd.pojo.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleDao extends ElasticsearchRepository<Article,Integer> {
    List<Article> findByTitle(String condition);
}

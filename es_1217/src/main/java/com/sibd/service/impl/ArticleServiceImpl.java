package com.sibd.service.impl;

import com.sibd.dao.ArticleDao;
import com.sibd.pojo.Article;
import com.sibd.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleDao articleDao;

    public void save(Article article){
        articleDao.save(article);
    }

    @Override
    public void delete(Article article) {
        articleDao.delete(article);
    }

    @Override
    public Iterable<Article> findAll() {
        return articleDao.findAll();
    }

    @Override
    public Page<Article> findAll(Pageable pageable) {
        return articleDao.findAll(pageable);
    }

    @Override
    public List<Article> findByTitle(String condition) {
        return articleDao.findByTitle(condition);
    }
}

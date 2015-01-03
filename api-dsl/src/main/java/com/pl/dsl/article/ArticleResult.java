package com.pl.dsl.article;

import com.pl.dsl.Result;

import java.util.Objects;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class ArticleResult implements Result<Article> {
    private float score;
    private Article item;

    public ArticleResult(float score, Article item) {
        this.score = Objects.equals(score, Float.NaN) ? Float.MIN_VALUE : score;
        this.item = item;
    }

    @Override
    public Article getItem() {
        return item;
    }

    @Override
    public float getScore() {
        return score;
    }
}

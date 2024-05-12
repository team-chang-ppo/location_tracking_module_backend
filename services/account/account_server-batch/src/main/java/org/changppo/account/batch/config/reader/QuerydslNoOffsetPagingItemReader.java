package org.changppo.account.batch.config.reader;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetOptions;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import java.util.function.Function;

public class QuerydslNoOffsetPagingItemReader<T> extends QuerydslPagingItemReader<T> {
    private QuerydslNoOffsetOptions<T> options;

    private QuerydslNoOffsetPagingItemReader() {
        this.setName(ClassUtils.getShortName(org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader.class));
    }

    public QuerydslNoOffsetPagingItemReader(EntityManagerFactory entityManagerFactory, int pageSize, QuerydslNoOffsetOptions<T> options, Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        super(entityManagerFactory, pageSize, queryFunction);
        this.setName(ClassUtils.getShortName(org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader.class));
        this.options = options;
    }

    protected void doReadPage() {
        EntityTransaction tx = this.getTxOrNull();
        JPQLQuery<T> query = this.createQuery().limit(this.getPageSize());
        this.initResults();
        this.fetchQuery(query, tx);
        this.resetCurrentIdIfNotLastPage();
    }

    protected JPAQuery<T> createQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(this.entityManager);
        JPAQuery<T> query = this.queryFunction.apply(queryFactory);
        this.options.initKeys(query, this.getPage());
        return this.options.createQuery(query, this.getPage());
    }

    private void resetCurrentIdIfNotLastPage() {
        if (this.isNotEmptyResults()) {
            this.options.resetCurrentId(this.getLastItem());
        }

    }

    private boolean isNotEmptyResults() {
        return !CollectionUtils.isEmpty(this.results) && this.results.get(0) != null;
    }

    private T getLastItem() {
        return this.results.get(this.results.size() - 1);
    }
}


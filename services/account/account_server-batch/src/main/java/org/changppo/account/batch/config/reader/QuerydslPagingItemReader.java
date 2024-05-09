package org.changppo.account.batch.config.reader;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class QuerydslPagingItemReader<T> extends AbstractPagingItemReader<T> {
    protected final Map<String, Object> jpaPropertyMap;
    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    protected Function<JPAQueryFactory, JPAQuery<T>> queryFunction;
    protected boolean transacted;

    protected QuerydslPagingItemReader() {
        this.jpaPropertyMap = new HashMap();
        this.transacted = true;
        this.setName(ClassUtils.getShortName(org.springframework.batch.item.querydsl.reader.QuerydslPagingItemReader.class));
    }

    public QuerydslPagingItemReader(EntityManagerFactory entityManagerFactory, int pageSize, Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this(entityManagerFactory, pageSize, true, queryFunction);
    }

    public QuerydslPagingItemReader(EntityManagerFactory entityManagerFactory, int pageSize, boolean transacted, Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this();
        this.entityManagerFactory = entityManagerFactory;
        this.queryFunction = queryFunction;
        this.setPageSize(pageSize);
        this.setTransacted(transacted);
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    protected void doOpen() throws Exception {
        super.doOpen();
        this.entityManager = this.entityManagerFactory.createEntityManager(this.jpaPropertyMap);
        if (this.entityManager == null) {
            throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
        }
    }

    protected void doReadPage() {
        EntityTransaction tx = this.getTxOrNull();
        JPQLQuery query = (JPQLQuery)((JPAQuery)this.createQuery().offset((long) this.getPage() * this.getPageSize())).limit(this.getPageSize());
        this.initResults();
        this.fetchQuery(query, tx);
    }

    protected EntityTransaction getTxOrNull() {
        if (this.transacted) {
            EntityTransaction tx = this.entityManager.getTransaction();
            tx.begin();
            this.entityManager.flush();
            this.entityManager.clear();
            return tx;
        } else {
            return null;
        }
    }

    protected JPAQuery<T> createQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(this.entityManager);
        return this.queryFunction.apply(queryFactory);
    }

    protected void initResults() {
        if (CollectionUtils.isEmpty(this.results)) {
            this.results = new CopyOnWriteArrayList<>();
        } else {
            this.results.clear();
        }

    }

    protected void fetchQuery(JPQLQuery<T> query, EntityTransaction tx) {
        if (this.transacted) {
            this.results.addAll(query.fetch());
            if (tx != null) {
                tx.commit();
            }
        } else {
            List<T> queryResult = query.fetch();

            for (T entity : queryResult) {
                this.entityManager.detach(entity);
                this.results.add(entity);
            }
        }

    }

    protected void doJumpToPage(int itemIndex) {
    }

    protected void doClose() throws Exception {
        this.entityManager.close();
        super.doClose();
    }
}

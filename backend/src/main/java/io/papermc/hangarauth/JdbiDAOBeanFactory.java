package io.papermc.hangarauth;

import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class JdbiDAOBeanFactory implements FactoryBean<Object>, InitializingBean {

    private final Jdbi jdbi;
    private final Class<?> jdbiDaoClass;
    private volatile Object jdbiDaoBean;

    public JdbiDAOBeanFactory(final Jdbi jdbi, final Class<?> jdbiDaoClass) {
        this.jdbi = jdbi;
        this.jdbiDaoClass = jdbiDaoClass;
    }

    @Override
    public Object getObject() {
        return this.jdbiDaoBean;
    }

    @Override
    public Class<?> getObjectType() {
        return this.jdbiDaoClass;
    }

    @Override
    public void afterPropertiesSet() {
        this.jdbiDaoBean = this.jdbi.onDemand(this.jdbiDaoClass);
    }
}

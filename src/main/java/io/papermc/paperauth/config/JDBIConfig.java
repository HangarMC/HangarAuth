package io.papermc.paperauth.config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.RowMapperFactory;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.postgres.PostgresTypes;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import java.util.List;
import java.util.logging.Logger;
import javax.sql.DataSource;

import io.papermc.paperauth.db.HangarAuthDao;

@Configuration
public class JDBIConfig {

    @Bean
    public JdbiPlugin sqlObjectPlugin() {
        return new SqlObjectPlugin();
    }

    @Bean
    public JdbiPlugin postgresPlugin() {
        return new PostgresPlugin();
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers, List<RowMapperFactory> rowMapperFactories, List<ColumnMapper<?>> columnMappers) {
        SqlLogger myLogger = new SqlLogger() {
            @Override
            public void logAfterExecution(StatementContext context) {
                Logger.getLogger("sql").info("sql: " + context.getRenderedSql());
            }
        };
        TransactionAwareDataSourceProxy dataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        Jdbi jdbi = Jdbi.create(dataSourceProxy);
//        jdbi.setSqlLogger(myLogger); // for debugging sql statements
        PostgresTypes config = jdbi.getConfig(PostgresTypes.class);

        jdbiPlugins.forEach(jdbi::installPlugin);
        rowMappers.forEach(jdbi::registerRowMapper);
        rowMapperFactories.forEach(jdbi::registerRowMapper);
        columnMappers.forEach(jdbi::registerColumnMapper);

        return jdbi;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public <T> HangarAuthDao<T> hangarDao(Jdbi jdbi, InjectionPoint injectionPoint) {
        if (injectionPoint instanceof DependencyDescriptor) {
            DependencyDescriptor descriptor = (DependencyDescriptor) injectionPoint;
            //noinspection unchecked
            return new HangarAuthDao<>((T) jdbi.onDemand(descriptor.getResolvableType().getGeneric(0).getRawClass()));
        }
        return null;
    }
}

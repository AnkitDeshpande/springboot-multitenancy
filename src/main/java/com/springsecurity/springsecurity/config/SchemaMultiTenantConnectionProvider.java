package com.springsecurity.springsecurity.config;

import com.springsecurity.springsecurity.constants.Database;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


/**
 * The class Schema multi tenant connection provider implements
 * multi tenant connection provider, hibernate properties customizer.
 */
@Component
public class SchemaMultiTenantConnectionProvider
        implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer {

    /**
     * Datasource.
     */
    @Autowired
    private DataSource dataSource;

    /**
     * Gets the any connection.
     *
     * @return the any connection
     * @throws SQLException
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        final Connection connection = dataSource.getConnection();
        connection.createStatement()
                .execute(String.format("USE %s;", Database.DEFAULT_DATABASE.getDbName()));
        return connection;
    }

    /**
     * Release any connection.
     *
     * @param connection the connection.
     * @throws SQLException
     */
    @Override
    public void releaseAnyConnection(final Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Gets the connection.
     *
     * @param schema the schema.
     * @return the connection
     * @throws SQLException
     */
    @Override
    public Connection getConnection(final Object schema) throws SQLException {
        final Connection connection = dataSource.getConnection();
        connection.createStatement()
                .execute(String.format("USE %s;", schema));
        return connection;
    }

    /**
     * Release connection.
     *
     * @param s          the s.
     * @param connection the connection.
     * @throws SQLException
     */
    @Override
    public void releaseConnection(final Object schema, final Connection connection) throws SQLException {
        connection.createStatement()
                .execute(String.format("USE %s;", Database.DEFAULT_DATABASE.getDbName()));
        connection.close();
    }

    /**
     * Supports aggressive release.
     *
     * @return boolean
     */
    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    /**
     * Is unwrappable as.
     *
     * @param aClass the a class.
     * @return boolean
     */
    @Override
    public boolean isUnwrappableAs(final Class<?> aClass) {
        return false;
    }

    /**
     * Unwrap.
     *
     * @param aClass the a class.
     * @return T
     */
    @Override
    public <T> T unwrap(final Class<T> aClass) {
        throw new UnsupportedOperationException("Can't unwrap this.");
    }

    /**
     * Customize hibernate properties.
     *
     * @param hibernateProperties the hibernate properties.
     */
    @Override
    public void customize(final Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
        hibernateProperties.put("org.hibernate.envers.audit_table_suffix", "_AUDIT_LOG");
        hibernateProperties.put("org.hibernate.envers.revision_field_name", "REVISION_ID");
        hibernateProperties.put("org.hibernate.envers.revision_type_field_name", "REVISION_TYPE");
    }

}
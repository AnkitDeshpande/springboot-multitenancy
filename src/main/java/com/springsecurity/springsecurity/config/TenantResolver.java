package com.springsecurity.springsecurity.config;

import com.springsecurity.springsecurity.constants.Database;
import com.springsecurity.springsecurity.models.TenantContext;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TenantResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

    /**
     * Resolve current tenant identifier.
     *
     * @return String
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getCurrentTenant() != null
                ? TenantContext.getCurrentTenant()
                : Database.DEFAULT_DATABASE.getDbName();
    }

    /**
     * Validate existing current sessions.
     *
     * @return boolean
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    /**
     * Customize.
     *
     * @param hibernateProperties the hibernate properties.
     */
    @Override
    public void customize(final Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

}

package com.crm.security;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class TenantStatementInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return sql;
        }

        String upperSql = sql.toUpperCase().trim();
        if (!upperSql.startsWith("SELECT") && !upperSql.startsWith("UPDATE") && !upperSql.startsWith("DELETE")) {
            return sql;
        }

        if (upperSql.contains("TENANT_ID")) {
            return sql;
        }

        if (upperSql.contains(" WHERE ")) {
            return sql + " AND tenant_id = " + tenantId;
        } else {
            return sql + " WHERE tenant_id = " + tenantId;
        }
    }
}

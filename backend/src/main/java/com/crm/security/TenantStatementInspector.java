package com.crm.security;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TenantStatementInspector implements StatementInspector {

    private static final Set<String> EXCLUDED_TABLES = Set.of(
        "tenants", "plans", "subscriptions", "billing_invoices",
        "payments", "payment_methods", "audit_logs", "usuarios"
    );

    private static final Pattern TABLE_PATTERN = Pattern.compile(
        "\\b(?:from|join|update|into|delete\\s+from)\\s+(?:`?\\w+`?\\.)?`?(\\w+)`?",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public String inspect(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }

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

        if (upperSql.contains("TENANTS ") || upperSql.contains("TENANTS.") || upperSql.contains(" PLANS ") || upperSql.contains(" PLANS.")) {
            return sql;
        }

        if (upperSql.startsWith("SELECT")) {
            return injectTenantFilterSelect(sql, tenantId);
        } else if (upperSql.startsWith("UPDATE")) {
            return injectTenantFilterUpdate(sql, tenantId);
        } else if (upperSql.startsWith("DELETE")) {
            return injectTenantFilterDelete(sql, tenantId);
        }

        return sql;
    }

    private String injectTenantFilterSelect(String sql, Long tenantId) {
        String upperSql = sql.toUpperCase();
        int whereIdx = findWhereClause(upperSql);
        if (whereIdx >= 0) {
            return sql + " AND tenant_id = " + tenantId;
        } else {
            int groupByIdx = upperSql.indexOf(" GROUP BY ");
            int orderByIdx = upperSql.indexOf(" ORDER BY ");
            int limitIdx = upperSql.indexOf(" LIMIT ");
            int insertIdx = findEarliest(groupByIdx, orderByIdx, limitIdx);
            if (insertIdx >= 0) {
                return sql.substring(0, insertIdx) + " WHERE tenant_id = " + tenantId + " " + sql.substring(insertIdx);
            }
            return sql + " WHERE tenant_id = " + tenantId;
        }
    }

    private String injectTenantFilterUpdate(String sql, Long tenantId) {
        String upperSql = sql.toUpperCase();
        int whereIdx = findWhereClause(upperSql);
        if (whereIdx >= 0) {
            return sql + " AND tenant_id = " + tenantId;
        }
        return sql + " WHERE tenant_id = " + tenantId;
    }

    private String injectTenantFilterDelete(String sql, Long tenantId) {
        String upperSql = sql.toUpperCase();
        int whereIdx = findWhereClause(upperSql);
        if (whereIdx >= 0) {
            return sql + " AND tenant_id = " + tenantId;
        }
        return sql + " WHERE tenant_id = " + tenantId;
    }

    private int findWhereClause(String upperSql) {
        int depth = 0;
        for (int i = 0; i < upperSql.length() - 6; i++) {
            char c = upperSql.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && upperSql.startsWith(" WHERE ", i)) {
                return i;
            }
        }
        return -1;
    }

    private int findEarliest(int... indices) {
        int earliest = Integer.MAX_VALUE;
        for (int idx : indices) {
            if (idx > 0 && idx < earliest) {
                earliest = idx;
            }
        }
        return earliest == Integer.MAX_VALUE ? -1 : earliest;
    }
}

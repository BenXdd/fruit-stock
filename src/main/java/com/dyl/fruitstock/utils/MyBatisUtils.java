package com.dyl.fruitstock.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Mybatis工具类
 */
public class MyBatisUtils {
    private static final SQLUtils.FormatOption       printFormat            = new SQLUtils.FormatOption(true, false);
    private static final Cache<String, SQLStatement> sqlExprCache           = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(60)).maximumSize(1000).build();
    private static final Cache<String, Boolean>      tenantFilterCheckCache = Caffeine.newBuilder().expireAfterAccess(Duration.ofSeconds(60)).maximumSize(1000).build();

    private MyBatisUtils() {
    }

    /**
     * 添加租户过滤条件
     *
     * @param sql   原sql
     * @param table 需要过滤的表名
     * @return
     */
    public static SQLStatement addTenantFilter(String sql, String table) {
        SQLStatement stmt = sqlExprCache.getIfPresent(sql);
        if (stmt != null) {
            return stmt;
        }
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "mysql");
        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }
        stmt = stmtList.get(0);
        if (stmt instanceof SQLUpdateStatement) {
            MySqlUpdateStatement update = ((MySqlUpdateStatement) stmt);
            if (addTenantFilter(update::addWhere, update.getTableSource(), table)) {
                sqlExprCache.put(sql, stmt);
                return stmt;
            }
        }

        if (stmt instanceof SQLDeleteStatement) {
            MySqlDeleteStatement delete = ((MySqlDeleteStatement) stmt);
            if (addTenantFilter(delete::addWhere, delete.getTableSource(), table)) {
                sqlExprCache.put(sql, stmt);
                return stmt;
            }
        }

        if (stmt instanceof SQLSelectStatement) {
            SQLSelectStatement select = ((SQLSelectStatement) stmt);
            if (addTenantFilter(select::addWhere, ((MySqlSelectQueryBlock) select.getSelect().getQuery()).getFrom(), table)) {
                sqlExprCache.put(sql, stmt);
                return stmt;
            }
        }

        return null;
    }

    public static boolean checkTenantFilter(String sql, String table) {
        Boolean checked = tenantFilterCheckCache.getIfPresent(sql);
        if (checked != null) {
            return checked;
        }
        SQLStatement stmt = sqlExprCache.get(sql, s -> {
            List<SQLStatement> l = SQLUtils.parseStatements(s, "mysql");
            if (l.size() != 1) {
                throw new IllegalArgumentException("sql not support count : " + sql);
            }
            return l.get(0);
        });

        if (stmt instanceof SQLUpdateStatement) {
            MySqlUpdateStatement update = ((MySqlUpdateStatement) stmt);
            if (checkTenantFilter(update.getWhere(), update.getTableSource(), table)) {
                sqlExprCache.put(sql, stmt);
                return true;
            }
        }

        if (stmt instanceof SQLDeleteStatement) {
            MySqlDeleteStatement delete = ((MySqlDeleteStatement) stmt);
            if (checkTenantFilter(delete.getWhere(), delete.getTableSource(), table)) {
                sqlExprCache.put(sql, stmt);
                return true;
            }
        }

        if (stmt instanceof SQLSelectStatement) {
            MySqlSelectQueryBlock select = ((MySqlSelectQueryBlock) ((SQLSelectStatement) stmt).getSelect().getQuery());
            if (checkTenantFilter(select.getWhere(), select.getFrom(), table)) {
                sqlExprCache.put(sql, stmt);
                return true;
            }
        }

        return false;
    }

    public static SQLStatement parseStatement(String sql) {
        SQLStatement stmt = sqlExprCache.getIfPresent(sql);
        if (stmt != null) {
            return stmt;
        }
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "mysql");
        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }
        stmt = stmtList.get(0);
        sqlExprCache.put(sql, stmt);
        return stmt;
    }

    public static String toSqlString(SQLStatement stmt) {
        return SQLUtils.toMySqlString(stmt, printFormat);
    }

    /**
     * 添加租户过滤条件
     *
     * @param rootUpdate 需要添加条件的where子句
     * @param from       表
     * @param table      需要过滤的表名
     * @return
     */
    private static boolean addTenantFilter(Function<SQLExpr, Boolean> rootUpdate, SQLTableSource from, String table) {
        SQLTableSource t = from.findTableSource(table);
        if (t != null) {
            addWhereCondition(rootUpdate, t);
            return true;
        }
        if (from instanceof SQLJoinTableSource) {
            if (addTenantFilter(rootUpdate, ((SQLJoinTableSource) from).getLeft(), table)) {
                return true;
            }
            return addTenantFilter(rootUpdate, ((SQLJoinTableSource) from).getRight(), table);
        }
        if (from instanceof SQLSubqueryTableSource) {
            MySqlSelectQueryBlock select = (MySqlSelectQueryBlock) ((SQLSubqueryTableSource) from).getSelect().getQuery();
            return addTenantFilter(sqlExpr -> {
                select.addWhere(sqlExpr);
                return true;
            }, select.getFrom(), table);
        }
        return false;
    }

    private static boolean checkTenantFilter(SQLExpr whereCondition, SQLTableSource from, String table) {
        SQLTableSource t = from.findTableSource(table);
        if (t != null) {
            if (whereCondition == null) {
                return false;
            }
            return checkWhereCondition(whereCondition, t);
        }
        if (from instanceof SQLJoinTableSource) {
            if (checkTenantFilter(whereCondition, ((SQLJoinTableSource) from).getLeft(), table)) {
                return true;
            }
            return checkTenantFilter(whereCondition, ((SQLJoinTableSource) from).getRight(), table);
        }
        if (from instanceof SQLSubqueryTableSource) {
            MySqlSelectQueryBlock select = (MySqlSelectQueryBlock) ((SQLSubqueryTableSource) from).getSelect().getQuery();
            return checkTenantFilter(select.getWhere(), select.getFrom(), table);
        }
        return false;
    }

    private static boolean checkWhereCondition(SQLExpr whereCondition, SQLTableSource table) {
        if (whereCondition instanceof SQLBinaryOpExpr) {
            boolean left = checkWhereCondition(((SQLBinaryOpExpr) whereCondition).getLeft(), table);
            boolean right = checkWhereCondition(((SQLBinaryOpExpr) whereCondition).getRight(), table);
            return left || right;
        }
        if (whereCondition instanceof SQLIdentifierExpr) {
            return "tenant_id".equals(((SQLIdentifierExpr) whereCondition).getName().toLowerCase());
        }
        if (whereCondition instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) whereCondition).getOwnernName().equals(table.computeAlias()) && ((SQLPropertyExpr) whereCondition).getName().equals("tenant_id");
        }
        return false;
    }

    /**
     * 添加查询条件
     *
     * @param addWhere
     * @param t
     */
    private static void addWhereCondition(Function<SQLExpr, Boolean> addWhere, SQLTableSource t) {
        addWhere.apply(new SQLBinaryOpExpr(new SQLIdentifierExpr((t.getAlias() == null ? t.toString() : t.getAlias()) + ".tenant_id"),
                SQLBinaryOperator.Equality, new SQLIdentifierExpr("?")));
    }

    /**
     * 查找Mapper参数
     *
     * @param p
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Pair<String, T> findParameter(Object p, Class<T> clazz) {
        if (p == null) {
            return null;
        }
        if (p instanceof Map) {
            Map<String, Object> pp = ((Map<String, Object>) p);
            for (Map.Entry<String, Object> e : pp.entrySet()) {
                if (e.getValue() != null && clazz.isAssignableFrom(e.getValue().getClass())) {
                    return new ImmutablePair<>(e.getKey(), (T) e.getValue());
                }
            }
        } else if (clazz.isAssignableFrom(p.getClass())) {
            return new ImmutablePair<>("", (T) p);
        }
        return null;
    }

    /**
     * 添加租户参数
     *
     * @param param
     * @param ms
     * @param parameterMappings
     */
    public static ParameterMapping addTenantIdParameter(String param, MappedStatement ms, List<ParameterMapping> parameterMappings) {
        ParameterMapping tenantIdParameterMapping;
        if ("".equals(param)) {
            tenantIdParameterMapping = new ParameterMapping.Builder(ms.getConfiguration(), "tenantId", Integer.class).build();
        } else {
            tenantIdParameterMapping = new ParameterMapping.Builder(ms.getConfiguration(), param + ".tenantId", Integer.class).build();
        }
        parameterMappings.add(tenantIdParameterMapping);
        return tenantIdParameterMapping;
    }

    /**
     * 替换原始查询
     *
     * @param invocation
     * @param oldBoundSql
     * @param newSql
     * @return
     */
    public static BoundSql replaceStatement(Invocation invocation, BoundSql oldBoundSql, List<ParameterMapping> parameterMappings, SQLStatement newSql) {
        Object[]        args       = invocation.getArgs();
        MappedStatement ms         = (MappedStatement) args[0];
        String          sql        = toSqlString(newSql);
        BoundSql        rwBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, oldBoundSql.getParameterObject());
        for (ParameterMapping parameterMapping : oldBoundSql.getParameterMappings()) {
            if (oldBoundSql.hasAdditionalParameter(parameterMapping.getProperty())) {
                rwBoundSql.setAdditionalParameter(parameterMapping.getProperty(), oldBoundSql.getAdditionalParameter(parameterMapping.getProperty()));
            }
        }
        // 覆盖重写的sql
        SqlSource       sqlSource = new BoundSqlSqlSource(rwBoundSql);
        MappedStatement rewriteMs = copyFromMappedStatement(ms, sqlSource);
        args[0] = rewriteMs;

        return rwBoundSql;
    }

    private static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(String.join(",", ms.getKeyProperties()));
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}

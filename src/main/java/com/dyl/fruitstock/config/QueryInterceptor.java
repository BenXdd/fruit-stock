package com.dyl.fruitstock.config;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;

import com.dyl.fruitstock.service.IPage;
import com.dyl.fruitstock.utils.MyBatisUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * 自动分页插件
 *
 * @author nisen
 */
@Intercepts({@Signature(
        method = "query",
        type = Executor.class,
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
)})
@SuppressWarnings({"unchecked", "rawtypes"})
public class QueryInterceptor implements Interceptor {

    /**
     * 每页最大返回记录数
     */
    private int maxSize = 500;
    /**
     * 第页最小记录数, 当传入参数小于等于0时, 重置
     */
    private int minSize = 20;

    /**
     * 重写查询sql, 过滤租户条件
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor        executor  = (Executor) invocation.getTarget();
        Object[]        args      = invocation.getArgs();
        MappedStatement ms        = (MappedStatement) args[0];
        Object          parameter = args[1];

        if (SqlCommandType.SELECT != ms.getSqlCommandType()
                || StatementType.CALLABLE == ms.getStatementType()) {
            return invocation.proceed();
        }
        BoundSql               boundSql          = ms.getBoundSql(parameter);
        SQLStatement           stmt              = null;
        List<ParameterMapping> parameterMappings = new ArrayList<>(boundSql.getParameterMappings());

        // 检查是否有分页对象, 如果没有则不进行分页查询
        Pair<String, IPage> p2 = MyBatisUtils.findParameter(parameter, IPage.class);
        if (p2 == null || !p2.getRight().count()) {
            return invocation.proceed();
        }
        IPage<?> page = p2.getRight();
        // 检查最大最小记录数
        if (page.getSize() <= 0) {
            page.setSize(minSize);
        }
        if (maxSize > 0 && page.getSize() > maxSize) {
            page.setSize(maxSize);
        }
        // count总数
        count(executor, ms, parameter, page, boundSql);
        // 为0时, 直接返回空集
        if (page.getTotal() == 0) {
            page.setList(new ArrayList<>(0));
            return page.getList();
        }
        stmt = MyBatisUtils.parseStatement(boundSql.getSql());
        MySqlSelectQueryBlock select = ((MySqlSelectQueryBlock) ((SQLSelectStatement) stmt).getSelect().getQuery());
        // 添加排序条件
        addOrderBy(page, select);
        select.setLimit(new SQLLimit(new SQLNumberExpr(page.offset()), new SQLNumberExpr(page.getSize())));
        MyBatisUtils.replaceStatement(invocation, boundSql, parameterMappings, stmt);
        // 查询结果
        List list = (List) invocation.proceed();
        // 设置返回结果
        page.setList(list);
        return list;
    }

    private void count(Executor executor, MappedStatement ms, Object parameter, IPage<?> page, BoundSql boundSql) throws SQLException {
        String countSql = null;
        try {
            countSql = PagerUtils.count(boundSql.getSql(), DbType.mysql);
        } catch (Exception e) {
            ms.getStatementLog().error(boundSql.getSql(), e);
            throw e;
        }
        BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            if (boundSql.hasAdditionalParameter(parameterMapping.getProperty())) {
                countBoundSql.setAdditionalParameter(parameterMapping.getProperty(), boundSql.getAdditionalParameter(parameterMapping.getProperty()));
            }
        }
        Cache cache = ms.getCache();
        if (cache != null && ms.isUseCache() && ms.getConfiguration().isCacheEnabled()) {
            CacheKey cacheKey = executor.createCacheKey(ms, parameter, RowBounds.DEFAULT, countBoundSql);
            Long     count    = (Long) cache.getObject(cacheKey);
            if (count == null) {
                count = this.getTotalCount(executor, ms, countBoundSql);
                cache.putObject(cacheKey, count);
            }
            page.setTotal(count);
        } else {
            page.setTotal(this.getTotalCount(executor, ms, countBoundSql));
        }
    }

    private IPage<?> getPage(Object parameter) {
        if (parameter instanceof Map) {
            for (Object p : ((Map) parameter).values()) {
                if (p instanceof IPage) {
                    return ((IPage) p);
                }
            }
        }
        return null;
    }

    private void addOrderBy(IPage page, MySqlSelectQueryBlock select) {
        if (page.orderBy() == null) {
            return;
        }
        Stream.of(page.orderBy()).filter(a -> !"".equals(a))
                .map(o -> {
                    String[] it = o.split("\\s+");
                    if (it.length > 1) {
                        return new SQLSelectOrderByItem(new SQLIdentifierExpr(it[0]), "DESC".equals(it[1].toUpperCase()) ?
                                SQLOrderingSpecification.DESC : SQLOrderingSpecification.ASC);
                    }
                    return new SQLSelectOrderByItem(new SQLIdentifierExpr(it[0]), SQLOrderingSpecification.ASC);
                })
                .forEach(select::addOrderBy);
    }

    /**
     * 查询总记录数
     *
     * @param executor
     * @param ms
     * @param countSql
     * @return
     * @throws SQLException
     */
    private Long getTotalCount(Executor executor, MappedStatement ms, BoundSql countSql) throws SQLException {
        Log        log  = ms.getStatementLog();
        Connection conn = executor.getTransaction().getConnection();
        if (log.isDebugEnabled()) {
            conn = ConnectionLogger.newInstance(conn, log, 1);
        }

        PreparedStatement       countStmt = conn.prepareStatement(countSql.getSql());
        DefaultParameterHandler handler   = new DefaultParameterHandler(ms, countSql.getParameterObject(), countSql);
        handler.setParameters(countStmt);
        ResultSet rs = null;

        try {
            rs = countStmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                }
            }
            if (countStmt != null) {
                try {
                    countStmt.close();
                } catch (SQLException ignore) {
                }
            }

        }

        return 0L;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

}

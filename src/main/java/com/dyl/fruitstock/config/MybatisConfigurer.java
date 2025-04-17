package com.dyl.fruitstock.config;



import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 配置Mybatis
 */
@Configuration
@MapperScan(basePackages = {"com.dyl.*.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")

public class MybatisConfigurer {

    @Bean
    public QueryInterceptor queryInterceptor() {
        return new QueryInterceptor();
    }


    /**
     * 配置
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        //添加自动分页拦截器
        factory.setPlugins(queryInterceptor());
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resourceList = new ArrayList<>();
        resourceList.addAll(Arrays.asList(resolver.getResources("classpath*:com/dyl/fruitstock/mapper/*.xml")));
        resourceList.addAll(Arrays.asList(resolver.getResources("classpath*:mapper/**/*.xml")));
        factory.setMapperLocations(resourceList.toArray(new Resource[0]));
        ///factory.setConfigLocation(resolver.getResource("classpath:mybatis/mybatis-config.xml"));
        SqlSessionFactory sqlSessionFactory = factory.getObject();
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);// 开启驼峰映射
        return factory.getObject();
    }

}


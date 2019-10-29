package me.zhengjie.modules.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author ：cesar.X.
 * @date ：Created in 9:26 AM 2019/2/15
 * @description：${description}
 * @dodified By：
 */

@Configuration
@EnableResourceServer
public class ResoucesConfiguration extends ResourceServerConfigurerAdapter {


    private static final String RESOURCE_ID = "get_user_info";

    @Autowired
    private RedisConnectionFactory redisConnection;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            // 允许 路径为/healthcheck/ 后面为任意信息的路径地址 不需要通过oauth2登录授权
            .antMatchers("/healthcheck").permitAll()
            .antMatchers("/newFreeLogin/**").permitAll()
            .antMatchers("/oauthLogin/**").permitAll()
            .anyRequest().authenticated();
    }

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnection);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        resources.tokenServices(defaultTokenServices).resourceId(RESOURCE_ID).stateless(false);
    }


}


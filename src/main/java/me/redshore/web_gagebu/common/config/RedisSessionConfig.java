package me.redshore.web_gagebu.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
@Profile({"!local & !test & !dev & !default"})
public class RedisSessionConfig {

}

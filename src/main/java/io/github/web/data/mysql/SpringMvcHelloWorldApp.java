package io.github.web.data.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class SpringMvcHelloWorldApp extends WebMvcConfigurerAdapter
{
	public static void main(String[] args) {
		SpringApplication.run(SpringMvcHelloWorldApp.class, args);
	}
}
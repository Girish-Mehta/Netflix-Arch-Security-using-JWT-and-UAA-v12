package com.demo.uaa.uaaserver;

import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
@EnableFeignClients("com.demo.uaa.uaaserver")
public class UaaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UaaServerApplication.class, args);
	}
	
	@Bean
	public Docket api() throws IOException, XmlPullParserException {
        return new Docket(DocumentationType.SWAGGER_2);
	}
}
	
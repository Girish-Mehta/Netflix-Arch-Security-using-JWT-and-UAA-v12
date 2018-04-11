package com.demo.greet.s2;

import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@RestController
public class S2Application {

	public static void main(String[] args) {
		SpringApplication.run(S2Application.class, args);
	}
	
	@GetMapping("/search")
	public String search() {
		return "Found a Biscuit";
	}
	
	@Bean
	public Docket api() throws IOException, XmlPullParserException {
        return new Docket(DocumentationType.SWAGGER_2);
	}
	
}

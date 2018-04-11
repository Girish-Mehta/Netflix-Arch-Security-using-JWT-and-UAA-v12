package com.demo;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Component
@Primary
@EnableAutoConfiguration
public class DocumentationController  implements SwaggerResourcesProvider  {

	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<>();
		resources.add(swaggerResource("registration-service", "/registration-service/v2/api-docs", "2.0"));
		resources.add(swaggerResource("uaa-server", "/uaa-server/v2/api-docs", "2.0"));
		resources.add(swaggerResource("hello-service", "/hello-service/v2/api-docs", "2.0"));
		resources.add(swaggerResource("s2", "/s2/v2/api-docs", "2.0"));
		
		return resources;
	}
	
	private SwaggerResource swaggerResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location);
		swaggerResource.setSwaggerVersion(version);
		return swaggerResource;
	}

}
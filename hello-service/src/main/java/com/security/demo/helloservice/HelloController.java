package com.security.demo.helloservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@Value("${accepted-roles}")
	String acceptedRoles;

	@Autowired
	private UaaProxy uaaproxy;
	
	@GetMapping("/user/hello")
	public String getHello(@RequestHeader("application-token") String token) {
		String status = uaaproxy.verifyTokenComingFromService(token);
		String[] data = status.split(",");

		String isAuthorised = data[0];
		if (isAuthorised.equals("true")) {
			String[] rolesFromConfig = acceptedRoles.split(",");
			for (String temp : rolesFromConfig) {
				String role = data[1];
				if (temp.equals(role)) {
					return "Do Your Work";
				}
			}
			return "Unauthorized";
		}
		return "Unauthorized";
	}
}
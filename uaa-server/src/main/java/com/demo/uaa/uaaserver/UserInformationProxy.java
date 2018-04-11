package com.demo.uaa.uaaserver;

import javax.validation.Valid;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


//accesing db service through feign client
@FeignClient(value="registration-service",url="http://localhost:9000")
@RibbonClient(name="registration-service")
public interface UserInformationProxy {

	@PostMapping("/login")
	public String getUserInformationn(@RequestBody UserInformation userInfoInBody);
	
	@PostMapping("/register")
	public String addUserInformation(@Valid @RequestBody UserInformation dataOfUserInBody);
}
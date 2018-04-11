package com.security.demo.helloservice;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="uaa-server",url="http://localhost:7000")
@RibbonClient(name="uaa-server")
public interface UaaProxy {
    
    @GetMapping("/verifytoken/{token}")
   public String verifyTokenComingFromService(@PathVariable(value="token") String token);
    
}
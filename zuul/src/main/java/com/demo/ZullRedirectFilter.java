package com.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class ZullRedirectFilter extends ZuulFilter {
	@Value("${Authorities}")
	String Authorities;
	
	@Value("${loginService}")
	String loginService;
	
	@Value("${publicUser}")
	String publicUser;

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		// during implementation, the hello-service shall come after a route and hence this 
		// line below shall not be written therefore this is only a temporary line to be written
		// until final implementation is not done using zuul routes
		if (request.getServletPath().equals("/hello-service/v2/api-docs"))
			//return HttpServletResponse.SC_FOUND;
		return new ResponseEntity<>(HttpStatus.FOUND);

		// if request is to public service
		String[] urlParts = request.getServletPath().split("/");
		String serviceUrl = urlParts[1];
		
		// check if the role has access to the serivce that the user is trying to access
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(Authorities);
			System.out.println(Authorities);
		} catch (ParseException e3) {
			return HttpServletResponse.SC_FORBIDDEN;
			// e3.printStackTrace();
		}
		String services = (String) json.get(publicUser);
		String[] serviceList = services.split(",");
		for (String service : serviceList) {
			if (serviceUrl.equals(service)) 
				// then let go
				//return HttpServletResponse.SC_OK;
				System.out.println("public service"+HttpStatus.FORBIDDEN);
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		try {
			// if header contains username and pass
			//System.out.println(request.getReader().toString());
			String token = request.getHeader("application-token");
			System.out.println("Hererere" + token.length());
			return HttpServletResponse.SC_OK;
		} catch (Exception e) {
			// redirect to login page if service is private
			try {
				ctx.setSendZuulResponse(false);
				ctx.put("forward:", loginService);
				ctx.setResponseStatusCode(HttpServletResponse.SC_TEMPORARY_REDIRECT);
				ctx.getResponse().sendRedirect(loginService);
				System.out.println("redirecting");
				return HttpServletResponse.SC_TEMPORARY_REDIRECT;
			} catch (Exception e1) {
				return HttpServletResponse.SC_FORBIDDEN;
			}
		}

		// return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}
}
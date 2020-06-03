package com.lab.call;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

public class ProxyFilter implements Filter {

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) req;
		Enumeration<String> headerNames = httpRequest.getHeaderNames();

		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				System.out.println("HeaderName	: "+headerName);
				System.out.println("HeaderValue	: "+ httpRequest.getHeader(headerName));
			}
		}

		PrintWriter out = resp.getWriter();
		out.print("filter is invoked before");

		chain.doFilter(req, resp);// sends request to next resource

		out.print("filter is invoked after");
	}

	public void destroy() {
	}
}

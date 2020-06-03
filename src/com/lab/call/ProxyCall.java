package com.lab.call;

//Import required java libraries
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

//Extend HttpServlet class
public class ProxyCall extends HttpServlet {
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("Get-->Call-->Fwd");
    	forwardRequest("GET", req, resp);
    }


    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    	System.out.println("Post-->Call-->Fwd");
    	forwardRequest("POST", req, resp);
    }

    private void forwardRequest(String method, HttpServletRequest req, HttpServletResponse resp) {
    	
    	final boolean hasoutbody = (method.equals("POST"));
        final String fwdHostUrl = "http://localhost:8081/java-servlet/servlet-call";
        System.out.println(fwdHostUrl);
        
        try {
            //final URL url = new URL(GlobalConstants.CLIENT_BACKEND_HTTPS + req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
        	final URL url = new URL(fwdHostUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            final Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                final String header = headers.nextElement();
                final Enumeration<String> values = req.getHeaders(header);
                while (values.hasMoreElements()) {
                    final String value = values.nextElement();
                    conn.addRequestProperty(header, value);
                }
            }

          //conn.setFollowRedirects(false);  // throws AccessDenied exception
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(hasoutbody);
            conn.connect();

            final byte[] buffer = new byte[16384];
            while (hasoutbody) {
                final int read = req.getInputStream().read(buffer);
                if (read <= 0) break;
                conn.getOutputStream().write(buffer, 0, read);
            }

            System.out.println("original length => "+resp.getContentType());
            resp.setStatus(conn.getResponseCode());
            
            for (int i = 1; ; ++i) {
                final String header = conn.getHeaderFieldKey(i);
                if (header == null) break;
                final String value = conn.getHeaderField(i);
                System.out.println("header : "+header+" :: value : "+value);
                if(header.equalsIgnoreCase("Content-Length")) {
                	resp.setHeader(header, null);
                } else {
                	resp.setHeader(header, value);
                }
                
            }
            
            if(true) {
            	PrintWriter out = resp.getWriter();
	            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            StringBuffer sb = new StringBuffer();
				String inputLine;
				while ((inputLine = in.readLine()) != null) 
					sb.append(inputLine);
				in.close();
				
				out.write(sb.toString());
				out.close();
            }

       
            
        } catch (Exception e) {
            e.printStackTrace();
            // pass
        }
    }

   
    public void destroy() {
    	// do nothing.
    }
}
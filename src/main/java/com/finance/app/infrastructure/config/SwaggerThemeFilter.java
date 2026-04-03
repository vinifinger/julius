package com.finance.app.infrastructure.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SwaggerThemeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        // Intercept both the redirect and the actual index.html requests
        if (uri != null && uri.contains("swagger-ui/index.html")) {
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

            // Proceed with the chain but buffer the response
            chain.doFilter(request, responseWrapper);

            byte[] responseArray = responseWrapper.getContentAsByteArray();
            String html = new String(responseArray, StandardCharsets.UTF_8);

            // Inject the CSS link before the closing head tag
            String customCss = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/swagger-dark.css\">\n";
            html = html.replace("</head>", customCss + "</head>");

            byte[] newResponse = html.getBytes(StandardCharsets.UTF_8);
            
            // Set the new length and write back to the real response output stream
            response.setContentLength(newResponse.length);
            response.getOutputStream().write(newResponse);
        } else {
            chain.doFilter(request, response);
        }
    }
}

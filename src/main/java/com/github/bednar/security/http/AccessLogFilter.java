package com.github.bednar.security.http;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging access to standard output.
 *
 * @author Jakub Bednář (25/08/2013 11:12 AM)
 */
public class AccessLogFilter implements Filter
{
    private static final Logger LOG = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Object[] information = {
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader(HttpHeaders.USER_AGENT)
        };

        LOG.info("AccessLog[{}][{}][{}][{}]", information);

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {
    }
}

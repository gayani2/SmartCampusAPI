package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, 
                                         ContainerResponseFilter {

    private static final Logger LOGGER = 
        Logger.getLogger(ApiLoggingFilter.class.getName());

    // Runs BEFORE every request reaches resource method
    @Override
    public void filter(ContainerRequestContext requestContext) 
            throws IOException {
        LOGGER.info("Incoming Request: " 
            + requestContext.getMethod() 
            + " " 
            + requestContext.getUriInfo().getRequestUri());
    }

    // Runs AFTER every response leaves resource method
    @Override
    public void filter(ContainerRequestContext requestContext, 
                       ContainerResponseContext responseContext) 
            throws IOException {
        LOGGER.info("Outgoing Response: Status " 
            + responseContext.getStatus()
            + " for "
            + requestContext.getMethod()
            + " "
            + requestContext.getUriInfo().getRequestUri());
    }
}
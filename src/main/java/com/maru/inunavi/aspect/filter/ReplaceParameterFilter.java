package com.maru.inunavi.aspect.filter;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ReplaceParameterFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new ParameterReplaceRequestWrapper((HttpServletRequest) request), response);
    }

    private static class ParameterReplaceRequestWrapper extends HttpServletRequestWrapper {

        private boolean isParameterReplacePresent;
        private String before;
        private String after;

        public ParameterReplaceRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String[] getParameterValues(String name) {
            isParameterReplacePresent = ((HandlerMethod) super.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE))
                    .getMethod()
                    .isAnnotationPresent(com.maru.inunavi.aspect.annotation.ReplaceParameter.class);

            if(isParameterReplacePresent){
                com.maru.inunavi.aspect.annotation.ReplaceParameter replaceParameter = ((HandlerMethod) super.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE))
                        .getMethod()
                        .getAnnotation(com.maru.inunavi.aspect.annotation.ReplaceParameter.class);

                before = replaceParameter.before();
                after = replaceParameter.after();
            }

            System.out.println("name = " + name);
            return isParameterReplacePresent ? new String[] {
                    Arrays.stream(super.getParameterValues(name))
                            .map(str -> str.replaceAll(before, after))
                            .collect(Collectors.joining())
            } : super.getParameterValues(name);
        }


    }
}

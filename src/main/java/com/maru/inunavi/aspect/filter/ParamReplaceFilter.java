package com.maru.inunavi.aspect.filter;

import com.maru.inunavi.aspect.annotation.ParamReplace;
import com.maru.inunavi.aspect.annotation.SnakeToCamel;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ParamReplaceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new ParamReplaceRequestWrapper((HttpServletRequest) request), response);
    }

    private static class ParamReplaceRequestWrapper extends HttpServletRequestWrapper {

        private boolean isParamReplacePresent;
        private String before;
        private String after;

        public ParamReplaceRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String[] getParameterValues(String name) {
            isParamReplacePresent = ((HandlerMethod) super.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE))
                    .getMethod()
                    .isAnnotationPresent(ParamReplace.class);

            if(isParamReplacePresent){
                ParamReplace paramReplace = ((HandlerMethod) super.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE))
                        .getMethod()
                        .getAnnotation(ParamReplace.class);

                before = paramReplace.before();
                after = paramReplace.after();
            }

            return isParamReplacePresent ? new String[] {
                    Arrays.stream(super.getParameterValues(name))
                            .map(str -> str.replaceAll(before, after))
                            .collect(Collectors.joining())
            } : super.getParameterValues(name);
        }


    }
}

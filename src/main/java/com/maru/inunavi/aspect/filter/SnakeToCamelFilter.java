package com.maru.inunavi.aspect.filter;

import com.maru.inunavi.aspect.annotation.SnakeToCamel;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

@Component
public class SnakeToCamelFilter implements Filter {

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        chain.doFilter(new SnakeToCamelRequestWrapper((HttpServletRequest) request), response);
    }

    private static class SnakeToCamelRequestWrapper extends HttpServletRequestWrapper{

        private Map<String, String> camelToSnakeMap;
        private boolean isSnakeToCamelPresent;

        public SnakeToCamelRequestWrapper(HttpServletRequest request) {
            super(request);
            camelToSnakeMap = new HashMap<>();
        }

        @Override
        public Enumeration<String> getParameterNames() {
            isSnakeToCamelPresent = ((HandlerMethod) super.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE))
                    .getMethod()
                    .isAnnotationPresent(SnakeToCamel.class);

            if(!isSnakeToCamelPresent){
                return super.getParameterNames();
            }

            List<String> camelParameters = new ArrayList<>();
            Iterator<String> parameterNameIter = super.getParameterNames().asIterator();

            while(parameterNameIter.hasNext()){

                String snakeCase = parameterNameIter.next();
                String camelCase = convertSnakeToCamel(snakeCase);

                camelParameters.add(camelCase);
            }

            return Collections.enumeration(camelParameters);
        }

        @Override
        public String[] getParameterValues(String name) {
            isSnakeToCamelPresent = ((HandlerMethod) super.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE))
                    .getMethod()
                    .isAnnotationPresent(SnakeToCamel.class);

            return isSnakeToCamelPresent ? super.getParameterValues(convertCamelToSnake(name)) : super.getParameterValues(name);
        }

        public String convertSnakeToCamel(String snakeParam) {
            StringBuilder camelParam = new StringBuilder();

            boolean nextUpper = false;

            for(int i = 0; i < snakeParam.length(); i++){
                if(nextUpper){
                    camelParam.append(Character.toUpperCase(snakeParam.charAt(i)));
                    nextUpper = false;
                }
                else if(snakeParam.charAt(i) == '_'){
                    nextUpper = true;
                }
                else{
                    camelParam.append(snakeParam.charAt(i));
                }
            }

            return camelParam.toString();
        }

        public String convertCamelToSnake(String camelParam) {
            StringBuilder snakeParam = new StringBuilder();

            for (int i = 0; i < camelParam.length(); i++) {
                char currentChar = camelParam.charAt(i);

                if (Character.isUpperCase(currentChar) && i > 0 && Character.isLowerCase(camelParam.charAt(i - 1))) {
                    snakeParam.append("_");
                }

                snakeParam.append(Character.toLowerCase(currentChar));
            }

            return snakeParam.toString();
        }

    }
}

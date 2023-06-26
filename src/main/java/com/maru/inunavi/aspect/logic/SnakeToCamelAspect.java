package com.maru.inunavi.aspect.logic;

import com.maru.inunavi.aspect.annotation.SnakeToCamel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.ParameterMap;
import org.apache.el.util.ReflectionUtil;
import org.aspectj.util.Reflection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.util.MethodInvocationUtils;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class SnakeToCamelAspect implements Filter {

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        chain.doFilter(new ParameterEditRequestWrapper((HttpServletRequest) request),response);
    }

    private static class ParameterEditRequestWrapper extends HttpServletRequestWrapper{

        private Map<String, String[]> newParameterMap;

        public ParameterEditRequestWrapper(HttpServletRequest request) {
            super(request);
            newParameterMap = new HashMap<>();
        }

        public String getParameter(String snakeText) {
            String returnValue = null;
            String[] paramArray = getParameterValues(snakeText);
            if(paramArray != null && paramArray.length > 0){
                returnValue = paramArray[0];
            }
            return returnValue;
        }

        public Map getParameterMap(){
            return Collections.unmodifiableMap(newParameterMap);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(newParameterMap.keySet());
        }

        public void setParameter(String key, String value){
            newParameterMap.put(convert(key), new String[]{ value });
        }

        public String convert(String snakeParam) {
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
    }
}

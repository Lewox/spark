package spark.servlet;

import java.util.Enumeration;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

public class FilterConfigWrapper implements FilterConfig {
 
    private final FilterConfig delegate;

    public FilterConfigWrapper(FilterConfig delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getFilterName() {
        return delegate.getFilterName();
    }

    @Override
    public String getInitParameter(String name) {
        if (name.equals("applicationClass")) {
            return "spark.servlet.MyApp";
        }
        return delegate.getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return delegate.getInitParameterNames();
    }

    @Override
    public ServletContext getServletContext() {
        return delegate.getServletContext();
    }
}

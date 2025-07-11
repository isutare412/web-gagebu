package me.redshore.web_gagebu.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 200)
@Slf4j
public class AccessLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        var startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            var endTime = System.currentTimeMillis();
            var elapsedMillis = endTime - startTime;

            int responseContentLength =
                Integer.parseInt(response.getHeader("Content-Length") != null
                                 ? response.getHeader("Content-Length")
                                 : "-1");

            log.atInfo()
               .addKeyValue("method", request.getMethod())
               .addKeyValue("uri", getFullRequestUri(request))
               .addKeyValue("remoteAddr", request.getRemoteAddr())
               .addKeyValue("protocol", request.getProtocol())
               .addKeyValue("requestContentLength", request.getContentLength())
               .addKeyValue("responseContentLength", responseContentLength)
               .addKeyValue("status", response.getStatus())
               .addKeyValue("elapsedMillis", elapsedMillis)
               .log("Handle http request");
        }
    }

    private static String getFullRequestUri(HttpServletRequest request) {
        var uri = new StringBuilder(request.getRequestURI());

        var queryString = request.getQueryString();
        if (queryString != null) {
            uri.append("?").append(queryString);
        }

        return uri.toString();
    }

}

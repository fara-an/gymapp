package epam.lab.gymapp.filter.perrequest;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
@Component("transactionIdFilter")
public class TransactionIdFilter extends OncePerRequestFilter {

    public static final String TRANSACTION_ID = "transactionId";
    public static final String HEADER_NAME = "X-Transaction-Id";



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tx = request.getHeader(HEADER_NAME);
        if (tx==null ||  tx.isEmpty()){
           tx= UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID, tx);

        response.setHeader(HEADER_NAME, tx);

        try{
            filterChain.doFilter(request, response);
        }finally {
            MDC.clear();
        }

    }
}

package epam.lab.gymapp.filter.perrequest;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class TransactionIdFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String HEADER_NAME = "X-transaction-Id";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String txId = UUID.randomUUID().toString();
        try {
            MDC.put(TRANSACTION_ID, txId);
            ((HttpServletResponse) response).setHeader(HEADER_NAME, txId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }

    }
}

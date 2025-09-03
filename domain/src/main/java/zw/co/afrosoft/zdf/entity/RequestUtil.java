package zw.co.afrosoft.zdf.entity;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;



@Component
public class RequestUtil {

    @Autowired(required = false)
    private HttpServletRequest request;

    public String getClientIp() {
        if (RequestContextHolder.getRequestAttributes() == null || request == null) {
            return "N/A";
        }

        String ip = request.getHeader("X-Forwarded-For");
        return (ip == null || ip.isBlank()) ? request.getRemoteAddr() : ip.split(",")[0].trim();
    }
}


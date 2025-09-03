package zw.co.afrosoft.zdf.alert;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.feign.Role;
import zw.co.afrosoft.zdf.loans.Loan;

import java.util.List;
import java.util.Set;

/*
 * created by  Romeo Jerenyama
 * created on  21/3/2025 at 12:59
 */

/**
 * Service interface for managing real-time alerts via Server-Sent Events (SSE).
 * <p>
 * This service handles subscription management based on user roles and
 * sending alert notifications related to loan defaults and fully paid securities.
 * </p>
 */
public interface AlertService {

    /**
     * Subscribes a client to server-sent events (SSE) based on provided roles.
     * Typically used to subscribe users with ROLE_SYSTEM_SUPER_ADMIN or similar privileges.
     *
     * @param roles the set of roles for which the subscription is requested
     * @return an {@link SseEmitter} instance to send server-sent events asynchronously
     */
    SseEmitter subscribe(Set<Role> roles);

    /**
     * Sends alert notifications for loans that are in default status.
     *
     * @param defaultedLoans the list of loans that are defaulted
     */
    void sendLoanDefaultAlert(List<Loan> defaultedLoans);

    /**
     * Sends alert notifications for securities that have been fully paid up.
     *
     * @param securities the list of securities that are fully paid
     */
    void sendSecuritiesPaidUpAlert(List<Securities> securities);
}

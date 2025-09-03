package zw.co.afrosoft.zdf.alert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.feign.Role;
import zw.co.afrosoft.zdf.loans.Loan;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * created by  Romeo Jerenyama
 * created on  17/3/2025 at 13:08
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final List<SseEmitter> adminEmitters = new CopyOnWriteArrayList<>();

    private static final String EVENT_LOAN_DEFAULT_ALERT = "loan-default-alert";
    private static final String EVENT_PAID_UP_SECURITIES = "paid-up-securities";

    /**
     * {@inheritDoc}
     */
    @Override
    public SseEmitter subscribe(Set<Role> roles) {
        if (!isAdmin(roles)) {
            throw new SecurityException("Access Denied: Only admins can subscribe to alerts.");
        }

        SseEmitter emitter = new SseEmitter(0L); // No timeout
        adminEmitters.add(emitter);

        emitter.onCompletion(() -> adminEmitters.remove(emitter));
        emitter.onTimeout(() -> adminEmitters.remove(emitter));
        emitter.onError(e -> {
            log.error("Emitter error: {}", e.getMessage(), e);
            adminEmitters.remove(emitter);
        });

        return emitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendLoanDefaultAlert(List<Loan> defaultedLoans) {
        if (defaultedLoans == null || defaultedLoans.isEmpty() || adminEmitters.isEmpty()) return;

        Alert<Loan> alert = Alert.<Loan>builder()
                .type("DEFAULTED_LOAN")
                .message("Loan defaulters identified. Click to view details.")
                .actionItems(defaultedLoans)
                .timestamp(LocalDateTime.now())
                .build();

        broadcast(EVENT_LOAN_DEFAULT_ALERT, alert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSecuritiesPaidUpAlert(List<Securities> securities) {
        if (securities == null || securities.isEmpty() || adminEmitters.isEmpty()) return;

        Alert<Securities> alert = Alert.<Securities>builder()
                .type("PAID_UP_SECURITIES")
                .message("Member has paid up the securities!")
                .actionItems(securities)
                .timestamp(LocalDateTime.now())
                .build();

        broadcast(EVENT_PAID_UP_SECURITIES, alert);
    }

    /**
     * Broadcasts an alert to all subscribed SSE emitters.
     *
     * @param eventName the name of the event to send
     * @param alert     the alert payload
     */
    private void broadcast(String eventName, Alert alert) {
        for (SseEmitter emitter : new ArrayList<>(adminEmitters)) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(alert));
            } catch (IOException e) {
                log.warn("Failed to send alert. Removing emitter: {}", e.getMessage());
                emitter.complete();
                adminEmitters.remove(emitter);
            }
        }
    }

    /**
     * Checks if the user has administrative privileges.
     *
     * @param roles the set of user roles
     * @return true if user is admin
     */
    private boolean isAdmin(Set<Role> roles) {
        return roles != null &&
                (roles.contains(Role.ROLE_SYSTEM_ADMIN) || roles.contains(Role.ROLE_ADMIN));
    }
}

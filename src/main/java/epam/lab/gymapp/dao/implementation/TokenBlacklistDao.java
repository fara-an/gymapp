package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.model.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenBlacklistDao.class);
    private final SessionFactory sessionFactory;


    @Transactional
    public void save(BlacklistedToken token) {
        LOGGER.debug("Token is being blacklisted ");
        sessionFactory.getCurrentSession().persist(token);
    }
    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        LOGGER.debug("Checking whether token is blacklisted or not  ");
        String hql = "SELECT COUNT(t) FROM BlacklistedToken t WHERE t.token = :token";
        Long count = sessionFactory.getCurrentSession()
                .createQuery(hql, Long.class)
                .setParameter("token", token)
                .uniqueResult();

        return count != null && count > 0;
    }

    @Transactional
    public void deleteExpiredTokens() {
        String hql = "DELETE FROM BlacklistedToken t WHERE t.expiryDate < :now";
        sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("now", Instant.now())
                .executeUpdate();
    }
}

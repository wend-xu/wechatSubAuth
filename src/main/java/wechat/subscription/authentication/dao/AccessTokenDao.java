package wechat.subscription.authentication.dao;

import org.springframework.stereotype.Repository;
import wechat.subscription.authentication.pojo.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface AccessTokenDao extends JpaRepository<AccessToken,Long> {
    List<AccessToken> findAccessTokensByActive(String active);
}

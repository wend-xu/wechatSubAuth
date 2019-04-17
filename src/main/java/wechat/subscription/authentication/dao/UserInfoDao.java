package wechat.subscription.authentication.dao;

import org.springframework.stereotype.Repository;
import wechat.subscription.authentication.pojo.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface UserInfoDao extends JpaRepository<UserInfo,Long> {
    UserInfo findUserInfoByUuid(String uuid);

    List<UserInfo> findUserInfosByOpenid(String openId);
}

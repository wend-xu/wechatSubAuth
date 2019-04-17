package wechat.subscription.authentication.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@DynamicUpdate
@DynamicInsert
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Integer subscribe;
    String openid;
    String nickname;
    Long subscribeTime;
    String uuid;

    public UserInfo() {
    }

    public UserInfo(String uuid) {
        this.uuid = uuid;
    }
}


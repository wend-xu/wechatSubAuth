package wechat.subscription.authentication.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String accessTokrn;
    Long expireTime;
    String active;

    public AccessToken(String accessTokrn, Long expireTime, String active) {
        this.accessTokrn = accessTokrn;
        this.expireTime = expireTime;
        this.active = active;
    }

    public AccessToken(){
    }
}

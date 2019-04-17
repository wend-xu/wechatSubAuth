package wechat.subscription.authentication.pojo;

import lombok.Data;

@Data
public class ReturnBody {
    int status;
    String message;
    Object data;

    public ReturnBody(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ReturnBody(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

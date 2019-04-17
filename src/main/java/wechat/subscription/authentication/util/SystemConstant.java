package wechat.subscription.authentication.util;

public class SystemConstant {
    public static final int RESPONSE_FAIL = 100001;
    public static final int RESPONSE_SUCCESS = 100002;

    public static final int REQUEST_SUCCESS_CODE = 200;

    public static final String ACCESS_TOKEN_ACTIVE = "active";
    public static final String ACCESS_TOKEN_INACTIVE = "inactive";

    public static final String TOKEN = "token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String OPEN_ID = "openid";
    public static final String TICKET = "ticket";
    public static final String EVENT = "Event";
    public static final String EVENT_KEY = "EventKey";
    public static final String FROM_USER_NAME = "FromUserName";
    public static final String TO_USER_NAME = "ToUserName";
    public static final String NICK_NAME = "nickname";
    public static final String SUBSCRIBE = "subscribe";
    public static final String SUBSCRIBE_TIME = "subscribe_time";
    public static final String URL = "url";

    public static final int WECHAT_USER_HAS_SUBSCRIBE = 1;
    public static final int WECHAT_USER_REMOVE_SUBSCRIBE = 1;
    public static final String WECHAT_TOKEN = "WENDE";
    public static final String WECHAT_DEVELOP_APPID = "wxbd2dbac82cf9cca6";
    public static final String WECHAT_DEVELOP_APPSECRET = "21af8c392a40158035fec115cd948d6d";
    public static final String WECHAT_SUBSCRIBE_EVENT = "subscribe";
    public static final String WECHAT_UNSUBSCIBE_EVENT = "unsubscribe";
    public static final String WECHAT_SCAN_EVENT = "SCAN";
    public static final String WECHAT_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";
    public static final String WECGAT_GET_TEMP_QRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?";
    public static final String WECHAT_GET_SUBSCRIBE_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?";
}

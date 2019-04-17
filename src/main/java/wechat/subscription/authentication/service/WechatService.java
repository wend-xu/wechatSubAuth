package wechat.subscription.authentication.service;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wechat.subscription.authentication.dao.AccessTokenDao;
import wechat.subscription.authentication.dao.UserInfoDao;
import wechat.subscription.authentication.pojo.AccessToken;
import wechat.subscription.authentication.pojo.UserInfo;
import wechat.subscription.authentication.util.ParseTool;
import wechat.subscription.authentication.util.SystemConstant;

import java.beans.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WechatService {
    @Autowired
    AccessTokenDao accessTokenDao;
    @Autowired
    UserInfoDao userInfoDao;

    private static final Logger logger = LoggerFactory.getLogger(WechatService.class);
    public boolean verifySignature(String signature, String timestamp, String nonce){
        return ParseTool.verifySignature(signature,timestamp,nonce);
    }

    @Transient
    public String getAccesstoken(){
        List<AccessToken> accessTokens = accessTokenDao.findAccessTokensByActive(SystemConstant.ACCESS_TOKEN_ACTIVE);
        if(accessTokens.size() == 1){
            AccessToken accessToken = accessTokens.get(0);
            if(accessToken.getExpireTime() > new Date().getTime())
                return accessToken.getAccessTokrn();
        }else if(accessTokens.size() > 1){
            accessTokens.forEach(accessToken -> {
                accessToken.setActive(SystemConstant.ACCESS_TOKEN_INACTIVE);
                accessTokenDao.save(accessToken);
            });
        }
        String accessTokenStr = requestAccessToken();
        AccessToken accessToken = new AccessToken(accessTokenStr,new Date().getTime()+7000*1000, SystemConstant.ACCESS_TOKEN_ACTIVE);
        accessTokenDao.save(accessToken);
        return accessTokenStr;
    }

    private String requestAccessToken(){
        String accessToken = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(SystemConstant.WECHAT_GET_ACCESS_TOKEN +
                "appid="+SystemConstant.WECHAT_DEVELOP_APPID+"&secret="+SystemConstant.WECHAT_DEVELOP_APPSECRET,JSONObject.class);

        JSONObject body = null;
        if(responseEntity.getStatusCodeValue() == 200) {
            body = responseEntity.getBody();
            accessToken = body.getString(SystemConstant.ACCESS_TOKEN);
        }
        return accessToken;
    }

    public boolean loginStatusVerify(String token){
        String uuid = ParseTool.verifyToken(token,null);
        UserInfo userInfo = userInfoDao.findUserInfoByUuid(uuid);
        return userInfo != null && userInfo.getSubscribe() != null && userInfo.getSubscribe()==1;
    }

    @Transient
    public JSONObject getQRCode(String accessToken){
        String ticket = "";
        String token = "";
        String uuid = ParseTool.createUUID();

        JSONObject requestBody = new JSONObject();
        requestBody.put("expire_seconds",7200);
        requestBody.put("action_name","QR_STR_SCENE");

        JSONObject actionInfo = new JSONObject();
        JSONObject scence = new JSONObject();
        scence.put("scene_str",uuid);

        actionInfo.put("scene",scence);
        requestBody.put("action_info",actionInfo);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(SystemConstant.WECGAT_GET_TEMP_QRCODE+
                SystemConstant.ACCESS_TOKEN+"="+accessToken,requestBody,JSONObject.class);

        if(responseEntity.getStatusCodeValue() == SystemConstant.REQUEST_SUCCESS_CODE){
            JSONObject requestResult = responseEntity.getBody();
            ticket = requestResult.getString(SystemConstant.TICKET);
            token = ParseTool.createToken(uuid,null,7);
            userInfoDao.save(new UserInfo(uuid));
        }

        JSONObject result = new JSONObject();
        result.put(SystemConstant.TICKET,ticket);
        result.put(SystemConstant.TOKEN,token);

        return  result;
    }

    @Transient
    public String userScanOrSubscribe(Map<String,String> map,String accessToken){
        String event = map.get(SystemConstant.EVENT);
        String openId = map.get(SystemConstant.FROM_USER_NAME);
        logger.info(event);
        //取消关注事件
        if(event.equals(SystemConstant.WECHAT_UNSUBSCIBE_EVENT)){
            List<UserInfo> userInfos = userInfoDao.findUserInfosByOpenid(openId);
            userInfos.forEach(userInfo -> {
                //修改已关注的信息
                if(userInfo.getSubscribe().equals(SystemConstant.WECHAT_USER_HAS_SUBSCRIBE)){
                    userInfo.setSubscribe(SystemConstant.WECHAT_USER_REMOVE_SUBSCRIBE);
                    userInfoDao.save(userInfo);
                }
            });
            return "";
        }

        String eventKey = map.get(SystemConstant.EVENT_KEY);
        String subscribeId = map.get(SystemConstant.TO_USER_NAME);

        UserInfo userInfo = userInfoDao.findUserInfoByUuid(eventKey);
        JSONObject userInfoByOpenId = getUserInfoByOpenId(openId,accessToken);
        //用户已订阅
        if(userInfo != null && userInfoByOpenId != null
                && userInfoByOpenId.getInt(SystemConstant.SUBSCRIBE) == SystemConstant.WECHAT_USER_HAS_SUBSCRIBE){
            userInfo.setOpenid(openId);
            userInfo.setNickname(userInfoByOpenId.getString(SystemConstant.NICK_NAME));
            userInfo.setSubscribe(userInfoByOpenId.getInt(SystemConstant.SUBSCRIBE));
            userInfo.setSubscribeTime(userInfoByOpenId.getLong(SystemConstant.SUBSCRIBE_TIME));
            userInfoDao.save(userInfo);
            return ParseTool.returnTextMsg(openId,subscribeId,"扫码成功");
        }

        return "";
    }

    public JSONObject getUserInfoByOpenId(String openId,String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(SystemConstant.WECHAT_GET_SUBSCRIBE_USER_INFO+
                SystemConstant.ACCESS_TOKEN+"="+accessToken+"&"+SystemConstant.OPEN_ID+"="+openId,JSONObject.class);
        if(responseEntity.getStatusCodeValue() == 200){
            return responseEntity.getBody();
        }
        return null;
    }

}

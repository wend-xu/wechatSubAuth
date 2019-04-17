package wechat.subscription.authentication.controller;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import wechat.subscription.authentication.pojo.ReturnBody;
import wechat.subscription.authentication.service.WechatService;
import wechat.subscription.authentication.util.ParseTool;
import wechat.subscription.authentication.util.SystemConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/wechat")
@CrossOrigin
public class WechatController {
    @Autowired
    WechatService wechatService;

    //微信服务器接入事件
    @GetMapping("/request")
    public String validate(
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestParam(value = "echostr") String echostr){
        boolean result = wechatService.verifySignature(signature,timestamp,nonce);
        return result?echostr:null;
    }

    //微信服务器推送事件
    @PostMapping("/request")
    public String userEvent(HttpServletRequest request){
        Map<String,String>  map = ParseTool.parseRequestBodyXMLToMap(request);
        String event = map.get(SystemConstant.EVENT);
        //只处理订阅，取消订阅，扫码三个事件
        if(event.equals(SystemConstant.WECHAT_SUBSCRIBE_EVENT) ||
                event.equals(SystemConstant.WECHAT_UNSUBSCIBE_EVENT) || event.equals(SystemConstant.WECHAT_SCAN_EVENT))
            return wechatService.userScanOrSubscribe(map,wechatService.getAccesstoken());
        else
            return "";
    }

    @PostMapping("/download/wenku")
    public ReturnBody downloadRequest(@RequestBody JSONObject requestBody, HttpServletResponse httpResponse){
        String url = requestBody.getString("resourceUrl");
        String token = requestBody.getString("token");
        ReturnBody returnBody = new ReturnBody(SystemConstant.RESPONSE_FAIL,"扫码后获取下载权限");

        if(!token.equals("") && wechatService.loginStatusVerify(token)){
            //本来这段代码应该属于service
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
            map.add("url",url);
            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map,headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://61.160.195.141:8888/api2/wenku/",request,String.class);
            if(responseEntity.getStatusCodeValue() == SystemConstant.REQUEST_SUCCESS_CODE){
                JSONObject jsonObject = JSONObject.fromObject(responseEntity.getBody());
                JSONObject data = new JSONObject();
                data.put("url",jsonObject.getString("link"));
                returnBody = new ReturnBody(SystemConstant.RESPONSE_SUCCESS,"即将开始下载");
                returnBody.setData(data);
            }
        }else{
            JSONObject QRCodeInfo = wechatService.getQRCode(wechatService.getAccesstoken());
            returnBody.setData(QRCodeInfo);
        }
        return returnBody;
    }
}

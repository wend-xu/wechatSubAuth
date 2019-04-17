package wechat.subscription.authentication.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ParseTool {
    private static final String SECRET = "c7298c6ff4844e3984260cc1cc9c50e9";

    public static String createToken(String uuid,String claim,int expireDay){
        if(claim == null) claim = "uuid";
        String token = null;
        if(!uuid.equals("")){
            Map<String,Object> header = new HashMap<>();
            header.put("alg","HS256");
            header.put("typ","JWT");
            Date issueAt = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,expireDay);
            Date expiresAt = calendar.getTime();
            token = JWT.create().withHeader(header).
                    withClaim(claim,uuid).
                    withExpiresAt(expiresAt).withIssuedAt(issueAt).
                    sign(Algorithm.HMAC256(SECRET));
        }
        return token;
    }

    public static String verifyToken(String token,String claim){
        if(claim == null) claim = "uuid";
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decodedJWT = null;
        try{
            decodedJWT = verifier.verify(token);
        }catch (TokenExpiredException e){
            e.printStackTrace();
        }finally {
            if(decodedJWT == null){
                return null;
            }else {
                return decodedJWT.getClaim(claim).asString();
            }
        }
    }

    public static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    public static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    public static boolean verifySignature(String signature, String timestamp, String nonce){
        List<String> arr = new ArrayList<>();
        arr.add(SystemConstant.WECHAT_TOKEN);
        arr.add(timestamp);
        arr.add(nonce);

        Collections.sort(arr);

        final StringBuilder sb = new StringBuilder();
        arr.forEach(value -> sb.append(value));

        MessageDigest md;
        String equalSignature = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(sb.toString().getBytes());
            equalSignature = byteToStr(digest);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return equalSignature != null? equalSignature.equals(signature.toUpperCase()): true;
    }

    public static String createUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public static String returnTextMsg(String openId,String subscriptionId,String content){
        return "<xml>" +
                "  <ToUserName>+"+openId+"+</ToUserName>" +
                "  <FromUserName>"+subscriptionId+"</FromUserName>" +
                "  <CreateTime>"+new Date().getTime()+"</CreateTime>" +
                "  <MsgType>text</MsgType>" +
                "  <Content>"+content+"</Content>" +
                "</xml>";
    }

    public static Map<String,String> parseRequestBodyXMLToMap(HttpServletRequest request){
        Map<String,String> map = new HashMap<>();
        try{
            InputStream is = request.getInputStream();
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            Element rootEl = document.getRootElement();
            List<Element> elements = rootEl.elements();
            elements.forEach(element -> { map.put(element.getName(),element.getText()); });
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}

package xyz.wystudio.CloudPan.OneTwoThreePan;

import com.fasterxml.jackson.databind.JsonNode;
import xyz.wystudio.CloudPan.api.UserInfo;

/**
 * 此类为 123云盘 的用户信息实现类
 */
public class OneTwoThreeUserInfo implements UserInfo {
    // 用户账号id
    int uid;
    // 昵称
    String nickName;
    // 头像
    String headImage;
    //手机号
    String passport;
    // 邮箱
    String mail;
    // 已用空间
    long spaceUsed;
    // 永久空间
    long spacePermanent;
    // 临时空间
    long spaceTemp;
    // 临时空间到期日
    String spaceTempExpr;
    // 是否会员
    boolean isVip;
    // 剩余直链流量
    int directTraffic;
    // 直链链接是否隐藏UID
    boolean isHideUID;

    public OneTwoThreeUserInfo(JsonNode node) {
        this.uid = node.get("uid").asInt();
        this.nickName = node.get("nickname").asText();
        this.headImage = node.get("headImage").asText();
        this.passport = node.get("passport").asText();
        this.mail = node.get("mail").asText();
        this.spaceUsed = node.get("spaceUsed").asLong();
        this.spacePermanent = node.get("spacePermanent").asLong();
        this.spaceTemp = node.get("spaceTemp").asLong();
        this.spaceTempExpr = node.get("spaceTempExpr").asText();
        this.isVip = node.get("vip").asBoolean();
        this.directTraffic = node.get("directTraffic").asInt();
        this.isHideUID = node.get("isHideUID").asBoolean();
    }

    @Override
    public String getName() {
        return nickName;
    }

    public int getUid() {
        return uid;
    }

    public String getHeadImage() {
        return headImage;
    }

    public String getPassport() {
        return passport;
    }

    public String getMail() {
        return mail;
    }

    public long getSpaceUsed() {
        return spaceUsed;
    }

    public long getSpacePermanent() {
        return spacePermanent;
    }

    public long getSpaceTemp() {
        return spaceTemp;
    }

    public String getSpaceTempExpr() {
        return spaceTempExpr;
    }

    public boolean isVip() {
        return isVip;
    }

    public int getDirectTraffic() {
        return directTraffic;
    }

    public boolean isHideUID() {
        return isHideUID;
    }
}

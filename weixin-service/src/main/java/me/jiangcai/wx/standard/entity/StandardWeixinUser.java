package me.jiangcai.wx.standard.entity;

import me.jiangcai.wx.model.Gender;
import me.jiangcai.wx.model.WeixinUser;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.standard.entity.support.AppIdOpenID;
import me.jiangcai.wx.standard.entity.support.LocaleConverter;
import me.jiangcai.wx.standard.entity.support.StringArrayConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * 适用于JPA
 * openId并非一个天然的主键；主键应该是appKey+openId
 *
 * @author CJ
 */
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "unionId")
        }
)
@IdClass(AppIdOpenID.class)
public class StandardWeixinUser extends WeixinUserDetail implements WeixinUser {

    // 最后一次获取真实微信详情的时间
    private LocalDateTime lastRefreshDetailTime;
    private String accessToken;
    private LocalDateTime accessTimeToExpire;
    private String refreshToken;
    private String[] tokenScopes;

    @Column(columnDefinition = "datetime")
    public LocalDateTime getLastRefreshDetailTime() {
        return lastRefreshDetailTime;
    }

    public void setLastRefreshDetailTime(LocalDateTime lastRefreshDetailTime) {
        this.lastRefreshDetailTime = lastRefreshDetailTime;
    }

    @Column(length = 120)
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Column(columnDefinition = "datetime")
    @Override
    public LocalDateTime getAccessTimeToExpire() {
        return accessTimeToExpire;
    }

    @Override
    public void setAccessTimeToExpire(LocalDateTime accessTimeToExpire) {
        this.accessTimeToExpire = accessTimeToExpire;
    }

    @Column(length = 120)
    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Convert(converter = StringArrayConverter.class)
    @Column(length = 30)
    @Override
    public String[] getTokenScopes() {
        return tokenScopes;
    }

    @Override
    public void setTokenScopes(String[] tokenScopes) {
        this.tokenScopes = tokenScopes;
    }

    @Column(length = 30)
    @Override
    public String getUnionId() {
        return super.getUnionId();
    }

    @Id
    @Column(length = 30)
    @Override
    public String getAppId() {
        return super.getAppId();
    }

    @Id
    @Column(length = 30)
    @Override
    public String getOpenId() {
        return super.getOpenId();
    }

    @Column(length = 100,columnDefinition = "varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    @Override
    public String getNickname() {
        return super.getNickname();
    }

    @Override
    public Gender getGender() {
        return super.getGender();
    }

    @Column(length = 30)
    @Override
    public String getProvince() {
        return super.getProvince();
    }

    @Column(length = 30)
    @Override
    public String getCity() {
        return super.getCity();
    }

    @Column(length = 30)
    @Override
    public String getCountry() {
        return super.getCountry();
    }

    @Column(length = 180)
    @Override
    public String getHeadImageUrl() {
        return super.getHeadImageUrl();
    }

    @Convert(converter = LocaleConverter.class)
    @Column(length = 10)
    @Override
    public Locale getLocale() {
        return super.getLocale();
    }

    @Convert(converter = StringArrayConverter.class)
    @Column(length = 30)
    @Override
    public String[] getPrivilege() {
        return super.getPrivilege();
    }
}

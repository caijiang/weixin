package me.jiangcai.wx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.jiangcai.wx.converter.BooleanDeserializer;
import me.jiangcai.wx.converter.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

/**
 * 这个微信用户详情只可以看到关注本公众号的用户
 *
 * @author CJ
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonIgnoreProperties(value = {"tagid_list"}, ignoreUnknown = true)
public class MyWeixinUserDetail extends WeixinUserDetail {
    // tagid_list:[] 还不知道是什么

    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean subscribe;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime subscribe_time;
    private String remark;
    @JsonProperty("groupid")
    private int groupId;

}

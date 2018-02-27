package me.jiangcai.wx.standard;

import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 在初始化时多干一些事儿，因为h2建表方式跟mysql的差异导致我们支持的mysql建表方式不再h2环境中支持；所以我们采用了后建表的方式。
 * h2在mysql兼容性提高之后，整段代码都应该被移除
 *
 * @author CJ
 */
public interface H2GrammarFix {

    @Transactional
    void fixIt() throws SQLException;
}

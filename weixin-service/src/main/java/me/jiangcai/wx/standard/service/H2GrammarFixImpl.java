package me.jiangcai.wx.standard.service;

import me.jiangcai.wx.standard.H2GrammarFix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * @author CJ
 */
@Service
public class H2GrammarFixImpl implements H2GrammarFix {
    @Autowired(required = false)
    private Set<EntityManager> entityManagerSet;
    @Autowired(required = false)
    private DataSource dataSource;


    @Override
    public void fixIt() throws SQLException {
        EntityManager entityManager;
        if (entityManagerSet != null) {
            //noinspection OptionalGetWithoutIsPresent,ConstantConditions
            entityManager = entityManagerSet.stream().findAny().get();
        } else
            entityManager = null;

        Connection connection;
        if (entityManager != null) {
            connection = entityManager.unwrap(Connection.class);
            if (connection == null) {
                throw new IllegalStateException("@Transactional did not work check DataSupportConfig for details.");
            }
        } else if (dataSource != null) {
            connection = dataSource.getConnection();
        } else
            throw new IllegalStateException("没有JPA也没有datasource，很尴尬");


        try (Statement statement = connection.createStatement()) {
            // 注意此处应该需要跟实体类同时更新
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS STANDARDWEIXINUSER (" +
                    "OPENID VARCHAR(30) NOT NULL, " +
                    "APPID VARCHAR(30) NOT NULL, " +
                    "ACCESSTIMETOEXPIRE DATETIME, " +
                    "ACCESSTOKEN VARCHAR(120), " +
                    "CITY VARCHAR(30), " +
                    "COUNTRY VARCHAR(30), " +
                    "GENDER INTEGER, " +
                    "HEADIMAGEURL VARCHAR(180), " +
                    "LASTREFRESHDETAILTIME DATETIME, " +
                    "LOCALE VARCHAR(10), " +
                    "NICKNAME VARCHAR(100), " +
                    "PRIVILEGE VARCHAR(30), " +
                    "PROVINCE VARCHAR(30), " +
                    "REFRESHTOKEN VARCHAR(120), " +
                    "TOKENSCOPES VARCHAR(30), " +
                    "UNIONID VARCHAR(30), " +
                    "PRIMARY KEY (OPENID, APPID))");
        }

    }
}

package com.soap.search.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfUtil {
    private static final Logger Log = LogManager.getLogger(ConfUtil.class);
    private static Properties soapProperties;
    private static Properties loadProperties(String fileName) {
        Log.info("加载配置文件：{}",fileName);
        Properties properties = new Properties();
        try {
            // 使用 ClassLoader 从 resources 目录读取
            InputStream inputStream = ConfUtil.class.getClassLoader()
                    .getResourceAsStream(fileName);
            if (inputStream != null) {
                properties.load(inputStream);
                inputStream.close();
            }
            return properties;
        } catch (IOException e) {
            Log.error(e.getMessage(),e);
            return null;
        }
    }

    // 获取配置值的便捷方法
    public static String getProperty(String key) {
        if(soapProperties == null) {
            synchronized (ConfUtil.class) {
                if (soapProperties == null) {
                    soapProperties = loadProperties("soap.properties");
                }
            }
        }
        return soapProperties.getProperty(key);
    }
}


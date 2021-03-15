package org.smart4j.plugin.security;

/**
 * 常量接口
 *
 * @author huangyong
 * @since 2.3
 */
public interface SecurityConstant {

    String REALMS = "smart.plugin.security.realms";
    String REALMS_JDBC = "jdbc";
    String REALMS_AD = "ad";
    String REALMS_CUSTOM = "custom";

    String SMART_SECURITY = "smart.plugin.security.custom.class";

    String JDBC_AUTHC_QUERY = "smart.plugin.security.jdbc.authc_query";
    String JDBC_ROLES_QUERY = "smart.plugin.security.jdbc.roles_query";
    String JDBC_PERMISSIONS_QUERY = "smart.plugin.security.jdbc.permissions_query";

    String AD_URL = "smart.plugin.security.ad.url";
    String AD_SYSTEM_USERNAME = "smart.plugin.security.ad.system_username";
    String AD_SYSTEM_PASSWORD = "smart.plugin.security.ad.system_password";
    String AD_SEARCH_BASE = "smart.plugin.security.ad.search_base";

    String CACHEABLE = "smart.plugin.security.cacheable";
}

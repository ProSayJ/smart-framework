package org.smart4j.framework.orm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.smart4j.framework.dao.DatabaseHelper;
import org.smart4j.framework.dao.SqlHelper;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.MapUtil;
import org.smart4j.framework.util.ObjectUtil;
import org.smart4j.framework.util.StringUtil;

/**
 * 提供与实体相关的数据库操作
 *
 * @author huangyong
 * @since 1.0
 */
public class DataSet {

    /**
     * 查询单条数据，并转为相应类型的实体
     */
    public static <T> T select(Class<T> entityClass, String condition, Object... params) {
        condition = transferCondition(entityClass, condition);
        String sql = SqlHelper.generateSelectSql(entityClass, condition, "");
        return DatabaseHelper.queryEntity(entityClass, sql, params);
    }

    /**
     * 查询多条数据，并转为相应类型的实体列表
     */
    public static <T> List<T> selectList(Class<T> entityClass) {
        return selectListWithConditionAndSort(entityClass, "", "");
    }

    /**
     * 查询多条数据，并转为相应类型的实体列表（带有查询条件与查询参数）
     */
    public static <T> List<T> selectListWithCondition(Class<T> entityClass, String condition, Object... params) {
        return selectListWithConditionAndSort(entityClass, condition, "", params);
    }

    /**
     * 查询多条数据，并转为相应类型的实体列表（带有排序方式）
     */
    public static <T> List<T> selectListWithSort(Class<T> entityClass, String sort) {
        return selectListWithConditionAndSort(entityClass, "", sort);
    }

    /**
     * 查询多条数据，并转为相应类型的实体列表（带有查询条件、排序方式与查询参数）
     */
    public static <T> List<T> selectListWithConditionAndSort(Class<T> entityClass, String condition, String sort, Object... params) {
        condition = transferCondition(entityClass, condition);
        sort = transferSort(entityClass, sort);
        String sql = SqlHelper.generateSelectSql(entityClass, condition, sort);
        return DatabaseHelper.queryEntityList(entityClass, sql, params);
    }

    /**
     * 查询数据条数
     */
    public static long selectCount(Class<?> entityClass, String condition, Object... params) {
        condition = transferCondition(entityClass, condition);
        String sql = SqlHelper.generateSelectSqlForCount(entityClass, condition);
        return DatabaseHelper.queryCount(sql, params);
    }

    /**
     * 查询多条数据，并转为列表（分页方式）
     */
    public static <T> List<T> selectListForPager(int pageNumber, int pageSize, Class<T> entityClass, String condition, String sort, Object... params) {
        condition = transferCondition(entityClass, condition);
        sort = transferCondition(entityClass, sort);
        String sql = SqlHelper.generateSelectSqlForPager(pageNumber, pageSize, entityClass, condition, sort);
        return DatabaseHelper.queryEntityList(entityClass, sql, params);
    }

    /**
     * 查询多条数据，并转为映射
     */
    public static <T> Map<Long, T> selectMap(Class<T> entityClass) {
        return selectMapWithPK(entityClass, "id", "", "");
    }

    /**
     * 查询多条数据，并转为映射（带有查询条件与查询参数）
     */
    public static <T> Map<Long, T> selectMapWithCondition(Class<T> entityClass, String condition, Object... params) {
        return selectMapWithPK(entityClass, "id", condition, "", params);
    }

    /**
     * 查询多条数据，并转为映射（带有查询条件、排序方式与查询参数）
     */
    public static <T> Map<Long, T> selectMapWithConditionAndSort(Class<T> entityClass, String condition, String sort, Object... params) {
        return selectMapWithPK(entityClass, "id", condition, sort, params);
    }

    /**
     * 查询多条数据，并转为映射（带有主键名）
     */
    @SuppressWarnings("unchecked")
    public static <PK, T> Map<PK, T> selectMapWithPK(Class<T> entityClass, String pkName, String condition, String sort, Object... params) {
        Map<PK, T> map = new HashMap<PK, T>();
        List<T> list = selectListWithConditionAndSort(entityClass, condition, sort, params);
        for (T obj : list) {
            PK pk = (PK) ObjectUtil.getFieldValue(obj, pkName);
            map.put(pk, obj);
        }
        return map;
    }

    /**
     * 根据列名查询单条数据，并转为相应类型的实体
     */
    public static <T> T selectColumn(Class<T> entityClass, String columnName, String condition, Object... params) {
        condition = transferCondition(entityClass, condition);
        String sql = SqlHelper.generateSelectSql(entityClass, condition, "");
        return DatabaseHelper.queryColumn(columnName, sql, params);
    }

    /**
     * 根据列名查询多条数据，并转为相应类型的实体列表
     */
    public static <T> List<T> selectColumnList(Class<?> entityClass, String columnName, String condition, String sort, Object... params) {
        condition = transferCondition(entityClass, condition);
        sort = transferCondition(entityClass, sort);
        String sql = SqlHelper.generateSelectSql(entityClass, condition, sort);
        return DatabaseHelper.queryColumnList(columnName, sql, params);
    }

    /**
     * 插入一条数据
     */
    public static boolean insert(Class<?> entityClass, Map<String, Object> fieldMap) {
        if (MapUtil.isEmpty(fieldMap)) {
            return true;
        }
        String sql = SqlHelper.generateInsertSql(entityClass, fieldMap.keySet());
        int rows = DatabaseHelper.update(sql, fieldMap.values().toArray());
        return rows > 0;
    }

    /**
     * 插入一个实体
     */
    public static boolean insert(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        Map<String, Object> fieldMap = ObjectUtil.getFieldMap(entity);
        return insert(entityClass, fieldMap);
    }

    /**
     * 更新相关数据
     */
    public static boolean update(Class<?> entityClass, Map<String, Object> fieldMap, String condition, Object... params) {
        if (MapUtil.isEmpty(fieldMap)) {
            return true;
        }
        condition = transferCondition(entityClass, condition);
        String sql = SqlHelper.generateUpdateSql(entityClass, fieldMap, condition);
        int rows = DatabaseHelper.update(sql, ArrayUtil.concat(fieldMap.values().toArray(), params));
        return rows > 0;
    }

    /**
     * 更新一个实体
     */
    public static boolean update(Object entity) {
        return update(entity, "id");
    }

    /**
     * 更新一个实体（带有主键名）
     */
    public static boolean update(Object entityObject, String pkName) {
        if (entityObject == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entityObject.getClass();
        Map<String, Object> fieldMap = ObjectUtil.getFieldMap(entityObject);
        String condition = pkName + " = ?";
        Object[] params = {ObjectUtil.getFieldValue(entityObject, pkName)};
        return update(entityClass, fieldMap, condition, params);
    }

    /**
     * 删除相关数据
     */
    public static boolean delete(Class<?> entityClass, String condition, Object... params) {
        condition = transferCondition(entityClass, condition);
        String sql = SqlHelper.generateDeleteSql(entityClass, condition);
        int rows = DatabaseHelper.update(sql, params);
        return rows > 0;
    }

    /**
     * 删除一个实体
     */
    public static boolean delete(Object entityObject) {
        return delete(entityObject, "id");
    }

    /**
     * 删除一个实体（可指定主键名）
     */
    public static boolean delete(Object entityObject, String pkName) {
        if (entityObject == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entityObject.getClass();
        String condition = pkName + " = ?";
        Object[] params = {ObjectUtil.getFieldValue(entityObject, pkName)};
        return delete(entityClass, condition, params);
    }

    private static String transferCondition(Class<?> entityClass, String condition) {
        if (StringUtil.isNotEmpty(condition)) {
            StringBuffer buffer = new StringBuffer();
            String regex = "([a-z_]+([a-z|A-Z|0-9|_]*)+)\\s*(=|!=|<>|>|>=|<|<=|like)\\s*";
            Matcher matcher = Pattern.compile(regex).matcher(condition.trim());
            while (matcher.find()) {
                String fieldName = matcher.group(1);
                String columnName = EntityHelper.getColumnName(entityClass, fieldName);
                String operator = matcher.group(3);
                matcher.appendReplacement(buffer, columnName);
                buffer.append(" ").append(operator).append(" ");
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        return condition;
    }

    private static String transferSort(Class<?> entityClass, String sort) {
        if (StringUtil.isNotEmpty(sort)) {
            StringBuffer buffer = new StringBuffer();
            String regex = "([a-z_]+([A-Z]+[a-z|0-9|_]+)+)";
            Matcher matcher = Pattern.compile(regex).matcher(sort.trim());
            while (matcher.find()) {
                String fieldName = matcher.group(1);
                String columnName = EntityHelper.getColumnName(entityClass, fieldName);
                matcher.appendReplacement(buffer, columnName);
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        return sort;
    }
}

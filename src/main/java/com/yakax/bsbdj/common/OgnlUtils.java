package com.yakax.bsbdj.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class OgnlUtils {

    /**
     * ognl解析字符串
     *
     * @param ognl
     * @param root
     * @return
     */
    public static String getString(String ognl, Map root) {
        try {
            return Ognl.getValue(ognl, root).toString();
        } catch (OgnlException e) {
            log.error(ExceptionUtils.getStackTrace(e) + "ognl错误");
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析的到数字 采用所有数字父类number
     *
     * @param ognl
     * @param root
     * @return
     */
    public static Number getNumber(String ognl, Map root) {
        Number number = null;
        try {
            Object value = Ognl.getValue(ognl, root);
            if (value != null) {
                if (value instanceof Number) {
                    number = (Number) value;
                } else if (value instanceof String) {
                    //"1231"处理这种
                    number = new BigDecimal((String) value);
                }
            }
        } catch (OgnlException e) {
            log.error(ExceptionUtils.getStackTrace(e) + "ognl错误Number");
        }
        return number;
    }

    /**
     * //解析真假
     *
     * @param ognl
     * @param root
     * @return
     */
    public static Boolean getBoolean(String ognl, Map root) {
        Boolean result = false;
        try {
            Object value = Ognl.getValue(ognl, root);
            if (value != null) {
                if (value instanceof Boolean) {
                    result = (Boolean) value;
                } else if (value instanceof String) {
                    //"true"处理这种
                    result = ((String) value).equalsIgnoreCase("true");
                } else if (value instanceof Number) {
                    // 1
                    result = ((Number) value).intValue() == 1;
                }
            }
        } catch (OgnlException e) {
            log.error(ExceptionUtils.getStackTrace(e) + "ognl错误Boolean");
        }
        return result;
    }

    /**
     * 解析list 里面健值对
     *
     * @param ognl
     * @param root
     * @return
     */
    public static List<Map<String, Object>> getListMap(String ognl, Map root) {
        List<Map<String, Object>> result = null;
        try {
            result = (List<Map<String, Object>>) Ognl.getValue(ognl, root);
            if (result == null)
                result = new ArrayList<>();
        } catch (OgnlException e) {
            log.error(ExceptionUtils.getStackTrace(e) + "ognl错误List<Map>");
        }
        return result;
    }

    /**
     * 解析list 里面String
     *
     * @param ognl
     * @param root
     * @return
     */
    public static List<String> getListString(String ognl, Map root) {
        List<String> result = null;
        try {
            result = (List<String>) Ognl.getValue(ognl, root);
            if (result == null)
                result = new ArrayList<>();
        } catch (OgnlException e) {
            log.error(ExceptionUtils.getStackTrace(e) + "ognl错误List<String>");
        }
        return result;
    }
}

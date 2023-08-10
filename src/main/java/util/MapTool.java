package util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapTool {

    private static Logger logger = LogManager.getLogger();
    public static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper(){

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // When serializing, the default date format is yyyy-MM-dd'T'HH:mm:ss.SSSZ.
//        objectMapper.cconfigure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false)
        // When serializing, properties with a null value are ignored.
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        return objectMapper;
    }

    private List getFiledsInfo(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        List list = new ArrayList();
        Map infoMap = null;
        for (int i = 0; i < fields.length; i++) {
            infoMap = new HashMap();
            infoMap.put("name", fields[i].getName());
            infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
            list.add(infoMap);
        }
        return list;
    }

    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<String> getDev(String title){
        Pattern p = Pattern.compile("(?<=devTypes\\()(\\w|,)+(?=\\))|(?<=prodIds\\()(\\w|,)+(?=\\))");
        List<String> list = new ArrayList<>();
        if(title==null||title.equals("")){
            return list;
        }
        Matcher matcher = p.matcher(title);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }


    public static String objectToProstr(Object o) {

        String jsonstr = objectToJsonstr(o);
        String deviceStr = null;
        try {
            Map<String, Object> deviceMap
                    = objectMapper.readValue(jsonstr, new TypeReference<Map<String, Object>>() {
            });
            List proList = getKeyValue(deviceMap);
            deviceStr = objectMapper.writeValueAsString(proList);

        } catch (JsonProcessingException e) {
            logger.error(e.toString(), e);
        }
        return deviceStr;
    }

    public static String objectToJsonstr(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.error(e.toString(), e);
        }
        return null;
    }

    public static List getKeyValue(Map<String, Object> map) {
        List list = new ArrayList();
        Map infoMap = null;
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            infoMap = new HashMap();
            infoMap.put("name", next.getKey());
            infoMap.put("value", next.getValue());
            list.add(infoMap);
        }

        return list;
    }

    /*
     * @Description  map2bean：map -> entity
     * @Warning：The property names of an object must match the keys of a map.
     * @param map
     * @param clz
     * @return
     **/
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> clz) throws Exception {
        if (null == map) {
            throw new RuntimeException();
        }
        try {
            // create an object to be converted
            T obj = clz.newInstance();
            // Get the value from the Map with the same name as the property,
                    // and set the value to the object (using the setter method)
            // Get the property descriptor
            BeanInfo b = Introspector.getBeanInfo(clz, Object.class);
            PropertyDescriptor[] pds = b.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                // property setter
                Method setter = pd.getWriteMethod();
                // Get the value from the Map with the key name that matches the property name, and set the value to the property
                setter.invoke(obj, map.get(pd.getName()));
            }
            return obj;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return null;
        }
    }

}

package io.github.vimisky.luta.applier.mysql.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LutaMySQLUtils {

    public static String BINARY_PREFIX = "__binary__";
    public static String BINARY_SUFFIX = "__yranib__";

    public static String convert(byte[] byteArray) {
//        BigInteger bigInteger = new BigInteger(1, byteArray);
//        return bigInteger.toString(16);
        return new String(Hex.encodeHex(byteArray));
    }
    private static Object columnValueSQLWrapper(Object columnValue) throws JsonProcessingException {
        if (columnValue instanceof String){
            String str = (String)columnValue;
            if(str.startsWith(BINARY_PREFIX) && str.endsWith(BINARY_SUFFIX)){
                Pattern p = Pattern.compile("^"+BINARY_PREFIX+"(?<substr>.+)"+BINARY_SUFFIX+"$");
                Matcher m = p.matcher(str);
                String subStr = null;
                if (m.find()){
                    subStr = m.group("substr");
                    String newSubStr = new String(Base64.decodeBase64(subStr));
//                    System.out.println("newSubStr length: " + newSubStr.length());
                    return "UNHEX('" + convert(Base64.decodeBase64(subStr)) + "')";
                }else {
                    //永远不会有这个else
                    return null;
                }

            }else {
                return new ObjectMapper().writeValueAsString(columnValue) ;
            }

        }else {
            return columnValue;
        }
    }
    public static String buildDeleteSql(String schemaName, String tableName, List<String> whereColumnNames, List<Object> whereColumnValues) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        if (whereColumnNames.size()>0){
            sb.append("delete from `").append(schemaName).append("`.`").append(tableName).append("`");

            sb.append(" where ");
            Object firstWhereColumnName = whereColumnNames.get(0);
            Object firstWhereColumnValue = whereColumnValues.get(0);

            sb.append("`").append(firstWhereColumnName).append("`");
            if (firstWhereColumnValue==null){
                sb.append(" is ").append(columnValueSQLWrapper(firstWhereColumnValue));
            }else{
                sb.append("=").append(columnValueSQLWrapper(firstWhereColumnValue));
            }

            for (int i = 1; i< whereColumnNames.size(); i++){
                sb.append(" and `").append(whereColumnNames.get(i)).append("`");
                if (whereColumnValues.get(i)==null){
                    sb.append(" is ").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                }else{
                    sb.append("=").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                }
            }
            sb.append(" ;\n");
        }
        return sb.toString();
    }
    public static String buildInsertSql(String schemaName, String tableName, List<String> columnNames, List<Object> columnValues) throws JsonProcessingException {

        StringBuilder sb = new StringBuilder();

        if (columnNames.size()>0){
            sb.append("insert into `").append(schemaName).append("`.`").append(tableName).append("` ( ");

            Object firstColumnName = columnNames.get(0);
            sb.append("`").append(firstColumnName).append("`");

            for (int i = 1; i < columnNames.size(); i++){
                sb.append(",`").append(columnNames.get(i)).append("`");
            }

            sb.append(" ) values ( ");

            Object firstColumnValue = columnValues.get(0);
            sb.append(columnValueSQLWrapper(firstColumnValue));
//            if (firstColumnValue instanceof String){
//                sb.append("'").append(firstColumnValue).append("'");
//            }else {
//                sb.append(firstColumnValue);
//            }

            for (int i = 1; i< columnValues.size(); i++){
                sb.append(" , ").append(columnValueSQLWrapper(columnValues.get(i)));
//                Object columnValue = ;
//                if (columnValue instanceof String){
//                    sb.append(", '").append(columnValue).append("'");
//                }else {
//                    sb.append(", ").append(columnValue);
//                }
            }
            sb.append(" ) ;\n");
        }

        return sb.toString();
    }
    public static String buildUpdateSql(String schemaName, String tableName, List<String> columnNames, List<Object> columnValues, List<String> whereColumnNames, List<Object> whereColumnValues) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();

        if (columnValues.size()>0){
            sb.append("update `").append(schemaName).append("`.`").append(tableName).append("` set ");

            Object firstColumnName = columnNames.get(0);
            Object firstColumnValue = columnValues.get(0);

            sb.append("`").append(firstColumnName).append("`");
            sb.append("=").append(columnValueSQLWrapper(firstColumnValue));
//            if (firstColumnValue instanceof String){
//                sb.append("'").append(firstColumnValue).append("'");
//            }else {
//                sb.append(firstColumnValue);
//            }

            for (int i = 1; i < columnNames.size() ; i++){

                sb.append(", `").append(columnNames.get(i)).append("`");
                sb.append("=").append(columnValueSQLWrapper(columnValues.get(i)));
//                Object columnValue = columnValues.get(i);
//                if (columnValue instanceof String){
//                    sb.append("'").append(columnValues.get(i)).append("'").append(", `");
//                }else {
//                    sb.append(columnValues.get(i)).append(", `");
//                }

            }

            if (whereColumnNames.size()>0){

                sb.append(" where ");
                Object firstWhereColumnName = whereColumnNames.get(0);
                Object firstWhereColumnValue = whereColumnValues.get(0);

                sb.append("`").append(firstWhereColumnName).append("`");
                if (firstWhereColumnName==null){
                    sb.append(" is ").append(columnValueSQLWrapper(firstWhereColumnValue));
                }else{
                    sb.append("=").append(columnValueSQLWrapper(firstWhereColumnValue));
                }

                for (int i = 1; i< whereColumnNames.size(); i++){
                    sb.append(" and `").append(whereColumnNames.get(i)).append("`");
                    if (whereColumnValues.get(i)==null){
                        sb.append(" is ").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                    }else{
                        sb.append("=").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                    }
                }
            }

            sb.append(";\n");
        }

        return sb.toString();
    }

}

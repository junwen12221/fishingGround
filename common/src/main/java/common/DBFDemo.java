package common;

import com.alibaba.druid.pool.DruidDataSource;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Objects;


public class DBFDemo {


    public static void main(String[] args) throws Exception {
        String path = "";
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("admin");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/xxxxxx?Unicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT&userSSL=false");
        //请设置数据库编码
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(1);
        //dataSource.setMaxActive(10); // 启用监控统计功能  dataSource.setFilters("stat");// for mysql  dataSource.setPoolPreparedStatements(false);
        //dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(false);
        dataSource.getConnection().close();//先创建缓存对象并缓存起来
        LocalDateTime first = LocalDateTime.now();//开始计时
        Connection con = dataSource.getConnection(); //链接本地MYSQL
        con.setAutoCommit(false); // 设置手动提交
        con.getTransactionIsolation();//是否开启事务
        String insert_sql = "INSERT INTO score2 (emp_code,emp_name,salary,emp_name1,emp_name2) VALUES (?,?,?,?,?)";
        try (PreparedStatement psts = con.prepareStatement(insert_sql)) {//需要关闭
            try (FileInputStream fileInputStream = new FileInputStream(path); BufferedInputStream bf = new BufferedInputStream(fileInputStream)) {
                // 读取文件的输入流
                // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息
                DBFReader reader = new DBFReader(bf);
                // 设置字符集，避免中文乱码
                //reader.setCharactersetName("UTF-8");
                // 一条条取出path文件中记录
                while (true) {
                    Object[] rowValues = reader.nextRecord();   //下标为0开始
                    if (rowValues == null) {
                        break;
                    }
                    psts.setString(1, Objects.toString(rowValues[0]).trim());
                    psts.setString(2, Objects.toString(rowValues[1]).trim());
                    psts.setString(3, Objects.toString(rowValues[2]).trim());
                    psts.setString(4, Objects.toString(rowValues[3]).trim());
                    psts.setString(5, Objects.toString(rowValues[4]).trim());
                    psts.addBatch();
                }
            }
            // 执行批处理
            psts.executeBatch();
            con.commit();  // 提交
        }
        LocalDateTime second = LocalDateTime.now();
        System.out.println(" \n ======>" + second.toString() + "  " + first.toString());
    }

    public static void _main(String args[])
            throws IOException {

        // let us create field definitions first
        // we will go for 3 fields
        //
        DBFField fields[] = new DBFField[5];

        fields[0] = new DBFField();
        fields[0].setName("emp_code");
        fields[0].setDataType(DBFField.FIELD_TYPE_C);
        fields[0].setFieldLength(10);

        fields[1] = new DBFField();
        fields[1].setName("emp_name");
        fields[1].setDataType(DBFField.FIELD_TYPE_C);
        fields[1].setFieldLength(20);

        fields[2] = new DBFField();
        fields[2].setName("salary");
        fields[2].setDataType(DBFField.FIELD_TYPE_N);
        fields[2].setFieldLength(12);
        fields[2].setDecimalCount(2);

        fields[3] = new DBFField();
        fields[3].setName("emp_name1");
        fields[3].setDataType(DBFField.FIELD_TYPE_C);
        fields[3].setFieldLength(20);

        fields[4] = new DBFField();
        fields[4].setName("emp_name2");
        fields[4].setDataType(DBFField.FIELD_TYPE_C);
        fields[4].setFieldLength(20);

        DBFWriter writer = new DBFWriter();
        writer.setFields(fields);

        // now populate DBFWriter
        //

        for (int i = 0; i < 6000; ++i) {
            Object rowData[] = new Object[5];
            rowData[0] = "1000";
            rowData[1] = "John";
            rowData[2] = new Double(5000.00);
            rowData[3] = "Lalit";
            rowData[4] = "Lalit";

            writer.addRecord(rowData);


            rowData[0] = "1001";
            rowData[1] = "Lalit";
            rowData[2] = new Double(3400.00);
            rowData[3] = "Lalit";
            rowData[4] = "Lalit";

            writer.addRecord(rowData);

            rowData = new Object[5];
            rowData[0] = "1002";
            rowData[1] = "Rohit";
            rowData[2] = new Double(7350.00);
            rowData[3] = "Lalit";
            rowData[4] = "Lalit";

            writer.addRecord(rowData);
        }


        FileOutputStream fos = new FileOutputStream("");
        writer.write(fos);
        fos.close();
    }

}


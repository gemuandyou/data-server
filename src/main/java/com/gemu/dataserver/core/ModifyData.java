package com.gemu.dataserver.core;

import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.tool.ByteAndHexTool;
import com.gemu.dataserver.tool.SerializableTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 修改或删除数据
 * <p/>
 * Created by gemu on 13/06/2017.
 */
@Repository
public class ModifyData {

    @Value("${dataserver.datapath}")
    private String dbPath;
    @Value("${dataserver.singleFileEntityCount}")
    private Integer singleFileEntityCount;
    @Autowired
    ReadData readData;
    @Autowired
    WriteData writeData;

    /**
     * 更新部分字段
     * @param source
     * @param entityName
     * @param id
     * @param fieldMap
     * @param <T>
     * @return 更新是否成功
     */
    public <T extends BaseData> boolean update(String source, String entityName, String id, Class<T> tClass, Map<String, Object> fieldMap) throws NoSuchFieldException, EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        fieldMap.remove("id");
        T t = readData.get(id, source, entityName);
        if (t == null) return false;
        // 赋值新字段
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            Field fields = tClass.getDeclaredField(entry.getKey());
            fields.setAccessible(true);
            try {
                fields.set(t, entry.getValue());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 保存更新后的对象
        String entityPath = dbPath +
                (dbPath.endsWith(File.separator) ? "" : File.separator) +
                source + File.separator + entityName;
        Map<String, Integer> entityPosition = readData.getEntityPosition(entityPath, "id", id);
        if (entityPosition.size() <= 0) return false;
        return modify(entityPath, entityPosition.get("fileNum"), entityPosition.get("lineNum"), t);
    }

    /**
     * 全量更新
     * @param source
     * @param entityName
     * @param t
     * @param <T>
     * @return 更新是否成功
     */
    public <T extends BaseData> boolean update(String source, String entityName, T t) {
        // 保存更新后的对象
        String entityPath = dbPath +
                (dbPath.endsWith(File.separator) ? "" : File.separator) +
                source + File.separator + entityName;
        Map<String, Integer> entityPosition = readData.getEntityPosition(entityPath, "id", t.getId());
        if (entityPosition.size() <= 0) return false;
        return modify(entityPath, entityPosition.get("fileNum"), entityPosition.get("lineNum"), t);
    }

    /**
     * 修改实体对象文件
     * @param entityPath    实体对象存储的路径
     * @param fileNum       文件序号
     * @param lineNum       实体对象所在行
     * @param <T>           实体类型
     * @return
     */
    private <T extends BaseData> boolean modify(String entityPath, Integer fileNum, Integer lineNum, T t) {
        byte isFinished = 0;
        File file = new File(entityPath + File.separator + fileNum);
        File tmp = new File(entityPath + File.separator + fileNum + ".temporary");
        if (!file.exists()) return false;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new LineNumberReader(new FileReader(file));
            int line = 1;
            String buff = "";
            bw = new BufferedWriter(new FileWriter(tmp));
            // 暂存被修改对象之前的记录
            while ((buff = br.readLine()) != null) {
                if (line >= lineNum) break;
                line++;
                bw.append(buff + "\n");
                if (line % 100 == 0) {
                    bw.flush();
                }
            }
            bw.append(writeData.convertLinefeedToPlaceHolder(ByteAndHexTool.bytesToHexString(SerializableTool.serialization(t))) + "\n");
            // 暂存被修改对象之后的记录
            while ((buff = br.readLine()) != null) {
                line++;
                bw.append(buff + "\n");
                if (line % 100 == 0) {
                    bw.flush();
                }
            }
            isFinished = 1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.gc();
            }
        }
        if (isFinished == 1 && tmp.exists()) {
            for (int i = 0; i < 100; i++) {
                boolean delete = file.delete();
                if (delete) {
                    boolean renameSuccess = tmp.renameTo(file);
                    if (renameSuccess) {
                        return true;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}

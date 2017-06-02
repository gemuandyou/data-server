package com.gemu.dataserver.core;


import com.gemu.dataserver.annotation.NeedIndex;
import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.auxiliary.EntityStatus;
import com.gemu.dataserver.tool.ByteAndHexTool;
import com.gemu.dataserver.tool.SerializableTool;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 写入数据
 *
 * Created by gemu on 29/05/2017.
 */
@Repository
public class WriteData {

    @Value("${dataserver.datapath}")
    private String dbPath;
    @Value("${dataserver.singleFileEntityCount}")
    private Integer singleFileEntityCount;

    /**
     * 写入数据
     *
     * @param source 实体对象来源
     * @param entityName 实体对象名
     * @param t 实体对象
     * @param <T>
     * @return 写入后的实体对象的ID 为空表示写入失败
     */
    public <T extends BaseData> String write(String source, String entityName, T t) {
        synchronized (t.getClass()) {
            singleFileEntityCount = singleFileEntityCount == null ? 500 : singleFileEntityCount;

            FileWriter writer = null; // 数据写入
            FileWriter statusWriter = null; // 数据状态写入
            String entityPath = ""; // 实体对象存储路径
            String entityOffset = ""; // 实体对象所在位置定位。格式：文件名-所在行数
            try {
                entityPath = dbPath +
                        (dbPath.endsWith(File.separator) ? "" : File.separator) +
                        source + File.separator;
                File dir = new File(entityPath + entityName + File.separator);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 根据 存储状态文件 判断当前存储对象是否需要创建文件，当单个文件满足500行的时候就需要另外创建存储文件
                EntityStatus entityStatus = checkEntityStatus(entityPath, entityName + ".sta");
                if (entityStatus.getTotalEntityCount() % singleFileEntityCount == 0) { // 以满足500，需要创建存储文件
                    t.setId(entityStatus.getTotalEntityCount() + "0.0" + Thread.currentThread().getId());
                    writer = new FileWriter(entityPath + entityName + File.separator + (entityStatus.getMaxFileNum() + 1));
                    writer.append(convertLinefeedToPlaceHolder(ByteAndHexTool.bytesToHexString(SerializableTool.serialization(t))) + "\n");
                    entityStatus.setMaxFileNum(entityStatus.getMaxFileNum() + 1);
                    entityOffset = (entityStatus.getMaxFileNum() + 1) + "-" + 1;
                } else { // 在原来的存储文件上追加
                    t.setId(entityStatus.getTotalEntityCount() + "0.0" + Thread.currentThread().getId());
                    writer = new FileWriter(entityPath + entityName + File.separator + (entityStatus.getMaxFileNum()), true);
                    writer.append(convertLinefeedToPlaceHolder(ByteAndHexTool.bytesToHexString(SerializableTool.serialization(t))) + "\n");
                    entityOffset = (entityStatus.getMaxFileNum()) + "-" + (entityStatus.getTotalEntityCount() + 1) % singleFileEntityCount;
                }

                // 修改实体状态
                statusWriter = new FileWriter(entityPath + entityName + ".sta");
                Gson gson = new Gson();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("totalEntityCount", entityStatus.getTotalEntityCount() + 1);
                jsonObject.addProperty("maxFileNum", entityStatus.getMaxFileNum());
                String json = gson.toJson(jsonObject);
                statusWriter.write(json);

                // 创建实体索引，用于条件查询
                Class<? extends BaseData> clazz = t.getClass();
                Field[] fields = clazz.getDeclaredFields();
                Date now = new Date();
                FileWriter indexWriter = null; // 指针写入
                for (Field field : fields) {
                    if (field.getAnnotation(NeedIndex.class) == null) {
                        continue;
                    }
                    String idxDir = entityPath + entityName + File.separator + "index" + File.separator + field.getName();
                    dir = new File(idxDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    field.setAccessible(true);
                    try {
                        indexWriter = new FileWriter(idxDir + File.separator + new SimpleDateFormat("yyyyMMdd").format(now) + ".query"); // 文件格式：fieldValue=|=10-1#fieldValue -|- 10-1
                        indexWriter.append((field.get(t) == null ? "" : field.get(t).toString().replaceAll("\n", "")) + "=|=" + entityOffset + "#");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (indexWriter != null) indexWriter.close();
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) writer.close();
                    if (statusWriter != null) statusWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * 检查实体对象的状态，并返回
     * @param entityStatusPath 实体对象状态文件路径
     * @param entityStatusName 实体对象状态文件名
     * @return 实体状态
     */
    private EntityStatus checkEntityStatus(String entityStatusPath, String entityStatusName) {
        EntityStatus entityStatus = new EntityStatus();
        entityStatus.loadStatus(entityStatusPath, entityStatusName);
        return entityStatus;
    }

    /**
     * 将实体对象的字段内容中的换行占位符替换为换行。
     * 目的是为了保证一个对象只存一行。
     *
     * @param string
     * @param
     */
    private String convertLinefeedToPlaceHolder(String string) {
        return string.replaceAll("\\n", "`linenew`");
    }


}

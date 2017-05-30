package com.gemu.dataserver.core;


import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.entity.auxiliary.EntityStatus;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.tool.ByteAndHexTool;
import com.gemu.dataserver.tool.SerializableTool;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取数据
 *
 * Created by gemu on 29/05/2017.
 */
@Repository
public class ReadData {

    @Value("${dataserver.datapath}")
    private String dbPath;
    @Value("${dataserver.singleFileEntityCount}")
    private Integer singleFileEntityCount;

    /**
     * 分页获取实体对象列表
     *
     * @param pageNo 页码
     * @param source 实体对象来源
     * @param entityName 实体对象名
     * @param <T>
     * @return
     */
    public <T extends BaseData> EntityPage<T> read(int pageNo, String source, String entityName)
            throws SourceNotFoundException, EntityNotFoundException, DataAssetsNotFoundException {
        singleFileEntityCount = singleFileEntityCount == null ? 500 : singleFileEntityCount;

        if (source == null || "".equals(source)) throw new SourceNotFoundException("实体来源不存在");
        if (entityName == null || "".equals(entityName)) throw new EntityNotFoundException("实体不存在");
        String entityPath = dbPath +
                (dbPath.endsWith(File.separator) ? "" : File.separator) +
                source + File.separator + entityName;
        File dir = new File(entityPath + File.separator);
        if (!dir.exists()) {
            throw new DataAssetsNotFoundException("数据资源地址错误");
        }
        EntityPage entityPage = new EntityPage();

        // 获取实体分页信息
        EntityStatus entityStatus = null;
        File entityStatusFile = new File(entityPath + ".sta");
        if (!entityStatusFile.exists()) {
            entityPage.setPageNo(pageNo);
            entityPage.setTotalCount(0);
            entityPage.setEntries(new ArrayList());
            return entityPage;
        } else {
            FileReader reader = null;
            try {
                reader = new FileReader(entityStatusFile);
                Gson gson = new Gson();
                entityStatus = gson.fromJson(reader, EntityStatus.class);
                entityStatus = entityStatus == null ? new EntityStatus() : entityStatus;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        entityPage.setPageNo(pageNo);
        entityPage.setTotalCount(entityStatus.getTotalEntityCount());

        // 获取实体对象分页数据
        int startIndex = (pageNo - 1) * EntityPage.pageSize + 1; // 数据起始查询位置
        int fileIndex = startIndex / singleFileEntityCount + 1; // 数据存储文件序号索引
        int singleStartIndex = startIndex % singleFileEntityCount; // 单个文件内数据起始位置
        if (singleStartIndex + EntityPage.pageSize <= singleFileEntityCount) { // 数据存储在一个文件中
            File entityFile = new File(entityPath + File.separator + fileIndex);
            if (!entityFile.exists()) { // 存储文件不存在
                entityPage.setEntries(new ArrayList());
                return entityPage;
            }
            List<T> ts = getEntities(entityFile, singleStartIndex, singleStartIndex + EntityPage.pageSize);
            entityPage.setEntries(ts);
        } else { // 有部分数据在下一个文件中
            // 上半部分
            File entityFile1 = new File(entityPath + File.separator + fileIndex);
            if (!entityFile1.exists()) { // 存储文件不存在
                entityPage.setEntries(new ArrayList());
                return entityPage;
            }
            List<T> ts = new ArrayList<T>();
            List<T> tsP1 = getEntities(entityFile1, singleStartIndex, singleFileEntityCount);
            ts.addAll(tsP1);

            // 下半部分
            File entityFile2 = new File(entityPath + File.separator + fileIndex + 1);
            if (entityFile2.exists()) { // 存储文件存在
                List<T> tsP2 = getEntities(entityFile2, 1,
                        EntityPage.pageSize - (singleFileEntityCount - singleStartIndex));
                ts.addAll(tsP2);
            }
            entityPage.setEntries(ts);
        }
        return entityPage;
    }

    /**
     * 获取实体对象列表
     *
     * @param entityFile 实体对象存储文件
     * @param singleStartIndex 文件起始位置
     * @param singleEndIndex 文件结束位置
     * @param <T>
     * @return
     */
    public <T extends BaseData> List<T> getEntities(File entityFile, int singleStartIndex, int singleEndIndex) {
        FileReader reader = null;
        List<T> ts = new ArrayList<T>();
        try {
            reader = new FileReader(entityFile);
            BufferedReader br = new BufferedReader(reader);
            int rowNum = 1;
            String entity = null;
            while ((entity = br.readLine()) != null) {
                if (rowNum < singleStartIndex) {
                    rowNum++;
                    continue;
                }
                if (rowNum > singleEndIndex) {
                    break;
                }
                T t = (T) SerializableTool.deserialization(ByteAndHexTool.hexStringToBytes(convertPlaceHolderToLinefeed(entity)));
                ts.add(t);
                rowNum++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ts;
    }

    /**
     * 将实体对象的字段内容中的换行替换为特殊占位符。
     *
     * @param string
     * @param
     */
    private String convertPlaceHolderToLinefeed(String string) {
        return string.replaceAll("`linenew`", "\n");
    }

}

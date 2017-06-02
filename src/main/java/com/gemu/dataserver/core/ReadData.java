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
import java.util.Map;

/**
 * 读取数据
 * <p/>
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
     * @param pageNo     页码
     * @param source     实体对象来源
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
        EntityStatus entityStatus = getEntityStatus(entityPath);
        entityPage.setPageNo(pageNo);
        if (entityStatus == null) {
            entityPage.setTotalCount(0);
            entityPage.setEntries(new ArrayList());
            return entityPage;
        }
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
     * 根据条件分页获取实体对象列表
     *
     * @param pageNo
     * @param source
     * @param entityName
     * @param filters    条件集合<br>
     *                   <p>由 字段名 和 字段过滤条件 组成。
     *                   字段过滤条件仅有三种形式。
     *                   <ol>
     *                   <li>等于。格式：string</li>
     *                   <li>不等于。格式：!string</li>
     *                   <li>模糊。格式：%string%</li>
     *                   </pl></p>
     * @param <T>
     * @return
     * @throws EntityNotFoundException
     * @throws DataAssetsNotFoundException
     * @throws SourceNotFoundException
     */
    public <T extends BaseData> EntityPage<T> filterRead(int pageNo, String source, String entityName, Map<String, String> filters)
            throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        if (filters.size() == 0)
            return read(pageNo, source, entityName);
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
        EntityStatus entityStatus = getEntityStatus(entityPath);
        entityPage.setPageNo(pageNo);
        if (entityStatus == null) {
            entityPage.setTotalCount(0);
            entityPage.setEntries(new ArrayList());
            return entityPage;
        }
        entityPage.setTotalCount(entityStatus.getTotalEntityCount());

//        int startIndex = (pageNo - 1) * EntityPage.pageSize + 1; // 数据起始查询位置
//        int fileIndex = startIndex / singleFileEntityCount + 1; // 数据存储文件序号索引
//        int singleStartIndex = startIndex % singleFileEntityCount; // 单个文件内数据起始位置
//        if (singleStartIndex + EntityPage.pageSize <= singleFileEntityCount) { // 数据存储在一个文件中
//            File entityFile = new File(entityPath + File.separator + fileIndex);
//            if (!entityFile.exists()) { // 存储文件不存在
//                entityPage.setEntries(new ArrayList());
//                return entityPage;
//            }
//            List<T> ts = getEntities(entityFile, singleStartIndex, singleStartIndex + EntityPage.pageSize);
//            entityPage.setEntries(ts);
//        } else { // 有部分数据在下一个文件中
//            // 上半部分
//            File entityFile1 = new File(entityPath + File.separator + fileIndex);
//            if (!entityFile1.exists()) { // 存储文件不存在
//                entityPage.setEntries(new ArrayList());
//                return entityPage;
//            }
//            List<T> ts = new ArrayList<T>();
//            List<T> tsP1 = getEntities(entityFile1, singleStartIndex, singleFileEntityCount);
//            ts.addAll(tsP1);
//
//            // 下半部分
//            File entityFile2 = new File(entityPath + File.separator + fileIndex + 1);
//            if (entityFile2.exists()) { // 存储文件存在
//                List<T> tsP2 = getEntities(entityFile2, 1,
//                        EntityPage.pageSize - (singleFileEntityCount - singleStartIndex));
//                ts.addAll(tsP2);
//            }
//            entityPage.setEntries(ts);
//        }
        // 获取实体对象分页数据
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            List<Map<String, Integer>> positions = getEntityPosition(entry.getKey(), entry.getValue());
            for (Map<String, Integer> position : positions) {
                T t = accessEntityFile(entityPath, position.get("fileNum"), position.get("lineNum"));
            }
        }
        return null;
    }

    /**
     * 根据文件序号和实体所在行获取实体对象
     *
     * @param <T> 实体类型
     * @param entityPath 实体对象存储的路径
     *@param fileNum 文件序号
     * @param lineNum 实体对象所在行   @return 实体对象
     */
    private <T extends BaseData> T accessEntityFile(String entityPath, Integer fileNum, Integer lineNum) {
        RandomAccessFile randomAccessFile = new RandomAccessFile(entityPath + File.separator + fileNum, mode);
        return null;
    }

    /**
     * 根据待过滤的字段名和值获取得到实体对象的位置信息
     * @param fieldName 字段名
     * @param filterValue 进行筛选的值
     * @return 实体对象的定位集合。格式：[{"fileNum":1,"lineNum":1}]
     */
    private List<Map<String,Integer>> getEntityPosition(String fieldName, String filterValue) {
        // TODO fieldValue=|=10-1#fieldValue -|- 10-1
        return null;
    }

    /**
     * 获取实体对象列表
     *
     * @param entityFile       实体对象存储文件
     * @param singleStartIndex 文件起始位置
     * @param singleEndIndex   文件结束位置
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
     * 获取实体对象状态信息
     *
     * @param entityPath 实体对象路径
     * @return
     */
    private EntityStatus getEntityStatus(String entityPath) {

        // 获取实体分页信息
        EntityStatus entityStatus = null;
        File entityStatusFile = new File(entityPath + ".sta");
        if (!entityStatusFile.exists()) {
            return null;
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
            return entityStatus;
        }
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

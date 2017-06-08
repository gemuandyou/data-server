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
import java.text.SimpleDateFormat;
import java.util.*;

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
     * 根据ID获取实体对象
     * @param id 实体对象ID
     * @param source 实体对象来源
     * @param entityName 实体对象名
     * @param <T>
     * @return
     */
    public <T extends BaseData> T get(String id, String source, String entityName)
            throws SourceNotFoundException, EntityNotFoundException, DataAssetsNotFoundException {
        if (source == null || "".equals(source)) throw new SourceNotFoundException("实体来源不存在");
        if (entityName == null || "".equals(entityName)) throw new EntityNotFoundException("实体不存在");

        String entityPath = dbPath +
                (dbPath.endsWith(File.separator) ? "" : File.separator) +
                source + File.separator + entityName;

        Map<String, Integer> posMap = getEntityPoistion(entityPath, "id", id);
        T t = accessEntityFile(entityPath, posMap.get("fileNum"), posMap.get("lineNum"));
        return t;
    }

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

        int startIndex = entityStatus.getTotalEntityCount() - (pageNo - 1) * EntityPage.pageSize; // 数据起始查询位置
        int fileIndex = startIndex / singleFileEntityCount + (startIndex % singleFileEntityCount == 0 ? 0 : 1); // 数据存储文件序号索引
        fileIndex = fileIndex == 0 ? 1 : fileIndex;
        int singleStartIndex = startIndex % singleFileEntityCount; // 单个文件内数据起始位置
        if (singleStartIndex == 0 || singleStartIndex >= EntityPage.pageSize) { // 数据存储在一个文件中
            File entityFile = new File(entityPath + File.separator + fileIndex);
            if (!entityFile.exists()) { // 存储文件不存在
                entityPage.setEntries(new ArrayList());
                return entityPage;
            }
            List<T> ts = getEntities(entityFile, singleStartIndex, singleStartIndex - EntityPage.pageSize);
            entityPage.setEntries(ts);
        } else { // 查询的数据在另个存储文件中
            // 上半部分
            File entityFile1 = new File(entityPath + File.separator + fileIndex);
            if (!entityFile1.exists()) { // 存储文件不存在
                entityPage.setEntries(new ArrayList());
                return entityPage;
            }
            List<T> ts = new ArrayList<T>();
            List<T> tsP1 = getEntities(entityFile1, singleStartIndex, 0);
            ts.addAll(tsP1);

            // 下半部分
            File entityFile2 = new File(entityPath + File.separator + (fileIndex - 1));
            if (entityFile2.exists()) { // 存储文件存在
                List<T> tsP2 = getEntities(entityFile2, singleFileEntityCount,
                        EntityPage.pageSize - singleStartIndex);
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
    public <T extends BaseData> EntityPage<T> filterRead(int pageNo, String source, String entityName,
                                                         Map<String, String> filters)
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

        // 获取实体对象分页数据
        List<T> ts = new ArrayList<T>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            List<Map<String, Integer>> positions = getEntityPosition(pageNo, entityPath, entry.getKey(), entry.getValue());
            for (Map<String, Integer> position : positions) {
                T t = accessEntityFile(entityPath, position.get("fileNum"), position.get("lineNum"));
                if (t != null) {
                    ts.add(t);
                }
            }
        }
        entityPage.setEntries(ts);
        return entityPage;
    }

    /**
     * 根据文件序号和实体所在行获取实体对象
     *
     * @param <T>        实体类型
     * @param entityPath 实体对象存储的路径
     * @param fileNum    文件序号
     * @param lineNum    实体对象所在行   @return 实体对象
     */
    private <T extends BaseData> T accessEntityFile(String entityPath, Integer fileNum, Integer lineNum) {
        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(
                    new File(entityPath + File.separator + fileNum)));
            T t = null;
            while (true) {
                String entity = lineNumberReader.readLine();
                if (lineNumberReader.getLineNumber() == lineNum) {
                    t = (T) SerializableTool.deserialization(ByteAndHexTool.hexStringToBytes(convertPlaceHolderToLinefeed(entity)));
                    break;
                }
            }
            return t;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据待过滤的字段名和值获取得到实体对象的位置信息
     *
     * @param pageNo
     * @param entityPath
     * @param fieldName   字段名
     * @param filterValue 进行筛选的值
     *                    字段筛选条件仅有三种形式。
     *                    <ol>
     *                    <li>等于。格式：string</li>
     *                    <li>不等于。格式：!string</li>
     *                    <li>模糊。格式：%string%</li>
     *                    </pl></p>
     * @return 实体对象的定位集合。格式：[{"fileNum":1,"lineNum":1}]
     */
    private List<Map<String, Integer>> getEntityPosition(int pageNo, String entityPath, String fieldName, String filterValue) {
        List<Map<String, Integer>> positions = new ArrayList<Map<String, Integer>>();
        int startIndex = (pageNo - 1) * EntityPage.pageSize;
        int index = 0;

        int filterForm = 1; // 1：等于；2：不等于；3：模糊
        if (filterValue.startsWith("!")) {
            filterForm = 2;
            filterValue = filterValue.substring(1);
        }
        if (filterValue.startsWith("%") && filterValue.endsWith("%")) {
            filterForm = 3;
            filterValue = filterValue.substring(1, filterValue.length() - 1);
        }

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        File file = null;
        RandomAccessFile reader = null;
        while (true) {
            file = new File(entityPath + File.separator + "index" + File.separator + fieldName + File.separator +
                    sdf.format(now) + ".query");
            now = new Date(now.getTime() - 24 * 3600000);
            if (!file.exists()) {
                break;
            }
            try {
                reader = new RandomAccessFile(file, "r");
                reader.seek(reader.length() - 1);
                while (reader.getFilePointer() != 0L) {
                    char c = (char) reader.read(); // `字段内容`=|=实体对象所在文件序号-实体对象所在行数#`字段内容`=|=#实体对象所在文件序号-实体对象所在行数
                    reader.seek(reader.getFilePointer() - 2);
                    if (c == '\0') {
                        continue;
                    }
                    if (c == '#') {
                        StringBuffer groupInfo = parsePositionByIndex(reader);

                        String info = groupInfo.toString();
                        if (info != null && !"".equals(info)) {
                            String[] split = info.split("=\\|=");
                            if (split.length == 2) {
                                String value = split[0];
                                value = value.substring(value.startsWith("`") ? 1 : 0, value.length() - 1);
                                switch (filterForm) {
                                    case 1:
                                        if (!value.equals(filterValue))
                                            continue;
                                        break;
                                    case 2:
                                        if (value.equals(filterValue))
                                            continue;
                                        break;
                                    case 3:
                                        if (value.indexOf(filterValue) == -1) {
                                            continue;
                                        }
                                        break;
                                }
                                index++;
                                if (index < startIndex) {
                                    continue;
                                }
                                if (index > startIndex + EntityPage.pageSize) {
                                    break;
                                }
                                String position = split[1];
                                Map<String, Integer> posMap = new HashMap<String, Integer>();
                                String[] positionInfo = position.split("-");
                                posMap.put("fileNum", Integer.parseInt(positionInfo[0]));
                                posMap.put("lineNum", Integer.parseInt(positionInfo[1]));
                                positions.add(posMap);
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (index > startIndex + EntityPage.pageSize) {
                break;
            }
        }
        return positions;
    }

    /**
     * 根据索引字段名和值获取实体位置信息
     * @param entityPath
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public Map<String, Integer> getEntityPoistion(String entityPath, String fieldName, String fieldValue) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        File file = null;
        RandomAccessFile reader = null;

        Map<String, Integer> posMap = new HashMap<String, Integer>();
        File indexDir = new File(entityPath + File.separator + "index" + File.separator + fieldName);
        int fileCount = indexDir.list().length;

        while (true) {
            if (fileCount <= 0) break;
            file = new File(entityPath + File.separator + "index" + File.separator + fieldName + File.separator +
                    sdf.format(now) + ".query");
            now = new Date(now.getTime() - 24 * 3600000);
            if (!file.exists()) {
                continue;
            }
            fileCount--;
            try {
                reader = new RandomAccessFile(file, "r");
                reader.seek(reader.length() - 1);
                while (reader.getFilePointer() != 0L) {
                    char c = (char) reader.read(); // `字段内容`=|=实体对象所在文件序号-实体对象所在行数#`字段内容`=|=#实体对象所在文件序号-实体对象所在行数
                    reader.seek(reader.getFilePointer() - 2);
                    if (c == '\0') {
                        continue;
                    }
                    if (c == '#') {
                        StringBuffer groupInfo = parsePositionByIndex(reader);

                        String info = groupInfo.toString();
                        if (info != null && !"".equals(info)) {
                            String[] split = info.split("=\\|=");
                            if (split.length == 2) {
                                String value = split[0];
                                value = value.substring(value.startsWith("`") ? 1 : 0, value.length() - 1);
                                if (value.equals(fieldValue)) {
                                    String position = split[1];
                                    String[] positionInfo = position.split("-");
                                    posMap.put("fileNum", Integer.parseInt(positionInfo[0]));
                                    posMap.put("lineNum", Integer.parseInt(positionInfo[1]));
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (posMap.size() != 0)
                break;
        }
        return posMap;
    }

    /**
     * 通过索引解析出实体位置信息
     * @param reader
     * @return
     * @throws IOException
     */
    private StringBuffer parsePositionByIndex(RandomAccessFile reader) throws IOException {
        StringBuffer groupInfo = new StringBuffer();
        while (true) {
            char c1 = (char) reader.read();
            if (c1 == '#' || reader.getFilePointer() == 1L) {
                reader.seek(reader.getFilePointer() - 1);
                break;
            }
            groupInfo.insert(0, c1);
            reader.seek(reader.getFilePointer() - (reader.getFilePointer() > 1 ? 2 : 1));
        }
        return groupInfo;
    }

    /**
     * 获取实体对象列表<br>这里读取文件的顺序是倒叙的
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
        for (int i = 0; i < singleStartIndex - singleEndIndex; i++) {
            ts.add(null);
        }
        try {
            reader = new FileReader(entityFile);
            LineNumberReader br = new LineNumberReader(reader);
            while (true) {
                if (br.getLineNumber() < singleEndIndex) {
                    br.readLine();
                    continue;
                } else if (br.getLineNumber() >= singleStartIndex) {
                    break;
                } else {
                    String entity = br.readLine();
                    if (entity != null && !"".equals(entity)) {
                        T t = (T) SerializableTool.deserialization(ByteAndHexTool.hexStringToBytes(convertPlaceHolderToLinefeed(entity)));
                        ts.set(singleStartIndex - br.getLineNumber(), t);
                    } else {
                        break;
                    }
                }
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

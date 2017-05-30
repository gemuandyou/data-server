package com.gemu.dataserver.entity.auxiliary;

import com.google.gson.Gson;

import java.io.*;

/**
 * 实体状态
 * Created by gemu on 29/05/2017.
 */
public class EntityStatus {

    /**
     * 总实体数量
     */
    private int totalEntityCount = 0;
    /**
     * 实体下总文件数量
     */
    private int maxFileNum = 0;

    /**
     * 加载实体状态文件
     * @param entityStatusFilePath 实体状态文件路径
     * @param entityStatusName 实体状态文件名
     * @return 是否成功加载实体状态文件
     */
    public boolean loadStatus(String entityStatusFilePath, String entityStatusName) {
        Reader reader = null;
        try {
            File file = new File(entityStatusFilePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(entityStatusFilePath, entityStatusName);
            if (!file.exists()) file.createNewFile();

            reader = new FileReader(file);
            Gson gson = new Gson();
            EntityStatus entityStatus = gson.fromJson(reader, this.getClass());
            entityStatus = entityStatus == null ? new EntityStatus() : entityStatus;
            this.setMaxFileNum(entityStatus.getMaxFileNum());
            this.setTotalEntityCount(entityStatus.getTotalEntityCount());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public int getTotalEntityCount() {
        return totalEntityCount;
    }

    public void setTotalEntityCount(int totalEntityCount) {
        this.totalEntityCount = totalEntityCount;
    }

    public int getMaxFileNum() {
        return maxFileNum;
    }

    public void setMaxFileNum(int maxFileNum) {
        this.maxFileNum = maxFileNum;
    }
}

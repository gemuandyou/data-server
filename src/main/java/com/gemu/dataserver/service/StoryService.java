package com.gemu.dataserver.service;

import com.gemu.dataserver.core.ModifyData;
import com.gemu.dataserver.core.ReadData;
import com.gemu.dataserver.core.WriteData;
import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.Comment;
import com.gemu.dataserver.entity.PreviewStory;
import com.gemu.dataserver.entity.Story;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.entity.auxiliary.param.StorageStory;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 故事 业务
 * Created by gemu on 29/05/2017.
 */
@Service
public class StoryService {

    @Autowired
    WriteData writeData;
    @Autowired
    ReadData readData;
    @Autowired
    ModifyData modifyData;

    /**
     * 创建故事
     *
     * @param prevImg 故事预览图（可为空）
     * @param prevWords 故事简介（不传值为title值）
     * @param author 作者
     * @param title 故事标题
     * @param subhead 故事子标题
     * @param date 故事日期（时间戳）
     * @param paragraph 故事HTML内容
     * @return 是否创建成功
     */
    public boolean addStory(String prevImg, String prevWords, String author, String title, String subhead, Long date, String paragraph) {
        if (prevWords == null || "".equals(prevWords)) {
            prevWords = title;
        }
        // 创建故事
        Story story = new Story(title, subhead, author, date, paragraph);
        String storyId = writeData.write("friends", "story", story);
        // 生成故事预览
        addPreviewStory(prevImg, prevWords, author, storyId);
        return true;
    }

    /**
     * 添加故事预览
     * @param prevImg 故事预览图（可为空）
     * @param prevWords 故事简介（不传值为title值）
     * @param author 作者
     * @param storyId 故事ID
     */
    private void addPreviewStory(String prevImg, String prevWords, String author, String storyId) {
        PreviewStory previewStory = new PreviewStory(prevImg, prevWords, author, storyId);
        writeData.write("friends","previewStory", previewStory);
    }

    /**
     * 分页获取故事列表
     * @param pageNo 页码
     * @return
     */
    public EntityPage pageStory(int pageNo) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        EntityPage<BaseData> page = readData.read(pageNo, "friends", "previewStory");
        return page;
    }

    /**
     * 根据条件分页获取实体对象列表
     *
     * @param pageNo
     * @param filters    条件集合（目前仅支持一个条件）<br>
     *                   <p>由 字段名 和 字段过滤条件 组成。
     *                   字段过滤条件仅有三种形式。
     *                   <ol>
     *                   <li>等于。格式：string</li>
     *                   <li>不等于。格式：!string</li>
     *                   <li>模糊。格式：%string%</li>
     *                   </pl></p>
     * @return
     */
    public EntityPage filterPageStory(int pageNo, Map<String, String> filters) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        EntityPage<BaseData> page = readData.filterRead(pageNo, "friends", "previewStory", filters);
        return page;
    }

    /**
     * 根据ID获取故事
     * @param id 故事ID
     * @return
     */
    public Story getStory(String id) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        return readData.get(id, "friends", "story");
    }

    /**
     * 更新故事
     * @param id
     * @param storageStory
     * @return
     * @throws SourceNotFoundException
     * @throws EntityNotFoundException
     * @throws DataAssetsNotFoundException
     * @throws NoSuchFieldException
     */
    public boolean update(String id, StorageStory storageStory)
            throws SourceNotFoundException, EntityNotFoundException, DataAssetsNotFoundException, NoSuchFieldException {
        Map<String, Object> storyMap = new HashMap<String, Object>();
        if (storageStory.getTitle() != null) {
            storyMap.put("title", storageStory.getTitle());
        }
        if (storageStory.getSubhead() != null) {
            storyMap.put("subhead", storageStory.getSubhead());
        }
        if (storageStory.getParagraph() != null) {
            storyMap.put("paragraph", storageStory.getParagraph());
        }
        boolean success = modifyData.update("friends", "story", id, Story.class, storyMap);
        if (!success) return false;
        PreviewStory preStory = readData.getByField("storyId", id, "friends", "previewStory");
        if (preStory == null) {
            return false; // TODO 事务回滚
        }
        Map<String, Object> prevStoryMap = new HashMap<String, Object>();
        if (storageStory.getPrevImg() != null) {
            prevStoryMap.put("image", storageStory.getPrevImg());
        }
        if (storageStory.getPrevWords() != null) {
            prevStoryMap.put("words", storageStory.getPrevWords());
        }
        boolean previewSuccess = modifyData.update("friends", "previewStory", id, PreviewStory.class, prevStoryMap);
        return previewSuccess && success;
    }

    /**
     * 评论故事
     * @param comment
     * @return
     */
    public boolean comment(Comment comment) {
        String commentId = writeData.write("friends", "comment", comment);
        return commentId != null && !"".equals(commentId);
    }

    /**
     * 获取故事评论列表
     * @param pageNo
     * @param filters
     * @return
     */
    public EntityPage filterPageComment(int pageNo, Map<String, String> filters) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        EntityPage<BaseData> page = readData.filterRead(pageNo, "friends", "comment", filters);
        return page;
    }

}

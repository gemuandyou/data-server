package com.gemu.dataserver.service;

import com.gemu.dataserver.core.ReadData;
import com.gemu.dataserver.core.WriteData;
import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.PreviewStory;
import com.gemu.dataserver.entity.Story;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        writeData.write("friends", "story", story);
        // 生成故事预览
        addPreviewStory(prevImg, prevWords, author);
        return true;
    }

    /**
     * 添加故事预览
     * @param prevImg 故事预览图（可为空）
     * @param prevWords 故事简介（不传值为title值）
     * @param author 作者
     */
    private void addPreviewStory(String prevImg, String prevWords, String author) {
        PreviewStory previewStory = new PreviewStory(prevImg, prevWords, author);
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
}

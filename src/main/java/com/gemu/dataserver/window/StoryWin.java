package com.gemu.dataserver.window;

import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 故事 接口
 * Created by gemu on 29/05/2017.
 */
@RestController
@RequestMapping("story")
public class StoryWin {

    @Autowired
    StoryService storyService;

    /**
     * 添加故事
     *
     * @param prevImg 故事预览图（可为空）
     * @param prevWords 故事简介（不传值为title值）
     * @param author 作者
     * @param title 故事标题
     * @param subhead 故事子标题
     * @param date 故事日期（时间戳）
     * @param paragraph 故事HTML内容
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String addStory(String prevImg, String prevWords, String author,
                           String title, String subhead, Long date, String paragraph) {
        boolean success = storyService.addStory(prevImg, prevWords, author, title, subhead, date, paragraph);
        return success + "";
    }

    /**
     * 获取故事列表
     * @param pageNo 页码
     * @return
     */
    @RequestMapping(value = "getPage", method = RequestMethod.POST)
    public String getPage(@RequestParam(required = false, defaultValue = "1") int pageNo) {
        EntityPage page = null;
        try {
            page = storyService.pageStory(pageNo);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (DataAssetsNotFoundException e) {
            e.printStackTrace();
        } catch (SourceNotFoundException e) {
            e.printStackTrace();
        }
        return page.toString();
    }


}

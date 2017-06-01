package com.gemu.dataserver.window;

import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.entity.auxiliary.StorageStory;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

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
     * @param storageStory
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String addStory(@RequestBody StorageStory storageStory) {
        boolean success = storyService.addStory(storageStory.getPrevImg(), storageStory.getPrevWords(), storageStory.getAuthor(),
                storageStory.getTitle(), storageStory.getSubhead(), storageStory.getDate(), storageStory.getParagraph());
        return success + "";
    }

    /**
     * 获取故事列表
     * @param pageNo 页码
     * @return
     */
    @RequestMapping(value = "getPage", method = RequestMethod.POST)
    public Object getPage(@RequestParam(required = false, defaultValue = "1") int pageNo) {
        EntityPage page = new EntityPage();
        try {
            page = storyService.pageStory(pageNo);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (DataAssetsNotFoundException e) {
            e.printStackTrace();
        } catch (SourceNotFoundException e) {
            e.printStackTrace();
        }
        return page == null ? new EntityPage() : page;
    }


}

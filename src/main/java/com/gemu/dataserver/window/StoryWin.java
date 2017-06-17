package com.gemu.dataserver.window;

import com.gemu.dataserver.entity.Comment;
import com.gemu.dataserver.entity.Story;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.entity.auxiliary.param.PageCondition;
import com.gemu.dataserver.entity.auxiliary.param.StorageStory;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
     * 根据ID获取故事内容
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "get", method = RequestMethod.POST)
    public Story get(@RequestBody String id) {
        Story story= null;
        try {
            story = storyService.getStory(id);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (DataAssetsNotFoundException e) {
            e.printStackTrace();
        } catch (SourceNotFoundException e) {
            e.printStackTrace();
        }
        return story;
    }

    /**
     * 获取故事列表
     * @param pageCondition 页码和过滤条件
     * @return
     */
    @RequestMapping(value = "getPage", method = RequestMethod.POST)
    public EntityPage getPage(@RequestBody PageCondition pageCondition) {
        EntityPage page = new EntityPage();
        try {
            if (pageCondition.getFilter() != null) {
                page = storyService.filterPageStory(pageCondition.getPageNo(), pageCondition.getFilter());
            } else {
                page = storyService.pageStory(pageCondition.getPageNo());
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (DataAssetsNotFoundException e) {
            e.printStackTrace();
        } catch (SourceNotFoundException e) {
            e.printStackTrace();
        }
        return page == null ? new EntityPage() : page;
    }

    /**
     * 更新故事
     * @param storageStory
     * @return
     */
    @RequestMapping(value = "update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean update(@RequestBody StorageStory storageStory) {
        try {
            return storyService.update(storageStory.getId(), storageStory);
        } catch (SourceNotFoundException e) {
            e.printStackTrace();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (DataAssetsNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 评论故事
     * @param comment
     * @return
     */
    @RequestMapping(value="comment", method = RequestMethod.POST)
    public boolean comment(@RequestBody Comment comment) {
        return storyService.comment(comment);
    }

    /**
     * 获取故事评论列表
     * @param pageCondition
     * @return
     */
    @RequestMapping(value="getCommentPage", method = RequestMethod.POST)
    public EntityPage comment(@RequestBody PageCondition pageCondition) {
        EntityPage page = new EntityPage();
        try {
            if (pageCondition.getFilter() != null) {
                page = storyService.filterPageComment(pageCondition.getPageNo(), pageCondition.getFilter());
            }
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

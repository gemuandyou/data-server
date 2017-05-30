package com.gemu.dataserver.window;

import com.gemu.dataserver.core.ReadData;
import com.gemu.dataserver.core.WriteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by gemu on 28/05/2017.
 */
@RestController
@RequestMapping("/test")
public class TestWin {

    @Autowired
    ReadData readData;
    @Autowired
    WriteData writeData;

    @RequestMapping("")
    public String test() {
//        writeData.write("story", null);
        return "hello";
    }

}

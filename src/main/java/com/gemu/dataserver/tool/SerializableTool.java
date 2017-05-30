package com.gemu.dataserver.tool;

import java.io.*;

/**
 * Created by gemu on 29/05/2017.
 */
public class SerializableTool {


    public static byte[] serialization(Object obj) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteArray);
        out.writeObject(obj);
        byte[] bytes = byteArray.toByteArray();
        return bytes;
    }

    public static Object deserialization(byte[] chunk) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(chunk);
        ObjectInputStream in = new ObjectInputStream(byteArray);
        Object obj = in.readObject();
        return obj;
    }

}

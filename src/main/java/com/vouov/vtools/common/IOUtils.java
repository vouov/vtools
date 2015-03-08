package com.vouov.vtools.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO操作工具类
 *
 * @author yuminglong@gmail.com
 * @date 2015/3/4
 */
public class IOUtils {
    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

    public static void close(Closeable closeable){
        if(closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

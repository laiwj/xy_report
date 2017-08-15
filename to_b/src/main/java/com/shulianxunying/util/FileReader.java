package com.shulianxunying.util;

import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.*;
import java.util.Properties;

public class FileReader {
    public final static String UTF8 = "utf-8";
    public final static String GB2312 = "GB2312";


    public static InputStream getResourcesInputStream(String resource) {
        return FileReader.class.getClassLoader().getResourceAsStream(resource);
    }


    public static Properties readProperties(String property, String encoding) {
        Properties p = new Properties();
        try {
            InputStream inputStream = getResourcesInputStream(property);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
            p.load(inputStreamReader);
            inputStream.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return p;
    }


    public static BufferedReader getReader(String resource, String encoding) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(getResourcesInputStream(resource), encoding));
    }


    public static BufferedWriter getWriter(String absolutePath, String encoding) throws IOException {
        return new BufferedWriter(new FileWriterWithEncoding(absolutePath, encoding, true));
    }
}

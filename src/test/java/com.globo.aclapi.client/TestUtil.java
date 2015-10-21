package com.globo.aclapi.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtil {

    // @param path - after test/resources
    public static String getSample(String path) throws Exception{
        InputStream in = TestUtil.class.getClassLoader().getResourceAsStream(path);

        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb=new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while(read != null) {
            //System.out.println(read);
            sb.append(read);
            read =br.readLine();

        }

        return sb.toString();
    }
}

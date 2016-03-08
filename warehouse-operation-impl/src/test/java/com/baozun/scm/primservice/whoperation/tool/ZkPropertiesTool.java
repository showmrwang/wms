package com.baozun.scm.primservice.whoperation.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZkPropertiesTool implements Watcher {

    // sta env
    static String root = "/sysconfig/wms4/web";
    static String zkHost = "10.8.4.48";
    static String confForlder = "exportconfig";

    static String propertiesForlder = "E:/workspace4.3/center-service-impl-wms4/src/test/resources/exportconfig";

    // dev env
    /*
     * static String root="/sysconfig/pacs/service/dev/"; static String zkHost="10.8.4.48"; static
     * String confForlder="zkconfig_dev";
     */


    static ZooKeeper zk = null;

    public void runAllPro(String root, String forlder) throws KeeperException, InterruptedException {

        root = root + "/";

        String path = ZkPropertiesTool.class.getResource("/" + forlder).getPath();

        File dir = new File(path);
        File[] tmps = dir.listFiles();
        if (tmps != null) {
            for (File cd : tmps) {
                // list.add(child.getAbsolutePath());

                int index2 = cd.getName().lastIndexOf(".");
                String fileName = cd.getName().substring(0, index2);
                run(root + fileName, forlder + "/" + cd.getName());
            }
        }
    }



    public static Properties findCommonPro(String source) {

        String path = source;

        Properties pro = new Properties();


        InputStream is = getResourceAsStream(path, ZkPropertiesTool.class);

        pro = new Properties();
        try {
            pro.load(is);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return pro;
    }

    public static URL getResource(String resourceName, Class<?> callingClass) {
        URL url = null;
        url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = ZkPropertiesTool.class.getClassLoader().getResource(resourceName);
        }
        if (url == null && callingClass != null) {
            url = callingClass.getClassLoader().getResource(resourceName);
        }
        return url;
    }

    public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
        URL url = getResource(resourceName, callingClass);
        try {
            return (url != null) ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }

    public void run(String root, String configPath) throws KeeperException, InterruptedException {



        Properties prop = findCommonPro(configPath);

        String[] strs = root.split("/");

        String lastNode = "";
        for (int i = 1; i < strs.length; i++) {
            String str = strs[i];
            lastNode = lastNode + "/" + str;
            Stat stat = zk.exists(lastNode, false);

            if (stat == null) {
                zk.create(lastNode, "config".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

        }


        for (Map.Entry<Object, Object> entry : prop.entrySet()) {

            System.out.println(entry.getKey() + ":" + entry.getValue());

            Stat stat = zk.exists(root + "/" + entry.getKey(), false);

            if (stat != null) {
                zk.delete(root + "/" + entry.getKey(), -1);
            }
            zk.create(root + "/" + entry.getKey(), entry.getValue().toString().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void init() {

        try {
            zk = new ZooKeeper(zkHost, 30000, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static void export(String zkNode, String filePath) throws Exception {

        String path = filePath;


        List<String> children = zk.getChildren(zkNode, false);



        for (String child : children) {
            Properties properties = new Properties();


            List<String> keys = zk.getChildren(zkNode + "/" + child, false);
            if (keys != null && keys.size() > 0) {
                for (String key : keys) {
                    String value = new String(zk.getData(zkNode + "/" + child + "/" + key, false, null));
                    properties.put(key, value);
                }
                properties.save(new FileOutputStream(new File(path + "/" + child + ".properties")), "");
            }


        }

    }

    public static void main(String[] args) throws Exception, InterruptedException {



        ZkPropertiesTool zt = new ZkPropertiesTool();

        zt.init();


        zt.runAllPro(root, confForlder);


        // zt.export(root, propertiesForlder);


    }

    @Override
    public void process(WatchedEvent event) {

    }

}

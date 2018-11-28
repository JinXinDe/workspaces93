package cn.itcast.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

public class FastDFSTest {

    @Test
    public void test() throws Exception {
        //1. 获取追踪服务器的配置文件路径并设置到全局配置对象中；
        String cont_filename = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();
        ClientGlobal.init(cont_filename);
        //2. 创建追踪服务器客户端；
        TrackerClient trackerClient = new TrackerClient();
        //3. 利用追踪服务器客户端获取追踪服务对象；
        TrackerServer trackerServer = trackerClient.getConnection();
        //4. 创建存储服务对象；可以由追踪服务器获取所以可以为null
        StorageServer storageServer = null;
        //5. 创建存储客户端对象
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        //6. 利用存储客户端对象的上传方法上传图片
        /**
         * 返回的数据如下：
         * group1
         * M00/00/00/wKgMqFv-GlKAT1qgAABw0se6LsY088.jpg
         */
        String[] upload_file = storageClient.upload_file("D:\\aaa\\2.jpg", "jpg", null);
        if (upload_file != null && upload_file.length > 0) {

            //7. 处理上传的返回结果
            for (String str : upload_file) {
                System.out.println(str);
            }

            //组名
            String groupName = upload_file[0];
            //文件路径
            String fileName = upload_file[1];
            //获取存储该图片的服务器ip
            ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, groupName, fileName);
            for (ServerInfo serverInfo : serverInfos) {
                System.out.println("ip = " + serverInfo.getIpAddr() + "; post= " + serverInfo.getPort() );
            }

            String url = "http://" + serverInfos[0].getIpAddr() + "/" + groupName + "/" + fileName;
            System.out.println("图片的可访问地址为: " + url);
        }
    }
}

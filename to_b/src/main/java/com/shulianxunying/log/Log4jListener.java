package com.shulianxunying.log;

import org.apache.log4j.Logger;
import org.springframework.web.util.Log4jConfigListener;
import org.springframework.web.util.Log4jWebConfigurer;

import javax.servlet.ServletContextEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


/**
 * Created by 李龙飞 on 2015-07-31.
 */
public class Log4jListener extends Log4jConfigListener {
    Logger logger = Logger.getLogger(Log4jListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String address = null;
        try {
            address = getRealIp();//InetAddress.getLocalHost().getHostAddress();
            Log4jWebConfigurer.initLogging(event.getServletContext());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public String getRealIp() throws SocketException, UnknownHostException {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        Enumeration<NetworkInterface> netInterfaces =
                NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;// 是否找到外网IP
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();

            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress()
                        && !ip.isLoopbackAddress()
                        && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                    String ipNow = ip.getHostAddress();
                    if (ipNow != null && ipNow.length() > 0)
                        netip = (netip==null?"":netip+ ",")  + ipNow;
                    finded = true;
                    logger.info("ip:" + ip + " LocalHost: " + address.toString() + "外网IP");
                    //break;
                } else if (ip.isSiteLocalAddress()
                        && !ip.isLoopbackAddress()
                        && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                    String ipNow = ip.getHostAddress();
                    if (ipNow != null && ipNow.length() > 0)
                        localip =  (localip==null?"":localip+ ",") + ip.getHostAddress();
                    logger.info("ip:" + ip + " LocalHost: " + address.toString() + "内网IP");
                }

                //logger.notice_list("ip:"+ip+" LocalHost: "+address.toString()+"内网IP");

            }
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }
}


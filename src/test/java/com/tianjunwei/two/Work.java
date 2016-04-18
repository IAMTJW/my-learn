package com.tianjunwei.two;

import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;  
import com.rabbitmq.client.Connection;  
import com.rabbitmq.client.ConnectionFactory;  
import com.rabbitmq.client.QueueingConsumer;  
  
/**
* @Title: Work.java  
* @Package com.tianjunwei.two  
* @Description:  指定工作队列消费
* @author tianjunwei  tiantianjunwei@126.com   
* @date 2016年4月12日 下午11:26:40  
* @version V1.0
 */
public class Work  {  
    //队列名称  
    private final static String QUEUE_NAME = "queue_one";  
  
    public static void main(String[] argv) throws java.io.IOException,java.lang.InterruptedException, TimeoutException {  
        //区分不同工作进程的输出  
        int hashCode = Work.class.hashCode();  
        //创建连接和频道  
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("localhost");  
        Connection connection = factory.newConnection();  
        Channel channel = connection.createChannel();  
        //声明队列  
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  
        System.out.println(hashCode  
                + " [*] Waiting for messages. To exit press CTRL+C");  
      
        
        QueueingConsumer consumer = new QueueingConsumer(channel);  
        // 指定消费队列  
        //打开应答机制
        boolean ack = false;
        channel.basicConsume(QUEUE_NAME, ack, consumer);  
        while (true)  
        {  
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();  
            String message = new String(delivery.getBody());  
  
            System.out.println(hashCode + " [x] Received '" + message + "'");  
            doWork(message);  
            System.out.println(hashCode + " [x] Done");  
            //发送应答
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
  
        }  
  
    }  
  
    /** 
     * 每个点耗时1s 
     * @param task 
     * @throws InterruptedException 
     */  
    private static void doWork(String task) throws InterruptedException  
    {  
        for (char ch : task.toCharArray())  
        {  
            if (ch == '.')  
                Thread.sleep(1000);  
        }  
    }  
}  
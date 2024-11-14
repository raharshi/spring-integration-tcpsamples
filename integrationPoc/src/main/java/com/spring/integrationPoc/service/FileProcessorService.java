package com.spring.integrationPoc.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.integrationPoc.dto.UserInfo;

@Service
public class FileProcessorService {


    
    @Bean(name="process-data-channel")
    public MessageChannel processChannel(){
        // return new QueueChannel(10);
        return new DirectChannel();
    }
    @Bean(name="reply-channel")
    public MessageChannel replyChannel(){
        // return new QueueChannel(10);
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "tcpServerRequestChannel",outputChannel = "process-data-channel")
    public Message<String> placeOrder(Message<String> request){
        System.out.println("INPUT::::"+request.getPayload());
        return request;
    }

    @ServiceActivator(inputChannel = "process-data-channel", outputChannel = "reply-channel")
    public Message<UserInfo> processData(Message<String> request){
            String filePath = request.getPayload();
            File file = new File(filePath);

            Message<UserInfo> userMessage = MessageBuilder.withPayload(new UserInfo()).build();
            
            if (file.exists() && file.isFile()) {
                try {
                    // Read file content as a JSON string
                    ObjectMapper objectMapper = new ObjectMapper();
                    UserInfo data = objectMapper.readValue(file, UserInfo.class);
                    userMessage = MessageBuilder.withPayload(data).build();
                    userMessage.getPayload().setStatus("Request executed succesfully!!");
                } catch (Exception e) {
                    throw new RuntimeException("Error reading file: " + e.getMessage());
                }
            } else {
                // throw new RuntimeException("File not found or invalid path: " + filePath);
                userMessage.getPayload().setStatus("Request Failed!!");
            }
            
        return userMessage;
    }

    @ServiceActivator(inputChannel = "reply-channel")
    public String replyrequest(Message<UserInfo> request) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(request.getPayload());
    }

}

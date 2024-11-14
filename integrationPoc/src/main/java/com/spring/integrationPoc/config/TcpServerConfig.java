package com.spring.integrationPoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.messaging.MessageChannel;

@Configuration
public class TcpServerConfig {

    @Value("${tcp.server.port}")
    private int tcpPort;

    // creating NioServer connection factory for non-blocking i/p and o/p
    @Bean
    public TcpNioServerConnectionFactory serverConnectionFactory() {
        TcpNioServerConnectionFactory factory = new TcpNioServerConnectionFactory(tcpPort);
        factory.setSerializer(TcpCodecs.crlf());
        factory.setDeserializer(TcpCodecs.crlf());
        return factory;
    }

    // creating inBound Gateway for accepting request
    @Bean
    public TcpInboundGateway tcpInboundGateway() {
        TcpInboundGateway gateway = new TcpInboundGateway();
        // setting connection factory
        gateway.setConnectionFactory(serverConnectionFactory());
        // setting the request channel
        gateway.setRequestChannel(tcpServerRequestChannel());
        return gateway;
    }

    // creating direct channel as request channel
    @Bean
    public MessageChannel tcpServerRequestChannel() {
        return MessageChannels.direct().getObject();
    }
}

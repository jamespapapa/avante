package com.my.homework.external

import io.netty.channel.ChannelOption
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

interface WebClientBuildable {
    fun WebClient.Builder.commonClientConnector() =
        this.clientConnector(getReactorClientHttpConnector())

    private fun getReactorClientHttpConnector(): ReactorClientHttpConnector {
        val sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()
        val httpClient = HttpClient.create()
            .secure { sslContextSpec ->
                sslContextSpec.sslContext(sslContext)
            }
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(10))
                    .addHandlerLast(WriteTimeoutHandler(10))
            }

        return ReactorClientHttpConnector(httpClient)
    }
}

/*
 * Copyright 2013-2015 Eugene Petrenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jonnyzzz.teamcity.plugins.node.agent.nvm

import com.jonnyzzz.teamcity.plugins.node.common.log4j
import jetbrains.buildServer.version.ServerVersionHolder
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.SchemeRegistryFactory
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpProtocolParams
import java.security.cert.X509Certificate
import jetbrains.buildServer.serverSide.TeamCityProperties
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.NTCredentials
import org.apache.http.auth.UsernamePasswordCredentials
import java.net.ProxySelector
import org.apache.http.conn.scheme.Scheme
import org.apache.http.impl.conn.ProxySelectorRoutePlanner
import org.apache.http.client.protocol.RequestAcceptEncoding
import org.apache.http.client.protocol.ResponseContentEncoding
import org.apache.http.impl.conn.DefaultProxyRoutePlanner
import org.apache.http.conn.ssl.X509HostnameVerifier
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.springframework.beans.factory.DisposableBean
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 13.08.13 22:43
 */
interface HttpClientWrapper {
  fun <T> execute(request: HttpUriRequest, action: HttpResponse.() -> T): T
}

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.08.11 16:24
 */
class HttpClientWrapperImpl : HttpClientWrapper, DisposableBean {
  private val LOG = log4j(NVMDownloader::class.java)

  private val myClient: HttpClient;
  init {
    val serverVersion = ServerVersionHolder.getVersion().displayVersion;
    val ps = BasicHttpParams();

    DefaultHttpClient.setDefaultHttpParams(ps);
    HttpConnectionParams.setConnectionTimeout(ps, 300 * 1000);
    HttpConnectionParams.setSoTimeout(ps, 300 * 1000);
    HttpProtocolParams.setUserAgent(ps, "JetBrains TeamCity " + serverVersion);

    val cm =
            if (!TeamCityProperties.getBoolean("teamcity.node.verify.ssl.certificate")) {
              val schemaRegistry = SchemeRegistryFactory.createDefault()
              val sslSocketFactory = SSLSocketFactory(
                      object : TrustStrategy {
                        override fun isTrusted(chain: Array<out X509Certificate>?, authType: String?): Boolean {
                          return true;
                        }
                      }, object : X509HostnameVerifier {
                        override fun verify(host: String?, ssl: SSLSocket?) {}
                        override fun verify(host: String?, cert: X509Certificate?) { }
                        override fun verify(host: String?, cns: Array<out String>?, subjectAlts: Array<out String>?) { }
                        override fun verify(p0: String?, p1: SSLSession?): Boolean = true
                      })

              schemaRegistry.register(Scheme("https", 443, sslSocketFactory));
              PoolingClientConnectionManager(schemaRegistry)
            } else {
              PoolingClientConnectionManager()
            }

    val httpclient = DefaultHttpClient(cm, ps);

    httpclient.routePlanner = ProxySelectorRoutePlanner(
            httpclient.connectionManager!!.schemeRegistry,
            ProxySelector.getDefault());


    httpclient.addRequestInterceptor(RequestAcceptEncoding());
    httpclient.addResponseInterceptor(ResponseContentEncoding());
    httpclient.httpRequestRetryHandler = DefaultHttpRequestRetryHandler(3, true);

    val PREFIX = "teamcity.http.proxy.";
    val SUFFIX = ".node";

    val proxyHost = TeamCityProperties.getPropertyOrNull(PREFIX + "host" + SUFFIX)
    val proxyPort = TeamCityProperties.getInteger(PREFIX + "port" + SUFFIX, 3128)

    val proxyDomain = TeamCityProperties.getPropertyOrNull(PREFIX + "domain" + SUFFIX)
    val proxyUser = TeamCityProperties.getPropertyOrNull(PREFIX + "user" + SUFFIX)
    val proxyPassword = TeamCityProperties.getPropertyOrNull(PREFIX + "password" + SUFFIX)
    val proxyWorkstation = TeamCityProperties.getPropertyOrNull(PREFIX + "workstation" + SUFFIX)

    if (proxyHost != null && proxyPort > 0) {
      val proxy = HttpHost(proxyHost, proxyPort);

      httpclient.routePlanner = DefaultProxyRoutePlanner(proxy)
      
      if (proxyUser != null && proxyPassword != null) {
        if (proxyDomain != null || proxyWorkstation != null) {
          LOG.info("TeamCity.Node.NVM. Using HTTP proxy $proxyHost:$proxyPort, username: ${proxyDomain ?: proxyWorkstation ?: "."}\\$proxyUser")

          httpclient.credentialsProvider.setCredentials(
                  AuthScope(proxyHost, proxyPort),
                  NTCredentials(proxyUser,
                          proxyPassword,
                          proxyWorkstation,
                          proxyDomain))
        } else {
          LOG.info("TeamCity.Node.NVM. Using HTTP proxy $proxyHost:$proxyPort, username: $proxyUser")
          httpclient.credentialsProvider.setCredentials(
                  AuthScope(proxyHost, proxyPort),
                  UsernamePasswordCredentials(proxyUser,
                          proxyPassword))
        }
      } else {
        LOG.info("TeamCity.Node.NVM. Using HTTP proxy $proxyHost:$proxyPort")
      }
    }

    myClient = httpclient;
  }

  override fun <T> execute(request: HttpUriRequest, action: HttpResponse.() -> T): T {
    val response = myClient.execute(request)!!
    try {
      return response.action()
    } finally {
      request.abort()
    }
  }


  override fun destroy() {
    myClient.connectionManager!!.shutdown();
  }
}


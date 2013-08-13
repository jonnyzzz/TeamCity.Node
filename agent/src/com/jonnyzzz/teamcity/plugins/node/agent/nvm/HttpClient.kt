/*
 * Copyright 2013-2013 Eugene Petrenko
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
import java.net.ProxySelector
import org.apache.http.conn.scheme.Scheme
import org.apache.http.impl.conn.ProxySelectorRoutePlanner
import org.apache.http.client.protocol.RequestAcceptEncoding
import org.apache.http.client.protocol.ResponseContentEncoding
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.springframework.beans.factory.DisposableBean

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 13.08.13 22:43
 */
public trait HttpClientWrapper {
  fun <T> execute(request: HttpUriRequest, action: HttpResponse.() -> T): T
}

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.08.11 16:24
 */
public class HttpClientWrapperImpl : HttpClientWrapper, DisposableBean {
  private val myClient: HttpClient;
  {
    val serverVersion = ServerVersionHolder.getVersion().getDisplayVersion();
    val ps = BasicHttpParams();

    DefaultHttpClient.setDefaultHttpParams(ps);
    HttpConnectionParams.setConnectionTimeout(ps, 300 * 1000);
    HttpConnectionParams.setSoTimeout(ps, 300 * 1000);
    HttpProtocolParams.setUserAgent(ps, "JetBrains TeamCity " + serverVersion);

    val schemaRegistry = SchemeRegistryFactory.createDefault()!!;
    val sslSocketFactory = SSLSocketFactory(object :TrustStrategy {
      public override fun isTrusted(chain: Array<out X509Certificate>?, authType: String?): Boolean {
        return !TeamCityProperties.getBoolean("teamcity.github.verify.ssl.certificate");
      }
    })

    schemaRegistry.register(Scheme("https", 443, sslSocketFactory));
    val cm = PoolingClientConnectionManager(schemaRegistry)
    val httpclient = DefaultHttpClient(cm, ps);

    httpclient.setRoutePlanner(ProxySelectorRoutePlanner(
            httpclient.getConnectionManager()!!.getSchemeRegistry(),
            ProxySelector.getDefault()));


    httpclient.addRequestInterceptor(RequestAcceptEncoding());
    httpclient.addResponseInterceptor(ResponseContentEncoding());
    httpclient.setHttpRequestRetryHandler(DefaultHttpRequestRetryHandler(3, true));

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


  public override fun destroy() {
    myClient.getConnectionManager()!!.shutdown();
  }
}


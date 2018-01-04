<%--
  ~ Copyright 2013-2017 Eugene Petrenko
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ include file="/include.jsp"%>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="bean" class="com.jonnyzzz.teamcity.plugins.node.common.BowerBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr>
  <th><label for="${bean.targets}">Type:</label></th>
  <td>
    <props:selectProperty name="${bean.bowerMode}">
      <c:forEach var="it" items="${bean.bowerModes}">
        <props:option value="${it.value}"><c:out value="${it.title}"/></props:option>
      </c:forEach>
    </props:selectProperty>
    <span class="smallNote">
      Specify weather you like to use system-wide or project's npm installed bower
    </span>
    <span class="error" id="error_${bean.bowerMode}"></span>
  </td>
</tr>

<tr>
  <th><label for="${bean.targets}">Tasks:</label></th>
  <td>
    <props:multilineProperty name="${bean.targets}" linkTitle="Commands" cols="58" rows="5" expanded="${true}"/>
    <span class="smallNote">Specify bower tasks to run (new-line separated)</span>
    <span class="error" id="error_${bean.targets}"></span>
  </td>
</tr>

<forms:workingDirectory/>

<tr>
  <th><label for="${bean.commandLineParameterKey}">Additional command line parameters:</label></th>
  <td>
    <props:multilineProperty name="${bean.commandLineParameterKey}"  cols="58" linkTitle="Expand" rows="5"/>
    <span class="smallNote">
      Enter additional command line parameters for Bower. Put each parameter on a new line
      <br />
      <em>--teamcity.properties.all</em> and <em>--teamcity.properties</em> parameters are added implicitly
      pointing to JSON files with all TeamCity properties and system properties respectively
    </span>
    <span class="error" id="error_${bean.commandLineParameterKey}"></span>
  </td>
</tr>


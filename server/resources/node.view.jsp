<%--
  ~ Copyright 2000-2013 Eugene Petrenko
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
<jsp:useBean id="bean" class="com.jonnyzzz.teamcity.plugins.node.common.NodeBean"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<c:set var="nmode" value="${propertiesBean.properties[bean.executionModeKey]}"/>

<c:choose>
  <c:when test="${nmode eq bean.executionModeFile.value}">
    <div class="parameter">
      File: <props:displayValue name="${bean.executionModeFile.parameter}"/>
    </div>
  </c:when>
  <c:when test="${nmode eq bean.executionModeScript.value}">
    <div class="parameter">
      Script: <props:displayValue name="${bean.executionModeScript.parameter}" showInPopup="${true}" emptyValue="<empty>"/>
    </div>
  </c:when>
</c:choose>

<div class="parameter">
  Additional Command Line Arguments: <props:displayValue name="${bean.commandLineParameterKey}" showInPopup="${true}" emptyValue="<empty>"/>
</div>

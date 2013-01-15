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

<jsp:useBean id="bean" class="com.jonnyzzz.teamcity.plugins.node.common.NPMBean"/>

<tr>
  <th><label for="${bean.npmCommandsKey}">npm commands:</label></th>
  <td>
    <props:multilineProperty name="${bean.npmCommandsKey}" linkTitle="Commands" cols="58" rows="5" expanded="${true}"/>
    <span class="smallNote">Specify npm commands to run</span>
    <span class="error" id="error_${bean.npmCommandsKey}"></span>
  </td>
</tr>

<l:settingsGroup title="Execution">
  <!-- here goes npm runtime selector, that is not jet here -->
<forms:workingDirectory/>

<tr>
  <th><label for="${bean.commandLineParameterKey}">Additional command line parameters:</label></th>
  <td>
    <props:multilineProperty name="${bean.commandLineParameterKey}"  cols="58" linkTitle="Expand" rows="5"/>
    <span class="smallNote">Enter additional command line parameters for npm. Put each parameter on a new line</span>
    <span class="error" id="error_${bean.commandLineParameterKey}"></span>
  </td>
</tr>
</l:settingsGroup>

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

<tr>
  <th>Script:</th>
  <td>
    <props:selectProperty name="${bean.executionModeKey}" id="nodeExecutionMode" className="longField">
      <c:forEach var="mode" items="${bean.executionModeValues}">
        <props:option value="${mode.value}"><c:out value="${mode.description}"/></props:option>
      </c:forEach>
    </props:selectProperty>
    <span class="error" id="error_${bean.executionModeKey}"></span>
  </td>
</tr>

<tr class="jonnyzzz_node_${bean.executionModeFile.parameter}">
  <th><label for="${bean.executionModeFile.parameter}">Script file:</label></th>
  <td>
    <props:textProperty name="${bean.executionModeFile.parameter}" className="longField"/>
    <span class="smallNote">Path to JavaScript or CoffeeScript, relative to the checkout directory</span>
    <span class="error" id="error_${bean.executionModeFile.parameter}"></span>
  </td>
</tr>

<tr class="jonnyzzz_node_${bean.executionModeScript.parameter}">
  <th><label for="${bean.phantomJsExtensionKey}">File Type<l:star/>:</label></th>
  <td>
    <props:textProperty name="${bean.phantomJsExtensionKey}" className="longField"/>
    <span class="smallNote">Select script file extension, use 'js' by default</span>
    <span class="error" id="error_${bean.phantomJsExtensionKey}"></span>
  </td>
</tr>

<tr class="jonnyzzz_node_${bean.executionModeScript.parameter}">
  <th><label for="${bean.executionModeScript.parameter}">Script source:</label></th>
  <td>
    <props:multilineProperty name="${bean.executionModeScript.parameter}"
                             linkTitle="JavaScript to Execute"
                             cols="58" rows="10"
                             expanded="${true}"/>
    <span class="smallNote">Enter contents of a JavaScript or CoffeeScript. TeamCity references will be replaced in the code</span>
    <span class="error" id="error_${bean.executionModeScript.parameter}"></span>
  </td>
</tr>

<tr>
  <th><label for="${bean.scriptParameterKey}">Script arguments:</label></th>
  <td>
    <props:multilineProperty name="${bean.scriptParameterKey}"  cols="58" linkTitle="Expand" rows="5" expanded="${true}"/>
    <span class="smallNote">Enter arguments for script</span>
    <span class="error" id="error_${bean.scriptParameterKey}"></span>
  </td>
</tr>

<l:settingsGroup title="Execution">
<tr>
  <th><label for="${bean.toolPathKey}">Phantom js<l:star/>:</label></th>
  <td>
    <props:textProperty name="${bean.toolPathKey}" className="longField"/>
    <span class="smallNote">Specify path to Phantom.JS executable</span>
    <span class="error" id="error_${bean.toolPathKey}"></span>
  </td>
</tr>

<forms:workingDirectory/>

<tr>
  <th><label for="${bean.commandLineParameterKey}">Additional command line parameters:</label></th>
  <td>
    <props:multilineProperty name="${bean.commandLineParameterKey}"  cols="58" linkTitle="Expand" rows="5"/>
    <span class="smallNote">Enter additional command line parameters. Put each parameter on a new line</span>
    <span class="error" id="error_${bean.commandLineParameterKey}"></span>
  </td>
</tr>
</l:settingsGroup>

<script type="text/javascript">
  (function() {
    var update = function() {
      var mode = $j("#nodeExecutionMode").val();

      <c:forEach var="mode" items="${bean.executionModeValues}">
        if (mode == "${mode.value}") {
          $j("tr.jonnyzzz_node_${mode.parameter}").show();
        } else {
          $j("tr.jonnyzzz_node_${mode.parameter}").hide();
        }
      </c:forEach>
      BS.MultilineProperties.updateVisible();
    };

    $j("#nodeExecutionMode").change(update);
    $j(document).ready(update);
  })();
</script>

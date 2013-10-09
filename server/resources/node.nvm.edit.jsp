<%@ include file="/include.jsp" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright 2013-2013 Eugene Petrenko
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

<jsp:useBean id="bean" class="com.jonnyzzz.teamcity.plugins.node.common.NVMBean"/>

<tr>
  <th><label for="${bean.NVMVersion}">Node.js version:</label><l:star/></th>
  <td>
    <props:textProperty name="${bean.NVMVersion}" className="longField"/>
    <span class="smallNote">
      Specify version of Node.js to install
    </span>
  </td>
</tr>

<tr>
  <th><label for="${bean.NVMURL}">NVM URL</label><l:star/></th>
  <td>
    <props:textProperty name="${bean.NVMURL}" className="longField"/>
    <span class="smallNote">
      Specify custom URL for NVM download as <em>.zip</em> archive.
      Leave blank to use default <a href="https://github.com/creationix/nvm" target="_blank">creatonix/nvm</a>
    </span>
  </td>
</tr>

<tr>
  <th>Options:</th>
  <td>
    <props:checkboxProperty name="${bean.NVMSource}" />
    <label for="${bean.NVMSource}">Install from source</label>
    <span class="smallNote">
      Adds <em>-s</em> commandline parameter to make it install node.js from source
    </span>
  </td>
</tr>


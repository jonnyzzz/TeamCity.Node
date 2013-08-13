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

import com.jonnyzzz.teamcity.plugins.node.agent.nvm.HttpClientWrapperImpl;
import com.jonnyzzz.teamcity.plugins.node.agent.nvm.NVMDownloader;
import jetbrains.buildServer.BaseTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 13.08.13 23:18
 */
public class NVMDownloadTest extends BaseTestCase {
  @Test
  public void test() throws IOException {
    File dir = createTempDir();
    new NVMDownloader(new HttpClientWrapperImpl()).downloadNVM(dir);
  }
}

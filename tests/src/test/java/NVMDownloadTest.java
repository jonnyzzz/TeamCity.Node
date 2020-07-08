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
import com.jonnyzzz.teamcity.plugins.node.common.NVMBean;
import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.RunBuildException;
import junit.framework.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 13.08.13 23:18
 */
public class NVMDownloadTest extends BaseTestCase {
  @BeforeMethod
  public void setup() {
    System.setProperty("teamcity.node.verify.ssl.certificate", "false");
  }
  
  @Test
  public void test_default() throws IOException {
    doSuccessfulTest(new NVMBean().getNVM_ArchiveUrl());
  }

  @Test
  public void test_master() throws IOException {
    doSuccessfulTest("https://github.com/nvm-sh/nvm/archive/master.zip");
  }

  private void doSuccessfulTest(String url) throws IOException {
    final File dir = createTempDir();
    new NVMDownloader(new HttpClientWrapperImpl()).downloadNVM(dir, url);

    File[] files = dir.listFiles();
    Assert.assertNotNull(files);

    System.out.println("result: " + Arrays.toString(files));

    Assert.assertTrue(files.length > 0);
    Assert.assertTrue(new File(dir, "nvm.sh").isFile());
  }

  @Test(expectedExceptions = RunBuildException.class)
  public void error() throws IOException {
    File dir = createTempDir();
    new NVMDownloader(new HttpClientWrapperImpl()).downloadNVM(dir, "http://blog.jonnyzzz.name");
  }
}

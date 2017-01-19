/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.assets;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mule.module.apikit.AbstractConfiguration.APPLICATION_RAML;

import org.mule.module.apikit.util.FunctionalAppDeployTestCase;
import org.mule.runtime.core.util.FileUtils;
import org.mule.tck.junit4.rule.DynamicPort;

import com.jayway.restassured.RestAssured;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ApiDirTestCase extends FunctionalAppDeployTestCase
{

    @Rule
    public DynamicPort serverPort = new DynamicPort("serverPort");
    @Rule
    public DynamicPort serverPort2 = new DynamicPort("serverPort2");
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Override
    public int getTestTimeoutSecs()
    {
        return 6000;
    }

    @Override
    public void doSetUp() throws Exception
    {
        RestAssured.port = serverPort2.getNumber();
        super.doSetUp();
    }

    @Test
    public void ramlApi() throws Exception
    {
        File appDir = new File(this.getClass().getClassLoader().getResource("org/mule/module/apikit/assets").getFile());//"src/test/resources", "org/mule/module/apikit/assets");
        String appName = "ramlApi";
        //deploymentService.deployDomain(new File(this.getClass().getClassLoader().getResource("org/mule/module/apikit/domain/default")));
        //File domainDir = Files.createTempDirectory("/default");//new File(this.getClass().getClassLoader().getResource("org/mule/module/apikit/domain").getFile());//"src/test/resources", "org/mule/module/apikit/assets");
//        URL folderUrl = new URL("file://", folder.getRoot().getPath());

//        deploymentService.deployDomain(folderUrl);
        File tempAppFolder = new File(muleHome, "default");
        FileUtils.copyDirectory(new File(muleHome.getPath() + "/domains"), tempAppFolder);
        //File appFolder = new File(appsDir, appName);
        //tempAppFolder.renameTo(appFolder);


        deployExplodedApp(appDir, appName);
        deploymentService.start();
        assertApplicationDeploymentSuccess(applicationDeploymentListener, appName);
        assertRaml();
        assertConsole();
    }

    private void assertRaml() throws Exception
    {
        given().header("Accept", APPLICATION_RAML)
                .expect()
                .response().body(containsString("baseUri: http://localhost:" + serverPort.getValue() + "/api"), containsString("!include example.json"))
                .header("Content-type", APPLICATION_RAML).statusCode(200)
                .when().get("/api/?raml");
    }

    private void assertConsole() throws Exception
    {
        given().header("Accept", "text/html")
                .expect()
                .response().body(containsString("src=\"api/?raml\""))
                .header("Content-type", "text/html").statusCode(200)
                .when().get("/");
    }
}
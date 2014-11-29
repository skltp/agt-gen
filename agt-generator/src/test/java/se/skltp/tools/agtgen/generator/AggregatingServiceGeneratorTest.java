/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.tools.agtgen.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.soitoolkit.tools.generator.model.enums.DeploymentModelEnum.STANDALONE_DEPLOY;
import static org.soitoolkit.tools.generator.util.SystemUtil.BUILD_COMMAND;
import static org.soitoolkit.tools.generator.util.SystemUtil.CLEAN_COMMAND;
import static org.soitoolkit.tools.generator.util.SystemUtil.ECLIPSE_AND_TEST_REPORT_COMMAND;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.soitoolkit.tools.generator.model.enums.DeploymentModelEnum;
import org.soitoolkit.tools.generator.model.enums.MuleVersionEnum;
import org.soitoolkit.tools.generator.util.PreferencesUtil;
import org.soitoolkit.tools.generator.util.SystemUtil;

public class AggregatingServiceGeneratorTest {

	private static final String TEST_OUT_FOLDER = PreferencesUtil.getDefaultRootFolder() + "/jUnitTests";
	private static final String VERSION = "1.0.0-SNAPSHOT";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

    /**
	 * 
	 * @throws java.io.IOException
	 */
	@Test
	public void testAggregatingServices1() throws IOException {

		doTestAggregatingServices("riv.crm.requeststatus", "GetAggregatedRequestActivities", MuleVersionEnum.MULE_3_3_0, STANDALONE_DEPLOY);

	}

    /**
	 * 
	 * @throws java.io.IOException
	 */
	@Test
	public void testAggregatingServices2() throws IOException {

		doTestAggregatingServices("riv.clinicalprocess.activity.actions", "GetAggregatedActivity", MuleVersionEnum.MULE_3_3_0, STANDALONE_DEPLOY);

		/*
		MuleVersionEnum[] muleVersions = MuleVersionEnum.values();
		for (int i = 0; i < muleVersions.length; i++) {
			if (!muleVersions[i].isEEVersion()) {
				doTestAggregationServices("se.skltp.aggregatingservices.clinicalprocess.activity.actions", "GetAggregatedGetActivity" + muleVersions[i].getVerNoNumbersOnly(), muleVersions[i], STANDALONE_DEPLOY);
			}
		}
		*/
	}

	private void doTestAggregatingServices(String domainId, String artifactId, MuleVersionEnum muleVersion, DeploymentModelEnum deploymentModel) throws IOException {

		String projectFolder = TEST_OUT_FOLDER + "/" + artifactId + "-TEST";
		createAggregationService(domainId, artifactId, muleVersion, projectFolder);
		//GetAggregatedGetActivity-TEST/riv.clinicalprocess.activity.actions/GetAggregatedGetActivity/trunk
		performMavenBuild(projectFolder + "/" + domainId + "/" + artifactId + "/trunk");
	}

	private void createAggregationService(String domainId, String artifactId, MuleVersionEnum muleVersion, String projectFolder) throws IOException {

		SystemUtil.delDirs(projectFolder);

        boolean genSchema = true;

		int expectedNoOfFiles1 = 91; // For domain + subdomain
		int expectedNoOfFiles2 = 94; // For domain + subdomain + subdomain
		
		int noOfFilesBefore = SystemUtil.countFiles(projectFolder);

        new AggregatingServiceGenerator(System.out, domainId, artifactId, VERSION, muleVersion, projectFolder, genSchema, null, null).startGenerator();
				
		int actualNoOfFiles = SystemUtil.countFiles(projectFolder) - noOfFilesBefore;
		
		assertTrue("Missmatch in expected number of created files and folders."
				+ " Expected " + expectedNoOfFiles1 + " or " + expectedNoOfFiles2 + " but found " + actualNoOfFiles,
				actualNoOfFiles == expectedNoOfFiles1 || actualNoOfFiles == expectedNoOfFiles2);
	}

	private void performMavenBuild(String projectFolder) throws IOException {

		boolean testOk = false;
		
		try {
			SystemUtil.executeCommand(BUILD_COMMAND, projectFolder);
			testOk = true;
		} finally {
			// Always try to create eclipsefiles and test reports 
			SystemUtil.executeCommand(ECLIPSE_AND_TEST_REPORT_COMMAND, projectFolder);
		}
		
		// If the build runs fine then also perform a clean-command to save GB's of diskspace...
		if (testOk) SystemUtil.executeCommand(CLEAN_COMMAND, projectFolder);
	}

}
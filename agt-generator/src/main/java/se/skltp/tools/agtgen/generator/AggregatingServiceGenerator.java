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

import static org.soitoolkit.tools.generator.model.impl.ModelUtil.initialLowerCase;

import java.io.PrintStream;

import org.soitoolkit.tools.generator.Generator;
import org.soitoolkit.tools.generator.GeneratorUtil;
import org.soitoolkit.tools.generator.model.IModel;
import org.soitoolkit.tools.generator.model.enums.MuleVersionEnum;
import org.soitoolkit.tools.generator.model.enums.TransformerEnum;
import org.soitoolkit.tools.generator.model.enums.TransportEnum;

public class AggregatingServiceGenerator implements Generator {

    GeneratorUtil guParent;
    GeneratorUtil guSchema;
    GeneratorUtil guImplementation;
	GeneratorUtil guTestStub;
	
	public AggregatingServiceGenerator(PrintStream ps, String domainId, String artifactId, String version, MuleVersionEnum muleVersion, String outputFolder, boolean genSchema, String schemaArtifactId, String schemaTopFolder) {
        guParent         = createGeneratorUtil(ps, domainId, artifactId, version, muleVersion, "/aggregatingService/parent"    , outputFolder, null                    , schemaArtifactId, schemaTopFolder);
        guSchema         = createGeneratorUtil(ps, domainId, artifactId, version, muleVersion, "/aggregatingService/icSchema"  , outputFolder, artifactId + "-schemas" , schemaArtifactId, schemaTopFolder);
        guImplementation = createGeneratorUtil(ps, domainId, artifactId, version, muleVersion, "/aggregatingService/ic"        , outputFolder, artifactId              , schemaArtifactId, schemaTopFolder);
		guTestStub       = createGeneratorUtil(ps, domainId, artifactId, version, muleVersion, "/aggregatingService/icTestStub", outputFolder, artifactId + "-teststub", schemaArtifactId, schemaTopFolder);
	}

	public GeneratorUtil createGeneratorUtil(PrintStream ps, String domainId, String artifactId, String version, MuleVersionEnum muleVersion, String templateFolder, String outputFolder, String outputFolderSuffix, String schemaArtifactId, String schemaTopFolder) {
        String groupId = "se.skltp.aggregatingservices." + domainId;
        String serviceName = artifactId;

        outputFolder = outputFolder + "/" + domainId + "." + artifactId;
        if (outputFolderSuffix != null) {
            outputFolder += "/" + outputFolderSuffix;
        }

        GeneratorUtil gu = new GeneratorUtil(ps, groupId, artifactId, version, serviceName, muleVersion, TransportEnum.HTTP, TransportEnum.HTTP, TransformerEnum.JAVA, templateFolder, outputFolder);
        IModel m = gu.getModel();
        m.getExt().put("domainId"                        , domainId);
        m.getExt().put("schemaTopFolder"                 , schemaTopFolder);
        m.getExt().put("schemaDomainId"                  , domainId.replace('.', ':'));
        m.getExt().put("schemaGroupId"                   , domainId);
        m.getExt().put("schemaArtifactId"                , schemaArtifactId);
        m.getExt().put("schemaLowercaseArtifactId"       , schemaArtifactId.toLowerCase());
        m.getExt().put("schemaInitialLowercaseArtifactId", initialLowerCase(schemaArtifactId));
        return gu;
	}

    public void startGenerator() {
        startParentGenerator();
        startImplementationGenerator();
    	startTeststubProjectGenerator();
    }

    private void startParentGenerator() {
        IModel m = guParent.getModel();
        guParent.logInfo("\n\nCreate parent: " + m.getGroupId() + " - " + m.getArtifactId() + "\n");
        guParent.generateContentAndCreateFile("pom.xml.gt");
    }

	private void startTeststubProjectGenerator() {
		IModel m = guTestStub.getModel();
		guTestStub.logInfo("\n\nCreate teststub: " + m.getGroupId() + " - " + m.getArtifactId() + "\n");
		guTestStub.generateContentAndCreateFile("pom.xml.gt");
		guTestStub.generateContentAndCreateFile("mule-project.xml.gt");

		guTestStub.generateContentAndCreateFile("src/main/app/__teststubStandaloneProject__-config.xml.gt");
		guTestStub.generateContentAndCreateFile("src/main/app/mule-deploy.properties.gt");

		guTestStub.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/__capitalizedJavaTeststubArtifactId__MuleServer.java.gt");
		guTestStub.generateContentAndCreateFile("src/main/resources/log4j2.xml.gt");
	}

	private void startImplementationGenerator() {
		IModel m = guImplementation.getModel();
        guImplementation.logInfo("\n\nCreate implementation: " + m.getGroupId() + " - " + m.getArtifactId() + "\n");

        guImplementation.generateContentAndCreateFile("pom.xml.gt");
        guImplementation.generateContentAndCreateFile("mule-project.xml.gt");

        guImplementation.generateFolder("src/main/java/__javaPackageFilepath__");
        guImplementation.generateFolder("src/environment");
        guImplementation.generateFolder("flows");
        guImplementation.generateContentAndCreateFileUsingGroovyGenerator(getClass().getResource("GenerateMinimalMflow.groovy"), "flows/__artifactId__-common.mflow");
        
        guImplementation.generateContentAndCreateFile("src/environment/log4j2.xml.gt");

        guImplementation.generateContentAndCreateFile("src/main/java/__javaPackageFilepath__/QueryObjectFactoryImpl.java.gt");
        guImplementation.generateContentAndCreateFile("src/main/java/__javaPackageFilepath__/RequestListFactoryImpl.java.gt");
        guImplementation.generateContentAndCreateFile("src/main/java/__javaPackageFilepath__/ResponseListFactoryImpl.java.gt");
		
        guImplementation.generateContentAndCreateFile("src/main/resources/log4j2.xml.gt");
        guImplementation.generateContentAndCreateFile("src/main/resources/__configPropertyFile__.properties.gt");
        
        guImplementation.generateContentAndCreateFile("src/main/app/__artifactId__-common.xml.gt");
        guImplementation.generateContentAndCreateFile("src/main/app/mule-deploy.properties.gt");
        
        guImplementation.copyContentAndCreateFile("src/test/certs/client-truststore.jks.gt");
        guImplementation.copyContentAndCreateFile("src/test/certs/client.jks.gt");
        guImplementation.copyContentAndCreateFile("src/test/certs/truststore.jks.gt");
        guImplementation.copyContentAndCreateFile("src/test/certs/server.jks.gt");
        guImplementation.copyContentAndCreateFile("src/test/certs/readme.txt.gt");

        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/QueryObjectFactoryTest.java.gt");
        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/RequestListFactoryTest.java.gt");
        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/ResponseListFactoryTest.java.gt");
        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/__capitalizedJavaArtifactId__MuleServer.java.gt");
        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/integrationtest/__capitalizedJavaService__IntegrationTest.java.gt");
        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/integrationtest/__capitalizedJavaService__TestConsumer.java.gt");
        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/integrationtest/__capitalizedJavaService__TestProducer.java.gt");
        guImplementation.generateContentAndCreateFile("src/test/java/__javaPackageFilepath__/integrationtest/__capitalizedJavaService__TestProducerDb.java.gt");
	}
}
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
package se.skltp.tools.agtgen.maven;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.soitoolkit.tools.generator.Generator;
import org.soitoolkit.tools.generator.model.ModelFactory;
import org.soitoolkit.tools.generator.model.enums.MuleVersionEnum;
import se.skltp.tools.agtgen.generator.AggregatingServiceGenerator;

/**
 * Goal for creating an new Integration Component
 *
 * @goal genAS
 * @requiresProject false
 * 
 * @author Magnus Larsson
 */
public class GenAggregatingServiceMojo extends AbstractMojo {

	/**
     * DomainId.
     * @parameter expression="${domainId}" default-value="domain1.sub11.sub111"
     * @required
     */
    private String domainId;

	/**
     * ArtifactId.
     * @parameter expression="${artifactId}" default-value="GetAggregatedSample"
     * @required
     */
    private String artifactId;

	/**
     * Version.
     * @parameter expression="${version}" default-value="1.0.0-RC1-SNAPSHOT"
     * @optional
     */
    private String version;

	/**
     * Mule version.
     * @parameter expression="${muleVersion}" default-value="3.7.0"
     * @optional
     */
    private String muleVersion;

	/**
     * Groovy Model.
     * @parameter expression="${groovyModel}"
     * @optional
     */
    private URL groovyModel;

	/**
     * Location of the output folder.
     * @parameter expression="${outDir}" default-value="."
     * @optional
     */
    private File outDir;

    /**
     * Generate Schema.
     * @parameter expression="${genSchema}" default-value="true"
     * @optional
     */
    private boolean genSchema;

    /**
     * If not generate default schema, whet is the artifactId for the Schema?, e.g. "GetRequestActivities".
     * @parameter expression="${schemaArtifactId}"
     * @optional
     */
    private String schemaArtifactId;

    /**
     * If not generate default schema, then under what top-folder will the schema be found, e.g. "clinicalprocess_healthcond_actoutcome".
     * @parameter expression="${schemaTopFolder}"
     * @optional
     */
    private String schemaTopFolder;

    public void execute() throws MojoExecutionException {

    	
        getLog().info("");
        getLog().info("========================================");
        getLog().info("= Generating a new Aggregating Service =");
        getLog().info("========================================");
        getLog().info("");
        getLog().info("arguments:");
        getLog().info("(change an arg by suppling: -Darg=value):");
        getLog().info("");
        getLog().info("outDir           = \"" + outDir.getPath());
        getLog().info("artifactId       = \"" + artifactId + "\"");
        getLog().info("domainId         = \"" + domainId + "\"");
        getLog().info("version          = \"" + version + "\"");
        getLog().info("muleVersion      = \"" + muleVersion + "\"");
        getLog().info("groovyModel      = \"" + groovyModel + "\"");
        getLog().info("genSchema        = \"" + genSchema + "\"");
        getLog().info("schemaArtifactId = \"" + schemaArtifactId + "\"");
        getLog().info("schemaTopFolder  = \"" + schemaTopFolder + "\"");
        getLog().info("");

        initGroovyModel();

        MuleVersionEnum muleVersionEnum = initMuleVersion(muleVersion);

		Generator g = new AggregatingServiceGenerator(System.out, domainId, artifactId, version, muleVersionEnum, outDir.getPath(), genSchema, schemaArtifactId, schemaTopFolder);

		g.startGenerator();
    }

	private void initGroovyModel() throws MojoExecutionException {
		if (groovyModel != null) {
	    	try {
				ModelFactory.setModelGroovyClass(groovyModel);
			} catch (Exception e) {
				throw new MojoExecutionException("Invalid Groovy model: " + groovyModel, e);
			}
		}
	}

	private MuleVersionEnum initMuleVersion(String muleVersion) throws MojoExecutionException {
		MuleVersionEnum muleVersionEnum = null;
		try {
			muleVersionEnum = MuleVersionEnum.getByLabel(muleVersion);
		} catch (Exception e) {
			throw new MojoExecutionException("Invalid Mule version: " + muleVersion + ", allowed values: " + MuleVersionEnum.allowedLabelValues(), e);
		}
		if (muleVersionEnum == null) {
			throw new MojoExecutionException("Invalid Mule version: " + muleVersion + ", allowed values: " + MuleVersionEnum.allowedLabelValues());
		}
		return muleVersionEnum;
	}
}

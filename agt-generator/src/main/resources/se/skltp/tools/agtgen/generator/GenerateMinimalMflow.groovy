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
package se.skltp.tools.agtgen.generator

import org.soitoolkit.tools.generator.FileGenerator
import org.soitoolkit.tools.generator.model.IModel

public class GenerateMflow implements FileGenerator {
	public String generateFileContent(IModel model) {
    	def writer = new StringWriter()
    	def xml = new groovy.xml.MarkupBuilder(writer)

    	xml.'mule-configuration'(name:model.service, xmlns:'http://www.mulesoft.com/tooling/messageflow') {}

    	writer.toString()
	}
}
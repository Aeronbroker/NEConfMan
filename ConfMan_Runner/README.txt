 *          NEC Europe Ltd. PROPRIETARY INFORMATION
 *
 * This software is supplied under the terms of a license agreement
 * or nondisclosure agreement with NEC Europe Ltd. and may not be
 * copied or disclosed except in accordance with the terms of that
 * agreement. The software and its source code contain valuable
 * trade secrets and confidential information which have to be
 * maintained in confidence.
 * Any unauthorized publication, transfer to third parties or
 * duplication of the object or source code - either totally or in
 * part - is prohibited.
 *
 *
 *      Copyright (c) 2006 NEC Europe Ltd. All Rights Reserved.
 *
 *
 * NEC Europe Ltd. DISCLAIMS ALL WARRANTIES, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE AND THE WARRANTY AGAINST LATENT
 * DEFECTS, WITH RESPECT TO THE PROGRAM AND THE ACCOMPANYING
 * DOCUMENTATION.
 *
 * No Liability For Consequential Damages IN NO EVENT SHALL NEC Europe
 * Ltd., NEC Corporation OR ANY OF ITS SUBSIDIARIES BE LIABLE FOR ANY
 * DAMAGES WHATSOEVER (INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS
 * OF BUSINESS PROFITS, BUSINESS INTERRUPTION, LOSS OF INFORMATION, OR
 * OTHER PECUNIARY LOSS AND INDIRECT, CONSEQUENTIAL, INCIDENTAL,
 * ECONOMIC OR PUNITIVE DAMAGES) ARISING OUT OF THE USE OF OR INABILITY
 * TO USE THIS PROGRAM, EVEN IF NEC Europe Ltd. HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 *     THE ABOVE HEADER MAY NOT BE EXTRACTED OR MODIFIED IN ANY WAY.

-------------------------------------------------------------------------

REQUIREMENT:

 - JAVA: Java 1.7
 - OS: Software was tested on Linux distribution.
 - PostgreSQLServer: any version is compatible (recommended 9.2)
 - CouchDB: recommended from 1.5.1


-------------------------------------------------------------------------


Installation and Configuration Guidelines:

 - (1) Installation of PostgreSQL Server: 
	- (1a) First step will be install the PostgreSQL Server on your computer.
	- (1b) Install PostGIS plugin
	- (1c) Create a PostgreSQL User with Admin Rigth

 - (2) Install CouchDB server

 - (3) Configuration:
	- (3a) Place the "fiwareRelease" somewhere in the file system
	- (3b) Change the configuration file in "fiwareRelease/configurationManager/config/"
		- config.properties: postgres configuration, couchdb configuration
	- (3c) Change entry "dir.config" in "confman/configuration/config.ini" with the absolute path of
		"fiwareRealse" folder 

 - (4) Start the configuration manager with startConfigurationManager.bat or startConfigurationManager.sh

-------------------------------------------------------------------------

Changelog
version 1.0
release: 31.10.2014

-------------------------------------------------------------------------

For contact (NEC):
flavio.cirillo@neclab.eu

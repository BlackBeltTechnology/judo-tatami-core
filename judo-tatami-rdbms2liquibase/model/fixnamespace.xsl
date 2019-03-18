<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	version="1.0">
	
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template match="/">
	    <databaseChangeLog 
	    	xmlns="http://www.liquibase.org/xml/ns/dbchangelog" 
	    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	    	xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
	        <xsl:copy-of select="node()/*"/>
	    </databaseChangeLog>
	</xsl:template>
</xsl:stylesheet>
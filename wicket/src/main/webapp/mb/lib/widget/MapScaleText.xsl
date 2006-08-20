<?xml version="1.0"?>
<xsl:stylesheet xmlns:mb="http://mapbuilder.sourceforge.net/mapbuilder" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:output method="xml" encoding="utf-8"/><xsl:param name="lang">en</xsl:param><xsl:param name="modelId"/><xsl:param name="widgetId"/><xsl:param name="mapScale"/><xsl:param name="mapScaleLabel">scale 1:</xsl:param><xsl:param name="formName">MapScaleTextForm</xsl:param><xsl:template match="/"><div><form name="{$formName}" id="{$formName}" onsubmit="return config.objects.{$widgetId}.submitForm()"><xsl:value-of select="$mapScaleLabel"/><input name="mapScale" type="text" size="10" value="{$mapScale}"/></form></div></xsl:template></xsl:stylesheet>

<?xml version="1.0"?>
<xsl:stylesheet xmlns:wmc="http://www.opengis.net/context" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" exclude-result-prefixes="wmc xlink"><xsl:output method="xml" omit-xml-declaration="yes" encoding="utf-8"/><xsl:strip-space elements="*"/><xsl:param name="lang">en</xsl:param><xsl:param name="title"/><xsl:param name="jsfunction">config.loadModel('</xsl:param><xsl:param name="targetModel"/><xsl:template match="/wmc:ViewContextCollection"><ul><xsl:apply-templates select="wmc:ViewContextReference"/></ul></xsl:template><xsl:template match="wmc:ViewContextReference"><xsl:param name="linkUrl">javascript:<xsl:value-of select="$jsfunction"/><xsl:value-of select="$targetModel"/>','<xsl:value-of select="wmc:ContextURL/wmc:OnlineResource/@xlink:href"/>')</xsl:param><li><a href="{$linkUrl}"><xsl:choose><xsl:when test="wmc:Title/@xml:lang"><xsl:value-of select="wmc:Title[@xml:lang=$lang]"/></xsl:when><xsl:otherwise><xsl:value-of select="wmc:Title"/></xsl:otherwise></xsl:choose></a></li></xsl:template></xsl:stylesheet>

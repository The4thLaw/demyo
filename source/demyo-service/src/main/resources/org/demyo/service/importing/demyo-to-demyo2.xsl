<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="utf-8" indent="yes"/>
	
	<xsl:template match="/library">
		<library>
			<meta>
				<version>
					<xsl:attribute name="demyo"><xsl:value-of select="@demyo-version"/></xsl:attribute>
					<!-- Reset the schema version on conversion -->
					<xsl:attribute name="schema">1</xsl:attribute>
				</version>
				
				<counts>
					<xsl:attribute name="publishers"><xsl:value-of select="publishers/@entries"/></xsl:attribute>
					<xsl:attribute name="collections"><xsl:value-of select="collections/@entries"/></xsl:attribute>
					<xsl:attribute name="bindings"><xsl:value-of select="bindings/@entries"/></xsl:attribute>
					<xsl:attribute name="authors"><xsl:value-of select="authors/@entries"/></xsl:attribute>
					<xsl:attribute name="tags"><xsl:value-of select="tags/@entries"/></xsl:attribute>
					<xsl:attribute name="images"><xsl:value-of select="images/@entries"/></xsl:attribute>
					<xsl:attribute name="series"><xsl:value-of select="series/@entries"/></xsl:attribute>
					<xsl:attribute name="albums"><xsl:value-of select="albums/@entries"/></xsl:attribute>
					<xsl:attribute name="derivative_types"><xsl:value-of select="derivative_types/@entries"/></xsl:attribute>
					<xsl:attribute name="sources"><xsl:value-of select="sources/@entries"/></xsl:attribute>
					<xsl:attribute name="derivatives"><xsl:value-of select="derivatives/@entries"/></xsl:attribute>
					<xsl:attribute name="borrowers"><xsl:value-of select="borrowers/@entries"/></xsl:attribute>
					<xsl:attribute name="loan-history"><xsl:value-of select="loan-history/@entries"/></xsl:attribute>
					<xsl:attribute name="albums_prices"><xsl:value-of select="album_prices/@entries"/></xsl:attribute>
					<xsl:attribute name="derivatives_prices"><xsl:value-of select="derivative_prices/@entries"/></xsl:attribute>
				</counts>
			</meta>
			<xsl:apply-templates select="images"/>
			<xsl:apply-templates select="publishers"/>
			<xsl:apply-templates select="collections"/>
			<xsl:apply-templates select="bindings"/>
			<xsl:apply-templates select="authors"/>
			<xsl:apply-templates select="tags"/>
			<xsl:apply-templates select="series"/>
			<xsl:apply-templates select="albums"/>
			<xsl:apply-templates select="album_prices"/>
			<xsl:apply-templates select="borrowers"/>
			<xsl:apply-templates select="loan-history"/>
			<xsl:apply-templates select="derivative_types"/>
			<xsl:apply-templates select="sources"/>
			<xsl:apply-templates select="derivatives"/>
			<xsl:apply-templates select="derivative_prices"/>
		</library>
	</xsl:template>
	
	
	
	<xsl:template match="/library/images">
		<images>
			<xsl:apply-templates select="image"/>
		</images>
	</xsl:template>
	
	<xsl:template match="/library/images/image">
		<image>
			<!-- Copy all attributes except generated and obsolete ones -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:when test="name() != 'is_local' and name() != 'is_fixed_url'">
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</image>
	</xsl:template>
	
	
	
	<xsl:template match="/library/publishers">
		<publishers>
			<xsl:apply-templates select="publisher"/>
		</publishers>
	</xsl:template>
	
	<xsl:template match="/library/publishers/publisher">
		<publisher>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</publisher>
	</xsl:template>
	
	
	
	<xsl:template match="/library/collections">
		<collections>
			<xsl:apply-templates select="collection"/>
		</collections>
	</xsl:template>
	
	<xsl:template match="/library/collections/collection">
		<collection>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</collection>
	</xsl:template>
	
	
	
	<xsl:template match="/library/bindings">
		<bindings>
			<xsl:apply-templates select="binding"/>
		</bindings>
	</xsl:template>
	
	<xsl:template match="/library/bindings/binding">
		<binding>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			</binding>
	</xsl:template>
	
	
	
	<xsl:template match="/library/authors">
		<authors>
			<xsl:apply-templates select="author"/>
		</authors>
	</xsl:template>
	
	<xsl:template match="/library/authors/author">
		<author>
			<!-- Copy all attributes except generated ones -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:when test="name() != 'meta_name'">
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</author>
	</xsl:template>
	
	
	
	<xsl:template match="/library/tags">
		<tags>
			<xsl:apply-templates select="tag"/>
		</tags>
	</xsl:template>
	
	<xsl:template match="/library/tags/tag">
		<tag>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:when test="name() = 'fgcolor'">
						<xsl:attribute name="fgcolour"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
					<xsl:when test="name() = 'bgcolor'">
						<xsl:attribute name="bgcolour"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</tag>
	</xsl:template>
	
	
	
	<xsl:template match="/library/series">
		<series-list>
			<xsl:apply-templates select="series"/>
		</series-list>
	</xsl:template>
	
	<xsl:template match="/library/series/series">
		<series>
			<!-- Copy all attributes except ones to split -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:when test="name() = 'completed'"><!-- Convert boolean -->
						<xsl:attribute name="{name(.)}"><xsl:value-of select="boolean(number(.))"/></xsl:attribute>
					</xsl:when>
					<xsl:when test="name() != 'related_series'">
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
			<!-- Split related_series into real fields -->
			<xsl:if test="string-length(@related_series)">
				<related_series-list>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit"><xsl:value-of select="@related_series"/></xsl:with-param>
						<xsl:with-param name="elementName">related_series</xsl:with-param>
					</xsl:call-template>
				</related_series-list>
			</xsl:if>
		</series>
	</xsl:template>
	
	
	
	<xsl:template match="/library/albums">
		<albums>
			<xsl:apply-templates select="album"/>
		</albums>
	</xsl:template>
	
	<xsl:template match="/library/albums/album">
		<album>
			<!-- Copy all attributes except ones to split -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="name() = 'title' and not(string-length())">
						<!-- Now, all albums must have a title, even one shots. But they won't have a Series. -->
						<xsl:variable name="series_id" select="../@series_id" />
						<xsl:attribute name="title"><xsl:value-of select="/library/series/series[@id=$series_id]/@name" /></xsl:attribute>
					</xsl:when>
					<xsl:when test="not(string-length(.)) or .='0000-00-00'"><!-- Nothing --></xsl:when>
					<xsl:when test=".='0' and (name() = 'height' or name() = 'width' or name() = 'pages')"><!-- Nothing --></xsl:when>
					<xsl:when test="name() = 'wishlist'"><!-- Convert boolean -->
						<xsl:attribute name="{name(.)}"><xsl:value-of select="boolean(number(.))"/></xsl:attribute>
					</xsl:when>
					<xsl:when test="name() = 'purchase_date'"><!-- Convert name -->
						<xsl:attribute name="acquisition_date"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
					<!-- Ignore to split and borrower -->
					<xsl:when test="name() != 'artists' and name() != 'writers' and name() != 'colorists' and name() != 'tags' and name() != 'images' and name() != 'borrowers'">
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
			<!-- Split compound fields into real fields -->
			<xsl:if test="string-length(@artists)">
				<artists>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit"><xsl:value-of select="@artists"/></xsl:with-param>
						<xsl:with-param name="elementName">artist</xsl:with-param>
					</xsl:call-template>
				</artists>
			</xsl:if>
			<xsl:if test="string-length(@writers)">
				<writers>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit"><xsl:value-of select="@writers"/></xsl:with-param>
						<xsl:with-param name="elementName">writer</xsl:with-param>
					</xsl:call-template>
				</writers>
			</xsl:if>
			<xsl:if test="string-length(@colorists)">
				<colorists>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit"><xsl:value-of select="@colorists"/></xsl:with-param>
						<xsl:with-param name="elementName">colorist</xsl:with-param>
					</xsl:call-template>
				</colorists>
			</xsl:if>
			<xsl:if test="string-length(@tags)">
				<album-tags>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit"><xsl:value-of select="@tags"/></xsl:with-param>
						<xsl:with-param name="elementName">album-tag</xsl:with-param>
					</xsl:call-template>
				</album-tags>
			</xsl:if>
			<xsl:if test="string-length(@images)">
				<album-images>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit"><xsl:value-of select="@images"/></xsl:with-param>
						<xsl:with-param name="elementName">album-image</xsl:with-param>
					</xsl:call-template>
				</album-images>
			</xsl:if>
		</album>
	</xsl:template>
	
	
	
	<xsl:template match="/library/album_prices">
		<albums_prices>
			<xsl:apply-templates select="album_price"/>
		</albums_prices>
	</xsl:template>
	
	<xsl:template match="/library/album_prices/album_price">
		<album_price>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</album_price>
	</xsl:template>
	
	
	
	<xsl:template match="/library/borrowers">
		<borrowers>
			<xsl:apply-templates select="borrower"/>
		</borrowers>
	</xsl:template>
	
	<xsl:template match="/library/borrowers/borrower">
		<borrower>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:when test="name() != 'meta_name'">
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</borrower>
	</xsl:template>
	
	
	
	<xsl:template match="/library/loan-history">
		<loan-history>
			<xsl:apply-templates select="loan"/>
		</loan-history>
	</xsl:template>
	
	<xsl:template match="/library/loan-history/loan">
		<loan>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</loan>
	</xsl:template>
	
	
	
	<xsl:template match="/library/derivative_types">
		<derivative_types>
			<xsl:apply-templates select="derivative_type"/>
		</derivative_types>
	</xsl:template>
	
	<xsl:template match="/library/derivative_types/derivative_type">
		<derivative_type>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</derivative_type>
	</xsl:template>
	
	
	
	<xsl:template match="/library/sources">
		<sources>
			<xsl:apply-templates select="source"/>
		</sources>
	</xsl:template>
	
	<xsl:template match="/library/sources/source">
		<source>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</source>
	</xsl:template>
	
	
	
	<xsl:template match="/library/derivatives">
		<derivatives>
			<xsl:apply-templates select="derivative"/>
		</derivatives>
	</xsl:template>
	
	<!-- ignore meta_name="Sérigraphie pour Amenophis IV, de Durango", split images="94"/ -->
	<xsl:template match="/library/derivatives/derivative">
		<derivative>
			<!-- Copy all attributes except ones to split -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.)) or .='0000-00-00'"><!-- Nothing --></xsl:when>
					<xsl:when test=".='0' and (name() = 'width' or name() = 'height' or name() = 'depth')"><!-- Nothing --></xsl:when>
					<xsl:when test="name() = 'signed' or name() = 'authors_copy' or name() = 'restricted_sale'"><!-- Convert boolean -->
						<xsl:attribute name="{name(.)}"><xsl:value-of select="boolean(number(.))"/></xsl:attribute>
					</xsl:when>
					<xsl:when test="name() = 'purchase_date'"><!-- Convert name -->
						<xsl:attribute name="acquisition_date"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
					<!-- Ignore meta, to split, and obsolete -->
					<xsl:when test="name() != 'meta_name' and name() != 'images' and name() != 'price'">
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
			<!-- Split compound fields into real fields -->
			<xsl:if test="string-length(@images)">
				<derivative-images>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit"><xsl:value-of select="@images"/></xsl:with-param>
						<xsl:with-param name="elementName">derivative-image</xsl:with-param>
					</xsl:call-template>
				</derivative-images>
			</xsl:if>
		</derivative>
	</xsl:template>
	
	
	
	<xsl:template match="/library/derivative_prices">
		<derivatives_prices>
			<xsl:apply-templates select="derivative_price"/>
		</derivatives_prices>
	</xsl:template>
	
	<xsl:template match="/library/derivative_prices/derivative_price">
		<derivative_price>
			<!-- Copy all attributes -->
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="not(string-length(.))"><!-- Nothing --></xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</derivative_price>
	</xsl:template>
	
	
	
	<!--http://stackoverflow.com/questions/4845660/xsl-how-to-split-strings-->
	<xsl:template name="split-id-list">
		<xsl:param name="toSplit" select="."/>
		<xsl:param name="elementName"/>
		<xsl:if test="string-length($toSplit)">
			<xsl:choose>
				<xsl:when test="string-length(substring-before($toSplit,','))">
					<xsl:element name="{$elementName}">
						<xsl:attribute name="ref"><xsl:value-of select="substring-before($toSplit,',')"/></xsl:attribute>
					</xsl:element>
					<xsl:call-template name="split-id-list">
						<xsl:with-param name="toSplit" select="substring-after($toSplit, ',')"/>
						<xsl:with-param name="elementName"><xsl:value-of select="$elementName"/></xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="{$elementName}">
						<xsl:attribute name="ref"><xsl:value-of select="$toSplit"/></xsl:attribute>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>

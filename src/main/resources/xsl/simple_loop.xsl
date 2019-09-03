<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gml="http://graphml.graphdrawing.org/xmlns" xmlns:y="http://www.yworks.com/xml/graphml">
  

	<xsl:output method="text" encoding="UTF-8" indent="no"/>
	<xsl:strip-space elements="*"/>
	
<xsl:variable name="documentclass"><xsl:text>article</xsl:text></xsl:variable>

<xsl:variable name="tikzpicture_params">
<xsl:text>Vertex/.style={circle, draw=none, fill=VertexColor, minimum size=\Scaling*1.95cm},
every label/.style={rectangle, fill=VertexColor, align=center, minimum width=0.45cm, inner sep=2, text depth=0.25ex, text height=1.25ex, font=\scriptsize\ttfamily, text=LabelTextColor},
x=\Scaling,y=\Scaling,
&gt;=Stealth, toploop/.style={in=280, out=260, loop, shorten >=16, min distance=30},</xsl:text>
</xsl:variable>

<xsl:variable name="customization">
<xsl:text>
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Quick Customization:
\colorlet{EdgeColor}{SeminarBlau}
\colorlet{VertexColor}{SeminarHellGruen}
\colorlet{LabelTextColor}{black}
\newcommand*{\Scaling}{0.3}
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
&#xa;
</xsl:text>
</xsl:variable>

<xsl:variable name="usepackages">
<xsl:text>\usepackage{xcolor}
\usepackage{tikz}
\usetikzlibrary{topaths}
\usetikzlibrary{arrows.meta}
\usetikzlibrary{backgrounds}
\usepackage[active,tightpage]{preview}
\PreviewEnvironment{tikzpicture}
</xsl:text>
</xsl:variable>

<xsl:variable name="colordefinitions">
<xsl:text>
%% actual Seminar-style color:
\definecolor{GoetheOrangePlakativ}{RGB}{237,167,45} % Pantone 151
\definecolor{SeminarBlau}{RGB}{0,154,224}
\definecolor{SeminarRot}{rgb}{0.75,0,0}
\definecolor{SeminarGruen}{RGB}{0,128,0}
\definecolor{SeminarOrange}{RGB}{237,167,45}
\definecolor{SeminarGrau}{rgb}{0.32,0.3,0.38}
\colorlet{SeminarHellBlau}{SeminarBlau!25!white}
\colorlet{SeminarBlauGruen}{SeminarBlau!50!SeminarGruen}
\colorlet{SeminarGruenBlau}{SeminarBlau!50!SeminarGruen}
\colorlet{SeminarMix}{SeminarBlau!50!SeminarGruen}
\colorlet{SeminarHellGruen}{SeminarGruen!25!white}
\colorlet{SeminarHellRot}{SeminarRot!25!white}
\colorlet{SeminarHellOrange}{GoetheOrangePlakativ!25!white}
\colorlet{SeminarHellMix}{SeminarMix!25!white}
\colorlet{SeminarHellBlauGruen}{SeminarMix!25!white}
\colorlet{SeminarHellGruenBlau}{SeminarMix!25!white}
\colorlet{SeminarRotOrange}{SeminarRot!50!GoetheOrangePlakativ}
\colorlet{SeminarOrangeRot}{SeminarRot!50!GoetheOrangePlakativ}
\colorlet{SeminarHellOrangeRot}{SeminarOrangeRot!25!white}
\colorlet{SeminarHellRotOrange}{SeminarOrangeRot!25!white}
\colorlet{SeminarRotGruen}{SeminarRot!50!SeminarGruen}
\colorlet{SeminarGruenRot}{SeminarRot!50!SeminarGruen}
\colorlet{SeminarRotBlau}{SeminarRot!50!SeminarBlau}
\colorlet{SeminarBlauRot}{SeminarRot!50!SeminarBlau}
\colorlet{SeminarHellRotBlau}{SeminarRotBlau!25!white}
\colorlet{SeminarHellBlauRot}{SeminarRotBlau!25!white}
\colorlet{SeminarHellRotGruen}{SeminarRotGruen!25!white}
\colorlet{SeminarHellGruenRot}{SeminarRotGruen!25!white}
\colorlet{SeminarHellGrau}{SeminarGrau!35!white}
\colorlet{SeminarOrangeGruen}{SeminarOrange!50!SeminarGruen}
\colorlet{SeminarGruenOrange}{SeminarOrange!50!SeminarGruen}
\colorlet{SeminarHellGruenOrange}{SeminarOrangeGruen!25!white}
\colorlet{SeminarHellOrangeGruen}{SeminarOrangeGruen!25!white}
\colorlet{SeminarOrangeBlau}{SeminarOrange!50!SeminarBlau}
\colorlet{SeminarBlauOrange}{SeminarOrange!50!SeminarBlau}
\colorlet{SeminarHellBlauOrange}{SeminarOrangeBlau!25!white}
\colorlet{SeminarHellOrangeBlau}{SeminarOrangeBlau!25!white}
\colorlet{SeminarSehrHellRot}{SeminarRot!15!white}
\colorlet{SeminarSehrHellGrau}{SeminarGrau!15!white}
\colorlet{SeminarSehrHellGruen}{SeminarGruen!15!white}
\colorlet{SeminarSehrHellBlau}{SeminarBlau!15!white}
\colorlet{SeminarSehrHellOrange}{SeminarHellOrange!15!white}
\colorlet{SeminarOrangeGrau}{SeminarOrange!50!SeminarGrau}
\colorlet{SeminarGrauOrange}{SeminarOrange!50!SeminarGrau}
\colorlet{SeminarRotGrau}{SeminarRot!50!SeminarGrau}
\colorlet{SeminarGrauRot}{SeminarRot!50!SeminarGrau}
\colorlet{SeminarBlauGrau}{SeminarBlau!50!SeminarGrau}
\colorlet{SeminarGrauBlau}{SeminarBlau!50!SeminarGrau}
\colorlet{SeminarGruenGrau}{SeminarGruen!50!SeminarGrau}
\colorlet{SeminarGrauGruen}{SeminarGruen!50!SeminarGrau}
\colorlet{SeminarHellGrauRot}{SeminarGrauRot!25!white}
\colorlet{SeminarHellRotGrau}{SeminarGrauRot!25!white}
\colorlet{SeminarHellGrauBlau}{SeminarGrauBlau!25!white}
\colorlet{SeminarHellBlauGrau}{SeminarGrauBlau!25!white}
\colorlet{SeminarHellGrauGruen}{SeminarGrauGruen!25!white}
\colorlet{SeminarHellGruenGrau}{SeminarGrauGruen!25!white}
\colorlet{SeminarHellGrauOrange}{SeminarGrauOrange!25!white}
\colorlet{SeminarHellOrangeGrau}{SeminarGrauOrange!25!white}
</xsl:text>
</xsl:variable>


	<!-- Template to Build Output -->
	<xsl:template match="/">		
		<!-- DocumentClass Definition -->
		<xsl:text>\documentclass{</xsl:text><xsl:value-of select="$documentclass"/><xsl:text>}&#xa;</xsl:text>
				
		<!-- Include Variables -->
		<xsl:value-of select="$usepackages"/>				
		<xsl:value-of select="$colordefinitions"/>		
		<xsl:value-of select="$customization"/>		
						
		<xsl:text>\begin{document}&#xa;</xsl:text>		
		
		<xsl:text>\begin{tikzpicture}[</xsl:text>
		<xsl:value-of select="$tikzpicture_params"/>
		<xsl:text>]&#xa;</xsl:text>
		<xsl:text>&#xa;\pgftransformyscale{-1}&#xa;&#xa;</xsl:text>
		
		<xsl:for-each select="gml:graphml/gml:graph/gml:node">	
			<xsl:text>\node (</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text>) [at={(</xsl:text>
			<xsl:value-of select="gml:data/*/y:Geometry/@x"/>
			<xsl:text>,</xsl:text>			
			<xsl:value-of select="gml:data/*/y:Geometry/@y"/>
			<xsl:text>)},  Vertex, label={center:</xsl:text>
			<xsl:value-of select="gml:data/*/."/>
			<xsl:text>}]{};</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>

		<xsl:text>&#xa;\begin{scope}[on background layer]&#xa;</xsl:text>
		<xsl:text>\begin{scope}[EdgeColor]&#xa;</xsl:text>
		<xsl:for-each select="gml:graphml/gml:graph/gml:edge">	
			<xsl:text>\path [->,line width=\Scaling*</xsl:text>
			<xsl:value-of select="gml:data/y:PolyLineEdge/y:LineStyle/@width"/>
			<xsl:choose>
				<xsl:when test="string(@source) = string(@target)">
					<xsl:text>,draw,](</xsl:text>
					<xsl:value-of select="@source"/>
					<xsl:text>) edge [toploop] (</xsl:text>
					<xsl:value-of select="@target"/>
					<xsl:text>);</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>,draw,](</xsl:text>
					<xsl:value-of select="@source"/>
					<xsl:text>) to (</xsl:text>
					<xsl:value-of select="@target"/>
					<xsl:text>);</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
		<xsl:text>\end{scope}&#xa;</xsl:text>
		<xsl:text>\end{scope}&#xa;</xsl:text>
		<xsl:text>\end{tikzpicture}&#xa;</xsl:text>
		<xsl:text>\end{document}&#xa;</xsl:text>
	</xsl:template>
  
</xsl:stylesheet>

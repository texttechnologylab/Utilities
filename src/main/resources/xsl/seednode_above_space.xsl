<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gml="http://graphml.graphdrawing.org/xmlns" xmlns:y="http://www.yworks.com/xml/graphml" xmlns:math="http://exslt.org/math">
  

<xsl:output method="text" encoding="UTF-8" indent="no"/>
<xsl:strip-space elements="*"/>

<!-- Select Node Attribute which denotes the centroid -->	
<xsl:variable name="centroidattr_id" select="/gml:graphml/gml:key[@attr.name='centroid']/@id" />
<xsl:variable name="seednode_id" select="/gml:graphml/gml:graph/gml:node/gml:data[@key=$centroidattr_id and .='true']/../@id" />	

<xsl:variable name="node_xmin" select="math:min(/gml:graphml/gml:graph/gml:node/gml:data/y:ShapeNode/y:Geometry/@x)" />	
<xsl:variable name="node_xmax" select="math:max(/gml:graphml/gml:graph/gml:node/gml:data/y:ShapeNode/y:Geometry/@x)" />	
<xsl:variable name="node_ymin" select="math:min(/gml:graphml/gml:graph/gml:node/gml:data/y:ShapeNode/y:Geometry/@y)" />	
<xsl:variable name="node_ymax" select="math:max(/gml:graphml/gml:graph/gml:node/gml:data/y:ShapeNode/y:Geometry/@y)" />	

<xsl:variable name="indent_top"><xsl:text>100</xsl:text></xsl:variable>	
<xsl:variable name="indent_left"><xsl:text>100</xsl:text></xsl:variable>	
<xsl:variable name="indent_bottom"><xsl:text>100</xsl:text></xsl:variable>	
<xsl:variable name="indent_right"><xsl:text>100</xsl:text></xsl:variable>	
	
<xsl:variable name="documentclass"><xsl:text>article</xsl:text></xsl:variable>

<xsl:variable name="tikzpicture_params">
<xsl:text>Vertex/.style={ellipse, draw=SeminarGruen, line width=2.0, fill=white, minimum width=\Scaling*1.75cm},
Seed/.style={ellipse, draw=SeminarGrau!70, line width=2.0, fill=SeminarOrange, minimum width=\Scaling*2cm},
every label/.style={rectangle callout, rounded corners, draw=SeminarGruen, fill=SeminarSehrHellGruen, text=black, callout relative pointer={(270:0.3cm)}, align=center, minimum width=0.7cm, inner sep=\Scaling*2.5ex, font=\footnotesize\ttfamily},
SeedText/.style={rectangle callout, draw=SeminarGrau!70, fill=SeminarOrange, align=center, minimum width=1cm, font=\ttfamily, text=black, callout relative pointer={(270:0.3cm)}},
every to/.style={bend left},
x=\Scaling,y=\Scaling,
&gt;=latex
</xsl:text>
</xsl:variable>

<xsl:variable name="usepackages">
<xsl:text>\usepackage{xcolor}
\usepackage{tikz}
\usepackage[active,tightpage]{preview}
\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}
\usepackage{lmodern}
</xsl:text>
</xsl:variable>

<xsl:variable name="usetikzlibraries">
<xsl:text>\usetikzlibrary{topaths}
\usetikzlibrary{arrows}
\usetikzlibrary{backgrounds}
\usetikzlibrary{shapes.callouts}
\usetikzlibrary{shapes.geometric}
\usetikzlibrary{fit}
</xsl:text>
</xsl:variable>


<xsl:variable name="colordefinitions">
<xsl:text>
%% old ones for compatibility reasons:
\definecolor{LNshadowblue}{RGB}{112,159,203}
\definecolor{LNlightblue}{RGB}{153,196,236}
\definecolor{LNultralightblue}{RGB}{229,240,250}
\definecolor{LNgreen}{RGB}{153,204,51}
\definecolor{GoetheOrange}{RGB}{243,192,57} % Pantone 143
\definecolor{GoetheOrangePlakativ}{RGB}{237,167,45} % Pantone 151
\definecolor{Papier}{RGB}{233,234,192}
\definecolor{agttlightSeminarGrau}{RGB}{219,220,222}
\definecolor{bottomcolor}{rgb}{0.32,0.3,0.38}
\definecolor{middlecolor}{rgb}{0.08,0.08,0.16}
\definecolor{loeweblau}{rgb}{0.33,0.57,0.84}
% \definecolor{loeweblau}{RGB}{84,145,214}
\definecolor{loewehellblau}{RGB}{219,238,244}
\definecolor{loewegruen}{RGB}{90,162,80}
\definecolor{loeweorange}{RGB}{220,150,55}
\definecolor{DigihumRot}{RGB}{212,0,0}
\definecolor{DigihumBlau}{RGB}{0,94,170}
\definecolor{DigihumHellBlau}{RGB}{221,240,255}
\definecolor{DigihumText}{RGB}{103,102,102}
\definecolor{DigihumHellGrau}{RGB}{214,214,214}
\colorlet{loewegrau}{DigihumText!83!white}
%%
%% actual Seminar-style color:
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

<xsl:variable name="newcommands">
<xsl:text>\newcommand*{\Scaling}{0.4}
\newcommand*{\LineScaling}{2}
\renewcommand{\familydefault}{\sfdefault}
</xsl:text>
</xsl:variable>

<xsl:variable name="predocument_misc_settings">
<xsl:text>\PreviewEnvironment{tikzpicture}
</xsl:text>
</xsl:variable>
	
	<!-- Template to Build Output -->
	<xsl:template match="/">			
		<!-- DocumentClass Definition -->
		<xsl:text>\documentclass{</xsl:text><xsl:value-of select="$documentclass"/><xsl:text>}&#xa;</xsl:text>
				
		<!-- Include Variables -->
		<xsl:value-of select="$usepackages"/>		
		<xsl:value-of select="$usetikzlibraries"/>				
		<xsl:value-of select="$colordefinitions"/>		
		<xsl:value-of select="$newcommands"/>		
		
		<!-- Include other settings which should be put before \begin{document} -->
		<xsl:value-of select="$predocument_misc_settings"/>
				
		<xsl:text>\begin{document}&#xa;</xsl:text>
				
		<xsl:text>\begin{tikzpicture}[</xsl:text>
		<xsl:value-of select="$tikzpicture_params"/>
		<xsl:text>]&#xa;</xsl:text>
				
		
		<xsl:for-each select="gml:graphml/gml:graph/gml:node">	
			<xsl:choose>
				<xsl:when test="gml:data[@key=$centroidattr_id]/.='true'">
					<xsl:text>\node (</xsl:text>
					<xsl:value-of select="@id"/>
					<xsl:text>) [at={(</xsl:text>
					<xsl:value-of select="gml:data/y:ShapeNode/y:Geometry/@x"/>
					<xsl:text>pt,</xsl:text>			
					<xsl:value-of select="gml:data/y:ShapeNode/y:Geometry/@y"/>
					<xsl:text>pt)},  Seed, label={[SeedText]above:</xsl:text>
					<xsl:value-of select="gml:data/y:ShapeNode/."/>
					<xsl:text>}]{};</xsl:text>
					<xsl:text>&#xa;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>\node (</xsl:text>
					<xsl:value-of select="@id"/>
					<xsl:text>) [at={(</xsl:text>
					<xsl:value-of select="gml:data/y:ShapeNode/y:Geometry/@x"/>
					<xsl:text>pt,</xsl:text>			
					<xsl:value-of select="gml:data/y:ShapeNode/y:Geometry/@y"/>
					<xsl:text>pt)},  Vertex, label={above:</xsl:text>
					<xsl:value-of select="gml:data/y:ShapeNode/."/>
					<xsl:text>}]{};</xsl:text>
					<xsl:text>&#xa;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>								
		</xsl:for-each>

		<xsl:text>\begin{scope}[on background layer]&#xa;</xsl:text>
		<xsl:text>\node (Left) [at={(</xsl:text><xsl:value-of select="$node_xmin - $indent_left"/><xsl:text>pt,</xsl:text><xsl:value-of select="($node_ymax - $node_ymin) div 2"/><xsl:text>pt)}] {};&#xa;</xsl:text>
		<xsl:text>\node (Right) [at={(</xsl:text><xsl:value-of select="$node_xmax + $indent_right"/><xsl:text>pt,</xsl:text><xsl:value-of select="($node_ymax - $node_ymin) div 2"/><xsl:text>pt)}] {};&#xa;</xsl:text>
		<xsl:text>\node (Top) [at={(</xsl:text><xsl:value-of select="($node_xmax - $node_xmin) div 2"/><xsl:text>pt,</xsl:text><xsl:value-of select="$node_ymin - $indent_top"/><xsl:text>pt)}] {};&#xa;</xsl:text>
		<xsl:text>\node (Bottom) [at={(</xsl:text><xsl:value-of select="($node_xmax - $node_xmin) div 2"/><xsl:text>pt,</xsl:text><xsl:value-of select="$node_ymax + $indent_bottom"/><xsl:text>pt)}] {};&#xa;</xsl:text>
		<xsl:text>\node [rectangle, fit=(Left) (Bottom) (Right) (Top), fill=black] (X) {};&#xa;</xsl:text>
		<xsl:text>\draw [top color=black, bottom color=SeminarSehrHellGrau] (</xsl:text><xsl:value-of select="$seednode_id"/><xsl:text>) -- ([yshift=</xsl:text><xsl:value-of select="0 - ($node_ymax - $node_ymin) div 4"/><xsl:text>pt]X.west) -- (X.south west) -- (X.south east) -- ([yshift=</xsl:text><xsl:value-of select="0 - ($node_ymax - $node_ymin) div 4"/><xsl:text>pt]X.east) -- cycle;&#xa;</xsl:text>

		<xsl:text>\begin{scope}[white]&#xa;</xsl:text>
		<xsl:for-each select="gml:graphml/gml:graph/gml:edge[@source!=$seednode_id]">	
			<xsl:text>\path [->,line width=\LineScaling*</xsl:text>
			<xsl:value-of select="gml:data/y:PolyLineEdge/y:LineStyle/@width"/>
			<xsl:text>pt,draw,](</xsl:text>
			<xsl:value-of select="@source"/>
			<xsl:text>) to (</xsl:text>
			<xsl:value-of select="@target"/>
			<xsl:text>);</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
		
		<xsl:text>\end{scope}&#xa;</xsl:text>
		<xsl:text>\begin{scope}[SeminarOrange]&#xa;</xsl:text>
		
		<xsl:for-each select="gml:graphml/gml:graph/gml:edge[@source=$seednode_id]">	
			<xsl:text>\path [->,line width=\LineScaling*</xsl:text>
			<xsl:value-of select="gml:data/y:PolyLineEdge/y:LineStyle/@width"/>
			<xsl:text>pt,draw,](</xsl:text>
			<xsl:value-of select="@source"/>
			<xsl:text>) to (</xsl:text>
			<xsl:value-of select="@target"/>
			<xsl:text>);</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
		
		<xsl:text>\end{scope}&#xa;</xsl:text>
		<xsl:text>\end{scope}&#xa;</xsl:text>
		
		<xsl:text>\end{tikzpicture}&#xa;</xsl:text>
		<xsl:text>\end{document}&#xa;</xsl:text>
	</xsl:template>
  
</xsl:stylesheet>

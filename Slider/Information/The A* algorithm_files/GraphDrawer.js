/**
 * Die Farben, die im Projekt genutzt werden.
 * Aus dem TUM Styleguide.
 * @type Object 
 */
var const_Colors = {
    NodeFilling : "#98C6EA", // Pantone 283, 100%
    NodeBorder : "#0065BD", // Pantone 300, 100%, "TUM-Blau"
    NodeBorderHighlight : "#C4071B", // Helles Rot 100% aus TUM Styleguide
    NodeFillingHighlight : "#73B78D", // Dunkelgrün 55 % aus TUM Styleguide
    NodeFillingQuestion : "#C4071B", // Helles Rot 100% aus TUM Styleguide
    EdgeHighlight1 : "#C4071B", // Helles Rot 100% aus TUM Styleguide
    EdgeHighlight2 : "#73B78D", // Dunkelgrün 55 % aus TUM Styleguide
    EdgeHighlight3 : "#73B78D", // Dunkelgrün 55 % aus TUM Styleguide
    EdgeHighlight4: "#007C30", // Dunkelgrün 100 % aus TUM Styleguide
    EdgeHighlight5: "#55B560", // Used for showing MST
    RedText : "#C4071B", // Helles Rot 100% aus TUM Styleguide
    GreenText : "#007C30", // Dunkelgrün 100 % aus TUM Styleguide
    PQColor : "#FFFF70", // Helles Gelb
    StartNodeColor : "#33CC33", // Dunklgrün
    StartEndNodeColor : "#00983D", // Dunklgrün
    CurrentNodeColor : "#C4071B", // Helles Rot
    FinishedNodeColor : "#73B78D", // Wie EdgeHighlight2
    ShortestPathColor : "#73B78D", // Wie EdgeHighlight2
    UnusedEdgeColor : "#0065BD", // Wie NodeBorder
    NormalEdgeColor : "#000000" // Schwarz
};

/**
 * Standardgröße eines Knotens
 * @type Number
 */
var global_KnotenRadius = 20;                           // Radius der Knoten
/**
 * Standardaussehen einer Kante.
 * @type Object
 */
var global_Edgelayout = {'arrowAngle' : Math.PI/8,	         // Winkel des Pfeilkopfs relativ zum Pfeilkörper
			             'arrowHeadLength' : 15,             // Länge des Pfeilkopfs
                         'lineColor' : "black",		         // Farbe des Pfeils
			             'lineWidth' : 2,		             // Dicke des Pfeils
                         'font'	: 'Arial',		             // Schrifart 
                         'fontSize' : 14,		             // Schriftgrösse in Pixeln
                         'isHighlighted': false,             // Ob die Kante eine besondere Markierung haben soll
                         'progressArrow': false,             // Zusätzlicher Animationspfeil 
                         'progressArrowPosition': 0.0,       // Position des Animationspfeils
                         'progressArrowSource': null,        // Animationspfeil Source Knoten
                         'progressArrowTarget': null         // Animationspfeil Target Knoten
			};
                        
/**
 * Standardaussehen eines Knotens.
 * @type Object
 */
var global_NodeLayout = {'fillStyle' : const_Colors.NodeFilling,    // Farbe der Füllung
                         'nodeRadius' : 15,                         // Radius der Kreises
                         'borderColor' : const_Colors.NodeBorder,   // Farbe des Rands (ohne Markierung)
                         'borderWidth' : 2,                         // Breite des Rands
                         'fontColor' : 'black',                     // Farbe der Schrift
                         'font' : 'bold',                           // Schriftart
                         'fontSize' : 14                            // Schriftgrösse in Pixeln
                        };

function translate(x,y){
    return "translate("+x+","+y+")";
}

var GraphDrawer = function(svgOrigin,extraMargin,transTime, tabID){

    /////////////////
    //PRIVATE

    var transTime = (transTime!=null) ? transTime : 250;

    var extraMargin = extraMargin || {};

    var xRange = +svgOrigin.attr("width") || 400;
        yRange = +svgOrigin.attr("height") || 300;
    var wS = global_NodeLayout['borderWidth'];
    
    var margin = {
            top: global_KnotenRadius+wS+ (extraMargin.top || 10),
            right: global_KnotenRadius+wS,
            bottom: global_KnotenRadius+wS,
            left: global_KnotenRadius+wS +(extraMargin.left || 0)}

        width = xRange - margin.left - margin.right,
        height = yRange - margin.top - margin.bottom;

    this.height = height;
    this.width = width;

    this.margin = margin;

    var radius = global_KnotenRadius;

    svgOrigin
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)

    var svg = svgOrigin.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    this.svg=svg;

    var svg_links=svg.append("g").attr("id", "edges");
    var svg_nodes=svg.append("g").attr("id", "nodes");

    this.getScreenCoordinates = function(node) {
        return { x: xfun(node) + margin.left, y: yfun(node) + margin.top};
    }

    this.x = d3.scale.linear()
        .range([margin.left, width-margin.right])
        .domain([0,xRange]);

    this.y = d3.scale.linear()
        .range([height-margin.top, margin.bottom])
        .domain([0,yRange]);

    var transform = function(d){
        xRange = +svgOrigin.attr("width") || 400;
        yRange = +svgOrigin.attr("height") || 300;
        if (this.x(this.nodeX(d)) < 0) {
            var x1 = this.nodeX(d);
            var x2 = this.x(x1);
        }
        return translate(this.x(this.nodeX(d)),this.y(this.nodeY(d)));
    }
    transform = transform.bind(this);

    this.squeeze = function(){
        var nodes;

        if(Graph.instance && (nodes = Graph.instance.getNodes())){
            this.x.domain(d3.extent(nodes, function(d) { return d.x; }));
            this.y.domain(d3.extent(nodes, function(d) { return d.y; }));
        }
    }

    //somehow we get old copies of nodes in d where the state is outdated
    //-> workaround: get the correct node from the Graph instance using its id
    var xfun = function(d){
        return this.x(this.nodeX(Graph.instance.nodes.get(d.id) || d));
    }

    var yfun = function(d){
        return this.y(this.nodeY(Graph.instance.nodes.get(d.id) || d));
    }

    xfun = xfun.bind(this);

    yfun = yfun.bind(this);

    this.createPathString = function(d, a, b, transf) {
        var edge = Graph.instance.edges.get(d.id);
        var line = lineAttribs(d, a, b);
        var control = getControlPoint(edge, line);
        if (transf) {
            var s = this.getScreenCoordinates(edge.start);
            var t = this.getScreenCoordinates(edge.end);
            line = {x1: s.x, y1: s.y, x2: t.x, y2: t.y};
            if (control) {
                control.x += margin.left;
                control.y += margin.top;
            }
        }
        var pathString = "";
        if (control == null) {
            pathString += "M " + line.x1 + " " + line.y1 + " L " + line.x2 + " " + line.y2;
        }
        else {
            pathString += "M " + line.x1 + " " + line.y1 + " Q " + control.x + " " + control.y + " " + line.x2 + " " + line.y2;
        }
        return pathString;
    }

    this.createHiddenPathString = function(d, a, b, transf) {
        var edge = Graph.instance.edges.get(d.id);
        var line = lineAttribs(d, a, b);
        var control = getControlPoint(edge, line);
        if (transf) {
            var s = this.getScreenCoordinates(edge.start);
            var t = this.getScreenCoordinates(edge.end);
            line = {x1: s.x, y1: s.y, x2: t.x, y2: t.y};
            if (control) {
                control.x += margin.left;
                control.y += margin.top;
            }
        }
        var pathString = "";
        // If x-value of start nodes is less than x-value of end node, everything is fine.
        if (edge.start.x < edge.end.x) {
            if (control == null) {
                pathString += "M " + line.x1 + " " + line.y1 + " L " + line.x2 + " " + line.y2;
            }
            else {
                pathString += "M " + line.x1 + " " + line.y1 + " Q " + control.x + " " + control.y + " " + line.x2 + " " + line.y2;
            }
        }
        // If not, we traverse the path in the opposite direction. This ensures that the edge label is not displayed upside down.
        else {
            if (control == null) {
                pathString += "M " + line.x2 + " " + line.y2 + " L " + line.x1 + " " + line.y1;
            }
            else {
                pathString += "M " + line.x2 + " " + line.y2 + " Q " + control.x + " " + control.y + " " + line.x1 + " " + line.y1;
            }
        }
        return pathString;
    }

    function setPathString(d, a, b) {
        var pathString = that.createPathString(d, a, b, false);
        if(transTime) d3.select(this).transition().duration(transTime).attr("d", pathString);
        else d3.select(this).attr("d", pathString);
    }

    function setHiddenPathString(d, a, b) {
        var pathString = that.createHiddenPathString(d, a, b, false);
        if(transTime) d3.select(this).transition().duration(transTime).attr("d", pathString);
        else d3.select(this).attr("d", pathString);
    }

    function lineAttribs(d,a,b){     
        var attr = {x1: xfun(d.start), y1: yfun(d.start), x2: xfun(d.end), y2: yfun(d.end)};
        return attr;
    };

    /**
     * Berechnet die Koordinaten eines Punktes auf einer quadratischen Bezierkurve
     * @param startX    Koordinaten des Startknotens
     * @param startY    Koordinaten des Startknotens
     * @param cpX       Koordinaten des Kontrollknotens
     * @param cpY       Koordinaten des Kontrollknotens
     * @param endX      Koordinaten des Zielknotens
     * @param endY      Koordinaten des Zielknotens
     * @param position  Position des Punktes zwischen Startpunkt und Endpunkt (0<=position<=1)
     * @returns {{x: number, y: number}} Koordinaten des gesuchten Punktes
     */
    function getQuadraticCurvePoint(startX, startY, cpX, cpY, endX, endY, position) {
        return {
            x: getQBezierValue(position, startX, cpX, endX),
            y: getQBezierValue(position, startY, cpY, endY)
        };
    };

    /**
     * Berechnet den Bezier-Wert fuer die Berechnung von Kruemmungen der Kanten
     * @param t
     * @param p1
     * @param p2
     * @param p3
     * @returns {number} BezierValue
     */
    function getQBezierValue(t, p1, p2, p3) {
        var iT = 1 - t;
        return iT * iT * p1 + 2 * iT * t * p2 + t * t * p3;
    }

    /*
    * Falls die Kante eine Kurve ist(es gibt Multikanten), wird der Kontrollpunkt zur Zeichnung der Kante berechnet.
    * @method
    * */
    function getControlPoint(edge, lineCoords){
        //finde alle Kanten zwischen source und target
        var edges = [];
        var s = edge.start.getOutEdges();
        var t = edge.end.getOutEdges();
        for(var i in s){
            if(s[i].end.id == edge.end.id){
                edges.push(s[i].id);
            }
        }
        for(var i in t){
            if(t[i].end.id == edge.start.id) {
                edges.push(t[i].id);
            }
        }
        var card = edges.length;
        var control = null;
        //berechne den Rang der Kante (id als referenz)
        edges.sort();
        var rank = edges.indexOf(edge.id) + 1;
        var median = parseInt((card+1)/2);
        if(card > 1 && !(card%2 == 1 && rank == median)){
            //berechne den Kontrollpunkt fuer die canvas-Zeichenfunktion
            var INTERVAL = 50;
            var a = {x: lineCoords.x1, y: lineCoords.y1};
            var b = {x: lineCoords.x2, y: lineCoords.y2};
            if(edge.start.id > edge.end.id){ //a ist immer kleiner b, vertausche falls notwendig
                var tmp = a;
                a = b;
                b = tmp;
            }
            var c = {x: (a.x + b.x)/2 , y: (a.y + b.y)/2};
            var ab = {x: (b.x - a.x) , y: (b.y - a.y)};
            var norm = Math.sqrt(ab.x*ab.x+ab.y*ab.y);
            var d = {x: -ab.y/norm, y: ab.x/norm};
            if(rank <= median){
                control = {x: c.x + (median-rank + 1- card%2)*INTERVAL*d.x, y: c.y + (median-rank +1 - card%2)*INTERVAL*d.y};
                if(card%2 == 0) control = {x: control.x - INTERVAL/2*d.x, y: control.y - INTERVAL/2*d.y};
            }
            else{
                rank = rank - median;
                control = {x: c.x - rank*INTERVAL*d.x, y: c.y - rank*INTERVAL*d.y};
                if(card%2 == 0) control = {x: control.x + INTERVAL/2*d.x, y: control.y + INTERVAL/2*d.y};
            }
        }
        return control;
    };

    /**
     * Zeigt, ob sich die gegebenen Koordinaten auf der Kante befinden.
     * Es wird geprüft ob der Mausklick nah genug (innerhalb einer Toleranz) an der Kante war.
     * @param {Number} mx				x-Koordinate
     * @param {Number} my				y-Koordinate
     * @this {Edge}
     * @returns {Boolean}
     * @method
     */
    this.containsEdge = function(edge, mx, my) {
        var toleranz = 7;									// Wie viele Punkte entfernt von der Kante darf man klicken?
        var sourceX = edge.start.x;
        var sourceY = edge.start.y;
        var targetX = edge.end.x;
        var targetY = edge.end.y;    
        var alpha = Math.atan2(targetY-sourceY,targetX-sourceX);
        // Ist der Mauszeiger auf der Kante?

        var MouseShift = {x:mx-sourceX,y:my-sourceY};
        var MouseShiftRot = {x: MouseShift.x*Math.cos(-alpha) - MouseShift.y*Math.sin(-alpha),
                    y: MouseShift.x*Math.sin(-alpha) + MouseShift.y*Math.cos(-alpha)};
        var targetShift = {x:targetX-sourceX,y:targetY-sourceY};
        var targetShiftRot = {x:targetShift.x*Math.cos(-alpha) - targetShift.y*Math.sin(-alpha),
                    y:targetShift.x*Math.sin(-alpha) + targetShift.y*Math.cos(-alpha)};
        if(MouseShiftRot.x>=0 && MouseShiftRot.x<=targetShiftRot.x && Math.abs(MouseShiftRot.y)<=toleranz) {
            return true;
        }

        //ist das eine kurve oder eine linie?
        var lineCoords = lineAttribs(edge, null, null);
        var control = getControlPoint(edge, lineCoords);
        if (control) {
            control.x += margin.left;
            control.y += margin.top;
        }
        if(control != null){//kurve
            var controlShift = {x:control.x-sourceX,y:control.y-sourceY};
            var controlShiftRot = {x: controlShift.x*Math.cos(-alpha) - controlShift.y*Math.sin(-alpha),
                y: controlShift.x*Math.sin(-alpha) + controlShift.y*Math.cos(-alpha)};
            var curvePoint = getQuadraticCurvePoint(0,0,controlShiftRot.x,controlShiftRot.y,targetShiftRot.x,targetShiftRot.y,controlShiftRot.x/targetShiftRot.x);
            if(MouseShiftRot.x>=0 && MouseShiftRot.x<=targetShiftRot.x && Math.abs(MouseShiftRot.y-curvePoint.y)<=toleranz) {
                return true;
            }
        }
        else {
            // Ist der Mauszeiger auf dem Text?
            var center = {x: (targetX+sourceX)/2, y:(targetY+sourceY)/2};
            var labelWidth = global_Edgelayout['fontSize']; // TO FIX
            var arrowHeight = Math.sin(global_Edgelayout['arrowAngle'])*global_Edgelayout['arrowHeadLength'];
            var c0 = {x:center.x+Math.cos(alpha)*labelWidth/2,
                y:center.y+Math.sin(alpha)*labelWidth/2};
            var c1 = {x:center.x-Math.cos(alpha)*labelWidth/2,
                y:center.y-Math.sin(alpha)*labelWidth/2};
            var c11 = {x:c1.x + Math.cos(alpha + Math.PI/2)*(-3-arrowHeight-global_Edgelayout['fontSize']),
                        y:c1.y + Math.sin(alpha + Math.PI/2)*(-3-arrowHeight-global_Edgelayout['fontSize'])};
            var upperCornerOld = {x:c11.x-c0.x,y:c11.y-c0.y};
            var upperCorner = {x:upperCornerOld.x*Math.cos(-alpha) - upperCornerOld.y*Math.sin(-alpha),
                        y:upperCornerOld.x*Math.sin(-alpha) + upperCornerOld.y*Math.cos(-alpha)};

            var rotatedMouseOld = {x:mx-c0.x,y:my-c0.y};
            var rotatedMouse = {x: rotatedMouseOld.x*Math.cos(-alpha) - rotatedMouseOld.y*Math.sin(-alpha),
                        y: rotatedMouseOld.x*Math.sin(-alpha) + rotatedMouseOld.y*Math.cos(-alpha)};
            if(rotatedMouse.x<=0 && rotatedMouse.x>= upperCorner.x && rotatedMouse.y<=0 && rotatedMouse.y>= upperCorner.y) {
                return true;
            }
        }
        return false;
    };

    /////////////////
    //PRIVILEDGED


    this.clear = function(){
        svg_nodes.selectAll("g").remove();
        svg_links.selectAll("g").remove();
    };

    this.type="GraphDrawer";
    this.graph = Graph.instance;
    this.svgOrigin = svgOrigin;

    var that = this;

    this.screenPosToNodePos = function(pos){
        return {x: that.x.invert(pos[0]-margin.left), y: that.y.invert(pos[1]-margin.top)};
    };

    this.screenPosToTransform = function(pos){
        return "translate(" + (pos[0]-margin.left) + "," + (pos[1]-margin.top) + ")";
    }

    this.updateNodes = function(){

        // DATA JOIN
        // Join new data with old elements, if any.
          var selection = svg_nodes.selectAll(".node")
            .data(Graph.instance.getNodes(),function(d){return d.id});


        // UPDATE
        // Update old elements as needed.

        // ENTER
        // Create new elements as needed.
          var enterSelection = selection
            .enter().append("g")
            .attr("class","node")
            .call(this.onNodesEntered);//Foo.prototype.setText.bind(bar))

            enterSelection.append("circle")
                .attr("r", radius)
                .style("fill",global_NodeLayout['fillStyle'])
                .style("stroke-width",global_NodeLayout['borderWidth'])
                .style("stroke",global_NodeLayout['borderColor'])

            enterSelection.append("text")
                .attr("class","label unselectable")
                .attr("dy", ".35em")           // set offset y position
                .attr("text-anchor", "middle")

            enterSelection.append("text")
                .attr("class","resource unselectable")
                .attr("dy",-global_KnotenRadius+"px")           // set offset y position
                .attr("text-anchor", "middle")


        // ENTER + UPDATE
        // Appending to the enter selection expands the update selection to include
        // entering elements; so, operations on the update selection after appending to
        // the enter selection will apply to both entering and updating nodes.
            if(transTime){
            selection
                .transition().duration(transTime)
                .attr("transform",transform)
                .call(this.onNodesUpdated);
            }else{
            selection
                .attr("transform",transform)
                .call(this.onNodesUpdated);
            }

            selection.selectAll("text.label")
                 .text(this.nodeLabel);

            selection.selectAll("text.resource")
                .text(this.nodeText);


        // EXIT
        // Remove old elements as needed.
              selection.exit().remove();
    
    } //end updateNodes()



    this.updateEdges = function(){

        var selection = svg_links.selectAll(".edge")
            .data(Graph.instance.getEdges(),function(d){
                return d.id;
             });

    //ENTER

        var enterSelection = selection
            .enter()
            .append("g")
            .attr("class","edge")
            .call(this.onEdgesEntered);
        
		enterSelection.append("path")
            .attr("id", function(d) {
                return "edgePath_" + d.id;
            })
		    .attr("class","arrow")
            .attr("fill", "none")
		    .style("stroke","black")
		    .style("stroke-width",global_Edgelayout['lineWidth'])
		    .style("marker-end", "url(#arrowhead2)");

		enterSelection.append("path")
            .attr("id", function(d) {
                return tabID + "_edgeTextPath_" + d.id;
            })
            .attr("class","hidden")
            .attr("fill", "none")
		    .style("stroke","black")
		    .style("stroke-width",global_Edgelayout['lineWidth'])
		    .style("visibility", "hidden");

        enterSelection.append("text")
            .attr("class","resource unselectable edgeLabel")
            .append("textPath")
            .attr("id", function(d) {
                return "textPath_" + d.id; 
            })
            .attr("xlink:href", function(d) {
                return "#" + tabID + "_edgeTextPath_" + d.id; 
            })
            .attr("startOffset", "50%")
   

    var that = this;


    //ENTER + UPDATE
        var selt = selection;//.transition().duration(1000);
        selt.selectAll("path.arrow")
            .each(setPathString)
        selt.selectAll("path.hidden")
            .each(setHiddenPathString)

        selt.selectAll("text.resource")
            .style("text-anchor", "middle")
            .attr("dy", "-3")
            .selectAll("textPath")
            .attr("xlink:href", function(d) {
                return "#" + tabID + "_edgeTextPath_" + d.id; 
            })
            .attr("startOffset", "50%")
            .text(this.edgeText);

        selection.call(this.onEdgesUpdated)

    //EXIT
        var exitSelection = selection.exit()
        exitSelection.remove();

    }


    //initialize //TODO: is called twice when we init both tabs at the same time
    if (Graph.instance == null){
        Graph.setGraph("tg");
    }

} //end constructor GraphDrawer


GraphDrawer.prototype.update= function(){
  this.updateNodes();
  this.updateEdges();
}

GraphDrawer.prototype.getType= function(){
    console.log(this.type);
}

GraphDrawer.prototype.onNodesEntered = function(selection) {
//     console.log(selection[0].length + " nodes entered")
}

GraphDrawer.prototype.onNodesUpdated = function(selection) {
//     console.log(selection[0].length + " nodes updated")
}

GraphDrawer.prototype.onEdgesEntered = function(selection) {
//     console.log(selection[0].length + " edges entered")
}

GraphDrawer.prototype.onEdgesUpdated = function(selection) {
//     console.log(selection[0].length + " edges entered")
}

GraphDrawer.prototype.edgeText = function(d){
    return d.toString();
}

GraphDrawer.prototype.nodeText = function(d){
    return d.toString();   
}

/**
 * Bildet Knoten-IDs auf den zugehörigen Namen ab
 * @method
 * @param {Number} nodeID
 * @return {String} name
 */
GraphDrawer.prototype.mapNodeIDToName = function(nodeID) {
    // Labels sind a-z und ab 26 Knoten n1, n2, n3, ...
    if (nodeID < 26) {
        return String.fromCharCode(nodeID + 97);
    } else {
        var n = nodeID - 25;
        return "n" + n;
    }
};

GraphDrawer.prototype.nodeLabel = function(d) {
    return GraphDrawer.prototype.mapNodeIDToName(d.id);
}

GraphDrawer.prototype.nodeX = function(d){
    if(!d){
        console.log(d);
    }
    return d.x;
};

GraphDrawer.prototype.nodeY = function(d){
    return d.y;
};

GraphDrawer.prototype.nodePos = function(d){
    var obj = {};
    obj.x = this.x(this.nodeX(d));
    obj.y = this.y(this.nodeY(d));
    return obj;
}

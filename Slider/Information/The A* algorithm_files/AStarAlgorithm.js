/*
 * Animation des A*-Algorithmus
 * @author Lisa Velden
 *
 */"use strict";

/**
 * Instanz des A*-Algorithmus, erweitert die Klasse GraphDrawer
 * @constructor
 * @extends GraphDrawer
 */
function AStarAlgorithm(svgSelection) {
	GraphDrawer.call(this,svgSelection,null,null,"ta");

	/**
     * closure for this class
	 * @type AStarAlgorithm
	 */
    var that = this;
    
    var id = 0;
    var STATE_SELECTSOURCE = 0;
    var STATE_SELECTTARGET = 1;
    
    /**
     * the logger instance
     * @type Logger
     */
    var logger = new Logger(d3.select("#logger"));

    /**
     * status variables
     * @type Object
     */
    var s = null;

    /**
     * Knoten, von dem aus alle Entfernungen berechnet werden.
     * @type GraphNode
     */
    var startNode = null;

    /**
     * Knoten, zu dem der Pfad berechnet wird.
     * @type GraphNode
     */
    var targetNode = null;

    /**
     * Replay Stack, speichert alle Schritte des Ablaufs für Zurück Button
     * @type {Array}
     */
    var replayHistory = new Array();

    /**
     * Initialisiert das Zeichenfeld
     * @method
     */
    this.init = function() {
        Graph.addChangeListener(function(){
            that.clear();
            that.reset();
            that.squeeze();
            that.update();
        });

        this.reset();
        this.update();
    };

    /**
     * clear all states
     */
    this.reset = function(){
        s = {
            id: 0, // state id
            oldID: -1,
            distance: new Object(),
            parentEdges: new Object(),
            parentNodes: new Object(),
            pqueue: new PriorityQueue(),
            currentNode: null,
            copiedOutEdges: null,
            currentEdge: null,
            previousEdge: null,
            previousEdgeColor: null,
            oppositeNodeID: -1,
            oppositeNode: null,
            neighbourDistance: undefined,
            anotherEdgeRed: false,
            edgeColors: new Array(),
            nodeColors: new Array(),
            nodeLabels: new Array(),
            // Funktionswerte der Schätzfunktion
            h: new Array()
        };

        startNode = null;
        targetNode = null;

        logger.data = [];
        this.replayHistory = [];

        if(Graph.instance){
            Graph.instance.nodes.forEach(function(nodeID, node) {
                s.nodeColors[nodeID] = const_Colors.NodeFilling;
                s.nodeLabels[nodeID] = GraphDrawer.prototype.mapNodeIDToName(parseInt(nodeID));
            });

            Graph.instance.edges.forEach(function(edgeID, edge) {
                s.edgeColors[edgeID] = "#000000";
            });
        }
    }

    /**
     * Makes the view consistent with the state
     * @method
     */
    this.update = function(){

        this.updateDescriptionAndPseudocode();
        logger.update();

        // Zeichne PQ-Elemente in Statusfenster
        var c1 = $("#pqCanvas");
        // Bestimme Kontext des Canvas, auf denen die Priority Queue gezeichnet wird
        var pqCtx = c1[0].getContext("2d");
        pqCtx.clearRect(0, 0, c1.width(), c1.height());
        drawPQ(pqCtx, s.pqueue);
        
        if(Graph.instance){
             AStarAlgorithm.prototype.update.call(this); //updates the graph
        }
    }

    /**
     * When Tab comes into view
     * @method
     */
    this.activate = function() { 
        // Warteschlange anzeigen
        $("#pqCanvas").show();
       
        this.reset();
        this.squeeze();
        this.update();
    };

    /**
     * tab disappears from view
     * @method
     */
    this.deactivate = function() {
        this.stopFastForward();
        this.reset();
        Graph.instance = null;
        // load selected graph again 
        Graph.setGraph("tg");
    };

    this.getWarnBeforeLeave = function() {
        return startNode && s.id != 10;
    }
    
    
    this.setDisabledBackward = function(disabled) {
        $("#ta_button_Zurueck").button("option", "disabled", disabled);
    };
    
    this.setDisabledForward = function(disabled, disabledSpulen) {
        var disabledSpulen = (disabledSpulen!==undefined) ? disabledSpulen : disabled;
        $("#ta_button_1Schritt").button("option", "disabled", disabled);
        $("#ta_button_vorspulen").button("option", "disabled", disabledSpulen);
    };

    /**
     * Kopiert den Zustand des Algorithmus
     * @method
     * @param {Context} oldState
     * @returns {Context} newState
     */
    this.copyAlgorithmState = function(oldState) {
        var newState = jQuery.extend(true, {}, oldState);
        var newPQueue = oldState.pqueue.clone();
        newState.pqueue = newPQueue;
        return newState;
    };

    /**
     * add a step to the replay stack, serialize stateful data
     * @method
     */
    this.addReplayStep = function() {
        var sCopy = this.copyAlgorithmState(s);
        replayHistory.push({
            "graphState": Graph.instance.getState(),
            "s": sCopy,
            "legende": $("#tab_ta").find(".LegendeText").html(),
            "loggerData": JSON.stringify(logger.data)
        });
    };

    /**
     * playback the last step from stack, deserialize stateful data
     * @method
     */
    this.previousStepChoice = function() {        
        var oldState = replayHistory.pop();
        
        Graph.instance.setState(oldState.graphState);
        s = oldState.s;
        logger.data = JSON.parse(oldState.loggerData);
        $("#tab_ta").find(".LegendeText").html(oldState.legende);
        
        // Warteschlange anzeigen
        $("#pqCanvas").show();
        this.update();
    };

    /**
     * Executes the next step in the algorithm
     * @method
     */
    this.nextStepChoice = function(d) {

        // Store current state of the algorithm.      
        this.addReplayStep();

        s.oldID = s.id;

        switch (s.id) {
            case STATE_SELECTSOURCE:
                this.selectSource(d);
                s.id = 1;
                break;
            case STATE_SELECTTARGET:
                this.selectTarget(d);
                s.id = 2;
                break;
            case 2:
                this.initializeAlgorithm();
                s.id = 3;
                break;
            case 3:
                this.extractMin();
                break;
            case 4:
                this.expandNode();
                break;
            case 5:
                this.updateVisitedNode();
                break;
            case 6:
                this.insertNewNode();
                break;
            case 7:
                this.markAsProcessed();
                break;
            case 8:               
                clearPQ();
                this.end();
                break;
            case 9:               
                clearPQ();
                this.end();
                break;
            default:
                console.log("State does not exist.");
                break;
        }

        // update view depending on the current state
        this.update();
    };

    /**
     * Select the source node.
     */
    this.selectSource = function(d) {
        startNode = Graph.instance.nodes.get(d.id);
        s.nodeColors[startNode.id] = const_Colors.StartEndNodeColor;
        s.nodeLabels[startNode.id] = "Start";
        this.setDisabledBackward(false);
        this.setDisabledForward(false);
    };

    /**
     * Select the target node.
     */
    this.selectTarget = function(d) {
        targetNode = Graph.instance.nodes.get(d.id);
        s.nodeColors[targetNode.id] = const_Colors.StartEndNodeColor;
        s.nodeLabels[targetNode.id] = LNG.K('algorithm_node_3');
            
        // Berechnet für jeden Knoten die Schätzfunktion h(x), hier euklischer Abstand (sqrt(sum_i(x_i-y_i)^2))
        Graph.instance.nodes.forEach(function(nodeID, node) {
            s.h[nodeID] = Math.sqrt(Math.pow(node.x - targetNode.x, 2) + Math.pow(node.y - targetNode.y, 2));
        });
        this.setDisabledBackward(false);
        this.setDisabledForward(false);
    };

    /**
     * Initialisiere den Algorithmus, stelle die Felder auf ihre Startwerte.
     * @method
     */
    this.initializeAlgorithm = function() {
        // Initialisiere Distanzen mit unendlich und Vaterknoten mit null
        Graph.instance.nodes.forEach(function(nodeID, node) {
            s.distance[nodeID] = "inf";
            if (nodeID != startNode.id) 
                s.nodeLabels[nodeID] = s.nodeLabels[nodeID] + ": " + String.fromCharCode(8734);
            // Unendlich
            s.parentEdges[nodeID] = null;
            s.parentNodes[nodeID] = null;
        });

        // Startknoten: Distanz 0 und er selbst ist sein Vaterknoten
        s.distance[startNode.id] = 0;
        s.parentNodes[startNode.id] = startNode.id;
        s.nodeLabels[startNode.id] = s.nodeLabels[startNode.id] + ": " + "0";
        s.pqueue.insert(new Node(0, startNode));

        // merke für Animation, dass Kante behandelt war (wird später bei loopOutEdges() benötigt)
        s.anotherEdgeRed = false;
    };

    /**
     * Iteriere durch die Hauptschleife: solange Elemente in der Priority Queue vorhanden sind, entnehme das erste Element
     * @method
     */
    this.extractMin = function() {
        if ($("#ta_button_Zurueck").button("option", "disabled") && this.fastForwardIntervalID == null) {
            $("#ta_button_Zurueck").button("option", "disabled", false);
        }

        var currentWrapperNode = s.pqueue.extractMin();
        s.currentNode = currentWrapperNode.getValue();
        s.nodeColors[s.currentNode.id] = const_Colors.CurrentNodeColor;

        var outEdges = s.currentNode.getOutEdges();
        s.copiedOutEdges = outEdges;

        if (s.copiedOutEdges.length > 0 && s.currentNode.id != targetNode.id) {
            // Neuer Status -> nächste Kante: loopOutEdges()
            s.id = 4;
        } else {
            // Neuer Status -> Vaterknoten abgearbeitet: markAsProcessed()
            s.id = 7;
        };
    };

    /**
     * Alle ausgehenden Kanten des aktuellen Knotens betrachten
     * @method
     */
    this.expandNode = function () {
        if (s.anotherEdgeRed) {
            s.anotherEdgeRed = false;
            s.edgeColors[s.currentEdge.id] = const_Colors.NormalEdgeColor;
        }
        s.currentEdge = s.copiedOutEdges.shift();
        s.oppositeNodeID = s.currentEdge.end.id;
        s.oppositeNode = Graph.instance.nodes.get(s.oppositeNodeID);
        s.edgeColors[s.currentEdge.id] = const_Colors.EdgeHighlight1;
        s.neighbourDistance = s.distance[s.currentEdge.start.id] + s.currentEdge.resources[0];

        //Distanzen und Prioritäten für bereits entdeckte Knoten updaten
        if (s.pqueue.contains(s.oppositeNode) && (s.neighbourDistance < s.distance[s.oppositeNodeID])) {
            // Neuer Status -> updateVisitedNode()
            s.id = 5;
            //Neu entdeckten Knoten in Priority Queue einfügen
        } else if (s.parentNodes[s.oppositeNodeID] == undefined) {
            // Neuer Status -> insertNewNode()
            s.id = 6;
        } else {
            if (s.copiedOutEdges.length > 0) {
                // merke für Animation, dass Kante unbehandelt war
                s.anotherEdgeRed = true;
                // Neuer Status -> nächste Kante: loopOutEdges()
                s.id = 4;
            } else {
                // Neuer Status -> Vaterknoten abgearbeitet: markAsProcessed()
                s.id = 7;
            };
        };
    };

    /**
     * Ist der Knoten bereits in der Priority Queue enthalten und die neuberechnete Distanz kleiner, so wird der Distanzwert aktualisiert
     * @method
     */
    this.updateVisitedNode = function() {
        // f(x) = g(x) + h(x)
        var f_value = s.neighbourDistance + s.h[s.oppositeNodeID];
        s.pqueue.decreaseKey(Graph.instance.nodes.get(s.oppositeNodeID), f_value);

        s.nodeColors[s.oppositeNodeID] = const_Colors.PQColor;

        s.distance[s.oppositeNodeID] = s.neighbourDistance;
        s.parentEdges[s.oppositeNodeID] = s.currentEdge.id;
        s.parentNodes[s.oppositeNodeID] = s.currentNode.id;
        s.edgeColors[s.currentEdge.id] = const_Colors.NormalEdgeColor;

        // Knotenlabel updaten
        s.nodeLabels[s.oppositeNodeID] = GraphDrawer.prototype.mapNodeIDToName(parseInt(s.oppositeNode.id)) + ": " + (Math.round(f_value)).toString();

        if (s.copiedOutEdges.length > 0) {
            // Neuer Status -> nächste Kante: loopOutEdges()
            s.id = 4;
        } else {
            // Neuer Status -> Vaterknoten abgearbeitet: markAsProcessed()
            s.id = 7;
        };
    };

    /**
     * Wurde der Knoten erst neu entdeckt, so wird er in die Priority Queue eingefügt
     * @method
     */
    this.insertNewNode = function() {
        // f(x) = g(x) + h(x)
        var f_value = s.neighbourDistance + s.h[s.oppositeNodeID];
        s.pqueue.insert(new Node(f_value, s.oppositeNode));
        s.distance[s.oppositeNodeID] = s.neighbourDistance;
        s.parentEdges[s.oppositeNodeID] = s.currentEdge.id;
        s.parentNodes[s.oppositeNodeID] = s.currentNode.id;
        s.nodeColors[s.oppositeNodeID] = const_Colors.PQColor;
        s.nodeLabels[s.oppositeNodeID] = GraphDrawer.prototype.mapNodeIDToName(parseInt(s.oppositeNode.id)) + ": " + (Math.round(f_value)).toString();
        s.edgeColors[s.currentEdge.id] = const_Colors.NormalEdgeColor;

        if (s.copiedOutEdges.length > 0) {
            // Neuer Status -> nächste Kante: loopOutEdges()
            s.id = 4;
        } else {
            // Neuer Status -> Vaterknoten abgearbeitet: markAsProcessed()
            s.id = 7;
        };
    };

    /**
     * Markiere bereits abgearbeitete Knoten
     * @method
     */
    this.markAsProcessed = function() {
        if (s.currentNode.id != startNode.id && s.currentNode.id != targetNode.id) {
            s.nodeColors[s.currentNode.id] = const_Colors.FinishedNodeColor;
        } else {
            s.nodeColors[s.currentNode.id] = const_Colors.StartEndNodeColor;
        };

        if (s.currentEdge) {
            if (s.edgeColors[s.currentEdge.id] == const_Colors.EdgeHighlight1) {
                s.edgeColors[s.currentEdge.id] = const_Colors.NormalEdgeColor;
            };
        };

        if (s.pqueue.length() > 0 && s.currentNode.id != targetNode.id) {
            // Neuer Status -> nächster Knoten: extractMin()
            s.id = 3;
        } else {

            if (s.currentNode.id == targetNode.id) {
                // Neuer Status -> Ende & Pfad gefunden
                s.id = 8;

                // kürzesten Pfad einfärben
                var tmpNodeID = targetNode.id;
                while (tmpNodeID != startNode.id) {
                    s.edgeColors[s.parentEdges[tmpNodeID]] = const_Colors.ShortestPathColor;
                    tmpNodeID = s.parentNodes[tmpNodeID];

                };
            } else {
                // Neuer Status -> Ende & Zielknoten nicht erreichbar
                s.id = 9;
            };
        };
    };

    /**
     * Zeigt Texte und Buttons zum Ende des Algorithmus an.
     * @method
     */
    this.end = function() {
        // Alte Vorgänger-Datensturktur wiederherstellen
        parent = jQuery.extend(true, {}, s.parentEdges);

        changeTextAStar(s.id, "ta");

        Graph.instance.nodes.forEach(function(nodeID, node) {
            var nodeName ="";
            if (nodeID == startNode.id) nodeName = "Start: ";
            else nodeName = GraphDrawer.prototype.mapNodeIDToName(parseInt(nodeID)) + ": ";

            var dist = "";
            if (s.distance[nodeID] == "inf") dist = String.fromCharCode(8734);
            else dist = s.distance[nodeID].toString();
            
            s.nodeLabels[nodeID] = nodeName + dist;
        });

        // Falls wir im "Vorspulen" Modus waren, daktiviere diesen
        if (this.fastForwardIntervalID) {
            this.stopFastForward();
        }
        // Ausführung nicht mehr erlauben
        $("#ta_button_1Schritt").button("option", "disabled", true);
        $("#ta_button_vorspulen").button("option", "disabled", true);

        this.showEndButtons();

        // Neuer Status -> alles fertig
        s.id = 10;
    };

    /**
     * Zeigt Buttons zum Ende des Algorithmus an.
     * @method
     */
    this.showEndButtons = function() {
        $("#ta_div_statusText").append("<p></p><h3>"+LNG.K('algorithm_button_8')+"</h3>");
        $("#ta_div_statusText").append("<button id=ta_button_gotoIdee>"+LNG.K('algorithm_button_5')+"</button>");
        $("#ta_div_statusText").append("<h3>"+LNG.K('algorithm_status_5')+":</h3>");
        $("#ta_div_statusText").append("<button id=ta_button_gotoFA1>"+LNG.K('algorithm_button_6')+"</button>");
        $("#ta_div_statusText").append("<button id=ta_button_gotoFA2>"+LNG.K('algorithm_button_7')+"</button>");
        $("#ta_button_gotoIdee").button();
        $("#ta_button_gotoFA1").button();
        $("#ta_button_gotoFA2").button();
        $("#ta_button_gotoIdee").click(function() {
            $("#tabs").tabs("option", "active", 3);
        });
        $("#ta_button_gotoFA1").click(function() {
            $("#tabs").tabs("option", "active", 4);
        });
        $("#ta_button_gotoFA2").click(function() {
            $("#tabs").tabs("option", "active", 5);
        });

        //"Warteschlange" entfernen
        $("#pqCanvas").hide();
    };

    this.onNodesEntered = function(selection) {
        //select source node
        selection
          .on("click", function(d) {
              if (s.id == STATE_SELECTSOURCE || s.id == STATE_SELECTTARGET) {
                  that.nextStepChoice(d);
              }
        });
    }

    this.onNodesUpdated = function(selection) {
        selection
         .style("cursor",function(d) {
            if (s.id == STATE_SELECTSOURCE || (s.id == STATE_SELECTTARGET && d.id != startNode.id)) {
                return "pointer";
            }
        });
        selection
        .selectAll("circle")
        .style("fill", function(d) {
            return getNodeColor(d);
        });
    }

    this.onEdgesUpdated = function(selection) { 
        // update edge color        
        selection
        .selectAll("path")
        .style("stroke", function(d) {
            return getLineColor(d);
        });

        // update arrow color
        selection
        .selectAll("path.arrow")
        .style("marker-end", function(d) {
            return getArrow(d);
        });
    }

    this.nodeLabel = function(d) {
        return s.nodeLabels[d.id];
    }

    var getNodeColor = function(d) {
        var node = Graph.instance.nodes.get(d.id);     
        return s.nodeColors[node.id];
    }

    var getArrow = function(d) {
        var edge = Graph.instance.edges.get(d.id);
        if (s.edgeColors[edge.id] == const_Colors.NormalEdgeColor) {
            return "url(#arrowhead2)";
        }
        else if (s.edgeColors[edge.id] == const_Colors.EdgeHighlight1) {
            return "url(#arrowhead2-red)";
        }
        else { // ShortestPathColor
            return "url(#arrowhead2-green)";
        }
    }

    var getLineColor = function(d) {
        var edge = Graph.instance.edges.get(d.id);
        return s.edgeColors[edge.id];
    }

    // Maps current state to pseudo code lines in the HTML file.
    var getPseudocodeLines = function(divCounter) {
        if (s.oldID == 0 || s.oldID == -1 || s.oldID == 1) return divCounter == 0;
        else if (s.oldID == 2) return divCounter == 1;
        else if (s.oldID == 3) return divCounter == 3;
        else if (s.oldID == 4) return divCounter == 5;
        else if (s.oldID == 5) return divCounter == 6;
        else if (s.oldID == 6) return divCounter == 7;
        else if (s.oldID == 7) return divCounter == 2;
        else if (s.oldID == 8) return divCounter == 4 || divCounter == 9;
        else if (s.oldID == 9) return divCounter == 8 || divCounter == 9;
        else return divCounter == 9;
    }

    /**
     * updates status description and pseudocode highlight based on current s.id
     * @method
     */
    this.updateDescriptionAndPseudocode = function() {  
    
        if (s.id != 10) {
            // Text anzeigen, solange wir noch nicht fertig sind
            changeTextAStar(s.oldID, "ta");
            if (s.oldID == 5 || s.oldID == 6) {
                this.calculateHeuristic(s.distance[s.oppositeNode.id], s.h[s.oppositeNode.id]);
            };
        };
 
        var sel = d3.select("#ta_div_statusPseudocode").selectAll("div").selectAll("p");
        sel.classed("marked", function(a, pInDivCounter, divCounter) {          
            return getPseudocodeLines(divCounter);
        });

        if (this.fastForwardIntervalID != null) {
            this.setDisabledForward(true,false);
            this.setDisabledBackward(true);
        }
        else if (targetNode == null) {
            this.setDisabledBackward(true);
            this.setDisabledForward(true);
        }
        else if (s.id == 2) {
            this.setDisabledForward(false);
            this.setDisabledBackward(true);
        } 
        else if (s.id == 10) {
            this.setDisabledForward(true);
            this.setDisabledBackward(false);
        }
        else {
            this.setDisabledForward(false);
            this.setDisabledBackward(false);
        }
    };

    /********************************************************* Heuristik ***********************************************************/

    /**
     * Zeigt die Berechnung der Heuristik an.
     * @method
     * @param {Number} g
     * @param {Number} h
     */
    this.calculateHeuristic = function(g, h) {
        // auf ganze Integer runden
        var h = Math.round(h);
        // Ergebnis ebenfalls runden, um JS-Rundungsfehler vorzubeugen
        var f = Math.round(g + h);
        $("#ta_div_statusText").append("<div id=\"div_table\"></div>");
        $("#div_table").load("html/f_value.html", undefined, function() {
            $("#g_value").text(g);
            $("#h_value").text(h);
            $("#f_value").text(f);
        });

    };
};

// Vererbung realisieren
AStarAlgorithm.prototype = Object.create(GraphDrawer.prototype);
AStarAlgorithm.prototype.constructor = AStarAlgorithm;

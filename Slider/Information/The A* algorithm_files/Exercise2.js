/**
 * @author Lisa Velden
 * Forschungsaufgabe 2
 */"use strict";

/**
 * Instanz der Forschungsaufgabe 2
 * @constructor
 * @extends GraphDrawer
 */
function Exercise2(svgSelection) {
	GraphDrawer.call(this,svgSelection,null,null,"tf2");

	/**
     * closure for this class
	 * @type Exercise2
	 */
    var that = this;
    
    var id = 0;
    var STATE_SELECTSOURCE = 0;
    
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
     * Zielknoten, zu dem die Entfernungen berechnet werden.
     * @type GraphNode
     */
    var targetNode = null;

    /**
     * Replay Stack, speichert alle Schritte des Ablaufs für Zurück Button
     * @type {Array}
     */
    var replayHistory = new Array();

    /**
     * Status der Frage.<br>
     * Keys: aktiv, warAktiv
     * Values: Boolean
     * @type Object
     */
    var questionStatus = new Object();

        /**
     * Zeigt an, ob vor dem Verlassen des Tabs gewarnt werden soll.
     * @type Boolean
     */
    var warnBeforeLeave = false;

    /**
     * Welcher Tab (Erklärung oder Pseudocode) angezeigt wurde, bevor die Frage kam.
     * Dieser Tag wird nach der Frage wieder eingeblendet.
     * @type Boolean
     */
    var tabBeforeQuestion = null;

    /**
     * Statistiken zu den Fragen
     * @type Object
     */
    var questionStats = {
        numQuestions : 4,
        correct : 0,
        wrong : 0,
        gestellt : 0
    };

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
            id: 2, // state id
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

        questionStats = {
            numQuestions : 4,
            correct : 0,
            wrong : 0,
            gestellt : 0
        };

        questionStatus = new Object();
        tabBeforeQuestion = null;
        warnBeforeLeave = false;

        logger.data = [];
        this.replayHistory = [];

        if(Graph.instance){
            startNode = Graph.instance.nodes.get(12);
            targetNode = Graph.instance.nodes.get(24);

            Graph.instance.nodes.forEach(function(nodeID, node) {
                s.nodeColors[nodeID] = const_Colors.NodeFilling;
                s.nodeLabels[nodeID] = GraphDrawer.prototype.mapNodeIDToName(parseInt(nodeID));
            });

            Graph.instance.edges.forEach(function(edgeID, edge) {
                s.edgeColors[edgeID] = "#000000";
            });

            s.nodeLabels[12] = "Start";
            s.nodeColors[12] = const_Colors.StartEndNodeColor;
            s.nodeLabels[24] = "Ziel";
            s.nodeColors[24] = const_Colors.StartEndNodeColor;

            // Berechnet für jeden Knoten die Schätzfunktion h(x), hier euklischer Abstand (sqrt(sum_i(x_i-y_i)^2))
            Graph.instance.nodes.forEach(function(nodeID, node) {
                if (targetNode) 
                    s.h[nodeID] = Math.sqrt(Math.pow(node.x - targetNode.x, 2) + Math.pow(node.y - targetNode.y, 2));
            });
        }
    }

    /**
     * Makes the view consistent with the state
     * @method
     */
    this.update = function(){
        if (!questionStatus.aktiv) {
            this.updateDescriptionAndPseudocode();
        }
        logger.update();

        if(Graph.instance){
             Exercise2.prototype.update.call(this); //updates the graph
        }
    }

    /**
     * When Tab comes into view
     * @method
     */
    this.activate = function() {     
        // set graph to exercise graph
        var GRAPH_FILENAME = GRAPH_FILENAME || null;
        var completeFilename = GRAPH_FILENAME || "graphs/gridgraph.txt"; 
        Graph.loadInstance(completeFilename,function(error,text,completeFilename){
            console.log("error loading graph instance "+error + " from " + completeFilename +" text: "+text);
        });  
   
        this.reset();
        this.squeeze();
        this.update();
    };

    /**
     * tab disappears from view
     * @method
     */
    this.deactivate = function() {
        if (questionStatus.aktiv || questionStatus.warAktiv) {
            this.removeQuestionTab();
        }
        removeResultsTab();

        this.stopFastForward();
        this.reset();
        Graph.instance = null;
        // load selected graph again 
        Graph.setGraph("tg");
    };

    this.refresh = function() {
        // set graph to exercise graph
        var GRAPH_FILENAME = GRAPH_FILENAME || null;
        var completeFilename = GRAPH_FILENAME || "graphs/gridgraph.txt"; 
        Graph.loadInstance(completeFilename,function(error,text,completeFilename){
            console.log("error loading graph instance "+error + " from " + completeFilename +" text: "+text);
        });     

        removeResultsTab();
    
        // reinitialize data structures and update view
        this.reset();
        this.update();
    };
    
    
    this.setDisabledBackward = function(disabled) {
        $("#tf2_button_Zurueck").button("option", "disabled", disabled);
    };
    
    this.setDisabledForward = function(disabled, disabledSpulen) {
        var disabledSpulen = (disabledSpulen!==undefined) ? disabledSpulen : disabled;
        $("#tf2_button_1Schritt").button("option", "disabled", disabled);
        $("#tf2_button_vorspulen").button("option", "disabled", disabledSpulen);
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
            "legende": $("#tab_tf2").find(".LegendeText").html(),
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
        $("#tab_tf2").find(".LegendeText").html(oldState.legende);
        
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

        if (questionStatus.aktiv) {
            this.stopFastForward();
        };

        if (!questionStatus.aktiv) {
            if (questionStatus.warAktiv) {
                this.removeQuestionTab();
                questionStatus.warAktiv = false;
            }

            switch (s.id) {
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
                    this.end();
                    break;
                case 9:               
                    this.end();
                    break;
                case 10:
                    this.showResults();
                    break;
                default:
                    console.log("State does not exist.");
                    break;
            }

            // update view depending on the current state
            this.update();
        }
    };

    /**
     * Select the source node.
     */
    this.selectSource = function(d) {
        startNode = Graph.instance.nodes.get(d.id);
        s.nodeColors[startNode.id] = const_Colors.NodeFillingHighlight;
        s.nodeLabels[startNode.id] = "Start";
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
        warnBeforeLeave = true;
    };

    /**
     * Iteriere durch die Hauptschleife: solange Elemente in der Priority Queue vorhanden sind, entnehme das erste Element
     * @method
     */
    this.extractMin = function() {
        if ($("#tf2_button_Zurueck").button("option", "disabled") && this.fastForwardIntervalID == null) {
            $("#tf2_button_Zurueck").button("option", "disabled", false);
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

            //Frage zu Distanz von Knoten s:
            if (s.oppositeNode.id == 18 && questionStats.gestellt < questionStats.numQuestions) {
                this.poseQuestion("assets/question_astar_node_s_"+LNG.getLanguage()+".json");
            };
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

        //Frage, wenn Zielknoten in PQ:
        if (s.oppositeNode.id == targetNode.id && questionStats.gestellt < questionStats.numQuestions) {
            this.poseQuestion("assets/question_astar_target_into_pq_"+LNG.getLanguage()+".json");
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

                //Frage, wenn Zielknoten entnommen:
                if (questionStats.gestellt < questionStats.numQuestions) {
                    this.poseQuestion("assets/question_astar_target_from_pq_"+LNG.getLanguage()+".json");
                };

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

        //Frage am Ende der Ausführung:
        this.poseQuestion("assets/question_astar_end_"+LNG.getLanguage()+".json");

        changeTextAStar(s.id, "tf2");

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
        if (this.fastForwardIntervalID != null) {
            this.stopFastForward();
        }
        // Ausführung nicht mehr erlauben
        $("#tf2_button_1Schritt").button("option", "disabled", true);
        $("#tf2_button_vorspulen").button("option", "disabled", true);

        // Neuer Status -> alles fertig
        s.id = 10;
    };

    this.onNodesEntered = function(selection) {
    }

    this.onNodesUpdated = function(selection) {
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
            changeTextAStar(s.oldID, "tf2");
            if (s.oldID == 5 || s.oldID == 6) {
                this.calculateHeuristic(s.distance[s.oppositeNode.id], s.h[s.oppositeNode.id]);
            };
        };
 
        var sel = d3.select("#tf2_div_statusPseudocode").selectAll("div").selectAll("p");
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

    /********************************************************************  Für die Fragen  *********************************************************************/

    /**
     * Läd die Frage aus dem entsprechenden JSON-File und zeigt sie an.
     * @method
     * @param {String} questionURL
     */
    this.poseQuestion = function(questionURL) {
        var request = $.ajax({
            url : questionURL,
            async : false,
            dataType : "text"
        });

        this.addQuestionTab();

        request.done(function(data) {
            var question = $.parseJSON(data);

            questionStats.gestellt++;

            $("#tf2_div_Frage").html(LNG.K('aufgabe2_status_1')+" " + questionStats.gestellt + " "+LNG.K('aufgabe2_status_2')+" " + questionStats.numQuestions);
            $("#tf2_div_Frage").append("<p class=\"question\">" + question.question + "</p>");
            $("p.question").css("color", const_Colors.RedText);

            var firstTry = true;

            for (var i = 0; i < question.answers.length; i++) {
                var answer = question.answers[i];

                var idInput = 'answer_' + i;
                var idLabel = 'answer_' + i + '_label';

                var inputHTML = '<input type="radio" id="' + idInput + '" name="group1"/>';
                var labelHTML = '<label id="' + idLabel + '" for="' + idInput + '">' + answer.answer + '</label>';

                $("#tf2_div_Antworten").append(inputHTML + labelHTML + '<br>');

                if (i === question.correctAnswerIndex) {
                    var ans = answer.answer;
                    var exp = answer.explanation;

                    $("#" + idInput).click(function() {
                        $("#tf2_button_1Schritt").button("option", "disabled", false);
                        $("#tf2_button_vorspulen").button("option", "disabled", false);

                        $("p.question").css("color", const_Colors.GreenText);

                        $("#tf2_div_Antworten").html("<h2>"+LNG.K('aufgabe2_status_3')+": " + ans + "</h2>");
                        $("#tf2_div_Antworten").append(exp);

                        if (firstTry) {
                            questionStats.correct++;
                        } else {
                            questionStats.wrong++;
                        }
                        questionStatus = {
                            "aktiv" : false,
                            "warAktiv" : true
                        };

                    });
                } else {
                    // Closure durch Funktion, um lokale Variablen zu schützen
                    var f = function(id, label) {
                        $("#" + id).click(function() {
                            $("#" + label).addClass("ui-state-error");
                            firstTry = false;
                        });
                    };
                    f(idInput, idLabel);
                };
            };
        });

        questionStatus = {
            "aktiv" : true,
            "warAktiv" : false
        };
    };

    /**
     * Fügt einen Tab für die Frage hinzu.<br>
     * Deaktiviert außerdem die Buttons zum weitermachen
     * @method
     */
    this.addQuestionTab = function() {
        var li = "<li id='tf2_li_FrageTab'><a href='#tf2_div_FrageTab'>"+LNG.K('aufgabe2_status_4')+"</a></li>", id = "tf2_div_FrageTab";
        $("#tf2_div_statusTabs").find(".ui-tabs-nav").append(li);
        $("#tf2_div_statusTabs").append("<div id='" + id + "'><div id='tf2_div_Frage'></div><div id='tf2_div_Antworten'></div></div>");
        $("#tf2_div_statusTabs").tabs("refresh");
        tabBeforeQuestion = $("#tf2_div_statusTabs").tabs("option", "active");
        $("#tf2_div_statusTabs").tabs("option", "active", 2);
        $("#tf2_button_1Schritt").button("option", "disabled", true);
        $("#tf2_button_vorspulen").button("option", "disabled", true);
    };

    /**
     * Entfernt den Tab für die Ergebnisse und aktiviert den vorherigen Tab.
     * @method
     */
    var removeResultsTab = function() {
        $("#tf2_div_statusTabs").tabs("option", "active", 0);
        $("#tf2_li_ErgebnisseTab").remove().attr("aria-controls");
        $("#tf2_div_ErgebnisseTab").remove();
        $("#tf2_div_statusTabs").tabs("refresh");
    };

    /**
     * Entfernt den Tab für die Frage und aktiviert den vorherigen Tab.
     * @method
     */
    this.removeQuestionTab = function() {
        if ($("#tf2_div_statusTabs").tabs("option", "active") == 2) {
            $("#tf2_div_statusTabs").tabs("option", "active", tabBeforeQuestion);
        }
        $("#tf2_li_FrageTab").remove().attr("aria-controls");
        $("#tf2_div_FrageTab").remove();
        $("#tf2_div_statusTabs").tabs("refresh");
    };

    /**
     * Zeigt - in eigenem Tab - die Resultate der Aufgabe an.
     * @method
     */
    this.showResults = function() {
        warnBeforeLeave = false;
        this.stopFastForward();

        // Ausführung nicht mehr erlauben
        $("#tf2_button_1Schritt").button("option", "disabled", true);
        $("#tf2_button_vorspulen").button("option", "disabled", true);

        var li = "<li id='tf2_li_ErgebnisseTab'><a href='#tf2_div_ErgebnisseTab'>"+LNG.K('aufgabe2_status_5')+"</a></li>", id = "tf2_div_ErgebnisseTab";
        $("#tf2_div_statusTabs").find(".ui-tabs-nav").append(li);
        $("#tf2_div_statusTabs").append("<div id='" + id + "'></div>");
        $("#tf2_div_statusTabs").tabs("refresh");
        $("#tf2_div_statusTabs").tabs("option", "active", 2);
        if (questionStats.numQuestions == questionStats.correct) {
            $("#tf2_div_ErgebnisseTab").append("<h2>"+LNG.K('aufgabe2_status_6')+"</h2>");
            $("#tf2_div_ErgebnisseTab").append("<p>"+LNG.K('aufgabe2_status_7')+"</p>");
        } else {
            $("#tf2_div_ErgebnisseTab").append("<h2>"+LNG.K('aufgabe2_status_8')+"</h2>");
            $("#tf2_div_ErgebnisseTab").append("<p>"+LNG.K('aufgabe2_status_9')+": " + questionStats.numQuestions + "</p>");
            $("#tf2_div_ErgebnisseTab").append("<p>"+LNG.K('aufgabe2_status_10')+": " + questionStats.correct + "</p>");
            $("#tf2_div_ErgebnisseTab").append("<p>"+LNG.K('aufgabe2_status_11')+": " + questionStats.wrong + "</p>");
            $("#tf2_div_ErgebnisseTab").append('<button id="tf2_button_Retry">'+LNG.K('aufgabe2_button_5')+'</button>');
            $("#tf2_button_Retry").button().click(function() {
                that.refresh();
            });
        }
        $("#tf2_div_ErgebnisseTab").append('<button id="tf2_button_gotoWeiteres">'+LNG.K('aufgabe2_button_6')+'</button>');
        $("#tf2_button_gotoWeiteres").button().click(function() {
            $("#tabs").tabs("option", "active", 6);
            $("#tw_Accordion").accordion("option", "active", 2);
        });
    };

    /**
     * Zeigt and, ob vor dem Verlassen des Tabs gewarnt werden soll.
     * @returns {Boolean}
     */
    this.getWarnBeforeLeave = function() {
        return warnBeforeLeave;
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
        $("#tf2_div_statusText").append("<div id=\"div_table\"></div>");
        $("#div_table").load("html/f_value.html", undefined, function() {
            $("#g_value").text(g);
            $("#h_value").text(h);
            $("#f_value").text(f);
        });

    };

};

// Vererbung realisieren
Exercise2.prototype = Object.create(GraphDrawer.prototype);
Exercise2.prototype.constructor = Exercise2;

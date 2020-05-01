/**
 * Tab for the first exercise
 * initializes the buttons, callbacks, the logger and fast forward funcitonality
 * @author Adrian Haarbach, Johannes Feil
 * @augments Tab
 * @class
 */
function ExerciseTab1(algo,p_tab) {
    Tab.call(this, algo, p_tab);

    /**
     * ID of the fast forward interval
     * @type Number
     */
    algo.fastForwardIntervalID = null;

    var that = this;

    /**
     * Timeout speed in milliseconds for fast forward
     * @type Number
     */
    var fastForwardSpeed = 180;

    /**
     * the logger instance
     * @type Logger
     */
    var logger = new Logger(d3.select("#logger"));


    var fastforwardOptions = {label: $("#tf1_button_text_fastforward").text(), icons: {primary: "ui-icon-seek-next"}};

    /**
     * Initialisiert das Zeichenfeld
     * @method
     */
    this.init = function() {

        var pauseOptions = {label: $("#tf1_button_text_pause").text(), icons: {primary: "ui-icon-pause"}};

        if(algo.rewindStart && algo.rewindStop){
        var rewindOptions = {label: $("#tf1_button_text_rewind").text(), icons: {primary: "ui-icon-seek-prev"}};
            $("#tf1_button_rewind")
            .button(rewindOptions)
            .click(function() {
                $(this).button("option",this.checked ? pauseOptions : rewindOptions);
                this.checked ? algo.rewindStart() : algo.rewindStop();
            })
        }else{
            $("#tf1_button_rewind").hide();
            $("#tf1_button_text_rewind").hide();
        }
        
        $("#tf1_button_Zurueck")
            .button({icons: {primary: "ui-icon-seek-start"}})
            .click(function() {
                algo.previousStepChoice();
            });
        
        $("#tf1_button_1Schritt")
            .button({icons: {primary: "ui-icon-seek-end"}})
            .click(function() {
                algo.nextStepChoice();
            });

        $("#tf1_button_vorspulen")
            .button(fastforwardOptions)
            .click(function() {
                $(this).button("option",this.checked ? pauseOptions : fastforwardOptions);
                this.checked ? that.fastForwardAlgorithm() : that.stopFastForward();
            });

        $("#tf1_vorspulen_speed").on("input",function(){
            fastForwardSpeed=+this.value;  
        });



        $("#tf1_div_statusTabs").tabs();
        $("#tf1_div_statusTabs").tabs("option", "active", 2);

        $("#tf1_tr_LegendeClickable").removeClass("greyedOutBackground");
        
        var sel = d3.select("#tf1_div_statusPseudocode").selectAll("div").selectAll("p")
        sel.attr("class", function(a, pInDivCounter, divCounter) {
            return "pseudocode";
        });

        // set graph to exercise graph
        var GRAPH_FILENAME = GRAPH_FILENAME || null;
        var completeFilename = GRAPH_FILENAME || "graphs/gridgraph.txt"; 
        Graph.loadInstance(completeFilename,function(error,text,completeFilename){
            console.log("error loading graph instance "+error + " from " + completeFilename +" text: "+text);
        }); 

        Tab.prototype.init.call(this);

    };

    /**
     * "Spult vor", führt den Algorithmus mit hoher Geschwindigkeit aus.
     * @method
     */
    this.fastForwardAlgorithm = function() {
        
        algo.fastForwardIntervalID = window.setInterval(function() {
            algo.nextStepChoice();
        }, fastForwardSpeed);

        algo.update();
    };

    /**
     * Stoppt das automatische Abspielen des Algorithmus
     * @method
     */
    this.stopFastForward = function() {
        window.clearInterval(algo.fastForwardIntervalID);
        algo.fastForwardIntervalID = null;
        d3.select("#tf1_button_vorspulen").property("checked",false);
        $("#tf1_button_vorspulen").button("option",fastforwardOptions);
        algo.update();
    };


    algo.stopFastForward = this.stopFastForward;
}

// Vererbung realisieren
ExerciseTab1.prototype = Object.create(Tab.prototype);
ExerciseTab1.prototype.constructor = ExerciseTab1;

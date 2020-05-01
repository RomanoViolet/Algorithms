/**
 * @author Lisa Velden
 * Text Management
 */

/********************************************** Erklärungstext / Anzeige in Pseudocode **********************************************/

/**
 * Passe Text in Erklärungs- und Pseudocodebereich an.
 * @method
 * @param {Number} statusID
 * @param {String} tabprefix
 */
function changeTextDijkstra(statusID, tabprefix) {

    if (statusID > 0) {
        // Titel "Warteschlange" anzeigen
        $("#" + tabprefix + "_title_pq").show();
    }
    else {
        $("#" + tabprefix + "_title_pq").hide();
    }

    switch(statusID) {
        case -1:
            if (tabprefix === "ta") {
                $("#" + tabprefix + "_div_statusText").html("<h3>" + LNG.K('algorithm_msg_root1') + "</h3>" + "<p>" + LNG.K('algorithm_msg_root2') + "</p>" + "<p>" + LNG.K('algorithm_msg_root3') + "</p>");
            }
            else if (tabprefix === "tf1") {
                $("#" + tabprefix + "_div_statusText").html("<p>" + LNG.K('aufgabe1_msg_start') + "</p>");
            }
            break;

        case 0:
            $("#" + tabprefix + "_div_statusErklaerung").removeClass("ui-state-error");
            $("#" + tabprefix + "_div_statusText").html("<h3>" + LNG.K('algorithm_msg_start') + "</h3>");
            break;

        case 1:
            // Initialisierung
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_dij_0_1')+"</h3>" + "<p>"+LNG.K('textdb_dij_0_2')+"</p>" + "<p>"+LNG.K('textdb_dij_0_3')+"</p>" + "<p>"+LNG.K('textdb_dij_0_4')+"</p>");
            break;

        case 2:
            // Minimum aus Priority Queue entnehmen
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_dij_1_1')+"</h3>" + "<p>"+LNG.K('textdb_dij_1_2')+"</p>");
            break;

        case 3:
            // Über ausgehende Kanten/Nachbarknoten iterieren
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_dij_2_1')+"</h3>" + "<p> "+LNG.K('textdb_dij_2_2')+"</p>");
            break;

        case 4:
            // Priortät bereits besuchter Knoten verringern
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_dij_3_1')+"</h3>" + "<p>"+LNG.K('textdb_dij_3_2')+"</p>" + "<p>"+LNG.K('textdb_dij_3_3')+"</p>");
            break;

        case 5:
            // Neuen Knoten in Priority Queue einfügen
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_dij_4_1')+"</h3>" + "<p>"+LNG.K('textdb_dij_4_2')+"</p>");
            break;

        case 6:
            // Bereits abgearbeiten Knoten markieren
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_dij_5_1')+"</h3>");
            break;

        case 7:
            // Algortihmus ist zu Ende
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_dij_6_1')+"</h3>");
            if (tabprefix === "ta") {
                $("#ta_div_statusText").append("<p></p><h3>"+LNG.K('algorithm_status_2')+"</h3>");
                $("#ta_div_statusText").append("<button id=ta_button_gotoIdee>"+LNG.K('algorithm_button_5')+"</button>");
                $("#ta_div_statusText").append("<h3>"+LNG.K('algorithm_status_3')+"</h3>");
                $("#ta_div_statusText").append("<button id=ta_button_gotoFA1>"+LNG.K('algorithm_button_6')+"</button>");
                $("#ta_div_statusText").append("<button id=ta_button_gotoFA2>"+LNG.K('algorithm_button_7')+"</button>");
                $("#ta_div_statusText").append("<button id=ta_button_gotoFA3>"+LNG.K('algorithm_button_8')+"</button>");
                $("#ta_button_gotoIdee").button();
                $("#ta_button_gotoFA1").button();
                $("#ta_button_gotoFA2").button();
                $("#ta_button_gotoFA3").button();
                $("#ta_button_gotoIdee").click(function() {
                    $("#tabs").tabs("option", "active", 3);
                });
                $("#ta_button_gotoFA1").click(function() {
                    $("#tabs").tabs("option", "active", 4);
                });
                $("#ta_button_gotoFA2").click(function() {
                    $("#tabs").tabs("option", "active", 5);
                });
                $("#ta_button_gotoFA3").click(function() {
                    $("#tabs").tabs("option", "active", 6);
                });
            };        

            //"Warteschlange" entfernen
            $("#" + tabprefix + "_title_pq").hide();
            break;

        default:
            console.log("Wrong Status ID");
    }
};

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
function changeTextAStar(statusID, tabprefix) {

    if (statusID > 1) {
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
            else if (tabprefix === "tf2") {
                $("#" + tabprefix + "_div_statusText").html("<p>" + LNG.K('aufgabe2_msg_start') + "</p>");
            }
            break;

        case 0:
            if (tabprefix === "ta") {
                $("#" + tabprefix + "_div_statusText").html("<h3>" + LNG.K('algorithm_msg_target1') + "</h3>" + "<p>" + LNG.K('algorithm_msg_target2') + "</p>" + "<p>" + LNG.K('algorithm_msg_target3') + "</p>");
            }
            break;
        case 1:
            $("#" + tabprefix + "_div_statusErklaerung").removeClass("ui-state-error");
            $("#" + tabprefix + "_div_statusText").html("<h3>" + LNG.K('algorithm_msg_start') + "</h3>");
            break;
        case 2:
            // Initialisierung
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_0_1')+"</h3>" + "<p>"+LNG.K('textdb_0_2')+"</p>" + "<p>"+LNG.K('textdb_0_3')+"</p>" + "<p>"+LNG.K('textdb_0_4')+"</p>");

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l2").addClass("marked");
            $("#" + tabprefix + "_p_l3").addClass("marked");
            $("#" + tabprefix + "_p_l4").addClass("marked");
            $("#" + tabprefix + "_p_l5").addClass("marked");
            break;

        case 3:
            // Minimum aus Priority Queue entnehmen
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_1_1')+"</h3>" + "<p>"+LNG.K('textdb_1_2')+"</p><p>"+LNG.K('textdb_1_3')+"</p>");

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l7").addClass("marked");
            break;

        case 4:
            // Über ausgehende Kanten/Nachbarknoten iterieren
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_2_1')+"</h3>" + "<p>"+LNG.K('textdb_2_2')+"</p>");

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l9").addClass("marked");
            $("#" + tabprefix + "_p_l10").addClass("marked");
            break;

        case 5:
            // Priortät bereits besuchter Knoten verringern
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_3_1')+"</h3>" + "<p>"+LNG.K('textdb_3_2')+"</p>" + "<p>"+LNG.K('textdb_3_3')+":</p>");

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l11").addClass("marked");
            $("#" + tabprefix + "_p_l12").addClass("marked");
            $("#" + tabprefix + "_p_l13").addClass("marked");
            break;

        case 6:
            // Neuen Knoten in Priority Queue einfügen
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_4_1')+"</h3>" + "<p>"+LNG.K('textdb_4_2')+"</p><p>"+LNG.K('textdb_4_3')+":</p>");

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l14").addClass("marked");
            $("#" + tabprefix + "_p_l15").addClass("marked");
            $("#" + tabprefix + "_p_l16").addClass("marked");
            break;

        case 7:
            // Bereits abgearbeiten Knoten markieren
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_5_1')+"</h3>");

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l6").addClass("marked");
            break;

        case 8:
            // Algortihmus ist zu Ende und es wurde ein kürzester Weg gefunden
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_6_1')+"</h3>");
            if (tabprefix === "ta") {
                $("#ta_div_statusText").append("<p>"+LNG.K('textdb_6_2')+"</p> <p>"+LNG.K('textdb_6_3')+"</p>");
            };

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l8").addClass("marked");
            $("#" + tabprefix + "_p_l18").addClass("marked");

            //"Warteschlange" entfernen
            $("#" + tabprefix + "_title_pq").hide();
            break;

        case 9:
            // Algortihmus ist zu Ende, es wurde aber kein kürzester Weg gefunden
            // Erklärung im Statusfenster
            $("#" + tabprefix + "_div_statusText").html("<h3>"+LNG.K('textdb_7_1')+"</h3>");
            if (tabprefix === "ta") {
                $("#ta_div_statusText").append("<p>"+LNG.K('textdb_7_2')+"</p>");
            };

            // Markierungen im Pseudocode
            $(".marked").removeClass("marked");
            $("#" + tabprefix + "_p_l17").addClass("marked");
            $("#" + tabprefix + "_p_l18").addClass("marked");

            //"Warteschlange" entfernen
            $("#" + tabprefix + "_title_pq").hide();
            break;

        default:
            console.log("Fehlerhafte StatusID.");
    }
};

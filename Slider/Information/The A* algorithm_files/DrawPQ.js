/**
 * Zeichnet den Inhalt der Priority Queue bzw. die ersten 10 Elemente
 * @param {Object} ctx
 * @param {PriorityQueue} pqueue
 */
function drawPQ(ctx, pqueue) {
    var j = 0;
    // Zeilenzähler
    ctx.beginPath();
    ctx.fillStyle = '#0096FF';
    ctx.strokeStyle = '#444444';

    ctx.font = "18px Arial";

    var startY = 8;
    // Y-Höhe ab der PQ gezeichnet wird
    var breadth = 30;
    // Kastenbreite der PQ-Elemente
    var height = 30;
    // Kastenhöhe der PQ-Elemente
    var row = height + 10;
    // Abstand für nächste Reihe
    var offset = 8;
    // Abstand Kasten zu Rand horizontal
    var margin = 6;
    // Abstand Text zu Rand vertikal
    var elemNumber = 9;
    // Anzahl der PQ-Elemente pro Zeile

    for (var i = 0; i < Math.min(18, pqueue.length()); i++) {

        ctx.moveTo(offset + (i - j * elemNumber) * breadth, startY + j * row);
        ctx.lineTo(offset + (i - j * elemNumber) * breadth, startY + height + j * row);
        ctx.lineTo((i - j * elemNumber) * breadth + (breadth + offset), startY + height + j * row);
        ctx.lineTo((i - j * elemNumber) * breadth + (breadth + offset), startY + j * row);
        ctx.lineTo(offset + (i - j * elemNumber) * breadth, startY + j * row);

        var content = pqueue.queue[i].getValue().id;
        content = GraphDrawer.prototype.mapNodeIDToName(content);

        ctx.fillText(content, 2 * offset + (breadth * (i - j * elemNumber)), startY + 0.5 * height + margin + j * row);

        if (i == 8) {
            j++;
            // zweite Zeile
        } else if (i == 17) {
            ctx.font = "24px Arial";
            ctx.fillText("...", offset, startY + 0.5 * height + 2 * row);
            // mehr als 10 Elemente
        }
        ;

    };

    ctx.stroke();
    ctx.closePath();
};

/**
 * Löscht Bereich, auf dem die Priority Queue gezeichnet wird
 * @method
 */
function clearPQ() {
    var c1 = $("#pqCanvas");
    // Bestimme Kontext des Canvas, auf denen die Priority Queue gezeichnet wird
    var pqCtx = c1[0].getContext("2d");
    pqCtx.clearRect(0, 0, c1.width(), c1.height());
};

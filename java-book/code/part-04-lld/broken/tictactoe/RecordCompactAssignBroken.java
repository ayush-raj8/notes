public class RecordCompactAssignBroken {
  record Cell(int row, int col) {
    Cell {
      this.row = Math.abs(row);
    }
  }
}

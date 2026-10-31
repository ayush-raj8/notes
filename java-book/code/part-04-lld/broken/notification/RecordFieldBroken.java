public class RecordFieldBroken {
  record Alert(String text) {
    int retries;
  }
}

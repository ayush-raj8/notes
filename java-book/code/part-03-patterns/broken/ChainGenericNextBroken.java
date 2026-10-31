final class Handler<T> {
  private Handler<T> next;

  void setNext(Handler<T> next) {
    this.next = next;
  }
}

public final class ChainGenericNextBroken {
  public static void main(String[] args) {
    Handler<String> names = new Handler<>();
    Handler<Integer> numbers = new Handler<>();
    names.setNext(numbers);
  }
}

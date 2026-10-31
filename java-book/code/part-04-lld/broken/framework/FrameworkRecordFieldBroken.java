import lld.framework.Requirement;

public class FrameworkRecordFieldBroken {
  public static void main(String[] args) {
    Requirement r = Requirement.functional("User logs In");
    r.text = "changed";
    System.out.println(r);
  }
}

import lld.fs.FileNode;

public class PackagePrivateCtorBroken {
  public static void main(String[] args) {
    FileNode f = new FileNode("a.txt", "hello");
    System.out.println(f.read());
  }
}

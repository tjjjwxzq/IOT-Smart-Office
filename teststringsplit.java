public class teststringsplit{
  public static void main(String[] args)
  {
    String test1 = "Data+1";
    String test2 = "Alert Level 1";

    for(String str : test1.split("\\+"))
      System.out.println(str);
    for(String str : test2.split("\\+"))
      System.out.println(str);

  }
}

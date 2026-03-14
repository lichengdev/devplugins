package org.cuiq.util;

public class ColourLogUtil
{
  public static String redText(Object text, boolean highlight)
  {
    return "\033[31" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
  
  private static String isHighlight(boolean highlight)
  {
    return highlight ? ";1" : "";
  }
  
  public static String greenText(Object text, boolean highlight)
  {
    return "\033[32" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
  
  public static String yellowText(Object text, boolean highlight)
  {
    return "\033[33" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
  
  public static String blueText(Object text, boolean highlight)
  {
    return "\033[34" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
  
  public static String purpleText(Object text, boolean highlight)
  {
    return "\033[35" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
  
  public static String babyBlueText(Object text, boolean highlight)
  {
    return "\033[36" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
  
  public static String whiteText(Object text, boolean highlight)
  {
    return "\033[37" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
  
  public static String blackText(Object text, boolean highlight)
  {
    return "\033[30" + isHighlight(highlight) + "m" + text + "\033[0m";
  }
}

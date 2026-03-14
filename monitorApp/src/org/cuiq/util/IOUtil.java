package org.cuiq.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IOUtil
{
  public static final int EOF = -1;
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  
  public static int copy(InputStream input, OutputStream output)
    throws IOException
  {
    long count = copyLarge(input, output);
    if (count > 2147483647L) {
      return -1;
    }
    return (int)count;
  }
  
  public static long copyLarge(InputStream input, OutputStream output)
    throws IOException
  {
    return copy(input, output, 4096);
  }
  
  public static long copy(InputStream input, OutputStream output, int bufferSize)
    throws IOException
  {
    return copyLarge(input, output, new byte[bufferSize]);
  }
  
  public static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
    throws IOException
  {
    long count = 0L;
    int n;
    while (-1 != (n = input.read(buffer)))
    {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }
  
  public static long copyLarge(Reader input, Writer output)
    throws IOException
  {
    return copyLarge(input, output, new char[4096]);
  }
  
  public static long copyLarge(Reader input, Writer output, char[] buffer)
    throws IOException
  {
    long count = 0L;
    int n;
    while (-1 != (n = input.read(buffer)))
    {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }
  
  public static void closeQuietly(Closeable closeable)
  {
    try
    {
      if (closeable != null) {
        closeable.close();
      }
    }
    catch (IOException localIOException) {}
  }
}

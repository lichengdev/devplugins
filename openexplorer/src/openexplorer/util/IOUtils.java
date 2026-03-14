package openexplorer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOUtils
{
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    
    public static long copyLarge(Reader input, Writer output) throws IOException
    {
        char[] buffer = new char[4096];
        long count = 0L;
        
        int n;
        for (n = 0; -1 != (n = input.read(buffer)); count += (long) n)
        {
            output.write(buffer, 0, n);
        }
        
        return count;
    }
    
    public static int copy(Reader input, Writer output) throws IOException
    {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int) count;
    }
    
    public static void copy(InputStream input, Writer output) throws IOException
    {
        InputStreamReader in = new InputStreamReader(input);
        copy((Reader) in, output);
    }
    
    public static String toString(InputStream input) throws IOException
    {
        StringWriter sw = new StringWriter();
        copy((InputStream) input, sw);
        return sw.toString();
    }
}

package IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;


public class FileOutpuStreamToWriter extends FileWriter {
    FileOutputStream fileOutputStream;



    @Override
    public void write(int c) throws IOException {
        fileOutputStream.write(c);
    }


    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
        fileOutputStream.write((new String(cbuf)).getBytes("windows-1251"), off, len);
    }


    @Override
    public void write(String str, int off, int len) throws IOException {
        fileOutputStream.write(str.getBytes("windows-1251"), off, len);
    }




    public FileOutpuStreamToWriter(String path) throws IOException {
        super(new File(path));
        fileOutputStream = new FileOutputStream(new File(path));
    }
}

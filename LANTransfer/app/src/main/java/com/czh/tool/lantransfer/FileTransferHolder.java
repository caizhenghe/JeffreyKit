package com.czh.tool.lantransfer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTransferHolder {
    private String fileName;
    private File receivedFile;
    private BufferedOutputStream fileOutPutStream;
    private long totalSize;


    public BufferedOutputStream getFileOutPutStream() {
        return fileOutPutStream;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        totalSize = 0;
        if (!Constants.DIR.exists()) {
            Constants.DIR.mkdirs();
        }
        this.receivedFile = new File(Constants.DIR, this.fileName);
//            Timber.d(receivedFile.getAbsolutePath());
        try {
            fileOutPutStream = new BufferedOutputStream(new FileOutputStream(receivedFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        if (fileOutPutStream != null) {
            try {
                fileOutPutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileOutPutStream = null;
    }

    public void write(byte[] data) {
        if (fileOutPutStream != null) {
            try {
                fileOutPutStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        totalSize += data.length;
    }
}

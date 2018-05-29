package com.czh.tool.lantransfer;

public interface FileTransferContract {
    interface View {
        void showIpAddress(String ip);
        void updateHelp(String ip);
        boolean isActive();
    }

    interface Presenter {
        void getIpAddress();
        void startServer();
        void stopServer();
    }
}

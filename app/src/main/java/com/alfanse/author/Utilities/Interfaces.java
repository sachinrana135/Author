package com.alfanse.author.Utilities;

/**
 * Created by Velocity-1601 on 6/18/2017.
 */

public class Interfaces {

    public interface uploadFileListener {

        void onUploadFileStart();

        void onUploadFileProgressUpdate(int updateProgress);

        void onUploadFileFinish();

    }
}

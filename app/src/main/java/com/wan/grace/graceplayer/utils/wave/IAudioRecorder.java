package com.wan.grace.graceplayer.utils.wave;

/**
 * Interface for audio recorder
 */
interface IAudioRecorder {
    void startRecord();
    void finishRecord();
    boolean isRecording();
}

package com.jay.contact.event;

public class ProgressValue {
    public ProgressValue() {
    }

    public ProgressValue(int progress) {
        this.progress = progress;
    }

    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

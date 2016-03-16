package fr.gstraymond.biz;

import android.widget.ProgressBar;

public class ProgressBarUpdater implements ElasticSearchClient.CallBacks {

    private ProgressBar progressBar;

    public ProgressBarUpdater(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void start() {
        progressBar.setProgress(0);
    }

    @Override
    public void buildRequest() {
        progressBar.setProgress(33);
    }

    @Override
    public void getResponse() {
        progressBar.setProgress(66);
    }

    @Override
    public void end() {
        progressBar.setProgress(100);
    }
}

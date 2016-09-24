package fr.gstraymond.network;

public class Result<Elem> {
    public Elem elem;
    public long httpDuration;
    public long parseDuration;

    Result(Elem elem, long httpDuration, long parseDuration) {
        this.elem = elem;
        this.httpDuration = httpDuration;
        this.parseDuration = parseDuration;
    }
}

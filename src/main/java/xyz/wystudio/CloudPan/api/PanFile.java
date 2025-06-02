package xyz.wystudio.CloudPan.api;

public interface PanFile {
    public String getName();
    public Long getSize();
    public boolean isDirectory();
    public boolean isFile();
    public String getLastModified();
}

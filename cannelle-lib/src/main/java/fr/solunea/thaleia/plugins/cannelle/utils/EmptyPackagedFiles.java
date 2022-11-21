package fr.solunea.thaleia.plugins.cannelle.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class EmptyPackagedFiles extends PackagedFiles {

    public EmptyPackagedFiles() {
        super();
    }

    @Override
    public File getFile(String nameFile) {
        return null;
    }

    @Override
    public String getSizeFile(String nameFile) {
        return "";
    }

    @Override
    public String getFileExtension(String nameFile) {
        return "";
    }

    @Override
    public String findStringFile(String nameFile) {
        return "";
    }

    @Override
    public Collection<File> listFiles(String[] extensions, boolean recursive) {
        return new ArrayList<>();
    }

    @Override
    public Collection<String> listFilenamesWithoutExtension(String[] extensions, boolean recursive) {
        return new ArrayList<>();
    }

    @Override
    public void normalizeFiles() {
    }
}

package fr.solunea.thaleia.plugins.cannelle.v6.uctranslatemodule;

import java.util.Optional;

public interface ITranslatable {

    default Optional<String> getTranslatableValue() {
        Optional<String> s = Optional.empty();
        return s;
    }

}
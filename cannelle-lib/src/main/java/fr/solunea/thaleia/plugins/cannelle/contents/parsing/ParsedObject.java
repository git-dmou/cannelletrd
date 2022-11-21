package fr.solunea.thaleia.plugins.cannelle.contents.parsing;

public class ParsedObject<T> implements IParsedObject<T> {

	private T object;

	private String originalLocation;

	public ParsedObject(T object, String originalLocation) {
		this.object = object;
		this.originalLocation = originalLocation;
	}

	@Override
	public T getObject() {
		return object;
	}

	@Override
	public String getOriginalLocation() {
		return originalLocation;
	}

}

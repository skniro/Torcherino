package torcherino.config;

public interface TypeSerializer<T>
{
	String serialize(T data);

	T deserialize(String data);
}

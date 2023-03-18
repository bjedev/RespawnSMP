package dev.fluyd.respawnsmp.utils;

import dev.fluyd.respawnsmp.RespawnSMP;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;

public final class Serialize {
	private static final String DATA_FOLDER = RespawnSMP.INSTANCE.getDataFolder().getPath();

	public static final void serialize(Object object, String fileName) throws IOException {
		serialize(object, fileName, DataType.DATA);
	}

	public enum DataType {
		DATA(new File(String.format("%s/data/", DATA_FOLDER))),
		PLAYERDATA(new File(String.format("%s/player-data/", DATA_FOLDER)));

		private final File type;

		private DataType(File type) {
			this.type = type;
		}

		public File getType() {
			return this.type;
		}
	}

	public static final void serialize(Object object, String fileName, DataType type) throws IOException {
		if (!(object instanceof Serializable)) {
			throw new NotSerializableException("Object cannot be serialized as it does not implement Serializable!");
		} else {
			File file = new File(type.getType(), fileName + ".dat");
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
			oos.writeObject(object);
			fos.close();
			oos.close();
		}
	}

	public static final Object deserialize(String fileName) throws Exception {
		return deserialize(fileName, DataType.DATA);
	}

	public static final Object deserialize(String fileName, DataType type) throws Exception {
		File file = new File(type.getType(), fileName + ".dat");
		if (!file.exists()) {
			throw new FileNotFoundException("That file could not be found!");
		} else {
			FileInputStream fis = new FileInputStream(file);
			BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
			Object object = ois.readObject();
			fis.close();
			ois.close();
			return object;
		}
	}

	public static final boolean exists(String fileName, DataType type) {
		File file = new File(type.getType(), fileName + ".dat");
		return file.exists();
	}
}
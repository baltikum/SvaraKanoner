package common;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * A static utility class for saving down class fields in the ini format.
 * Only supports primitive fields and strings as field types.
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class IniStream {

    /** Read ini file entries into an object.
     * @param obj The objects fields to fill from the file.
     * @param file The input file.
     * @throws IOException If the file doesn't exist or for other reasons can't be written to.
     */
    public static void read(Object obj, File file) throws IOException {
       try (BufferedReader input = new BufferedReader(new FileReader(file))){
            while (input.ready()){
                String line = input.readLine();
                line = line.strip();
                if (!line.startsWith("#")) {
                    String[] nameValue = line.split("=");
                    if (nameValue.length != 2) continue;
                    String name = nameValue[0].trim().toLowerCase(Locale.ROOT);
                    String value = nameValue[1].trim().toLowerCase(Locale.ROOT);

                    Field field = null;
                    for (Field it : obj.getClass().getDeclaredFields()) {
                        if (it.getName().toLowerCase(Locale.ROOT).contentEquals(name)) {
                            field = it;
                            break;
                        }
                    }
                    if (field == null) throw new IOException("Illegal entry no field '" + nameValue[0] + "'.");

                    Class<?> type = field.getType();

                    if (type.isPrimitive()) {
                        if (type.isAssignableFrom(boolean.class))
                            field.set(obj, Boolean.valueOf(value));
                        else if (type.isAssignableFrom(byte.class))
                            field.set(obj, Byte.valueOf(value));
                        else if (type.isAssignableFrom(short.class))
                            field.set(obj, Short.valueOf(value));
                        else if (type.isAssignableFrom(int.class))
                            field.set(obj, Integer.valueOf(value));
                        else if (type.isAssignableFrom(long.class))
                            field.set(obj, Long.valueOf(value));
                        else if (type.isAssignableFrom(float.class))
                            field.set(obj, Float.valueOf(value));
                        else if (type.isAssignableFrom(double.class))
                            field.set(obj, Double.valueOf(value));
                        else
                            throw new IllegalArgumentException("Unknown primitive type. ");
                    } else if (type.isAssignableFrom(String.class)) {
                        System.out.println("string");
                        field.set(obj, value);
                    } else {
                        throw new IllegalArgumentException("Unknown type for field '" + field.getName() + "'. ");
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw new IOException("Syntax error");
        } catch (IllegalAccessException e) {
            throw new IOException("Illegal entry");
        }
    }


    /** Writes an objects fields as entries to an ini file.
     * @param obj The objects fields to write.
     * @param file The output file.
     * @throws IOException If the file doesn't exist or for other reasons can't be written to.
     *                     Or if the field don't exist for an entry or any other syntax error.
     */
    public static void write(Object obj, File file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)){
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type.isPrimitive()) {
                    if (type.isAssignableFrom(boolean.class))
                        writeBoolean(out, field.getName(), (boolean) field.get(obj));
                    else if (type.isAssignableFrom(byte.class))
                        writeInt(out, field.getName(), (byte) field.get(obj));
                    else if (type.isAssignableFrom(short.class))
                        writeInt(out, field.getName(), (short) field.get(obj));
                    else if (type.isAssignableFrom(int.class))
                        writeInt(out, field.getName(), (int) field.get(obj));
                    else if (type.isAssignableFrom(long.class))
                        writeInt(out, field.getName(), (long) field.get(obj));
                    else if (type.isAssignableFrom(float.class))
                        writeDecimal(out, field.getName(), (float) field.get(obj));
                    else if (type.isAssignableFrom(double.class))
                        writeDecimal(out, field.getName(), (double) field.get(obj));
                    else
                        throw new IOException("Unknown primitive type. ");
                } else if (type.isAssignableFrom(String.class)) {
                    writeString(out, field.getName(), (String)field.get(obj));
                } else {
                    throw new IOException("Unknown type for field '" + field.getName() + "'. ");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void writeBoolean(FileOutputStream out, String fieldName, boolean value) throws IOException {
        out.write(fieldName.getBytes());
        out.write('=');
        out.write(String.valueOf(value).getBytes());
        out.write('\n');
    }

    private static void writeInt(FileOutputStream out, String fieldName, long integer) throws IOException {
        out.write(fieldName.getBytes());
        out.write('=');
        out.write(String.valueOf(integer).getBytes());
        out.write('\n');
    }

    private static void writeDecimal(FileOutputStream out, String fieldName, double decimal) throws IOException {
        out.write(fieldName.getBytes());
        out.write('=');
        out.write(String.valueOf(decimal).getBytes());
        out.write('\n');
    }

    private static void writeString(FileOutputStream out, String fieldName, String str) throws IOException {
        out.write(fieldName.getBytes());
        out.write('=');
        out.write(str.getBytes());
        out.write('\n');
    }
}

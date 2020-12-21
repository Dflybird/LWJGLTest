package utils;

import graphic.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gq
 * @Date 2020/12/20 20:57
 * @Version 1.0
 **/
public class ObjLoader {

    public static Mesh loadObj(String fileName) {
        File file = new File(fileName);
        String line;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] slices = line.split("\\s+");
                switch (slices[0]) {
                    case "v":
                        vertices.add(new Vector3f(
                                Float.parseFloat(slices[1]),
                                Float.parseFloat(slices[2]),
                                Float.parseFloat(slices[3])));
                        break;
                    case "vt":
                        textures.add(new Vector2f(
                                Float.parseFloat(slices[1]),
                                Float.parseFloat(slices[2])));
                        break;
                    case "vn":
                        normals.add(new Vector3f(
                                Float.parseFloat(slices[1]),
                                Float.parseFloat(slices[2]),
                                Float.parseFloat(slices[3])));
                        break;
                    case "f":
                        faces.add(new Face(slices[1], slices[2], slices[3]));
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    private static class Face{

        public Face(String group1, String group2, String group3) {
        }
    }
}

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    HashMap<String, Vertex> vertices;

    public Graph() {
        this.vertices = new HashMap<>();
    }

    public void addVertex(Vertex vertex) {
        if (!vertices.containsKey(vertex.label)) {
            vertices.put(vertex.label, vertex);
        }
    }

    public void addEdge(String label1, String label2) {
        Vertex v1 = vertices.get(label1);
        Vertex v2 = vertices.get(label2);

        if (v1 != null && v2 != null) {
            v1.neighbors.add(v2);
            v2.neighbors.add(v1);
        }
    }

    public boolean hasEdge(String label1, String label2) {
        Vertex v1 = vertices.get(label1);
        Vertex v2 = vertices.get(label2);

        if (v1 != null && v2 != null) {
            for (Vertex neighbor : v1.neighbors) {
                if (neighbor.label.equals(label2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeVertex(String label) {
        Vertex vertexToRemove = vertices.remove(label);

        if (vertexToRemove != null) {
            for (Vertex v : vertices.values()) {
                v.neighbors.removeIf(neighbor -> neighbor.label.equals(label));
            }
        }
    }

    public void removeEdge(String label1, String label2) {
        Vertex v1 = vertices.get(label1);
        Vertex v2 = vertices.get(label2);

        if (v1 != null && v2 != null) {
            v1.neighbors.removeIf(neighbor -> neighbor.label.equals(label2));
            v2.neighbors.removeIf(neighbor -> neighbor.label.equals(label1));
        }
    }

    public void printGraph() {
        int longest = 7;
        for (String str : vertices.keySet()) {
            longest = Math.max(longest, str.length() + 1);
        }

        String line = "Vertex ";
        for (int i = 7; i < longest; i++)
            line += " ";
        int leftLength = line.length();
        line += "| Adjacent Vertices";
        System.out.println(line);

        for (int i = 0; i < line.length(); i++) {
            System.out.print("-");
        }
        System.out.println();

        for (String str : vertices.keySet()) {
            Vertex v1 = vertices.get(str);

            for (int i = str.length(); i < leftLength; i++) {
                str += " ";
            }
            System.out.print(str + "| ");

            for (int i = 0; i < v1.neighbors.size() - 1; i++) {
                System.out.print(v1.neighbors.get(i).label + ", ");
            }

            if (!v1.neighbors.isEmpty()) {
                System.out.print(v1.neighbors.get(v1.neighbors.size() - 1).label);
            }

            System.out.println();
        }
    }

    public boolean depthFirstSearch(String s, String t, List<String> path) {
        HashSet<Vertex> explored = new HashSet<>();
        LinkedList<Vertex> stack = new LinkedList<>();
        Vertex sVer = vertices.get(s);
        Vertex tVer = vertices.get(t);

        stack.add(sVer);
        while (!stack.isEmpty()) {
            Vertex v = stack.removeLast();
            if (!explored.contains(v)) {
                explored.add(v);
                path.add(v.label);

                if (v == tVer) {
                    return true;
                }

                for (Vertex n : v.neighbors) {
                    if (!explored.contains(n)) {
                        stack.add(n);
                    }
                }
            }
        }
        return false;
    }

    public boolean breadthFirstSearch(String s, String t, List<String> path) {
        HashMap<Vertex, Vertex> parentMap = new HashMap<>();
        HashSet<Vertex> explored = new HashSet<>();
        LinkedList<Vertex> queue = new LinkedList<>();
        Vertex sVer = vertices.get(s);
        Vertex tVer = vertices.get(t);

        queue.add(sVer);
        explored.add(sVer);

        while (!queue.isEmpty()) {
            Vertex v = queue.remove();

            for (Vertex n : v.neighbors) {
                if (!explored.contains(n)) {
                    queue.add(n);
                    explored.add(n);
                    parentMap.put(n, v);

                    if (n == tVer) {
                        Vertex current = tVer;
                        path.add(current.label);

                        while (current != sVer) {
                            current = parentMap.get(current);
                            path.add(current.label);
                        }


                        int start = 0;
                        int end = path.size() - 1;
                        while (start < end) {
                            String temp = path.get(start);
                            path.set(start, path.get(end));
                            path.set(end, temp);

                            start++;
                            end--;
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

}


class Vertex {
    //tip from sawyer -- each vertex can store position on graph
    String label;
    LinkedList<Vertex> neighbors;
    Point position;

    public Vertex(String label, Point position) {
        this.label = label;
        this.position = position;
        this.neighbors = new LinkedList<>();
    }
}



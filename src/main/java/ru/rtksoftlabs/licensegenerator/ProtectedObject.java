package ru.rtksoftlabs.licensegenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProtectedObject {
    private List<String> listOfStringsWithPathToAllLeafs;

    public String data;

    public List<ProtectedObject> children;

    public ProtectedObject() {
    }

    public ProtectedObject(String data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public ProtectedObject addChild(String child) {
        ProtectedObject childNode = new ProtectedObject(child);
        this.children.add(childNode);
        return childNode;
    }

    public List<String> generateListOfAllPathsToLeafs(ProtectedObject node, String accumulator) {
        listOfStringsWithPathToAllLeafs = new ArrayList<>();

        for (ProtectedObject child: node.children) {
            String elem = accumulator;
            elem += "/" + child.data;

            if ((child.children != null) && (child.children.size() > 0)) {
                generateListOfAllPathsToLeafs(child, elem);
            } else {
                listOfStringsWithPathToAllLeafs.add(elem);
            }
        }

        return listOfStringsWithPathToAllLeafs;
    }

    public List<String> returnListOfStringsWithPathToAllLeafs() {
        if (listOfStringsWithPathToAllLeafs == null) {
            generateListOfAllPathsToLeafs(this, data);
        }

        return listOfStringsWithPathToAllLeafs;
    }

    public boolean find(ProtectedObject protectedObject) {
        List<String> otherList = protectedObject.returnListOfStringsWithPathToAllLeafs();

        return returnListOfStringsWithPathToAllLeafs().containsAll(otherList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtectedObject that = (ProtectedObject) o;
        return Objects.equals(data, that.data) &&
                Objects.equals(children, that.children);
    }
}


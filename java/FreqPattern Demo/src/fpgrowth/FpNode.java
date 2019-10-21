package fpgrowth;

import java.io.Serializable;
import java.util.*;

/**
 * @description implementation of FpNode
 * @author FANG
 * @date 2019/10/20 16:13
 **/
public class FpNode implements Comparable<FpNode> {
    // item name
    private String name;
    // item count
    private int count;
    // parent node
    private FpNode parent;
    // child nodes
    private List<FpNode> children;
    // next node of the same item
    private FpNode next;

    @Override
    public int compareTo(FpNode node) {
        int count0 = node.getCount();
        // in descending order
        return count0 - this.count;
    }

    public FpNode() {

    }

    public FpNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FpNode getParent() {
        return parent;
    }

    public void setParent(FpNode parent) {
        this.parent = parent;
    }

    public List<FpNode> getChildren() {
        return children;
    }

    public void addChild(FpNode child) {
        if (this.getChildren() == null) {
            List<FpNode> list = new ArrayList<FpNode>();
            list.add(child);
            this.setChildren(list);
        } else {
            this.getChildren().add(child);
        }
    }

    public FpNode findChild(String name) {
        List<FpNode> children = this.getChildren();
        if (children != null) {
            for (FpNode child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void setChildren(List<FpNode> children) {
        this.children = children;
    }

    public void printChildren() {
        List<FpNode> children = this.getChildren();
        if (children != null) {
            for (FpNode child : children) {
                System.out.print(child.getName() + " ");
            }
        } else {
            System.out.print("null");
        }
    }

    public FpNode getNext() {
        return next;
    }

    public void setNext(FpNode next) {
        this.next = next;
    }

    public void increCount(int n) {
        this.count += n;
    }

}

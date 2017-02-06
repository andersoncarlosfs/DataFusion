/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.validation.constraints.NotNull;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * Disjoint-set data structure to keep track of a set of equivalence class
 * partitioned into a number of disjoint subsets
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public class QuotientSet implements Serializable {

    static final long serialVersionUID = 1L;

    public final RedBlackBalancedSearchTree tree = new RedBlackBalancedSearchTree();

    Collection<Collection<RDFNode>> x = new HashSet<>();

    public void put(RDFNode node) {
        tree.put(node);
        x.add(new HashSet<RDFNode>(Collections.singletonList(node)));
    }

    public void union(RDFNode subject, RDFNode object) {
        tree.union(subject, object);
    }

    public Collection<Collection<RDFNode>> asCollection() {
        return x;
    }

    /**
     *
     */
    public static class RedBlackBalancedSearchTree {

        private static final boolean RED = true;
        private static final boolean BLACK = false;

        private int size;

        private Node root;

        private boolean isRed(Node node) {
            if (node == null) {
                return false;
            }
            return node.color == RED;
        }

        private int size(Node node) {
            if (node == null) {
                return 0;
            }
            return node.size;
        }

        private Node get(Node node, RDFNode resource) {
            while (node != null) {
                int cmp = resource.toString().compareTo(node.resource.toString());
                if (cmp < 0) {
                    node = node.left;
                } else if (cmp > 0) {
                    node = node.right;
                } else {
                    return node;
                }
            }
            return null;
        }

        public void put(RDFNode resource) {
            if (resource == null) {
                throw new IllegalArgumentException();
            }
            root = put(root, resource, size++);
            root.color = BLACK;
        }

        private Node put(Node node, RDFNode resource, int index) {
            if (node == null) {
                return new Node(resource, index);
            }
            int cmp = resource.toString().compareTo(node.resource.toString());
            if (cmp < 0) {
                node.left = put(node.left, resource, index);
            } else if (cmp > 0) {
                node.right = put(node.right, resource, index);
            } else {
                node.index = index;
            }
            if (isRed(node.right) && !isRed(node.left)) {
                node = rotateLeft(node);
            }
            if (isRed(node.left) && isRed(node.left.left)) {
                node = rotateRight(node);
            }
            if (isRed(node.left) && isRed(node.right)) {
                node.color = !node.color;
                node.left.color = !node.left.color;
                node.right.color = !node.right.color;
            }
            node.size = size(node.left) + size(node.right) + 1;
            return node;
        }

        private Node rotateRight(Node node) {
            Node brancha = node.left;
            node.left = brancha.right;
            brancha.right = node;
            brancha.color = brancha.right.color;
            brancha.right.color = RED;
            brancha.size = node.size;
            node.size = size(node.left) + size(node.right) + 1;
            return brancha;
        }

        private Node rotateLeft(Node node) {
            Node brancha = node.right;
            node.right = brancha.left;
            brancha.left = node;
            brancha.color = brancha.left.color;
            brancha.left.color = RED;
            brancha.size = node.size;
            node.size = size(node.left) + size(node.right) + 1;
            return brancha;
        }

        private Node find(RDFNode resource) {
            Node root = get(this.root, resource);
            RDFNode parent = resource;
            while (root.resource != root.parent) {
                Node node = get(this.root, root.parent);
                root.parent = node.resource;
                root = node;
            }
            while (resource != parent) {
                Node node = get(this.root, resource);
                node.parent = root.resource;
                resource = node.parent;
            }
            return root;
        }

        public void union(RDFNode subject, RDFNode object) {
            Node subjectNode = find(subject);
            Node objectNode = find(object);
            if (subjectNode.parent == objectNode.parent) {
                return;
            }
            /**
             * Make the smaller root point to the larger one
             */
            if (subjectNode.rank < objectNode.rank) {
                subjectNode.parent = objectNode.resource;
                objectNode.rank += subjectNode.rank;
            } else {
                objectNode.parent = subjectNode.resource;
                subjectNode.rank += objectNode.rank;
            }
        }

        public Node get(RDFNode resource) {
            return get(root, resource);
        }

        /**
         *
         */
        public static class Node {

            @NotNull
            public final RDFNode resource;
            public int index;
            public Node left, right;
            public boolean color;
            public int size;
            public RDFNode parent;
            public int rank;

            public Node(RDFNode resource, int index) {
                this.resource = resource;
                this.index = index;
                this.color = RED;
                this.size = 1;
                this.parent = resource;
                this.rank = 1;
            }

            @Override
            public String toString() {
                if (resource != null) {
                    return resource.toString(); //To change body of generated methods, choose Tools | Templates.
                }
                return null;
            }

        }

    }

}

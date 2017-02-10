/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andersoncarlosfs.model.di;

import com.andersoncarlosfs.model.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.base.block.FileMode;
import org.apache.jena.tdb.sys.SystemTDB;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 * 2,35s
 *
 * @author Anderson Carlos Ferreira da Silva
 */
public abstract class DataIntegration_6M1 implements AutoCloseable {

    public static final Collection<Property> equivalenceProperties;

    static {

        //
        SystemTDB.setFileMode(FileMode.direct);

        //
        equivalenceProperties = new HashSet<>();
        equivalenceProperties.add(OWL.sameAs);
        equivalenceProperties.add(SKOS.exactMatch);

    }

    /**
     *
     * @return the temporaryDirectory
     */
    protected abstract Path getTemporaryDirectory();

    /**
     *
     * @param dataSources
     * @param equivalenceProperties
     * @return the equivalence classes
     * @throws IOException
     */
    public Collection<Collection<RDFNode>> findEquivalenceClasses(Collection<DataSource> dataSources, Collection<Property> equivalenceProperties) throws IOException {
        RedBlackBalancedSearchTree quotientSet = new RedBlackBalancedSearchTree();
        Model model = ModelFactory.createDefaultModel();
        model.read("../../../../Datasets/INA/links.n3");
        SimpleSelector selector = new SimpleSelector() {
            @Override
            public boolean test(Statement s) {
                return equivalenceProperties.contains(s.getPredicate());
            }
        };
        StmtIterator statements = model.listStatements(selector);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            RDFNode subject = statement.getSubject();
            RDFNode object = statement.getObject();
            quotientSet.put(subject);
            quotientSet.put(object);
            quotientSet.union(subject, object);
        }
        return quotientSet.asCollection();
    }

    /**
     *
     * @see AutoCloseable#close()
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

        //
        TDB.closedown();
        // Apache Commons IO
        FileUtils.forceDelete(getTemporaryDirectory().toFile());
        // 
        //Files.walk(getTemporaryDirectory()).map(Path::toFile).sorted(Comparator.comparing(File::isDirectory)).forEach(File::delete);
        //getTemporaryDirectory().toFile().delete();

    }

    /**
     * @see Object#finalize()
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    /**
     *
     * Disjoint-set data structure to keep track of a set of equivalence class
     * partitioned into a number of disjoint subsets
     *
     * @see <a href="http://algs4.cs.princeton.edu/15uf/">1.5 Case Study:
     * Union-Find</a>
     * @see <a href="http://algs4.cs.princeton.edu/33balanced/">3.3 Balanced
     * Search Trees</a>
     * @author Anderson Carlos Ferreira da Silva
     */
    private static class RedBlackBalancedSearchTree {

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

        public boolean isEmpty() {
            return root == null;
        }

        private Node get(RDFNode resource) {
            if (resource == null) {
                // TODO exception message here
                throw new IllegalArgumentException();
            }
            return get(root, resource);
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
                // TODO exception message here
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

        private RDFNode min() {
            if (isEmpty()) {
                // TODO exception message here
                throw new NoSuchElementException();
            }
            return min(root).resource;
        }

        private Node min(Node node) {
            if (node.left == null) {
                return node;
            } else {
                return min(node.left);
            }
        }

        public RDFNode max() {
            if (isEmpty()) {
                // TODO exception message here
                throw new NoSuchElementException();
            }
            return max(root).resource;
        }

        private Node max(Node node) {
            if (node.right == null) {
                return node;
            } else {
                return max(node.right);
            }
        }

        /**
         *
         * @param resource
         * @return
         */
        private Node find(RDFNode resource) {
            Node root = get(resource);
            RDFNode parent = resource;
            while (root.resource != root.parent) {
                Node node = get(root.parent);
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

        /**
         * *
         *
         * @param subject
         * @param object
         */
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

        private Collection<Collection<RDFNode>> toCollection(Node node, Collection<Collection<RDFNode>> quotientSet, RDFNode min, RDFNode max) {
            if (node != null) {
                int cmpMin = min.toString().compareTo(node.resource.toString());
                int cmpMax = max.toString().compareTo(node.resource.toString());
                if (cmpMin < 0) {
                    toCollection(node.left, quotientSet, min, max);
                }
                if (cmpMin <= 0 && cmpMax >= 0) {
                    Collection<RDFNode> equivalenceClass = new HashSet<>();
                    for (Collection<RDFNode> subset : quotientSet) {
                        if (subset.contains(node.resource) || subset.contains(node.parent)) {
                            equivalenceClass = subset;
                            break;
                        }
                    }
                    if (equivalenceClass.isEmpty()) {
                        quotientSet.add(equivalenceClass);
                    }
                    equivalenceClass.add(node.resource);
                    equivalenceClass.add(node.parent);
                }
                if (cmpMax > 0) {
                    toCollection(node.right, quotientSet, min, max);
                }
            }
            return quotientSet;
        }

        public Collection<Collection<RDFNode>> asCollection() {
            if (isEmpty()) {
                return Collections.emptySet();
            }
            return toCollection(root, new HashSet<Collection<RDFNode>>(), min(), max());
        }

        /**
         *
         */
        private static class Node {

            @NotNull
            private final RDFNode resource;
            private int index;
            private Node left, right;
            private boolean color;
            private int size;
            private RDFNode parent;
            private int rank;

            private Node(RDFNode resource, int index) {
                this.resource = resource;
                this.index = index;
                this.color = RED;
                this.size = 1;
                this.parent = resource;
                this.rank = 1;
            }

        }

    }

}

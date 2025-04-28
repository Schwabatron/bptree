package bptree;

import java.io.IOException;

/**
 * The {@code BPlusTree} class implements B+-trees.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public abstract class BPlusTree<K extends Comparable<K>, P> {

	/**
	 * An {@code InvalidInsertionException} is thrown when a key already existent in a {@code BPlusTree} is attempted to
	 * be inserted again in the {@code BPlusTree}.
	 * 
	 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
	 *
	 */
	public static class InvalidInsertionException extends Exception {

		public InvalidInsertionException(String msg) {
			super(msg);
		}

		/**
		 * An automatically generated serial version UID.
		 */
		private static final long serialVersionUID = -2281189104087198670L;

	}

	/**
	 * An {@code InvalidDeletionException} is thrown when a key non-existent in a {@code BPlusTree} is attempted to be
	 * deleted from the {@code BPlusTree}.
	 * 
	 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
	 *
	 */
	public static class InvalidDeletionException extends Exception {

		public InvalidDeletionException(String msg) {
			super(msg);
		}

		/**
		 * An automatically generated serial version UID.
		 */
		private static final long serialVersionUID = -2281189104087198670L;

	}

	/**
	 * The maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have.
	 */
	protected int degree;

	/**
	 * Constructs a {@code BPlusTree}.
	 * 
	 * @param degree
	 *            the maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have
	 */
	public BPlusTree(int degree) {
		this.degree = degree;
	}

	/**
	 * Returns the degree of this {@code BPlusTree}.
	 * 
	 * @return the degree of this {@code BPlusTree}
	 */
	public int degree() {
		return degree;
	}

	/**
	 * Returns the root {@code Node} of this {@code BPlusTree}.
	 * 
	 * @return the root {@code Node} of this {@code BPlusTree}; {@code null} if this {@code BPlusTree} is empty
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public abstract Node<K> root() throws IOException;

	/**
	 * Returns the specified child {@code Node} of the specified {@code NonLeafNode}.
	 * 
	 * @param node
	 *            a {@code NonLeafNode}
	 * @param i
	 *            the index of the child {@code Node}
	 * @return the specified child {@code Node} of the specified {@code NonLeafNode}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public abstract Node<K> child(NonLeafNode<K> node, int i) throws IOException;

	/**
	 * Inserts the specified key and pointer into this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to insert
	 * @param p
	 *            the pointer to insert
	 * @throws InvalidInsertionException
	 *             if a key already existent in this {@code BPlusTree} is attempted to be inserted again in the
	 *             {@code BPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public abstract void insert(K k, P p) throws InvalidInsertionException, IOException;

	/**
	 * Removes the specified key and the corresponding pointer from this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to delete
	 * @throws InvalidDeletionException
	 *             if a key non-existent in a {@code BPlusTree} is attempted to be deleted from the {@code BPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public abstract void delete(K k) throws InvalidDeletionException, IOException;

}

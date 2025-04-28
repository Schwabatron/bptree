package bptree;

import bptree.BPlusTree.InvalidDeletionException;

/**
 * The {@code NonLeafNode} class implements non-leaf nodes in a B+-tree.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * @param <K>
 *            the type of keys
 */
public class NonLeafNode<K extends Comparable<K>> extends Node<K> {

	/**
	 * An automatically generated serial version UID.
	 */
	private static final long serialVersionUID = -5878186273639744395L;

	/**
	 * Constructs a {@code NonLeafNode}.
	 * 
	 * @param degree
	 *            the degree of the {@code NonLeafNode}
	 */
	public NonLeafNode(int degree) {
		super(degree);
	}

	/**
	 * Constructs a {@code NonLeafNode} while adding the specified key and pointers.
	 * 
	 * @param degree
	 *            the degree of the {@code NonLeafNode}
	 * @param n
	 *            a pointer to a {@code Node}
	 * @param key
	 *            a key
	 * @param nn
	 *            a pointer to a {@code Node}
	 */
	public NonLeafNode(int degree, Node<K> n, K key, Node<K> nn) {
		this(degree);
		pointers[0] = n;
		keys[0] = key;
		pointers[1] = nn;
		keyCount = 1;
	}

	/**
	 * Returns a pointer to the child {@code Node} of this {@code NonLeafNode} that must be responsible for this
	 * specified key.
	 * 
	 * @param k
	 *            a search key
	 * @return a pointer to the child {@code Node} of this {@code NonLeafNode} that must be responsible for this
	 *         specified key
	 */
	Node<K> child(K k) {
		int i = 0;
		for (; i < keyCount; i++) {
			int c = k.compareTo(keys[i]);
			if (c == 0)
				return pointer(i + 1);
			else if (c < 0)
				return pointer(i);
		}
		return pointer(i);
	}

	/**
	 * Inserts the specified key and pointer after the specified pointer.
	 * 
	 * @param key
	 *            a key
	 * @param pointer
	 *            a pointer to a {@code Node}
	 * @param p
	 *            a pointer after which the specified key and pointer will be inserted
	 */
	public void insertAfter(K key, Node<K> pointer, Node<K> p) {
		int i = keyCount;
		while (!pointers[i].equals(p)) {
			keys[i] = keys[i - 1];
			pointers[i + 1] = pointers[i];
			i--;
		}
		keys[i] = key;
		pointers[i + 1] = pointer;
		keyCount++;
	}

	/**
	 * Returns the number of children that this {@code NonLeafNode} has.
	 * 
	 * @return the number of children that this {@code NonLeafNode} has
	 */
	public int childCount() {
		return keyCount + 1;
	}

	/**
	 * Copies the specified keys and pointers of the specified {@code NonLeafNode} into this {@code NonLeafNode}.
	 * 
	 * @param node
	 *            a {@code NonLeafNode}
	 * @param beginIndex
	 *            the beginning index of the keys, inclusive
	 * @param endIndex
	 *            the ending index of the pointers, inclusive
	 */
	public void copy(NonLeafNode<K> node, int beginIndex, int endIndex) {
		clear();
		super.append(node, beginIndex, endIndex - 1);
		this.pointers[keyCount] = node.pointers[keyCount + beginIndex];
	}

	/**
	 * Inserts a key and pointer at the specified indices.
	 * 
	 * @param k
	 *            a key
	 * @param iK
	 *            the index at which the key is inserted
	 * @param p
	 *            a pointer to a {@code Node}
	 * @param iP
	 *            the index at which the pointer is inserted
	 */
	public void insert(K k, int iK, Node<K> p, int iP) {
		for (int i = keyCount; i > iK; i--)
			keys[i] = keys[i - 1];
		keys[iK] = k;
		for (int i = keyCount + 1; i > iP; i--)
			pointers[i] = pointers[i - 1];
		pointers[iP] = p;
		keyCount++;
	}

	/**
	 * Removes the key and pointer at the specified indices.
	 * 
	 * @param iK
	 *            the index at which the key is deleted
	 * @param iP
	 *            the index at which the pointer is deleted
	 */
	public void delete(int iK, int iP) {
		for (int i = iK; i < keyCount - 1; i++)
			keys[i] = keys[i + 1];
		for (int i = iP; i < keyCount; i++)
			pointers[i] = pointers[i + 1];
		keys[keyCount - 1] = null;
		pointers[keyCount] = null;
		keyCount--;
	}

	/**
	 * Removes the specified key and a relevant pointer from this {@code NonLeafNode}.
	 * 
	 * @param key
	 *            a key
	 * @throws InvalidDeletionException
	 *             if a key non-existent in this {@code NonLeafNode} is attempted to be removed from this
	 *             {@code NonLeafNode}.
	 */
	@Override
	public void remove(K key) throws InvalidDeletionException {
		int i = 0;
		for (; i < keyCount; i++) {
			if (keys[i].compareTo(key) == 0) {
				for (int j = i; j < keyCount - 1; j++) {
					keys[j] = keys[j + 1];
					pointers[j + 1] = pointers[j + 2];
				}
				break;
			}
		}
		if (i == keyCount)
			throw new InvalidDeletionException("key: " + key);
		keyCount--;
		keys[keyCount] = null;
		pointers[keyCount + 1] = null;
	}

	/**
	 * Changes the key between the specified pointers.
	 * 
	 * @param p
	 *            a pointer to a {@code Node}
	 * @param n
	 *            a pointer to a {@code Node}
	 * @param k
	 *            a key
	 */
	public void changeKey(Node<K> p, Node<K> n, K k) {
		for (int i = 0; i < keyCount; i++)
			if (pointers[i].equals(p) && pointers[i + 1].equals(n)) {
				keys[i] = k;
				return;
			}
		throw new UnsupportedOperationException("There must be a bug in the code. This case must not happen!");
	}


	public int indexOf(Node<K> child) {
		for (int i = 0; i <= keyCount; i++) {
			if (pointers[i].equals(child)) {
				return i;
			}
		}
		return -1;
	}

	public int pointerCount() {
		return keyCount + 1;
	}

	/**
	 * Determines whether or not this {@code NonLeafNode} is under-utilized and thus some action such as merging or
	 * redistribution is needed.
	 * 
	 * @return {@code true} if this {@code NonLeafNode} is under-utilized and thus some action such as merging or
	 *         redistribution is needed; {@code false} otherwise
	 */
	@Override
	public boolean isUnderUtilized() {
		if(this.childCount() < Math.ceil((degree) / 2.0)) //checking if the non leaf has the min amount of children for its key count
		{
			return true; //if it has less children than required then it is underutilized
		}
		else
		{
			return false; //otherwise it is utilized
		}
	}

	/**
	 * Determines whether or not this {@code NonLeafNode} can be merged with the specified {@code Node}.
	 * 
	 * @param other
	 *            another {@code Node}
	 * @return {@code true} if this {@code NonLeafNode} can be merged with the specified {@code Node}; {@code false}
	 *         otherwise
	 */
	@Override
	public boolean mergeable(Node<K> other) {
		return keyCount + other.keyCount() <= degree - 1;
	}

}
